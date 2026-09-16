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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Entity;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * 起始层事件（d:/dungeon-repo/Quest/StarterLayer4.md）：第 4 层经过 200 回合后，
 * 随机生成百夫长/弑君者/霜星之一。
 *
 * 实现为一个地面 Entity（不占格、随层持久化，见 actors/Entity）：
 * 挂在角落墙格上，实体贴图按 heroFOV 逐帧显隐，所以玩家看不到它，
 * 但回合数与「已触发」状态都会随存档往返。
 */
public class StarterLayer4Event extends Entity {

	/** 触发前需要经过的回合数 */
	private static final int DELAY = 200;
	/** 生成点与英雄的最小距离，避免贴脸刷怪 */
	private static final int MIN_SPAWN_DISTANCE = 6;

	private static final String LEFT = "left";
	private static final String SPAWNED = "spawned";

	private int left = DELAY;
	private boolean spawned;

	@Override
	protected boolean act() {
		if (left > 0) {
			left--;
		} else if (!spawned) {
			spawnMiniboss();
		}
		spend(TICK);
		return true;
	}

	private void spawnMiniboss() {

		spawned = true;

		Mob mob = Random.oneOf(new Centurion(), new Crownslayer(), new FrostNova());

		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < Dungeon.level.length(); i++) {
			if (Dungeon.level.passable[i]
					&& !Dungeon.level.solid[i]
					&& Actor.findChar(i) == null
					&& Dungeon.level.distance(i, Dungeon.hero.pos) >= MIN_SPAWN_DISTANCE) {
				candidates.add(i);
			}
		}

		//找不到落脚点（理论上不会发生）：安静地结束，不刷怪
		if (candidates.isEmpty()) {
			despawn();
			return;
		}

		mob.pos = Random.element(candidates);
		mob.state = mob.WANDERING;

		GameScene.add(mob);
		GLog.w(Messages.get(this, "spawn", mob.name()));

		despawn();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
		bundle.put(SPAWNED, spawned);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getInt(LEFT);
		spawned = bundle.getBoolean(SPAWNED);
	}
}
