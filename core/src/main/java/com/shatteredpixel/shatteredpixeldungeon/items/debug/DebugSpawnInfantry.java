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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ported.Infantry;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

/** Debug 工具：在选定的地板格生成一名移植步兵（{@link Infantry}）。 */
public class DebugSpawnInfantry extends Item implements DebugTool {

	private static final String AC_SPAWN = "SPAWN";

	{
		image = ItemSpriteSheet.SCROLL_HOLDER;
		defaultAction = AC_SPAWN;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SPAWN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_SPAWN.equals(action) && Dungeon.level != null) {
			GameScene.selectCell(spawner);
		}
	}

	private final CellSelector.Listener spawner = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;

			if (!Dungeon.level.heroFOV[cell]
					|| !Dungeon.level.passable[cell]
					|| Actor.findChar(cell) != null
					|| Dungeon.level.entityAt(cell) != null) {
				GLog.w(Messages.get(DebugSpawnInfantry.class, "invalid"));
				return;
			}

			Infantry infantry = new Infantry();
			infantry.pos = cell;
			infantry.state = infantry.HUNTING;
			GameScene.add(infantry, 2f);
			Dungeon.level.occupyCell(infantry);

			GLog.p(Messages.get(DebugSpawnInfantry.class, "spawned", infantry.name()));
		}

		@Override
		public String prompt() {
			return Messages.get(DebugSpawnInfantry.class, "prompt");
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
