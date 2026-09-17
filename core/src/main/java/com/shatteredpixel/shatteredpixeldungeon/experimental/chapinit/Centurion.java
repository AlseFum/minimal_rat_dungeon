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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

/**
 * 萨卡兹百夫长（d:/dungeon-repo/Enemy/QuestSpecified/Centurion(StarterLayer4).md）：
 * HT=30, ATK=2~8, DR=1；远程造成法术伤害，范围曼哈顿 3 格，每次 3~6 点。
 * 死亡掉落是「一瓶治疗药水和一个法杖」列表，按 OR 语义随机一件。
 */
public class Centurion extends Mob {

	/** 法术射程（曼哈顿 3 格） */
	private static final int CAST_RANGE = 3;
	private static final int CAST_MIN = 3;
	private static final int CAST_MAX = 6;

	{
		spriteClass = Sprite.class;

		HP = HT = 30;
		defenseSkill = 4;

		properties.add(Property.MINIBOSS);

		lootChance = 1f;
		exp = 6;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(2, 8);
	}

	@Override
	public int attackSkill(Char target) {
		return 10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 1);
	}

	/** 掉落列表 OR 语义：随机一件 */
	@Override
	public Item createLoot() {
		if (Random.Int(2) == 0) {
			return new PotionOfHealing();
		}
		return Loot.randomUsingDefaults(Loot.WAND);
	}

	// ==================== 远程法术 ====================

	@Override
	protected boolean canAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}
		if (enemy == null || !enemy.isAlive()) {
			return false;
		}
		if (Dungeon.level.distance(pos, enemy.pos) > CAST_RANGE) {
			return false;
		}
		//法术可以绕过拐角，只要不被别的角色挡下
		return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {

		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return super.doAttack(enemy);
		}

		final int target = enemy.pos;

		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.zap(target, new Callback() {
				@Override
				public void call() {
					castBolt(target);
					Invisibility.dispel(Centurion.this);
					spend(pendingAttackDelay / 2f);
					sprite.idle();
					next();
				}
			});
			return false;
		} else {
			castBolt(target);
			Invisibility.dispel(this);
			spend(pendingAttackDelay / 2f);
			return true;
		}
	}

	private void castBolt(int cell) {
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.get(cell).burst(Speck.factory(Speck.LIGHT), 6);
			Sample.INSTANCE.play(Assets.Sounds.ZAP);
		}

		Char ch = Actor.findChar(cell);
		if (ch == null || ch == this || !ch.isAlive()) return;

		Proc.damage(new DamageInfo()
				.offender(this)
				.defender(ch)
				.way(DamageWay.MAGIC)
				.type(DamageType.PHYSICAL)
				.solidDamage(Random.NormalIntRange(CAST_MIN, CAST_MAX)));
	}

	public static class Sprite extends SarkazCenturionSprite {
		public Sprite() {
			super();
		}
	}
}
