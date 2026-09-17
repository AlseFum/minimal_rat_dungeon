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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NinePatch;

/**
 * 进层提示浮层：长得像窗口，但**不阻挡操作**——
 * 它不是 ui.Window（因此不计入 GameScene.showingWindow / interfaceBlockingHero），
 * 也没有 Window 那层全屏 PointerArea 遮罩，玩家可以照常走动、点地图。
 * 淡入 → 停留 → 淡出，然后自我销毁。
 *
 * 由 DungeonRegion.enterMessageAsNotice() 打开时，GameScene 用它代替 GLog 播报。
 */
public class EnterNotice extends Group {

	private static final float FADE_IN  = 0.4f;
	private static final float HOLD     = 3.5f;
	private static final float FADE_OUT = 0.8f;

	private static final int MARGIN = 5;
	private static final int MAX_WIDTH = 150;
	/** 浮层距 UI 顶部的距离 */
	private static final int TOP = 18;

	private final NinePatch chrome;
	private final RenderedTextBlock text;

	private float elapsed = 0;

	public EnterNotice( String message ) {
		super();

		text = PixelScene.renderTextBlock( message, 6 );
		text.maxWidth( MAX_WIDTH );

		chrome = Chrome.get( Chrome.Type.WINDOW );
		chrome.size( text.width() + MARGIN * 2 + chrome.marginHor(),
				text.height() + MARGIN * 2 + chrome.marginVer() );

		//Group 本身没有坐标，位置直接算在子节点上：UI 层顶部居中
		camera = PixelScene.uiCamera;
		float panelX = PixelScene.align( camera, (camera.width - chrome.width()) / 2f );
		chrome.x = panelX;
		chrome.y = TOP;
		add( chrome );

		text.setPos( panelX + chrome.marginLeft() + MARGIN, TOP + chrome.marginTop() + MARGIN );
		add( text );

		setAlpha( 0f );
	}

	@Override
	public void update() {
		super.update();

		elapsed += Game.elapsed;

		float a;
		if (elapsed < FADE_IN) {
			a = elapsed / FADE_IN;
		} else if (elapsed < FADE_IN + HOLD) {
			a = 1f;
		} else if (elapsed < FADE_IN + HOLD + FADE_OUT) {
			a = 1f - (elapsed - FADE_IN - HOLD) / FADE_OUT;
		} else {
			killAndErase();
			return;
		}

		setAlpha( a );
	}

	//Group 不会把 alpha 传给成员，逐个设
	private void setAlpha( float value ) {
		chrome.alpha( value );
		text.alpha( value );
	}
}
