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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
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

	/** 超过这个距离就穿梭，而不是一步步走 */
	private static final int BLINK_RANGE = 4;
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

	@Override
	protected boolean getCloser(int target) {
		//沉默（被拉拽）时不能穿梭，只能正常走路
		if (buff(Silence.class) == null
				&& Dungeon.level.distance(pos, target) > BLINK_RANGE
				&& blinkNear(target)) {
			return true;
		}
		return super.getCloser(target);
	}

	/** 传送到目标附近的空格；找不到落点就返回 false，退回普通移动 */
	private boolean blinkNear(int target) {

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

		if (candidates.isEmpty()) return false;

		pos = Random.element(candidates);
		return true;
	}

	//TODO 换正式立绘，tint 仅为区分占位图
	public static class Sprite extends PortedPlaceholderMobSprite {
		public Sprite() {
			super(0xA04040FF);
		}
	}
}
