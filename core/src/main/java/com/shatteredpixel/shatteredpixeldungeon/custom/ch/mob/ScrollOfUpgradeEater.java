package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mob;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Bundle;

public class ScrollOfUpgradeEater extends InventoryScroll {
    {
        image = ItemSpriteSheet.SCROLL_LAGUZ;
        icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
        preferredBag = Belongings.Backpack.class;
        unique = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return eaten ? new ItemSprite.Glowing(0x00D000) : null;
    }

    private boolean eaten = false;
    private int level_stored = 0;

    @Override
    protected boolean usableOnItem(Item item) {
        return item.isUpgradable();
    }

    @Override
    protected void onItemSelected(Item item) {
        if(!eaten){
            if(item.isEquipped(Dungeon.hero)){
                GLog.w(M.L(ScrollOfUpgradeEater.class, "cant_eat_equipped"));
            }else {
                GameScene.show(new WndOptions(M.L(this, "eat_confirm_title"), M.L(this, "eat_confirm_body", item.name()), M.L(this, "sure"), M.L(this, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            eaten = true;
                            level_stored = item.level();
                            level_stored = Math.min(3, level_stored);

                            item.detach(Dungeon.hero.belongings.backpack);
                            GLog.p(M.L(ScrollOfUpgradeEater.class, "eaten", item.name()));
                        }
                    }
                });
            }
            collect();
        }else{
            for(int i = 0; i < level_stored; ++i){
                item.upgrade();
            }
            GLog.p(M.L(this, "upgrade", item.name()));
            eaten = false;
            level_stored = 0;
        }
    }

    @Override
    public String desc() {
        String base = M.L(this, "desc");
        if(!eaten){
            base += M.L(this, "not_eat");
        }else{
            base += M.L(this, "ready_upgrade", level_stored);
        }
        return base;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("eaten_a_item", eaten);
        bundle.put("upgrade_stored", level_stored);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        eaten = bundle.getBoolean("eaten_a_item");
        level_stored = bundle.getInt("upgrade_stored");
    }
}
