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

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

/**
 * 初始章节（ChapInit）占位贴图：本章节各敌人暂无专属美术，统一复用
 * ported/infantry.png，以 tint 区分角色。
 *
 * TODO 每个角色拿到正式立绘后，改为自己的贴图/帧号，并删掉这里的 tint 逻辑。
 *
 * 帧序沿用 ZootDungeon 原值：idle=23，run=0..6，attack=8..12，die=13..21。
 * 远程动作用 zap，暂无专属帧，复用 attack 帧。
 */
public class PortedPlaceholderMobSprite extends MobSprite {

	private static final String PLACEHOLDER_TEX = "sprites/namsek/Infantry.png";
	private static final int FRAME_SIZE = 32;
	/** 单帧 32x32 挤进地图格的比例（0.5 = 16px） */
	private static final float TILE_SCALE = 0.5f;

	/** ARGB，alpha 即 tint 强度（如 0x80AA8844 = 半强度棕） */
	private final int tintColor;

	public PortedPlaceholderMobSprite( int tintColor ) {
		super();

		this.tintColor = tintColor;

		texture(PLACEHOLDER_TEX);
		//800x32 = 25 个 32x32 帧
		TextureFilm frames = new TextureFilm(texture, FRAME_SIZE, FRAME_SIZE);

		idle = new Animation(1, true);
		idle.frames(frames, 23);

		run = new Animation(10, true);
		run.frames(frames, 0, 1, 2, 3, 4, 5, 6);

		attack = new Animation(10, false);
		attack.frames(frames, 8, 9, 10, 11, 12);

		zap = attack.clone();

		die = new Animation(9, false);
		die.frames(frames, 13, 14, 15, 16, 17, 18, 19, 20, 21);

		resetColor();

		play(idle);
	}

	/**
	 * 大图素材不走 scale 缩放，按 fork 处理移植贴图的惯例（见 ItemSprite.view）：
	 * UV 保持整帧，重置几何尺寸并重建顶点。必须覆写 frame()——动画每帧都会调用它，
	 * 否则尺寸会被还原成 32x32。
	 */
	@Override
	public void frame(RectF frame) {
		super.frame(frame);
		width = FRAME_SIZE * TILE_SCALE;
		height = FRAME_SIZE * TILE_SCALE;
		updateFrame();
		updateVertices();
	}

	/**
	 * 受击闪光结束时引擎会重置顶点色，会把占位 tint 一并抹掉，所以这里补回来。
	 */
	@Override
	public void resetColor() {
		super.resetColor();
		if (tintColor != 0) {
			tint(tintColor);
		}
	}
}
