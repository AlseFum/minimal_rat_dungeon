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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.function.Supplier;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;

/**
 * 可注册的 talent 层。每层包含若干 talent 选项与独立等级阈值点数池，
 * 角色通过 {@link Hero#addTalentLayer(String)} 获得层（幂等，可无限添加）。
 *
 * 层的可用性由"是否已被加入角色"天然决定——没有 available 谓词。
 * 注册表仿 SpriteRegistry / DungeonRegion：重复 id 注册抛异常，find 未知 id 返回 null。
 */
public class TalentLayer {

	public final String id;
	private final TalentEntry[] entries;
	private final int[] levelThresholds;
	private final Supplier<String> titleProvider;
	/** 合并源层：不可独立添加，只在选择时把其天赋并入目标层（如子职业 t3 并入职业 t3，共用点数池） */
	private final boolean mergeSource;

	/** 一个 talent 选项：talent + 该层内可加点数上限 */
	public static class TalentEntry {
		public final Talent talent;
		public final int maxPoints;

		public TalentEntry(Talent talent) {
			this(talent, talent.maxPoints());
		}

		public TalentEntry(Talent talent, int maxPoints) {
			this.talent = talent;
			this.maxPoints = maxPoints;
		}
	}

	public TalentLayer(String id, TalentEntry[] entries, int[] levelThresholds, Supplier<String> titleProvider) {
		this(id, entries, levelThresholds, titleProvider, false);
	}

	public TalentLayer(String id, TalentEntry[] entries, Supplier<String> titleProvider) {
		this(id, entries, DEFAULT_THRESHOLDS, titleProvider, false);
	}

	public TalentLayer(String id, Talent[] talents, int[] levelThresholds, Supplier<String> titleProvider) {
		this(id, entries(talents), levelThresholds, titleProvider, false);
	}

	public TalentLayer(String id, Talent[] talents, Supplier<String> titleProvider) {
		this(id, entries(talents), DEFAULT_THRESHOLDS, titleProvider, false);
	}

	private TalentLayer(String id, TalentEntry[] entries, int[] levelThresholds, Supplier<String> titleProvider, boolean mergeSource) {
		this.id = id;
		this.entries = entries;
		this.levelThresholds = levelThresholds;
		this.titleProvider = titleProvider;
		this.mergeSource = mergeSource;
	}

	private static TalentEntry[] entries(Talent[] talents) {
		TalentEntry[] entries = new TalentEntry[talents.length];
		for (int i = 0; i < talents.length; i++) {
			entries[i] = new TalentEntry(talents[i]);
		}
		return entries;
	}

	public TalentEntry[] entries() {
		return entries;
	}

	public int talentCount() {
		return entries.length;
	}

	/**
	 * 每层独立的等级阈值：{起始等级, 封顶等级}。
	 * 起始等级起每级给 1 点，到封顶等级不再增长（池大小 = 封顶 - 起始）。
	 * tier 概念完全由外部（等级与显示）决定，层自身不持有。
	 */
	public int[] levelThresholds() {
		return levelThresholds;
	}

	/** 层标题（惰性求值，避免静态初始化时触碰 Messages） */
	public String title() {
		return titleProvider.get();
	}

	/** 是否为合并源层（不可独立添加；转职时并入目标层） */
	public boolean isMergeSource() {
		return mergeSource;
	}

	// ==================== 注册表 ====================

	private static final LinkedHashMap<String, TalentLayer> REGISTRY = new LinkedHashMap<>();

	/** 注册一个层；重复/空 id 抛 IllegalArgumentException（仿 SpriteRegistry） */
	public static void register(TalentLayer layer) {
		if (layer == null || layer.id == null || layer.id.isEmpty()) {
			throw new IllegalArgumentException("talent layer id must not be null or empty");
		}
		if (REGISTRY.containsKey(layer.id)) {
			throw new IllegalArgumentException("talent layer '" + layer.id + "' is already registered");
		}
		REGISTRY.put(layer.id, layer);
	}

	/** 按 id 查找；未知 id 返回 null */
	public static TalentLayer find(String id) {
		return REGISTRY.get(id);
	}

	/** 全部已注册层（注册序 = 显示序） */
	public static TalentLayer[] all() {
		return REGISTRY.values().toArray(new TalentLayer[0]);
	}

