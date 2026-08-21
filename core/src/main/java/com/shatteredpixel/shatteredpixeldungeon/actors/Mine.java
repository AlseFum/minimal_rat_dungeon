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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

/** A one-shot ground entity with an idempotent detonation entry point. */
public abstract class Mine extends Entity {

	private static final String DETONATED = "detonated";

	protected boolean detonated;

	@Override
	public int image() {
		return ItemSpriteSheet.BOMB;
	}

	public final void detonate() {
		if (detonated) return;
		detonated = true;

		if (Dungeon.level != null && Dungeon.level.heroFOV[pos]) {
			CellEmitter.center(pos).burst(Speck.factory(Speck.STAR), 8);
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
		}

		onDetonate();
		despawn();
	}

	protected abstract void onDetonate();

	protected static boolean isEnemy( Char character ) {
		return character != null && character.isAlive()
				&& character.alignment == Char.Alignment.ENEMY;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(DETONATED, detonated);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		detonated = bundle.getBoolean(DETONATED);
	}
}
