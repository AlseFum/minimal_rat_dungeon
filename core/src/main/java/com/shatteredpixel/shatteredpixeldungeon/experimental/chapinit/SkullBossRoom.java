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

package com.shatteredpixel.shatteredpixeldungeon.experimental.chapinit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;

/**
 * 碎骨的 BOSS 房：菱形空地 + 从每个门延伸进来的一小段走廊。
 * 地形照抄 DiamondGooRoom，去掉史莱姆窝（水十字 + GooNest），改为放置碎骨。
 */
public class SkullBossRoom extends StandardRoom {

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 1, 0};
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		return false;
	}

	@Override
	public void paint(Level level) {

		Painter.fill(level, this, Terrain.WALL);
		Painter.fillDiamond(level, this, 1, Terrain.EMPTY);

		for (Door door : connected.values()) {
			door.set(Door.Type.REGULAR);

			Point dir;
			if (door.x == left) {
				dir = new Point(1, 0);
			} else if (door.y == top) {
				dir = new Point(0, 1);
			} else if (door.x == right) {
				dir = new Point(-1, 0);
			} else {
				dir = new Point(0, -1);
			}

			Point curr = new Point(door);
			do {
				Painter.set(level, curr, Terrain.EMPTY_SP);
				curr.x += dir.x;
				curr.y += dir.y;
			} while (level.map[level.pointToCell(curr)] == Terrain.WALL);
		}

		//BOSS 在 boss room 的 paint 阶段放入（同 GooBossRoom 的做法）
		SkullShatterer boss = new SkullShatterer();
		boss.pos = level.pointToCell(center());
		level.mobs.add(boss);
	}

	@Override
	public boolean canPlaceWater(Point p) {
		return false;
	}
}
