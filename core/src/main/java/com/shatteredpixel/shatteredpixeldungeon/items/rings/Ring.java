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
 */

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * The nine numerical ring variants share this implementation. Force and Wealth
 * remain subclasses because they own behavior beyond a numerical effect.
 */
public class Ring extends KindofMisc {

	public enum Effect {
		ACCURACY("ringofaccuracy", ItemSpriteSheet.Icons.RING_ACCURACY, 1.3f, false),
		ARCANA("ringofarcana", ItemSpriteSheet.Icons.RING_ARCANA, 1.175f, false),
		ELEMENTS("ringofelements", ItemSpriteSheet.Icons.RING_ELEMENTS, 0.825f, true),
		ENERGY("ringofenergy", ItemSpriteSheet.Icons.RING_ENERGY, 1.175f, false),
		EVASION("ringofevasion", ItemSpriteSheet.Icons.RING_EVASION, 1.125f, false),
		FUROR("ringoffuror", ItemSpriteSheet.Icons.RING_FUROR, 1.09051f, false),
		HASTE("ringofhaste", ItemSpriteSheet.Icons.RING_HASTE, 1.175f, false),
		SHARPSHOOTING("ringofsharpshooting", ItemSpriteSheet.Icons.RING_SHARPSHOOT, 1.2f, false),
		TENACITY("ringoftenacity", ItemSpriteSheet.Icons.RING_TENACITY, 0.85f, true);

		private final String messageKey;
		private final int icon;
		private final float base;
		private final boolean reduction;

		Effect(String messageKey, int icon, float base, boolean reduction) {
			this.messageKey = messageKey;
			this.icon = icon;
			this.base = base;
			this.reduction = reduction;
		}

		private float percent(int bonus) {
			double multiplier = Math.pow(base, bonus);
			return 100f * (reduction ? (1f - (float) multiplier) : ((float) multiplier - 1f));
		}
	}

	private static final String FORCE_ID = "ringofforce";
	private static final String WEALTH_ID = "ringofwealth";
	private static final String[] IDENTITY_IDS = createIdentityIds();

	private static String[] createIdentityIds() {
		String[] result = new String[Effect.values().length + 2];
		int index = 0;
		for (Effect effect : Effect.values()) {
			result[index++] = effect.messageKey;
		}
		result[index++] = FORCE_ID;
		result[index] = WEALTH_ID;
		return result;
	}

	private static final LinkedHashMap<String, Integer> gems = new LinkedHashMap<String, Integer>() {
		{
			put("garnet", ItemSpriteSheet.RING_GARNET);
			put("ruby", ItemSpriteSheet.RING_RUBY);
			put("topaz", ItemSpriteSheet.RING_TOPAZ);
			put("emerald", ItemSpriteSheet.RING_EMERALD);
			put("onyx", ItemSpriteSheet.RING_ONYX);
			put("opal", ItemSpriteSheet.RING_OPAL);
			put("tourmaline", ItemSpriteSheet.RING_TOURMALINE);
			put("sapphire", ItemSpriteSheet.RING_SAPPHIRE);
			put("amethyst", ItemSpriteSheet.RING_AMETHYST);
			put("quartz", ItemSpriteSheet.RING_QUARTZ);
			put("agate", ItemSpriteSheet.RING_AGATE);
			put("diamond", ItemSpriteSheet.RING_DIAMOND);
		}
	};

	private static final LinkedHashMap<String, String> identityGems = new LinkedHashMap<>();
	private static final LinkedHashSet<String> knownIdentities = new LinkedHashSet<>();

	private static final String LABEL_SUFFIX = "_label";
	private static final String KNOWN_SUFFIX = "_known";

	protected Buff buff;
	protected Class<? extends RingBuff> buffClass;

	private Effect effect;
	private String gem;

	// Rings cannot be used like other equipment, so they identify through experience.
	private float levelsToID = 1;

	public static final HashSet<Class> RESISTS = new HashSet<>();

	static {
		RESISTS.add(Burning.class);
		RESISTS.add(Chill.class);
		RESISTS.add(Frost.class);
		RESISTS.add(Ooze.class);
		RESISTS.add(Paralysis.class);
		RESISTS.add(Poison.class);
		RESISTS.add(Corrosion.class);
		RESISTS.add(ToxicGas.class);
		RESISTS.add(Electricity.class);
		RESISTS.addAll(AntiMagic.RESISTS);
	}

	public static void initGems() {
		identityGems.clear();
		knownIdentities.clear();
		ArrayList<String> labelsLeft = new ArrayList<>(gems.keySet());
		for (String id : IDENTITY_IDS) {
			identityGems.put(id, labelsLeft.remove(Random.Int(labelsLeft.size())));
		}
	}

