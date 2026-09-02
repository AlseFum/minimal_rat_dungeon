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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.IceCrystalAltar;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

/** Seeds an {@link IceCrystalAltar} on a selected floor cell. */
public class DebugIceCrystalAltar extends Item implements DebugTool {

	private static final String AC_PLACE = "PLACE";

	{
		image = ItemSpriteSheet.BEACON;
		defaultAction = AC_PLACE;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PLACE);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);
		if (AC_PLACE.equals(action) && Dungeon.level != null) {
			GameScene.selectCell(placer);
		}
	}

	private final CellSelector.Listener placer = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (cell == null) return;

			if (!Dungeon.level.heroFOV[cell]
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null
					|| Dungeon.level.entityAt(cell) != null) {
				GLog.w(Messages.get(DebugIceCrystalAltar.class, "invalid"));
				return;
			}

			GameScene.add(Blob.seed(cell, 50, IceCrystalAltar.class));
		}

		@Override
		public String prompt() {
			return Messages.get(DebugIceCrystalAltar.class, "prompt");
		}
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
