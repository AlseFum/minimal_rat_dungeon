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

/** Falling magenta miasma particles for the MISERY cripple blob. */
public class CrippleBlobParticle extends PixelParticle {

	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			((CrippleBlobParticle)emitter.recycle( CrippleBlobParticle.class )).reset( x, y );
		}
	};

	public CrippleBlobParticle() {
		super();
		acc.set(0, +40);
	}

	public void reset(float x, float y) {
		revive();

		this.x = x;
		this.y = y;

		left = lifespan = Random.Float(0.4f, 0.8f);
		size = Random.Int(3, 6);
		speed.set(Random.Float(-8, +8), Random.Float(-20, +5));
	}

	@Override
	public void update() {
		super.update();

		float p = left / lifespan;
		color(ColorMath.interpolate(0x440022, 0x110011, p));
		am = p < 0.2f ? p / 0.2f * 0.8f : (1f - p) / 0.8f * 0.8f;
	}
}
