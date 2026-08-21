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

package com.shatteredpixel.shatteredpixeldungeon.actors.entities;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Mine;

/** A minimal working mine: enemies detonate it by entering its cell. */
public class ContactMine extends Mine {

	public int damage() {
		return 20;
	}

	@Override
	public void onStep( Char who ) {
		if (isEnemy(who)) {
			detonate();
		}
	}

	@Override
	protected void onDetonate() {
		Char target = charAt(pos);
		if (target != null && target.isAlive()) {
			target.damage(damage(), this);
		}
	}

	private static Char charAt( int cell ) {
		return com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(cell);
	}
}
