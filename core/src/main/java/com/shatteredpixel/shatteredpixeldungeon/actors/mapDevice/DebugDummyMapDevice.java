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

import com.shatteredpixel.shatteredpixeldungeon.sprites.MapDeviceSprite;
import com.watabou.utils.Bundle;

/** A minimal map device used to verify placement, blocking, and hit routing. */
public class DebugDummyMapDevice extends MapDevice {

	private static final String CHARGE = "charge";

	private int charge;

	{
		spriteClass = MapDeviceSprite.class;
	}

	public int charge() {
		return charge;
	}

	@Override
	public void receiveDamage( Object source ) {
		charge++;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
	}
}
