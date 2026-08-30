/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 * Shattered Pixel Dungeon Copyright (C) 2014-2026 Evan Debenham
 * GNU GPL v3 licensed.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.base.AccurateWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Cudgel extends AccurateWeapon {

	{
		image = ItemSpriteSheet.MACE; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;

		tier = 1;
		ACC = 1.40f; //40% boost to accuracy
		dmgBoostBase = 3;
		bones = false;
	}

}
