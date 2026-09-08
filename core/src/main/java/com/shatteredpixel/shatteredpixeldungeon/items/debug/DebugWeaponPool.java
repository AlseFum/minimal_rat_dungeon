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

package com.shatteredpixel.shatteredpixeldungeon.items.debug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWeaponPool;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/**
 * Debug 工具：生成当前"常规武器池"（WEP_T1 一般武）中的武器。
 * 打开 {@link WndWeaponPool} 图标列表选择；列表在打开时从 {@link Loot#classes(String)}
 * 动态读取，池内容调整后无需改动本类。
 */
public class DebugWeaponPool extends Item implements DebugTool {

	private static final String AC_GENERATE = "GENERATE";

	{
		image = ItemSpriteSheet.WEAPON_HOLDER;
		defaultAction = AC_GENERATE;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_GENERATE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_GENERATE.equals(action) && Dungeon.hero != null) {
			Class<?>[] pool = Loot.classes(Loot.WEP_T1);
			if (pool == null || pool.length == 0) {
				GLog.w(Messages.get(this, "empty"));
				return;
			}
			ShatteredPixelDungeon.scene().addToFront(new WndWeaponPool(this));
		}
	}

	/** 生成一把池内武器（identify 后放入背包）；背包满时提示。 */
	@SuppressWarnings("unchecked")
	public void generate(Class<?> cls) {
		Item weapon = Reflection.newInstance((Class<? extends Item>) cls);
		if (weapon == null) {
			return;
		}
		weapon.identify();
		if (weapon.collect()) {
			GLog.p(Messages.get(this, "generated", weapon.name()));
		} else {
			GLog.w(Messages.get(this, "full"));
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
