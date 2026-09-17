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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.DungeonRegion;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * 初始章节区域（d:/dungeon-repo/ThemePack/ChapInit.md）：仅 1-5 层出现，游戏起始教程。
 *
 * - 敌人：步兵 / 源石虫 / 迅捷源石虫（{@link ChapInitLevel}）
 * - 第 5 层 BOSS：碎骨（{@link SkullShattererBossLevel}）
 * - 第 4 层事件：200 回合后随机出现百夫长/弑君者/霜星（{@link StarterLayer4Event}）
 * - 地图：暂用下水道纹理（切尔诺伯格纹理待美术）
 *
 * 本区域与 sewers、acti、chapv/vi/vii 一起参与 1-5 层的区域竞争（权重 10）。
 */
public class ChapInitRegion extends DungeonRegion {

	@Override
	public String id() {
		return "chapinit";
	}

	@Override
	public float weight() {
		//只作为常规流程的起始章节；无尽模式的第 5 层另有自己的 BOSS 节奏，不参与竞争
		return Dungeon.mode == Dungeon.Mode.NORMAL
				&& Dungeon.branch == 0
				&& Dungeon.depth >= 1 && Dungeon.depth <= 5 ? 100 : 0;
	}

	@Override
	protected Level constructLevel() {
		Level level = isBossLevel() ? new SkullShattererBossLevel() : new ChapInitLevel();
		level.regionId = id();
		return level;
	}

	@Override
	public boolean isBossLevel() {
		return Dungeon.depth == 5 && Dungeon.branch == 0;
	}

	@Override
	public boolean shopOnLevel() {
		return Dungeon.depth == 2 && Dungeon.branch == 0;
	}

	@Override
	public void configureLevel(Level level) {
		if (isBossLevel()) {
			//BOSS 层出口接终章（与下水道 BOSS 层一致）
			LevelTransition exit = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
			if (exit != null) {
				exit.destDepth = 6;
				exit.destBranch = 63;
				exit.destType = LevelTransition.Type.REGULAR_ENTRANCE;
			}
		} else if (Dungeon.depth == 4) {
			//第 4 层挂上起始层事件；实体本身不可见（角落墙格永不在视野里），
			//回合数与触发状态随存档往返。Actor 注册由后续的 Actor.init() 完成。
			level.addEntity(new StarterLayer4Event(), 0);
		}
	}

	@Override
	public String enterMessage() {
		return Messages.get(this, "enter");
	}

	/** 用不阻挡操作的浮层显示进入信息，而不是刷一行日志（见 {@link EnterNotice}） */
	@Override
	public boolean enterMessageAsNotice() {
		return true;
	}
}
