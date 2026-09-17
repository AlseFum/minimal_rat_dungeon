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
 * 霜星贴图：1800x288 = 36x36 帧的整张大图集（50 列 x 8 行，约 350 帧，多个动作段拼在一起），
 * 这里只取其中白袍兜帽形态的一段。
 *
 * 帧分组（眼判，图集无源码可查）：
 * - 25-34 行走循环（10 帧）
 * - 35-36 倒地（死亡）
 * - 41-44 蹲身施法（攻击）
 * 若想换用图集里别的一段，只改这里的帧号即可。
 */
public class FrostNovaSprite extends NamsekSprite {

	public FrostNovaSprite() {
		super("sprites/namsek/FrostNova.png", 36, 36, 0.5f);

		TextureFilm frames = film();

		idle = new Animation(1, true);
		idle.frames(frames, 25);

		run = new Animation(10, true);
		run.frames(frames, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34);

		attack = new Animation(8, false);
		attack.frames(frames, 41, 42, 43, 44);

		zap = attack.clone();

		die = new Animation(5, false);
		die.frames(frames, 35, 36);

		play(idle);
	}
}
