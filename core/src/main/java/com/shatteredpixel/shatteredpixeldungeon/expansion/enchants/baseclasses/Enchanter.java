 package com.shatteredpixel.shatteredpixeldungeon.expansion.enchants.baseclasses;

 import com.shatteredpixel.shatteredpixeldungeon.Assets;
 import com.shatteredpixel.shatteredpixeldungeon.Chrome;
 import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
 import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
 import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
 import com.shatteredpixel.shatteredpixeldungeon.custom.ch.ChallengeItem;
 import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
 import com.shatteredpixel.shatteredpixeldungeon.items.Item;
 import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
 import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
 import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
 import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
 import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
 import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
 import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
 import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
 import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
 import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
 import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
 import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
 import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
 import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
 import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
 import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
 import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
 import com.watabou.noosa.Game;
 import com.watabou.noosa.Image;
 import com.watabou.noosa.NinePatch;
 import com.watabou.noosa.audio.Sample;
 import com.watabou.noosa.ui.Component;
 import com.watabou.utils.Bundlable;
 import com.watabou.utils.Reflection;

 import java.util.ArrayList;

public class Enchanter extends ChallengeItem {
    {
        unique = true;
        defaultAction = AC_ENCHANT;
        image = ItemSpriteSheet.KIT;
    }

