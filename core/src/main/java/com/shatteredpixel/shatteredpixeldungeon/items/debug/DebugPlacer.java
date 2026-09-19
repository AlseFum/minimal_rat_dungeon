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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.HeatPumpChannel;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.IceCrystalAltar;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.OriginiumAltar;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostAura;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.ArrowDevice;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ported.Infantry;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

import java.util.ArrayList;

/**
 * Debug 工具（放置类）：装着一个 Debug 工具原先各自独立的"选格施放"功能。
 *
 * <p>五个占格放置器（箭装置 / 热泵 / 冰晶祭坛 / 源石祭坛 / 刷步兵）共用同一套
 * 空地校验——格子需在视野内、可通行、无角色、无实体——由
 * {@link #placerFor(Placer, String)} 统一处理，各自只提供"放什么"。
 * 冰霜光环作用于角色而非空地，故用单独的监听器。</p>
 */
public class DebugPlacer extends Item implements DebugTool {

	private static final String AC_ARROW_DEVICE = "ARROW_DEVICE";
	private static final String AC_HEAT_PUMP = "HEAT_PUMP";
	private static final String AC_ICE_CRYSTAL = "ICE_CRYSTAL";
	private static final String AC_ORIGINIUM = "ORIGINIUM";
	private static final String AC_SPAWN_INFANTRY = "SPAWN_INFANTRY";
	private static final String AC_FROST_AURA = "FROST_AURA";

	//compass directions, index matches ArrowDevice's facing indices (E, NE, N, NW, W, SW, S, SE)
	private static final String[] DIRECTION_NAMES = {"E", "NE", "N", "NW", "W", "SW", "S", "SE"};

	{
		image = ItemSpriteSheet.BEACON;
		defaultAction = AC_ARROW_DEVICE;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ARROW_DEVICE);
		actions.add(AC_HEAT_PUMP);
		actions.add(AC_ICE_CRYSTAL);
		actions.add(AC_ORIGINIUM);
		actions.add(AC_SPAWN_INFANTRY);
		actions.add(AC_FROST_AURA);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);
		if (Dungeon.level == null) return;

		if (AC_ARROW_DEVICE.equals(action)) {
			GameScene.selectCell(placerFor(this::chooseArrowDirection, "prompt"));
		} else if (AC_HEAT_PUMP.equals(action)) {
			GameScene.selectCell(placerFor(cell -> GameScene.add(Blob.seed(cell, 50, HeatPumpChannel.class)), "prompt"));
		} else if (AC_ICE_CRYSTAL.equals(action)) {
			GameScene.selectCell(placerFor(cell -> GameScene.add(Blob.seed(cell, 50, IceCrystalAltar.class)), "prompt"));
		} else if (AC_ORIGINIUM.equals(action)) {
			GameScene.selectCell(placerFor(cell -> GameScene.add(Blob.seed(cell, 50, OriginiumAltar.class)), "prompt"));
		} else if (AC_SPAWN_INFANTRY.equals(action)) {
			GameScene.selectCell(placerFor(this::spawnInfantry, "prompt_spawn"));
		} else if (AC_FROST_AURA.equals(action)) {
			GameScene.selectCell(auraApplier);
		}
	}

	/**
	 * 占格放置器：统一校验选中格是否为空地（视野内 / 可通行 / 无角色 / 无实体），
	 * 通过后交给 {@code placer} 放置。
	 */
	private CellSelector.Listener placerFor( final Placer placer, final String promptKey ) {
		return new CellSelector.Listener() {
			@Override
			public void onSelect( Integer cell ) {
				if (cell == null) return;

				if (!Dungeon.level.heroFOV[cell]
						|| !Dungeon.level.passable[cell]
						|| Actor.findChar(cell) != null
						|| Dungeon.level.entityAt(cell) != null) {
					GLog.w(Messages.get(DebugPlacer.class, "invalid"));
					return;
				}

				placer.place(cell);
			}

			@Override
			public String prompt() {
				return Messages.get(DebugPlacer.class, promptKey);
			}
		};
	}

	/** 放置动作：在已校验过的空格上动手。 */
	private interface Placer {
		void place( int cell );
	}

	/** 箭装置先问朝向，再放。 */
	private void chooseArrowDirection( int cell ) {
		GameScene.show(new WndOptions(
				Messages.get(DebugPlacer.class, "wnd_title"),
				Messages.get(DebugPlacer.class, "wnd_desc"),
				DIRECTION_NAMES) {
			@Override
			protected void onSelect( int index ) {
				ArrowDevice device = new ArrowDevice();
				device.pos = cell;
				device.facing(index);
				GameScene.add(device);
			}
		});
	}

	private void spawnInfantry( int cell ) {
		Infantry infantry = new Infantry();
		infantry.pos = cell;
		infantry.state = infantry.HUNTING;
		GameScene.add(infantry, 2f);
		Dungeon.level.occupyCell(infantry);

		GLog.p(Messages.get(DebugPlacer.class, "spawned", infantry.name()));
	}

	/** 冰霜光环：格子必须站着角色（不是空地）。 */
	private final CellSelector.Listener auraApplier = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (cell == null) return;

			Char ch = Actor.findChar(cell);
			if (ch == null || !Dungeon.level.heroFOV[cell]) {
				GLog.w(Messages.get(DebugPlacer.class, "invalid_frost"));
				return;
			}

			Buff.prolong(ch, FrostAura.class, FrostAura.DURATION);
			GLog.i(Messages.get(DebugPlacer.class, "applied", ch.name(), (int)FrostAura.DURATION));
		}

		@Override
		public String prompt() {
			return Messages.get(DebugPlacer.class, "prompt_frost");
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
