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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import java.util.LinkedHashMap;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugBag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BowFragment;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenHilt;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.CloakScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.SealShard;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.TornPage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.DeviceCompat;

/**
 * 动态注册的职业。内置职业为静态 final 实例（匿名子类），
 * 新职业 = 运行时 {@link #register(HeroClass)} 注册一个实例。
 *
 * 职业 id 沿用旧 enum 常量名（"WARRIOR" 等），Messages 键与存档键零迁移；
 * 全代码库的 {@code == HeroClass.WARRIOR} 比较继续按实例引用生效。
 */
public abstract class HeroClass {

	// ==================== 注册表 ====================

	private static final LinkedHashMap<String, HeroClass> REGISTRY = new LinkedHashMap<>();

	/** 注册职业并返回实例（便于静态字段声明）；重复/空 id 抛 IllegalArgumentException（仿 SpriteRegistry） */
	public static HeroClass register(HeroClass cls) {
		if (cls == null || cls.id == null || cls.id.isEmpty()) {
			throw new IllegalArgumentException("hero class id must not be null or empty");
		}
		if (REGISTRY.containsKey(cls.id)) {
			throw new IllegalArgumentException("hero class '" + cls.id + "' is already registered");
		}
		REGISTRY.put(cls.id, cls);
		return cls;
	}

	/** 按 id 查找；未知 id 返回 null */
	public static HeroClass find(String id) {
		return REGISTRY.get(id);
	}

	/** 全部已注册职业（注册序） */
	public static HeroClass[] all() {
		return REGISTRY.values().toArray(new HeroClass[0]);
	}

	// ==================== 内置职业 ====================

