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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cleansing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** A dart with one of the seed-derived effects. The effect is data, not a subtype. */
public class TippedDart extends Dart {

	public enum Effect {
		ROT(Rotberry.Seed.class, ItemSpriteSheet.ROT_DART, "rotdart"),
		HEALING(Sungrass.Seed.class, ItemSpriteSheet.HEALING_DART, "healingdart"),
		DISPLACING(Fadeleaf.Seed.class, ItemSpriteSheet.DISPLACING_DART, "displacingdart"),
		CHILLING(Icecap.Seed.class, ItemSpriteSheet.CHILLING_DART, "chillingdart"),
		INCENDIARY(Firebloom.Seed.class, ItemSpriteSheet.INCENDIARY_DART, "incendiarydart"),
		POISON(Sorrowmoss.Seed.class, ItemSpriteSheet.POISON_DART, "poisondart"),
		ADRENALINE(Swiftthistle.Seed.class, ItemSpriteSheet.ADRENALINE_DART, "adrenalinedart"),
		BLINDING(Blindweed.Seed.class, ItemSpriteSheet.BLINDING_DART, "blindingdart"),
		SHOCKING(Stormvine.Seed.class, ItemSpriteSheet.SHOCKING_DART, "shockingdart"),
		PARALYTIC(Earthroot.Seed.class, ItemSpriteSheet.PARALYTIC_DART, "paralyticdart"),
		CLEANSING(Mageroyal.Seed.class, ItemSpriteSheet.CLEANSING_DART, "cleansingdart"),
		HOLY(Starflower.Seed.class, ItemSpriteSheet.HOLY_DART, "holydart");

		private final Class<? extends Plant.Seed> seed;
		private final int image;
		private final String key;

		Effect(Class<? extends Plant.Seed> seed, int image, String key) {
			this.seed = seed;
			this.image = image;
			this.key = key;
		}

		private TippedDart create(int quantity) {
			return (TippedDart) new TippedDart(this).quantity(quantity);
		}
	}

	public static final class HolyDamage {
		public static final HolyDamage INSTANCE = new HolyDamage();
		private HolyDamage() {}
	}

	private static final String EFFECT = "effect";
	private static final String AC_CLEAN = "CLEAN";

	private Effect effect;

	{
		tier = 1;
		baseUses = 1f;
		useRoundingInDurabilityCalc = false;
	}

	public TippedDart() {
		this(Effect.ADRENALINE);
	}

	public TippedDart(Effect effect) {
		setEffect(effect);
	}

	public Effect effect() {
		return effect;
	}