	private static void ensureGems() {
		if (identityGems.size() != IDENTITY_IDS.length) {
			initGems();
		}
	}

	public static void clearGems() {
		identityGems.clear();
		knownIdentities.clear();
	}

	public static void save(Bundle bundle) {
		ensureGems();
		for (String id : IDENTITY_IDS) {
			bundle.put(id + LABEL_SUFFIX, identityGems.get(id));
			bundle.put(id + KNOWN_SUFFIX, knownIdentities.contains(id));
		}
	}

	public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
		ensureGems();
		HashSet<String> saved = new HashSet<>();
		for (Item item : items) {
			if (item instanceof Ring) {
				String id = ((Ring) item).identityId();
				if (id != null && saved.add(id)) {
					bundle.put(id + LABEL_SUFFIX, identityGems.get(id));
					bundle.put(id + KNOWN_SUFFIX, knownIdentities.contains(id));
				}
			}
		}
	}

	public static void restore(Bundle bundle) {
		identityGems.clear();
		knownIdentities.clear();

		ArrayList<String> labelsLeft = new ArrayList<>(gems.keySet());
		ArrayList<String> missing = new ArrayList<>();
		for (String id : IDENTITY_IDS) {
			if (bundle.contains(id + LABEL_SUFFIX)) {
				String label = bundle.getString(id + LABEL_SUFFIX);
				identityGems.put(id, label);
				labelsLeft.remove(label);
				if (bundle.getBoolean(id + KNOWN_SUFFIX)) {
					knownIdentities.add(id);
				}
			} else {
				missing.add(id);
			}
		}
		for (String id : missing) {
			identityGems.put(id, labelsLeft.remove(Random.Int(labelsLeft.size())));
		}
	}

	public Ring() {
		super();
		if (getClass() == Ring.class) {
			setEffect(Random.element(Effect.values()));
		}
		reset();
	}

	public Ring(Effect effect) {
		super();
		setEffect(effect);
		reset();
	}

	private void setEffect(Effect effect) {
		this.effect = effect == null ? Effect.ACCURACY : effect;
		buffClass = EffectBuff.class;
		icon = this.effect.icon;
	}

	public Effect effect() {
		return effect;
	}

	public static Ring randomEffect() {
		return new Ring();
	}

	public boolean sameIdentity(Ring other) {
		if (other == null) {
			return false;
		}
		if (getClass() == Ring.class || other.getClass() == Ring.class) {
			return getClass() == other.getClass() && effect == other.effect;
		}
		return getClass() == other.getClass();
	}

	private String identityId() {
		if (getClass() == Ring.class) {
			return effect == null ? null : effect.messageKey;
		}
		if (this instanceof RingOfForce) {
			return FORCE_ID;
		}
		if (this instanceof RingOfWealth) {
			return WEALTH_ID;
		}
		return null;
	}

	private boolean identityKnown() {
		String id = identityId();
		return id != null && knownIdentities.contains(id);
	}

	public String identityName() {
		return effect == null
				? Messages.get(getClass(), "name")
				: effectMessage("name");
	}

	private String effectMessage(String key, Object... args) {
		return Messages.get("items.rings." + effect.messageKey + "." + key, args);
	}

	private void applyIdentityVisual() {
		if (effect != null) {
			icon = effect.icon;
		}
		String id = identityId();
		if (id != null && identityGems.containsKey(id)) {
			gem = identityGems.get(id);
			image = gems.get(gem);
		} else {
			gem = "garnet";
			image = ItemSpriteSheet.RING_GARNET;
		}
	}

	// Anonymous rings do not affect identification state.
	protected boolean anonymous = false;

	public void anonymize() {
		if (!identityKnown()) {
			image = ItemSpriteSheet.RING_HOLDER;
		}
		anonymous = true;
	}

	public void reset() {
		super.reset();
		levelsToID = 1;
		if (getClass() == Ring.class && effect == null) {
			setEffect(Random.element(Effect.values()));
		}
		applyIdentityVisual();
	}

	public void activate(Char ch) {
		if (buff != null) {
			buff.detach();
			buff = null;
		}
		buff = buff();
		if (buff != null) {
			buff.attachTo(ch);
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)) {
			if (buff != null) {
				buff.detach();
				buff = null;
			}
			return true;
		}
		return false;
	}

	public boolean isKnown() {
		return anonymous || identityKnown();
	}

	public void setKnown() {
		if (anonymous) {
			return;
		}
		ensureGems();
		String id = identityId();
		if (id != null) {
			knownIdentities.add(id);
		}
		if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
			Catalog.setSeen(getClass());
			Statistics.itemTypesDiscovered.add(getClass());
		}
	}

	@Override
	public String name() {
		return isKnown() ? identityName() : Messages.get(Ring.class, gem);
	}

	@Override
	public String desc() {
		if (!isKnown()) {
			return Messages.get(Ring.class, "unknown_desc");
		}
		return effect == null ? super.desc() : effectMessage("desc");
	}

	@Override
	public String info() {
		String desc;
		if (anonymous && !identityKnown()) {
			desc = desc();
		} else {
			desc = super.info();
		}

		if (cursed && isEquipped(Dungeon.hero)) {
			desc += "\n\n" + Messages.get(Ring.class, "cursed_worn");
		} else if (cursed && cursedKnown) {
			desc += "\n\n" + Messages.get(Ring.class, "curse_known");
		} else if (!isIdentified() && cursedKnown) {
			desc += "\n\n" + Messages.get(Ring.class, "not_cursed");
		}

		if (isKnown()) {
			desc += "\n\n" + statsInfo();
		}
		return desc;
	}

	protected String statsInfo() {
		if (effect == null) {
			return "";
		}
		if (!isIdentified()) {
			if (effect == Effect.SHARPSHOOTING) {
				return effectMessage("typical_stats", 1, Messages.decimalFormat("#.##", 20f));
			}
			return effectMessage("typical_stats",
					Messages.decimalFormat("#.##", effect.percent(1)));
		}

		int soloBuffed = soloBuffedBonus();
		String info;
		if (effect == Effect.SHARPSHOOTING) {
			info = effectMessage("stats", soloBuffed,
					Messages.decimalFormat("#.##", 100f * (Math.pow(1.2, soloBonus()) - 1f)));
		} else {
			info = effectMessage("stats",
					Messages.decimalFormat("#.##", effect.percent(soloBuffed)));
		}

		if (Dungeon.hero != null && isEquipped(Dungeon.hero)
				&& soloBuffed != combinedBuffedBonus(Dungeon.hero)) {
			if (effect == Effect.SHARPSHOOTING) {
				info += "\n\n" + effectMessage("combined_stats",
						combinedBuffedBonus(Dungeon.hero),
						Messages.decimalFormat("#.##",
								100f * (Math.pow(1.2, combinedBonus(Dungeon.hero)) - 1f)));
			} else {
				info += "\n\n" + effectMessage("combined_stats",
						Messages.decimalFormat("#.##",
								effect.percent(combinedBuffedBonus(Dungeon.hero))));
			}
		}
		return info;
	}

	public String upgradeStatName(int stat) {
		return effect == null
				? Messages.get(this, "upgrade_stat_name_" + stat)
				: effectMessage("upgrade_stat_name_" + stat);
	}

	private int adjustedUpgradeLevel(int level) {
		boolean applyCurse = effect == Effect.ARCANA ? cursed : cursed && cursedKnown;
		return applyCurse ? Math.min(-1, level - 3) : level;
	}

	public String upgradeStat1(int level) {
		if (effect == null) {
			return null;
		}
		level = adjustedUpgradeLevel(level);
		if (effect == Effect.SHARPSHOOTING) {
			return Integer.toString(level + 1);
		}
		return Messages.decimalFormat("#.##", effect.percent(level + 1)) + "%";
	}

	public String upgradeStat2(int level) {
		if (effect != Effect.SHARPSHOOTING) {
			return null;
		}
		level = adjustedUpgradeLevel(level);
		return Messages.decimalFormat("#.##", 100f * (Math.pow(1.2, level + 1) - 1f)) + "%";
	}

	public String upgradeStat3(int level) {
		return null;
	}

	@Override
	public Item upgrade() {
		super.upgrade();
		if (Random.Int(3) == 0) {
			cursed = false;
		}
		return this;
	}

	@Override
	public boolean isIdentified() {
		return super.isIdentified() && isKnown();
	}

	@Override
	public Item identify(boolean byHero) {
		setKnown();
		levelsToID = 0;
		return super.identify(byHero);
	}

	public void setIDReady() {
		levelsToID = -1;
	}

	public boolean readyToIdentify() {
		return !isIdentified() && levelsToID <= 0;
	}

	@Override
	public Item random() {
		int level = 0;
		if (Random.Int(3) == 0) {
			level++;
			if (Random.Int(5) == 0) {
				level++;
			}
		}
		level(level);
		if (Random.Float() < 0.3f) {
			cursed = true;
		}
		return this;
	}

	public static ArrayList<Ring> getKnown() {
		ensureGems();
		return identities(true);
	}

	public static ArrayList<Ring> getUnknown() {
		ensureGems();
		return identities(false);
	}

	private static ArrayList<Ring> identities(boolean known) {
		ArrayList<Ring> result = new ArrayList<>();
		for (String id : IDENTITY_IDS) {
			if (knownIdentities.contains(id) == known) {
				result.add(identity(id));
			}
		}
		return result;
	}

	private static Ring identity(String id) {
		for (Effect effect : Effect.values()) {
			if (effect.messageKey.equals(id)) {
				return new Ring(effect);
			}
		}
		if (FORCE_ID.equals(id)) {
			return new RingOfForce();
		}
		return new RingOfWealth();
	}

	public static boolean allKnown() {
		return knownIdentities.size() == IDENTITY_IDS.length;
	}

	@Override
	public int value() {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= level() + 1;
			} else if (level() < 0) {
				price /= 1 - level();
			}
		}
		return Math.max(price, 1);
	}

	protected RingBuff buff() {
		return effect == null ? null : new EffectBuff();
	}

	private static final String LEVELS_TO_ID = "levels_to_ID";
	private static final String EFFECT = "effect";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVELS_TO_ID, levelsToID);
		if (effect != null) {
			bundle.put(EFFECT, effect);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (getClass() == Ring.class) {
			if (bundle.contains(EFFECT)) {
				setEffect(bundle.getEnum(EFFECT, Effect.class));
			} else if (effect == null) {
				setEffect(Random.element(Effect.values()));
			}
		}
		levelsToID = bundle.getFloat(LEVELS_TO_ID);
		applyIdentityVisual();
	}

	public void onHeroGainExp(float levelPercent, Hero hero) {
		if (isIdentified() || !isEquipped(hero)) {
			return;
		}
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		levelsToID -= levelPercent;
		if (levelsToID <= 0) {
			if (ShardOfOblivion.passiveIDDisabled()) {
				if (levelsToID > -1) {
					GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
				}
				setIDReady();
			} else {
				identify();
				GLog.p(Messages.get(Ring.class, "identify"));
				Badges.validateItemLevelAquired(this);
			}
		}
	}

	@Override
	public int buffedLvl() {
		int level = super.buffedLvl();
		if (Dungeon.hero != null && Dungeon.hero.buff(EnhancedRings.class) != null) {
			level++;
		}
		return level;
	}

	// Class-based queries remain for the specialized Force and Wealth rings.
	public static int getBonus(Char target, Class<? extends RingBuff> type) {
		if (target.buff(MagicImmune.class) != null) {
			return 0;
		}
		int bonus = 0;
		for (RingBuff buff : target.buffs(type)) {
			bonus += buff.level();
		}
		SpiritForm.SpiritFormBuff spirit = target.buff(SpiritForm.SpiritFormBuff.class);
		if (bonus == 0 && spirit != null && spirit.ring() != null
				&& spirit.ring().buffClass == type) {
			bonus += spirit.ring().soloBonus();
		}
		return bonus;
	}

	public static int getBuffedBonus(Char target, Class<? extends RingBuff> type) {
		if (target.buff(MagicImmune.class) != null) {
			return 0;
		}
		int bonus = 0;
		for (RingBuff buff : target.buffs(type)) {
			bonus += buff.buffedLvl();
		}
		SpiritForm.SpiritFormBuff spirit = target.buff(SpiritForm.SpiritFormBuff.class);
		if (bonus == 0 && spirit != null && spirit.ring() != null
				&& spirit.ring().buffClass == type) {
			bonus += spirit.ring().soloBuffedBonus();
		}
		return bonus;
	}

	public static int getBonus(Char target, Effect effect) {
		if (target.buff(MagicImmune.class) != null) {
			return 0;
		}
		int bonus = 0;
		for (EffectBuff buff : target.buffs(EffectBuff.class)) {
			if (buff.effect() == effect) {
				bonus += buff.level();
			}
		}
		SpiritForm.SpiritFormBuff spirit = target.buff(SpiritForm.SpiritFormBuff.class);
		if (bonus == 0 && spirit != null && spirit.ring() != null
				&& spirit.ring().effect == effect) {
			bonus += spirit.ring().soloBonus();
		}
		return bonus;
	}

	public static int getBuffedBonus(Char target, Effect effect) {
		if (target.buff(MagicImmune.class) != null) {
			return 0;
		}
		int bonus = 0;
		for (EffectBuff buff : target.buffs(EffectBuff.class)) {
			if (buff.effect() == effect) {
				bonus += buff.buffedLvl();
			}
		}
		SpiritForm.SpiritFormBuff spirit = target.buff(SpiritForm.SpiritFormBuff.class);
		if (bonus == 0 && spirit != null && spirit.ring() != null
				&& spirit.ring().effect == effect) {
			bonus += spirit.ring().soloBuffedBonus();
		}
		return bonus;
	}

	public static float accuracyMultiplier(Char target) {
		return (float) Math.pow(1.3f, getBuffedBonus(target, Effect.ACCURACY));
	}

	public static float enchantPowerMultiplier(Char target) {
		return (float) Math.pow(1.175f, getBuffedBonus(target, Effect.ARCANA));
	}

	public static float resist(Char target, Class effect) {
		if (getBuffedBonus(target, Effect.ELEMENTS) == 0) {
			return 1f;
		}
		for (Class resistance : RESISTS) {
			if (resistance.isAssignableFrom(effect)) {
				return (float) Math.pow(0.825, getBuffedBonus(target, Effect.ELEMENTS));
			}
		}
		return 1f;
	}

	public static float wandChargeMultiplier(Char target) {
		float bonus = (float) Math.pow(1.175, getBuffedBonus(target, Effect.ENERGY));
		if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.CLERIC
				&& ((Hero) target).hasTalent(Talent.LIGHT_READING)) {
			bonus *= 1f + 0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_READING) / 3f;
		}
		return bonus;
	}

	public static float artifactChargeMultiplier(Char target) {
		float bonus = (float) Math.pow(1.175, getBuffedBonus(target, Effect.ENERGY));
		if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.ROGUE
				&& ((Hero) target).hasTalent(Talent.LIGHT_CLOAK)) {
			bonus *= 1f + 0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_CLOAK) / 3f;
		}
		return bonus;
	}

	public static float armorChargeMultiplier(Char target) {
		return (float) Math.pow(1.175, getBuffedBonus(target, Effect.ENERGY));
	}

	public static float evasionMultiplier(Char target) {
		return (float) Math.pow(1.125, getBuffedBonus(target, Effect.EVASION));
	}

	public static float attackSpeedMultiplier(Char target) {
		return (float) Math.pow(1.09051, getBuffedBonus(target, Effect.FUROR));
	}

	public static float speedMultiplier(Char target) {
		return (float) Math.pow(1.175, getBuffedBonus(target, Effect.HASTE));
	}

	public static int levelDamageBonus(Char target) {
		return getBuffedBonus(target, Effect.SHARPSHOOTING);
	}

	public static float durabilityMultiplier(Char target) {
		return (float) Math.pow(1.2, getBonus(target, Effect.SHARPSHOOTING));
	}

	public static float damageMultiplier(Char target) {
		return (float) Math.pow(0.85,
				getBuffedBonus(target, Effect.TENACITY)
						* ((float) (target.HT - target.HP) / target.HT));
	}

	public int soloBonus() {
		return cursed ? Math.min(0, level() - 2) : level() + 1;
	}

	public int soloBuffedBonus() {
		return cursed ? Math.min(0, buffedLvl() - 2) : buffedLvl() + 1;
	}

	public int combinedBonus(Hero hero) {
		int bonus = 0;
		if (hero.belongings.ring() != null && sameIdentity(hero.belongings.ring())) {
			bonus += hero.belongings.ring().soloBonus();
		}
		if (hero.belongings.misc() instanceof Ring
				&& sameIdentity((Ring) hero.belongings.misc())) {
			bonus += ((Ring) hero.belongings.misc()).soloBonus();
		}
		return bonus;
	}

	public int combinedBuffedBonus(Hero hero) {
		int bonus = 0;
		if (hero.belongings.ring() != null && sameIdentity(hero.belongings.ring())) {
			bonus += hero.belongings.ring().soloBuffedBonus();
		}
		if (hero.belongings.misc() instanceof Ring
				&& sameIdentity((Ring) hero.belongings.misc())) {
			bonus += ((Ring) hero.belongings.misc()).soloBuffedBonus();
		}
		return bonus;
	}

	public class RingBuff extends Buff {

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)) {
				if (target instanceof Hero && Dungeon.hero == null
						&& cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean act() {
			spend(TICK);
			return true;
		}

		public int level() {
			return Ring.this.soloBonus();
		}

		public int buffedLvl() {
			return Ring.this.soloBuffedBonus();
		}
	}

	public class EffectBuff extends RingBuff {

		public Effect effect() {
			return Ring.this.effect;
		}
	}
}
