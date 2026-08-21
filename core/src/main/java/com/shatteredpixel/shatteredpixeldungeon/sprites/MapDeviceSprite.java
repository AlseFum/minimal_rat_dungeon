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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;

/** Shared placeholder visual for map devices until a concrete device supplies one. */
public class MapDeviceSprite extends MobSprite {

	public MapDeviceSprite() {
		super();
		texture(Assets.Sprites.WARDS);

		idle = new Animation(1, true);
		idle.frames(texture.uvRect(0, 0, 9, 10));
		run = idle.clone();
		attack = idle.clone();
		operate = idle.clone();
		zap = idle.clone();
		die = idle.clone();

		perspectiveRaise = 5 / 16f;
		renderShadow = false;
		hardlight(0x99CCFF);
		play(idle);
	}

	@Override
	public void turnTo( int from, int to ) {
	}
}
