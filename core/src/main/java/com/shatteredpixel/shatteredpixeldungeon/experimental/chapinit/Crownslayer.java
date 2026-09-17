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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * 弑君者（d:/dungeon-repo/Enemy/QuestSpecified/Crownslayer(StarterLayer4).md）：
 * HT=30, ATK=3~9, 无 DR；会一直向玩家行进，遇门不开门而是直接穿到门另一侧，避免被伏击；
 * 受到拖拉会被沉默一段时间。
 *
 * 实现说明：穿梭按「与目标拉开距离时直接跳到目标身边」实现，因此天然不触发开门
 * （门只是一个格子，落点要求可通行即可）。TODO 文档里「方向上有非玩家的敌方单位时，
 * 穿到其相对方向后一格并对其造成伤害」这条尚未实现。
 * 死亡掉落列表按 OR 语义随机一件（若干投掷武器 / 浮空药水 / 一件护甲）。
 */
public class Crownslayer extends Mob {

	/** 超过这个距离就穿梭，而不是一步步走（贴到 2 格内才收手） */
	private static final int BLINK_RANGE = 2;
	/** 穿梭落点与目标的距离上限 */
	private static final int BLINK_LANDING = 2;

	{
		spriteClass = Sprite.class;

		HP = HT = 30;
		defenseSkill = 6;

		properties.add(Property.MINIBOSS);

		lootChance = 1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(3, 9);
	}

	@Override
	public int attackSkill(Char target) {
		return 10;
	}

	/** 掉落列表 OR 语义：随机一件。投掷武器本身就是成组的（quantity 2~3） */
	@Override
	public Item createLoot() {
		switch (Random.Int(3)) {
			case 0:
				return Loot.randomMissile().quantity(Random.NormalIntRange(2, 3));
			case 1:
				return new PotionOfLevitation();
			default:
				return Loot.randomArmor();
		}
	}

	// ==================== 穿梭 ====================

	/**
	 * 穿梭作为一个独立回合动作：盯上目标且距离超过 {@link #BLINK_RANGE} 时直接跳过去，
	 * 这一回合不再移动/攻击。
	 *
	 * 不挂在 getCloser() 上——那条路只在状态机提议的落点也离得远时才走到，
	 * 实机几乎不会触发（怪一边走一边靠近，等发现玩家时已经在 4 格内了）。
	 */
	@Override
	protected ActionSubmission proposeAction() {

		if (tryBlink()) {
			//穿梭占掉这一整回合（IDLE 在提议阶段就付清整回合）
			return ActionSubmission.idle();
		}

		return super.proposeAction();
	}

	private boolean tryBlink() {

		//受到拖拉被沉默时不能穿梭
		if (buff(Silence.class) != null) return false;

		//用当前视野判断，避免依赖上一回合的状态机结论
		updateFovAndThrowItems();

		Char prey = (enemy != null && enemy.isAlive() && Actor.chars().contains(enemy))
				? enemy : chooseEnemy();
		if (prey == null || !prey.isAlive() || !fieldOfView[prey.pos]) return false;

		if (Dungeon.level.distance(pos, prey.pos) <= BLINK_RANGE) return false;

		int landing = findLanding(prey.pos);
		if (landing == -1) return false;

		//直接换格：落点要求可通行即可，因此不会去开门，也就绕开了门后的伏击
		int oldPos = pos;
		pos = landing;
		moveSprite(oldPos, pos);

		//两端各给一点特效，不然看起来只是"瞬移了一下"
		if (Dungeon.level.heroFOV[oldPos]) {
			CellEmitter.get(oldPos).burst(Speck.factory(Speck.LIGHT), 6);
		}
		if (Dungeon.level.heroFOV[landing]) {
			CellEmitter.get(landing).burst(Speck.factory(Speck.LIGHT), 6);
		}
		if (Dungeon.level.heroFOV[oldPos] || Dungeon.level.heroFOV[landing]) {
			Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		}

		spend(TICK);
		return true;
	}

	/** 目标附近的空格；找不到返回 -1 */
	private int findLanding(int target) {

		boolean[] passable = Dungeon.level.passable.clone();
		PathFinder.buildDistanceMap(target, passable, BLINK_LANDING);

		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < Dungeon.level.length(); i++) {
			if (PathFinder.distance[i] > 0 && PathFinder.distance[i] <= BLINK_LANDING
					&& !Dungeon.level.solid[i]
					&& Dungeon.level.passable[i]
					&& Actor.findChar(i) == null
					&& i != Dungeon.level.exit()) {
				candidates.add(i);
			}
		}

		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	public static class Sprite extends CrownSlayerSprite {
		public Sprite() {
			super();
		}
	}
}
