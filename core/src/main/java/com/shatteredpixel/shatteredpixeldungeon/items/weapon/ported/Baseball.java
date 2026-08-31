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

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** A baseball. When thrown by a baseball bat it deals 3x damage with 2.5x accuracy. */
public class Baseball extends MissileWeapon {

	static {
		SpriteRegistry.r("ported.baseball", "sprites/ported/cuora_baseball.png", 0, 0, 32, 32);
	}

	@Override
	public String spriteRegion() {
		return "ported.baseball";
}

	private boolean batSourced = false;

	{
		image = ItemSpriteSheet.THROWING_STONE; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;

		tier = 1;
		baseUses = 10;
		sticky = false;
		bones = true;
	}

	public void markBatSourced() {
		this.batSourced = true;
	}

	@Override
	public int min(int lvl) {
		return 1 * tier + lvl;                      //lower than the standard 2*tier+lvl
	}

	@Override
	public int max(int lvl) {
		return 2 * tier + lvl;                      //lower than the standard 5*tier+2*lvl
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = super.damageRoll(owner);
		if (batSourced) {
			damage *= 3;                            //3x damage when hit by the bat
		}
		return damage;
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		if (batSourced) {
			return 2.5f;                            //harder to dodge when hit by the bat
		}
		return super.accuracyFactor(owner, target);
	}

	@Override
	protected void onThrow(int cell) {
		super.onThrow(cell);
		batSourced = false;
	}
}
