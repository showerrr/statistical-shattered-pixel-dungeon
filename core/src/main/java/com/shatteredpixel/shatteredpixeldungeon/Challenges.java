/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;

public class Challenges {

	//Some of these internal IDs are outdated and don't represent what these challenges do
	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
	public static final int CHAMPION_ENEMIES	= 128;
	public static final int STRONGER_BOSSES 	= 256;

	public static final int SPD_CHALLENGE_MAX = 511;

	public static final int TEST_MODE			= 1<<9;
	//hero.live for buff, mimic spawning
	public static final int MIMIC_DUNGEON 		= 1<<15;
	//Dungeon.newLevel, Hero.speed(for speed limit)
	public static final int ELITE_BOSSES		= 1<<16;
	public static final int ELITE_ENEMIES		= 1<<17;

	public static final int EXPANSION_ENCH 		= 1<<18;

	public static final int MINIGAMES			= 1<<10;

	public static final int MAX_VALUE           = (1<<19)-1;

	public static final String[] NAME_IDS = {
			"champion_enemies",
			"stronger_bosses",
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls",

			"test_mode",
			"mimic_dungeon",
			"elite_bosses",
			"elite_enemies",

			"expansion_ench",

			"minigames"
	};

	public static final long[] MASKS = {
			CHAMPION_ENEMIES, STRONGER_BOSSES, NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS,
			TEST_MODE,
			MIMIC_DUNGEON,
			ELITE_BOSSES,
			ELITE_ENEMIES,

			EXPANSION_ENCH,

			MINIGAMES
	};

	public static int activeChallenges(){
		int chCount = 0;
		for (long ch : Challenges.MASKS){
			if ((Dungeon.challenges & ch) != 0 && ch <= SPD_CHALLENGE_MAX) chCount++;
		}
		return chCount;
	}

	public static boolean isItemBlocked( Item item ){

		if (Dungeon.isChallenged(NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		if(Dungeon.isChallenged(ELITE_ENEMIES)){
			if(item instanceof WandOfBlastWave){
				return true;
			}
		}

		if(Dungeon.isChallenged(ELITE_ENEMIES | ELITE_BOSSES)){
			if(item instanceof RingOfElements){
				return true;
			}
		}

		return false;

	}

}