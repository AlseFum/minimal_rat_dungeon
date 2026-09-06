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

import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.*;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The single, floor-independent item drop registry used by {@link Loot#random()}.
 *
 * <p>This list is deliberately explicit. It gives every independently usable gameplay
 * item an explicitly weighted factory (三档权重：常见 10 / 罕见 4 / 稀有 1), makes
 * deletions fail visibly at compile time, avoids runtime classpath scanning, and does
 * not rely on reflective construction. Unique entries are fixed at the rare tier and
 * are skipped by {@link Loot#random()} once generated this run. Items that only spawn
 * through scripted channels (quest rewards, guaranteed drops, alchemy, hero starts)
 * are intentionally absent from this list.</p>
 */
public final class ItemDropRegistry {

	private ItemDropRegistry() {
	}

	public static final class Entry {

		private final Class<? extends Item> type;
		private final Supplier<? extends Item> factory;
		private final boolean unique;
		private final int weight;

		private Entry(Class<? extends Item> type, Supplier<? extends Item> factory, boolean unique, int weight) {
			this.type = type;
			this.factory = factory;
			this.unique = unique;
			this.weight = weight;
		}

		public Class<? extends Item> type() {
			return type;
		}

		public Item create() {
			return factory.get();
		}

		public boolean unique() {
			return unique;
		}

		//三档权重：常见 10 / 罕见 4 / 稀有 1；unique 条目固定稀有档
		public int weight() {
			return weight;
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

	private static final List<Entry> ENTRIES = Collections.unmodifiableList(Arrays.asList(
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

			// The reduced potion family keeps one representative per core effect.
			item(PotionOfFrost.class, PotionOfFrost::new),
			item(PotionOfHaste.class, PotionOfHaste::new),
			item(PotionOfHealing.class, PotionOfHealing::new),
			item(PotionOfLevitation.class, PotionOfLevitation::new),
			item(PotionOfLiquidFlame.class, PotionOfLiquidFlame::new),
			item(PotionOfPurity.class, PotionOfPurity::new),
			item(PotionOfToxicGas.class, PotionOfToxicGas::new),

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

			// Melee weapons（一般武器；开局武器由 HeroClass 职业开局发放，不入掉落池）
			item(Crossbow.class, Crossbow::new),
			item(Flail.class, Flail::new),
			item(RoundShield.class, RoundShield::new),
			item(Scimitar.class, Scimitar::new),
			item(Sickle.class, Sickle::new),
			item(Spear.class, Spear::new),

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

	private static final Map<Class<? extends Item>, Entry> BY_TYPE;

	static {
		Set<Class<? extends Item>> types = new HashSet<>();
		Map<Class<? extends Item>, Entry> byType = new LinkedHashMap<>();
		for (Entry entry : ENTRIES) {
			if (!types.add(entry.type)) {
				throw new IllegalStateException("Duplicate flat item drop entry: " + entry.type.getName());
			}
			byType.put(entry.type, entry);
		}
		BY_TYPE = Collections.unmodifiableMap(byType);
	}

	public static List<Entry> entries() {
		return ENTRIES;
	}

	public static int size() {
		return ENTRIES.size();
	}

	/**
	 * Creates an explicitly registered item without reflective construction.
	 *
	 * <p>A null result means the class is a compatibility-only type (for example a
	 * dynamically supplied mob loot class) and the caller may choose its legacy
	 * fallback.</p>
	 */
	static Item create(Class<? extends Item> type) {
		Entry entry = BY_TYPE.get(type);
		return entry == null ? null : entry.create();
	}

	static boolean isUniqueType(Class<? extends Item> type) {
		Entry entry = BY_TYPE.get(type);
		return entry != null && entry.unique;
	}
}
