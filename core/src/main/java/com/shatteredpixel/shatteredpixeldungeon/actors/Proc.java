package com.shatteredpixel.shatteredpixeldungeon.actors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.MapDevice;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.MiningRockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/**
 * 全游戏统一的伤害结算入口。
 *
 * 所有造成伤害的路径（近战、远程、法术、DoT、陷阱、环境、坠落、真实伤害、处决）
 * 最终都汇聚到 {@link #damage(DamageInfo)}，在单一点内完成：
 * 免疫/抗性 → Doom/DeathMark 倍率 → ChampionEnemy → AntiMagic/Barkskin →
 * Paralysis → 战士护盾 → 护盾吸收 → 扣血 → Grim 后置判定 → Kinetic 过杀存储 → 死亡处理。
 *
 * 真实伤害（{@link #isTrue(DamageInfo)}：way 为 GRIM / EXECUTE / FALL，或携带 trueDamage）
 * 跳过护甲、护盾、全部倍率与免疫检查，直接按数值扣血。
 */
public class Proc {

	/** 本次伤害是否为真实伤害（跳过一切防御与免疫检查） */
	public static boolean isTrue(DamageInfo info) {
		return DamageWay.GRIM.equals(info.way)
				|| DamageWay.EXECUTE.equals(info.way)
				|| DamageWay.FALL.equals(info.way);
	}

