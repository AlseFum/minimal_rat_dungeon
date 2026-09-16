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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ported.Infantry;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.watabou.utils.Random;

/**
 * 初始章节的普通层（ChapInit.md 的敌人表：步兵 + 两种源石虫）。
 *
 * 只覆写敌人池：地图生成、纹理、音乐全部沿用下水道——
 * 切尔诺伯格纹理尚不存在，TODO 美术到位后换 tilesTex()。
 *
 * 覆写 {@link com.shatteredpixel.shatteredpixeldungeon.levels.Level#createMob()} 同时作用于
 * 初始生成（RegularLevel.createMobs）与 MobSpawner 的刷新，因此无需另挂钩子。
 */
public class ChapInitLevel extends SewerLevel {

	@Override
	public Mob createMob() {
		//步兵 5 / 源石虫 5 / 迅捷源石虫 1
		switch (Random.chances(new float[]{5, 5, 1})) {
			case 0: default:
				return new Infantry();
			case 1:
				return new OriginiumSlug();
			case 2:
				return new OriginiumSlugAgile();
		}
	}
}
