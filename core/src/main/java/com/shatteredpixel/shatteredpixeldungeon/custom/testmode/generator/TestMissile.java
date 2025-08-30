package com.shatteredpixel.shatteredpixeldungeon.custom.testmode.generator;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Objects;

public class TestMissile extends TestGenerator {
    {
        image = ItemSpriteSheet.MISSILE_HOLDER;
    }

    private int selected = 0;
    private int item_quantity = 1;
    private int levelToGen = 0;
    private boolean bowGenerated = false;

    private int enchant_id = 0;
    private int enchant_rarity = 0;

    private static final String AC_BOW = "bow";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!bowGenerated) actions.add(AC_BOW);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_GIVE)) {
            GameScene.show(new SettingsWindow());
        } else if (action.equals(AC_BOW)) {
            SpiritBow bow = new SpiritBow();
            bow.identify().collect();
            bowGenerated = true;
        }
    }

    private void modify(MissileWeapon m) {
        m.level(levelToGen);
        m.quantity(item_quantity);
        Class<? extends Weapon.Enchantment> ench = TestMelee.generateEnchant(enchant_rarity, enchant_id);
        if (ench == null) {
            m.enchant(null);
        } else {
            m.enchant(Reflection.newInstance(ench));
        }
        m.cursed = cursed;
    }

    private void createMissiles(){
        MissileWeapon m = Reflection.newInstance(missileList.get(selected));
        modify(m);
        m.identify();
        if(m.collect()){
            GLog.i(Messages.get(this, "collect_success", m.name()));
        }else{
            m.doDrop(curUser);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("item_quantity", item_quantity);
        bundle.put("selected", selected);
        bundle.put("level_to_gen", levelToGen);
        bundle.put("bow_generated", bowGenerated);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        item_quantity = bundle.getInt("item_quantity");
        selected = bundle.getInt("selected");
        levelToGen = bundle.getInt("level_to_gen");
        bowGenerated = bundle.getBoolean("bow_generated");
    }

    private static ArrayList<Class<? extends MissileWeapon>> missileList = new ArrayList<>();

    private Class<? extends MissileWeapon> idToMissile(int id) {
        switch (id) {
            case 0:
                return Dart.class;
            case 1:
                return ThrowingStone.class;
            case 2:
                return ThrowingKnife.class;
            case 3:
                return FishingSpear.class;
            case 4:
                return Shuriken.class;
            case 5:
                return ThrowingClub.class;
            case 6:
                return ThrowingSpear.class;
            case 7:
                return Kunai.class;
            case 8:
                return Bolas.class;
            case 9:
                return Javelin.class;
            case 10:
                return HeavyBoomerang.class;
            case 11:
            default:
                return Tomahawk.class;
            case 12:
                return ThrowingHammer.class;
            case 13:
                return Trident.class;
            case 14:
                return ForceCube.class;
            case 15:
                return ThrowingSpike.class;
        }
    }

    private void buildList() {
        if (missileList.isEmpty()) {
            for (int i = 0; i < 16; ++i) {
                missileList.add(idToMissile(i));
            }
        }
    }

    private String currentEnchName(Class<? extends Weapon.Enchantment> ench) {
        if (enchant_rarity < 4)
            return currentEnchName(ench, Messages.get(Weapon.Enchantment.class, "enchant"));
        else
            return currentEnchName(ench, Messages.get(Item.class, "curse"));
    }

    private String currentEnchName(Class<? extends Weapon.Enchantment> ench, String wepName) {
        return Messages.get(ench, "name", wepName);
    }

    private class SettingsWindow extends Window {
        private RenderedTextBlock t_select;
        private OptionSlider o_level;
        private OptionSlider o_quantity;
        private RedButton b_create;
        private ArrayList<IconButton> buttonList = new ArrayList<>();
        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 19;
        private static final int GAP = 2;
        private RenderedTextBlock t_infoEnchant;
        private OptionSlider o_enchant_rarity;
        private OptionSlider o_enchant_id;
        private CheckBox c_curse;

        public SettingsWindow() {
            buildList();
            createImage();

            t_select = PixelScene.renderTextBlock("", 6);
            t_select.text();
            t_select.setPos(0, buttonList.get(buttonList.size() - 1).bottom() + GAP);
            add(t_select);

            o_level = new OptionSlider(Messages.get(this, "level"), "0", "12", 0, 12) {
                @Override
                protected void onChange() {
                    levelToGen = getSelectedValue();
                }
            };
            o_level.setSelectedValue(levelToGen);
            o_level.setRect(0, t_select.bottom() + 2 * GAP, WIDTH, 24);
            add(o_level);

            o_quantity = new OptionSlider(Messages.get(this, "quantity"), "1", "10", 1, 10) {
                @Override
                protected void onChange() {
                    item_quantity = getSelectedValue();
                }
            };
            o_quantity.setSelectedValue(item_quantity);
            o_quantity.setRect(0, t_select.bottom() + 2 * GAP, WIDTH, 24);
            add(o_quantity);

            t_infoEnchant = PixelScene.renderTextBlock("", 6);
            t_infoEnchant.text(enchantDesc());
            add(t_infoEnchant);

            o_enchant_rarity = new OptionSlider(Messages.get(this, "enchant_rarity"), "0", "4", 0, 4) {
                @Override
                protected void onChange() {
                    enchant_rarity = getSelectedValue();
                    updateEnchantText();
                }
            };
            o_enchant_rarity.setSelectedValue(enchant_rarity);
            add(o_enchant_rarity);

            o_enchant_id = new OptionSlider(Messages.get(this, "enchant_id"), "0", "7", 0, 7) {
                @Override
                protected void onChange() {
                    enchant_id = getSelectedValue();
                    updateEnchantText();
                }
            };
            o_enchant_id.setSelectedValue(enchant_id);
            add(o_enchant_id);

            c_curse = new CheckBox(Messages.get(this, "curse")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    cursed = checked();
                }
            };
            c_curse.checked(cursed);
            add(c_curse);

            b_create = new RedButton(Messages.get(this, "create_button")) {
                @Override
                protected void onClick() {
                    createMissiles();
                }
            };
            add(b_create);
            updateText();
        }

        private void updateText() {
            t_select.text(Messages.get(TestMissile.class, "select", Messages.get(missileList.get(selected), "name")));
            layout();
        }

        private void layout() {
            t_select.setPos(0, buttonList.get(buttonList.size() - 1).bottom() + GAP);
            o_level.setRect(0, t_select.bottom() + 2 * GAP, WIDTH, 24);
            o_quantity.setRect(0, o_level.bottom() + GAP, WIDTH, 24);
            t_infoEnchant.setPos(0, GAP + o_quantity.bottom());
            o_enchant_rarity.setRect(0, GAP + t_infoEnchant.bottom(), WIDTH, 24);
            o_enchant_id.setRect(0, GAP + o_enchant_rarity.bottom(), WIDTH, 24);
            c_curse.setRect(0, GAP + o_enchant_id.bottom(), WIDTH/2f - GAP/2f, 16);
            b_create.setRect(0, c_curse.bottom() + GAP, WIDTH, 16);
            resize(WIDTH, (int) b_create.bottom() );
        }

        private void createImage() {
            float left;
            float top = GAP;
            int placed = 0;
            int row = 1;
            int picPerRow = 6;
            int len = missileList.size();
            left = (WIDTH - BTN_SIZE * Math.min(len, picPerRow)) / 2f;
            for (int i = 0; i < len; ++i) {
                final int j = i;
                IconButton btn = new IconButton() {
                    @Override
                    protected void onClick() {
                        selected = Math.min(j, 15);
                        super.onClick();
                        updateText();
                    }
                };
                Image im = new Image(Assets.Sprites.ITEMS);
                im.frame(ItemSpriteSheet.film.get(Objects.requireNonNull(Reflection.newInstance(missileList.get(i))).image));
                im.scale.set(1.0f);
                btn.icon(im);
                btn.setRect(left + placed * BTN_SIZE, top + (row - 1) * (BTN_SIZE + GAP), BTN_SIZE, BTN_SIZE);
                add(btn);
                placed++;
                if (placed > 0 && placed % picPerRow == 0) {
                    placed = 0;
                    left = (WIDTH - BTN_SIZE * Math.min(len - row * picPerRow, picPerRow)) / 2f;
                    row++;
                }
                buttonList.add(btn);
            }
        }

        private void updateEnchantText() {
            t_infoEnchant.text(enchantDesc());
            layout();
        }

        private String enchantDesc() {
            //String desc = Messages.get(BossRushMelee.class, "enchant_id_pre", enchant_rarity);
            String desc = "";
            String key = "enchant_id_e" + String.valueOf(enchant_rarity);
            Class<? extends Weapon.Enchantment> ench = TestMelee.generateEnchant(enchant_rarity, enchant_id);
            desc += Messages.get(TestMelee.class, key, (ench == null ? Messages.get(TestMelee.class, "null_enchant") : currentEnchName(ench)));
            return desc;
        }

    }
}
