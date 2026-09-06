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
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.CrackedSpyglass;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ThirteenLeafClover;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
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
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Loot {

	//========== 物品类别 ==========
	//类别对外只是字符串句柄（常量）。池内容与运行状态全部由本类内部维护。

	//类别配置表：必须声明在常量之前（常量初始化时会写入它）
	private static final LinkedHashMap<String, CatData> DATA = new LinkedHashMap<>();

	public static final String TRINKET  = cat("TRINKET", 0, 0, Trinket.class);
	public static final String WEAPON   = cat("WEAPON", 2, 2, MeleeWeapon.class);
	public static final String WEP_T1   = cat("WEP_T1", 0, 0, MeleeWeapon.class);
	public static final String WEP_T2   = cat("WEP_T2", 0, 0, MeleeWeapon.class);
	public static final String WEP_T3   = cat("WEP_T3", 0, 0, MeleeWeapon.class);
	public static final String WEP_T4   = cat("WEP_T4", 0, 0, MeleeWeapon.class);
	public static final String WEP_T5   = cat("WEP_T5", 0, 0, MeleeWeapon.class);
	public static final String ARMOR    = cat("ARMOR", 2, 1, Armor.class );
	public static final String MISSILE  = cat("MISSILE", 1, 2, MissileWeapon.class );
	public static final String MIS_T1   = cat("MIS_T1", 0, 0, MissileWeapon.class );
	public static final String MIS_T2   = cat("MIS_T2", 0, 0, MissileWeapon.class );
	public static final String MIS_T3   = cat("MIS_T3", 0, 0, MissileWeapon.class );
	public static final String MIS_T4   = cat("MIS_T4", 0, 0, MissileWeapon.class );
	public static final String MIS_T5   = cat("MIS_T5", 0, 0, MissileWeapon.class );
	public static final String WAND     = cat("WAND", 1, 1, Wand.class );
	public static final String RING     = cat("RING", 1, 0, Ring.class );
	public static final String ARTIFACT = cat("ARTIFACT", 0, 1, Artifact.class);
	public static final String FOOD     = cat("FOOD", 0, 0, Food.class );
	public static final String POTION   = cat("POTION", 8, 8, Potion.class );
	public static final String SEED     = cat("SEED", 1, 1, Plant.Seed.class );
	public static final String SCROLL   = cat("SCROLL", 8, 8, Scroll.class );
	public static final String STONE    = cat("STONE", 1, 1, Runestone.class);
	public static final String GOLD     = cat("GOLD", 10, 10, Gold.class );

	//声明顺序即物品排序与存档序号，勿动
	private static final String[] ALL = { TRINKET, WEAPON, WEP_T1, WEP_T2, WEP_T3, WEP_T4, WEP_T5,
		ARMOR, MISSILE, MIS_T1, MIS_T2, MIS_T3, MIS_T4, MIS_T5,
		WAND, RING, ARTIFACT, FOOD, POTION, SEED, SCROLL, STONE, GOLD };

	//类别配置（静态初始化后只读）：每类物品清单与模板概率
	private static final class CatData {
		Class<?>[] classes;
		float[] defaultProbs = null;
		float[] defaultProbs2 = null;
		float[] defaultProbsTotal = null;
		float firstProb;
		float secondProb;
		Class<? extends Item> superClass;
	}

	//取得（必要时新建）类别配置
	private static CatData of( String cat ){
		return DATA.computeIfAbsent( cat, k -> new CatData() );
	}

	//类别常量注册：把模板概率并入配置表后返回句柄
	private static String cat( String name, float firstProb, float secondProb, Class<? extends Item> superClass ){
		CatData d = of( name );
		d.firstProb = firstProb;
		d.secondProb = secondProb;
		d.superClass = superClass;
		return name;
	}

	//运行期 deck 状态：按类别独立维护，不挂在句柄常量上
	private static final class Deck {
		float[] probs;
		boolean using2ndProbs = false;
		Long seed = null;
		int dropped = 0;
	}
	private static final HashMap<String, Deck> decks = new HashMap<>();

	private static Deck deck( String cat ){
		return decks.computeIfAbsent( cat, k -> new Deck() );
	}

	//某个类别的物品清单（原公开字段 classes 的查询入口）
	public static Class<?>[] classes( String cat ){
		return of( cat ).classes;
	}

	//某个类别两张 deck 的合计模板概率（两 deck 类别用）
	public static float[] defaultProbsTotal( String cat ){
		return of( cat ).defaultProbsTotal;
	}

	//some generator categories can have ordering within that category as well
	// note that sub category ordering doesn't need to always include items that belong
	// to that categories superclass, e.g. bombs are ordered within thrown weapons
	private static HashMap<Class, ArrayList<Class>> subOrderings = new HashMap<>();
	static {
		subOrderings.put(Trinket.class, new ArrayList<>(Arrays.asList(Trinket.class, TrinketCatalyst.class)));
		subOrderings.put(MissileWeapon.class, new ArrayList<>(Arrays.asList(MissileWeapon.class, Bomb.class)));
		subOrderings.put(Potion.class, new ArrayList<>(Arrays.asList(Waterskin.class, Potion.class, LiquidMetal.class)));
		subOrderings.put(Scroll.class, new ArrayList<>(Arrays.asList(Scroll.class, ExoticScroll.class, Spell.class, ArcaneResin.class)));
	}

	//in case there are multiple matches, this will return the latest match
	public static int order( Item item ) {
		int catResult = -1, subResult = 0;
		for (int i=0; i < ALL.length; i++) {
			ArrayList<Class> subOrdering = subOrderings.get(of(ALL[i]).superClass);
			if (subOrdering != null){
				for (int j=0; j < subOrdering.size(); j++){
					if (subOrdering.get(j).isInstance(item)){
						catResult = i;
						subResult = j;
					}
				}
			} else {
				if (of(ALL[i]).superClass.isInstance(item)) {
					catResult = i;
					subResult = 0;
				}
			}
		}
		if (catResult != -1) return catResult*100 + subResult;

		//items without a category-defined order are sorted based on the spritesheet
		return Short.MAX_VALUE+item.image();
	}

		static {
			of("GOLD").classes = new Class<?>[]{
					Gold.class };
			deck("GOLD").probs = new float[]{ 1 };
			
			of("POTION").classes = new Class<?>[]{
					PotionOfHealing.class,
					PotionOfFrost.class,
					PotionOfLiquidFlame.class,
					PotionOfToxicGas.class,
					PotionOfHaste.class,
					PotionOfLevitation.class,
					PotionOfPurity.class};
			of("POTION").defaultProbs  = new float[]{ 1, 1, 1, 1, 1, 1, 1 };
			deck("POTION").probs = of("POTION").defaultProbs.clone();
			
			of("SEED").classes = new Class<?>[]{
					Rotberry.Seed.class, //quest item
					Sungrass.Seed.class,
					Fadeleaf.Seed.class,
					Icecap.Seed.class,
					Firebloom.Seed.class,
					Sorrowmoss.Seed.class,
					Swiftthistle.Seed.class,
					Blindweed.Seed.class,
					Stormvine.Seed.class,
					Earthroot.Seed.class,
					Mageroyal.Seed.class,
					Starflower.Seed.class};
			of("SEED").defaultProbs = new float[]{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1 };
			deck("SEED").probs = of("SEED").defaultProbs.clone();
			
			of("SCROLL").classes = new Class<?>[]{
					ScrollOfUpgrade.class, //3 drop every chapter, see Dungeon.souNeeded()
					ScrollOfIdentify.class,
					ScrollOfRemoveCurse.class,
					ScrollOfMirrorImage.class,
					ScrollOfRecharging.class,
					ScrollOfTeleportation.class,
					ScrollOfLullaby.class,
					ScrollOfMagicMapping.class,
					ScrollOfRage.class,
					ScrollOfRetribution.class,
					ScrollOfTerror.class,
					ScrollOfTransmutation.class
			};
			of("SCROLL").defaultProbs  = new float[]{ 0, 3, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1 };
			of("SCROLL").defaultProbs2 = new float[]{ 0, 3, 2, 2, 1, 2, 1, 1, 1, 1, 1, 0 };
			deck("SCROLL").probs = of("SCROLL").defaultProbs.clone();
			
			of("STONE").classes = new Class<?>[]{
					StoneOfEnchantment.class,   //1 is guaranteed to drop on floors 6-19
					StoneOfIntuition.class,     //1 additional stone is also dropped on floors 1-3
					StoneOfDetectMagic.class,
					StoneOfFlock.class,
					StoneOfShock.class,
					StoneOfBlink.class,
					StoneOfDeepSleep.class,
					StoneOfClairvoyance.class,
					StoneOfAggression.class,
					StoneOfBlast.class,
					StoneOfFear.class,
					StoneOfAugmentation.class  //1 is sold in each shop
			};
			of("STONE").defaultProbs = new float[]{ 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0 };
			deck("STONE").probs = of("STONE").defaultProbs.clone();

			of("WAND").classes = new Class<?>[]{
					WandOfMagicMissile.class,
					WandOfLightning.class,
					WandOfDisintegration.class,
					WandOfFireblast.class,
					WandOfCorrosion.class,
					WandOfBlastWave.class,
					WandOfLivingEarth.class,
					WandOfFrost.class,
					WandOfPrismaticLight.class,
					WandOfWarding.class,
					WandOfTransfusion.class,
					WandOfCorruption.class,
					WandOfRegrowth.class };
			of("WAND").defaultProbs = new float[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
			deck("WAND").probs = of("WAND").defaultProbs.clone();
			
			//see generator.randomWeapon
			of("WEAPON").classes = new Class<?>[]{};
			deck("WEAPON").probs = new float[]{};
			
			//一般武器（开局武器由职业发放，不入此池）。浮动强度见 randomWeapon/floatingTier。
			of("WEP_T1").classes = new Class<?>[]{
					Spear.class,
					RoundShield.class,
					Sickle.class,
					Crossbow.class,
					Flail.class,
					Scimitar.class
			};
			of("WEP_T1").defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1 };
			deck("WEP_T1").probs = of("WEP_T1").defaultProbs.clone();
			
			of("WEP_T2").classes = of("WEP_T1").classes;
			of("WEP_T2").defaultProbs = of("WEP_T1").defaultProbs.clone();
			deck("WEP_T2").probs = of("WEP_T2").defaultProbs.clone();
			
			of("WEP_T3").classes = of("WEP_T1").classes;
			of("WEP_T3").defaultProbs = of("WEP_T1").defaultProbs.clone();
			deck("WEP_T3").probs = of("WEP_T3").defaultProbs.clone();
			
			of("WEP_T4").classes = of("WEP_T1").classes;
			of("WEP_T4").defaultProbs = of("WEP_T1").defaultProbs.clone();
			deck("WEP_T4").probs = of("WEP_T4").defaultProbs.clone();
			
			of("WEP_T5").classes = of("WEP_T1").classes;
			of("WEP_T5").defaultProbs = of("WEP_T1").defaultProbs.clone();
			deck("WEP_T5").probs = of("WEP_T5").defaultProbs.clone();
			
			//see Generator.randomArmor
			of("ARMOR").classes = new Class<?>[]{ Armor.class };
			deck("ARMOR").probs = new float[]{ 1 };
			
			//see Generator.randomMissile
			of("MISSILE").classes = new Class<?>[]{};
			deck("MISSILE").probs = new float[]{};
			
			of("MIS_T1").classes = new Class<?>[]{
					ThrowingStone.class,
					ThrowingKnife.class,
					ThrowingClub.class,
					Bolas.class,
					ForceCube.class,
					HeavyBoomerang.class,
					Shuriken.class,
					Tomahawk.class,
					Dart.class
			};
			of("MIS_T1").defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			deck("MIS_T1").probs = of("MIS_T1").defaultProbs.clone();
			
			of("MIS_T2").classes = of("MIS_T1").classes;
			of("MIS_T2").defaultProbs = of("MIS_T1").defaultProbs.clone();
			deck("MIS_T2").probs = of("MIS_T2").defaultProbs.clone();
			
			of("MIS_T3").classes = of("MIS_T1").classes;
			of("MIS_T3").defaultProbs = of("MIS_T1").defaultProbs.clone();
			deck("MIS_T3").probs = of("MIS_T3").defaultProbs.clone();
			
			of("MIS_T4").classes = of("MIS_T1").classes;
			of("MIS_T4").defaultProbs = of("MIS_T1").defaultProbs.clone();
			deck("MIS_T4").probs = of("MIS_T4").defaultProbs.clone();
			
			of("MIS_T5").classes = of("MIS_T1").classes;
			of("MIS_T5").defaultProbs = of("MIS_T1").defaultProbs.clone();
			deck("MIS_T5").probs = of("MIS_T5").defaultProbs.clone();
			
			of("FOOD").classes = new Class<?>[]{
					Food.class,
					Pasty.class,
					MysteryMeat.class };
			of("FOOD").defaultProbs = new float[]{ 4, 1, 0 };
			deck("FOOD").probs = of("FOOD").defaultProbs.clone();
			
			of("RING").classes = new Class<?>[]{
					Ring.class,
					RingOfForce.class,
					RingOfWealth.class};
			// Ring uniformly selects one of nine effects, so this preserves the
			// old per-identity 3:3:...:3 generation weight.
			of("RING").defaultProbs = new float[]{ 27, 3, 3 };
			deck("RING").probs = of("RING").defaultProbs.clone();
			
			of("ARTIFACT").classes = new Class<?>[]{
					AlchemistsToolkit.class,
					ChaliceOfBlood.class,
					CloakOfShadows.class,
					DriedRose.class,
					EtherealChains.class,
					HolyTome.class,
					HornOfPlenty.class,
					MasterThievesArmband.class,
					SandalsOfNature.class,
					SkeletonKey.class,
					TalismanOfForesight.class,
					TimekeepersHourglass.class,
					UnstableSpellbook.class
			};
			of("ARTIFACT").defaultProbs = new float[]{ 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1 };
			deck("ARTIFACT").probs = of("ARTIFACT").defaultProbs.clone();

			//Trinkets are unique like artifacts, but unlike them you can only have one at once
			//So we don't need the same enforcement of uniqueness
			of("TRINKET").classes = new Class<?>[]{
					ParchmentScrap.class,
					PetrifiedSeed.class,
					ExoticCrystals.class,
					MossyClump.class,
					DimensionalSundial.class,
					ThirteenLeafClover.class,
					TrapMechanism.class,
					WondrousResin.class,
					EyeOfNewt.class,
					SaltCube.class,
					VialOfBlood.class,
					ShardOfOblivion.class,
					ChaoticCenser.class,
					FerretTuft.class,
					CrackedSpyglass.class
			};
			of("TRINKET").defaultProbs = new float[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			deck("TRINKET").probs = of("TRINKET").defaultProbs.clone();

			for (String cat : ALL){
				if (of(cat).defaultProbs2 != null){
					of(cat).defaultProbsTotal = new float[of(cat).defaultProbs.length];
					for (int i = 0; i < of(cat).defaultProbs.length; i++){
						of(cat).defaultProbsTotal[i] = of(cat).defaultProbs[i] + of(cat).defaultProbs2[i];
					}
				}
			}
		}


	private static boolean usingFirstDeck = false;
	private static HashMap<String,Float> defaultCatProbs = new LinkedHashMap<>();
	private static HashMap<String,Float> categoryProbs = new LinkedHashMap<>();

	private static final LinkedHashSet<Class<? extends Item>> generatedUniqueItems = new LinkedHashSet<>();

	public static void fullReset() {
		generatedUniqueItems.clear();
		usingFirstDeck = Random.Int(2) == 0;
		generalReset();
		for (String cat : ALL) {
			deck(cat).using2ndProbs =  of(cat).defaultProbs2 != null && Random.Int(2) == 0;
			reset(cat);
			if (of(cat).defaultProbs != null) {
				deck(cat).seed = Random.Long();
				deck(cat).dropped = 0;
			}
		}
	}

	public static void generalReset(){
		for (String cat : ALL) {
			categoryProbs.put( cat, usingFirstDeck ? of(cat).firstProb : of(cat).secondProb );
			defaultCatProbs.put( cat, of(cat).firstProb + of(cat).secondProb );
		}
	}

	public static void reset(String cat){
		if (of(cat).defaultProbs != null) {
			if (of(cat).defaultProbs2 != null){
				deck(cat).using2ndProbs = !deck(cat).using2ndProbs;
				deck(cat).probs = deck(cat).using2ndProbs ? of(cat).defaultProbs2.clone() : of(cat).defaultProbs.clone();
			} else {
				deck(cat).probs = of(cat).defaultProbs.clone();
			}
		}
	}

	//reverts changes to drop chances generates by this item
	//equivalent of shuffling the card back into the deck, does not preserve order!
	public static void undoDrop(Item item){
		undoDrop(item.getClass());
	}

	public static void undoDrop(Class cls){
		for (String cat : ALL){
			if (cls.isAssignableFrom(of(cat).superClass)){
				if (of(cat).defaultProbs == null) continue;
				for (int i = 0; i < of(cat).classes.length; i++){
					if (cls == of(cat).classes[i]){
						deck(cat).probs[i]++;
					}
				}
			}
		}
	}
	
	public static Item random() {
		// 平铺注册表加权抽取（常见 10 / 罕见 4 / 稀有 1）；已生成的 unique 条目权重按 0 处理。
		// 近战武器会在生成后按楼层浮动预强化（见 floatingTier/applyDepthLevel）。
		syncObservedUniqueItems();

		float sum = 0;
		for (ItemDropRegistry.Entry entry : ItemDropRegistry.entries()) {
			if (!entry.unique() || !generatedUniqueItems.contains(entry.type())) {
				sum += entry.weight();
			}
		}

		if (sum <= 0) {
			throw new IllegalStateException("The flat item drop registry has no available entries");
		}

		float roll = Random.Float(sum);
		for (ItemDropRegistry.Entry entry : ItemDropRegistry.entries()) {
			if (entry.unique() && generatedUniqueItems.contains(entry.type())) {
				continue;
			}
			if (roll < entry.weight()) {
				Item result = entry.create().random();
				applyDepthLevel(result);
				trackUniqueDrop(entry, result);
				return result;
			}
			roll -= entry.weight();
		}

		throw new IllegalStateException("The flat item drop registry changed during selection");
	}

	private static void syncObservedUniqueItems() {
		if (Dungeon.hero != null && Dungeon.hero.belongings != null) {
			for (Item item : Dungeon.hero.belongings) {
				trackObservedUnique(item);
			}
		}
		if (Dungeon.level != null && Dungeon.level.heaps != null) {
			for (Heap heap : Dungeon.level.heaps.valueList()) {
				for (Item item : heap.items) {
					trackObservedUnique(item);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void trackObservedUnique(Item item) {
		Class<? extends Item> type = (Class<? extends Item>) item.getClass();
		if (item.unique || item instanceof Artifact || ItemDropRegistry.isUniqueType(type)) {
			generatedUniqueItems.add(type);
		}
	}

	@SuppressWarnings("unchecked")
	private static void trackUniqueDrop(ItemDropRegistry.Entry entry, Item result) {
		Class<? extends Item> resultType = (Class<? extends Item>) result.getClass();
		if (entry.unique() || result.unique || result instanceof Artifact
				|| ItemDropRegistry.isUniqueType(resultType)) {
			generatedUniqueItems.add(entry.type());
			generatedUniqueItems.add(resultType);
			if (result instanceof Artifact) {
				removeArtifact((Class<? extends Artifact>) resultType);
			}
		}
	}

	//特殊格装备渠道（SecretSummoningRoom / GrassyGraveRoom）：先按权重选装备类别，
	//再走各装备类别既有的 deck 抽取（ARTIFACT 用尽时 random(cat) 自动回退戒指，不会死链）。
	private static final String[] GEAR_CATS = { WEAPON, ARMOR, MISSILE, WAND, RING, ARTIFACT, TRINKET };
	private static final float[] GEAR_CAT_WEIGHTS = { 20, 20, 15, 15, 10, 10, 10 };

	public static Item randomGear() {
		return random( GEAR_CATS[ Random.chances( GEAR_CAT_WEIGHTS ) ] );
	}

	public static Item randomUsingDefaults(){
		return randomUsingDefaults(Random.chances( defaultCatProbs ));
	}
	
	public static Item random( String cat ) {
		syncObservedUniqueItems();
		if (cat == ARMOR){
			return randomArmor();
		} else if (cat == WEAPON){
			return randomWeapon();
		} else if (cat == MISSILE){
			return randomMissile();
		} else if (cat == ARTIFACT){
			Item item = randomArtifact();
			//if we're out of artifacts, return a ring instead.
			return item != null ? item : random(RING);
		}
				if (of(cat).defaultProbs != null && deck(cat).seed != null){
					Random.pushGenerator(deck(cat).seed);
					for (int i = 0; i < deck(cat).dropped; i++) Random.Long();
				}

				int i = Random.chances(deck(cat).probs);
				if (i == -1) {
					reset(cat);
					i = Random.chances(deck(cat).probs);
				}
				if (of(cat).defaultProbs != null) deck(cat).probs[i]--;
				Class<?> itemCls = of(cat).classes[i];

				if (of(cat).defaultProbs != null && deck(cat).seed != null){
					Random.popGenerator();
					deck(cat).dropped++;
				}

				if (ExoticScroll.regToExo.containsKey(itemCls)){
					if (Random.Float() < ExoticCrystals.consumableExoticChance()){
						itemCls = ExoticScroll.regToExo.get(itemCls);
					}
				}

				return createRandom(itemCls);
	}

	//overrides any deck systems and always uses default probs
	// except for artifacts, which must always use a deck
	public static Item randomUsingDefaults( String cat ){
		if (cat == WEAPON){
			return randomWeapon(true);
		} else if (cat == MISSILE){
			return randomMissile(true);
		} else if (of(cat).defaultProbs == null || cat == ARTIFACT) {
			return random(cat);
		} else if (of(cat).defaultProbsTotal != null){
			return createRandom(of(cat).classes[Random.chances(of(cat).defaultProbsTotal)]);
		} else {
			Class<?> itemCls = of(cat).classes[Random.chances(of(cat).defaultProbs)];

			if (ExoticScroll.regToExo.containsKey(itemCls)){
				if (Random.Float() < ExoticCrystals.consumableExoticChance()){
					itemCls = ExoticScroll.regToExo.get(itemCls);
				}
			}

			return createRandom(itemCls);
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		return createRandom(cl);
	}

	/**
	 * Uses the explicit drop factory whenever possible. The reflective fallback is
	 * retained only for compatibility with dynamically supplied mob loot classes
	 * which are intentionally outside the flat drop registry.
	 */
	@SuppressWarnings("unchecked")
	private static Item createRandom(Class<?> type) {
		if (!Item.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException("Item generator received a non-item type: " + type);
		}

		Item result = ItemDropRegistry.create((Class<? extends Item>) type);
		if (result == null) {
			result = Reflection.newInstance((Class<? extends Item>) type);
		}
		if (result == null) {
			throw new IllegalStateException("Unable to construct item type: " + type.getName());
		}

		result = result.random();
		applyDepthLevel(result);
		trackObservedUnique(result);
		return result;
	}

	public static Armor randomArmor(){
		return randomArmor(0);
	}
	
	public static Armor randomArmor(int floorSet) {
		// floorSet remains in the signature for old room/reward callers; all five
		// levels intentionally draw from the same surviving armor pool.
		return (Armor) createRandom(of("ARMOR").classes[Random.Int(of("ARMOR").classes.length)]);
	}

	//武器掉落强度档：以 当前楼层/5 为基准，20% 低一档、50% 同档、30% 高一档（夹取到 [1,5]）。
	//fork: 近战武器类内 tier 已统一为 1，掉落强度差异由本档位决定的预强化等级提供。
	public static int floatingTier(){
		int base = Dungeon.depth / 5;
		float r = Random.Float();
		int tier = base;
		if (r < 0.2f){
			tier = base - 1;
		} else if (r > 0.7f){
			tier = base + 1;
		}
		return Math.min(5, Math.max(1, tier));
	}

	//按 floatingTier 给近战武器预强化（tier-1 级），覆盖 Weapon.random() 自带的随机等级。
	private static void applyDepthLevel(Item item){
		if (item instanceof MeleeWeapon){
			((MeleeWeapon) item).level(floatingTier() - 1);
		}
	}

	public static final String[] wepTiers = new String[]{
			WEP_T1,
			WEP_T2,
			WEP_T3,
			WEP_T4,
			WEP_T5
	};

	public static MeleeWeapon randomWeapon(){
		return randomWeapon(0);
	}

	public static MeleeWeapon randomWeapon(int floorSet) {
		return randomWeapon(floorSet, false);
	}

	public static MeleeWeapon randomWeapon(boolean useDefaults) {
		return randomWeapon(0, useDefaults);
	}
	
	public static MeleeWeapon randomWeapon(int floorSet, boolean useDefaults) {
		// fork: 武器类内 tier 统一为 1，WEP_T1~T5 共享同一张一般武器池；
		// floorSet 仅保留兼容，掉落的强度差异由 floatingTier 预强化（applyDepthLevel）提供。
		return (MeleeWeapon) (useDefaults
				? randomUsingDefaults(WEP_T1)
				: random(WEP_T1));
	}
	
	public static final String[] misTiers = new String[]{
			MIS_T1,
			MIS_T2,
			MIS_T3,
			MIS_T4,
			MIS_T5
	};
	
	public static MissileWeapon randomMissile(){
		return randomMissile(0);
	}

	public static MissileWeapon randomMissile(int floorSet) {
		return randomMissile(floorSet, false);
	}

	public static MissileWeapon randomMissile(boolean useDefaults) {
		return randomMissile(0, useDefaults);
	}

	public static MissileWeapon randomMissile(int floorSet, boolean useDefaults) {
		// floorSet is retained for compatibility; all surviving missile weapons are
		// available on every level in the condensed flow.
		return (MissileWeapon) (useDefaults
				? randomUsingDefaults(MIS_T1)
				: random(MIS_T1));
	}

	//enforces uniqueness of artifacts throughout a run.
	public static Artifact randomArtifact() {

		String cat = ARTIFACT;

		if (of(cat).defaultProbs != null && deck(cat).seed != null){
			Random.pushGenerator(deck(cat).seed);
			for (int i = 0; i < deck(cat).dropped; i++) Random.Long();
		}

		int i = Random.chances( deck(cat).probs );

		if (of(cat).defaultProbs != null && deck(cat).seed != null){
			Random.popGenerator();
			deck(cat).dropped++;
		}

		//if no artifacts are left, return null
		if (i == -1){
			return null;
		}

		deck(cat).probs[i]--;
		return (Artifact) createRandom(of(cat).classes[i]);

	}

	public static boolean removeArtifact(Class<?extends Artifact> artifact) {
		String cat = ARTIFACT;
		for (int i = 0; i < of(cat).classes.length; i++){
			if (of(cat).classes[i].equals(artifact) && deck(cat).probs[i] > 0) {
				deck(cat).probs[i] = 0;
				return true;
			}
		}
		return false;
	}

	private static final String FIRST_DECK = "first_deck";
	private static final String GENERAL_PROBS = "general_probs";
	private static final String CATEGORY_PROBS = "_probs";
	private static final String CATEGORY_USING_PROBS2 = "_using_probs2";
	private static final String CATEGORY_SEED = "_seed";
	private static final String CATEGORY_DROPPED = "_dropped";
	private static final String GENERATED_UNIQUE_ITEMS = "generated_unique_items";

	public static void storeInBundle(Bundle bundle) {
		bundle.put(FIRST_DECK, usingFirstDeck);
		bundle.put(GENERATED_UNIQUE_ITEMS, generatedUniqueItems.toArray(new Class<?>[0]));

		Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
		float[] storeProbs = new float[genProbs.length];
		for (int i = 0; i < storeProbs.length; i++){
			storeProbs[i] = genProbs[i];
		}
		bundle.put( GENERAL_PROBS, storeProbs);

		for (String cat : ALL){
			if (of(cat).defaultProbs == null) continue;

			bundle.put(cat.toLowerCase() + CATEGORY_PROBS, deck(cat).probs);

			if (of(cat).defaultProbs2 != null){
				bundle.put(cat.toLowerCase() + CATEGORY_USING_PROBS2, deck(cat).using2ndProbs);
			}

			if (deck(cat).seed != null) {
				bundle.put(cat.toLowerCase() + CATEGORY_SEED, deck(cat).seed);
				bundle.put(cat.toLowerCase() + CATEGORY_DROPPED, deck(cat).dropped);
			}
		}
	}

	public static void restoreFromBundle(Bundle bundle) {
		fullReset();

		if (bundle.contains(GENERATED_UNIQUE_ITEMS)) {
			for (Class<?> type : bundle.getClassArray(GENERATED_UNIQUE_ITEMS)) {
				if (type != null && Item.class.isAssignableFrom(type)) {
					generatedUniqueItems.add((Class<? extends Item>) type);
				}
			}
		}

		usingFirstDeck = bundle.getBoolean(FIRST_DECK);

		if (bundle.contains(GENERAL_PROBS)){
			float[] probs = bundle.getFloatArray(GENERAL_PROBS);
			if (probs.length == ALL.length) {
				for (int i = 0; i < probs.length; i++) {
					categoryProbs.put(ALL[i], probs[i]);
				}
			}
		}

		for (String cat : ALL){
			if (bundle.contains(cat.toLowerCase() + CATEGORY_PROBS)){
				float[] probs = bundle.getFloatArray(cat.toLowerCase() + CATEGORY_PROBS);
				if (of(cat).defaultProbs != null && probs.length == of(cat).defaultProbs.length){
					deck(cat).probs = probs;
				}
				if (bundle.contains(cat.toLowerCase() + CATEGORY_USING_PROBS2)){
					deck(cat).using2ndProbs = bundle.getBoolean(cat.toLowerCase() + CATEGORY_USING_PROBS2);
				} else {
					deck(cat).using2ndProbs = false;
				}
				if (bundle.contains(cat.toLowerCase() + CATEGORY_SEED)){
					deck(cat).seed = bundle.getLong(cat.toLowerCase() + CATEGORY_SEED);
					deck(cat).dropped = bundle.getInt(cat.toLowerCase() + CATEGORY_DROPPED);
				}

				//pre-v3.0.0 and pre-v3.3.0 conversion for artifacts (addition of tome and key)
				if (cat == ARTIFACT && probs.length != of(cat).defaultProbs.length){
					int tomeIDX = 5;
					int keyIDX = 9;
					int j = 0;
					for (int i = 0; i < probs.length; i++){
						//we do a specific check here for holy tome pre-v3.0.0
						if (j == tomeIDX && probs.length == of(cat).defaultProbs.length-2){
							deck(cat).probs[j] = 0;
							j++;
						} else if (j == keyIDX){
							deck(cat).probs[j] = 1;
							j++;
						}
						deck(cat).probs[j] = probs[i];
						j++;
					}

				}

			}
		}
		
	}
}

