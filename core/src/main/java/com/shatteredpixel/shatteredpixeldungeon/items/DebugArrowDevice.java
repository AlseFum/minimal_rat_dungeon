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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.ArrowDevice;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

import java.util.ArrayList;

/**
 * Places an {@link ArrowDevice} on a selected vacant floor cell.
 * After selecting the cell, a small window asks for the direction the
 * device should face.
 */
public class DebugArrowDevice extends Item implements DebugTool {

	private static final String AC_PLACE = "PLACE";

	{
		image = ItemSpriteSheet.SPIRIT_ARROW;
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
		System.out.println("[ARROW] execute() action=" + action + " level=" + (Dungeon.level != null));
		super.execute(hero, action);
		if (AC_PLACE.equals(action) && Dungeon.level != null) {
			GameScene.selectCell(placer);
		}
	}

	private final CellSelector.Listener placer = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			System.out.println("[ARROW] placer.onSelect cell=" + cell);
			if (cell == null) return;

			System.out.println("[ARROW]   heroFOV=" + Dungeon.level.heroFOV[cell]
					+ " passable=" + Dungeon.level.passable[cell]
					+ " char=" + (Actor.findChar(cell) != null)
					+ " entity=" + (Dungeon.level.entityAt(cell) != null));
			if (!Dungeon.level.heroFOV[cell]
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null
					|| Dungeon.level.entityAt(cell) != null) {
				GLog.w(Messages.get(DebugArrowDevice.class, "invalid"));
				return;
			}

			GameScene.show(new WndOptions(
					Messages.get(DebugArrowDevice.class, "wnd_title"),
					Messages.get(DebugArrowDevice.class, "wnd_desc"),
					DIRECTION_NAMES) {
				@Override
				protected void onSelect( int index ) {
					ArrowDevice device = new ArrowDevice();
					device.pos = cell;
					device.facing(index);
					GameScene.add(device);
					System.out.println("[ARROW] device placed at " + cell + " facing " + DIRECTION_NAMES[index]);
				}
			});
		}

		@Override
		public String prompt() {
			return Messages.get(DebugArrowDevice.class, "prompt");
		}
	};

	//compass directions, index matches ArrowDevice's facing indices (E, NE, N, NW, W, SW, S, SE)
	private static final String[] DIRECTION_NAMES = {"E", "NE", "N", "NW", "W", "SW", "S", "SE"};

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
