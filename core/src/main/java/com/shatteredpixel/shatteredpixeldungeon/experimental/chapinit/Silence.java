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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/**
 * 沉默（d:/dungeon-repo/Enemy/QuestSpecified/Crownslayer(StarterLayer4).md
 * 「受到拖拉会被沉默一段时间」）：弑君者被拉拽时无法使用穿梭技能。
 *
 * TODO 目前没有挂上的入口——本 fork 的伤害管线（Proc.damage）不回调 Char.damage()，
 * 拉拽（击退类效果）也没有统一的钩子。等拉拽统一入口落地后，在拉拽结算处
 * Buff.prolong(target, Silence.class, 若干回合) 即可，{@link Crownslayer} 已经读这个 buff。
 */
public class Silence extends FlavourBuff {

	public static final float DURATION = 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.WEAKNESS;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
}
