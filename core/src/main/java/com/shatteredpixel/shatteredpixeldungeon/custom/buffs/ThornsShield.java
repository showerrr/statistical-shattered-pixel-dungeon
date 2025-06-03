package com.shatteredpixel.shatteredpixeldungeon.custom.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class ThornsShield extends FlavourBuff {
    {
        type = buffType.NEUTRAL;
        announced = true;
    }

    @Override
    public int icon() {
        return BuffIndicator.ENDURE;
    }

    @Override
    public void tintIcon(Image icon) {
        super.tintIcon(icon);
        icon.hardlight(1f, 0f, 0f);
    }

    @Override
    public void fx(boolean on) {
        super.fx(on);
        if(on) target.sprite.aura(0xFF0000);
        else{
            target.sprite.clearAura();
        }
    }

    @Override
    public String desc() {
        return M.L(this, "desc", dispTurns());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }
}