	/**
	 * 统一伤害结算。返回实际造成的伤害（扣血前的数值，含被护盾吸收的部分）。
	 */
	public static int damage(DamageInfo info) {
		Char defender = info.defender;
		if (defender == null || !defender.isAlive() || info.total() < 0) return 0;

		//map devices never take HP damage from any source (melee, AoE, gas, ...);
		//they only receive the attack event and persist
		if (defender instanceof MapDevice) {
			((MapDevice) defender).receiveDamage(
					info.legacySrc != null ? info.legacySrc : (Object) (info.offender != null ? info.offender : info));
			return 0;
		}

		Object src = info.legacySrc;

		//无敌检查（主动无敌 buff 是一道独立于类型免疫的硬门，true 伤害同样不可穿透）
		Class<?> invulClass = src != null ? src.getClass() : info.offender != null ? info.offender.getClass() : Proc.class;
		if (defender.isInvulnerable(invulClass)) {
			if (defender.sprite != null) {
				defender.sprite.showStatus(CharSprite.POSITIVE, Messages.get(defender, "invulnerable"));
			}
			return 0;
		}

		//LifeLink 伤害均摊（均摊伤害本身与饥饿伤害不再摊）
		if (!(src instanceof LifeLink || src instanceof Hunger) && defender.buff(LifeLink.class) != null) {
			HashSet<LifeLink> links = defender.buffs(LifeLink.class);
			for (LifeLink link : links.toArray(new LifeLink[0])) {
				if (Actor.findById(link.object) == null) {
					links.remove(link);
					link.detach();
				}
			}
			int share = (int) Math.ceil(info.total() / (float) (links.size() + 1));
			for (LifeLink link : links) {
				Char ch = (Char) Actor.findById(link.object);
				if (ch != null) {
					ch.damage(share, link);
					if (!ch.isAlive()) {
						link.detach();
						if (ch == Dungeon.hero) {
							Badges.validateDeathFromFriendlyMagic();
							Dungeon.fail(src);
							GLog.n(Messages.get(LifeLink.class, "ondeath"));
						}
					}
				}
			}
			info.solidDamage = share;
			info.trueDamage = 0;
		}

		//基础伤害：显式 solidDamage 优先，否则由武器骰出（temporarily a float to avoid constant rounding）
		float damage;
		if (info.solidDamage != 0) {
			damage = info.solidDamage * info.dmgMulti;
		} else if (!isTrue(info) && info.weapon instanceof KindOfWeapon && info.offender != null) {
			damage = ((KindOfWeapon) info.weapon).damageRoll(info.offender) * info.dmgMulti;
		} else {
			damage = info.solidDamage * info.dmgMulti;
		}

		int dmg;
		if (!isTrue(info)) {

			//if dmg is from a character we already reduced it in Char.attack
			if (!info.reducedInAttack) {
				if (Dungeon.hero.alignment == defender.alignment
						&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
						&& (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
					damage *= 0.9f - 0.1f * Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
				}
			}

			if (defender.buff(PowerOfMany.PowerBuff.class) != null) {
				if (defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
					damage *= 0.70f - 0.05f * Dungeon.hero.pointsInTalent(Talent.LIFE_LINK);
				} else {
					damage *= 0.75f;
				}
			}

			Terror t = defender.buff(Terror.class);
			if (t != null) {
				t.recover();
			}
			Dread d = defender.buff(Dread.class);
			if (d != null) {
				d.recover();
			}
			Charm c = defender.buff(Charm.class);
			if (c != null) {
				c.recover(src);
			}
			if (defender.buff(Frost.class) != null) {
				Buff.detach(defender, Frost.class);
			}
			if (defender.buff(MagicalSleep.class) != null) {
				Buff.detach(defender, MagicalSleep.class);
			}
			if (defender.buff(Doom.class) != null && !defender.isImmune(Doom.class)) {
				damage *= 1.67f;
			}
			if (defender.alignment != Char.Alignment.ALLY && defender.buff(DeathMark.DeathMarkTracker.class) != null) {
				damage *= 1.25f;
			}

			if (defender.buff(Sickle.HarvestBleedTracker.class) != null) {
				defender.buff(Sickle.HarvestBleedTracker.class).detach();

				if (!defender.isImmune(Bleeding.class)) {
					Bleeding b = defender.buff(Bleeding.class);
					if (b == null) {
						b = new Bleeding();
					}
					b.announced = false;
					b.set(Math.round(damage), Sickle.HarvestBleedTracker.class);
					b.attachTo(defender);
					defender.sprite.showStatus(CharSprite.WARNING, Messages.titleCase(b.name()) + " " + (int) b.level());
					return 0;
				}
			}

			//免疫/抗性检查：type 桥接到类检查；垫片路径额外带上原始 src 类（去重）
			LinkedHashSet<Class<?>> resistCheck = new LinkedHashSet<>();
			if (info.type != null) {
				for (Class<?> cls : DamageType.classes(info.type)) {
					resistCheck.add(cls);
				}
			}
			if (src != null) {
				resistCheck.add(src.getClass());
			}
			boolean immune = false;
			for (Class<?> cls : resistCheck) {
				if (defender.isImmune(cls)) {
					immune = true;
					break;
				}
			}
			if (immune) {
				damage = 0;
			} else {
				for (Class<?> cls : resistCheck) {
					damage *= defender.resist(cls);
				}
			}

			dmg = Math.round(damage);

			//we ceil these specifically to favor the player vs. champ dmg reduction
			// most important vs. giant champions in the earlygame
			for (ChampionEnemy buff : defender.buffs(ChampionEnemy.class)) {
				dmg = (int) Math.ceil(dmg * buff.damageTakenFactor());
			}

			//TODO improve this when I have proper damage source logic
			if ((src != null && AntiMagic.RESISTS.contains(src.getClass())) || (src == null && DamageWay.MAGIC.equals(info.way))) {
				dmg -= AntiMagic.drRoll(defender, defender.glyphLevel(AntiMagic.class));
				dmg -= Random.NormalIntRange(0, Barkskin.currentLevel(defender));
				if (dmg < 0) dmg = 0;
			}

			if (defender.buff(Paralysis.class) != null) {
				defender.buff(Paralysis.class).processDamage(dmg);
			}

			BrokenSeal.WarriorShield shield = defender.buff(BrokenSeal.WarriorShield.class);
			if (!(src instanceof Hunger)
					&& dmg > 0
					//either HP is already half or below (ignoring shield)
					// or the hit will reduce it to half or below
					&& (defender.HP <= defender.HT / 2 || defender.HP + defender.shielding() - dmg <= defender.HT / 2)
					&& shield != null && !shield.coolingDown()) {
				defender.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shield.maxShield()), FloatingText.SHIELDING);
				shield.activate();
			}

			int shielded = dmg;
			dmg = ShieldBuff.processDamage(defender, dmg, src != null ? src : (Object) (info.offender != null ? info.offender : info));
			shielded -= dmg;

			//真实伤害部分：防御结算后仍造成，不经过任何检查
			dmg += info.trueDamage;

			showDamageIcon(defender, info, src, dmg, shielded);

			defender.HP -= dmg;

		} else {
			//真实伤害：跳过整条修改链，直接扣血
			dmg = info.total();
			showDamageIcon(defender, info, src, dmg, 0);
			defender.HP -= dmg;
		}

