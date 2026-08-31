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

import com.shatteredpixel.shatteredpixeldungeon.sprites.SpriteRegistry;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.base.CleaveWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MelanthaSword extends CleaveWeapon {

	static {
		SpriteRegistry.r("ported.melantha_sword", "sprites/ported/melantha_sword.png", 0, 0, 32, 32);
	}

	@Override
	public String spriteRegion() {
		return "ported.melantha_sword";
}

	{
		image = ItemSpriteSheet.SWORD; //TODO: dedicated icon
		tier = 1;
		dmgBoostBase = 2;
	}
}
