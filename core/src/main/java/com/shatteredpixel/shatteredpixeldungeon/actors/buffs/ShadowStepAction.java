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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.MiseryShadowBlob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * MISERY feature: the action-indicator button which lets the hero shadow-step
 * to any shadow cell, paying HP for the distance.
 */
public class ShadowStepAction implements ActionIndicator.Action {

	public static final ShadowStepAction INSTANCE = new ShadowStepAction();

	@Override
	public String actionName() {
		return Messages.get(this, "name");
	}

	@Override
	public int indicatorColor() {
		return 0x8844AA;
	}

	@Override
	public void doAction() {
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				if (!MiseryShadowBlob.isShadowCell(cell)) {
					GLog.w(Messages.get(ShadowStepAction.class, "not_shadow"));
					return;
				}
				MiseryShadowBlob blob = (MiseryShadowBlob) Dungeon.level.blobs.get(MiseryShadowBlob.class);
				if (blob != null && Dungeon.hero != null) {
					blob.teleportTo(Dungeon.hero, cell);
				}
			}

			@Override
			public String prompt() {
				return Messages.get(ShadowStepAction.class, "prompt");
			}
		});
	}
}
