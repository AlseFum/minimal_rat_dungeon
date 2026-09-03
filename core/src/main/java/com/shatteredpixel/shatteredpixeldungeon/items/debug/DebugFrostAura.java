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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostAura;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

/**
 * Applies a {@link FrostAura} to the character standing on the selected cell.
 * Attacking that character then fails, and the attacker is chilled - handy for
 * testing the midAction failure pipeline.
 */
public class DebugFrostAura extends Item implements DebugTool {

	private static final String AC_APPLY = "APPLY";

	{
		image = ItemSpriteSheet.FROST_BOMB;
		defaultAction = AC_APPLY;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);
		if (AC_APPLY.equals(action) && Dungeon.level != null) {
			GameScene.selectCell(applier);
		}
	}

	private final CellSelector.Listener applier = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (cell == null) return;

			Char ch = Actor.findChar(cell);
			if (ch == null || !Dungeon.level.heroFOV[cell]) {
				GLog.w(Messages.get(DebugFrostAura.class, "invalid"));
				return;
			}

			Buff.prolong(ch, FrostAura.class, FrostAura.DURATION);
			GLog.i(Messages.get(DebugFrostAura.class, "applied", ch.name(), (int)FrostAura.DURATION));
		}

		@Override
		public String prompt() {
			return Messages.get(DebugFrostAura.class, "prompt");
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
