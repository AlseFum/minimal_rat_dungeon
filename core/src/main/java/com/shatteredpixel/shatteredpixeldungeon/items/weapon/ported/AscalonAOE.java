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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.sprites.SpriteRegistry;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

/** Ascalon's signature weapon — sweeping AOE attacks with lingering wounds. */
public class AscalonAOE extends MeleeWeapon {

	static {
		SpriteRegistry.r("ported.ascalon", "sprites/ported/ascalon_weapon.png", 0, 0, 64, 64);
	}

	@Override
	public String spriteRegion() {
		return "ported.ascalon";
}

	{
		//ZootDungeon used TextureRegistry.texture("sheet.cola.ascalon_weapon", ...) — not ported, placeholder sprite
		image = ItemSpriteSheet.DAGGER; //TODO: dedicated icon
		tier = 1;
		bones = false;
		RCH = 3;
		usesTargeting = false;
	}

	private static final int AOE_RADIUS = 3;
	private static final float AOE_DAMAGE_MULT = 0.3f;
	private static final float EVASION_BONUS = 1.5f;

	// 防止在 AOE 技能攻击中重复触发 proc 内的 Wound 逻辑
	private static boolean applyingAOEAbility = false;

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (super.doEquip(hero)) {
			Buff.affect(hero, EvasionBonus.class);
			return true;
		}
		return false;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)) {
			Buff.detach(hero, EvasionBonus.class);
			return true;
		}
		return false;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);

		if (attacker instanceof Hero && defender.isAlive() && !applyingAOEAbility) {
			Hero hero = (Hero) attacker;
			boolean inFog = Blob.volumeAt(hero.pos, SmokeScreen.class) > 0
					|| Blob.volumeAt(defender.pos, SmokeScreen.class) > 0;
			AscalonWound.applyFrom(hero, defender, damage, inFog);
		}

		return damage;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) return;

		if (target != hero.pos) {
			PathFinder.buildDistanceMap(target, Dungeon.level.passable, AOE_RADIUS);
		}

		//port of ZootDungeon Select.chars().all().except(hero/ally).that(...) query:
		//all living enemies in FOV, within AOE radius (or adjacent, for self-centered casts)
		java.util.Set<Char> targets = new java.util.HashSet<>();
		for (Char ch : Actor.chars()) {
			if (ch == hero) continue;
			if (ch.alignment == Char.Alignment.ALLY) continue;
			if (!ch.isAlive() || !Dungeon.level.heroFOV[ch.pos]) continue;
			if (target == hero.pos) {
				if (Dungeon.level.adjacent(hero.pos, ch.pos)) targets.add(ch);
			} else {
				if (ch.pos >= 0 && ch.pos < PathFinder.distance.length
						&& PathFinder.distance[ch.pos] <= AOE_RADIUS
						&& PathFinder.distance[ch.pos] > 0) targets.add(ch);
			}
		}

		if (targets.isEmpty()) {
			GLog.w(Messages.get(this, "ability_no_target"));
			hero.belongings.abilityWeapon = null;
			return;
		}

		beforeAbilityUsed(hero, null);
		applyingAOEAbility = true;

		final java.util.Set<Char> finalTargets = targets;
		final int[] hitCount = {0};
		for (Char enemy : finalTargets) {
			hero.sprite.attack(enemy.pos, () -> {
				hero.attack(enemy, 1f, 0f, Char.INFINITE_ACCURACY);

				int aoeDamage = Math.max(1, Math.round(hero.damageRoll() * AOE_DAMAGE_MULT));
				boolean inFog = Blob.volumeAt(hero.pos, SmokeScreen.class) > 0
						|| Blob.volumeAt(enemy.pos, SmokeScreen.class) > 0;
				AscalonWound.applyFrom(hero, enemy, aoeDamage, inFog);

				hitCount[0]++;
				if (hitCount[0] == finalTargets.size()) {
					applyingAOEAbility = false;
					Invisibility.dispel();
					hero.spendAndNext(hero.attackDelay());
					afterAbilityUsed(hero);
				}
			});
		}
	}

	@Override
	public String abilityInfo() {
		int avgDmg = (min(0) + max(0)) / 2;
		int aoeDmg = Math.max(1, Math.round(avgDmg * AOE_DAMAGE_MULT));
		return Messages.get(this, "ability_desc", min(0), max(0), aoeDmg, AOE_RADIUS);
	}

	// ===== 闪避加成 =====

	public static class EvasionBonus extends FlavourBuff {

		{
			type = buffType.POSITIVE;
			announced = false;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_EVASIVE;
		}
	}

	public static float evasionMultiplier(Char target) {
		if (target.buff(EvasionBonus.class) != null) {
			return EVASION_BONUS;
		}
		return 1f;
	}

	public static boolean isApplyingAOEAbility() {
		return applyingAOEAbility;
	}

	// ===== 创伤（AscalonWound）=====

	public static class AscalonWound extends Buff {

		public static final float DURATION = 25f;

		private static final float DOT_MULT_NO_FOG = 0.10f;
		private static final float DOT_MULT_IN_FOG = 0.20f;
		private static final float STACK_DOT_EXTRA_CAP = 0.12f;
		private static final float MAX_DOT_MULT = 0.40f;
		private static final float LIFE_STEAL_FROM_DOT_RATE = 0.10f;
		private static final float KILL_HEAL_MAX_HP_RATE = 0.10f;
		private static final float STACK_STEAL_EXTRA_CAP = 0.08f;
		private static final float MAX_LIFE_STEAL = 0.40f;
		private static final float MAX_KILL_HEAL_RATE = 0.40f;
		private static final float STACK_CURVE = 0.45f;

		public Char caster;
		public int damagePerTick;
		public int stackCount;
		private float left = DURATION;

		{
			type = buffType.NEGATIVE;
			announced = true;
		}

		private static final String CASTER_ID = "caster_id";
		private static final String DAMAGE_PER_TICK = "damagePerTick";
		private static final String LEFT = "left";
		private static final String HERO_ATTACK_DAMAGE_LEGACY = "heroAttackDamage";
		private static final String IN_FOG_WHEN_ATTACKED = "inFogWhenAttacked";
		private static final String STACK_COUNT = "stackCount";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CASTER_ID, caster != null ? caster.id() : -1);
			bundle.put(DAMAGE_PER_TICK, damagePerTick);
			bundle.put(STACK_COUNT, stackCount);
			bundle.put(LEFT, left);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			if (bundle.contains(CASTER_ID)) {
				int id = bundle.getInt(CASTER_ID);
				Actor a = id >= 0 ? Actor.findById(id) : null;
				caster = a instanceof Char ? (Char) a : null;
			} else {
				caster = Dungeon.hero;
			}
			if (bundle.contains(DAMAGE_PER_TICK)) {
				damagePerTick = bundle.getInt(DAMAGE_PER_TICK);
			} else {
				float oldSnap = bundle.getFloat(HERO_ATTACK_DAMAGE_LEGACY);
				boolean fog = bundle.getBoolean(IN_FOG_WHEN_ATTACKED);
				float mult = fog ? DOT_MULT_IN_FOG : DOT_MULT_NO_FOG;
				damagePerTick = Math.max(1, Math.round(oldSnap * mult));
			}
			if (bundle.contains(STACK_COUNT)) {
				stackCount = bundle.getInt(STACK_COUNT);
			} else {
				stackCount = 1;
			}
			if (bundle.contains(LEFT)) {
				left = bundle.getFloat(LEFT);
			} else {
				left = DURATION;
			}
		}

		public static void applyFrom(Char caster, Char target, float attackSnapshot, boolean inFog) {
			if (caster == null || !target.isAlive()) return;
			AscalonWound w = target.buff(AscalonWound.class);
			if (w == null) {
				w = new AscalonWound();
				if (!w.attachTo(target)) return;
			}
			float duration = DURATION * target.resist(AscalonWound.class);
			w.extend(duration);
			w.set(caster, Math.max(1f, attackSnapshot), inFog);
		}

		public void set(Char caster, float attackSnapshot, boolean inFog) {
			this.caster = caster;
			stackCount++;
			float base = inFog ? DOT_MULT_IN_FOG : DOT_MULT_NO_FOG;
			float mult = base + diminishingExtra(stackCount, STACK_DOT_EXTRA_CAP);
			mult = Math.min(mult, MAX_DOT_MULT);
			int next = Math.max(1, Math.round(attackSnapshot * mult));
			this.damagePerTick = Math.max(this.damagePerTick, next);
		}

		private static float diminishingExtra(int stacks, float cap) {
			return cap * (1f - 1f / (1f + STACK_CURVE * Math.max(0, stacks - 1)));
		}

		public void extend(float duration) {
			left = Math.max(left, duration);
		}

		@Override
		public int icon() {
			return BuffIndicator.BLEEDING;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.8f, 0.2f, 0.2f);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, left / DURATION);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString((int) left);
		}

		//note: kept for API parity, but no movement-speed hook in minimal — speed not modified
		public float speedFactor() {
			return 0.82f;
		}

		@Override
		public boolean act() {
			if (!target.isAlive() || left <= 0) {
				detach();
				return true;
			}

			int hpBefore = target.HP;
			//goes through minimal Char.damage(int, Object) shim → Proc.fromLegacy
			target.damage(damagePerTick, this);

			if (caster != null && caster.isAlive() && target.HP < hpBefore) {
				int lost = hpBefore - target.HP;
				float steal = Math.min(
						LIFE_STEAL_FROM_DOT_RATE + diminishingExtra(stackCount, STACK_STEAL_EXTRA_CAP),
						MAX_LIFE_STEAL);
				healCaster(Math.round(lost * steal));
			}

			if (!target.isAlive() && target instanceof Mob && caster != null && caster.isAlive()) {
				float killRate = Math.min(
						KILL_HEAL_MAX_HP_RATE + diminishingExtra(stackCount, STACK_STEAL_EXTRA_CAP),
						MAX_KILL_HEAL_RATE);
				int heal = Math.round(caster.HT * killRate);
				if (heal > 0) {
					healCaster(heal);
					GLog.p(Messages.get(this, "heal", heal));
				}
			}

			left -= TICK;
			spend(TICK);
			return true;
		}

		private void healCaster(int amount) {
			if (amount <= 0 || caster == null || !caster.isAlive()) return;
			caster.HP = Math.min(caster.HP + amount, caster.HT);
			if (caster.sprite != null && caster.HP < caster.HT) {
				caster.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
			}
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(left));
		}
	}
}
