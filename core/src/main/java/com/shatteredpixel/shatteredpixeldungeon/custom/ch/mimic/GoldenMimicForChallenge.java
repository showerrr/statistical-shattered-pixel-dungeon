package com.shatteredpixel.shatteredpixeldungeon.custom.ch.mimic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GoldenMimicForChallenge extends MimicForChallenge {

    {
        spriteClass = MimicSprite.Golden.class;

        properties.add(Property.MINIBOSS);
    }

    @Override
    public String name() {
        if (alignment == Alignment.NEUTRAL){
            return Messages.get(Heap.class, "locked_chest");
        } else {
            return super.name();
        }
    }

    @Override
    public String description() {
        if (alignment == Alignment.NEUTRAL){
            return Messages.get(Heap.class, "locked_chest_desc") + "\n\n" + Messages.get(this, "hidden_hint") + ModifierDesc();
        } else {
            return super.description();
        }
    }

    public void stopHiding(){
        state = HUNTING;
        if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
            enemy = Dungeon.hero;
            target = Dungeon.hero.pos;
            enemySeen = true;
            GLog.w(Messages.get(this, "reveal") );
            CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
            Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
        }
    }

    //A higher level means more offensive modifiers, so golden ones are MUCH MORE strong
    @Override
    public void setFloatLevel(int floatLevel) {
        super.setFloatLevel(Math.round(floatLevel *1.5f));
    }

    @Override
    public float showPower() {
        return super.showPower()*1.9f;
    }

    @Override
    protected void generatePrize(boolean useDecks) {
        super.generatePrize(useDecks);
        //all existing prize items are guaranteed uncursed, and +1
        for (Item i : items){
            if (i instanceof EquipableItem || i instanceof Wand){
                i.cursed = false;
                i.cursedKnown = true;
                if (i instanceof Weapon && ((Weapon) i).hasCurseEnchant()){
                    ((Weapon) i).enchant(null);
                }
                if (i instanceof Armor && ((Armor) i).hasCurseGlyph()){
                    ((Armor) i).inscribe(null);
                }
                if (i.level() < 6 &&(!(i instanceof Weapon || i instanceof Wand))){
                    i.upgrade();
                }
            }
        }
    }
}
