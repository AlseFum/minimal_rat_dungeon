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
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemRegistry;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGiveItems;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/**
 * Debug 工具：打开 {@link WndGiveItems}，按类别取任意物品，并可删除背包内的物品。
 *
 * <p>列表来源是 {@link ItemRegistry}（未入池的英雄开局武/Zoot 移植武另附），
 * 所以注册表增删条目、改权重或挂 modifier 之后，本工具会自动跟随。</p>
 */
public class DebugGiveItems extends Item implements DebugTool {

	private static final String AC_GIVE = "GIVE";

	{
		image = ItemSpriteSheet.CHEST;
		defaultAction = AC_GIVE;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_GIVE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_GIVE.equals(action) && Dungeon.hero != null) {
			ShatteredPixelDungeon.scene().addToFront(new WndGiveItems(this));
		}
	}

	/**
	 * 取一件：已注册类型走注册表（工厂 + modifier，不做 random，数值可预期），
	 * 未注册类型（英雄开局武、Zoot 移植武）反射构造。
	 */
	@SuppressWarnings("unchecked")
	public void give(Class<?> cls) {
		if (cls == null || !Item.class.isAssignableFrom(cls)) {
			return;
		}

		Item item = ItemRegistry.build((Class<? extends Item>) cls);
		if (item == null) {
			item = Reflection.newInstance((Class<? extends Item>) cls);
		}
		if (item == null) {
			return;
		}

		item.identify();
		if (item.collect()) {
			GLog.p(Messages.get(this, "generated", item.name()));
		} else {
			GLog.w(Messages.get(this, "full"));
		}
	}

	/** 删一件：已装备的先卸下，再从背包（含嵌套包）摘除；快格清理由 detachAll 负责。 */
	public boolean delete(Item item) {
		Hero hero = Dungeon.hero;
		if (hero == null || item == null) {
			return false;
		}

		if (item.isEquipped(hero) && item instanceof EquipableItem
				&& !((EquipableItem) item).doUnequip(hero, false)) {
			GLog.w(Messages.get(this, "unequip_fail", item.name()));
			return false;
		}

		item.detachAll(hero.belongings.backpack);
		GLog.p(Messages.get(this, "deleted", item.name()));
		return true;
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
