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

import com.watabou.noosa.TextureFilm;

/**
 * 弑君者贴图：792x36 = 36x36 帧，横向共 22 帧。
 *
 * 帧分组（眼判，图集无源码可查）：
 * - 0-17 行走循环
 * - 18   拖出白色气流的一击（攻击；也是她穿梭技能的表现帧）
 * - 20-21 收尾帧（图集没有专门的死亡动画，死亡暂用尾部两帧）
 */
public class CrownSlayerSprite extends NamsekSprite {

	public CrownSlayerSprite() {
		super("sprites/namsek/CrownSlayer.png", 36, 36, 0.5f);

		TextureFilm frames = film();

		idle = new Animation(1, true);
		idle.frames(frames, 0);

		run = new Animation(12, true);
		run.frames(frames, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);

		attack = new Animation(8, false);
		attack.frames(frames, 18, 19);

		zap = attack.clone();

		die = new Animation(6, false);
		die.frames(frames, 18, 20, 21);

		play(idle);
	}
}