    private static final String AC_ENCHANT = "enchant";

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ENCHANT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
       if(action.equals(AC_ENCHANT)){
           GameScene.show(new WndEnchant());
       }else {
           super.execute(hero, action);
       }
    }

    public static class WndEnchant extends Window {
        private static final int BTN_SIZE = 24;
        private static final int WIDTH = 120;
        private static final int GAP = 2;

        private final ItemButton[] inputs = new ItemButton[3];
        private final ItemButton toEnchant;
        private final RedButton execute;

        private static float currentScroll;

        private EnchRecipe currentAvailableRecipe;

        public WndEnchant(){
            super(0, 0, Chrome.get(Chrome.Type.WINDOW_SILVER));

            resize(WIDTH, 100);

            RenderedTextBlock title = PixelScene.renderTextBlock(M.L(Enchanter.class, "title"), 10);
            title.setPos(WIDTH/2f-title.width()/2, GAP);
            add(title);

            synchronized (inputs){
                float xpos = WIDTH/2f - inputs.length*BTN_SIZE/2f-GAP*3*((inputs.length-1)/2f);
                for(int i=0; i< inputs.length; ++i){
                    inputs[i]=new ItemButton(){
                        @Override
                        protected void onClick() {
                            super.onClick();
                            if(item!=null){
                                if (!item.collect()) {
                                    Dungeon.level.drop(item, Dungeon.hero.pos);
                                }
                                item = null;
                                slot.item(new WndBag.Placeholder(ItemSpriteSheet.SOMETHING));
                                updateState();
                            }else{
                                GameScene.show(WndBag.lastBag(inputSelector));
                            }
                        }

                        @Override
                        protected boolean onLongClick() {
                            if (item != null){
                                Game.scene().addToFront(new WndInfoItem(item));
                                return true;
                            }
                            return false;
                        }
                    };
                    inputs[i].setRect(xpos, 18, BTN_SIZE, BTN_SIZE);
                    xpos += BTN_SIZE + GAP*3;
                    add(inputs[i]);
                }
            }

            execute = new RedButton(""){
                Image arrow;

                @Override
                protected void createChildren() {
                    super.createChildren();

                    arrow = Icons.get(Icons.ARROW);
                    add(arrow);
                }

                @Override
                protected void layout() {
                    super.layout();
                    arrow.angle = 90;
                    arrow.x = x + (width + arrow.width)/2f;
                    arrow.y = y + (height - arrow.height)/2f;
                    PixelScene.align(arrow);
                }

                @Override
                public void enable(boolean value) {
                    super.enable(value);
                    if (value){
                        arrow.tint(1, 1, 0, 1);
                        arrow.alpha(1f);
                        bg.alpha(1f);
                    } else {
                        arrow.color(0, 0, 0);
                        arrow.alpha(0.6f);
                        bg.alpha(0.6f);
                    }
                }

                @Override
                protected void onClick() {
                    super.onClick();
                    if(currentAvailableRecipe != null){
                        doEnchant(currentAvailableRecipe);
                    }
                    Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                }
            };
            execute.setRect(WIDTH/2f - 8, inputs[0].bottom() + GAP * 2, 16, 21);
            add(execute);

            toEnchant = new ItemButton(){
                @Override
                protected void onClick() {
                    super.onClick();
                    if(item!=null){
                            if (!item.collect()) {
                                Dungeon.level.drop(item, Dungeon.hero.pos);
                            }
                        item = null;
                        slot.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
                        updateState();
                    }else{
                        /*
                        GameScene.show(WndBag.lastBag(item -> {
                            if(item != null) {
                                if(item.isEquipped(Dungeon.hero)){
                                    GLog.w(M.L(Enchanter.class, "unequip_first"));
                                    return;
                                }
                                toEnchant.item(item.detach(Dungeon.hero.belongings.backpack));
                                updateState();
                            }
                        }, WndBag.Mode.WEAPON, M.L(Enchanter.class, "select_weapon")));

                         */
                        GameScene.show(WndBag.lastBag(new WndBag.ItemSelector() {
                            @Override
                            public String textPrompt() {
                                 return M.L(Enchanter.class, "select_weapon");
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof MeleeWeapon;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if(item != null){
                                    if(item.isEquipped(Dungeon.hero)){
                                        GLog.w(M.L(Enchanter.class, "unequip_first"));
                                        return;
                                    }
                                    toEnchant.item(item.detach(Dungeon.hero.belongings.backpack));
                                    updateState();
                                }
                            }
                        }));
                    }
                }

                @Override
                protected boolean onLongClick() {
                    if (item != null){
                        Game.scene().addToFront(new WndInfoItem(item));
                        return true;
                    }
                    return false;
                }
            };
            toEnchant.setRect(WIDTH/2f-BTN_SIZE/2f, execute.bottom() + GAP * 2, BTN_SIZE, BTN_SIZE);
            add(toEnchant);

            slotReset();

            IconButton recipeButton = new IconButton(){
                @Override
                protected void onClick() {
                    super.onClick();
                    GameScene.show(new WndInfoInscription());
                }
            };
            Image im = new Image(Assets.Sprites.ITEMS);
            im.frame(ItemSpriteSheet.film.get(ItemSpriteSheet.ALCH_PAGE));
            recipeButton.icon(im);
            add(recipeButton);
            recipeButton.setRect(WIDTH - GAP - 12, height - GAP - 12, 12, 12);

        }

        private void doEnchant(EnchRecipe recipe){
            if(!(toEnchant.item instanceof Weapon)){
                GLog.w(M.L(Enchanter.class, "illegal_receiver"));
                return;
            }
            if(!EnchRecipe.enchant((Weapon) toEnchant.item, recipe)){
                GLog.w(M.L(Enchanter.class, "enchant_fail_bug"));
                return;
            }
            synchronized (inputs) {
                for (int i = 0; i < inputs.length; i++) {
                    if (inputs[i] != null && inputs[i].item != null) {
                        inputs[i].item.quantity(inputs[i].item.quantity()-1);
                        if (inputs[i].item.quantity() <= 0) {
                            inputs[i].slot.item(new WndBag.Placeholder(ItemSpriteSheet.SOMETHING));
                            inputs[i].item = null;
                        } else {
                            inputs[i].slot.item(inputs[i].item);
                        }
                    }
                }
            }
            Item item = toEnchant.item;
            if(item!=null){
                if (!item.collect()) {
                    Dungeon.level.drop(item, Dungeon.hero.pos);
                }
            }
            toEnchant.slot.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
            updateState();
        }

        private void slotReset(){
            synchronized (inputs) {
                for (int i = 0; i < inputs.length; ++i) {
                    Item item = inputs[i].item;
                    if (item != null) {
                        if (!item.collect()) {
                            Dungeon.level.drop(item, Dungeon.hero.pos);
                        }
                    }
                    inputs[i].slot.item(new WndBag.Placeholder(ItemSpriteSheet.SOMETHING));
                }
            }
            Item item = toEnchant.item;
            if(item!=null){
                if (!item.collect()) {
                    Dungeon.level.drop(item, Dungeon.hero.pos);
                }
            }
            toEnchant.slot.item(new WndBag.Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
        }

        private void updateState(){
            ArrayList<Class<? extends Item>> classes = new ArrayList<>();
            for(ItemButton itb: inputs){
                if(itb.item != null){
                    classes.add(itb.item.getClass());
                }
            }
            currentAvailableRecipe = EnchRecipe.searchForRecipe(classes);
            execute.enable(currentAvailableRecipe != null && toEnchant.item != null);
        }

        @Override
        public void onBackPressed() {
            slotReset();
            super.onBackPressed();
        }

        protected WndBag.ItemSelector inputSelector = new WndBag.ItemSelector() {
            @Override
            public String textPrompt() {
                return  M.L(Enchanter.class, "select_ingredient");
            }

            @Override
            public boolean itemSelectable(Item item) {
                return true;
            }

            @Override
            public void onSelect(Item item) {
                synchronized (inputs) {
                    if (item != null && inputs[0] != null) {
                        if(item.isEquipped(Dungeon.hero)){
                            GLog.w(M.L(Enchanter.class, "unequip_first"));
                            return;
                        }
                        for (int i = 0; i < inputs.length; i++) {
                            if (inputs[i].item == null) {
                                inputs[i].item(item.detach(Dungeon.hero.belongings.backpack));
                                break;
                            }
                        }
                        updateState();
                    }
                }
            }
        };

        public class WndInfoInscription extends Window {
            private static final int WIDTH = 120;
            private static final int HEIGHT = 160;
            private static final int BTN_HEIGHT = 16;
            private static final int GAP = 4;
            private static final int TEXT_SIZE = 6;

            private final ArrayList<Float> upperBoundary;
            private final ArrayList<Float> bottomBoundary;
            private final ArrayList<Class<? extends Inscription>> storedInscription;
            private final ArrayList<Integer> triggers;
            private ScrollPane sp;
            public WndInfoInscription(){
                super(0, 0, Chrome.get(Chrome.Type.WINDOW_SILVER));
                upperBoundary = new ArrayList<>(50);
                bottomBoundary = new ArrayList<>(50);
                storedInscription = new ArrayList<>(50);
                triggers = new ArrayList<>(50);
                resize(WIDTH, HEIGHT);
                sp = new ScrollPane(new Component()){
                    @Override
                    public void onClick(float x, float y) {
                        super.onClick(x, y);
                        for(int i=0; i<upperBoundary.size(); ++i){
                            if(y>upperBoundary.get(i) && y < bottomBoundary.get(i)){
                                Inscription inscription = Reflection.newInstance(storedInscription.get(i));
                                if(inscription instanceof CountInscription){
                                    ((CountInscription) inscription).setTriggers(triggers.get(i));
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append('_').append(inscription.name()).append("_\n\n");
                                sb.append(inscription.desc());
                                GameScene.show(new WndFastEnchantInfo(sb.toString(), EnchRecipe.values()[i].input));
                            }
                        }
                    }
                };
                add(sp);
                Component content = sp.content();
                float pos = GAP * 2;
                RenderedTextBlock hint = PixelScene.renderTextBlock(M.L(Enchanter.class, "hint")+"\n\n", TEXT_SIZE);
                hint.maxWidth(WIDTH);
                content.add(hint);
                hint.setPos(0, pos);
                PixelScene.align(hint);
                pos += GAP + hint.height();
                int i = 1;
                for(EnchRecipe recipe: EnchRecipe.values()){
                    StringBuilder sb = new StringBuilder();
                    sb.append(i);
                    sb.append(')');
                    for(int k = 0; k < recipe.input.size(); ++k){
                        sb.append(Reflection.newInstance(recipe.input.get(k)).trueName());
                        if(k+1<recipe.input.size()) {
                            sb.append('+');
                        }
                    }
                    sb.append("\n");
                    Inscription insc = Reflection.newInstance(recipe.inscription);
                    sb.append('_').append(insc.name()).append('_');
                    if(insc instanceof CountInscription){
                        sb.append('(').append(recipe.triggers).append(')');
                    }
                    sb.append('\n');
                    RenderedTextBlock rtb = PixelScene.renderTextBlock(sb.toString(), TEXT_SIZE);
                    rtb.maxWidth(WIDTH - 2);
                    rtb.setPos(0, pos);
                    PixelScene.align(rtb);
                    pos += rtb.height() + TEXT_SIZE;
                    ++i;

                    upperBoundary.add(rtb.top());
                    bottomBoundary.add(rtb.bottom());
                    storedInscription.add(recipe.inscription);
                    triggers.add(recipe.triggers);

                    content.add(rtb);
                }

                content.setSize(WIDTH, pos + GAP);
                sp.setRect(0, 0, WIDTH, HEIGHT - GAP - BTN_HEIGHT);

                sp.scrollTo(0, currentScroll);

                RedButton pageUp = new RedButton(M.L(Enchanter.class, "page_up"), 9){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        sp.scrollTo(0, Math.max(sp.content().camera().scroll.y - sp.height() + TEXT_SIZE, 0));
                    }
                };
                add(pageUp);
                pageUp.setRect(0, HEIGHT - BTN_HEIGHT, WIDTH/2f - GAP / 2f, BTN_HEIGHT);

                RedButton pageDown = new RedButton(M.L(Enchanter.class, "page_down"), 9){
                    @Override
                    protected void onClick() {
                        super.onClick();
                        sp.scrollTo(0, Math.min(sp.content().camera().scroll.y + sp.height() - TEXT_SIZE, sp.content().height() - sp.height()));
                    }
                };
                add(pageDown);
                pageDown.setRect(GAP/ 2f + WIDTH / 2f, HEIGHT - BTN_HEIGHT, WIDTH/2f - GAP / 2f, BTN_HEIGHT);

            }

            @Override
            public void onBackPressed() {
                super.onBackPressed();
                currentScroll = sp.content().camera().scroll.y;
            }

            public class WndFastEnchantInfo extends Window{
                private static final int WIDTH = 120;
                private static final int MARGIN = 4;
                public WndFastEnchantInfo(String text, ArrayList<Class<? extends Item>> recipe){
                    super();

                    ArrayList<Class<? extends Item>> lack = new ArrayList<>();
                    Belongings inv = Dungeon.hero.belongings;
                    for (Class<? extends Item> finding : recipe){
                        boolean found = false;
                        for(Item item: inv){
                            if(item.getClass().equals(finding)){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            lack.add(finding);
                        }
                    }

                    if(lack.isEmpty()) {
                        text += M.L(Enchanter.class, "can_enchant");
                    }else {
                        text += M.L(Enchanter.class, "lack_item");
                        StringBuilder textBuilder = new StringBuilder(text);
                        for (Class<? extends Item> lackClass : lack) {
                            textBuilder.append(Reflection.newInstance(lackClass).trueName());
                            textBuilder.append("  ");
                        }
                        text = textBuilder.toString();
                    }

                    RenderedTextBlock info = PixelScene.renderTextBlock( text, 6 );
                    info.maxWidth(WIDTH - MARGIN * 2);
                    info.setPos(MARGIN, MARGIN);
                    add( info );

                    RedButton rb = new RedButton(M.L(Enchanter.class, "quick_enchant")){
                        @Override
                        protected void onClick() {
                            super.onClick();
                            slotReset();

                            Belongings inventory = Dungeon.hero.belongings;
                            int curslot = 0;
                            for (Class<? extends Item> finding : recipe){
                                for(Item item: inventory){
                                    if(item.getClass().equals(finding)){
                                        Item toUse = item.detach(inventory.backpack);
                                        inputs[curslot].item(toUse);
                                        ++curslot;
                                        break;
                                    }
                                }
                            }

                            updateState();
                            onBackPressed();
                            WndInfoInscription.this.onBackPressed();
                        }
                    };
                    add(rb);
                    rb.setRect(MARGIN, info.bottom() + MARGIN, WIDTH - 2 * MARGIN, 18);
                    rb.active = lack.isEmpty();
                    rb.alpha(lack.isEmpty()?1f:0.4f);

                    resize(WIDTH, (int)rb.bottom() + MARGIN );
                }
            }

        }

        /*
        protected WndBag.ItemSelector inputSelector = item -> {
            synchronized (inputs) {
                if (item != null && inputs[0] != null) {
                    if(item.isEquipped(Dungeon.hero)){
                        GLog.w(M.L(Enchanter.class, "unequip_first"));
                        return;
                    }
                    for (int i = 0; i < inputs.length; i++) {
                        if (inputs[i].item == null) {
                            inputs[i].item(item.detach(Dungeon.hero.belongings.backpack));
                            break;
                        }
                    }
                    updateState();
                }
            }
        };#

         */
    }

    public static class ItemButton extends Component {

       protected NinePatch bg;
       protected ItemSlot slot;

       public Item item = null;

       @Override
       protected void createChildren() {
           super.createChildren();

           bg = Chrome.get( Chrome.Type.RED_BUTTON);
           add( bg );

           slot = new ItemSlot() {
               @Override
               protected void onPointerDown() {
                   bg.brightness( 1.2f );
                   Sample.INSTANCE.play( Assets.Sounds.CLICK );
               }
               @Override
               protected void onPointerUp() {
                   bg.resetColor();
               }
               @Override
               protected void onClick() {
                   ItemButton.this.onClick();
               }

               @Override
               protected boolean onLongClick() {
                   return ItemButton.this.onLongClick();
               }
           };
           slot.enable(true);
           add( slot );
       }

       protected void onClick() {}
       protected boolean onLongClick() {
           return false;
       }

       @Override
       protected void layout() {
           super.layout();

           bg.x = x;
           bg.y = y;
           bg.size( width, height );

           slot.setRect( x + 2, y + 2, width - 4, height - 4 );
       }

       public void item( Item item ) {
           slot.item( this.item = item );
       }
    }

}