	public static final int[] DEFAULT_THRESHOLDS = new int[]{2, 7};

	/** 职业层：起始/封顶沿用原 tier 阈值（t1:2-7, t2:7-13, t3:13-21） */
	private static TalentLayer tierLayer(String cls, int tier, Talent... talents) {
		int[] th = new int[]{2, 7, 13, 21};
		return new TalentLayer(cls + "_t" + tier, talents,
				new int[]{th[tier - 1], th[tier]},
				() -> Messages.get(TalentsPane.class, "tier", tier));
	}

	/**
	 * 子职业层 = 合并源层：不独立给点，转职时其天赋并入职业 t3 层（同池）。
	 * 保留注册供 WndInfoSubclass 预览。
	 */
	private static TalentLayer subclassLayer(HeroSubClass subCls, Talent... talents) {
		String id = subCls.id.toLowerCase(Locale.ENGLISH) + "_t3";
		return new TalentLayer(id, entries(talents), new int[]{13, 21}, subCls::title, true);
	}

	private static TalentLayer abilityLayer(ArmorAbility ability) {
		return new TalentLayer(ability.talentLayerId(), ability.talents(), new int[]{21, 31}, ability::name);
	}

	// ==================== 内置注册 ====================

	static {

		// ---- 职业层 t1/t2/t3（talent 列表与原 Talent.initClassTalents switch 逐字一致） ----

		register(tierLayer("warrior", 1, Talent.HEARTY_MEAL, Talent.VETERANS_INTUITION, Talent.PROVOKED_ANGER, Talent.IRON_WILL));
		register(tierLayer("warrior", 2, Talent.IRON_STOMACH, Talent.LIQUID_WILLPOWER, Talent.RUNIC_TRANSFERENCE, Talent.LETHAL_MOMENTUM, Talent.IMPROVISED_PROJECTILES));
		register(tierLayer("warrior", 3, Talent.HOLD_FAST, Talent.STRONGMAN));

		register(tierLayer("mage", 1, Talent.EMPOWERING_MEAL, Talent.SCHOLARS_INTUITION, Talent.LINGERING_MAGIC, Talent.BACKUP_BARRIER));
		register(tierLayer("mage", 2, Talent.ENERGIZING_MEAL, Talent.INSCRIBED_POWER, Talent.WAND_PRESERVATION, Talent.ARCANE_VISION, Talent.SHIELD_BATTERY));
		register(tierLayer("mage", 3, Talent.DESPERATE_POWER, Talent.ALLY_WARP));

		register(tierLayer("rogue", 1, Talent.CACHED_RATIONS, Talent.THIEFS_INTUITION, Talent.SUCKER_PUNCH, Talent.PROTECTIVE_SHADOWS));
		register(tierLayer("rogue", 2, Talent.MYSTICAL_MEAL, Talent.INSCRIBED_STEALTH, Talent.WIDE_SEARCH, Talent.SILENT_STEPS, Talent.ROGUES_FORESIGHT));
		register(tierLayer("rogue", 3, Talent.ENHANCED_RINGS, Talent.LIGHT_CLOAK));

		register(tierLayer("huntress", 1, Talent.NATURES_BOUNTY, Talent.SURVIVALISTS_INTUITION, Talent.FOLLOWUP_STRIKE, Talent.NATURES_AID));
		register(tierLayer("huntress", 2, Talent.INVIGORATING_MEAL, Talent.LIQUID_NATURE, Talent.REJUVENATING_STEPS, Talent.HEIGHTENED_SENSES, Talent.DURABLE_PROJECTILES));
		register(tierLayer("huntress", 3, Talent.POINT_BLANK, Talent.SEER_SHOT));

		register(tierLayer("duelist", 1, Talent.STRENGTHENING_MEAL, Talent.ADVENTURERS_INTUITION, Talent.PATIENT_STRIKE, Talent.AGGRESSIVE_BARRIER));
		register(tierLayer("duelist", 2, Talent.FOCUSED_MEAL, Talent.LIQUID_AGILITY, Talent.WEAPON_RECHARGING, Talent.LETHAL_HASTE, Talent.SWIFT_EQUIP));
		register(tierLayer("duelist", 3, Talent.PRECISE_ASSAULT, Talent.DEADLY_FOLLOWUP));

		register(tierLayer("cleric", 1, Talent.SATIATED_SPELLS, Talent.HOLY_INTUITION, Talent.SEARING_LIGHT, Talent.SHIELD_OF_LIGHT));
		register(tierLayer("cleric", 2, Talent.ENLIGHTENING_MEAL, Talent.RECALL_INSCRIPTION, Talent.SUNRAY, Talent.DIVINE_SENSE, Talent.BLESS));
		register(tierLayer("cleric", 3, Talent.CLEANSE, Talent.LIGHT_READING));

		// ---- 子职业层 t3（与原 initSubclassTalents switch 逐字一致） ----

		register(subclassLayer(HeroSubClass.BERSERKER, Talent.ENDLESS_RAGE, Talent.DEATHLESS_FURY, Talent.ENRAGED_CATALYST));
		register(subclassLayer(HeroSubClass.GLADIATOR, Talent.CLEAVE, Talent.LETHAL_DEFENSE, Talent.ENHANCED_COMBO));
		register(subclassLayer(HeroSubClass.BATTLEMAGE, Talent.EMPOWERED_STRIKE, Talent.MYSTICAL_CHARGE, Talent.EXCESS_CHARGE));
		register(subclassLayer(HeroSubClass.WARLOCK, Talent.SOUL_EATER, Talent.SOUL_SIPHON, Talent.NECROMANCERS_MINIONS));
		register(subclassLayer(HeroSubClass.ASSASSIN, Talent.ENHANCED_LETHALITY, Talent.ASSASSINS_REACH, Talent.BOUNTY_HUNTER));
		register(subclassLayer(HeroSubClass.FREERUNNER, Talent.EVASIVE_ARMOR, Talent.PROJECTILE_MOMENTUM, Talent.SPEEDY_STEALTH));
		register(subclassLayer(HeroSubClass.SNIPER, Talent.FARSIGHT, Talent.SHARED_ENCHANTMENT, Talent.SHARED_UPGRADES));
		register(subclassLayer(HeroSubClass.WARDEN, Talent.DURABLE_TIPS, Talent.BARKSKIN, Talent.SHIELDING_DEW));
		register(subclassLayer(HeroSubClass.CHAMPION, Talent.VARIED_CHARGE, Talent.TWIN_UPGRADES, Talent.COMBINED_LETHALITY));
		register(subclassLayer(HeroSubClass.MONK, Talent.UNENCUMBERED_SPIRIT, Talent.MONASTIC_VIGOR, Talent.COMBINED_ENERGY));
		register(subclassLayer(HeroSubClass.PRIEST, Talent.HOLY_LANCE, Talent.HALLOWED_GROUND, Talent.MNEMONIC_PRAYER));
		register(subclassLayer(HeroSubClass.PALADIN, Talent.LAY_ON_HANDS, Talent.AURA_OF_PROTECTION, Talent.WALL_OF_LIGHT));

		// ---- 护甲技能层 t4（每个技能一层，含 HEROIC_ENERGY；与原 initArmorTalents 一致） ----

		register(abilityLayer(new HeroicLeap()));
		register(abilityLayer(new Shockwave()));
		register(abilityLayer(new Endure()));

		register(abilityLayer(new ElementalBlast()));
		register(abilityLayer(new WildMagic()));
		register(abilityLayer(new WarpBeacon()));

		register(abilityLayer(new SmokeBomb()));
		register(abilityLayer(new DeathMark()));
		register(abilityLayer(new ShadowClone()));

		register(abilityLayer(new SpectralBlades()));
		register(abilityLayer(new NaturesPower()));
		register(abilityLayer(new SpiritHawk()));

		register(abilityLayer(new Challenge()));
		register(abilityLayer(new ElementalStrike()));
		register(abilityLayer(new Feint()));

		register(abilityLayer(new AscendedForm()));
		register(abilityLayer(new Trinity()));
		register(abilityLayer(new PowerOfMany()));

		register(abilityLayer(new Ratmogrify()));

		// ---- 老鼠层（Ratmogrify 变体天赋；不含 HEROIC_ENERGY，避免与技能层双注册） ----

		register(new TalentLayer("rat_t4",
				new Talent[]{Talent.RATSISTANCE, Talent.RATLOMACY, Talent.RATFORCEMENTS},
				new int[]{21, 31},
				() -> Messages.titleCase(Messages.get(Ratmogrify.class, "name"))));
	}

}
