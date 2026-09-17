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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/**
 * 霜星（d:/dungeon-repo/Enemy/QuestSpecified/Frostnova(StarterLayer4).md）：
 * HT=30, ATK=4~9, 无 DR；每 8 回合对曼哈顿 2 格内造成寒冷效果；
 * 近战对寒冷目标 1.5 倍、冻结目标 2 倍伤害。
 * 死亡掉落列表按 OR 语义随机一件（净化药水 / 只附魔寒霜的附魔符石）。
 *
 * 说明：脉冲只上寒冷不上冻结——本 fork 的 Frost（冻结）会让目标麻痹，
 * 由她主动挂上过于致命，冻结点数交给玩家自己的冰系效果。
 */
public class FrostNova extends Mob {

	/** 每 8 回合一次脉冲 */
	private static final int PULSE_INTERVAL = 8;
	/** 脉冲范围（曼哈顿） */
	private static final int PULSE_RANGE = 2;
	/** 脉冲上寒冷的回合数 */
	private static final float PULSE_CHILL = 6f;

	private int turnsToPulse = PULSE_INTERVAL;

	{
		spriteClass = Sprite.class;

		HP = HT = 30;
		defenseSkill = 5;

		properties.add(Property.MINIBOSS);

		lootChance = 1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(4, 9);
	}

	@Override
	public int attackSkill(Char target) {
		return 10;
	}

	/** 近战对寒冷/冻结的目标有叠加伤害（Char.attack 里在护甲结算后调用） */
	@Override
	public int attackProc(Char enemy, int damage) {
		if (enemy.buff(Frost.class) != null) {
			damage = Math.round(damage * 2f);
		} else if (enemy.buff(Chill.class) != null) {
			damage = Math.round(damage * 1.5f);
		}
		return super.attackProc(enemy, damage);
	}

	/** 回合计时：Char.act() 是 final，本 fork 的回合逻辑钩子是 proposeAction */
	@Override
	protected ActionSubmission proposeAction() {
		if (--turnsToPulse <= 0) {
			turnsToPulse = PULSE_INTERVAL;
			coldPulse();
		}
		return super.proposeAction();
	}

	private void coldPulse() {

		boolean any = false;

		for (Char ch : Actor.chars().toArray(new Char[0])) {
			if (ch == this || !ch.isAlive()) continue;
			if (Dungeon.level.distance(pos, ch.pos) > PULSE_RANGE) continue;

			Buff.affect(ch, Chill.class, PULSE_CHILL);
			any = true;

			if (Dungeon.level.heroFOV[ch.pos]) {
				CellEmitter.get(ch.pos).burst(SnowParticle.FACTORY, 4);
			}
		}

		if (any) {
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
		}
	}

	/** 掉落列表 OR 语义：随机一件 */
	@Override
	public Item createLoot() {
		if (Random.Int(2) == 0) {
			return new PotionOfPurity();
		}
		return new StoneOfFrostEnchantment();
	}

	// ==================== 存档 ====================

	private static final String TURNS_TO_PULSE = "turnstopulse";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TURNS_TO_PULSE, turnsToPulse);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turnsToPulse = bundle.getInt(TURNS_TO_PULSE);
	}

	public static class Sprite extends FrostNovaSprite {
		public Sprite() {
			super();
		}
	}
}