		if (defender.HP > 0 && defender.buff(Grim.GrimTracker.class) != null) {

			float finalChance = defender.buff(Grim.GrimTracker.class).maxChance;
			finalChance *= (float) Math.pow(((defender.HT - defender.HP) / (float) defender.HT), 2);

			if (Random.Float() < finalChance) {
				int extraDmg = Math.round(defender.HP * defender.resist(Grim.class));
				dmg += extraDmg;
				defender.HP -= extraDmg;

				defender.sprite.emitter().burst(ShadowParticle.UP, 5);
				if (!defender.isAlive() && defender.buff(Grim.GrimTracker.class).qualifiesForBadge) {
					Badges.validateGrimWeapon();
				}
			}
		}

		if (defender.HP < 0 && info.offender != null && defender.alignment == Char.Alignment.ENEMY) {
			if (info.offender.buff(Kinetic.KineticTracker.class) != null) {
				int dmgToAdd = -defender.HP;
				dmgToAdd -= info.offender.buff(Kinetic.KineticTracker.class).conservedDamage;
				dmgToAdd = Math.round(dmgToAdd * Weapon.Enchantment.genericProcChanceMultiplier(info.offender));
				if (dmgToAdd > 0) {
					Buff.affect(info.offender, Kinetic.ConservedDamage.class).setBonus(dmgToAdd);
				}
				info.offender.buff(Kinetic.KineticTracker.class).detach();
			}
		}

		if (defender.HP < 0) defender.HP = 0;

		if (!defender.isAlive()) {
			defender.die(src != null ? src : (Object) (info.offender != null ? info.offender : info));
		} else if (defender.HP == 0 && defender.buff(DeathMark.DeathMarkTracker.class) != null) {
			DeathMark.processFearTheReaper(defender);
		}

