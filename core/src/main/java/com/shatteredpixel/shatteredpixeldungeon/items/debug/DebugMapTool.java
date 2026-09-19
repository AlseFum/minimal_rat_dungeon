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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.DebugDummyMapDevice;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

import java.util.ArrayList;

/** Debug 工具（地图类）：层级与地图层面的操作。 */
public class DebugMapTool extends Item implements DebugTool {

	private static final String AC_NEXT_FLOOR = "NEXT_FLOOR";
	private static final String AC_MAP_DEVICE = "MAP_DEVICE";

	{
		image = ItemSpriteSheet.SCROLL_HOLDER;
		defaultAction = AC_NEXT_FLOOR;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_NEXT_FLOOR);
		actions.add(AC_MAP_DEVICE);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);
		if (Dungeon.level == null) return;

		if (AC_NEXT_FLOOR.equals(action)) {
			goToNextLevel();
		} else if (AC_MAP_DEVICE.equals(action)) {
			GameScene.selectCell(placer);
		}
	}

	public static void goToNextLevel() {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		Level.beforeTransition();
		InterlevelScene.curTransition = new LevelTransition(
				Dungeon.level, Dungeon.level.exit(), LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE);
		InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		Game.switchScene(InterlevelScene.class);
	}

	private final CellSelector.Listener placer = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (cell == null) return;

			if (!Dungeon.level.heroFOV[cell]
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null
					|| Dungeon.level.entityAt(cell) != null) {
				GLog.w(Messages.get(DebugMapTool.class, "invalid"));
				return;
			}

			DebugDummyMapDevice device = new DebugDummyMapDevice();
			device.pos = cell;
			GameScene.add(device);
		}

		@Override
		public String prompt() {
			return Messages.get(DebugMapTool.class, "prompt");
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

	@Override
	public int value() {
		return 0;
	}
}
