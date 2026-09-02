/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The single retained mining-specific trap example. */
public class MiningRockfallTrap extends Trap {

	{
		color = GREY;
		shape = DIAMOND;
		canBeHidden = false;
		avoidsHallways = true;
	}

	@Override
	public void activate() {

		ArrayList<Integer> rockCells = new ArrayList<>();

		// Drop rocks in a 5x5 grid, ignoring cells protected by mining supports.
		PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				if (Dungeon.level instanceof MiningLevel) {
					boolean barricade = false;
					for (int offset : PathFinder.NEIGHBOURS9) {
						if (Dungeon.level.map[i + offset] == Terrain.BARRICADE) {
							barricade = true;
						}
					}
					if (barricade) {
						continue;
					}
				}
				rockCells.add(i);
			}
		}

		boolean seen = false;
		for (int cell : rockCells) {
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10);
				seen = true;
			}

			Char ch = Actor.findChar(cell);
			if (ch != null && ch.isAlive()) {
				if (ch instanceof Mob) {
					Buff.prolong(ch, HazardAssistTracker.class, HazardAssistTracker.DURATION);
				}

				// Mining rocks deal fixed physical damage that ignores armor.
				Proc.damage(new DamageInfo().defender(ch).way(DamageWay.TRAP).type(DamageType.PHYSICAL).solidDamage(Random.NormalIntRange(6, 12)).icon(FloatingText.PHYS_DMG_NO_BLOCK));
				Buff.prolong(ch, Paralysis.class, 3);

				if (!ch.isAlive() && ch == Dungeon.hero) {
					Dungeon.fail(this);
					GLog.n(Messages.get(this, "ondeath"));
					if (reclaimed) {
						Badges.validateDeathFromFriendlyMagic();
					}
				}
			} else if (ch == null
					&& Dungeon.level instanceof MiningLevel
					&& Dungeon.level.traps.get(cell) == null
					&& Dungeon.level.plants.get(cell) == null
					&& Random.Int(2) == 0) {
				Level.set(cell, Terrain.MINE_BOULDER);
				GameScene.updateMap(cell);
			}
		}

		if (seen) {
			PixelScene.shake(3, 0.7f);
			Sample.INSTANCE.play(Assets.Sounds.ROCKS);
		}
	}
}
