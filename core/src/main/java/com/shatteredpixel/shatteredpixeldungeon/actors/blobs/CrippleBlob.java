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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CrippleBlobParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * MISERY feature: a dark miasma which fades over time. Enemies standing in it
 * are crippled, and it counts as shadow for MISERY purposes.
 */
public class CrippleBlob extends Blob {

	{
		actPriority = HERO_PRIO;
	}

	@Override
	protected void evolve() {
		int cell;
		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0) {
					off[cell] = cur[cell] - 1; //fade by 1 per turn
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(CrippleBlobParticle.FACTORY, 0.15f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	/** Check if a cell has cripple miasma (used alongside MiseryShadowBlob for shadow checks). */
	public static boolean isCrippleCell(int cell) {
		if (Dungeon.level == null) return false;
		CrippleBlob blob = (CrippleBlob) Dungeon.level.blobs.get(CrippleBlob.class);
		return blob != null && blob.cur != null && blob.cur[cell] > 0;
	}
}
