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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.Baseball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

/** A baseball bat: throws a baseball from the inventory with bat-sourced bonuses. */
public class BaseballBat extends MeleeWeapon {

	public static final String AC_THROW_BASEBALL = "THROW_BASEBALL";

	{
		image = ItemSpriteSheet.MACE; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_CRUSH;
		tier = 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (findBaseball(hero) != null) {
			actions.add(AC_THROW_BASEBALL);
		}
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_THROW_BASEBALL)) {
			return Messages.get(this, "ac_throw_baseball");
		}
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_THROW_BASEBALL)) {
			Baseball baseball = findBaseball(hero);
			if (baseball != null) {
				baseball.markBatSourced();
				baseball.execute(hero, Item.AC_THROW);
			}
			return;
		}
		super.execute(hero, action);
	}

	private Baseball findBaseball(Hero hero) {
		if (hero.belongings.weapon() instanceof Baseball) {
			return (Baseball) hero.belongings.weapon();
		}
		for (Item i : hero.belongings.backpack) {
			if (i instanceof Baseball && i.quantity() > 0) {
				return (Baseball) i;
			}
		}
		return null;
	}
}
