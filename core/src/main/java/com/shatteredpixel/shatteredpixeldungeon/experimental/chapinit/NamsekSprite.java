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
 * namsek 素材（assets/sprites/namsek/）的通用贴图基类。
 *
 * 这些图集是从别处扒来的整张 sheet，帧比地图格（16px）大得多，所以：
 * <ul>
 *   <li>UV 保持整帧，几何尺寸按 tileScale 缩到地图格尺度（同 InfantrySprite 的做法）；</li>
 *   <li>必须覆写 frame()——动画每帧都会调用它，否则尺寸会被还原成原始帧大小。</li>
 * </ul>
 *
 * 子类在 super() 之后自行按图集的帧号分组配 idle/run/attack/zap/die。
 */
public class NamsekSprite extends MobSprite {

	protected final int frameW;
	protected final int frameH;
	private final float tileScale;

	/**
	 * @param texturePath 图集路径
	 * @param frameW      单帧宽（像素）
	 * @param frameH      单帧高（像素）
	 * @param tileScale   绘制缩放：源石虫图集本身已是地图格尺度（1f），
	 *                    人物图集约 36-42px/帧，取 0.5f 缩到地图格尺度
	 */
	public NamsekSprite( String texturePath, int frameW, int frameH, float tileScale ) {
		super();

		this.frameW = frameW;
		this.frameH = frameH;
		this.tileScale = tileScale;

		texture(texturePath);
	}

	protected TextureFilm film() {
		return new TextureFilm(texture, frameW, frameH);
	}

	@Override
	public void frame(RectF frame) {
		super.frame(frame);
		width = frameW * tileScale;
		height = frameH * tileScale;
		updateFrame();
		updateVertices();
	}
}
