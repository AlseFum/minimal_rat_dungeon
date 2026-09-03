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
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice;

import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

/**
 * A passive, cell-blocking map object. It is targetable because it is a Mob,
 * but damage is interpreted by subclasses instead of reducing its HP.
 */
public abstract class MapDevice extends Mob {

	{
		alignment = Alignment.NEUTRAL;
		actPriority = MOB_PRIO + 1;
		state = PASSIVE;
		properties.add(Property.IMMOVABLE);
	}

	public MapDevice() {
		HP = HT = 1;
	}

	@Override
	protected ActionSubmission proposeAction() {
		spend(TICK);
		return ActionSubmission.idle();
	}

	@Override
	public boolean heroShouldInteract() {
		return false;
	}

	@Override
	public boolean reset() {
		//devices persist: level resets must not remove them
		return true;
	}

	@Override
	public int attackSkill( Char target ) {
		return 0;
	}

	@Override
	public int defenseSkill( Char enemy ) {
		return 0;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	public void damage( int damage, Object source ) {
		receiveDamage(source);
	}

	/** Receives an attack event without applying ordinary HP damage. */
	public void receiveDamage( Object source ) {
	}
}
