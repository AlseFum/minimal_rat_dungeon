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
import com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.ArrowDevice;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * An experimental region which places arrow devices flush against walls.
 *
 * Placement algorithm: only cells that touch a wall (or the map edge) are
 * candidates; each candidate is aimed down its longest clear line of fire,
 * and only candidates with at least 4 tiles of open space in that direction
 * qualify. Four devices are placed, at least 5 tiles apart.
 *
 * Experimental: in DEMO mode it competes with the other experimental
 * regions and the sewers on depths 1-5.
 */
public class ChapVRegion extends DungeonRegion {

	@Override
	public String id() {
		return "chapv";
	}

	@Override
	public float weight() {
		return Dungeon.mode == Dungeon.Mode.DEMO && Dungeon.branch == 0 && Dungeon.depth >= 1 && Dungeon.depth <= 5 ? 1 : 0;
	}

	@Override
	protected Level constructLevel() {
		Level level = new SewerLevel();
		level.regionId = id();
		return level;
	}

	@Override
	public void configureLevel(Level level) {
		//candidates: open cells touching a wall or the map edge
		ArrayList<Integer> wallCells = new ArrayList<>();
		for (int i = 0; i < level.length(); i++) {
			if (openCell(level, i) && againstWall(level, i)) {
				wallCells.add(i);
			}
		}

		//place up to 4 devices, at least 5 tiles apart
		int placed = 0;
		while (placed < 4 && !wallCells.isEmpty()) {
			int cell = wallCells.remove(Random.Int(wallCells.size()));

			//aim down the longest clear line of fire
			int[] ray = longestRay(level, cell);
			if (ray[0] < 4) continue;

			ArrowDevice device = new ArrowDevice();
			device.pos = cell;
			device.facing(ArrowDevice.facingFor(ray[1], ray[2]));
			level.mobs.add(device);
			placed++;

			wallCells.removeIf(c -> level.distance(c, cell) < 5);
		}
	}

	private boolean openCell(Level level, int cell) {
		return level.passable[cell] && !level.solid[cell]
				&& level.findMob(cell) == null
				&& level.traps.get(cell) == null
				&& level.entityAt(cell) == null;
	}

	private boolean againstWall(Level level, int cell) {
		for (int n : PathFinder.NEIGHBOURS8) {
			int c = cell + n;
			if (!level.insideMap(c) || !level.passable[c]) {
				return true;
			}
		}
		return false;
	}

	//returns {length, dx, dy} of the longest clear ray, using the same
	//stopping rules as the arrow device itself
	private int[] longestRay(Level level, int cell) {
		int bestLen = 0, bestDx = 0, bestDy = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				int len = rayLength(level, cell, dx, dy);
				if (len > bestLen) {
					bestLen = len;
					bestDx = dx;
					bestDy = dy;
				}
			}
		}
		return new int[]{bestLen, bestDx, bestDy};
	}

	private int rayLength(Level level, int cell, int dx, int dy) {
		int w = level.width();
		int h = level.height();
		int x = cell % w;
		int y = cell / w;
		int len = 0;
		while (true) {
			x += dx;
			y += dy;
			if (x < 0 || x >= w || y < 0 || y >= h) break;
			int c = x + y * w;
			if (level.solid[c] || (!level.passable[c] && !level.avoid[c])) break;
			len++;
		}
		return len;
	}
}
