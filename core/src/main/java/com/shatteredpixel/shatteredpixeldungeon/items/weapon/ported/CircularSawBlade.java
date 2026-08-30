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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CircularSawBlade extends MissileWeapon {

	{
		image = ItemSpriteSheet.THROWING_KNIFE; //TODO: replace with saw blade sprite
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.15f;

		bones = false;

		tier = 1;
		baseUses = 3;
		sticky = false;
	}

	//note: the ZootDungeon original gains extra hits and explosions from a
	//saw-mastery talent and a blaze subclass, which this fork does not have,
	//so the weapon is pure data here

	@Override
	public int value() {
		return 30 * quantity;
	}
}
