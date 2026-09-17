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
 * 源石虫贴图：440x16 = 20 个 22x16 帧，其中 0-13 有内容、14-19 为空。
 * 图集本身就是地图格尺度（画面 19px 宽、~15px 高），不缩放。
 *
 * 帧分组是从图上动作推断的（没有原始工程代码可查），若实机观感不对，改这里即可：
 * - 0-7 爬行循环（身体起伏 + 腿脚交错）
 * - 3-5 单次扑咬（取爬行中身体压低、前足前伸的一段当攻击）
 * - 8-13 死亡（身体逐渐压扁变暗，最后翻肚朝天）
 *
 * 两种源石虫共用这套分组，只是图集不同（肚子黄色/红色）。
 */
public class BugSprite extends NamsekSprite {

	/** 图集本身是地图格尺度（22x16），1f = 原尺寸；源石虫实机偏大，取 0.5f */
	public BugSprite( String texturePath, float tileScale ) {
		super(texturePath, 22, 16, tileScale);

		TextureFilm frames = film();

		idle = new Animation(1, true);
		idle.frames(frames, 0);

		run = new Animation(10, true);
		run.frames(frames, 0, 1, 2, 3, 4, 5, 6, 7);

		attack = new Animation(12, false);
		attack.frames(frames, 3, 4, 5);

		zap = attack.clone();

		die = new Animation(9, false);
		die.frames(frames, 8, 9, 10, 11, 12, 13);

		play(idle);
	}
}
