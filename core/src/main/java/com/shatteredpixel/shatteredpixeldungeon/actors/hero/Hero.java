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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionResult;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.FailCause;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostAura;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HeroDisguise;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SnipersMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HallowedGround;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Smite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ThirteenLeafClover;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Hero extends Char {

	{
		actPriority = HERO_PRIO;
		
		alignment = Alignment.ALLY;
	}
	
	public static final int MAX_LEVEL = 30;

	public static final int STARTING_STR = 10;
	
	private static final float TIME_TO_REST		    = 1f;
	private static final float TIME_TO_SEARCH	    = 2f;
	private static final float HUNGER_FOR_SEARCH	= 6f;
	
	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;
	public ArmorAbility armorAbility = null;
	public LinkedHashMap<String, TalentLayerState> talentLayers = new LinkedHashMap<>();
	public LinkedHashMap<Talent, Talent> metamorphedTalents = new LinkedHashMap<>();

	/** 角色拥有的一个 talent 层状态：层定义 + 已分配点数 */
	public static class TalentLayerState {
		public final TalentLayer layer;
		public final LinkedHashMap<Talent, Integer> points;

		public TalentLayerState(TalentLayer layer) {
			this.layer = layer;
			this.points = new LinkedHashMap<>();
			for (TalentLayer.TalentEntry entry : layer.entries()) {
				points.put(entry.talent, 0);
			}
		}

		public TalentLayerState(TalentLayer layer, LinkedHashMap<Talent, Integer> points) {
			this.layer = layer;
			this.points = points;
		}
	}
	
	private int attackSkill = 10;
	private int defenseSkill = 5;

	public boolean ready = false;
	public boolean damageInterrupt = true;
	public HeroAction curAction = null;
	public HeroAction lastAction = null;

	//reference to the enemy the hero is currently in the process of attacking
	private Char attackTarget;

	//cache of the attack delay for the attack currently being attempted.
	//computed once at proposal time (attackDelay() may have side effects),
	//and shared by the proposal half and the completion half of the cost.
	private float currentAttackDelay = 1f;

	/** The hero's current attack target, or null when not attacking. */
	public Char enemy() {
		return attackTarget;
	}
	
	public boolean resting = false;
	
	public Belongings belongings;
	
	public int STR;
	
	public float awareness;
	
	public int lvl = 1;
	public int exp = 0;
	
	public int HTBoost = 0;
	
	private ArrayList<Mob> visibleEnemies;

	//This list is maintained so that some logic checks can be skipped
	// for enemies we know we aren't seeing normally, resulting in better performance
	public ArrayList<Mob> mindVisionEnemies = new ArrayList<>();

	public Hero() {
		super();

		HP = HT = 20;
		STR = STARTING_STR;
		
		belongings = new Belongings( this );
		
		visibleEnemies = new ArrayList<>();
	}

	/**
	 * The five-level showcase has no experience progression. Every new hero starts
	 * with the level-20 values that the old, longer campaign would have reached.
	 */
	public void initializeShowcaseStats() {
		lvl = 20;
		exp = 0;
		HP = HT = 115;
		attackSkill = 29;
		defenseSkill = 24;
		STR = STARTING_STR;
		HTBoost = 0;
	}
	
	public void updateHT( boolean boostHP ){
		int curHT = HT;
		
		HT = 20 + 5*(lvl-1) + HTBoost;
		
		if (boostHP){
			HP += Math.max(HT - curHT, 0);
		}
		HP = Math.min(HP, HT);
	}

	public int STR() {
		int strBonus = 0;

		AdrenalineSurge buff = buff(AdrenalineSurge.class);
		if (buff != null){
			strBonus += buff.boost();
		}

		if (hasTalent(Talent.STRONGMAN)){
			strBonus += (int)Math.floor(STR * (0.03f + 0.05f*pointsInTalent(Talent.STRONGMAN)));
		}

		return STR + strBonus;
	}

	private static final String CLASS       = "class";
	private static final String SUBCLASS    = "subClass";
	private static final String ABILITY     = "armorAbility";

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String STRENGTH	= "STR";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
	private static final String HTBOOST     = "htboost";
	
	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );

		bundle.put( CLASS, heroClass.id );
		bundle.put( SUBCLASS, subClass.id );
		bundle.put( ABILITY, armorAbility );
		Talent.storeTalentsInBundle( bundle, this );
		
		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );
		
		bundle.put( STRENGTH, STR );
		
		bundle.put( LEVEL, lvl );
		bundle.put( EXPERIENCE, exp );
		
		bundle.put( HTBOOST, HTBoost );

		belongings.storeInBundle( bundle );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {

		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );

		HTBoost = bundle.getInt(HTBOOST);

		super.restoreFromBundle( bundle );

		heroClass = HeroClass.find(bundle.getString(CLASS)) != null ? HeroClass.find(bundle.getString(CLASS)) : HeroClass.WARRIOR;
		subClass = HeroSubClass.find(bundle.getString(SUBCLASS)) != null ? HeroSubClass.find(bundle.getString(SUBCLASS)) : HeroSubClass.NONE;
		armorAbility = (ArmorAbility)bundle.get( ABILITY );
		Talent.restoreTalentsFromBundle( bundle, this );
		
		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );
		
		STR = bundle.getInt( STRENGTH );

		belongings.restoreFromBundle( bundle );
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.level = bundle.getInt( LEVEL );
		info.str = bundle.getInt( STRENGTH );
		info.exp = bundle.getInt( EXPERIENCE );
		info.hp = bundle.getInt( Char.TAG_HP );
		info.ht = bundle.getInt( Char.TAG_HT );
		info.shld = bundle.getInt( Char.TAG_SHLD );
		info.heroClass = HeroClass.find(bundle.getString(CLASS)) != null ? HeroClass.find(bundle.getString(CLASS)) : HeroClass.WARRIOR;
		info.subClass = HeroSubClass.find(bundle.getString(SUBCLASS)) != null ? HeroSubClass.find(bundle.getString(SUBCLASS)) : HeroSubClass.NONE;
		Belongings.preview( info, bundle );
	}

	public boolean hasTalent( Talent talent ){
		return pointsInTalent(talent) > 0;
	}

	public int pointsInTalent( Talent talent ){
		for (TalentLayerState state : talentLayers.values()){
			Integer points = state.points.get(talent);
			if (points != null) return points;
		}
		return 0;
	}

	/**
	 * 给角色添加一个 talent 层（幂等）。职业创建注入、子职业/技能选择、
	 * 道具添加都走这里——层可无限添加。
	 */
	public void addTalentLayer( String id ){
		TalentLayer layer = TalentLayer.find(id);
		if (layer == null){
			//未知层 id：静默忽略（存档里被移除的层、未来注册的层名）
			return;
		}
		addTalentLayer( layer );
	}

	public void addTalentLayer( TalentLayer layer ){
		if (layer.isMergeSource()) return; //合并源层不可独立添加（子职业层经 mergeSubclassTalents 并入）
		if (talentLayers.containsKey(layer.id)) return;

		TalentLayerState state = new TalentLayerState(layer);

		//变形（metamorph）替换：层内被替换的 talent 用替换值占位
		for (Talent original : metamorphedTalents.keySet()){
			if (state.points.containsKey(original)){
				state.points.remove(original);
				state.points.put(metamorphedTalents.get(original), 0);
			}
		}

		talentLayers.put(layer.id, state);
	}

	public void removeTalentLayer( String id ){
		talentLayers.remove(id);
	}

	/** 把 from 层的 talent 选项并入 into 层（同一点数池；如子职业 t3 并入职业 t3） */
	public void mergeTalentLayer( String intoId, String fromId ){
		TalentLayerState target = talentLayers.get(intoId);
		TalentLayer from = TalentLayer.find(fromId);
		if (target == null || from == null) return;

		for (TalentLayer.TalentEntry entry : from.entries()){
			if (!target.points.containsKey(entry.talent)){
				target.points.put(entry.talent, 0);
			}
		}
	}

	/** 转职：把子职业层的天赋并入职业 t3 层（原版 talents.get(2) 同一张表的语义） */
	public void mergeSubclassTalents( HeroSubClass subCls ){
		if (subCls == null || subCls == HeroSubClass.NONE || subCls.talentLayerId == null) return;
		if (heroClass != null && heroClass.talentLayerIds.length >= 3){
			mergeTalentLayer(heroClass.talentLayerIds[2], subCls.talentLayerId);
		}
	}

	public void upgradeTalent( Talent talent ){
		for (TalentLayerState state : talentLayers.values()){
			if (state.points.containsKey(talent)){
				int max = talent.maxPoints();
				for (TalentLayer.TalentEntry entry : state.layer.entries()){
					if (entry.talent == talent){
						max = entry.maxPoints;
						break;
					}
				}
				state.points.put(talent, Math.min(state.points.get(talent) + 1, max));
				break;
			}
		}
		Talent.onTalentUpgraded(this, talent);
	}

	public int talentPointsSpent( TalentLayer layer ){
		TalentLayerState state = talentLayers.get(layer.id);
		if (state == null) return 0;
		int total = 0;
		for (int i : state.points.values()){
			total += i;
		}
		return total;
	}

	public int bonusTalentPoints( TalentLayer layer ){
		return 0;
	}

	public int talentPointsAvailable( TalentLayer layer ){
		int[] thresholds = layer.levelThresholds();
		int unlock = thresholds[0];
		int cap = thresholds[thresholds.length - 1];

		//与原 tier 公式一致：lvl < 起始-1 不给点；封顶后给满池；中间每级 1 点
		if (lvl < unlock - 1) return 0;

		int spent = talentPointsSpent(layer);
		int bonus = bonusTalentPoints(layer);

		if (lvl >= cap){
			return cap - unlock - spent + bonus;
		} else {
			return 1 + lvl - unlock - spent + bonus;
		}
	}
	
	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title() : subClass.title();
	}

	@Override
	public String name(){
		if (buff(HeroDisguise.class) != null) {
			return buff(HeroDisguise.class).getDisguise().title();
		} else {
			return className();
		}
	}

	@Override
	public void hitSound(float pitch) {
		if (!RingOfForce.fightingUnarmed(this)) {
			belongings.attackingWeapon().hitSound(pitch);
		} else if (RingOfForce.getBuffedBonus(this, RingOfForce.Force.class) > 0) {
			//pitch deepens by 2.5% (additive) per point of strength, down to 75%
			super.hitSound( pitch * GameMath.gate( 0.75f, 1.25f - 0.025f*STR(), 1f) );
		} else {
			super.hitSound(pitch * 1.1f);
		}
	}

	@Override
	public boolean blockSound(float pitch) {
		if ( belongings.weapon() != null && belongings.weapon().defenseFactor(this) >= 4 ){
			Sample.INSTANCE.play( Assets.Sounds.HIT_PARRY, 1, pitch);
			return true;
		}
		return super.blockSound(pitch);
	}

	public void live() {
		for (Buff b : buffs()){
			if (!b.revivePersists) b.detach();
		}
		Buff.affect( this, Regeneration.class );
		Buff.affect( this, Hunger.class );
	}
	
	public int tier() {
		Armor armor = belongings.armor();
		if (armor != null){
			return armor.tier;
		} else {
			return 0;
		}
	}
	
	public boolean shoot( Char enemy, MissileWeapon wep ) {

		attackTarget = enemy;
		boolean wasEnemy = enemy.alignment == Alignment.ENEMY;

		//temporarily set the hero's weapon to the missile weapon being used
		//TODO improve this!
		belongings.thrownWeapon = wep;
		boolean hit = attack( enemy );
		Invisibility.dispel();
		belongings.thrownWeapon = null;

		if (hit && subClass == HeroSubClass.GLADIATOR && wasEnemy){
			Buff.affect( this, Combo.class ).hit( enemy );
		}

		if (hit && heroClass == HeroClass.DUELIST && wasEnemy){
			Buff.affect( this, Sai.ComboStrikeTracker.class).addHit();
		}

		attackTarget = null;
		return hit;
	}
	
	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		boolean result = super.attack(enemy, dmgMulti, dmgBonus, accMulti);
		if (!(belongings.attackingWeapon() instanceof MissileWeapon)){
			if (buff(Talent.PreciseAssaultTracker.class) != null){
				buff(Talent.PreciseAssaultTracker.class).detach();
			} else if (buff(Talent.LiquidAgilACCTracker.class) != null
						&& buff(Talent.LiquidAgilACCTracker.class).uses <= 0){
				buff(Talent.LiquidAgilACCTracker.class).detach();
			}
		}
		return result;
	}

	@Override
	public int attackSkill( Char target ) {
		KindOfWeapon wep = belongings.attackingWeapon();
		
		float accuracy = 1;
		accuracy *= Ring.accuracyMultiplier( this );
		
		//precise assault and liquid agility
		if (!(wep instanceof MissileWeapon)) {
			if ((hasTalent(Talent.PRECISE_ASSAULT) || hasTalent(Talent.LIQUID_AGILITY))
					//does not trigger on ability attacks
					&& belongings.abilityWeapon != wep && buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) == null){

				//non-duelist benefit for precise assault, can stack with liquid agility
				if (heroClass != HeroClass.DUELIST) {
					//persistent +10%/20%/30% ACC for other heroes
					accuracy *= 1f + 0.1f * pointsInTalent(Talent.PRECISE_ASSAULT);
				}

				if (wep instanceof Flail && buff(Flail.SpinAbilityTracker.class) != null){
					//do nothing, this is not a regular attack so don't consume talent fx
				} else if (wep instanceof Crossbow && buff(Crossbow.ChargedShot.class) != null){
					//do nothing, this is not a regular attack so don't consume talent fx
				} else if (buff(Talent.PreciseAssaultTracker.class) != null) {
					// 2x/5x/inf. ACC for duelist if she just used a weapon ability
					switch (pointsInTalent(Talent.PRECISE_ASSAULT)){
						default: case 1:
							accuracy *= 2; break;
						case 2:
							accuracy *= 5; break;
						case 3:
							accuracy *= Float.POSITIVE_INFINITY; break;
					}
				} else if (buff(Talent.LiquidAgilACCTracker.class) != null){
					// 3x/inf. ACC, depending on talent level
					accuracy *= pointsInTalent(Talent.LIQUID_AGILITY) == 2 ? Float.POSITIVE_INFINITY : 3f;
					Talent.LiquidAgilACCTracker buff = buff(Talent.LiquidAgilACCTracker.class);
					buff.uses--;
				}
			}
		} else {
			if (buff(Momentum.class) != null && buff(Momentum.class).freerunning()){
				accuracy *= 1f + pointsInTalent(Talent.PROJECTILE_MOMENTUM)/2f;
			}
		}

		if (buff(Scimitar.SwordDance.class) != null){
			accuracy *= 1.50f;
		}
		
		if (!RingOfForce.fightingUnarmed(this)) {
			return Math.max(1, Math.round(attackSkill * accuracy * wep.accuracyFactor( this, target )));
		} else {
			return Math.max(1, Math.round(attackSkill * accuracy));
		}
	}
	
	@Override
	public int defenseSkill( Char enemy ) {

		if (buff(Combo.ParryTracker.class) != null){
			if (canAttack(enemy) && !isCharmedBy(enemy)){
				Buff.affect(this, Combo.RiposteTracker.class).enemy = enemy;
			}
			return INFINITE_EVASION;
		}

		if (buff(RoundShield.GuardTracker.class) != null){
			return INFINITE_EVASION;
		}
		
		float evasion = defenseSkill;
		
		evasion *= Ring.evasionMultiplier( this );

		if (buff(Talent.LiquidAgilEVATracker.class) != null){
			if (pointsInTalent(Talent.LIQUID_AGILITY) == 1){
				evasion *= 3f;
			} else if (pointsInTalent(Talent.LIQUID_AGILITY) == 2){
				return INFINITE_EVASION;
			}
		}

		if (buff(Quarterstaff.DefensiveStance.class) != null){
			evasion *= 3;
		}
		
		if (paralysed > 0) {
			evasion /= 2;
		}

		if (belongings.armor() != null) {
			evasion = belongings.armor().evasionFactor(this, evasion);

			//stone specifically overrides to 0 always, guaranteed hit
			if (belongings.armor().hasGlyph(Stone.class, this) && !Stone.testingEvasion()){
				return 0;
			}
		}

		return Math.max(1, Math.round(evasion));
	}

	@Override
	public String defenseVerb() {
		Combo.ParryTracker parry = buff(Combo.ParryTracker.class);
		if (parry != null){
			parry.parried = true;
			if (buff(Combo.class) == null || buff(Combo.class).getComboCount() < 9 || pointsInTalent(Talent.ENHANCED_COMBO) < 2){
				parry.detach();
			}
				return Messages.get(Char.class, "def_verb");
		}

		if (buff(RoundShield.GuardTracker.class) != null){
			buff(RoundShield.GuardTracker.class).hasBlocked = true;
			BuffIndicator.refreshHero();
			Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			return Messages.get(RoundShield.GuardTracker.class, "guarded");
		}

		if (buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class) != null){
			buff(MonkEnergy.MonkAbility.Focus.FocusBuff.class).detach();
			if (sprite != null && sprite.visible) {
				Sample.INSTANCE.play(Assets.Sounds.HIT_PARRY, 1, Random.Float(0.96f, 1.05f));
			}
				return Messages.get(Char.class, "def_verb");
		}

		return super.defenseVerb();
	}

	@Override
	public int drRoll() {
		int dr = super.drRoll();

		if (belongings.armor() != null) {
			int armDr = Random.NormalIntRange( belongings.armor().DRMin(), belongings.armor().DRMax());
			if (STR() < belongings.armor().STRReq()){
				armDr -= 2*(belongings.armor().STRReq() - STR());
			}
			if (armDr > 0) dr += armDr;
		}
		if (belongings.weapon() != null && !RingOfForce.fightingUnarmed(this))  {
			int wepDr = Random.NormalIntRange( 0 , belongings.weapon().defenseFactor( this ) );
			if (STR() < ((Weapon)belongings.weapon()).STRReq()){
				wepDr -= 2*(((Weapon)belongings.weapon()).STRReq() - STR());
			}
			if (wepDr > 0) dr += wepDr;
		}

		if (buff(HoldFast.class) != null){
			dr += buff(HoldFast.class).armorBonus();
		}
		
		return dr;
	}
	
	@Override
	public int damageRoll() {
		KindOfWeapon wep = belongings.attackingWeapon();
		int dmg;

		if (!RingOfForce.fightingUnarmed(this)) {
			dmg = wep.damageRoll( this );

			if (!(wep instanceof MissileWeapon)) dmg += RingOfForce.armedDamageBonus(this);
		} else {
			dmg = RingOfForce.damageRoll(this);
			if (RingOfForce.unarmedGetsWeaponAugment(this)){
				dmg = ((Weapon)belongings.attackingWeapon()).augment.damageFactor(dmg);
			}
		}

		PhysicalEmpower emp = buff(PhysicalEmpower.class);
		if (emp != null){
			dmg += emp.dmgBoost;
			emp.left--;
			if (emp.left <= 0) {
				emp.detach();
			}
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
		}

		if (heroClass != HeroClass.DUELIST
				&& hasTalent(Talent.WEAPON_RECHARGING)
				&& (buff(Recharging.class) != null || buff(ArtifactRecharge.class) != null)){
			dmg = Math.round(dmg * 1.025f + (.025f*pointsInTalent(Talent.WEAPON_RECHARGING)));
		}

		if (dmg < 0) dmg = 0;
		return dmg;
	}

	//damage rolls that come from the hero can have their RNG influenced by clover
	public static int heroDamageIntRange(int min, int max ){
		if (Random.Float() < ThirteenLeafClover.alterHeroDamageChance()){
			return ThirteenLeafClover.alterDamageRoll(min, max);
		} else {
			return Random.NormalIntRange(min, max);
		}
	}
	
	@Override
	public float speed() {

		float speed = super.speed();

		speed *= Ring.speedMultiplier(this);
		
		if (belongings.armor() != null) {
			speed = belongings.armor().speedFactor(this, speed);
		}
		
		Momentum momentum = buff(Momentum.class);
		if (momentum != null){
			((HeroSprite)sprite).sprint( momentum.freerunning() ? 1.5f : 1f );
			speed *= momentum.speedMultiplier();
		} else {
			((HeroSprite)sprite).sprint( 1f );
		}

		NaturesPower.naturesPowerTracker natStrength = buff(NaturesPower.naturesPowerTracker.class);
		if (natStrength != null){
			speed *= (2f + 0.25f*pointsInTalent(Talent.GROWING_POWER));
		}

		speed = AscensionChallenge.modifyHeroSpeed(speed);
		
		return speed;
		
	}

	@Override
	public boolean canSurpriseAttack(){
		KindOfWeapon w = belongings.attackingWeapon();
		if (!(w instanceof Weapon))             return true;
		if (RingOfForce.fightingUnarmed(this))  return true;
		if (STR() < ((Weapon)w).STRReq())       return false;
		if (w instanceof Flail)                 return false;

		return super.canSurpriseAttack();
	}

	public boolean canAttack(Char enemy){
		if (enemy == null || pos == enemy.pos || !Actor.chars().contains(enemy)) {
			return false;
		}

		//can always attack adjacent enemies
		if (Dungeon.level.adjacent(pos, enemy.pos)) {
			return true;
		}

		KindOfWeapon wep = Dungeon.hero.belongings.attackingWeapon();

		if (wep != null){
			return wep.canReach(this, enemy.pos);
		} else if (buff(AscendedForm.AscendBuff.class) != null) {
			boolean[] passable = BArray.not(Dungeon.level.solid, null);
			for (Char ch : Actor.chars()) {
				if (ch != this) passable[ch.pos] = false;
			}

			PathFinder.buildDistanceMap(enemy.pos, passable, 3);

			return PathFinder.distance[pos] <= 3;
		} else {
			return false;
		}
	}
	
	public float attackDelay() {
		if (buff(Talent.LethalMomentumTracker.class) != null){
			buff(Talent.LethalMomentumTracker.class).detach();
			return 0;
		}

		float delay = 1f;

		if (!RingOfForce.fightingUnarmed(this)) {
			
			return delay * belongings.attackingWeapon().delayFactor( this );
			
		} else {
			//Normally putting furor speed on unarmed attacks would be unnecessary
			//But there's going to be that one guy who gets a furor+force ring combo
			//This is for that one guy, you shall get your fists of fury!
			float speed = Ring.attackSpeedMultiplier(this);

			//ditto for furor + sword dance!
			if (buff(Scimitar.SwordDance.class) != null){
				speed += 0.6f;
			}

			//and augments + brawler's stance! My goodness, so many options now compared to 2014!
			if (RingOfForce.unarmedGetsWeaponAugment(this)){
				delay = ((Weapon)belongings.weapon).augment.delayFactor(delay);
			}

			return delay/speed;
		}
	}

	@Override
	public void spend( float time ) {
		super.spend(time);
	}

	@Override
	public void spendConstant(float time) {
		super.spendConstant(time);
	}

	public void spendAndNextConstant(float time ) {
		busy();
		spendConstant( time );
		next();
	}

	public void spendAndNext( float time ) {
		busy();
		spend( time );
		next();
	}
	
	@Override
	protected ActionSubmission proposeAction() {

		//calls to dungeon.observe will also update hero's local FOV.
		fieldOfView = Dungeon.level.heroFOV;

		if (buff(Endure.EndureTracker.class) != null){
			buff(Endure.EndureTracker.class).endEnduring();
		}

		if (!ready) {
			//do a full observe (including fog update) if not resting.
			if (!resting || buff(MindVision.class) != null) {
				Dungeon.observe();
			} else {
				//otherwise just directly re-calculate FOV
				Dungeon.level.updateFieldOfView(this, fieldOfView);
			}
		}

		checkVisibleMobs();
		BuffIndicator.refreshHero();
		BuffIndicator.refreshBoss();

		if (paralysed > 0) {

			curAction = null;

			busy();
			spend( TICK );
			return ActionSubmission.idle();
		}

		if(hasTalent(Talent.BARKSKIN) && Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
			Barkskin.conditionallyAppend(this, (lvl*pointsInTalent(Talent.BARKSKIN))/2, 1 );
		}

		if (curAction == null) {

			//resting spends the whole turn in the proposal phase
			if (resting) {
				spendConstant( TIME_TO_REST );
			}

			return ActionSubmission.idle();

		} else {

			resting = false;

			ready = false;

			if (curAction instanceof HeroAction.Move) {
				HeroAction.Move action = (HeroAction.Move) curAction;

				//already arrived - hero moves in place if there is grass to trample
				if (action.dst == pos) {
					if (canSelfTrample()){
						spend( 0.5f / speed() );
						return ActionSubmission.move( action.dst );
					} else {
						return ActionSubmission.idle();
					}
				}

				//fail fast (without spending) if the immediate step is blocked,
				//matching the old zero-cost behavior for wall/occupant taps
				if (Dungeon.level.adjacent( pos, action.dst )
						&& (Actor.findChar( action.dst ) != null
							|| !Dungeon.level.passable[action.dst] && !Dungeon.level.avoid[action.dst])){
					return ActionSubmission.idle();
				}

				//half of the step's cost; the other half is spent when the step executes
				spend( moveStepDelay() / (2f * speed()) );
				return ActionSubmission.move( action.dst );

			} else if (curAction instanceof HeroAction.Attack) {
				HeroAction.Attack action = (HeroAction.Attack) curAction;
				Char target = action.target;

				if (target != null && isCharmedBy( target )){
					GLog.w( Messages.get(Charm.class, "cant_attack"));
					curAction = null;
					return ActionSubmission.idle();
				}

				if (target != null && target.isAlive() && canAttack( target ) && target.invisible == 0){

					//attack delay is cached here as attackDelay() may have side effects,
					//and the second half of the cost is spent on attack completion
					currentAttackDelay = attackDelay();
					spend( currentAttackDelay / 2f );
					return ActionSubmission.attack( target );

				} else {

					//otherwise close in on the target (or its corpse) to attack it
					if (target != null && target.pos != pos && fieldOfView[target.pos]){
						spend( moveStepDelay() / (2f * speed()) );
						return ActionSubmission.move( target.pos );
					}

					return ActionSubmission.idle();
				}

			} else if (curAction instanceof HeroAction.Interact) {
				Char ch = ((HeroAction.Interact)curAction).ch;

				if (ch != null && ch.isAlive() && ch.canInteract( this )){
					return ActionSubmission.interact( ch );
				}

				//otherwise approach the char
				if (ch != null && ch.pos != pos && fieldOfView[ch.pos]){
					spend( moveStepDelay() / (2f * speed()) );
					return ActionSubmission.interact( ch );
				}

				return ActionSubmission.idle();

			} else if (curAction instanceof HeroAction.Buy) {
				if (curAction.dst != pos) spend( moveStepDelay() / (2f * speed()) );
				return new ActionSubmission( ActionSubmission.BUY, curAction.dst );

			} else if (curAction instanceof HeroAction.Alchemy) {
				if (curAction.dst != pos) spend( moveStepDelay() / (2f * speed()) );
				return new ActionSubmission( ActionSubmission.ALCHEMY, curAction.dst );

			} else if (curAction instanceof HeroAction.PickUp) {
				if (curAction.dst != pos) spend( moveStepDelay() / (2f * speed()) );
				return new ActionSubmission( ActionSubmission.PICK_UP, curAction.dst );

			} else if (curAction instanceof HeroAction.OpenChest) {
				if (curAction.dst != pos && !Dungeon.level.adjacent( pos, curAction.dst )){
					spend( moveStepDelay() / (2f * speed()) );
				}
				return new ActionSubmission( ActionSubmission.OPEN_CHEST, curAction.dst );

			} else if (curAction instanceof HeroAction.Unlock) {
				if (!Dungeon.level.adjacent( pos, curAction.dst )){
					spend( moveStepDelay() / (2f * speed()) );
				}
				return new ActionSubmission( ActionSubmission.UNLOCK, curAction.dst );

			} else if (curAction instanceof HeroAction.Mine) {
				if (Dungeon.level.adjacent( pos, curAction.dst )){
					spend( 0.5f * TICK );
				} else {
					spend( moveStepDelay() / (2f * speed()) );
				}
				return new ActionSubmission( ActionSubmission.MINE, curAction.dst );

			} else if (curAction instanceof HeroAction.LvlTransition) {
				LevelTransition transition = Dungeon.level.getTransition( curAction.dst );
				if (transition == null || !transition.inside( pos )){
					spend( moveStepDelay() / (2f * speed()) );
				}
				return new ActionSubmission( ActionSubmission.TRANSITION, curAction.dst );

			}

			return ActionSubmission.idle();
		}
	}

	@Override
	protected ActionResult adjudicate( ActionSubmission sub ) {
		if (sub.name == ActionSubmission.ATTACK) {

			//the proposal already gates these conditions, this is a defensive re-check
			//in case the world changed between proposal and execution
			Char target = (Char)sub.param;
			if (target == null || !target.isAlive() || !Actor.chars().contains( target )){
				return ActionResult.fail( FailCause.TARGET_GONE );
			}
			if (!canAttack( target ) || target.invisible > 0){
				return ActionResult.fail( FailCause.TARGET_OUT_OF_RANGE );
			}

		}
		return ActionResult.OK;
	}

	@Override
	protected boolean doAction( ActionSubmission sub, ActionResult result ) {

		if (sub.name == ActionSubmission.IDLE) {

			if (paralysed > 0){
				next();
				return false;
			}

			//if we just loaded into a level and have a search buff, make sure to process them
			if(Actor.now() == 0){
				if (buff(Foresight.class) != null){
					search(false);
				} else if (buff(TalismanOfForesight.Foresight.class) != null){
					buff(TalismanOfForesight.Foresight.class).checkAwareness();
				}
			}

			if (resting){
				next();
				return false;
			}

			ready();
			return false;
		}

		if (!result.isOk()) {

			//adjudicated failures are a defensive net - proposals gate these conditions,
			//so normally nothing arrives here. fail costs are already paid in the proposal.
			if (sub.name == ActionSubmission.ATTACK){
				attackTarget = null;
				if (result.cause == FailCause.FROST_AURA){
					GLog.w( Messages.get(FrostAura.class, "blocked") );
				}
			}
			ready();
			return false;
		}

		if (sub.name == ActionSubmission.MOVE) {
			return doMove( (Integer)sub.param );

		} else if (sub.name == ActionSubmission.ATTACK) {
			return doAttack( (Char)sub.param );

		} else if (sub.name == ActionSubmission.INTERACT) {
			return actInteract( (HeroAction.Interact)curAction );

		} else if (sub.name == ActionSubmission.BUY) {
			return actBuy( (HeroAction.Buy)curAction );

		} else if (sub.name == ActionSubmission.PICK_UP) {
			return actPickUp( (HeroAction.PickUp)curAction );

		} else if (sub.name == ActionSubmission.OPEN_CHEST) {
			return actOpenChest( (HeroAction.OpenChest)curAction );

		} else if (sub.name == ActionSubmission.UNLOCK) {
			return actUnlock( (HeroAction.Unlock)curAction );

		} else if (sub.name == ActionSubmission.MINE) {
			return actMine( (HeroAction.Mine)curAction );

		} else if (sub.name == ActionSubmission.TRANSITION) {
			return actTransition( (HeroAction.LvlTransition)curAction );

		} else if (sub.name == ActionSubmission.ALCHEMY) {
			return actAlchemy( (HeroAction.Alchemy)curAction );
		}

		ready();
		return false;
	}
	
	public void busy() {
		ready = false;
	}
	
	private void ready() {
		if (sprite.looping()) sprite.idle();
		curAction = null;
		damageInterrupt = true;
		waitOrPickup = false;
		ready = true;
		canSelfTrample = true;

		AttackIndicator.updateState();
		
		GameScene.ready();
	}
	
	public void interrupt() {
		if (isAlive() && curAction != null &&
			((curAction instanceof HeroAction.Move && curAction.dst != pos) ||
			(curAction instanceof HeroAction.LvlTransition))) {
			lastAction = curAction;
		}
		curAction = null;
		GameScene.resetKeyHold();
		resting = false;
	}
	
	public void resume() {
		curAction = lastAction;
		lastAction = null;
		damageInterrupt = false;
		next();
	}

	private boolean canSelfTrample = false;
	public boolean canSelfTrample(){
		return canSelfTrample && !rooted && !flying &&
				//standing in high grass
				(Dungeon.level.map[pos] == Terrain.HIGH_GRASS ||
				//standing in furrowed grass and not huntress
				(heroClass != HeroClass.HUNTRESS && Dungeon.level.map[pos] == Terrain.FURROWED_GRASS) ||
				//standing on a plant
				Dungeon.level.plants.get(pos) != null);
	}
	
	//executes a single movement step (proposal already spent half of the step's cost)
	private boolean doMove( int dst ) {

		if (dst == pos) {

			//hero moves in place if there is grass to trample
			//(only possible via a Move curAction; the proposal routed other arrivals to IDLE)
			if (canSelfTrample()){
				canSelfTrample = false;
				Dungeon.level.pressCell(pos);
				spend( 0.5f / speed() );
				next();
				return false;
			}

			ready();
			return false;
		}

		if (getCloser( dst )) {
			if (curAction instanceof HeroAction.Move) {
				canSelfTrample = false;
			}
			return true;
		}

		ready();
		return false;
	}
	
	private boolean actInteract( HeroAction.Interact action ) {
		
		Char ch = action.ch;

		if (ch.isAlive() && ch.canInteract(this)) {
			
			ready();
			sprite.turnTo( pos, ch.pos );
			return ch.interact(this);
			
		} else {
			
			if (fieldOfView[ch.pos] && getCloser( ch.pos )) {

				return true;

			} else {
				ready();
				return false;
			}
			
		}
	}
	
	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst) {

			ready();
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Type.FOR_SALE && heap.size() == 1) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndTradeItem( heap ) );
					}
				});
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actAlchemy( HeroAction.Alchemy action ) {
		int dst = action.dst;
		if (Dungeon.level.distance(dst, pos) <= 1) {

			ready();
			
			AlchemistsToolkit.kitEnergy kit = buff(AlchemistsToolkit.kitEnergy.class);
			if (kit != null && kit.isCursed()){
				GLog.w( Messages.get(AlchemistsToolkit.class, "cursed"));
				return false;
			}

			AlchemyScene.clearToolkit();
			ShatteredPixelDungeon.switchScene(AlchemyScene.class);
			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	//used to keep track if the wait/pickup action was used
	// so that the hero spends a turn even if the fail to pick up an item
	public boolean waitOrPickup = false;

	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {
				Item item = heap.peek();
				if (item.doPickUp( this )) {
					heap.pickUp();

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key
							|| item instanceof Guidebook
							|| (item instanceof MissileWeapon && !MissileWeapon.UpgradedSetTracker.pickupValid(this, (MissileWeapon) item))) {
						//Do Nothing
					} else if (item instanceof DarkGold) {
						DarkGold existing = belongings.getItem(DarkGold.class);
						if (existing != null){
							if (existing.quantity() >= 40) {
								GLog.p(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
							} else {
								GLog.i(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
							}
						}
					} else {

						//TODO make all unique items important? or just POS / SOU?
						boolean important = item.unique && item.isIdentified() &&
								(item instanceof Scroll || item instanceof Potion);
						if (important) {
							GLog.p( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
						} else {
							GLog.i( Messages.capitalize(Messages.get(this, "you_now_have", item.name())) );
						}
					}
					
					curAction = null;
				} else {

					if (waitOrPickup) {
						spendAndNextConstant(TIME_TO_REST);
					}

					//allow the hero to move between levels even if they can't collect the item
					if (Dungeon.level.getTransition(pos) != null){
						throwItems();
					} else {
						heap.sprite.drop();
					}

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key) {
						//Do Nothing
					} else {
						GLog.newLine();
						GLog.n(Messages.capitalize(Messages.get(this, "you_cant_have", item.name())));
					}

					ready();
				}
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Dungeon.level.adjacent( pos, dst ) || pos == dst) {
			path = null;
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Type.HEAP && heap.type != Type.FOR_SALE)) {
				
				if ((heap.type == Type.LOCKED_CHEST && Notes.keyCount(new Key(Key.Kind.GOLDEN, Dungeon.depth)) < 1)
					|| (heap.type == Type.CRYSTAL_CHEST && Notes.keyCount(new Key(Key.Kind.CRYSTAL, Dungeon.depth)) < 1)){

						GLog.w( Messages.get(this, "locked_chest") );
						ready();
						return false;

				}
				
				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.Sounds.TOMB );
					PixelScene.shake( 1, 0.5f );
					break;
				case SKELETON:
				case REMAINS:
					break;
				default:
					Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				}
				
				sprite.operate( dst );
				
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Dungeon.level.adjacent( pos, doorCell )) {
			path = null;
			
			boolean hasKey = false;
			int door = Dungeon.level.map[doorCell];
			
			if (door == Terrain.LOCKED_DOOR
					&& Notes.keyCount(new Key(Key.Kind.IRON, Dungeon.depth)) > 0) {
				
				hasKey = true;

			} else if (door == Terrain.HERO_LKD_DR){

				if (belongings.getItem(SkeletonKey.class) != null
						&& !belongings.getItem(SkeletonKey.class).cursed){
					GLog.i(Messages.get(SkeletonKey.class, "locked_with_key"));
					ready();
					return false;
				} else {
					hasKey = true;
				}
				
			} else if (door == Terrain.CRYSTAL_DOOR
					&& Notes.keyCount(new Key(Key.Kind.CRYSTAL, Dungeon.depth)) > 0) {

				hasKey = true;

			} else if (door == Terrain.LOCKED_EXIT
					&& Notes.keyCount(new Key(Key.Kind.WORN, Dungeon.depth)) > 0) {

				hasKey = true;
				
			}
			
			if (hasKey) {
				
				sprite.operate( doorCell );
				
				Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
				
			} else {
				GLog.w( Messages.get(this, "locked_door") );
				ready();
			}

			return false;

		} else if (getCloser( doorCell )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actMine(HeroAction.Mine action){
		if (Dungeon.level.adjacent(pos, action.dst)){
			path = null;
			if ((Dungeon.level.map[action.dst] == Terrain.WALL
					|| Dungeon.level.map[action.dst] == Terrain.WALL_DECO
					|| Dungeon.level.map[action.dst] == Terrain.MINE_CRYSTAL
					|| Dungeon.level.map[action.dst] == Terrain.MINE_BOULDER)
				&& Dungeon.level.insideMap(action.dst)){
				sprite.attack(action.dst, new Callback() {
					@Override
					public void call() {

						boolean crystalAdjacent = false;
						for (int i : PathFinder.NEIGHBOURS8) {
							if (Dungeon.level.map[action.dst + i] == Terrain.MINE_CRYSTAL){
								crystalAdjacent = true;
								break;
							}
						}

						//1 hunger spent total
						if (Dungeon.level.map[action.dst] == Terrain.WALL_DECO){
							DarkGold gold = new DarkGold();
							if (gold.doPickUp( Dungeon.hero )) {
								DarkGold existing = Dungeon.hero.belongings.getItem(DarkGold.class);
								if (existing != null && existing.quantity()%5 == 0){
									if (existing.quantity() >= 40) {
										GLog.p(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
									} else {
										GLog.i(Messages.get(DarkGold.class, "you_now_have", existing.quantity()));
									}
								}
								spend(-Actor.TICK); //picking up the gold doesn't spend a turn here
							} else {
								Dungeon.level.drop( gold, pos ).sprite.drop();
							}
							PixelScene.shake(0.5f, 0.5f);
							CellEmitter.center( action.dst ).burst( Speck.factory( Speck.STAR ), 7 );
							Sample.INSTANCE.play( Assets.Sounds.EVOKE );
							Level.set( action.dst, Terrain.EMPTY_DECO );

							//mining gold doesn't break crystals
							crystalAdjacent = false;

						//4 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.WALL){
							buff(Hunger.class).affectHunger(-3);
							PixelScene.shake(0.5f, 0.5f);
							CellEmitter.get( action.dst ).burst( Speck.factory( Speck.ROCK ), 2 );
							Sample.INSTANCE.play( Assets.Sounds.MINE );
							Level.set( action.dst, Terrain.EMPTY_DECO );

						//1 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.MINE_CRYSTAL){
							Splash.at(action.dst, 0xFFFFFF, 5);
							Sample.INSTANCE.play( Assets.Sounds.SHATTER );
							Level.set( action.dst, Terrain.EMPTY );

						//1 hunger spent total
						} else if (Dungeon.level.map[action.dst] == Terrain.MINE_BOULDER){
							Splash.at(action.dst, 0x555555, 5);
							Sample.INSTANCE.play( Assets.Sounds.MINE, 0.6f );
							Level.set( action.dst, Terrain.EMPTY_DECO );
						}

						for (int i : PathFinder.NEIGHBOURS9) {
							Dungeon.level.discoverable[action.dst + i] = true;
						}
						for (int i : PathFinder.NEIGHBOURS9) {
							GameScene.updateMap( action.dst+i );
						}

						if (crystalAdjacent){
							sprite.parent.add(new Delayer(0.2f){
								@Override
								protected void onComplete() {
									boolean broke = false;
									for (int i : PathFinder.NEIGHBOURS8) {
										if (Dungeon.level.map[action.dst+i] == Terrain.MINE_CRYSTAL){
											Splash.at(action.dst+i, 0xFFFFFF, 5);
											Level.set( action.dst+i, Terrain.EMPTY );
											broke = true;
										}
									}
									if (broke){
										Sample.INSTANCE.play( Assets.Sounds.SHATTER );
									}

									for (int i : PathFinder.NEIGHBOURS9) {
										GameScene.updateMap( action.dst+i );
									}
									//only half of the turn - the proposal already spent the other half
									spendAndNext( 0.5f * TICK );
									ready();
								}
							});
						} else {
							spendAndNext( 0.5f * TICK ); //only half of the turn - the proposal already spent the other half
							ready();
						}

						Dungeon.observe();
					}
				});
			} else {
				ready();
			}
			return false;
		} else if (getCloser( action.dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actTransition(HeroAction.LvlTransition action ) {
		int stairs = action.dst;
		LevelTransition transition = Dungeon.level.getTransition(stairs);

		if (rooted) {
			PixelScene.shake(1, 1f);
			ready();
			return false;

		} else if (!Dungeon.level.locked && transition != null && transition.inside(pos)) {

			if (Dungeon.level.activateTransition(this, transition)){
				curAction = null;
			} else {
				ready();
			}

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	//executes an attack on a target the proposal found attackable
	//(proposal spent half of the attack delay; the rest is spent on attack completion)
	private boolean doAttack( Char target ) {

		attackTarget = target;

		if (heroClass != HeroClass.DUELIST
				&& hasTalent(Talent.AGGRESSIVE_BARRIER)
				&& buff(Talent.AggressiveBarrierCooldown.class) == null
				&& (HP / (float)HT) <= 0.5f){
			int shieldAmt = 1 + 2*pointsInTalent(Talent.AGGRESSIVE_BARRIER);
			Buff.affect(this, Barrier.class).setShield(shieldAmt);
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldAmt), FloatingText.SHIELDING);
			Buff.affect(this, Talent.AggressiveBarrierCooldown.class, 50f);

		}
		//attack target cleared on onAttackComplete
		sprite.attack( target.pos );

		return false;
	}

	public Char attackTarget(){
		return attackTarget;
	}
	
	public void rest( boolean fullRest ) {
		spendAndNextConstant( TIME_TO_REST );
		if (hasTalent(Talent.HOLD_FAST)){
			Buff.affect(this, HoldFast.class).pos = pos;
		}
		if (hasTalent(Talent.PATIENT_STRIKE)){
			Buff.affect(Dungeon.hero, Talent.PatientStrikeTracker.class).pos = Dungeon.hero.pos;
		}
		if (!fullRest) {
			if (sprite != null) {
				sprite.showStatus(CharSprite.DEFAULT, Messages.get(this, "wait"));
			}
		}
		resting = fullRest;
	}
	
	@Override
	public int attackProc( final Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );

		KindOfWeapon wep;
		if (RingOfForce.fightingUnarmed(this) && !RingOfForce.unarmedGetsWeaponEnchantment(this)){
			wep = null;
		} else {
			wep = belongings.attackingWeapon();
		}

		damage = Talent.onAttackProc( this, enemy, damage );

		if (wep != null) {
			damage = wep.proc( this, enemy, damage );
		} else {
			boolean wasEnemy = enemy.alignment == Alignment.ENEMY;
			if (buff(BodyForm.BodyFormBuff.class) != null
					&& buff(BodyForm.BodyFormBuff.class).enchant() != null){
				damage = buff(BodyForm.BodyFormBuff.class).enchant().proc(new Sword(), this, enemy, damage);
			}
			if (!wasEnemy || enemy.alignment == Alignment.ENEMY) {
				if (buff(HolyWeapon.HolyWepBuff.class) != null) {
					int dmg = subClass == HeroSubClass.PALADIN ? 6 : 2;
					Proc.damage(new DamageInfo().offender(this).defender(enemy).weapon(belongings.attackingWeapon()).way(DamageWay.MAGIC).type(DamageType.PHYSICAL).solidDamage(Math.round(dmg * Weapon.Enchantment.genericProcChanceMultiplier(this))));
				}
				if (buff(Smite.SmiteTracker.class) != null) {
					Proc.damage(new DamageInfo().offender(this).defender(enemy).weapon(belongings.attackingWeapon()).way(DamageWay.MAGIC).type(DamageType.PHYSICAL).solidDamage(Smite.bonusDmg(this, enemy)));
				}
			}
		}
		
		if (subClass == HeroSubClass.SNIPER) {
			if (wep instanceof MissileWeapon && !(wep instanceof SpiritBow.SpiritArrow) && enemy != this) {
				Actor.add(new Actor() {

					{
						actPriority = VFX_PRIO;
					}

					@Override
					protected boolean act() {
						if (enemy.isAlive()) {
							if (hasTalent(Talent.SHARED_UPGRADES)){
								int levelBonus = Math.min( 2*pointsInTalent(Talent.SHARED_UPGRADES), wep.buffedLvl() );
								// bonus dmg is 16.67% x weapon level, max of 2/4/6
								float bonusDmg = levelBonus/6f;
								Buff.prolong(Hero.this, SnipersMark.class, SnipersMark.DURATION + levelBonus).set(enemy.id(), bonusDmg);
							} else {
								Buff.prolong(Hero.this, SnipersMark.class, SnipersMark.DURATION).set(enemy.id(), 0);
							}
						}
						Actor.remove(this);
						return true;
					}
				});
			}
		}

		return damage;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		
		if (damage > 0 && subClass == HeroSubClass.BERSERKER){
			Berserk berserk = Buff.affect(this, Berserk.class);
			berserk.damage(damage);
		}
		
		if (belongings.armor() != null) {
			damage = belongings.armor().proc( enemy, this, damage );
		} else {
			if (buff(BodyForm.BodyFormBuff.class) != null
				&& buff(BodyForm.BodyFormBuff.class).glyph() != null){
				damage = buff(BodyForm.BodyFormBuff.class).glyph().proc(new Armor(), enemy, this, damage);
			}
			if (buff(HolyWard.HolyArmBuff.class) != null){
				int blocking = subClass == HeroSubClass.PALADIN ? 3 : 1;
				damage -= Math.round(blocking * Armor.Glyph.genericProcChanceMultiplier(enemy));
			}
		}

		WandOfLivingEarth.RockArmor rockArmor = buff(WandOfLivingEarth.RockArmor.class);
		if (rockArmor != null) {
			damage = rockArmor.absorb(damage);
		}
		
		return super.defenseProc( enemy, damage );
	}

	@Override
	public int glyphLevel(Class<? extends Armor.Glyph> cls) {
		if (belongings.armor() != null && belongings.armor().hasGlyph(cls, this)){
			return Math.max(super.glyphLevel(cls), belongings.armor.buffedLvl());
		} else if (buff(BodyForm.BodyFormBuff.class) != null
				&& buff(BodyForm.BodyFormBuff.class).glyph() != null
				&& buff(BodyForm.BodyFormBuff.class).glyph().getClass() == cls){
			return belongings.armor() != null ? belongings.armor.buffedLvl() : 0;
		} else {
			return super.glyphLevel(cls);
		}
	}

	@Override
	public void damage( int dmg, Object src ) {
		if (buff(TimekeepersHourglass.timeStasis.class) != null
				|| buff(TimeStasis.class) != null) {
			return;
		}

		//TODO hero cannot take damage in the vault tester area
		if (Dungeon.depth > 15 && Dungeon.branch == 1){
			dmg = 0;
		}

		//regular damage interrupt, triggers on any damage except specific mild DOT effects
		// unless the player recently hit 'continue moving', in which case this is ignored
		if (!(src instanceof Hunger || src instanceof Viscosity.DeferedDamage) && damageInterrupt) {
			interrupt();
		}

		if (this.buff(Drowsy.class) != null){
			Buff.detach(this, Drowsy.class);
			GLog.w( Messages.get(this, "pain_resist") );
		}

		//temporarily assign to a float to avoid rounding a bunch
		float damage = dmg;

		Endure.EndureTracker endure = buff(Endure.EndureTracker.class);
		if (!(src instanceof Char)){
			//reduce damage here if it isn't coming from a character (if it is we already reduced it)
			if (endure != null){
				damage = endure.adjustDamageTaken(dmg);
			}
			//the same also applies to challenge scroll damage reduction
			if (buff(ScrollOfChallenge.ChallengeArena.class) != null){
				damage *= 0.67f;
			}
			//and to monk meditate damage reduction
			if (buff(MonkEnergy.MonkAbility.Meditate.MeditateResistance.class) != null){
				damage *= 0.2f;
			}
		}

		//unused, could be removed
		CapeOfThorns.Thorns thorns = buff( CapeOfThorns.Thorns.class );
		if (thorns != null) {
			damage = thorns.proc((int)damage, (src instanceof Char ? (Char)src : null),  this);
		}

		if (buff(Talent.WarriorFoodImmunity.class) != null){
			if (pointsInTalent(Talent.IRON_STOMACH) == 1)       damage /= 4f;
			else if (pointsInTalent(Talent.IRON_STOMACH) == 2)  damage = 0;
		}

		dmg = Math.round(damage);

		//we ceil this one to avoid letting the player easily take 0 dmg from tenacity early
		dmg = (int)Math.ceil(dmg * Ring.damageMultiplier( this ));

		int preHP = HP + shielding();
		if (src instanceof Hunger) preHP -= shielding();
		super.damage( dmg, src );
		int postHP = HP + shielding();
		if (src instanceof Hunger) postHP -= shielding();
		int effectiveDamage = preHP - postHP;

		if (effectiveDamage <= 0) return;

		if (buff(Challenge.DuelParticipant.class) != null){
			buff(Challenge.DuelParticipant.class).addDamage(effectiveDamage);
		}

		//flash red when hit for serious damage.
		float percentDMG = effectiveDamage / (float)preHP; //percent of current HP that was taken
		float percentHP = 1 - ((HT - postHP) / (float)HT); //percent health after damage was taken
		// The flash intensity increases primarily based on damage taken and secondarily on missing HP.
		float flashIntensity = 0.25f * (percentDMG * percentDMG) / percentHP;
		//if the intensity is very low don't flash at all
		if (flashIntensity >= 0.05f){
			flashIntensity = Math.min(1/3f, flashIntensity); //cap intensity at 1/3
			GameScene.flash( (int)(0xFF*flashIntensity) << 16 );
			if (isAlive()) {
				if (flashIntensity >= 1/6f) {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_CRITICAL, 1/3f + flashIntensity * 2f);
				} else {
					Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN, 1/3f + flashIntensity * 4f);
				}
				//hero gets interrupted on taking serious damage, regardless of any other factor
				interrupt();
				damageInterrupt = true;
			}
		}
	}
	
	public void checkVisibleMobs() {
		ArrayList<Mob> visible = new ArrayList<>();

		boolean newMob = false;

		Mob target = null;
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (fieldOfView[ m.pos ] && m.landmark() != null){
				Notes.add(m.landmark());
			}

			if (fieldOfView[ m.pos ] && m.alignment == Alignment.ENEMY) {
				visible.add(m);
				if (!visibleEnemies.contains( m )) {
					newMob = true;
				}

				//only do a simple check for mind visioned enemies, better performance
				if ((!mindVisionEnemies.contains(m) && QuickSlotButton.autoAim(m) != -1)
						|| (mindVisionEnemies.contains(m) && new Ballistica( pos, m.pos, Ballistica.PROJECTILE ).collisionPos == m.pos)) {
					if (target == null) {
						target = m;
					} else if (distance(target) > distance(m)) {
						target = m;
					}
				}
			}
		}

		Char lastTarget = QuickSlotButton.lastTarget;
		if (target != null && (lastTarget == null ||
							!lastTarget.isAlive() || !lastTarget.isActive() ||
							lastTarget.alignment == Alignment.ALLY ||
							!fieldOfView[lastTarget.pos])){
			QuickSlotButton.target(target);
		}
		
		if (newMob) {
			if (resting){
				Dungeon.observe();
			}
			interrupt();
		}

		visibleEnemies = visible;

		//we also scan for blob landmarks here
		for (Blob b : Dungeon.level.blobs.values().toArray(new Blob[0])){
			if (b.volume > 0 && b.landmark() != null && !Notes.contains(b.landmark())){
				int cell;
				boolean found = false;
				//if a single cell within the blob is visible, we add the landmark
				for (int i=b.area.top; i < b.area.bottom; i++) {
					for (int j = b.area.left; j < b.area.right; j++) {
						cell = j + i* Dungeon.level.width();
						if (fieldOfView[cell] && b.cur[cell] > 0) {
							Notes.add( b.landmark() );
							found = true;
							break;
						}
					}
					if (found) break;
				}

				//Clear blobs that only exist for landmarks.
				// Might want to make this a properly if it's used more
				if (found && b instanceof WeakFloorRoom.WellID){
					b.fullyClear();
				}
			}
		}
	}
	
	public int visibleEnemies() {
		return visibleEnemies.size();
	}
	
	public Mob visibleEnemy( int index ) {
		return visibleEnemies.get(index % visibleEnemies.size());
	}

	public ArrayList<Mob> getVisibleEnemies(){
		return new ArrayList<>(visibleEnemies);
	}
	
	private boolean walkingToVisibleTrapInFog = false;

	//the delay for a single step of movement (0 with GreaterHaste, which is charged separately).
	//shared between the proposal half and the execution half of each step's cost.
	private float moveStepDelay(){
		return buff(GreaterHaste.class) != null ? 0f : 1f;
	}

	private boolean getCloser( final int target ) {

		if (target == pos)
			return false;

		if (rooted) {
			PixelScene.shake( 1, 1f );
			return false;
		}
		
		int step = -1;
		
		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null) {
				if (Dungeon.level.passable[target] || Dungeon.level.avoid[target]) {
					step = target;
				}
				if (walkingToVisibleTrapInFog
						&& Dungeon.level.traps.get(target) != null
						&& Dungeon.level.traps.get(target).visible
						&& Dungeon.level.traps.get(target).active){
					return false;
				}
			}
			
		} else {

			boolean newPath = false;
			if (path == null || path.isEmpty() || !Dungeon.level.adjacent(pos, path.getFirst()))
				newPath = true;
			else if (path.getLast() != target)
				newPath = true;
			else {
				if (!Dungeon.level.passable[path.get(0)] || Actor.findChar(path.get(0)) != null) {
					newPath = true;
				}
			}

			if (newPath) {

				int len = Dungeon.level.length();
				boolean[] p = Dungeon.level.passable;
				boolean[] v = Dungeon.level.visited;
				boolean[] m = Dungeon.level.mapped;
				boolean[] passable = new boolean[len];
				for (int i = 0; i < len; i++) {
					passable[i] = p[i] && (v[i] || m[i]);
				}

				PathFinder.Path newpath = Dungeon.findPath(this, target, passable, fieldOfView, true);
				if (newpath != null && path != null && newpath.size() > 2*path.size()){
					path = null;
				} else {
					path = newpath;
				}
			}

			if (path == null) return false;
			step = path.removeFirst();

		}

		if (step != -1) {

			float delay = moveStepDelay();

			if (Dungeon.level.pit[step] && !Dungeon.level.solid[step]
					&& (!flying || buff(Levitation.class) != null && buff(Levitation.class).detachesWithinDelay(delay / speed()))){
				if (!Chasm.jumpConfirmed){
					Chasm.heroJump(this);
					interrupt();
				} else {
					flying = false;
					remove(buff(Levitation.class)); //directly remove to prevent cell pressing
					Chasm.heroFall(target);
				}
				canSelfTrample = false;
				return false;
			}

			if (buff(GreaterHaste.class) != null){
				buff(GreaterHaste.class).spendMove();
			}

			if (subClass == HeroSubClass.FREERUNNER){
				Buff.affect(this, Momentum.class).gainStack();
			}
			
			sprite.move(pos, step);
			move(step);

			//only half of the step's cost - the proposal already spent the other half
			spend( delay / (2f * speed()) );

			search(false);

			return true;

		} else {

			return false;
			
		}

	}
	
	public boolean handle( int cell ) {
		
		if (cell == -1) {
			return false;
		}

		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView( this, fieldOfView );
		}

		if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]
				&& Dungeon.level.traps.get(cell) != null
				&& Dungeon.level.traps.get(cell).visible
				&& Dungeon.level.traps.get(cell).active) {
			walkingToVisibleTrapInFog = true;
		} else {
			walkingToVisibleTrapInFog = false;
		}
		
		Char ch = Actor.findChar( cell );
		Heap heap = Dungeon.level.heaps.get( cell );

		if (Dungeon.level.map[cell] == Terrain.ALCHEMY && cell != pos) {
			
			curAction = new HeroAction.Alchemy( cell );
			
		} else if (fieldOfView[cell] && ch instanceof Mob) {

			if (((Mob) ch).heroShouldInteract()) {
				curAction = new HeroAction.Interact( ch );
			} else {
				curAction = new HeroAction.Attack( ch );
			}

		//TODO perhaps only trigger this if hero is already adjacent? reducing mistaps
		} else if (Dungeon.level instanceof MiningLevel &&
					belongings.getItem(Pickaxe.class) != null &&
				(Dungeon.level.map[cell] == Terrain.WALL
						|| Dungeon.level.map[cell] == Terrain.WALL_DECO
						|| Dungeon.level.map[cell] == Terrain.MINE_CRYSTAL
						|| Dungeon.level.map[cell] == Terrain.MINE_BOULDER)){

			curAction = new HeroAction.Mine( cell );

		} else if (heap != null
				//moving to an item doesn't auto-pickup when enemies are near...
				&& (visibleEnemies.size() == 0 || cell == pos ||
				//...but only for standard heaps. Chests and similar open as normal.
				(heap.type != Type.HEAP && heap.type != Type.FOR_SALE))) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().value() > 0 ?
					new HeroAction.Buy( cell ) :
					new HeroAction.PickUp( cell );
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}
			
		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR
				|| Dungeon.level.map[cell] == Terrain.HERO_LKD_DR
				|| Dungeon.level.map[cell] == Terrain.CRYSTAL_DOOR
				|| Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {
			
			curAction = new HeroAction.Unlock( cell );
			
		} else if (Dungeon.level.getTransition(cell) != null
				//moving to a transition doesn't automatically trigger it when enemies are near
				&& (visibleEnemies.size() == 0 || cell == pos)
				&& !Dungeon.level.locked
				&& !Dungeon.level.plants.containsKey(cell)
				&& (Dungeon.depth < 26 || Dungeon.level.getTransition(cell).type == LevelTransition.Type.REGULAR_ENTRANCE) ) {

			curAction = new HeroAction.LvlTransition( cell );
			
		}  else {
			
			curAction = new HeroAction.Move( cell );
			lastAction = null;
			
		}

		return true;
	}
	
	public void earnExp( int exp, Class source ) {
		// Intentionally ignored: the showcase starts at level 20 and has no XP loop.
	}
	
	public int maxExp() {
		return maxExp( lvl );
	}
	
	public static int maxExp( int lvl ){
		return 5 + lvl * 5;
	}
	
	public boolean isStarving() {
		return Buff.affect(this, Hunger.class).isStarving();
	}
	
	@Override
	public boolean add( Buff buff ) {

		if (buff.type == Buff.buffType.NEGATIVE &&
				(buff(TimekeepersHourglass.timeStasis.class) != null || buff(TimeStasis.class) != null)) {
			return false;
		}

		boolean added = super.add( buff );

		if (sprite != null && added) {
			String msg = buff.heroMessage();
			if (msg != null){
				GLog.w(msg);
			}

			if (buff instanceof Paralysis || buff instanceof Vertigo) {
				interrupt();
			}

		}
		
		BuffIndicator.refreshHero();

		return added;
	}
	
	@Override
	public boolean remove( Buff buff ) {
		if (super.remove( buff )) {
			BuffIndicator.refreshHero();
			return true;
		}
		return false;
	}
	
	@Override
	protected synchronized void onRemove() {
		//same as super, except we retain charger for rankings purposes
		for (Buff buff : buffs()) {
			if (buff instanceof MeleeWeapon.Charger){
				Actor.remove(buff);
			} else {
				buff.detach();
			}
		}
	}

	@Override
	public void die( Object cause ) {
		
		curAction = null;

		Ankh ankh = null;

		//look for ankhs in player inventory, prioritize ones which are blessed.
		for (Ankh i : belongings.getAllItems(Ankh.class)){
			if (ankh == null || i.isBlessed()) {
				ankh = i;
			}
		}

		if (ankh != null) {
			interrupt();

			if (ankh.isBlessed()) {
				this.HP = HT / 4;

				PotionOfHealing.cure(this);
				Buff.prolong(this, Invulnerability.class, Invulnerability.DURATION);

				SpellSprite.show(this, SpellSprite.ANKH);
				GameScene.flash(0x80FFFF40);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				GLog.w(Messages.get(this, "revive"));
				Statistics.ankhsUsed++;
				Catalog.countUse(Ankh.class);

				ankh.detach(belongings.backpack);

				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayAnhk();
						return;
					}
				}
			} else {

				//this is hacky, basically we want to declare that a wndResurrect exists before
				//it actually gets created. This is important so that the game knows to not
				//delete the run or submit it to rankings, because a WndResurrect is about to exist
				//this is needed because the actual creation of the window is delayed here
				WndResurrect.instance = new Object();
				Ankh finalAnkh = ankh;
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndResurrect(finalAnkh) );
					}
				});

				if (cause instanceof Hero.Doom) {
					((Hero.Doom)cause).onDeath();
				}

				SacrificialFire.Marked sacMark = buff(SacrificialFire.Marked.class);
				if (sacMark != null){
					sacMark.detach();
				}

			}
			return;
		}
		
		Actor.fixTime();
		super.die( cause );
		reallyDie( cause );
	}
	
	public static void reallyDie( Object cause ) {
		
		int length = Dungeon.level.length();
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Dungeon.level.discoverable;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					Dungeon.level.discover( i );
				}
			}
		}
		
		Bones.leave();
		
		Dungeon.observe();
		GameScene.updateFog();
				
		Dungeon.hero.belongings.identify();

		int pos = Dungeon.hero.pos;

		ArrayList<Integer> passable = new ArrayList<>();
		for (Integer ofs : PathFinder.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Dungeon.level.passable[cell] || Dungeon.level.avoid[cell]) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );

		ArrayList<Item> items = new ArrayList<>(Dungeon.hero.belongings.backpack.items);
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}

			Item item = Random.element( items );
			Dungeon.level.drop( item, cell ).sprite.drop( pos );
			items.remove( item );
		}

		for (Char c : Actor.chars()){
			if (c instanceof DriedRose.GhostHero){
				((DriedRose.GhostHero) c).sayHeroKilled();
			}
		}

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.gameOver();
				Sample.INSTANCE.play( Assets.Sounds.DEATH );
			}
		});

		if (cause instanceof Hero.Doom) {
			((Hero.Doom)cause).onDeath();
		}

		Dungeon.deleteGame( GamesInProgress.curSlot, true );
	}

	//effectively cache this buff to prevent having to call buff(...) a bunch.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and thread coordination implications if that method calls buff(...) frequently
	private Berserk berserk;

	@Override
	public boolean isAlive() {
		
		if (HP <= 0){
			if (berserk == null) berserk = buff(Berserk.class);
			return berserk != null && berserk.berserking();
		} else {
			berserk = null;
			return super.isAlive();
		}
	}

	@Override
	public void move(int step, boolean travelling) {
		boolean wasHighGrass = Dungeon.level.map[step] == Terrain.HIGH_GRASS;

		super.move( step, travelling);
		
		if (!flying && travelling) {
			if (Dungeon.level.water[pos]) {
				Sample.INSTANCE.play( Assets.Sounds.WATER, 1, Random.Float( 0.8f, 1.25f ) );
			} else if (Dungeon.level.map[pos] == Terrain.EMPTY_SP) {
				Sample.INSTANCE.play( Assets.Sounds.STURDY, 1, Random.Float( 0.96f, 1.05f ) );
			} else if (Dungeon.level.map[pos] == Terrain.GRASS
					|| Dungeon.level.map[pos] == Terrain.EMBERS
					|| Dungeon.level.map[pos] == Terrain.FURROWED_GRASS){
				if (step == pos && wasHighGrass) {
					Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				} else {
					Sample.INSTANCE.play( Assets.Sounds.GRASS, 1, Random.Float( 0.96f, 1.05f ) );
				}
			} else {
				Sample.INSTANCE.play( Assets.Sounds.STEP, 1, Random.Float( 0.96f, 1.05f ) );
			}
		}
	}
	
	@Override
	public void onAttackComplete() {

		if (attackTarget == null){
			curAction = null;
			super.onAttackComplete();
			return;
		}
		
		AttackIndicator.target(attackTarget);
		boolean wasEnemy = attackTarget.alignment == Alignment.ENEMY;

		KindOfWeapon wep = belongings.attackingWeapon();
		int hitCount = wep != null ? Math.max(1, wep.hitCount()) : 1;
		boolean hit = attack(attackTarget, 1f, 0f, 1f, hitCount);

		Invisibility.dispel();
		//only half of the attack delay - the proposal already spent the other half
		spend( currentAttackDelay / 2f );

		if (hit && subClass == HeroSubClass.GLADIATOR && wasEnemy){
			Buff.affect( this, Combo.class ).hit(attackTarget);
		}

		if (hit && heroClass == HeroClass.DUELIST && wasEnemy){
			Buff.affect( this, Sai.ComboStrikeTracker.class).addHit();
		}

		curAction = null;
		attackTarget = null;

		super.onAttackComplete();
	}
	
	@Override
	public void onMotionComplete() {
		GameScene.checkKeyHold();
	}
	
	@Override
	public void onOperateComplete() {
		
		if (curAction instanceof HeroAction.Unlock) {

			int doorCell = ((HeroAction.Unlock)curAction).dst;
			int door = Dungeon.level.map[doorCell];

			SkeletonKey.keyRecharge skele = buff(SkeletonKey.keyRecharge.class);
			SkeletonKey.KeyReplacementTracker keyUseTrack = buff(SkeletonKey.KeyReplacementTracker.class);

			if (skele != null && skele.isCursed() && Random.Int(6) != 0){
				GLog.n(Messages.get(this, "key_distracted"));
				spendAndNext(2*Key.TIME_TO_UNLOCK);
				Buff.affect(this, Hunger.class).affectHunger(-4);
			} else if (Dungeon.level.distance(pos, doorCell) <= 1) {
				boolean hasKey = true;
				if (door == Terrain.LOCKED_DOOR) {
					hasKey = Notes.remove(new Key(Key.Kind.IRON, Dungeon.depth));
					if (hasKey) {
						if (keyUseTrack != null){
							keyUseTrack.processIronLockOpened();
						}
						Level.set(doorCell, Terrain.DOOR);
					}
				} else if (door == Terrain.HERO_LKD_DR) {
					hasKey = true;
					Level.set(doorCell, Terrain.DOOR);
					GLog.i( Messages.get(SkeletonKey.class, "force_lock"));
				} else if (door == Terrain.CRYSTAL_DOOR) {
					hasKey = Notes.remove(new Key(Key.Kind.CRYSTAL, Dungeon.depth));
					if (hasKey) {
						if (keyUseTrack != null){
							keyUseTrack.processCrystalLockOpened();
						}
						Level.set(doorCell, Terrain.EMPTY);
						Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
						CellEmitter.get( doorCell ).start( Speck.factory( Speck.DISCOVER ), 0.025f, 20 );
					}
				} else {
					hasKey = Notes.remove(new Key(Key.Kind.WORN, Dungeon.depth));
					if (hasKey) {
						Level.set(doorCell, Terrain.UNLOCKED_EXIT);
					}
				}
				
				if (hasKey) {
					GameScene.updateKeyDisplay();
					GameScene.updateMap(doorCell);
					spend(Key.TIME_TO_UNLOCK);
				}
			}
			
		} else if (curAction instanceof HeroAction.OpenChest) {
			
			Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst );
			SkeletonKey.keyRecharge skele = buff(SkeletonKey.keyRecharge.class);
			SkeletonKey.KeyReplacementTracker keyUseTrack = buff(SkeletonKey.KeyReplacementTracker.class);

			if (skele != null && skele.isCursed()
					&& (heap.type == Type.LOCKED_CHEST || heap.type == Type.CRYSTAL_CHEST)
					&& Random.Int(6) != 0){
				GLog.n(Messages.get(this, "key_distracted"));
				spend(2*Key.TIME_TO_UNLOCK);
				Buff.affect(this, Hunger.class).affectHunger(-4);
			} else if (Dungeon.level.distance(pos, heap.pos) <= 1){
				boolean hasKey = true;
				if (heap.type == Type.SKELETON || heap.type == Type.REMAINS) {
					Sample.INSTANCE.play( Assets.Sounds.BONES );
				} else if (heap.type == Type.LOCKED_CHEST){
					hasKey = Notes.remove(new Key(Key.Kind.GOLDEN, Dungeon.depth));
					if (hasKey && keyUseTrack != null){
						keyUseTrack.processGoldLockOpened();
					}
				} else if (heap.type == Type.CRYSTAL_CHEST){
					hasKey = Notes.remove(new Key(Key.Kind.CRYSTAL, Dungeon.depth));
					if (hasKey && keyUseTrack != null){
						keyUseTrack.processCrystalLockOpened();
					}
				}

				if (hasKey) {
					GameScene.updateKeyDisplay();
					heap.open(this);
					spend(Key.TIME_TO_UNLOCK);
				}
			}
			
		}
		curAction = null;

		if (!ready) {
			super.onOperateComplete();
		}
	}

	public boolean search( boolean intentional ) {
		
		if (!isAlive()) return false;
		
		boolean smthFound = false;

		boolean circular = pointsInTalent(Talent.WIDE_SEARCH) == 1;
		int distance = heroClass == HeroClass.ROGUE ? 2 : 1;
		if (hasTalent(Talent.WIDE_SEARCH)) distance++;
		
		boolean foresight = buff(Foresight.class) != null;
		boolean foresightScan = foresight && !Dungeon.level.mapped[pos];

		if (foresightScan){
			Dungeon.level.mapped[pos] = true;
		}

		if (foresight) {
			distance = Foresight.DISTANCE;
			circular = true;
		}

		Point c = Dungeon.level.cellToPoint(pos);

		TalismanOfForesight.Foresight talisman = buff( TalismanOfForesight.Foresight.class );
		boolean cursed = talisman != null && talisman.isCursed();

		int[] rounding = ShadowCaster.rounding[distance];

		int left, right;
		int curr;
		for (int y = Math.max(0, c.y - distance); y <= Math.min(Dungeon.level.height()-1, c.y + distance); y++) {
			if (!circular){
				left = c.x - distance;
			} else if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
				left = c.x - rounding[Math.abs(c.y - y)];
			} else {
				left = distance;
				while (rounding[left] < rounding[Math.abs(c.y - y)]){
					left--;
				}
				left = c.x - left;
			}
			right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
			left = Math.max(0, left);
			for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++){

				if ((foresight || fieldOfView[curr]) && curr != pos) {

					if ((foresight && (!Dungeon.level.mapped[curr] || foresightScan))){
						GameScene.effectOverFog(new CheckedCell(curr, foresightScan ? pos : curr));
					} else if (intentional) {
						GameScene.effectOverFog(new CheckedCell(curr, pos));
					}

					if (foresight){
						Dungeon.level.mapped[curr] = true;
					}
					
					if (Dungeon.level.secret[curr]){
						
						Trap trap = Dungeon.level.traps.get( curr );
						float chance;

						//searches aided by foresight always succeed, even if trap isn't searchable
						if (foresight){
							chance = 1f;

						//otherwise if the trap isn't searchable, searching always fails
						} else if (trap != null && !trap.canBeSearched){
							chance = 0f;

						//intentional searches always succeed against regular traps and doors
						} else if (intentional){
							chance = 1f;
						
						//unintentional searches always fail with a cursed talisman
						} else if (cursed) {
							chance = 0f;
							
						//unintentional trap detection scales from 40% at floor 0 to 30% at floor 25
						} else if (Dungeon.level.map[curr] == Terrain.SECRET_TRAP) {
							chance = 0.4f - (Dungeon.depth / 250f);
							
						//unintentional door detection scales from 20% at floor 0 to 0% at floor 20
						} else {
							chance = 0.2f - (Dungeon.depth / 100f);
						}

						//don't want to let the player search though hidden doors in tutorial
						if (SPDSettings.intro()){
							chance = 0;
						}
						
						if (Random.Float() < chance) {
						
							int oldValue = Dungeon.level.map[curr];
							
							GameScene.discoverTile( curr, oldValue );
							
							Dungeon.level.discover( curr );
							
							ScrollOfMagicMapping.discover( curr );
							
							if (fieldOfView[curr]) smthFound = true;
	
							if (talisman != null){
								if (oldValue == Terrain.SECRET_TRAP){
									talisman.charge(2);
								} else if (oldValue == Terrain.SECRET_DOOR){
									talisman.charge(10);
								}
							}
						}
					}
				}
			}
		}
		
		if (intentional) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "search") );
			sprite.operate( pos );
			if (!Dungeon.level.locked) {
				if (cursed) {
					GLog.n(Messages.get(this, "search_distracted"));
					Buff.affect(this, Hunger.class).affectHunger(TIME_TO_SEARCH - (2 * HUNGER_FOR_SEARCH));
				} else {
					Buff.affect(this, Hunger.class).affectHunger(TIME_TO_SEARCH - HUNGER_FOR_SEARCH);
				}
			}
			spendAndNext(TIME_TO_SEARCH);
			
		}
		
		if (smthFound) {
			GLog.w( Messages.get(this, "noticed_smth") );
			Sample.INSTANCE.play( Assets.Sounds.SECRET );
			interrupt();
		}

		if (foresight){
			GameScene.updateFog(pos, Foresight.DISTANCE+1);
		}

		if (talisman != null){
			talisman.checkAwareness();
		}
		
		return smthFound;
	}
	
	public void resurrect() {
		HP = HT;
		live();

		Buff.affect(this, LostInventory.class);
		Buff.affect(this, Invisibility.class, 3f);
		//lost inventory is dropped in interlevelscene

		//activate items that persist after lost inventory
		//FIXME this is very messy, maybe it would be better to just have one buff that
		// handled all items that recharge over time?
		for (Item i : belongings){
			if (i instanceof EquipableItem && i.isEquipped(this)){
				((EquipableItem) i).activate(this);
			} else if (i instanceof CloakOfShadows && i.keptThroughLostInventory() && hasTalent(Talent.LIGHT_CLOAK)) {
				((CloakOfShadows) i).activate(this);
			} else if (i instanceof HolyTome  && i.keptThroughLostInventory() && hasTalent(Talent.LIGHT_READING)) {
				((HolyTome) i).activate(this);
			} else if (i instanceof Wand && i.keptThroughLostInventory()){
				((Wand) i).charge(this);
			} else if (i instanceof MagesStaff && i.keptThroughLostInventory()){
				((MagesStaff) i).applyWandChargeBuff(this);
			}
		}

		updateHT(false);
	}

	@Override
	public void next() {
		if (isAlive())
			super.next();
	}

	public static interface Doom {
		public void onDeath();
	}
}
