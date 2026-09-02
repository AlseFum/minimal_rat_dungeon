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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.HeatPumpChannel;
import com.watabou.utils.Random;

/**
 * A region whose floors are scattered with patches of heat pump channels,
 * generated with {@link Patch} so the channels cluster into random areas.
 *
 * In DEMO mode it competes with the sewers region on depths 1-5.
 */
public class ActIRegion extends DungeonRegion {

	@Override
	public String id() {
		return "acti";
	}

	@Override
	public float weight() {
		return Dungeon.branch == 0 && Dungeon.depth >= 1 && Dungeon.depth <= 5 ? 1 : 0;
	}

	@Override
	protected Level constructLevel() {
		Level level = new SewerLevel();
		level.regionId = id();
		return level;
	}

	@Override
	public void configureLevel(Level level) {
		//random clustered patches of heat pump channels
		boolean[] patch = Patch.generate(level.width(), level.height(),
				Random.Float(0.03f, 0.08f), 4, true);
		for (int i = 0; i < level.length(); i++) {
			if (patch[i]
					&& level.passable[i] && !level.solid[i]
					&& level.findMob(i) == null
					&& level.traps.get(i) == null
					&& level.entityAt(i) == null) {
				Blob.seed(i, 50, HeatPumpChannel.class, level);
			}
		}
	}

	@Override
	public String enterMessage() {
		return Messages.get(this, "enter");
	}
}