	public TippedDart setEffect(Effect effect) {
		this.effect = effect == null ? Effect.ADRENALINE : effect;
		image = this.effect.image;
		usesTargeting = this.effect != Effect.HEALING;
		return this;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_TIP);
		actions.add(AC_CLEAN);
		return actions;
	}

	@Override
	public void execute(final Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_CLEAN)) {
			String[] options;
			if (quantity() > 1) {
				options = new String[]{Messages.get(this, "clean_all"), Messages.get(this, "clean_one"), Messages.get(this, "cancel")};
			} else {
				options = new String[]{Messages.get(this, "clean_one"), Messages.get(this, "cancel")};
			}

			GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(name()),
					Messages.get(this, "clean_desc"), options) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						detachAll(hero.belongings.backpack);
						new Dart().quantity(quantity()).collect();
						hero.spend(1f);
						hero.busy();
						hero.sprite.operate(hero.pos);
					} else if (index == 1 && quantity() > 1) {
						detach(hero.belongings.backpack);
						if (!new Dart().quantity(1).collect()) Dungeon.level.drop(new Dart().quantity(1), hero.pos).sprite.drop();
						durability = MAX_DURABILITY;
						hero.spend(1f);
						hero.busy();
						hero.sprite.operate(hero.pos);
					}
				}
			});
		}
	}

	@Override
	protected void onThrow(int cell) {
		if (effect == Effect.INCENDIARY) {
			Char enemy = Actor.findChar(cell);
			if ((enemy == null || enemy == curUser) && Dungeon.level.flamable[cell]) {
				GameScene.add(Blob.seed(cell, 4, Fire.class));
				decrementDurability();
				if (durability > 0 || spawnedForEffect) {
					super.onThrow(cell);
				} else {
					Dungeon.level.drop(new Dart().quantity(1), cell).sprite.drop();
				}
			} else {
				super.onThrow(cell);
			}
			return;
		}
		super.onThrow(cell);
	}

	@Override
	protected void rangedHit(Char enemy, int cell) {
		targetPos = cell;
		super.rangedHit(enemy, cell);
		if (durability <= 0 && !spawnedForEffect) {
			Dart d = new Dart();
			d.quantity(1);
			Catalog.countUse(getClass());
			if (sticky && enemy != null && enemy.isAlive() && enemy.alignment != Char.Alignment.ALLY) {
				PinCushion p = Buff.affect(enemy, PinCushion.class);
				if (p.target == enemy) {
					p.stick(d);
					return;
				}
			}
			Dungeon.level.drop(d, enemy.pos).sprite.drop();
		}
	}

	@Override
	public Item merge(Item other) {
		int total = quantity() + other.quantity();
		super.merge(other);
		int extra = total - quantity();
		if (extra > 0) lostDarts += extra;
		return this;
	}

	@Override
	public int damageRoll(Char owner) {
		if (harmlessToAllies() && owner instanceof Hero) {
			Char target = ((Hero) owner).attackTarget();
			if (target != null && target.alignment == owner.alignment) return 0;
		}
		return super.damageRoll(owner);
	}

	private boolean harmlessToAllies() {
		return effect == Effect.ADRENALINE || effect == Effect.CLEANSING
				|| effect == Effect.HEALING || effect == Effect.HOLY;
	}

	@Override
	public int proc(Char attacker, final Char defender, int damage) {
		switch (effect) {
			case ADRENALINE:
				if (processingChargedShot && defender == attacker) {
					break;
				} else if (attacker.alignment == defender.alignment) {
					Buff.prolong(defender, Adrenaline.class, Adrenaline.DURATION);
				} else {
					Buff.prolong(defender, Cripple.class, Cripple.DURATION / 2);
				}
				break;
			case BLINDING:
				if (!processingChargedShot || attacker.alignment != defender.alignment) {
					Buff.affect(defender, Blindness.class, Blindness.DURATION);
				}
				break;
			case CHILLING:
				if (!processingChargedShot || attacker.alignment != defender.alignment) {
					Buff.prolong(defender, Chill.class, Dungeon.level.water[defender.pos] ? Chill.DURATION : 6f);
				}
				break;
			case PARALYTIC:
				if (!processingChargedShot || attacker.alignment != defender.alignment) {
					Buff.prolong(defender, Paralysis.class, 5f);
				}
				break;
			case POISON:
				if (!processingChargedShot || attacker.alignment != defender.alignment) {
					Buff.affect(defender, Poison.class).set(3 + Dungeon.scalingDepth() / 2);
				}
				break;
			case ROT:
				if (!(processingChargedShot && attacker.alignment == defender.alignment)) {
					if (defender.properties().contains(Char.Property.BOSS) || defender.properties().contains(Char.Property.MINIBOSS)) {
						Buff.affect(defender, Corrosion.class).set(5f, Dungeon.scalingDepth() / 3);
					} else {
						Buff.affect(defender, Corrosion.class).set(10f, Dungeon.scalingDepth());
					}
				}
				break;
			case CLEANSING:
				if (processingChargedShot && defender == attacker) {
					break;
				} else if (attacker.alignment == defender.alignment) {
					Cleansing.cleanse(defender, Cleansing.DURATION * 2f);
				} else {
					for (Buff b : defender.buffs()) {
						if (!(b instanceof ChampionEnemy) && b.type == Buff.buffType.POSITIVE && !(b instanceof Crossbow.ChargedShot)) b.detach();
					}
					if (!defender.isAlive()) {
						defender.die(attacker);
						return super.proc(attacker, defender, damage);
					}
					if (defender instanceof Mob) {
						new FlavourBuff() {
							{actPriority = VFX_PRIO;}
							public boolean act() {
								if (((Mob) defender).state == ((Mob) defender).HUNTING || ((Mob) defender).state == ((Mob) defender).FLEEING) {
									((Mob) defender).state = ((Mob) defender).WANDERING;
								}
								((Mob) defender).beckon(Dungeon.level.randomDestination(defender));
								defender.sprite.showLost();
								return super.act();
							}
						}.attachTo(defender);
					}
				}
				break;
			case DISPLACING:
				if (!processingChargedShot || attacker.alignment != defender.alignment) displace(attacker, defender);
				break;
			case HEALING:
				if (!(processingChargedShot && (defender == attacker || attacker.alignment != defender.alignment))) {
					PotionOfHealing.cure(defender);
					Buff.affect(defender, Healing.class).setHeal((int) (0.5f * defender.HT + 30), 0.25f, 0);
				}
				break;
			case HOLY:
				if (processingChargedShot && defender == attacker) break;
				if (attacker.alignment == defender.alignment) Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
				if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)) {
					defender.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + buffedLvl());
					Sample.INSTANCE.play(com.shatteredpixel.shatteredpixeldungeon.Assets.Sounds.BURNING);
					Proc.damage(new DamageInfo().offender(attacker).defender(defender).way(DamageWay.RANGED).type(DamageType.PHYSICAL).solidDamage(Random.NormalIntRange(10 + Dungeon.scalingDepth() / 3, 20 + Dungeon.scalingDepth() / 3)));
				} else if (!processingChargedShot) {
					Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
				}
				break;
			case INCENDIARY:
				if (!processingChargedShot || attacker.alignment != defender.alignment) Buff.affect(defender, Burning.class).reignite(defender);
				break;
			case SHOCKING:
				if (!processingChargedShot || attacker.alignment != defender.alignment) shock(attacker, defender);
				break;
		}
		return super.proc(attacker, defender, damage);
	}

	private void displace(Char attacker, Char defender) {
		if (defender.properties().contains(Char.Property.IMMOVABLE)) return;
		ArrayList<Integer> visiblePositions = new ArrayList<>();
		ArrayList<Integer> nonVisiblePositions = new ArrayList<>();
		PathFinder.buildDistanceMap(attacker.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
		for (int pos = 0; pos < Dungeon.level.length(); pos++) {
			if (Dungeon.level.passable[pos] && PathFinder.distance[pos] >= 8 && PathFinder.distance[pos] <= 10
					&& (!Char.hasProp(defender, Char.Property.LARGE) || Dungeon.level.openSpace[pos]) && Actor.findChar(pos) == null) {
				if (Dungeon.level.heroFOV[pos]) visiblePositions.add(pos); else nonVisiblePositions.add(pos);
			}
		}
		int chosenPos = -1;
		ArrayList<Integer> candidates = visiblePositions.isEmpty() ? nonVisiblePositions : visiblePositions;
		for (int pos : candidates) {
			if (chosenPos == -1 || Dungeon.level.trueDistance(defender.pos, chosenPos) > Dungeon.level.trueDistance(defender.pos, pos)) chosenPos = pos;
		}
		if (chosenPos != -1) {
			ScrollOfTeleportation.appear(defender, chosenPos);
			Dungeon.level.occupyCell(defender);
			if (defender == Dungeon.hero) {
				Dungeon.observe();
				GameScene.updateFog();
			} else if (!Dungeon.level.heroFOV[chosenPos]) {
				Buff.append(attacker, TalismanOfForesight.CharAwareness.class, 5f).charID = defender.id();
			}
		}
	}

	private void shock(Char attacker, Char defender) {
		Proc.damage(new DamageInfo().offender(attacker).defender(defender).way(DamageWay.RANGED).type(DamageType.ELECTRIC).solidDamage(Random.NormalIntRange(5 + Dungeon.scalingDepth() / 4, 10 + Dungeon.scalingDepth() / 4)));
		CharSprite s = defender.sprite;
		if (s != null && s.parent != null) {
			ArrayList<Lightning.Arc> arcs = new ArrayList<>();
			arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2), new PointF(s.x + s.width, s.y + s.height / 2)));
			arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y), new PointF(s.x + s.width / 2, s.y + s.height)));
			s.parent.add(new Lightning(arcs, null));
			Sample.INSTANCE.play(com.shatteredpixel.shatteredpixeldungeon.Assets.Sounds.LIGHTNING);
		}
	}

	@Override
	public float durabilityPerUse(int level) {
		if (effect == Effect.ROT) return MAX_DURABILITY / 5f;
		float use = super.durabilityPerUse(level);
		if (Dungeon.hero != null) {
			use /= (1 + Dungeon.hero.pointsInTalent(Talent.DURABLE_TIPS));
			float lotusPreserve = 0f;
			if (targetPos != -1) {
				for (Char ch : Actor.chars()) if (ch instanceof WandOfRegrowth.Lotus && ((WandOfRegrowth.Lotus) ch).inRange(targetPos)) lotusPreserve = Math.max(lotusPreserve, ((WandOfRegrowth.Lotus) ch).seedPreservation());
				targetPos = -1;
			}
			int p = curUser == null ? Dungeon.hero.pos : curUser.pos;
			for (Char ch : Actor.chars()) if (ch instanceof WandOfRegrowth.Lotus && ((WandOfRegrowth.Lotus) ch).inRange(p)) lotusPreserve = Math.max(lotusPreserve, ((WandOfRegrowth.Lotus) ch).seedPreservation());
			use *= (1f - lotusPreserve);
		}
		float usages = Math.round(MAX_DURABILITY / use);
		if (bow != null && Dungeon.hero != null && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) usages += 3 + bow.buffedLvl();
		if (usages >= 100f) return 0;
		return (MAX_DURABILITY / usages) + 0.001f;
	}

	@Override
	public int value() {
		return Math.round(7.5f * quantity);
	}

	@Override
	public boolean isSimilar(Item item) {
		return item instanceof TippedDart && super.isSimilar(item)
				&& ((TippedDart) item).effect == effect;
	}

	@Override
	public String name() {
		return super.name(Messages.get(TippedDart.class, effect.key + "_name"));
	}

	@Override
	public String desc() {
		return Messages.get(TippedDart.class, effect.key + "_desc");
	}

	public String discoverHint() {
		return Messages.get(TippedDart.class, effect == Effect.ROT ? "rot_discover_hint" : "discover_hint");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(EFFECT, effect.key);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(EFFECT)) {
			String key = bundle.getString(EFFECT);
			for (Effect candidate : Effect.values()) if (candidate.key.equals(key)) {
				effect = candidate;
				break;
			}
		}
		setEffect(effect);
	}

	public static final LinkedHashMap<Class<? extends Plant.Seed>, Effect> types = new LinkedHashMap<>();
	static {
		for (Effect effect : Effect.values()) types.put(effect.seed, effect);
	}

	public static TippedDart getTipped(Plant.Seed seed, int quantity) {
		Effect effect = types.get(seed.getClass());
		return effect == null ? null : effect.create(quantity);
	}

	public static TippedDart randomTipped(int quantity) {
		Plant.Seed seed;
		do {
			seed = (Plant.Seed) Generator.randomUsingDefaults(Generator.Category.SEED);
		} while (!types.containsKey(seed.getClass()));
		return getTipped(seed, quantity);
	}

	public static TippedDart randomEffect(int quantity) {
		return Random.element(Effect.values()).create(quantity);
	}

	private static int targetPos = -1;
	public static int lostDarts = 0;
}
