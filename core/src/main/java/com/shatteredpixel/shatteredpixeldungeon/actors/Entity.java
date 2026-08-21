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

package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

/**
 * A persistent ground entity. Unlike a Char it does not occupy its cell,
 * so characters may stand on it and receive its step callbacks.
 */
public abstract class Entity extends Actor {

	private static final String POS = "pos";

	public int pos;

	{
		actPriority = BLOB_PRIO - 1;
	}

	@Override
	protected boolean act() {
		spend(TICK);
		return true;
	}

	/** Called whenever a character enters this entity's cell. */
	public void onStep( Char who ) {
	}

	/** Called after {@link #onStep(Char)} when that character is flying. */
	public void onFlyOver( Char who ) {
	}

	/** The icon rendered on the ground-entity layer. */
	public int image() {
		return ItemSpriteSheet.SOMETHING;
	}

	public void onSpawn( Level level ) {
	}

	public void onDespawn( Level level ) {
	}

	public final void despawn() {
		if (Dungeon.level != null) {
			Dungeon.level.removeEntity(this);
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(POS, pos);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		pos = bundle.getInt(POS);
	}
}
