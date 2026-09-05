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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

/** Quietly floating dark particles for MISERY shadow cells. */
public class ShadowBlobParticle extends PixelParticle {

	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((ShadowBlobParticle)emitter.recycle( ShadowBlobParticle.class )).reset( x, y );
		}
	};

	public ShadowBlobParticle() {
		super();
		acc.set(0, -12); // very gentle upward drift
	}

	public void reset(float x, float y) {
		revive();

		this.x = x;
		this.y = y;

		left = lifespan = Random.Float(1.5f, 2.5f);
		size = Random.Int(2, 5);
		speed.set(Random.Float(-3, +3), Random.Float(-6, +2));
	}

	@Override
	public void update() {
		super.update();

		float p = left / lifespan;
		// dark purple-black hue, gently pulsing alpha
		color(ColorMath.interpolate(0x220033, 0x0a0011, p));
		am = p < 0.3f ? p / 0.3f * 0.6f : (1f - p) / 0.7f * 0.6f;
	}
}
