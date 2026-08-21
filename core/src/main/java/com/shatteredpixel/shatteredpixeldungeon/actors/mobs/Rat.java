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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The complete ordinary-enemy family for the five-level showcase.
 *
 * Every variant deliberately shares one runtime class and one sprite. Stable variant IDs,
 * floor caps and variant-only state live here so spawning and saves no longer depend on a
 * Java subclass name.
 */
public class Rat extends Mob {

	public enum Variant {
		BASIC(0, 6, 1f, 1f, 0, 0, 0, 0xFFFFFF),
		DODGER(1, 2, 0.80f, 0.90f, 1, 8, -1, 0x99FF99),
		SPLITTER(2, 2, 1.20f, 0.80f, 0, 0, 0, 0xCCFFCC),
		SHELL(3, 2, 1.30f, 0.85f, -1, -1, 4, 0xCC9966),
		DAMAGE_CAP(4, 2, 1.15f, 0.90f, 0, 0, 1, 0x99CC66),
		BLEEDER(5, 2, 0.95f, 1.05f, 1, 1, 0, 0xFFEEEE),
		CORROSIVE(6, 2, 1.05f, 0.90f, 0, 0, 1, 0x99FF00),
		DEATH_BURST(7, 1, 0.95f, 1.05f, 0, 0, 1, 0xEEEECC),
		THIEF(8, 1, 0.90f, 0.90f, 2, 3, 0, 0x666666),
		SHOCK_BOLT(9, 1, 0.90f, 1.05f, 2, 1, 0, 0x66CCFF),
		HOOK(10, 1, 1.10f, 0.90f, 0, 1, 2, 0xAA8866),
		NECROMANCER(11, 1, 0.95f, 0.80f, 1, 1, 0, 0xAA88CC),
		VAMPIRE(12, 1, 0.90f, 0.95f, 2, 2, 0, 0xCC3333),
		BERSERKER(13, 1, 1.25f, 1.10f, 1, -1, 2, 0xCC6600),
		HEX_CASTER(14, 1, 0.90f, 0.90f, 2, 1, 0, 0xAA66CC),
		WEB_KITER(15, 1, 0.95f, 0.85f, 2, 3, 0, 0xDDDD99),
		GAS_TURRET(16, 1, 1.15f, 0.80f, 1, -1, 3, 0x669966),
		REVIVER(17, 1, 1.15f, 0.90f, 0, 0, 1, 0x888888),
		ELEMENTAL(18, 1, 1.05f, 1.05f, 1, 1, 1, 0xFFAA44),
		COUNTER(19, 1, 1.10f, 0.90f, 1, 3, 2, 0xEEEE88),
		TELEPORTER(20, 1, 1.10f, 0.95f, 1, 0, 2, 0xAAAAFF),
		LEAPER(21, 1, 0.95f, 1.15f, 2, 1, 0, 0xCC5555),
		SPAWNER(22, 1, 1.30f, 0.75f, 0, -1, 3, 0x993333),
		CHARMER(23, 1, 0.95f, 0.90f, 2, 2, 0, 0xFF99CC),
		DEATH_RAY(24, 1, 1.10f, 1.20f, 2, 0, 1, 0xCC66FF),
		AMBUSH(25, 1, 1.00f, 1.15f, 1, 2, 1, 0xAA8855),
		WATER_HUNTER(26, 1, 1.00f, 1.10f, 2, 2, 0, 0x44AAFF),
		STENCH(27, 1, 1.15f, 0.85f, 0, 0, 1, 0x77AA55),
		TRICKSTER(28, 1, 0.90f, 1.00f, 3, 4, 0, 0xDD8844),
		CRYSTAL(29, 1, 1.10f, 1.05f, 2, 2, 2, 0x88EEFF),
		GEOMANCER(30, 1, 1.25f, 0.90f, 1, 0, 4, 0xAA8855),
		SENTRY(31, 1, 1.05f, 0.95f, 2, 4, 1, 0x66FFFF),
		BOMBER(32, 1, 1.00f, 0.90f, 1, 1, 1, 0xFF8844),
		CHARGER(33, 1, 1.25f, 1.10f, 1, 0, 3, 0xFFFF66),
		LINKED_SUMMONER(34, 1, 1.20f, 0.85f, 1, 1, 2, 0xCCAA88),
		FIST(35, 1, 1.35f, 1.15f, 1, -1, 3, 0xFF6666);

		public final int stableId;
		public final float weight;
		private final float healthFactor;
		private final float damageFactor;
		private final int attackBonus;
		private final int defenseBonus;
		private final int armorBonus;
		private final int tint;