	public static final HeroClass WARRIOR = register(new HeroClass("WARRIOR",
			new HeroSubClass[]{HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR},
			Badges.Badge.MASTERY_WARRIOR, ItemSpriteSheet.SEAL,
			Assets.Sprites.WARRIOR, Assets.Splashes.WARRIOR,
			null, //战士默认解锁
			new String[]{"warrior_t1", "warrior_t2", "warrior_t3"},
			"intro_quest_warrior", 26) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initWarrior(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new SealShard();
		}
	});

	public static final HeroClass MAGE = register(new HeroClass("MAGE",
			new HeroSubClass[]{HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK},
			Badges.Badge.MASTERY_MAGE, ItemSpriteSheet.MAGES_STAFF,
			Assets.Sprites.MAGE, Assets.Splashes.MAGE,
			Badges.Badge.UNLOCK_MAGE,
			new String[]{"mage_t1", "mage_t2", "mage_t3"},
			"intro_quest_mage", 58) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initMage(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new BrokenStaff();
		}

		//法师法杖顶部有 2px 粒子特效区，图标裁剪掉
		@Override
		public Image iconImage() {
			Image result = new ItemSprite(iconSprite);
			com.watabou.utils.RectF frame = result.frame();
			frame.top += frame.height() / 8f;
			result.frame(frame);
			return result;
		}
	});

	public static final HeroClass ROGUE = register(new HeroClass("ROGUE",
			new HeroSubClass[]{HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER},
			Badges.Badge.MASTERY_ROGUE, ItemSpriteSheet.ARTIFACT_CLOAK,
			Assets.Sprites.ROGUE, Assets.Splashes.ROGUE,
			Badges.Badge.UNLOCK_ROGUE,
			new String[]{"rogue_t1", "rogue_t2", "rogue_t3"},
			"intro_quest_rogue", 90) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initRogue(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new CloakScrap();
		}
	});

	public static final HeroClass HUNTRESS = register(new HeroClass("HUNTRESS",
			new HeroSubClass[]{HeroSubClass.SNIPER, HeroSubClass.WARDEN},
			Badges.Badge.MASTERY_HUNTRESS, ItemSpriteSheet.SPIRIT_BOW,
			Assets.Sprites.HUNTRESS, Assets.Splashes.HUNTRESS,
			Badges.Badge.UNLOCK_HUNTRESS,
			new String[]{"huntress_t1", "huntress_t2", "huntress_t3"},
			"intro_quest_huntress", 122) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initHuntress(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new BowFragment();
		}
	});

	public static final HeroClass DUELIST = register(new HeroClass("DUELIST",
			new HeroSubClass[]{HeroSubClass.CHAMPION, HeroSubClass.MONK},
			Badges.Badge.MASTERY_DUELIST, ItemSpriteSheet.RAPIER,
			Assets.Sprites.DUELIST, Assets.Splashes.DUELIST,
			Badges.Badge.UNLOCK_DUELIST,
			new String[]{"duelist_t1", "duelist_t2", "duelist_t3"},
			"intro_quest_duelist", 154) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initDuelist(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new BrokenHilt();
		}
	});

	public static final HeroClass CLERIC = register(new HeroClass("CLERIC",
			new HeroSubClass[]{HeroSubClass.PRIEST, HeroSubClass.PALADIN},
			Badges.Badge.MASTERY_CLERIC, ItemSpriteSheet.ARTIFACT_TOME,
			Assets.Sprites.CLERIC, Assets.Splashes.CLERIC,
			Badges.Badge.UNLOCK_CLERIC,
			new String[]{"cleric_t1", "cleric_t2", "cleric_t3"},
			"intro_quest_cleric", 186) {
		@Override
		protected void initClassSpecific(Hero hero) {
			initCleric(hero);
		}

		@Override
		public ArmorAbility[] armorAbilities() {
			return new ArmorAbility[]{new AscendedForm(), new Trinity(), new PowerOfMany()};
		}

		@Override
		public RemainsItem remainsItem() {
			return new TornPage();
		}
	});

	// ==================== 数据字段 ====================

	/** 职业 id（沿用旧 enum 常量名，如 "WARRIOR"；Messages 键与存档键一致） */
	public final String id;
	private final HeroSubClass[] subClasses;
	/** 精通徽章（Badges.validateMastery 用） */
	public final Badges.Badge masteryBadge;
	/** 职业图标（ItemSpriteSheet 索引） */
	public final int iconSprite;
	public final String spritesheet;
	public final String splashArt;
	/** 解锁徽章；null = 默认解锁 */
	public final Badges.Badge unlockBadge;
	/** 创建角色时注入的 talent 层 id 列表 */
	public final String[] talentLayerIds;
	/** 铁匠任务对话键（如 "intro_quest_warrior"） */
	public final String introQuestMsgKey;
	/** HEROIC_ENERGY 天赋图标（Talent.icon 用） */
	public final int heroicEnergyIcon;

	protected HeroClass(String id, HeroSubClass[] subClasses, Badges.Badge masteryBadge, int iconSprite,
			String spritesheet, String splashArt, Badges.Badge unlockBadge, String[] talentLayerIds,
			String introQuestMsgKey, int heroicEnergyIcon) {
		this.id = id;
		this.subClasses = subClasses;
		this.masteryBadge = masteryBadge;
		this.iconSprite = iconSprite;
		this.spritesheet = spritesheet;
		this.splashArt = splashArt;
		this.unlockBadge = unlockBadge;
		this.talentLayerIds = talentLayerIds;
		this.introQuestMsgKey = introQuestMsgKey;
		this.heroicEnergyIcon = heroicEnergyIcon;
	}

	// ==================== 行为 ====================

	public void initHero( Hero hero ) {

		hero.heroClass = this;
		//职业层注入（创建角色时）
		for (String id : talentLayerIds){
			hero.addTalentLayer(id);
		}

		Item i = new Armor().identify();
		if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (Armor)i;

		i = new Food();
		if (!Challenges.isItemBlocked(i)) i.collect();

		Waterskin waterskin = new Waterskin();
		waterskin.collect();

		new ScrollOfIdentify().identify();

		initClassSpecific( hero );

		if (Dungeon.mode == Dungeon.Mode.DEMO) {
			hero.initializeShowcaseStats();
			new TengusMask().collect();
			new KingsCrown().collect();
		}
		if (DeviceCompat.isDebug()) {
			DebugBag.install();
			new StoneOfEnchantment().quantity(100).collect();
		}

		if (SPDSettings.quickslotWaterskin()) {
			for (int s = 0; s < QuickSlot.SIZE; s++) {
				if (Dungeon.quickslot.getItem(s) == null) {
					Dungeon.quickslot.setSlot(s, waterskin);
					break;
				}
			}
		}

	}

	/** 职业专属初始装备（原 initWarrior/initMage/... 内容） */
	protected abstract void initClassSpecific( Hero hero );

	public abstract ArmorAbility[] armorAbilities();

	/** 职业遗物（RemainsItem.get 用） */
	public abstract RemainsItem remainsItem();

	/** 职业图标（MAGE 覆写做法杖裁剪） */
	public Image iconImage() {
		return new ItemSprite(iconSprite);
	}

	public boolean isUnlocked(){
		//always unlock on debug builds
		if (DeviceCompat.isDebug()) return true;

		return unlockBadge == null || Badges.isUnlocked(unlockBadge);
	}

	public String title() {
		return Messages.get(HeroClass.class, id);
	}

	public String desc(){
		return Messages.get(HeroClass.class, id+"_desc");
	}

	public String shortDesc(){
		return Messages.get(HeroClass.class, id+"_desc_short");
	}

	public String unlockMsg() {
		return shortDesc() + "\n\n" + Messages.get(HeroClass.class, id+"_unlock");
	}

	public HeroSubClass[] subClasses() {
		return subClasses;
	}

	// ==================== 职业专属初始装备 ====================

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new Sword()).identify();
		ThrowingStone stones = new ThrowingStone();
		stones.identify().collect();

		Dungeon.quickslot.setSlot(0, stones);

		if (hero.belongings.armor != null){
			hero.belongings.armor.affixSeal(new BrokenSeal());
			Catalog.setSeen(BrokenSeal.class); //as it's not added to the inventory
		}

		new PotionOfHealing().identify();
		new ScrollOfRage().identify();
	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;

		staff = new MagesStaff(new WandOfMagicMissile());

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
		new PotionOfLiquidFlame().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.artifact = cloak).identify();
		hero.belongings.artifact.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.identify().collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfMagicMapping().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Sai()).identify();
		SpiritBow bow = new SpiritBow();
		bow.identify().collect();

		Dungeon.quickslot.setSlot(0, bow);

		new ScrollOfLullaby().identify();
	}

	private static void initDuelist( Hero hero ) {

		(hero.belongings.weapon = new Rapier()).identify();
		hero.belongings.weapon.activate(hero);

		ThrowingStone spikes = new ThrowingStone();
		spikes.quantity(2).identify().collect(); //set quantity is 3, but Duelist starts with 2

		Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
		Dungeon.quickslot.setSlot(1, spikes);

		new ScrollOfMirrorImage().identify();
	}

	private static void initCleric( Hero hero ) {

		(hero.belongings.weapon = new Mace()).identify();
		hero.belongings.weapon.activate(hero);

		HolyTome tome = new HolyTome();
		(hero.belongings.artifact = tome).identify();
		hero.belongings.artifact.activate( hero );

		Dungeon.quickslot.setSlot(0, tome);

		new PotionOfPurity().identify();
		new ScrollOfRemoveCurse().identify();
	}

}
