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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.sprites.SpriteRegistry;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * 由 ZootDungeon 项目的 items.weapon.PhantomKnife 移植而来。
 * 偷袭偏伤与 max 公式内联在此（原 AmbushWeapon 基类已并入），
 * 偷袭系决斗者技能复用 Dagger.sneakAbility。
 * image/tier/bones 亦沿用基类占位（DAGGER + TODO 图标，原 TextureRegistry 专用贴图未移植）。
 *
 * <p>note: 文本（name、desc_intro、desc_charge、desc_body、msg_charge_up、msg_summoned、
 * prompt、ability_name、ability_desc、typical_ability_desc、ability_target_range、ability_occupied）仍通过 Messages 读取，
 * 但 minimal fork 未随迁对应 .properties，缺失时会显示回退键名。
 */
public class PhantomKnife extends MeleeWeapon {

	/** fraction of the damage range kept as a lower bound on surprise hits */
	public float ambushRate = 0.5f;

	static {
		SpriteRegistry.r("ported.phantom_knife", "sprites/ported/phantom_knife.png", 0, 0, 64, 64);
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +
				lvl*(tier+1);
	}

	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero) owner;
			Char enemy = hero.enemy();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
				//surprise hit: bias the roll towards the high end
				int lvl = buffedLvl();
				int mn = min(lvl);
				int mx = max(lvl);
				int diff = mx - mn;
				int biasedMin = mn + Math.round(diff * ambushRate);
				if (biasedMin > mx) biasedMin = mx;

				int damage = Random.NormalIntRange(biasedMin, mx);
				damage = augment.damageFactor(damage);
				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					damage += Hero.heroDamageIntRange(0, exStr);
				}
				return damage;
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Dagger.sneakAbility(hero, target, 5, 2+buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 2+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 2);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(2+level);
	}

	@Override
	public String spriteRegion() {
		return "ported.phantom_knife";
}

	private static final String CHARGE = "charge";
	private static final String CHARGE_CAP = "chargeCap";

	private int charge = 0;
	private int chargeCap = 10;

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		StringBuilder sb = new StringBuilder(Messages.get(this, "desc_intro"));
		if (charge > 0) {
			sb.append("\n\n").append(Messages.get(this, "desc_charge", charge, chargeCap));
		}
		sb.append("\n\n").append(Messages.get(this, "desc_body"));
		return sb.toString();
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);

		if (attacker instanceof Hero) {
			Hero hero = (Hero) attacker;

			boolean isAmbush = defender instanceof Mob && ((Mob) defender).surprisedBy(hero);

			if (isAmbush) {
				boolean willKill = defender.HP <= damage;

				if (charge > 0 && !willKill) {
					trySummonAndAttack(hero, defender);
				}

				if (willKill && charge < chargeCap) {
					charge++;
					updateQuickslot();
					GLog.p(Messages.get(this, "msg_charge_up", charge, chargeCap));
				}
			}
		}

		return damage;
	}

	private void trySummonAndAttack(Hero hero, Char enemy) {
		int summonPos = findNearbyEmptyCell(enemy.pos);
		if (summonPos == -1) {
			return;
		}

		int basePower = tier * 2;
		int chargePower = Math.min(charge, chargeCap);
		int powerLevel = basePower + chargePower;

		SummonedMinion minion = new SummonedMinion(this, powerLevel, tier);
		minion.pos = summonPos;
		minion.state = minion.HUNTING;
		minion.setTarget(enemy.pos);

		GameScene.add(minion);

		CellEmitter.get(summonPos).burst(Speck.factory(Speck.STAR), 6);
		Sample.INSTANCE.play(Assets.Sounds.MELD);

		charge--;
		updateQuickslot();

		GLog.p(Messages.get(this, "msg_summoned", minion.name()));

		if (enemy.isAlive() && Actor.findChar(enemy.pos) == enemy) {
			minion.setEnemy(enemy);
			minion.state = minion.HUNTING;
		}
	}

	private void refundCharge(int amount) {
		if (amount <= 0) return;
		int before = charge;
		charge = Math.min(chargeCap, charge + amount);
		if (charge != before) {
			updateQuickslot();
			GLog.p(Messages.get(this, "msg_charge_up", charge, chargeCap));
		}
	}

	private int findNearbyEmptyCell(int centerPos) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int pos = centerPos + offset;
			if (Dungeon.level.passable[pos] && Actor.findChar(pos) == null) {
				return pos;
			}
		}

		for (int offset : PathFinder.NEIGHBOURS8) {
			int pos = centerPos + offset * 2;
			if (pos >= 0 && pos < Dungeon.level.length()
					&& Dungeon.level.passable[pos]
					&& Actor.findChar(pos) == null) {
				return pos;
			}
		}

		return -1;
	}

	@Override
	public Item upgrade() {
		chargeCap += 2;
		if (chargeCap > 20) chargeCap = 20;
		return super.upgrade();
	}

	public PhantomKnife randomize() {
		tier = 1;
		level(Random.IntRange(0, 3));
		chargeCap = Random.IntRange(8, 20);
		charge = Random.IntRange(0, chargeCap);
		ambushRate = Random.Float(0.4f, 1.2f);
		return this;
	}

	@Override
	public Item random() {
		return randomize();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(CHARGE_CAP, chargeCap);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
		chargeCap = bundle.getInt(CHARGE_CAP);
		if (chargeCap == 0) chargeCap = 10;
	}

	public static class SummonedMinion extends Mob {

		private PhantomKnife weapon;
		private int powerLevel;
		private int weaponTier;
		private int idleTurns = 0;
		private static final int MAX_IDLE_TURNS = 5;

		{
			spriteClass = GhostSprite.class;
			alignment = Alignment.ALLY;
			lootChance = 0f;
			//note: minimal fork 的 Mob 无 EXP 字段（本 fork 无经验系统），原 EXP = 0 未移植
			state = HUNTING;
		}

		/** For bundle restore only; fields are filled by {@link #restoreFromBundle}. */
		public SummonedMinion() {
			this(null, 1, 1);
		}

		public SummonedMinion(PhantomKnife weapon, int powerLevel, int weaponTier) {
			this.weapon = weapon;
			this.powerLevel = powerLevel;
			this.weaponTier = weaponTier;

			HP = HT = 10 + powerLevel * 2 + weaponTier * 3;
			defenseSkill = 2 + powerLevel + weaponTier;
			//note: minimal fork 的 Mob 无 maxLvl 字段（无等级成长系统），原 maxLvl 上限设定未移植
		}

		@Override
		public int damageRoll() {
			int base = Random.NormalIntRange(1, 3);
			return base + powerLevel + weaponTier;
		}

		@Override
		public int attackSkill(Char target) {
			return 8 + powerLevel * 2 + weaponTier * 2;
		}

		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, (powerLevel + weaponTier) / 2);
		}

		@Override
		public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti, int hitCount) {
			boolean wasAlive = enemy != null && enemy.isAlive();
			boolean result = super.attack(enemy, dmgMulti, dmgBonus, accMulti, hitCount);
			if (wasAlive && enemy != null && !enemy.isAlive() && enemy.alignment == Alignment.ENEMY) {
				if (weapon != null) weapon.refundCharge(1);
			}
			return result;
		}

		@Override
		public String name() {
			return Messages.get(SummonedMinion.class, "name", powerLevel, weaponTier);
		}

		@Override
		public String description() {
			return Messages.get(SummonedMinion.class, "desc");
		}

		@Override
		protected ActionSubmission proposeAction() {
			if (Dungeon.hero == null || !Dungeon.hero.isAlive()) {
				die(null);
				return ActionSubmission.idle();
			}

			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
				fieldOfView = new boolean[Dungeon.level.length()];
			}

			Dungeon.level.updateFieldOfView(this, fieldOfView);

			if (enemy == null || !enemy.isAlive() || enemy == this) {
				enemy = findTargetInFOV();
				if (enemy != null) {
					enemySeen = true;
					idleTurns = 0;
				} else {
					idleTurns++;
					if (idleTurns >= MAX_IDLE_TURNS) {
						die(null);
						return ActionSubmission.idle();
					}
					state = PASSIVE;
				}
			} else {
				idleTurns = 0;
			}

			return super.proposeAction();
		}

		private Char findTargetInFOV() {
			Char closest = null;
			int closestDist = Integer.MAX_VALUE;

			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.alignment == Alignment.ENEMY && mob.isAlive()
						&& fieldOfView[mob.pos] && mob.invisible <= 0) {
					int dist = Dungeon.level.distance(pos, mob.pos);
					if (dist < closestDist) {
						closest = mob;
						closestDist = dist;
					}
				}
			}

			return closest;
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
		}

		/** minimal 的 Mob.enemy/enemySeen/target 均为 protected，经此方法由外层武器类设置。 */
		public void setEnemy(Char e) {
			enemy = e;
			enemySeen = true;
		}

		public void setTarget(int cell) {
			target = cell;
		}

		@Override
		public Item createLoot() {
			return null;
		}
	}
}
