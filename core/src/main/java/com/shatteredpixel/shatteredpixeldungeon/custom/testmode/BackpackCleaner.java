package com.shatteredpixel.shatteredpixeldungeon.custom.testmode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

import java.util.ArrayList;

public class BackpackCleaner extends TestItem {
    {
        image = ItemSpriteSheet.BOMB;
        defaultAction = AC_CLEAR;
    }
    private static final String AC_CLEAR = "clear";
    private static final String AC_CLEAR_EQUIP = "clear_equip";
    private static final String AC_CLEAR_MISC = "clear_misc";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CLEAR);
        actions.add(AC_CLEAR_EQUIP);
        actions.add(AC_CLEAR_MISC);
        return actions;
    }


    private void clearAllItem(){
        int count = 0;

        for (Item it : Dungeon.hero.belongings.backpack.items.toArray(new Item[0])){
            if(!it.unique){
                if(!(it instanceof Key) && !(it.isEquipped(curUser))){
                    it.detachAll(Dungeon.hero.belongings.backpack);
                    ++count;
                }
            }else if(it instanceof Bag){
                for(Item item_in_bag: ((Bag) it).items.toArray(new Item[0])){
                    if(!item_in_bag.unique){
                        item_in_bag.detachAll(Dungeon.hero.belongings.backpack);
                        ++count;
                    }
                }
            }
        }
        GLog.i(Messages.get(this, "clear_all_result", count));
    }

    private boolean isEquipment(Item it){
        return (it instanceof Weapon) || (it instanceof Armor) || (it instanceof Ring) || (it instanceof Wand) || (it instanceof Artifact);
    }

    private void clearAllEquipment(){
        int count = 0;
        for (Item it : Dungeon.hero.belongings.backpack.items.toArray(new Item[0])){
            if(!it.unique){
                if(isEquipment(it)){
                    if(!it.isEquipped(curUser)){
                        it.detachAll(Dungeon.hero.belongings.backpack);
                        ++count;
                    }
                }
            }else if(it instanceof Bag){
                for(Item item_in_bag: ((Bag) it).items.toArray(new Item[0])){
                    if(isEquipment(item_in_bag) && !item_in_bag.unique){
                        item_in_bag.detachAll(Dungeon.hero.belongings.backpack);
                        ++count;
                    }
                }
            }
        }
        GLog.i(Messages.get(this, "clear_equipment_result", count));
    }

    private void clearAllMisc(){
        int count = 0;
        for (Item it : Dungeon.hero.belongings.backpack.items.toArray(new Item[0])){
            if(!it.unique){
                if(!isEquipment(it)){
                    if(!it.isEquipped(curUser)){
                        it.detachAll(Dungeon.hero.belongings.backpack);
                        ++count;
                    }
                }
            }else if(it instanceof Bag){
                for(Item item_in_bag: ((Bag) it).items.toArray(new Item[0])){
                    if(!isEquipment(item_in_bag) && !item_in_bag.unique){
                        item_in_bag.detachAll(Dungeon.hero.belongings.backpack);
                        ++count;
                    }
                }
            }
        }
        GLog.i(Messages.get(this, "clear_misc_result", count));
    }

    @Override
    public void execute(Hero hero, String action ) {
        super.execute( hero, action );
        if(action.equals(AC_CLEAR)){
            GameScene.show(
                    new WndOptions(Messages.titleCase(Messages.get(this, "clear")),
                            Messages.get(this, "clear_warn"),
                            Messages.get(this, "yes"),
                            Messages.get(this, "no")){
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0)
                                clearAllItem();
                        }
                    }
            );
        }else if(action.equals(AC_CLEAR_EQUIP)){
            GameScene.show(
                    new WndOptions(Messages.titleCase(Messages.get(this, "clear_equip")),
                            Messages.get(this, "clear_warn"),
                            Messages.get(this, "yes"),
                            Messages.get(this, "no")){
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0)
                                clearAllEquipment();
                        }
                    }
            );

        }else if(action.equals(AC_CLEAR_MISC)){
            GameScene.show(
                    new WndOptions(Messages.titleCase(Messages.get(this, "clear_misc")),
                            Messages.get(this, "clear_warn"),
                            Messages.get(this, "yes"),
                            Messages.get(this, "no")){
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0)
                                clearAllMisc();
                        }
                    }
            );
        }
    }
}