		Variant(int stableId, float weight, float healthFactor, float damageFactor,
				int attackBonus, int defenseBonus, int armorBonus, int tint) {
			this.stableId = stableId;
			this.weight = weight;
			this.healthFactor = healthFactor;
			this.damageFactor = damageFactor;
			this.attackBonus = attackBonus;
			this.defenseBonus = defenseBonus;
			this.armorBonus = armorBonus;
			this.tint = tint;
		}

		private static Variant fromStableId(int stableId) {
			for (Variant variant : values()) {
				if (variant.stableId == stableId) return variant;
			}
			return BASIC;
		}
	}

	private static final Variant[] RANDOM_VARIANTS = Variant.values();
	private static final float[] RANDOM_WEIGHTS = new float[RANDOM_VARIANTS.length];
	static {
		for (int i = 0; i < RANDOM_VARIANTS.length; i++) {
			RANDOM_WEIGHTS[i] = RANDOM_VARIANTS[i].weight;
		}
	}

	private Variant variant = Variant.BASIC;
	private int damageMin;
	private int damageMax;
	private int attackSkill;
	private int armor;

	private int splitGeneration;
	private int specialCooldown;
	private int reviveTurns;
	private int linkedRatId = -1;
	private boolean counterReady;
	private boolean berserkTriggered;
	private boolean charging;
	private int chargeTarget = -1;
	private boolean ambushReady = true;
	private Item stolenItem;
	private boolean summonedAlly;
	// CHARMER keeps the broken honeypot relationship without reviving the Bee class.
	private int potPos = -1;
	private int potHolder = -1;

	{
		spriteClass = RatSprite.class;
		configure(Variant.BASIC, true);
	}

	public static Rat random() {
		int selected = Random.chances(RANDOM_WEIGHTS);
		return of(selected < 0 ? Variant.BASIC : RANDOM_VARIANTS[selected]);
	}

	public static Rat of(Variant variant) {
		Rat rat = new Rat();
		rat.configure(variant == null ? Variant.BASIC : variant, true);
		return rat;
	}

