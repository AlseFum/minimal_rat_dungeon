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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

/**
 * 碎骨（d:/dungeon-repo/Enemy/SkullShatterer.md），初始章节第五层 BOSS：
 * HT=60, ATK=2~9, DR=2；距离较远时发射榴弹造成短十字范围杀伤并上脆弱；
 * 半血时给自己挂掉血狂暴；死亡掉落升级用的东西。
 *
 * BOSS 生命周期照 Goo（seal/unseal + BossHealthBar），但**不含 damage() 钩子**：
 * 本 fork 的伤害管线（Proc.damage）直接扣血、不回调 Char.damage()，所以
 * 半血判定放在 {@link #proposeAction()} 里按回合检查。
 */
public class SkullShatterer extends Mob {

	/** 榴弹射程（格） */
	private static final int GRENADE_RANGE = 8;
	/** 榴弹冷却（回合） */
	private static final int GRENADE_COOLDOWN = 2;
	/** 榴弹爆炸伤害 3~8 */
	private static final int GRENADE_MIN = 3;
	private static final int GRENADE_MAX = 8;
	/** 狂暴时的攻击力倍率 */
	private static final float RAGE_DAMAGE_MULT = 1.5f;

	private int grenadeCd = 0;

	{
		spriteClass = Sprite.class;

		HP = HT = 60;
		defenseSkill = 5;

		properties.add(Property.BOSS);

		//掉落全部走 die()，避免 rollToDropLoot 的随机判定
		lootChance = 0f;

		exp = 10;
	}

	@Override
	public int damageRoll() {
		int dmg = Random.NormalIntRange(2, 9);
		if (buff(SkullRage.class) != null) {
			dmg = Math.round(dmg * RAGE_DAMAGE_MULT);
		}
		return dmg;
	}

	@Override
	public int attackSkill(Char target) {
		return 12;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 2);
	}

	/**
	 * 每回合的自家逻辑：半血狂暴 + 榴弹冷却。
	 * Char.act() 是 final，本 fork 的回合逻辑钩子是 proposeAction。
	 */
	@Override
	protected ActionSubmission proposeAction() {
		if (HP > 0 && HP * 2 <= HT && buff(SkullRage.class) == null) {
			enrage();
		}
		if (grenadeCd > 0) grenadeCd--;
		return super.proposeAction();
	}

	private void enrage() {
		Buff.affect(this, SkullRage.class);
		if (sprite != null) {
			sprite.showStatus(CharSprite.WARNING, Messages.get(this, "enraged"));
		}
		yell(Messages.get(this, "rage"));
	}

	// ==================== 榴弹 ====================

	/** 相邻用普通近战，否则在射程内且弹道通畅时用榴弹（冷却中则重新靠近） */
	@Override
	protected boolean canAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}
		if (enemy == null || !enemy.isAlive() || grenadeCd > 0) {
			return false;
		}
		if (Dungeon.level.distance(pos, enemy.pos) > GRENADE_RANGE) {
			return false;
		}
		return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
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
					throwGrenade(target);
					Invisibility.dispel(SkullShatterer.this);
					spend(pendingAttackDelay / 2f);
					sprite.idle();
					next();
				}
			});
			return false;
		} else {
			throwGrenade(target);
			Invisibility.dispel(this);
			spend(pendingAttackDelay / 2f);
			return true;
		}
	}

	/** 短十字范围杀伤：中心格 + 上下左右各一格 */
	private void throwGrenade(int cell) {

		grenadeCd = GRENADE_COOLDOWN;

		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
		}
		Sample.INSTANCE.play(Assets.Sounds.BLAST);

		int w = Dungeon.level.width();
		int[] cells = {cell, cell - 1, cell + 1, cell - w, cell + w};

		for (int c : cells) {
			if (!Dungeon.level.insideMap(c)) continue;

			if (Dungeon.level.heroFOV[c]) {
				CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4);
			}

			Char ch = Actor.findChar(c);
			if (ch == null || ch == this || !ch.isAlive()) continue;

			Proc.damage(new DamageInfo()
					.offender(this)
					.defender(ch)
					.way(DamageWay.RANGED)
					.type(DamageType.PHYSICAL)
					.solidDamage(Random.NormalIntRange(GRENADE_MIN, GRENADE_MAX)));
			//榴弹附带脆弱（复用原版 Vulnerable）
			Buff.affect(ch, Vulnerable.class, Vulnerable.DURATION);
		}
	}

	// ==================== BOSS 生命周期 ====================

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			Dungeon.level.seal();
			yell(Messages.get(this, "notice"));
		}
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

		Dungeon.level.unseal();
		GameScene.bossSlain();

		//出口是 LOCKED_EXIT，靠 WORN 钥匙开（同 Goo）
		Dungeon.level.drop(new Key(Key.Kind.WORN, Dungeon.depth), pos).sprite.drop();
		//BOSS 掉落升级用的东西
		Dungeon.level.drop(new ScrollOfUpgrade(), pos).sprite.drop();

		//与下水道 BOSS 相同的进度发放：本章节区域可能顶替第 5 层，
		//漏掉这两件会让玩家拿不到子职业/护甲基能
		if (Dungeon.hero != null && Dungeon.hero.belongings != null && Dungeon.hero.isAlive()) {
			if (Dungeon.hero.belongings.getItem(TengusMask.class) == null) {
				new TengusMask().collect();
			}
			if (Dungeon.hero.belongings.getItem(KingsCrown.class) == null) {
				new KingsCrown().collect();
			}
		}

		Badges.validateBossSlain();
		if (Statistics.qualifiedForBossChallengeBadge) {
			Badges.validateBossChallengeCompleted();
		}
		Statistics.bossScores[0] += 1000;

		yell(Messages.get(this, "defeated"));
	}

	// ==================== 存档 ====================

	private static final String GRENADE_CD = "grenadecd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GRENADE_CD, grenadeCd);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		grenadeCd = bundle.getInt(GRENADE_CD);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}

	//TODO 换正式立绘，tint 仅为区分占位图
	public static class Sprite extends PortedPlaceholderMobSprite {
		public Sprite() {
			super(0xA0C04040);
		}
	}
}
