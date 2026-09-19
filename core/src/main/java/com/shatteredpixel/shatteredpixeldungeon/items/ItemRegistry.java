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

package com.shatteredpixel.shatteredpixeldungeon.items;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 物品注册表：全游戏唯一的物品来源表（不分楼层），承载三件事。
 *
 * <p><b>生成器</b>：每条 {@link Entry} 显式绑定一个工厂（三档权重：常见 10 / 罕见 4 / 稀有 1），
 * {@link #generate(Class)} 按类型确定性产出；清单刻意写死，删除物品会在编译期报错，
 * 不做运行时类路径扫描、不依赖反射构造。</p>
 *
 * <p><b>随机生成器</b>：{@link #random()} 按权重滚动抽取，本局已生成的 unique 条目权重按 0 处理。</p>
 *
 * <p><b>可修改项</b>：条目与清单在运行时可变（{@link Entry#weight(int)} 等流式 setter、
 * {@link #add} / {@link #remove} / {@link #replace} / {@link #resetToDefaults}），
 * 供后期物品修改功能使用；结构性修改必须走这些 mutator，以保持 byType 索引同步。</p>
 *
 * <p>只通过脚本化渠道产出的物品（任务奖励、保底掉落、炼金产出、开局武、剧情件）也登记在册，
 * 但用 {@link #scripted(Class, Supplier)} 记成<b>权重 0</b>：它们是物品目录的一部分
 * （调试取件、后续编辑器都靠这个目录），却永远不参与 {@link #random()} 的平铺抽取。</p>
 */
public final class ItemRegistry {

	private ItemRegistry() {
	}

	public static final class Entry {

		private final Class<? extends Item> type;
		private Supplier<? extends Item> factory;
		private boolean unique;
		private int weight;

		//生成时逐项应用的修饰钩子，供后期物品修改注入（先全部应用，再调 item.random()）
		private final List<UnaryOperator<Item>> modifiers = new ArrayList<>();

		private Entry(Class<? extends Item> type, Supplier<? extends Item> factory, boolean unique, int weight) {
			this.type = type;
			this.factory = factory;
			this.unique = unique;
			this.weight = weight;
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

		public boolean unique() {
			return unique;
		}

		//三档权重：常见 10 / 罕见 4 / 稀有 1；unique 条目固定稀有档
		public int weight() {
			return weight;
		}

		public Entry weight(int weight) {
			this.weight = weight;
			return this;
		}

		public Entry factory(Supplier<? extends Item> factory) {
			this.factory = factory;
			return this;
		}

		public Entry unique(boolean unique) {
			this.unique = unique;
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

	private static Entry item(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, false, 10);
	}

	private static Entry uncommon(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, false, 4);
	}

	private static Entry rare(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, false, 1);
	}

	private static Entry unique(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, true, 1);
	}

	/**
	 * 脚本化/保底发放的物品：登记进物品目录以便查阅与调试取件，权重 0，永不参与平铺随机
	 * （{@link #random()} 的求和不计 0 权重，滚动抽取也永远不会落在它身上）。
	 */
	private static Entry scripted(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, false, 0);
	}

	/** 初始清单（不可变快照），供 {@link #resetToDefaults()} 还原。 */
	private static final List<Entry> DEFAULTS = Collections.unmodifiableList(Arrays.asList(
			// Core items（Amulet/王冠/面具/开局物等仅脚本化来源，不入池）
			uncommon(Ankh.class, Ankh::new),
			uncommon(ArcaneResin.class, ArcaneResin::new),
			uncommon(Dewdrop.class, Dewdrop::new),
			uncommon(EnergyCrystal.class, EnergyCrystal::new),
			item(Gold.class, Gold::new),
			uncommon(Honeypot.class, Honeypot::new),
			uncommon(LiquidMetal.class, LiquidMetal::new),
			uncommon(Stylus.class, Stylus::new),
			uncommon(Torch.class, Torch::new),

			// 剧情/功能件（脚本化发放，不入平铺池）
			scripted(Amulet.class, Amulet::new),
			scripted(BrokenSeal.class, BrokenSeal::new),
			scripted(KingsCrown.class, KingsCrown::new),
			scripted(TengusMask.class, TengusMask::new),
			scripted(Waterskin.class, Waterskin::new),
			scripted(Honeypot.ShatteredPot.class, Honeypot.ShatteredPot::new),

			// Armor
			item(Armor.class, Armor::new),

			// Artifacts are run-unique, kept at the rare tier
			unique(AlchemistsToolkit.class, AlchemistsToolkit::new),
			unique(ChaliceOfBlood.class, ChaliceOfBlood::new),
			unique(CloakOfShadows.class, CloakOfShadows::new),
			unique(DriedRose.class, DriedRose::new),
			unique(EtherealChains.class, EtherealChains::new),
			unique(HolyTome.class, HolyTome::new),
			unique(HornOfPlenty.class, HornOfPlenty::new),
			unique(MasterThievesArmband.class, MasterThievesArmband::new),
			unique(SandalsOfNature.class, SandalsOfNature::new),
			unique(SkeletonKey.class, SkeletonKey::new),
			unique(TalismanOfForesight.class, TalismanOfForesight::new),
			unique(TimekeepersHourglass.class, TimekeepersHourglass::new),
			unique(UnstableSpellbook.class, UnstableSpellbook::new),
			uncommon(DriedRose.Petal.class, DriedRose.Petal::new),
			uncommon(TimekeepersHourglass.sandBag.class, TimekeepersHourglass.sandBag::new),
			scripted(CapeOfThorns.class, CapeOfThorns::new),

			// Bombs（均有 Bomb+原料 炼金配方兜底）
			uncommon(Bomb.class, Bomb::new),
			uncommon(PayloadBomb.class, PayloadBomb::randomPayload),
			uncommon(HolyBomb.class, HolyBomb::new),
			uncommon(Noisemaker.class, Noisemaker::new),
			uncommon(RegrowthBomb.class, RegrowthBomb::new),
			uncommon(WoollyBomb.class, WoollyBomb::new),
			uncommon(Bomb.DoubleBomb.class, Bomb.DoubleBomb::new),

			// Food
			item(Berry.class, Berry::new),
			item(Blandfruit.class, Blandfruit::new),
			item(ChargrilledMeat.class, ChargrilledMeat::new),
			item(Food.class, Food::new),
			item(FrozenCarpaccio.class, FrozenCarpaccio::new),
			item(MeatPie.class, MeatPie::new),
			item(MysteryMeat.class, MysteryMeat::new),
			item(Pasty.class, Pasty::new),
			item(SupplyRation.class, SupplyRation::new),
			item(Pasty.FishLeftover.class, Pasty.FishLeftover::new),
			item(Blandfruit.Chunks.class, Blandfruit.Chunks::new),

			// The retained journal example is a usable pickup, not a UI placeholder
			uncommon(Guidebook.class, Guidebook::new),

			// Keys are scripted with their locks and never roll from this pool
			scripted(Key.class, Key::new),

			// 任务与遗物（脚本化，仅供查阅/调试取件）
			scripted(DarkGold.class, DarkGold::new),
			scripted(GooBlob.class, GooBlob::new),
			scripted(Pickaxe.class, Pickaxe::new),
			scripted(BowFragment.class, BowFragment::new),
			scripted(BrokenHilt.class, BrokenHilt::new),
			scripted(BrokenStaff.class, BrokenStaff::new),
			scripted(CloakScrap.class, CloakScrap::new),
			scripted(SealShard.class, SealShard::new),
			scripted(TornPage.class, TornPage::new),

			// The reduced potion family keeps one representative per core effect.
			item(PotionOfFrost.class, PotionOfFrost::new),
			item(PotionOfHaste.class, PotionOfHaste::new),
			item(PotionOfHealing.class, PotionOfHealing::new),
			item(PotionOfLevitation.class, PotionOfLevitation::new),
			item(PotionOfLiquidFlame.class, PotionOfLiquidFlame::new),
			item(PotionOfPurity.class, PotionOfPurity::new),
			item(PotionOfToxicGas.class, PotionOfToxicGas::new),
			// 力量药水由 strNeeded 每 5 层保底发放，不进平铺池
			scripted(PotionOfStrength.class, PotionOfStrength::new),

			// Rings（Ring 统一随机九种效果，deck 概率 27:3:3）
			rare(Ring.class, Ring::new),
			rare(RingOfForce.class, RingOfForce::new),
			rare(RingOfWealth.class, RingOfWealth::new),

			// Regular scrolls（SoU 由 souNeeded 保底；exotic 卷轴由炼金 ScrollToExotic 产出，均不入池）
			item(ScrollOfIdentify.class, ScrollOfIdentify::new),
			item(ScrollOfLullaby.class, ScrollOfLullaby::new),
			item(ScrollOfMagicMapping.class, ScrollOfMagicMapping::new),
			item(ScrollOfMirrorImage.class, ScrollOfMirrorImage::new),
			item(ScrollOfRage.class, ScrollOfRage::new),
			item(ScrollOfRecharging.class, ScrollOfRecharging::new),
			item(ScrollOfRemoveCurse.class, ScrollOfRemoveCurse::new),
			item(ScrollOfRetribution.class, ScrollOfRetribution::new),
			item(ScrollOfTeleportation.class, ScrollOfTeleportation::new),
			item(ScrollOfTerror.class, ScrollOfTerror::new),
			item(ScrollOfTransmutation.class, ScrollOfTransmutation::new),
			// 升级卷由 souNeeded 保底，不参与平铺随机
			scripted(ScrollOfUpgrade.class, ScrollOfUpgrade::new),

			// Exotic scrolls（由炼金 ScrollToExotic 产出，不入池）
			scripted(ScrollOfAntiMagic.class, ScrollOfAntiMagic::new),
			scripted(ScrollOfChallenge.class, ScrollOfChallenge::new),
			scripted(ScrollOfDivination.class, ScrollOfDivination::new),
			scripted(ScrollOfDread.class, ScrollOfDread::new),
			scripted(ScrollOfEnchantment.class, ScrollOfEnchantment::new),
			scripted(ScrollOfForesight.class, ScrollOfForesight::new),
			scripted(ScrollOfPassage.class, ScrollOfPassage::new),
			scripted(ScrollOfPrismaticImage.class, ScrollOfPrismaticImage::new),
			scripted(ScrollOfPsionicBlast.class, ScrollOfPsionicBlast::new),
			scripted(ScrollOfSirensSong.class, ScrollOfSirensSong::new),

			// Alchemical spells（均有 Recipe 炼金配方兜底；MagicalInfusion 仅 SoU 合成）
			uncommon(Alchemize.class, Alchemize::new),
			uncommon(BeaconOfReturning.class, BeaconOfReturning::new),
			uncommon(CurseInfusion.class, CurseInfusion::new),
			uncommon(PhaseShift.class, PhaseShift::new),
			uncommon(ReclaimTrap.class, ReclaimTrap::new),
			uncommon(Recycle.class, Recycle::new),
			uncommon(SummonElemental.class, SummonElemental::new),
			uncommon(TelekineticGrab.class, TelekineticGrab::new),
			uncommon(UnstableSpell.class, UnstableSpell::new),
			uncommon(WildEnergy.class, WildEnergy::new),
			scripted(MagicalInfusion.class, MagicalInfusion::new),

			// Runestones（Enchantment 由 6-19 层保底/合成；Augmentation 每商店 1 个/合成，均不入池）
			item(StoneOfAggression.class, StoneOfAggression::new),
			item(StoneOfBlast.class, StoneOfBlast::new),
			item(StoneOfBlink.class, StoneOfBlink::new),
			item(StoneOfClairvoyance.class, StoneOfClairvoyance::new),
			item(StoneOfDeepSleep.class, StoneOfDeepSleep::new),
			item(StoneOfDetectMagic.class, StoneOfDetectMagic::new),
			item(StoneOfFear.class, StoneOfFear::new),
			item(StoneOfFlock.class, StoneOfFlock::new),
			item(StoneOfIntuition.class, StoneOfIntuition::new),
			item(StoneOfShock.class, StoneOfShock::new),
			// 附魔石 6-19 层保底、强化石每商店 1 个（均不入池）；冰霜附魔石为 chapinit 内容
			scripted(StoneOfEnchantment.class, StoneOfEnchantment::new),
			scripted(StoneOfAugmentation.class, StoneOfAugmentation::new),
			scripted(StoneOfFrostEnchantment.class, StoneOfFrostEnchantment::new),

			// Trinkets are run-unique；催化剂由 1-4 层保底发放，不入池
			unique(ChaoticCenser.class, ChaoticCenser::new),
			unique(CrackedSpyglass.class, CrackedSpyglass::new),
			unique(DimensionalSundial.class, DimensionalSundial::new),
			unique(ExoticCrystals.class, ExoticCrystals::new),
			unique(EyeOfNewt.class, EyeOfNewt::new),
			unique(FerretTuft.class, FerretTuft::new),
			unique(MossyClump.class, MossyClump::new),
			unique(ParchmentScrap.class, ParchmentScrap::new),
			unique(PetrifiedSeed.class, PetrifiedSeed::new),
			unique(SaltCube.class, SaltCube::new),
			unique(ShardOfOblivion.class, ShardOfOblivion::new),
			unique(ThirteenLeafClover.class, ThirteenLeafClover::new),
			unique(TrapMechanism.class, TrapMechanism::new),
			unique(VialOfBlood.class, VialOfBlood::new),
			unique(WondrousResin.class, WondrousResin::new),
			scripted(TrinketCatalyst.class, TrinketCatalyst::new),

			// Wands（充能武器，稀有档）
			rare(WandOfBlastWave.class, WandOfBlastWave::new),
			rare(WandOfCorrosion.class, WandOfCorrosion::new),
			rare(WandOfCorruption.class, WandOfCorruption::new),
			rare(WandOfDisintegration.class, WandOfDisintegration::new),
			rare(WandOfFireblast.class, WandOfFireblast::new),
			rare(WandOfFrost.class, WandOfFrost::new),
			rare(WandOfLightning.class, WandOfLightning::new),
			rare(WandOfLivingEarth.class, WandOfLivingEarth::new),
			rare(WandOfMagicMissile.class, WandOfMagicMissile::new),
			rare(WandOfPrismaticLight.class, WandOfPrismaticLight::new),
			rare(WandOfRegrowth.class, WandOfRegrowth::new),
			rare(WandOfTransfusion.class, WandOfTransfusion::new),
			rare(WandOfWarding.class, WandOfWarding::new),

			// Melee weapons（一般武入池：盗贼开局已换 WornDagger，Dagger 本体转为纯池武器；
			// Sword 战士开局已换 WornShortsword，通用剑作为"普通武器"位入池；
			// Crossbow 属远程弹药系武器，只在平铺池稀有档掉落，不进 WEP deck）
			item(Dagger.class, Dagger::new),
			item(Flail.class, Flail::new),
			item(RoundShield.class, RoundShield::new),
			item(Scimitar.class, Scimitar::new),
			item(Sickle.class, Sickle::new),
			item(Spear.class, Spear::new),
			item(Sword.class, Sword::new),
			rare(Crossbow.class, Crossbow::new),

			// 英雄开局武（HeroClass 发放，不入池；调试取件与编辑器需要能查到）
			scripted(Cudgel.class, Cudgel::new),
			scripted(Knuckleduster.class, Knuckleduster::new),
			scripted(MagesStaff.class, () -> new MagesStaff(new WandOfMagicMissile())),
			scripted(Rapier.class, Rapier::new),
			scripted(SpiritBow.class, SpiritBow::new),
			scripted(WornDagger.class, WornDagger::new),
			scripted(WornShortsword.class, WornShortsword::new),

			// Zoot 移植武（仅调试生成）
			scripted(AscalonAOE.class, AscalonAOE::new),
			scripted(Chakram.class, Chakram::new),
			scripted(DeployablewCrossBow.class, DeployablewCrossBow::new),
			scripted(HeavyBow.class, HeavyBow::new),
			scripted(PhantomKnife.class, PhantomKnife::new),

			// Thrown weapons and darts（TippedDart 需先有 Dart 上毒/蘸药）
			item(Bolas.class, Bolas::new),
			item(ForceCube.class, ForceCube::new),
			item(HeavyBoomerang.class, HeavyBoomerang::new),
			item(Shuriken.class, Shuriken::new),
			item(ThrowingClub.class, ThrowingClub::new),
			item(ThrowingKnife.class, ThrowingKnife::new),
			item(ThrowingStone.class, ThrowingStone::new),
			item(Tomahawk.class, Tomahawk::new),
			item(Dart.class, Dart::new),
			uncommon(TippedDart.class, () -> TippedDart.randomEffect(1)),

			// Every surviving independently plantable seed
			scripted(Rotberry.Seed.class, Rotberry.Seed::new),   //任务种子，保底发放
			item(BlandfruitBush.Seed.class, BlandfruitBush.Seed::new),
			item(Blindweed.Seed.class, Blindweed.Seed::new),
			item(Earthroot.Seed.class, Earthroot.Seed::new),
			item(Fadeleaf.Seed.class, Fadeleaf.Seed::new),
			item(Firebloom.Seed.class, Firebloom.Seed::new),
			item(Icecap.Seed.class, Icecap.Seed::new),
			item(Mageroyal.Seed.class, Mageroyal.Seed::new),
			item(Sorrowmoss.Seed.class, Sorrowmoss.Seed::new),
			item(Starflower.Seed.class, Starflower.Seed::new),
			item(Stormvine.Seed.class, Stormvine.Seed::new),
			item(Sungrass.Seed.class, Sungrass.Seed::new),
			item(Swiftthistle.Seed.class, Swiftthistle.Seed::new)
	));

	/** 活动清单（可变副本），结构性修改走 {@link #add} / {@link #remove} / {@link #replace}。 */
	private static final List<Entry> ENTRIES = new ArrayList<>(DEFAULTS);

	private static final Map<Class<? extends Item>, Entry> BY_TYPE = new LinkedHashMap<>();

	static {
		Map<Class<? extends Item>, Entry> byType = new LinkedHashMap<>();
		for (Entry entry : ENTRIES) {
			if (byType.put(entry.type, entry) != null) {
				throw new IllegalStateException("Duplicate flat item drop entry: " + entry.type.getName());
			}
		}
		BY_TYPE.putAll(byType);
	}

	//本局已生成的 unique 类型（含神器）；重开/换局由 resetRunState 清空
	private static final Set<Class<? extends Item>> generatedUniques = new LinkedHashSet<>();

	public static List<Entry> entries() {
		return ENTRIES;
	}

	public static int size() {
		return ENTRIES.size();
	}

	public static Entry get(Class<? extends Item> type) {
		return BY_TYPE.get(type);
	}

	/**
	 * Creates an explicitly registered item without reflective construction.
	 *
	 * <p>A null result means the class is a compatibility-only type (for example a
	 * dynamically supplied mob loot class) and the caller may choose its legacy
	 * fallback.</p>
	 */
	public static Item generate(Class<? extends Item> type) {
		Entry entry = BY_TYPE.get(type);
		return entry == null ? null : entry.generate();
	}

	/** 按类型构造（应用条目 modifier，不做 random）；未注册返回 null。 */
	public static Item build(Class<? extends Item> type) {
		Entry entry = BY_TYPE.get(type);
		return entry == null ? null : entry.build();
	}

	public static boolean isUniqueType(Class<? extends Item> type) {
		Entry entry = BY_TYPE.get(type);
		return entry != null && entry.unique;
	}

	/**
	 * 平铺加权抽取：常见 10 / 罕见 4 / 稀有 1，本局已生成的 unique 条目权重按 0 处理。
	 * unique 记录由本类自行维护（见 {@link #observe(Item)}）。
	 */
	public static Item random() {
		float sum = 0;
		for (Entry entry : ENTRIES) {
			if (!entry.unique || !generatedUniques.contains(entry.type)) {
				sum += entry.weight;
			}
		}

		if (sum <= 0) {
			throw new IllegalStateException("The flat item registry has no available entries");
		}

		float roll = Random.Float(sum);
		for (Entry entry : ENTRIES) {
			if (entry.unique && generatedUniques.contains(entry.type)) {
				continue;
			}
			if (roll < entry.weight) {
				Item result = entry.generate();
				//unique 条目按条目类型记账（工厂可能返回子类实例），其余按实例类型
				if (entry.unique) {
					generatedUniques.add(entry.type);
				}
				observe(result);
				return result;
			}
			roll -= entry.weight;
		}

		throw new IllegalStateException("The flat item registry changed during selection");
	}

	/**
	 * 记录本局已出现的 unique 物品（含从存档/地面/背包里观察到的），使其不再参与随机抽取。
	 * 判定沿用原来的语义：条目 unique、实例 unique、或本身是神器。
	 */
	@SuppressWarnings("unchecked")
	public static void observe(Item item) {
		if (item == null) return;
		Class<? extends Item> type = (Class<? extends Item>) item.getClass();
		if (item.unique || item instanceof Artifact || isUniqueType(type)) {
			generatedUniques.add(type);
		}
	}

	public static boolean isGeneratedUnique(Class<? extends Item> type) {
		return generatedUniques.contains(type);
	}

	/** 本局已生成的 unique 类型（存档用，只读视图）。 */
	public static Set<Class<? extends Item>> generatedUniques() {
		return generatedUniques;
	}

	/** 读档时恢复本局 unique 记录；非物品类型与 null 静默跳过。 */
	@SuppressWarnings("unchecked")
	public static void observeTypes(Class<?>[] types) {
		if (types == null) return;
		for (Class<?> type : types) {
			if (type != null && Item.class.isAssignableFrom(type)) {
				generatedUniques.add((Class<? extends Item>) type);
			}
		}
	}

	/** 换局/重开时清空本局 unique 记录（条目本身的修改有意保留）。 */
	public static void resetRunState() {
		generatedUniques.clear();
	}

	//========== 可修改项 ==========

	/** 追加一条（重复类型抛异常）。 */
	public static Entry add(Class<? extends Item> type, Supplier<? extends Item> factory, boolean unique, int weight) {
		if (BY_TYPE.containsKey(type)) {
			throw new IllegalStateException("Duplicate flat item drop entry: " + type.getName());
		}
		Entry entry = new Entry(type, factory, unique, weight);
		ENTRIES.add(entry);
		BY_TYPE.put(type, entry);
		return entry;
	}

	/** 移除一条；未注册则返回 null。 */
	public static Entry remove(Class<? extends Item> type) {
		Entry entry = BY_TYPE.remove(type);
		if (entry != null) {
			ENTRIES.remove(entry);
		}
		return entry;
	}

	/** 用同类型的新条目替换旧的（保留原位置）。 */
	public static void replace(Entry entry) {
		Entry old = BY_TYPE.put(entry.type, entry);
		if (old == null) {
			throw new IllegalStateException("Cannot replace unregistered item entry: " + entry.type.getName());
		}
		ENTRIES.set(ENTRIES.indexOf(old), entry);
	}

	/** 还原成初始清单（丢弃全部 add/remove/条目改动）。 */
	public static void resetToDefaults() {
		ENTRIES.clear();
		ENTRIES.addAll(DEFAULTS);
		BY_TYPE.clear();
		Set<Class<? extends Item>> types = new HashSet<>();
		for (Entry entry : ENTRIES) {
			if (!types.add(entry.type)) {
				throw new IllegalStateException("Duplicate flat item drop entry: " + entry.type.getName());
			}
			BY_TYPE.put(entry.type, entry);
		}
	}
}