		return dmg;
	}

	//浮动伤害图标：垫片路径保留原有基于来源类的判断，直呼路径按 type 推导
	private static void showDamageIcon(Char defender, DamageInfo info, Object src, int dmg, int shielded) {
		if (defender.sprite == null) return;

		int icon = FloatingText.PHYS_DMG;
		if (src != null) {
			if (NO_ARMOR_PHYSICAL_SOURCES.contains(src.getClass()))     icon = FloatingText.PHYS_DMG_NO_BLOCK;
			if (AntiMagic.RESISTS.contains(src.getClass()))             icon = FloatingText.MAGIC_DMG;
			if (src instanceof Pickaxe)                                 icon = FloatingText.PICK_DMG;

			//special case for sniper when using ranged attacks
			if (src == Dungeon.hero
					&& Dungeon.hero.subClass == HeroSubClass.SNIPER
					&& !Dungeon.level.adjacent(Dungeon.hero.pos, defender.pos)
					&& Dungeon.hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				icon = FloatingText.PHYS_DMG_NO_BLOCK;
			}

			//special case for monk using unarmed abilities
			if (src == Dungeon.hero
					&& Dungeon.hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
				icon = FloatingText.PHYS_DMG_NO_BLOCK;
			}

			if (src instanceof Hunger)                                  icon = FloatingText.HUNGER;
			if (src instanceof Burning)                                 icon = FloatingText.BURNING;
			if (src instanceof Chill || src instanceof Frost)           icon = FloatingText.FROST;
			if (src instanceof StormCloud)                              icon = FloatingText.WATER;
			if (src instanceof Burning)                                 icon = FloatingText.BURNING;
			if (src instanceof Electricity)                             icon = FloatingText.SHOCKING;
			if (src instanceof Bleeding)                                icon = FloatingText.BLEEDING;
			if (src instanceof ToxicGas)                                icon = FloatingText.TOXIC;
			if (src instanceof Corrosion)                               icon = FloatingText.CORROSION;
			if (src instanceof Poison)                                  icon = FloatingText.POISON;
			if (src instanceof Ooze)                                    icon = FloatingText.OOZE;
			if (src instanceof Viscosity.DeferedDamage)                 icon = FloatingText.DEFERRED;
			if (src instanceof Corruption)                              icon = FloatingText.CORRUPTION;
			if (src instanceof AscensionChallenge)                      icon = FloatingText.AMULET;
		} else {
			//直呼路径：显式 icon 优先，否则按 type 推导
			if (info.icon != -1) {
				icon = info.icon;
			} else if (info.type != null) {
				switch (info.type) {
					case DamageType.FIRE:       icon = FloatingText.BURNING; break;
					case DamageType.FROST:      icon = FloatingText.FROST; break;
					case DamageType.ELECTRIC:   icon = FloatingText.SHOCKING; break;
					case DamageType.POISON:     icon = FloatingText.POISON; break;
					case DamageType.ACID:       icon = FloatingText.OOZE; break;
					case DamageType.GAS:        icon = FloatingText.TOXIC; break;
					case DamageType.CORRUPTION: icon = FloatingText.CORRUPTION; break;
					case DamageType.WATER:      icon = FloatingText.WATER; break;
					default:                    icon = FloatingText.PHYS_DMG; break;
				}
			}
		}

		if ((icon == FloatingText.PHYS_DMG || icon == FloatingText.PHYS_DMG_NO_BLOCK) && Char.hitMissIcon != -1) {
			if (icon == FloatingText.PHYS_DMG_NO_BLOCK) Char.hitMissIcon += 18; //extra row
			icon = Char.hitMissIcon;
		}
		Char.hitMissIcon = -1;

		defender.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(dmg + shielded), icon);
	}

	//these are misc. sources of physical damage which do not apply armor, they get a different icon
	private static final HashSet<Class<?>> NO_ARMOR_PHYSICAL_SOURCES = new HashSet<>();
	static {
		NO_ARMOR_PHYSICAL_SOURCES.add(MiningRockfallTrap.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(LifeLink.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Chasm.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(WandOfBlastWave.Knockback.class);
		NO_ARMOR_PHYSICAL_SOURCES.add(Heap.class); //damage from wraiths attempting to spawn from heaps
		NO_ARMOR_PHYSICAL_SOURCES.add(DriedRose.GhostHero.NoRoseDamage.class);
	}

	// ==================== 延迟伤害 ====================

	/**
	 * 延迟结算一次伤害：挂载一个计时 actor，delay 回合后以原参数重新 Proc。
	 * 用于"当前行动结束后再结算"的场景（如死亡后的反击伤害）。
	 * 存档安全：DeferredProc 及其参数会随存档保存。
	 */
	public static void defer(DamageInfo info, float delay) {
		Actor.addDelayed(new DeferredProc(info), delay);
	}

	public static class DeferredProc extends Actor {

		private DamageInfo info;

		{
			actPriority = BLOB_PRIO + 1; //after hero, before other actors
		}

		public DeferredProc() {
		}

		public DeferredProc(DamageInfo info) {
			this.info = info;
		}

		@Override
		protected boolean act() {
			if (info == null) {
				Actor.remove(this);
				return true;
			}
			if (info.cell >= 0) {
				//AoE 爆炸：以锚点结算（锚点是死亡/投掷位置，而非触发时角色位置）
				for (Char ch : Actor.chars().toArray(new Char[0])) {
					if (ch.isAlive() && Dungeon.level.distance(ch.pos, info.cell) <= info.radius) {
						Proc.damage(info.copy().defender(ch).cell(-1));
					}
				}
			} else if (info.defender != null) {
				Proc.damage(info);
			}
			Actor.remove(this);
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			if (info != null) {
				bundle.put("defender", info.defender != null ? info.defender.id() : 0);
				bundle.put("offender", info.offender != null ? info.offender.id() : 0);
				bundle.put("way", info.way);
				bundle.put("type", info.type);
				bundle.put("solid", info.solidDamage);
				bundle.put("true", info.trueDamage);
				bundle.put("multi", info.dmgMulti);
				bundle.put("cell", info.cell);
				bundle.put("radius", info.radius);
			}
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			info = new DamageInfo()
					.way(bundle.getString("way"))
					.type(bundle.getString("type"))
					.solidDamage(bundle.getInt("solid"))
					.trueDamage(bundle.getInt("true"))
					.dmgMulti(bundle.getFloat("multi"))
					.cell(bundle.getInt("cell"))
					.radius(bundle.getInt("radius"));
			int oid = bundle.getInt("offender");
			if (oid > 0) {
				Actor a = Actor.findById(oid);
				if (a instanceof Char) info.offender = (Char) a;
			}
			int did = bundle.getInt("defender");
			if (did > 0) {
				Actor a = Actor.findById(did);
				if (a instanceof Char) info.defender = (Char) a;
			}
		}
	}

	// ==================== 旧垫片 ====================

	/**
	 * 旧式 {@link Char#damage(int, Object)} 的转换垫片：
	 * 从原始 source 推断出 (offender, way, type)，保证既有调用点不迁移也能获得正确归因。
	 */
	public static DamageInfo fromLegacy(Char defender, int dmg, Object src) {
		DamageInfo info = new DamageInfo()
				.defender(defender)
				.solidDamage(dmg)
				.legacySrc(src)
				.reducedInAttack(src instanceof Char);

		if (src instanceof Char) {
			info.offender = (Char) src;
			info.way = DamageWay.MELEE;

		} else if (src instanceof Burning) {
			info.way = DamageWay.DOT;
			info.type = DamageType.FIRE;
		} else if (src instanceof Bleeding || src instanceof Hunger
				|| src instanceof AscensionChallenge || src instanceof Viscosity.DeferedDamage) {
			info.way = DamageWay.DOT;
		} else if (src instanceof Poison) {
			info.way = DamageWay.DOT;
			info.type = DamageType.POISON;
		} else if (src instanceof Ooze || src instanceof Corrosion) {
			info.way = DamageWay.DOT;
			info.type = DamageType.ACID;
		} else if (src instanceof Corruption) {
			info.way = DamageWay.DOT;
			info.type = DamageType.CORRUPTION;
		} else if (src instanceof ToxicGas) {
			info.way = DamageWay.DOT;
			info.type = DamageType.GAS;
		} else if (src instanceof Electricity) {
			info.way = DamageWay.DOT;
			info.type = DamageType.ELECTRIC;
		} else if (src instanceof StormCloud) {
			info.way = DamageWay.DOT;
			info.type = DamageType.WATER;

		} else if (src instanceof Chasm) {
			info.way = DamageWay.FALL;

		} else if (src instanceof Rat.RangedAttack) {
			info.way = DamageWay.RANGED;

		} else if (src instanceof Wand) {
			info.way = DamageWay.MAGIC;
			info.type = wandType((Wand) src);
		} else if (src instanceof ScrollOfRetribution || src instanceof ScrollOfPsionicBlast) {
			info.way = DamageWay.MAGIC;

		} else if (src instanceof Trap) {
			info.way = DamageWay.TRAP;
		} else if (src instanceof Item) {
			info.way = DamageWay.ENV;

		} else {
			info.way = DamageWay.ENV;
		}
		return info;
	}

	private static String wandType(Wand w) {
		if (w instanceof WandOfLightning)      return DamageType.ELECTRIC;
		if (w instanceof WandOfFireblast)      return DamageType.FIRE;
		if (w instanceof WandOfFrost)          return DamageType.FROST;
		if (w instanceof WandOfCorrosion)      return DamageType.ACID;
		if (w instanceof WandOfCorruption)     return DamageType.CORRUPTION;
		if (w instanceof WandOfPrismaticLight) return DamageType.PHYSICAL;
		return DamageType.PHYSICAL;
	}

	// ==================== 伤害描述与分类（内部类） ====================

	/**
	 * 一次伤害的完整描述，由调用方构建后交给 {@link Proc#damage(DamageInfo)} 统一结算。
	 *
	 * 字段语义：
	 * - {@link #solidDamage}：进入完整修改链（免疫/抗性→Doom/DeathMark→护盾）的固定基础伤害。
	 *   为 0 且携带 weapon 时，由 Proc 内部通过 weapon 的 damageRoll 计算基础值。
	 * - {@link #trueDamage}：防御结算后仍造成的伤害，跳过护甲/护盾/全部倍率/免疫检查。
	 *   单独使用即"真实伤害"。
	 * - {@link #way} 为 GRIM / EXECUTE / FALL 时整次伤害走真实伤害路径（见 {@link Proc#isTrue(DamageInfo)}）。
	 */
	public static class DamageInfo {

		public Char offender;       // 造成伤害者；环境/陷阱等无来源时为 null
		public Char defender;       // 受伤害者
		public Item weapon;         // 造成伤害的武器/法杖等；可为 null
		public String way = DamageWay.ENV;   // 行为类别（字符串，见 DamageWay）
		public String type = DamageType.PHYSICAL; // 元素类别（字符串，见 DamageType）
		public int solidDamage = 0; // 进入修改链的基础伤害
		public int trueDamage = 0;  // 防御结算后仍造成的伤害（真实伤害）
		public float dmgMulti = 1f; // 通用伤害倍率
		public boolean reducedInAttack = false; // 伤害是否已经过 Char.attack 管线修正（决定 AuraOfProtection/Kinetic 等是否再处理）
		public Object legacySrc = null; // 旧垫片传入的原始 source；保留图标/LifeLink/Hunger 等特殊判断

		/** 浮动伤害图标，-1 = 由 type 推导 */
		public int icon = -1;

		/** 延迟伤害（{@link Proc#defer}）用：爆炸锚点，-1 表示单目标 */
		public int cell = -1;
		/** 延迟伤害用：AoE 半径（配合 cell），0 表示只炸 cell 格 */
		public int radius = 0;

		//可选附加数据（如 LifeLink 关联等），懒分配
		private HashMap<String, Object> extras;

		public DamageInfo offender(Char offender) {
			this.offender = offender;
			return this;
		}

		public DamageInfo defender(Char defender) {
			this.defender = defender;
			return this;
		}

		public DamageInfo weapon(Item weapon) {
			this.weapon = weapon;
			return this;
		}

		public DamageInfo way(String way) {
			this.way = way;
			return this;
		}

		public DamageInfo type(String type) {
			this.type = type;
			return this;
		}

		public DamageInfo solidDamage(int solidDamage) {
			this.solidDamage = solidDamage;
			return this;
		}

		public DamageInfo trueDamage(int trueDamage) {
			this.trueDamage = trueDamage;
			return this;
		}

		public DamageInfo dmgMulti(float dmgMulti) {
			this.dmgMulti = dmgMulti;
			return this;
		}

		public DamageInfo reducedInAttack(boolean reducedInAttack) {
			this.reducedInAttack = reducedInAttack;
			return this;
		}

		public DamageInfo legacySrc(Object legacySrc) {
			this.legacySrc = legacySrc;
			return this;
		}

		public DamageInfo icon(int icon) {
			this.icon = icon;
			return this;
		}

		public DamageInfo cell(int cell) {
			this.cell = cell;
			return this;
		}

		public DamageInfo radius(int radius) {
			this.radius = radius;
			return this;
		}

		public DamageInfo putExtra(String key, Object value) {
			if (extras == null) {
				extras = new HashMap<>();
			}
			extras.put(key, value);
			return this;
		}

		public Object extra(String key) {
			return extras == null ? null : extras.get(key);
		}

		public int total() {
			return solidDamage + trueDamage;
		}

		/** 复制一份，用于延迟伤害对多个目标逐个结算 */
		public DamageInfo copy() {
			DamageInfo copy = new DamageInfo()
					.offender(offender).defender(defender).weapon(weapon)
					.way(way).type(type)
					.solidDamage(solidDamage).trueDamage(trueDamage)
					.dmgMulti(dmgMulti).reducedInAttack(reducedInAttack)
					.legacySrc(legacySrc).icon(icon)
					.cell(cell).radius(radius);
			if (extras != null) {
				copy.extras = new HashMap<>(extras);
			}
			return copy;
		}
	}

	/**
	 * 伤害的行为类别（字符串常量，与 {@link DamageType} 元素正交）。
	 * 同样是火：近战附魔 = MELEE+FIRE，火球术 = MAGIC+FIRE，燃烧 DoT = DOT+FIRE。
	 *
	 * GRIM / EXECUTE / FALL 携带"真实伤害"语义：
	 * 跳过护甲、护盾、全部倍率与免疫检查，直接按数值扣血（见 {@link Proc#isTrue(DamageInfo)}）。
	 *
	 * 调用点既可用本类常量（{@code DamageWay.MELEE}），也可直接用裸字符串（{@code "melee"}）。
	 */
	public static class DamageWay {

		/** 近战攻击（武器、徒手、怪物近战） */
		public static final String MELEE = "melee";
		/** 远程攻击（投掷武器、弓、老鼠远程变种） */
		public static final String RANGED = "ranged";
		/** 法术（法杖 zap、卷轴、技能） */
		public static final String MAGIC = "magic";
		/** 持续伤害（buff 每回合 tick、blob 区域 tick） */
		public static final String DOT = "dot";
		/** 陷阱 */
		public static final String TRAP = "trap";
		/** 环境 / 爆炸 / 其他无明确来源 */
		public static final String ENV = "env";
		/** 坠落（真实伤害，type 仍为 PHYSICAL） */
		public static final String FALL = "fall";
		/** 自伤（血之圣杯、传送自伤等） */
		public static final String SELF = "self";
		/** 真实伤害：按比例穿透一切，无任何单位免疫（如 Grim） */
		public static final String GRIM = "grim";
		/** 处决：必杀，穿透一切（如潜伏处决、双持处决） */
		public static final String EXECUTE = "execute";

		private DamageWay() {
		}
	}

	/**
	 * 伤害的元素类别（字符串常量，与 {@link DamageWay} 行为正交）。
	 * 调用点既可用本类常量（{@code DamageType.FIRE}），也可直接用裸字符串（{@code "fire"}）。
	 *
	 * 每个 type 桥接到其代表性 buff/blob 类（见 {@link #classes(String)}），
	 * 用于免疫/抗性检查（{@link Char#isImmune(Class)} / {@link Char#resist(Class)}），
	 * 从而保留现有的类级免疫覆写（如 Brimstone → Burning.class 免疫）。
	 */
	public static class DamageType {

		/** 物理伤害（近战、普通投射物、坠落） */
		public static final String PHYSICAL = "physical";
		/** 火焰 / 燃烧 */
		public static final String FIRE = "fire";
		/** 冰冻 / 减速 */
		public static final String FROST = "frost";
		/** 闪电 */
		public static final String ELECTRIC = "electric";
		/** 毒素 */
		public static final String POISON = "poison";
		/** 酸性 / 腐蚀 / 黏液 */
		public static final String ACID = "acid";
		/** 毒气 */
		public static final String GAS = "gas";
		/** 腐化 */
		public static final String CORRUPTION = "corruption";
		/** 水 / 风暴云 */
		public static final String WATER = "water";

		private static final Map<String, Class<?>[]> CLASSES = new HashMap<>();

		static {
			CLASSES.put(FIRE, new Class<?>[]{Burning.class});
			CLASSES.put(FROST, new Class<?>[]{Frost.class, Chill.class});
			CLASSES.put(ELECTRIC, new Class<?>[]{Electricity.class});
			CLASSES.put(POISON, new Class<?>[]{Poison.class});
			CLASSES.put(ACID, new Class<?>[]{Ooze.class, Corrosion.class});
			CLASSES.put(GAS, new Class<?>[]{ToxicGas.class});
			CLASSES.put(CORRUPTION, new Class<?>[]{Corruption.class});
			CLASSES.put(WATER, new Class<?>[]{StormCloud.class});
		}

		private DamageType() {
		}

		/** 该类伤害对应的免疫/抗性检查类（未知 type 返回空数组） */
		public static Class<?>[] classes(String type) {
			Class<?>[] classes = CLASSES.get(type);
			return classes != null ? classes : new Class<?>[0];
		}
	}
}
