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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.OriginiumAltar;

import java.util.ArrayList;

/**
 * An experimental region which scatters several originum altars around
 * the level.
 *
 * Experimental: in DEMO mode it competes with the other experimental
 * regions and the sewers on depths 1-5.
 */
public class ChapVIIRegion extends DungeonRegion {

	@Override
	public String id() {
		return "chapvii";
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
		ArrayList<Integer> spots = pickOpenCells(level, 5, 5);
		for (int cell : spots) {
			Blob.seed(cell, 50, OriginiumAltar.class, level);
		}
	}

	@Override
	public String enterMessage() {
		return Messages.get(this, "enter");
	}
}
