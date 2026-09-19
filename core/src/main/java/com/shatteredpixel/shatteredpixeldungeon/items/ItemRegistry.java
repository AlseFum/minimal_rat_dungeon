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
 */package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.experimental.chapinit.StoneOfFrostEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.*;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.*;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.hero.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 物品注册表：全游戏唯一的物品目录（不分楼层）——只管"有哪些物品、怎么造、怎么改"。
 *
 * <p><b>目录 + 生成器</b>：每条 {@link Entry} 显式绑定一个工厂，{@link #generate(Class)} 按类型确定性产出；
 * 清单刻意写死，删除物品会在编译期报错，不做运行时类路径扫描、不依赖反射构造。</p>
 *
 * <p><b>抽取机器</b>：{@link #roll(Map)} 是通用加权抽取，<b>权重由调用方给</b>——
 * 掉落策略（权重、本局唯一、概率随层变化）都属于掉落系统，见 {@link Loot}。</p>
 *
 * <p><b>可修改项</b>：条目与清单在运行时可变（{@link #add} / {@link #remove} / {@link #replace} /
 * {@link #resetToDefaults} 与 {@link Entry#addModifier}），供后期物品修改功能使用；
 * 结构性修改必须走这些 mutator，以保持 byType 索引同步。</p>
 */
public final class ItemRegistry {

	private ItemRegistry() {
	}

	public static final class Entry {

		/**
		 * 目录内的数字把手：调试工具、编辑器、mod 配置拿它引用条目。
		 * 与存档无关（物品实际怎么存还怎么存），分段编号（见 DEFAULTS 里的注释）。
		 */
		private final int id;
		private final Class<? extends Item> type;
		private Supplier<? extends Item> factory;

		//生成时逐项应用的修饰钩子，供后期物品修改注入（先全部应用，再调 item.random()）
		private final List<UnaryOperator<Item>> modifiers = new ArrayList<>();

		private Entry(int id, Class<? extends Item> type, Supplier<? extends Item> factory) {
			this.id = id;
			this.type = type;
			this.factory = factory;
		}

		public int id() {
			return id;
		}

		public Class<? extends Item> type() {
			return type;
		}

		/** 裸实例（不 random、不应用 modifier），供图标预览等只读用途。 */
		public Item create() {
			return factory.get();
		}

		/** 未随机化的成品：工厂实例 → 逐项 modifier（调试/编辑器取件用，数值可预期）。 */
		public Item build() {
			Item item = factory.get();
			if (item == null) return null;
			for (UnaryOperator<Item> modifier : modifiers) {
				item = modifier.apply(item);
			}
			return item;
		}

		/** 掉落成品：{@link #build()} 之后再 {@link Item#random()}。 */
		public Item generate() {
			Item item = build();
			return item == null ? null : item.random();
		}

		public Entry factory(Supplier<? extends Item> factory) {
			this.factory = factory;
			return this;
		}

		public Entry addModifier(UnaryOperator<Item> modifier) {
			modifiers.add(modifier);
			return this;
		}

		public Entry removeModifier(UnaryOperator<Item> modifier) {
			modifiers.remove(modifier);
			return this;
		}
	}

	/**
	 * 登记一件物品：id + 类型 + 工厂。掉落权重与本局唯一性不在这里，见 {@link Loot}。
	 *
	 * <p>id 分段（调试时看号即知类别）：100s 核心/剧情、200s 护甲、300s 神器、400s 炸弹、
	 * 500s 食物、600s 药水、700s 卷轴、800s 法术、900s 符石、1000s 种子、1100s 饰品、
	 * 1200s 法杖、1300s 戒指、1400s 近战武器、1500s 投掷、1600s 开局武/移植武、
	 * 1700s 钥匙/日记/任务遗物。**9000+ 段留给 mod**，不要占用。</p>
	 */
	private static Entry entry(int id, Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(id, type, factory);
	}

	/** 初始清单（不可变快照），供 {@link #resetToDefaults()} 还原。 */
	private static final List<Entry> DEFAULTS = Collections.unmodifiableList(Arrays.asList(
			// Core items（Amulet/王冠/面具/开局物等仅脚本化来源，不入池）
			entry(100, Ankh.class, Ankh::new),
			entry(101, ArcaneResin.class, ArcaneResin::new),
			entry(102, Dewdrop.class, Dewdrop::new),
			entry(103, EnergyCrystal.class, EnergyCrystal::new),
			entry(104, Gold.class, Gold::new),
			entry(105, Honeypot.class, Honeypot::new),
			entry(106, LiquidMetal.class, LiquidMetal::new),
			entry(107, Stylus.class, Stylus::new),
			entry(108, Torch.class, Torch::new),

			// 剧情/功能件（脚本化发放，不入平铺池）
			entry(109, Amulet.class, Amulet::new),
			entry(110, BrokenSeal.class, BrokenSeal::new),
			entry(111, KingsCrown.class, KingsCrown::new),
			entry(112, TengusMask.class, TengusMask::new),
			entry(113, Waterskin.class, Waterskin::new),
			entry(114, Honeypot.ShatteredPot.class, Honeypot.ShatteredPot::new),

			// Armor
			entry(200, Armor.class, Armor::new),

			// Artifacts are run-unique, kept at the rare tier
			entry(300, AlchemistsToolkit.class, AlchemistsToolkit::new),
			entry(301, ChaliceOfBlood.class, ChaliceOfBlood::new),
			entry(302, CloakOfShadows.class, CloakOfShadows::new),
			entry(303, DriedRose.class, DriedRose::new),
			entry(304, EtherealChains.class, EtherealChains::new),
			entry(305, HolyTome.class, HolyTome::new),
			entry(306, HornOfPlenty.class, HornOfPlenty::new),
			entry(307, MasterThievesArmband.class, MasterThievesArmband::new),
			entry(308, SandalsOfNature.class, SandalsOfNature::new),
			entry(309, SkeletonKey.class, SkeletonKey::new),
			entry(310, TalismanOfForesight.class, TalismanOfForesight::new),
			entry(311, TimekeepersHourglass.class, TimekeepersHourglass::new),
			entry(312, UnstableSpellbook.class, UnstableSpellbook::new),
			entry(313, DriedRose.Petal.class, DriedRose.Petal::new),
			entry(314, TimekeepersHourglass.sandBag.class, TimekeepersHourglass.sandBag::new),
			entry(315, CapeOfThorns.class, CapeOfThorns::new),

			// Bombs（均有 Bomb+原料 炼金配方兜底）
			entry(400, Bomb.class, Bomb::new),
			entry(401, PayloadBomb.class, PayloadBomb::randomPayload),
			entry(402, HolyBomb.class, HolyBomb::new),
			entry(403, Noisemaker.class, Noisemaker::new),
			entry(404, RegrowthBomb.class, RegrowthBomb::new),
			entry(405, WoollyBomb.class, WoollyBomb::new),
			entry(406, Bomb.DoubleBomb.class, Bomb.DoubleBomb::new),

			// Food
			entry(500, Berry.class, Berry::new),
			entry(501, Blandfruit.class, Blandfruit::new),
			entry(502, ChargrilledMeat.class, ChargrilledMeat::new),
			entry(503, Food.class, Food::new),
			entry(504, FrozenCarpaccio.class, FrozenCarpaccio::new),
			entry(505, MeatPie.class, MeatPie::new),
			entry(506, MysteryMeat.class, MysteryMeat::new),
			entry(507, Pasty.class, Pasty::new),
			entry(508, SupplyRation.class, SupplyRation::new),
			entry(509, Pasty.FishLeftover.class, Pasty.FishLeftover::new),
			entry(510, Blandfruit.Chunks.class, Blandfruit.Chunks::new),

			// The retained journal example is a usable pickup, not a UI placeholder
			entry(1700, Guidebook.class, Guidebook::new),

			// Keys are scripted with their locks and never roll from this pool
			entry(1701, Key.class, Key::new),

			// 任务与遗物（脚本化，仅供查阅/调试取件）
			entry(1702, DarkGold.class, DarkGold::new),
			entry(1703, GooBlob.class, GooBlob::new),
			entry(1704, Pickaxe.class, Pickaxe::new),
			entry(1705, BowFragment.class, BowFragment::new),
			entry(1706, BrokenHilt.class, BrokenHilt::new),
			entry(1707, BrokenStaff.class, BrokenStaff::new),
			entry(1708, CloakScrap.class, CloakScrap::new),
			entry(1709, SealShard.class, SealShard::new),
			entry(1710, TornPage.class, TornPage::new),

			// The reduced potion family keeps one representative per core effect.
			entry(600, PotionOfFrost.class, PotionOfFrost::new),
			entry(601, PotionOfHaste.class, PotionOfHaste::new),
			entry(602, PotionOfHealing.class, PotionOfHealing::new),
			entry(603, PotionOfLevitation.class, PotionOfLevitation::new),
			entry(604, PotionOfLiquidFlame.class, PotionOfLiquidFlame::new),
			entry(605, PotionOfPurity.class, PotionOfPurity::new),
			entry(606, PotionOfToxicGas.class, PotionOfToxicGas::new),
			// 力量药水由 strNeeded 每 5 层保底发放，不进平铺池
			entry(607, PotionOfStrength.class, PotionOfStrength::new),

			// Rings（Ring 统一随机九种效果，deck 概率 27:3:3）
			entry(1300, Ring.class, Ring::new),
			entry(1301, RingOfForce.class, RingOfForce::new),
			entry(1302, RingOfWealth.class, RingOfWealth::new),

			// Regular scrolls（SoU 由 souNeeded 保底；exotic 卷轴由炼金 ScrollToExotic 产出，均不入池）
			entry(700, ScrollOfIdentify.class, ScrollOfIdentify::new),
			entry(701, ScrollOfLullaby.class, ScrollOfLullaby::new),
			entry(702, ScrollOfMagicMapping.class, ScrollOfMagicMapping::new),
			entry(703, ScrollOfMirrorImage.class, ScrollOfMirrorImage::new),
			entry(704, ScrollOfRage.class, ScrollOfRage::new),
			entry(705, ScrollOfRecharging.class, ScrollOfRecharging::new),
			entry(706, ScrollOfRemoveCurse.class, ScrollOfRemoveCurse::new),
			entry(707, ScrollOfRetribution.class, ScrollOfRetribution::new),
			entry(708, ScrollOfTeleportation.class, ScrollOfTeleportation::new),
			entry(709, ScrollOfTerror.class, ScrollOfTerror::new),
			entry(710, ScrollOfTransmutation.class, ScrollOfTransmutation::new),
			// 升级卷由 souNeeded 保底，不参与平铺随机
			entry(711, ScrollOfUpgrade.class, ScrollOfUpgrade::new),

			// Exotic scrolls（由炼金 ScrollToExotic 产出，不入池）
			entry(712, ScrollOfAntiMagic.class, ScrollOfAntiMagic::new),
			entry(713, ScrollOfChallenge.class, ScrollOfChallenge::new),
			entry(714, ScrollOfDivination.class, ScrollOfDivination::new),
			entry(715, ScrollOfDread.class, ScrollOfDread::new),
			entry(716, ScrollOfEnchantment.class, ScrollOfEnchantment::new),
			entry(717, ScrollOfForesight.class, ScrollOfForesight::new),
			entry(718, ScrollOfPassage.class, ScrollOfPassage::new),
			entry(719, ScrollOfPrismaticImage.class, ScrollOfPrismaticImage::new),
			entry(720, ScrollOfPsionicBlast.class, ScrollOfPsionicBlast::new),
			entry(721, ScrollOfSirensSong.class, ScrollOfSirensSong::new),

			// Alchemical spells（均有 Recipe 炼金配方兜底；MagicalInfusion 仅 SoU 合成）
			entry(800, Alchemize.class, Alchemize::new),
			entry(801, BeaconOfReturning.class, BeaconOfReturning::new),
			entry(802, CurseInfusion.class, CurseInfusion::new),
			entry(803, PhaseShift.class, PhaseShift::new),
			entry(804, ReclaimTrap.class, ReclaimTrap::new),
			entry(805, Recycle.class, Recycle::new),
			entry(806, SummonElemental.class, SummonElemental::new),
			entry(807, TelekineticGrab.class, TelekineticGrab::new),
			entry(808, UnstableSpell.class, UnstableSpell::new),
			entry(809, WildEnergy.class, WildEnergy::new),
			entry(810, MagicalInfusion.class, MagicalInfusion::new),

			// Runestones（Enchantment 由 6-19 层保底/合成；Augmentation 每商店 1 个/合成，均不入池）
			entry(900, StoneOfAggression.class, StoneOfAggression::new),
			entry(901, StoneOfBlast.class, StoneOfBlast::new),
			entry(902, StoneOfBlink.class, StoneOfBlink::new),
			entry(903, StoneOfClairvoyance.class, StoneOfClairvoyance::new),
			entry(904, StoneOfDeepSleep.class, StoneOfDeepSleep::new),
			entry(905, StoneOfDetectMagic.class, StoneOfDetectMagic::new),
			entry(906, StoneOfFear.class, StoneOfFear::new),
			entry(907, StoneOfFlock.class, StoneOfFlock::new),
			entry(908, StoneOfIntuition.class, StoneOfIntuition::new),
			entry(909, StoneOfShock.class, StoneOfShock::new),
			// 附魔石 6-19 层保底、强化石每商店 1 个（均不入池）；冰霜附魔石为 chapinit 内容
			entry(910, StoneOfEnchantment.class, StoneOfEnchantment::new),
			entry(911, StoneOfAugmentation.class, StoneOfAugmentation::new),
			entry(912, StoneOfFrostEnchantment.class, StoneOfFrostEnchantment::new),

			// Trinkets are run-unique；催化剂由 1-4 层保底发放，不入池
			entry(1100, ChaoticCenser.class, ChaoticCenser::new),
			entry(1101, CrackedSpyglass.class, CrackedSpyglass::new),
			entry(1102, DimensionalSundial.class, DimensionalSundial::new),
			entry(1103, ExoticCrystals.class, ExoticCrystals::new),
			entry(1104, EyeOfNewt.class, EyeOfNewt::new),
			entry(1105, FerretTuft.class, FerretTuft::new),
			entry(1106, MossyClump.class, MossyClump::new),
			entry(1107, ParchmentScrap.class, ParchmentScrap::new),
			entry(1108, PetrifiedSeed.class, PetrifiedSeed::new),
			entry(1109, SaltCube.class, SaltCube::new),
			entry(1110, ShardOfOblivion.class, ShardOfOblivion::new),
			entry(1111, ThirteenLeafClover.class, ThirteenLeafClover::new),
			entry(1112, TrapMechanism.class, TrapMechanism::new),
			entry(1113, VialOfBlood.class, VialOfBlood::new),
			entry(1114, WondrousResin.class, WondrousResin::new),
			entry(1115, TrinketCatalyst.class, TrinketCatalyst::new),

			// Wands（充能武器，稀有档）
			entry(1200, WandOfBlastWave.class, WandOfBlastWave::new),
			entry(1201, WandOfCorrosion.class, WandOfCorrosion::new),
			entry(1202, WandOfCorruption.class, WandOfCorruption::new),
			entry(1203, WandOfDisintegration.class, WandOfDisintegration::new),
			entry(1204, WandOfFireblast.class, WandOfFireblast::new),
			entry(1205, WandOfFrost.class, WandOfFrost::new),
			entry(1206, WandOfLightning.class, WandOfLightning::new),
			entry(1207, WandOfLivingEarth.class, WandOfLivingEarth::new),
			entry(1208, WandOfMagicMissile.class, WandOfMagicMissile::new),
			entry(1209, WandOfPrismaticLight.class, WandOfPrismaticLight::new),
			entry(1210, WandOfRegrowth.class, WandOfRegrowth::new),
			entry(1211, WandOfTransfusion.class, WandOfTransfusion::new),
			entry(1212, WandOfWarding.class, WandOfWarding::new),

			// Melee weapons（一般武入池：盗贼开局已换 WornDagger，Dagger 本体转为纯池武器；
			// Sword 战士开局已换 WornShortsword，通用剑作为"普通武器"位入池；
			// Crossbow 属远程弹药系武器，只在平铺池稀有档掉落，不进 WEP deck）
			entry(1400, Dagger.class, Dagger::new),
			entry(1401, Flail.class, Flail::new),
			entry(1402, RoundShield.class, RoundShield::new),
			entry(1403, Scimitar.class, Scimitar::new),
			entry(1404, Sickle.class, Sickle::new),
			entry(1405, Spear.class, Spear::new),
			entry(1406, Sword.class, Sword::new),
			entry(1407, Crossbow.class, Crossbow::new),

			// 英雄开局武（HeroClass 发放，不入池；调试取件与编辑器需要能查到）
			entry(1600, Cudgel.class, Cudgel::new),
			entry(1601, Knuckleduster.class, Knuckleduster::new),
			entry(1602, MagesStaff.class, () -> new MagesStaff(new WandOfMagicMissile())),
			entry(1603, Rapier.class, Rapier::new),
			entry(1604, SpiritBow.class, SpiritBow::new),
			entry(1605, WornDagger.class, WornDagger::new),
			entry(1606, WornShortsword.class, WornShortsword::new),

			// Zoot 移植武（仅调试生成）
			entry(1607, AscalonAOE.class, AscalonAOE::new),
			entry(1608, Chakram.class, Chakram::new),
			entry(1609, DeployablewCrossBow.class, DeployablewCrossBow::new),
			entry(1610, HeavyBow.class, HeavyBow::new),
			entry(1611, PhantomKnife.class, PhantomKnife::new),

			// Thrown weapons and darts（TippedDart 需先有 Dart 上毒/蘸药）
			entry(1500, Bolas.class, Bolas::new),
			entry(1501, ForceCube.class, ForceCube::new),
			entry(1502, HeavyBoomerang.class, HeavyBoomerang::new),
			entry(1503, Shuriken.class, Shuriken::new),
			entry(1504, ThrowingClub.class, ThrowingClub::new),
			entry(1505, ThrowingKnife.class, ThrowingKnife::new),
			entry(1506, ThrowingStone.class, ThrowingStone::new),
			entry(1507, Tomahawk.class, Tomahawk::new),
			entry(1508, Dart.class, Dart::new),
			entry(1509, TippedDart.class, () -> TippedDart.randomEffect(1)),

			// Every surviving independently plantable seed
			entry(1000, Rotberry.Seed.class, Rotberry.Seed::new),
			entry(1001, BlandfruitBush.Seed.class, BlandfruitBush.Seed::new),
			entry(1002, Blindweed.Seed.class, Blindweed.Seed::new),
			entry(1003, Earthroot.Seed.class, Earthroot.Seed::new),
			entry(1004, Fadeleaf.Seed.class, Fadeleaf.Seed::new),
			entry(1005, Firebloom.Seed.class, Firebloom.Seed::new),
			entry(1006, Icecap.Seed.class, Icecap.Seed::new),
			entry(1007, Mageroyal.Seed.class, Mageroyal.Seed::new),
			entry(1008, Sorrowmoss.Seed.class, Sorrowmoss.Seed::new),
			entry(1009, Starflower.Seed.class, Starflower.Seed::new),
			entry(1010, Stormvine.Seed.class, Stormvine.Seed::new),
			entry(1011, Sungrass.Seed.class, Sungrass.Seed::new),
			entry(1012, Swiftthistle.Seed.class, Swiftthistle.Seed::new)
	));

	/** 活动清单（可变副本），结构性修改走 {@link #add} / {@link #remove} / {@link #replace}。 */
	private static final List<Entry> ENTRIES = new ArrayList<>(DEFAULTS);

	private static final Map<Integer, Entry> BY_ID = new LinkedHashMap<>();
	/**
	 * 类型 → 该类型的全部条目，<b>声明顺序</b>；一个类可以派生出多个条目（变体），
	 * 所以这里是多值。第一个是默认条目（按类型取件时用它）。
	 */
	private static final Map<Class<? extends Item>, List<Entry>> BY_TYPE = new LinkedHashMap<>();

	static {
		for (Entry entry : ENTRIES) {
			if (BY_ID.put(entry.id, entry) != null) {
				throw new IllegalStateException("Duplicate item id: " + entry.id);
			}
			BY_TYPE.computeIfAbsent(entry.type, k -> new ArrayList<>()).add(entry);
		}
	}

	public static List<Entry> entries() {
		return ENTRIES;
	}

	public static int size() {
		return ENTRIES.size();
	}

	/** 按 id 取条目；未登记返回 null。 */
	public static Entry get(int id) {
		return BY_ID.get(id);
	}

	/** 该类型的默认条目（声明里的第一个）；非 1:1 的类型请用 {@link #entriesOf(Class)}。 */
	public static Entry get(Class<? extends Item> type) {
		List<Entry> list = BY_TYPE.get(type);
		return list == null ? null : list.get(0);
	}

	/** 该类型登记的全部条目（变体），按声明顺序；未登记返回空表。 */
	public static List<Entry> entriesOf(Class<? extends Item> type) {
		List<Entry> list = BY_TYPE.get(type);
		return list == null ? new ArrayList<>() : new ArrayList<>(list);
	}

	/**
	 * Creates an explicitly registered item without reflective construction.
	 *
	 * <p>A null result means the class is a compatibility-only type (for example a
	 * dynamically supplied mob loot class) and the caller may choose its legacy
	 * fallback.</p>
	 */
	public static Item generate(Class<? extends Item> type) {
		Entry entry = get(type);
		return entry == null ? null : entry.generate();
	}

	/** 按 id 生成（掉落成品，含 random）。 */
	public static Item generate(int id) {
		Entry entry = BY_ID.get(id);
		return entry == null ? null : entry.generate();
	}

	/** 按类型构造（应用条目 modifier，不做 random）；未注册返回 null。 */
	public static Item build(Class<? extends Item> type) {
		Entry entry = get(type);
		return entry == null ? null : entry.build();
	}

	/** 按 id 构造（应用条目 modifier，不做 random）。 */
	public static Item build(int id) {
		Entry entry = BY_ID.get(id);
		return entry == null ? null : entry.build();
	}

	/**
	 * 通用加权抽取：权重表按类型给，缺省或非正的条目不参与；返回抽中的条目。
	 *
	 * <p>平铺池的权重与"本局唯一"由 {@link Loot} 提供并维护——本类只管滚动抽取本身。
	 * 抽取按目录顺序进行，保证与既有行为逐位一致。</p>
	 */
	public static Entry roll(Map<Class<? extends Item>, Float> weights) {
		float sum = 0;
		for (Entry entry : ENTRIES) {
			Float w = weights.get(entry.type);
			if (w != null && w > 0) {
				sum += w;
			}
		}

		if (sum <= 0) {
			throw new IllegalStateException("No weighted entries available for the roll");
		}

		float roll = Random.Float(sum);
		for (Entry entry : ENTRIES) {
			Float w = weights.get(entry.type);
			if (w == null || w <= 0) continue;
			if (roll < w) return entry;
			roll -= w;
		}

		throw new IllegalStateException("The item registry changed during selection");
	}

	//========== 可修改项 ==========

	/** 追加一条（id 重复抛异常）；同一个类可以追加多条（变体）。 */
	public static Entry add(int id, Class<? extends Item> type, Supplier<? extends Item> factory) {
		if (BY_ID.containsKey(id)) {
			throw new IllegalStateException("Duplicate item id: " + id);
		}
		Entry entry = new Entry(id, type, factory);
		ENTRIES.add(entry);
		BY_ID.put(id, entry);
		BY_TYPE.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
		return entry;
	}

	/** 按 id 移除；未登记则返回 null。 */
	public static Entry remove(int id) {
		Entry entry = BY_ID.remove(id);
		if (entry != null) {
			ENTRIES.remove(entry);
			List<Entry> list = BY_TYPE.get(entry.type);
			if (list != null) {
				list.remove(entry);
				if (list.isEmpty()) BY_TYPE.remove(entry.type);
			}
		}
		return entry;
	}

	/** 移除某类型的默认条目；未登记则返回 null。 */
	public static Entry remove(Class<? extends Item> type) {
		Entry entry = get(type);
		return entry == null ? null : remove(entry.id);
	}

	/** 用同 id 的新条目替换旧的（保留原位置）。 */
	public static void replace(Entry entry) {
		Entry old = BY_ID.put(entry.id, entry);
		if (old == null) {
			throw new IllegalStateException("Cannot replace unregistered item entry: " + entry.id);
		}
		ENTRIES.set(ENTRIES.indexOf(old), entry);

		List<Entry> list = BY_TYPE.get(old.type);
		if (list != null) {
			int at = list.indexOf(old);
			if (at >= 0) list.set(at, entry);
			if (old.type != entry.type) {
				list.remove(entry);
				if (list.isEmpty()) BY_TYPE.remove(old.type);
				BY_TYPE.computeIfAbsent(entry.type, k -> new ArrayList<>()).add(entry);
			}
		}
	}

	/** 还原成初始清单（丢弃全部 add/remove/条目改动）。 */
	public static void resetToDefaults() {
		ENTRIES.clear();
		ENTRIES.addAll(DEFAULTS);
		BY_ID.clear();
		BY_TYPE.clear();
		for (Entry entry : ENTRIES) {
			if (BY_ID.put(entry.id, entry) != null) {
				throw new IllegalStateException("Duplicate item id: " + entry.id);
			}
			BY_TYPE.computeIfAbsent(entry.type, k -> new ArrayList<>()).add(entry);
		}
	}
}
