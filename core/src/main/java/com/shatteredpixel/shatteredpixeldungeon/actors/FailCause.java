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

package com.shatteredpixel.shatteredpixeldungeon.actors;

/**
 * Why an adjudicated action failed. Each cause maps to a check that used to live
 * scattered through the act() implementations; adjudication now performs them at
 * a single point, between proposal and execution.
 *
 * Some failures (locked doors, missing keys) intentionally stay un-adjudicated:
 * the hero's execution handles them at zero cost, exactly as before.
 */
public enum FailCause {
	PARALYSED,          //char.paralysed > 0
	ROOTED,             //char is rooted (blocks movement)
	TARGET_GONE,        //target is dead or no longer on the level
	TARGET_OUT_OF_RANGE,//target can no longer be attacked/reached
	TARGET_INVISIBLE,   //target has turned invisible
	CHARMED,            //char is charmed by the target
	NOT_PASSABLE,       //target cell can't be entered
	NO_PATH,            //no path to the destination (reserved; pathfinding may still fail at execution)
	OUT_OF_MAP,         //destination is outside the level
	CANT_INTERACT,      //interact target not alive or unreachable
	FROST_AURA          //attack blocked by the target's frost aura (FrostAura); attacker is chilled
}
