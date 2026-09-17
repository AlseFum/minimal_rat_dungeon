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

package com.shatteredpixel.shatteredpixeldungeon.experimental.chapinit;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.Random;

/**
 * 源石虫（d:/dungeon-repo/Enemy/OriginiumSlug.md）：HT=8, ATK=1~5, DR=1, Evade=1。
 * 起始章节最基础的杂兵。
 */
public class OriginiumSlug extends Mob {

	{
		spriteClass = Sprite.class;

		HP = HT = 8;
		defenseSkill = 1;

		lootChance = 0.1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 5);
	}

	@Override
	public int attackSkill(Char target) {
		return 9;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 1);
	}

	public static class Sprite extends BugSprite {
		public Sprite() {
			super("sprites/namsek/OriginiumSlug.png", 0.5f);
		}
	}
}