	/** Places a variant rat using the same delayed-spawn contract used by old summons. */
	public static Rat spawnAt(int pos, Variant variant) {
		if (Dungeon.level == null || pos < 0 || pos >= Dungeon.level.length()) return null;
		if (Dungeon.level.solid[pos] || Actor.findChar(pos) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS8) {
				int cell = pos + offset;
				if (cell >= 0 && cell < Dungeon.level.length()
						&& !Dungeon.level.solid[cell] && Actor.findChar(cell) == null) {
					candidates.add(cell);
				}
			}
			if (candidates.isEmpty()) return null;
			pos = Random.element(candidates);
		}
		Rat rat = of(variant);
		rat.pos = pos;
		rat.state = rat.HUNTING;
		GameScene.add(rat, 2f);
		Dungeon.level.occupyCell(rat);
		return rat;
	}

	public static void spawnAround(int pos, Variant variant) {
		for (int offset : PathFinder.NEIGHBOURS4) spawnAt(pos + offset, variant);
	}

	public Variant variant() {
		return variant;
	}

	@Override
	public String description() {
		String description = super.description();
		if (variant == Variant.BASIC) return description;

		return description + "\n\n_" + Messages.get(this, "variant") + "_\n"
				+ Messages.get(this, "variant_" + variant.name().toLowerCase(Locale.ENGLISH));
	}

	private void configure(Variant newVariant, boolean resetHealth) {
		variant = newVariant;
		if (variant == Variant.CHARMER) {
			flying = true;
			viewDistance = 4;
			intelligentAlly = true;
			if (state == SLEEPING) state = WANDERING;
		} else {
			flying = false;
			intelligentAlly = false;
		}
		Stats stats = Stats.forFloor(Dungeon.depth);

		int cappedHealth = Math.min(stats.healthCap,
				Math.max(1, Math.round(stats.health * variant.healthFactor)));
		if (resetHealth) HP = HT = cappedHealth;

		damageMin = Math.min(stats.damageMinCap,
				Math.max(1, Math.round(stats.damageMin * variant.damageFactor)));
		damageMax = Math.min(stats.damageMaxCap,
				Math.max(damageMin, Math.round(stats.damageMax * variant.damageFactor)));
		attackSkill = Math.min(stats.attackCap, Math.max(1, stats.attack + variant.attackBonus));
		defenseSkill = Math.min(stats.defenseCap, Math.max(0, stats.defense + variant.defenseBonus));
		armor = Math.min(stats.armorCap, Math.max(0, stats.armor + variant.armorBonus));

		if (resetHealth) {
			specialCooldown = initialCooldown(variant);
			ambushReady = true;
		}
	}

	private static int initialCooldown(Variant variant) {
		switch (variant) {
			case NECROMANCER: return 8;
			case SPAWNER: return 10;
			case LINKED_SUMMONER: return 9;
			case STENCH: return 4;
			case CHARGER: return 5;
			default: return 0;
		}
	}

	@Override
	public CharSprite sprite() {
		CharSprite sprite = super.sprite();
		if (variant.tint != 0xFFFFFF) sprite.hardlight(variant.tint);
		return sprite;
	}

	@Override
	protected boolean act() {
		if (reviveTurns > 0) {
			reviveTurns--;
			if (reviveTurns == 0) {
				HP = Math.max(1, HT / 2);
				state = WANDERING;
			}
			spend(TICK);
			return true;
		}

		if (specialCooldown > 0) specialCooldown--;
		switch (variant) {
			case NECROMANCER:
				if (specialCooldown == 0 && spawnRat(Variant.BASIC, false)) specialCooldown = 10;
				break;
			case SPAWNER:
				if (specialCooldown == 0 && spawnRat(Variant.BASIC, false)) specialCooldown = 12;
				break;
			case LINKED_SUMMONER:
				if (specialCooldown == 0 && linkedPartner() == null
						&& spawnRat(Variant.BASIC, true)) specialCooldown = 12;
				break;
			case STENCH:
				if (specialCooldown == 0 && !Dungeon.level.water[pos]) {
					GameScene.add(Blob.seed(pos, 12, ToxicGas.class));
					specialCooldown = 5;
				}
				break;
			case CHARGER:
				if (specialCooldown == 0) {
					Buff.affect(this, Barrier.class).setShield(Stats.floor() + 2);
					specialCooldown = 6;
				}
				break;
			default:
				break;
		}

		if (alignment != Alignment.ALLY
				&& Dungeon.level.heroFOV[pos]
				&& Dungeon.hero.armorAbility instanceof Ratmogrify) {
			alignment = Alignment.NEUTRAL;
			if (enemy == Dungeon.hero) enemy = null;
			if (state == SLEEPING) state = WANDERING;
		}
		return super.act();
	}

	@Override
	public int damageRoll() {
		int min = damageMin;
		int max = damageMax;
		if (variant == Variant.BERSERKER && HP * 2 <= HT) {
			min++;
			max += Stats.floor();
		}
		return Random.NormalIntRange(min, max);
	}

	@Override
	public int attackSkill(Char target) {
		return attackSkill;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, armor);
	}

	@Override
	public float speed() {
		float speed = super.speed();
		if (variant == Variant.SHELL || variant == Variant.GAS_TURRET) speed *= 0.8f;
		if (variant == Variant.VAMPIRE || (variant == Variant.THIEF && stolenItem != null)) speed *= 1.2f;
		if (variant == Variant.WATER_HUNTER) speed *= Dungeon.level.water[pos] ? 1.35f : 0.6f;
		return speed;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		boolean ranged = isRangedVariant()
				&& new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		if (variant == Variant.WATER_HUNTER
				&& (!Dungeon.level.water[pos] || !Dungeon.level.water[enemy.pos])) ranged = false;
		return super.canAttack(enemy) || ranged;
	}

	private boolean isRangedVariant() {
		switch (variant) {
			case SHOCK_BOLT:
			case HOOK:
			case HEX_CASTER:
			case WEB_KITER:
			case GAS_TURRET:
			case LEAPER:
			case DEATH_RAY:
			case TRICKSTER:
			case CRYSTAL:
			case GEOMANCER:
			case SENTRY:
			case BOMBER:
			case CHARGER:
			case WATER_HUNTER:
				return true;
			default:
				return false;
		}
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (Dungeon.level.adjacent(pos, enemy.pos)
				|| !isRangedVariant()
				|| new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos != enemy.pos) {
			return super.doAttack(enemy);
		}

		if ((variant == Variant.DEATH_RAY || variant == Variant.LEAPER
				|| variant == Variant.CHARGER) && !charging) {
			charging = true;
			chargeTarget = enemy.pos;
			sprite.showStatus(CharSprite.WARNING, "!");
			spend(attackDelay());
			return true;
		}
		if (charging && chargeTarget != enemy.pos) {
			charging = false;
			chargeTarget = -1;
			return super.doAttack(enemy);
		}

		charging = false;
		chargeTarget = -1;
		Invisibility.dispel(this);
		spend(attackDelay());

		if (variant == Variant.GAS_TURRET) {
			GameScene.add(Blob.seed(enemy.pos, 18, ToxicGas.class));
			return true;
		}
		if (variant == Variant.BOMBER) {
			Buff.append(this, RatBomb.class).set(enemy.pos, Stats.floor() + 2);
			return true;
		}
		if (variant == Variant.HOOK) {
			pullEnemy(enemy);
		}

		if (hit(this, enemy, true)) {
			int damage = damageRoll();
			if (variant == Variant.SHOCK_BOLT) damage += Stats.floor();
			if (variant == Variant.DEATH_RAY) damage += Stats.floor() * 2;
			if (variant == Variant.CRYSTAL) damage += Stats.floor();
			Proc.damage(new DamageInfo()
					.offender(this).defender(enemy)
					.way(DamageWay.RANGED)
					.type(variant == Variant.SHOCK_BOLT ? DamageType.ELECTRIC : DamageType.PHYSICAL)
					.solidDamage(attackProc(enemy, damage)));
			if (variant == Variant.SHOCK_BOLT) shockNearby(enemy, damage);
			if (variant == Variant.LEAPER || variant == Variant.CHARGER
					|| variant == Variant.WATER_HUNTER) leapNear(enemy);
		} else if (enemy.sprite != null) {
			enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
		}
		return true;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		switch (variant) {
			case BLEEDER:
				Buff.affect(enemy, Bleeding.class).set(Math.max(1, damage / 2f));
				break;
			case CORROSIVE:
				Buff.affect(enemy, Ooze.class).set(Ooze.DURATION);
				break;
			case SHOCK_BOLT:
				Buff.prolong(enemy, Vulnerable.class, Vulnerable.DURATION / 2f);
				break;
			case THIEF:
				if (alignment == Alignment.ENEMY && stolenItem == null
						&& enemy instanceof Hero && steal((Hero) enemy)) state = FLEEING;
				break;
			case VAMPIRE:
				int healing = Math.min(damage, Stats.floor() + 2);
				HP = Math.min(HT, HP + healing);
				break;
			case HEX_CASTER:
				applyRandomHex(enemy);
				break;
			case WEB_KITER:
				Buff.prolong(enemy, Cripple.class, Cripple.DURATION / 2f);
				state = FLEEING;
				break;
			case ELEMENTAL:
			case FIST:
				applyElement(enemy);
				break;
			case COUNTER:
				if (counterReady) {
					damage += Stats.floor() + 1;
					counterReady = false;
				}
				break;
			case TELEPORTER:
				ScrollOfTeleportation.teleportChar(this);
				break;
			case CHARMER:
				Charm charm = Buff.affect(enemy, Charm.class, Charm.DURATION / 2f);
				charm.object = id();
				HP = Math.min(HT, HP + Math.max(1, Stats.floor() / 2));
				ScrollOfTeleportation.teleportChar(this);
				break;
			case LEAPER:
				Buff.affect(enemy, Bleeding.class).set(Math.max(1, damage / 3f));
				break;
			case AMBUSH:
				if (ambushReady) {
					damage += damage / 2;
					ambushReady = false;
				}
				break;
			case WATER_HUNTER:
				if (Dungeon.level.water[enemy.pos]) damage += Math.max(1, damage / 2);
				break;
			case STENCH:
				if (!Dungeon.level.water[enemy.pos]) {
					Buff.affect(enemy, Ooze.class).set(Ooze.DURATION / 2f);
				}
				break;
			case TRICKSTER:
				applyRandomHex(enemy);
				state = FLEEING;
				break;
			case CRYSTAL:
				Buff.affect(this, Barrier.class).setShield(Stats.floor() + 1);
				break;
			case GEOMANCER:
				Buff.affect(this, Barrier.class).setShield(Stats.floor() + 1);
				Buff.prolong(enemy, Cripple.class, Cripple.DURATION / 2f);
				break;
			case CHARGER:
				Buff.affect(this, Barrier.class).setShield(Stats.floor() + 2);
				Buff.prolong(enemy, Cripple.class, Cripple.DURATION / 3f);
				break;
			case SENTRY:
				Buff.prolong(enemy, Blindness.class, Blindness.DURATION / 2f);
				break;
			default:
				break;
		}
		return damage;
	}

	@Override
	public boolean add(Buff buff) {
		if (super.add(buff)) {
			if (variant == Variant.CHARMER && buff instanceof AllyBuff) {
				intelligentAlly = false;
				setPotInfo(-1, null);
			}
			return true;
		}
		return false;
	}

	@Override
	protected Char chooseEnemy() {
		if (variant != Variant.CHARMER || alignment == Alignment.ALLY
				|| (potHolder == -1 && potPos == -1)) {
			return super.chooseEnemy();
		}

		Actor holder = potHolder == -1 ? null : Actor.findById(potHolder);
		if (holder instanceof Char) return (Char) holder;

		// A charmer still respects aggression effects, but only near its pot.
		if ((alignment == Alignment.ENEMY || buff(Amok.class) != null)
				&& state != PASSIVE && state != SLEEPING) {
			if (enemy != null && enemy.buff(StoneOfAggression.Aggression.class) != null
					&& Dungeon.level.distance(enemy.pos, potPos) <= 3) {
				state = HUNTING;
				return enemy;
			}
			for (Char character : Actor.chars()) {
				if (character != this && fieldOfView[character.pos]
						&& Dungeon.level.distance(character.pos, potPos) <= 3
						&& character.buff(StoneOfAggression.Aggression.class) != null) {
					state = HUNTING;
					return character;
				}
			}
		}

		boolean currentTargetInvalid = enemy == null || !enemy.isAlive()
				|| !Actor.chars().contains(enemy) || state == WANDERING
				|| Dungeon.level.distance(enemy.pos, potPos) > 3
				|| (alignment == Alignment.ALLY && enemy.alignment == Alignment.ALLY)
				|| (buff(Amok.class) == null && enemy.isInvulnerable(getClass()));
		if (!currentTargetInvalid) return enemy;

		Mob closest = null;
		for (Mob mob : Dungeon.level.mobs) {
			if (mob != this && Dungeon.level.distance(mob.pos, potPos) <= 3
					&& mob.alignment != Alignment.NEUTRAL
					&& !mob.isInvulnerable(getClass())
					&& !(alignment == Alignment.ALLY && mob.alignment == Alignment.ALLY)
					&& (closest == null || Dungeon.level.distance(closest.pos, pos)
							> Dungeon.level.distance(mob.pos, pos))) {
				closest = mob;
			}
		}
		if (closest != null) return closest;
		return alignment != Alignment.ALLY && Dungeon.hero != null
				&& Dungeon.level.distance(Dungeon.hero.pos, potPos) <= 3 ? Dungeon.hero : null;
	}

	@Override
	protected boolean getCloser(int target) {
		if (variant != Variant.CHARMER) return super.getCloser(target);
		if (alignment == Alignment.ALLY && enemy == null && buffs(AllyBuff.class).isEmpty()) {
			target = Dungeon.hero.pos;
		} else if (enemy != null && Actor.findById(potHolder) == enemy) {
			target = enemy.pos;
		} else if (potPos != -1 && (state == WANDERING || Dungeon.level.distance(target, potPos) > 3)) {
			if (!Dungeon.level.insideMap(potPos)) potPos = -1;
			else target = this.target = potPos;
		}
		return super.getCloser(target);
	}

	public void setPotInfo(int potPos, Char potHolder) {
		if (variant != Variant.CHARMER) return;
		this.potPos = potPos;
		this.potHolder = potHolder == null ? -1 : potHolder.id();
	}

	public int potPos() {
		return potPos;
	}

	public int potHolderID() {
		return potHolder;
	}

	/** Applies the bounded combat profile used by summoned elemental rats. */
	public void setSummonedALly() {
		summonedAlly = true;
		int scale = Math.max(2, 1 + Dungeon.scalingDepth() / 5);
		Stats stats = Stats.forFloor(Dungeon.depth);
		HT = Math.min(stats.healthCap, Math.max(HT, 15 * scale));
		HP = HT;
		defenseSkill = Math.min(stats.defenseCap, Math.max(defenseSkill, 5 * scale));
		damageMin = Math.min(stats.damageMinCap, Math.max(damageMin, 5 * scale));
		damageMax = Math.min(stats.damageMaxCap, Math.max(damageMax, 5 + 5 * scale));
		attackSkill = Math.min(stats.attackCap, Math.max(attackSkill, 5 + 5 * scale));
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (variant == Variant.COUNTER && !counterReady) {
			counterReady = true;
			damage = Math.max(0, damage / 2);
		}
		if (variant == Variant.SPLITTER && splitGeneration < 2 && HP >= damage + 2) {
			split(damage);
		}
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int damage, Object source) {
		if (variant == Variant.DAMAGE_CAP) {
			damage = Math.min(damage, Stats.floor() + 2);
		}

		Rat linked = linkedPartner();
		if (linked != null && !(source instanceof LinkedDamage) && damage > 1) {
			int shared = damage / 2;
			damage -= shared;
			linked.damage(shared, new LinkedDamage());
		}

		if (variant == Variant.REVIVER && reviveTurns == 0 && damage >= HP && hasNearbyRat()) {
			damage = Math.max(0, HP - 1);
			reviveTurns = 3;
			state = SLEEPING;
		}

		super.damage(damage, source);
		if (variant == Variant.BERSERKER && isAlive() && !berserkTriggered && HP * 2 <= HT) {
			berserkTriggered = true;
			Buff.affect(this, Barrier.class).setShield(Stats.floor() + 3);
		}
	}

	@Override
	public void die(Object cause) {
		Variant dyingVariant = variant;
		int deathPos = pos;
		super.die(cause);
		if (dyingVariant == Variant.DEATH_BURST) {
			//延迟 0.1 回合爆炸：死亡结算完成后再对锚点 3x3 造成伤害（defer 用例）
			Proc.defer(new DamageInfo()
					.cell(deathPos).radius(1)
					.way(DamageWay.ENV).type(DamageType.PHYSICAL)
					.solidDamage(Random.NormalIntRange(1, Stats.floor() + 3)),
					0.1f);
		} else if (dyingVariant == Variant.STENCH && !Dungeon.level.water[deathPos]) {
			GameScene.add(Blob.seed(deathPos, 20, ToxicGas.class));
		}
	}

	@Override
	public void rollToDropLoot() {
		if (stolenItem != null) {
			Dungeon.level.drop(stolenItem, pos).sprite.drop();
			stolenItem = null;
		}
		super.rollToDropLoot();
	}

	private boolean steal(Hero hero) {
		Item candidate = hero.belongings.randomUnequipped();
		if (candidate == null || candidate.unique || candidate.level() > 0) return false;
		if (!candidate.stackable) Dungeon.quickslot.convertToPlaceholder(candidate);
		Item.updateQuickslot();
		stolenItem = candidate.detach(hero.belongings.backpack);
		return stolenItem != null;
	}

	private void applyRandomHex(Char enemy) {
		switch (Random.Int(3)) {
			case 0:
				Buff.prolong(enemy, Weakness.class, Weakness.DURATION / 2f);
				break;
			case 1:
				Buff.prolong(enemy, Vulnerable.class, Vulnerable.DURATION / 2f);
				break;
			default:
				Buff.prolong(enemy, Blindness.class, Blindness.DURATION / 2f);
				break;
		}
	}

	private void applyElement(Char enemy) {
		if (Dungeon.level.water[enemy.pos]) {
			Buff.prolong(enemy, Chill.class, Chill.DURATION);
			return;
		}
		switch (Random.Int(3)) {
			case 0:
				Buff.affect(enemy, Burning.class).reignite(enemy);
				break;
			case 1:
				Buff.prolong(enemy, Chill.class, Chill.DURATION / 2f);
				break;
			default:
				Buff.prolong(enemy, Vulnerable.class, Vulnerable.DURATION / 2f);
				break;
		}
	}

	private void shockNearby(Char firstTarget, int damage) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char chained = Actor.findChar(firstTarget.pos + offset);
			if (chained != null && chained != this && chained != firstTarget
					&& chained.isAlive() && chained.alignment != alignment) {
				Proc.damage(new DamageInfo()
						.offender(this).defender(chained)
						.way(DamageWay.RANGED).type(DamageType.ELECTRIC)
						.solidDamage(Math.max(1, damage / 3)));
				break;
			}
		}
	}

	private void leapNear(Char target) {
		if (rooted) return;
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = target.pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) {
				cells.add(cell);
			}
		}
		if (cells.isEmpty()) return;
		int oldPos = pos;
		pos = Random.element(cells);
		Actor.add(new Pushing(this, oldPos, pos));
		Dungeon.level.occupyCell(this);
	}

	private void pullEnemy(Char enemy) {
		if (enemy.properties().contains(Property.IMMOVABLE)) return;
		Ballistica chain = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
		for (int cell : chain.subPath(1, chain.dist)) {
			if (!Dungeon.level.solid[cell] && Actor.findChar(cell) == null
					&& (Dungeon.level.openSpace[cell] || !Char.hasProp(enemy, Property.LARGE))) {
				enemy.pos = cell;
				if (enemy.sprite != null) enemy.sprite.place(cell);
				Dungeon.level.occupyCell(enemy);
				Buff.prolong(enemy, Cripple.class, 4f);
				if (enemy == Dungeon.hero) {
					Dungeon.hero.interrupt();
					Dungeon.observe();
				}
				return;
			}
		}
	}

	private void split(int incomingDamage) {
		ArrayList<Integer> cells = openNeighbourCells();
		if (cells.isEmpty()) return;
		Rat child = of(Variant.SPLITTER);
		child.splitGeneration = splitGeneration + 1;
		child.alignment = alignment;
		child.pos = Random.element(cells);
		child.state = child.HUNTING;
		GameScene.add(child, 1f);
		child.HP = Math.max(1, (HP - incomingDamage) / 2);
		HP -= child.HP;
		Actor.add(new Pushing(child, pos, child.pos));
		Dungeon.level.occupyCell(child);
	}

	private boolean spawnRat(Variant childVariant, boolean link) {
		ArrayList<Integer> cells = openNeighbourCells();
		if (cells.isEmpty()) return false;
		Rat child = of(childVariant);
		child.alignment = alignment;
		child.pos = Random.element(cells);
		child.state = child.HUNTING;
		if (link) {
			linkedRatId = child.id();
			child.linkedRatId = id();
		}
		GameScene.add(child, 1f);
		Dungeon.level.occupyCell(child);
		return true;
	}

	private ArrayList<Integer> openNeighbourCells() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) {
				result.add(cell);
			}
		}
		return result;
	}

	private boolean hasNearbyRat() {
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob != this && mob instanceof Rat && mob.alignment == alignment
					&& mob.isAlive() && Dungeon.level.distance(pos, mob.pos) <= 3) return true;
		}
		return false;
	}

	private Rat linkedPartner() {
		if (linkedRatId == -1) return null;
		Actor actor = Actor.findById(linkedRatId);
		if (actor instanceof Rat && ((Rat) actor).isAlive()) return (Rat) actor;
		linkedRatId = -1;
		return null;
	}

	private static final String VARIANT = "rat_variant";
	private static final String SPLIT_GENERATION = "split_generation";
	private static final String SPECIAL_COOLDOWN = "special_cooldown";
	private static final String REVIVE_TURNS = "revive_turns";
	private static final String LINKED_RAT = "linked_rat";
	private static final String COUNTER_READY = "counter_ready";
	private static final String BERSERK_TRIGGERED = "berserk_triggered";
	private static final String CHARGING = "charging";
	private static final String CHARGE_TARGET = "charge_target";
	private static final String AMBUSH_READY = "ambush_ready";
	private static final String STOLEN_ITEM = "stolen_item";
	private static final String SUMMONED_ALLY = "summoned_ally";
	private static final String RAT_ALLY = "rat_ally";
	private static final String POT_POS = "pot_pos";
	private static final String POT_HOLDER = "pot_holder";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VARIANT, variant.stableId);
		bundle.put(SPLIT_GENERATION, splitGeneration);
		bundle.put(SPECIAL_COOLDOWN, specialCooldown);
		bundle.put(REVIVE_TURNS, reviveTurns);
		bundle.put(LINKED_RAT, linkedRatId);
		bundle.put(COUNTER_READY, counterReady);
		bundle.put(BERSERK_TRIGGERED, berserkTriggered);
		bundle.put(CHARGING, charging);
		bundle.put(CHARGE_TARGET, chargeTarget);
		bundle.put(AMBUSH_READY, ambushReady);
		bundle.put(STOLEN_ITEM, stolenItem);
		bundle.put(SUMMONED_ALLY, summonedAlly);
		if (variant == Variant.CHARMER) {
			bundle.put(POT_POS, potPos);
			bundle.put(POT_HOLDER, potHolder);
		}
		if (alignment == Alignment.ALLY) bundle.put(RAT_ALLY, true);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		configure(Variant.fromStableId(bundle.getInt(VARIANT)), false);
		splitGeneration = bundle.getInt(SPLIT_GENERATION);
		specialCooldown = bundle.getInt(SPECIAL_COOLDOWN);
		reviveTurns = bundle.getInt(REVIVE_TURNS);
		linkedRatId = bundle.contains(LINKED_RAT) ? bundle.getInt(LINKED_RAT) : -1;
		counterReady = bundle.getBoolean(COUNTER_READY);
		berserkTriggered = bundle.getBoolean(BERSERK_TRIGGERED);
		charging = bundle.getBoolean(CHARGING);
		chargeTarget = bundle.contains(CHARGE_TARGET) ? bundle.getInt(CHARGE_TARGET) : -1;
		ambushReady = !bundle.contains(AMBUSH_READY) || bundle.getBoolean(AMBUSH_READY);
		stolenItem = (Item) bundle.get(STOLEN_ITEM);
		if (bundle.getBoolean(SUMMONED_ALLY)) setSummonedALly();
		potPos = bundle.contains(POT_POS) ? bundle.getInt(POT_POS) : -1;
		potHolder = bundle.contains(POT_HOLDER) ? bundle.getInt(POT_HOLDER) : -1;
		if (bundle.contains(RAT_ALLY)) alignment = Alignment.ALLY;
	}

	public static class RatBomb extends Buff {

		private int cell;
		private int damage;
		private int turns = 2;

		public RatBomb set(int cell, int damage) {
			this.cell = cell;
			this.damage = damage;
			return this;
		}

		@Override
		public boolean act() {
			if (--turns > 0) {
				spend(TICK);
				return true;
			}
			for (int offset : PathFinder.NEIGHBOURS9) {
				Char target = Actor.findChar(cell + offset);
				if (target != null && target.isAlive()) {
					Proc.damage(new DamageInfo()
						.defender(target)
						.way(DamageWay.ENV).type(DamageType.PHYSICAL)
						.solidDamage(Random.NormalIntRange(damage, damage * 2)));
				}
			}
			detach();
			return true;
		}

		private static final String CELL = "cell";
		private static final String DAMAGE = "damage";
		private static final String TURNS = "turns";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CELL, cell);
			bundle.put(DAMAGE, damage);
			bundle.put(TURNS, turns);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			cell = bundle.getInt(CELL);
			damage = bundle.getInt(DAMAGE);
			turns = bundle.getInt(TURNS);
		}
	}

	public static class RangedAttack {
		public final int variantId;

		public RangedAttack(int variantId) {
			this.variantId = variantId;
		}
	}

	public static class DeathBurst { }
	public static class BombDamage { }
	private static class LinkedDamage { }

	private static class Stats {
		private static final Stats[] FLOORS = {
				new Stats(8, 1, 4, 8, 2, 1, 12, 3, 6, 12, 10, 4),
				new Stats(12, 2, 5, 10, 4, 2, 18, 4, 8, 14, 12, 5),
				new Stats(18, 3, 7, 12, 6, 3, 27, 5, 10, 17, 15, 7),
				new Stats(26, 4, 9, 15, 8, 4, 38, 7, 13, 20, 18, 9),
				new Stats(36, 5, 12, 18, 10, 5, 52, 9, 17, 23, 21, 11)
		};

		final int health;
		final int damageMin;
		final int damageMax;
		final int attack;
		final int defense;
		final int armor;
		final int healthCap;
		final int damageMinCap;
		final int damageMaxCap;
		final int attackCap;
		final int defenseCap;
		final int armorCap;

		Stats(int health, int damageMin, int damageMax, int attack, int defense, int armor,
				int healthCap, int damageMinCap, int damageMaxCap, int attackCap,
				int defenseCap, int armorCap) {
			this.health = health;
			this.damageMin = damageMin;
			this.damageMax = damageMax;
			this.attack = attack;
			this.defense = defense;
			this.armor = armor;
			this.healthCap = healthCap;
			this.damageMinCap = damageMinCap;
			this.damageMaxCap = damageMaxCap;
			this.attackCap = attackCap;
			this.defenseCap = defenseCap;
			this.armorCap = armorCap;
		}

		static int floor() {
			return Math.max(1, Math.min(5, Dungeon.depth));
		}

		static Stats forFloor(int depth) {
			if (Dungeon.mode == Dungeon.Mode.INFINITE) {
				int cappedDepth = Math.max(1, Math.min(30, depth));
				float progress = (cappedDepth - 1) / 29f;
				return interpolate(FLOORS[0], FLOORS[4], progress);
			}
			return FLOORS[Math.max(1, Math.min(5, depth)) - 1];
		}

		private static Stats interpolate(Stats first, Stats last, float progress) {
			return new Stats(
					lerp(first.health, last.health, progress),
					lerp(first.damageMin, last.damageMin, progress),
					lerp(first.damageMax, last.damageMax, progress),
					lerp(first.attack, last.attack, progress),
					lerp(first.defense, last.defense, progress),
					lerp(first.armor, last.armor, progress),
					lerp(first.healthCap, last.healthCap, progress),
					lerp(first.damageMinCap, last.damageMinCap, progress),
					lerp(first.damageMaxCap, last.damageMaxCap, progress),
					lerp(first.attackCap, last.attackCap, progress),
					lerp(first.defenseCap, last.defenseCap, progress),
					lerp(first.armorCap, last.armorCap, progress));
		}

		private static int lerp(int start, int end, float progress) {
			return Math.round(start + (end - start) * progress);
		}
	}
}
