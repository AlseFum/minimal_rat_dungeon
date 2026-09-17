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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.hero.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Game;

/**
 * 动态注册的子职业。与 {@link HeroClass} 同模式：静态 final 实例 + 注册表，
 * 全代码库的 {@code == HeroSubClass.X} 比较继续按实例引用生效。
 * 每个子职业携带自己的 talent 层 id（TengusMask 选择时添加）。
 */
public class HeroSubClass {

	// ==================== 注册表 ====================

	private static final LinkedHashMap<String, HeroSubClass> REGISTRY = new LinkedHashMap<>();

	/** 注册子职业并返回实例；重复/空 id 抛 IllegalArgumentException */
	public static HeroSubClass register(HeroSubClass cls) {
		if (cls == null || cls.id == null || cls.id.isEmpty()) {
			throw new IllegalArgumentException("hero subclass id must not be null or empty");
		}
		if (REGISTRY.containsKey(cls.id)) {
			throw new IllegalArgumentException("hero subclass '" + cls.id + "' is already registered");
		}
		REGISTRY.put(cls.id, cls);
		return cls;
	}

	/** 按 id 查找；未知 id 返回 null */
	public static HeroSubClass find(String id) {
		return REGISTRY.get(id);
	}

	/** 全部已注册子职业（注册序） */
	public static HeroSubClass[] all() {
		return REGISTRY.values().toArray(new HeroSubClass[0]);
	}

	// ==================== 内置子职业 ====================

	public static final HeroSubClass NONE      = register(new HeroSubClass("NONE", HeroIcon.NONE, null));

	public static final HeroSubClass BERSERKER = register(new HeroSubClass("BERSERKER", HeroIcon.BERSERKER, "berserker_t3"));
	public static final HeroSubClass GLADIATOR = register(new HeroSubClass("GLADIATOR", HeroIcon.GLADIATOR, "gladiator_t3"));

	public static final HeroSubClass BATTLEMAGE = register(new HeroSubClass("BATTLEMAGE", HeroIcon.BATTLEMAGE, "battlemage_t3"));
	public static final HeroSubClass WARLOCK    = register(new HeroSubClass("WARLOCK", HeroIcon.WARLOCK, "warlock_t3"));

	public static final HeroSubClass ASSASSIN   = register(new HeroSubClass("ASSASSIN", HeroIcon.ASSASSIN, "assassin_t3"));
	public static final HeroSubClass FREERUNNER = register(new HeroSubClass("FREERUNNER", HeroIcon.FREERUNNER, "freerunner_t3"));

	public static final HeroSubClass SNIPER = register(new HeroSubClass("SNIPER", HeroIcon.SNIPER, "sniper_t3"));
	public static final HeroSubClass WARDEN = register(new HeroSubClass("WARDEN", HeroIcon.WARDEN, "warden_t3"));

	public static final HeroSubClass CHAMPION = register(new HeroSubClass("CHAMPION", HeroIcon.CHAMPION, "champion_t3"));
	public static final HeroSubClass MONK     = register(new HeroSubClass("MONK", HeroIcon.MONK, "monk_t3"));

	public static final HeroSubClass PRIEST  = register(new HeroSubClass("PRIEST", HeroIcon.PRIEST, "priest_t3"));
	public static final HeroSubClass PALADIN = register(new HeroSubClass("PALADIN", HeroIcon.PALADIN, "paladin_t3"));

	//MISERY: shadow assassin from ZootDungeon. Not attached to any hero class;
	//granted via the DebugMisery debug item. TODO: dedicated HeroIcon.
	public static final HeroSubClass MISERY = register(new HeroSubClass("MISERY", HeroIcon.ASSASSIN, "misery_t3"));

	// ==================== 数据字段 ====================

	/** 子职业 id（沿用旧 enum 常量名；Messages 键与存档键一致） */
	public final String id;
	private final int icon;
	/** 选择该子职业时添加的 talent 层 id（NONE 为 null） */
	public final String talentLayerId;

	protected HeroSubClass(String id, int icon, String talentLayerId) {
		this.id = id;
		this.icon = icon;
		this.talentLayerId = talentLayerId;
	}

	public String title() {
		return Messages.get(this, id);
	}

	public String shortDesc() {
		return Messages.get(this, id+"_short_desc");
	}

	public String desc() {
		//Include the staff effect description in the battlemage's desc if possible
		if (this == BATTLEMAGE){
			String desc = Messages.get(this, id + "_desc");
			if (Game.scene() instanceof GameScene){
				MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
				if (staff != null && staff.wandClass() != null){
					desc += "\n\n" + Messages.get(staff.wandClass(), "bmage_desc");
					desc = desc.replaceAll("_", "");
				}
			}
			return desc;
		} else {
			return Messages.get(this, id + "_desc");
		}
	}

	public int icon(){
		return icon;
	}

}
