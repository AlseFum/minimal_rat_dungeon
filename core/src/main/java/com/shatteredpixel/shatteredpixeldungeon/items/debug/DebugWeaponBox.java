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

package com.shatteredpixel.shatteredpixeldungeon.items.debug;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.AscalonAOE;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.Chakram;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.DeployablewCrossBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.PhantomKnife;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/**
 * Generates one of the ported ZootDungeon weapons, grouped by category.
 */
public class DebugWeaponBox extends Item implements DebugTool {

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
		if (AC_GENERATE.equals(action)) {
			String[] names = new String[WEAPONS.length];
			for (int i = 0; i < WEAPONS.length; i++) {
				names[i] = Messages.get(WEAPONS[i], "name");
			}
			GameScene.show(new WndOptions(
					Messages.get(this, "title"),
					Messages.get(this, "msg"),
					names) {
				@Override
				protected void onSelect(int index) {
					generate(WEAPONS[index]);
				}
			});
		}
	}

	private void generate(Class<? extends Item> weaponClass) {
		Item weapon = Reflection.newInstance(weaponClass);
		weapon.identify();
		if (weapon.collect()) {
			GLog.p(Messages.get(this, "generated", weapon.name()));
		} else {
			GLog.w(Messages.get(this, "full"));
		}
	}

	private static final Class<? extends Item>[] WEAPONS = new Class[]{
			AscalonAOE.class,
			Chakram.class,
			DeployablewCrossBow.class,
			PhantomKnife.class,
	};

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
