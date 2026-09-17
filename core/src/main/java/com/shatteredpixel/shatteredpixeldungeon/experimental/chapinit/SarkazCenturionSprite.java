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
 * 萨卡兹百夫长贴图：760x38 = 42x38 帧，横向排列。
 * 图在 x=4 起笔、每 42px 一帧，帧间靠右留 1px 透明列。
 *
 * 帧分组（眼判，图集无源码可查）：
 * - 0-8  持矛行走/摆动（9 帧循环）
 * - 10-11 前倾突刺（攻击）
 * - 11-13 突刺后身体散成残片（当死亡用；这张图集没有专门的死亡动画）
 * - 9 是空帧，12-13 是零散残片，故不单独使用
 */
public class SarkazCenturionSprite extends NamsekSprite {

	public SarkazCenturionSprite() {
		super("sprites/namsek/SarkazCenturion.png", 42, 38, 0.5f);

		TextureFilm frames = film();

		idle = new Animation(1, true);
		idle.frames(frames, 0);

		run = new Animation(10, true);
		run.frames(frames, 0, 1, 2, 3, 4, 5, 6, 7, 8);

		attack = new Animation(8, false);
		attack.frames(frames, 10, 11);

		zap = attack.clone();

		die = new Animation(7, false);
		die.frames(frames, 11, 12, 13);

		play(idle);
	}
}
