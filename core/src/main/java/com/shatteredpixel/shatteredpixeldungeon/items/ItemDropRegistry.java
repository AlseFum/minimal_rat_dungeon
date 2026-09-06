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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
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
 * item one equally weighted factory, makes deletions fail visibly at compile time, avoids
 * runtime classpath scanning, and does not rely on reflective construction.</p>
 */
public final class ItemDropRegistry {

	private ItemDropRegistry() {
	}

	public static final class Entry {

		private final Class<? extends Item> type;
		private final Supplier<? extends Item> factory;
		private final boolean unique;

		private Entry(Class<? extends Item> type, Supplier<? extends Item> factory, boolean unique) {
			this.type = type;
			this.factory = factory;
			this.unique = unique;
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
	}

	private static Entry item(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, false);
	}

	private static Entry unique(Class<? extends Item> type, Supplier<? extends Item> factory) {
		return new Entry(type, factory, true);
	}

	private static final List<Entry> ENTRIES = Collections.unmodifiableList(Arrays.asList(
			// Core items
			unique(Amulet.class, Amulet::new),
			item(Ankh.class, Ankh::new),
			item(ArcaneResin.class, ArcaneResin::new),
			unique(BrokenSeal.class, BrokenSeal::new),
			item(Dewdrop.class, Dewdrop::new),
			item(EnergyCrystal.class, EnergyCrystal::new),
			item(Gold.class, Gold::new),
			item(Honeypot.class, Honeypot::new),
			unique(KingsCrown.class, KingsCrown::new),
			item(LiquidMetal.class, LiquidMetal::new),
			item(Stylus.class, Stylus::new),
			unique(TengusMask.class, TengusMask::new),
			item(Torch.class, Torch::new),
			unique(Waterskin.class, Waterskin::new),

			// Armor
			item(Armor.class, Armor::new),

			// Artifacts and their independently collectible upgrade items
			unique(AlchemistsToolkit.class, AlchemistsToolkit::new),
			unique(CapeOfThorns.class, CapeOfThorns::new),
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
			item(DriedRose.Petal.class, DriedRose.Petal::new),
			item(TimekeepersHourglass.sandBag.class, TimekeepersHourglass.sandBag::new),

			// Bombs
			item(Bomb.class, Bomb::new),
			item(PayloadBomb.class, PayloadBomb::randomPayload),
			item(HolyBomb.class, HolyBomb::new),
			item(Noisemaker.class, Noisemaker::new),
			item(RegrowthBomb.class, RegrowthBomb::new),
			item(WoollyBomb.class, WoollyBomb::new),
			item(Bomb.DoubleBomb.class, Bomb.DoubleBomb::new),

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
			item(Guidebook.class, Guidebook::new),

			// Keys use the current floor so every randomly dropped key is functional
			unique(Key.class, () -> Key.random(Dungeon.depth)),

			// The reduced potion family keeps one representative per core effect.
			item(PotionOfFrost.class, PotionOfFrost::new),
			item(PotionOfHaste.class, PotionOfHaste::new),
			item(PotionOfHealing.class, PotionOfHealing::new),
			item(PotionOfLevitation.class, PotionOfLevitation::new),
			item(PotionOfLiquidFlame.class, PotionOfLiquidFlame::new),
			item(PotionOfPurity.class, PotionOfPurity::new),
			item(PotionOfToxicGas.class, PotionOfToxicGas::new),

			// Quest and boss materials
			unique(DarkGold.class, DarkGold::new),
			item(GooBlob.class, GooBlob::new),
			unique(Pickaxe.class, Pickaxe::new),

			// Hero-remains rewards
			item(BowFragment.class, BowFragment::new),
			item(BrokenHilt.class, BrokenHilt::new),
			item(BrokenStaff.class, BrokenStaff::new),
			item(CloakScrap.class, CloakScrap::new),
			item(SealShard.class, SealShard::new),
			item(TornPage.class, TornPage::new),

			// Rings
			item(Ring.class, Ring::new),
			item(RingOfForce.class, RingOfForce::new),
			item(RingOfWealth.class, RingOfWealth::new),

			// Scrolls and exotic scrolls
			item(ScrollOfAntiMagic.class, ScrollOfAntiMagic::new),
			item(ScrollOfChallenge.class, ScrollOfChallenge::new),
			item(ScrollOfDivination.class, ScrollOfDivination::new),
			item(ScrollOfDread.class, ScrollOfDread::new),
			unique(ScrollOfEnchantment.class, ScrollOfEnchantment::new),
			item(ScrollOfForesight.class, ScrollOfForesight::new),
			item(ScrollOfPassage.class, ScrollOfPassage::new),
			item(ScrollOfPrismaticImage.class, ScrollOfPrismaticImage::new),
			item(ScrollOfPsionicBlast.class, ScrollOfPsionicBlast::new),
			item(ScrollOfSirensSong.class, ScrollOfSirensSong::new),
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
			unique(ScrollOfUpgrade.class, ScrollOfUpgrade::new),

			// Alchemical spells
			item(Alchemize.class, Alchemize::new),
			item(BeaconOfReturning.class, BeaconOfReturning::new),
			item(CurseInfusion.class, CurseInfusion::new),
			unique(MagicalInfusion.class, MagicalInfusion::new),
			item(PhaseShift.class, PhaseShift::new),
			item(ReclaimTrap.class, ReclaimTrap::new),
			item(Recycle.class, Recycle::new),
			item(SummonElemental.class, SummonElemental::new),
			item(TelekineticGrab.class, TelekineticGrab::new),
			item(UnstableSpell.class, UnstableSpell::new),
			item(WildEnergy.class, WildEnergy::new),

			// Runestones
			item(StoneOfAggression.class, StoneOfAggression::new),
			item(StoneOfAugmentation.class, StoneOfAugmentation::new),
			item(StoneOfBlast.class, StoneOfBlast::new),
			item(StoneOfBlink.class, StoneOfBlink::new),
			item(StoneOfClairvoyance.class, StoneOfClairvoyance::new),
			item(StoneOfDeepSleep.class, StoneOfDeepSleep::new),
			item(StoneOfDetectMagic.class, StoneOfDetectMagic::new),
			unique(StoneOfEnchantment.class, StoneOfEnchantment::new),
			item(StoneOfFear.class, StoneOfFear::new),
			item(StoneOfFlock.class, StoneOfFlock::new),
			item(StoneOfIntuition.class, StoneOfIntuition::new),
			item(StoneOfShock.class, StoneOfShock::new),

			// Trinkets and their catalyst
			unique(TrinketCatalyst.class, TrinketCatalyst::new),
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

			// Wands
			item(WandOfBlastWave.class, WandOfBlastWave::new),
			item(WandOfCorrosion.class, WandOfCorrosion::new),
			item(WandOfCorruption.class, WandOfCorruption::new),
			item(WandOfDisintegration.class, WandOfDisintegration::new),
			item(WandOfFireblast.class, WandOfFireblast::new),
			item(WandOfFrost.class, WandOfFrost::new),
			item(WandOfLightning.class, WandOfLightning::new),
			item(WandOfLivingEarth.class, WandOfLivingEarth::new),
			item(WandOfMagicMissile.class, WandOfMagicMissile::new),
			item(WandOfPrismaticLight.class, WandOfPrismaticLight::new),
			item(WandOfRegrowth.class, WandOfRegrowth::new),
			item(WandOfTransfusion.class, WandOfTransfusion::new),
			item(WandOfWarding.class, WandOfWarding::new),

			// Melee weapons（一般武器；开局武器由 HeroClass 职业开局发放，不入掉落池）
			item(Crossbow.class, Crossbow::new),
			item(Flail.class, Flail::new),
			item(RoundShield.class, RoundShield::new),
			item(Scimitar.class, Scimitar::new),
			item(Sickle.class, Sickle::new),
			item(Spear.class, Spear::new),

			// Thrown weapons and darts
			item(Bolas.class, Bolas::new),
			item(ForceCube.class, ForceCube::new),
			item(HeavyBoomerang.class, HeavyBoomerang::new),
			item(Shuriken.class, Shuriken::new),
			item(ThrowingClub.class, ThrowingClub::new),
			item(ThrowingKnife.class, ThrowingKnife::new),
			item(ThrowingStone.class, ThrowingStone::new),
			item(Tomahawk.class, Tomahawk::new),
			item(Dart.class, Dart::new),
			item(TippedDart.class, () -> TippedDart.randomEffect(1)),
			unique(SpiritBow.class, SpiritBow::new),

			// Every surviving independently plantable seed
			item(BlandfruitBush.Seed.class, BlandfruitBush.Seed::new),
			item(Blindweed.Seed.class, Blindweed.Seed::new),
			item(Earthroot.Seed.class, Earthroot.Seed::new),
			item(Fadeleaf.Seed.class, Fadeleaf.Seed::new),
			item(Firebloom.Seed.class, Firebloom.Seed::new),
			item(Icecap.Seed.class, Icecap.Seed::new),
			item(Mageroyal.Seed.class, Mageroyal.Seed::new),
			unique(Rotberry.Seed.class, Rotberry.Seed::new),
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
