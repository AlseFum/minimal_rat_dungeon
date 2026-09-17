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
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Random;

/**
 * 迅捷源石虫（d:/dungeon-repo/Enemy/OriginiumSlugAgile.md）：属性同源石虫，
 * 但 Evade=20（足够高），用作教玩家伏击的教材；必定掉落一个种子。
 */
public class OriginiumSlugAgile extends Mob {

	{
		spriteClass = Sprite.class;

		HP = HT = 8;
		defenseSkill = 20;

		lootChance = 1f;
		exp = 2;
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

	/** 必定掉落一个种子（文档：死亡掉落必掉） */
	@Override
	public Item createLoot() {
		return Loot.randomUsingDefaults(Loot.SEED);
	}

	/** 同款甲虫，肚子是红色的（namsek 的 bug_a.png），帧分组与源石虫一致 */
	public static class Sprite extends BugSprite {
		public Sprite() {
			super("sprites/namsek/OriginiumSlugAgile.png", 0.5f);
		}
	}
}
