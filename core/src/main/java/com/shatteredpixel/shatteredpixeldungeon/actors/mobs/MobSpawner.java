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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;

/**
 * Owns only the timing of natural respawns. Enemy selection belongs to
 * {@link Rat#random()} through {@code Level.createMob()}.
 */
public class MobSpawner extends Actor {

	{
		actPriority = BUFF_PRIO;
	}

	@Override
	protected boolean act() {
		if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {
			if (Dungeon.level.spawnMob(12)) {
				spend(Dungeon.level.respawnCooldown());
			} else {
				spend(TICK);
			}
		} else {
			spend(Dungeon.level.respawnCooldown());
		}
		return true;
	}

	public void resetCooldown() {
		spend(-cooldown());
		spend(Dungeon.level.respawnCooldown());
	}
}
