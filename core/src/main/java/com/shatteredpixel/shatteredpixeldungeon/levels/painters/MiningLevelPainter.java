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

package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineSecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Graph;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class MiningLevelPainter extends RegularPainter {

	@Override
	protected int padding(Level level) {
		return 3;
	}

	private int goldToAdd = 0;

	public RegularPainter setGold(int amount){
		goldToAdd = amount;
		return this;
	}

	protected void generateGold(Level level, ArrayList<Room> rooms) {
		//we start by counting all the gold purposefully made by rooms
		for (int i = 0; i < level.length(); i++){
			if (level.map[i] == Terrain.WALL_DECO) {
				goldToAdd--;
			}
		}
		for (Heap h : level.heaps.valueList()){
			for (Item i : h.items){
				if (i instanceof DarkGold) goldToAdd -= i.quantity();
			}
		}

		int[] map = level.map;
		do {
			Random.shuffle(rooms);
			for (Room r : rooms) {

				if (r instanceof MineSecretRoom) continue;

				ArrayList<Integer> goldPosCandidates = new ArrayList<>();
				for (Point p : r.getPoints()){
					int i = level.pointToCell(p);

					if (level.insideMap(i) && goldToAdd > 0 && map[i] == Terrain.WALL){

						for (int j : PathFinder.NEIGHBOURS4){
							if (level.insideMap(i+j) && map[i+j] != Terrain.WALL){
								goldPosCandidates.add(i);
								break;
							}
						}
					}
				}

				if (goldToAdd > 0 && !goldPosCandidates.isEmpty()){
					int pos = Random.element(goldPosCandidates);

					map[pos] = Terrain.WALL_DECO;
					goldToAdd--;

					if (goldToAdd > 0){
						int i = PathFinder.NEIGHBOURS4[Random.Int(4)];
						if (level.insideMap(pos+i) && map[pos+i] == Terrain.WALL){
							map[pos+i] = Terrain.WALL_DECO;
							goldToAdd--;
						}
						if (Random.Int(2) == 0){
							i = PathFinder.NEIGHBOURS4[Random.Int(4)];
							if (level.insideMap(pos+i) && map[pos+i] == Terrain.WALL){
								map[pos+i] = Terrain.WALL_DECO;
								goldToAdd--;
							}
						}
					}

				}

			}
		} while (goldToAdd > 0);

	}

	@Override
	protected void paintDoors(Level l, ArrayList<Room> rooms) {
		HashMap<Room, Room> roomMerges = new HashMap<>();

		float hiddenDoorChance = 0.90f;

		//wall doors will still be wall
		//hidden doors become wall tiles a bit later in painting
		//everything else usually becomes empty, but can be wall sometimes
		for (Room r : rooms) {
			for (Room n : r.connected.keySet()) {

				Room.Door d = r.connected.get(n);
				int door = d.x + d.y * l.width();

				if (d.type == Room.Door.Type.WALL || d.type == Room.Door.Type.HIDDEN){
					l.map[door] = Terrain.WALL;
				} else {
					//some of these are randomly hidden, using the same rules as regular levels
					if (Random.Float() < hiddenDoorChance) {
						d.type = Room.Door.Type.HIDDEN;
						Graph.buildDistanceMap(rooms, r);
						if (n.distance == Integer.MAX_VALUE){
							l.map[door] = Terrain.EMPTY;
							d.type = Room.Door.Type.EMPTY;
						} else {
							l.map[door] = Terrain.WALL;
						}
					} else {
						l.map[door] = Terrain.EMPTY;
						d.type = Room.Door.Type.EMPTY;
					}

				}

				//if the door is empty, always merge the rooms
				if (l.map[door] == Terrain.EMPTY){
					if (roomMerges.get(r) == n || roomMerges.get(n) == r){
						continue;
					} else if (mergeRooms(l, r, n, r.connected.get(n), Terrain.EMPTY)) {
						roomMerges.put(r, n);
						roomMerges.put(n, r);
					}
				}

			}
		}
	}

	@Override
	protected void decorate(Level level, ArrayList<Room> rooms) {
		int w = level.width();
		int l = level.length();
		int[] map = level.map;

		for (Room r : rooms) {
			for (Room n : r.neigbours) {
				if (!r.connected.containsKey(n)) {
					mergeRooms(level, r, n, null, Random.Int(3) == 0 ? Terrain.REGION_DECO : Terrain.CHASM);
				}
			}
		}

		for (Room room : rooms) {
			if (!(room instanceof StandardRoom)) {
				continue;
			}

			if (room.width() <= 4 || room.height() <= 4) {
				continue;
			}

			int s = room.square();

			//for each corner, we have a chance to fill based on room size
			//but not if filling that corner replaces solid terrain, blocks a connection, or places a visible trap next to a wall
			if (Random.Int(s) > 8) {
				int corner = (room.left + 1) + (room.top + 1) * w;
				if ((Terrain.flags[map[corner]] & Terrain.SOLID) == 0
						&& map[corner - 1] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner - 1))
						&& map[corner - w] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner - w))
						&& map[corner + 1] != Terrain.TRAP && map[corner + w] != Terrain.TRAP) {
					map[corner] = Terrain.WALL;
					level.traps.remove(corner);
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.right - 1) + (room.top + 1) * w;
				if ((Terrain.flags[map[corner]] & Terrain.SOLID) == 0
						&& map[corner + 1] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner + 1))
						&& map[corner - w] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner - w))
						&& map[corner - 1] != Terrain.TRAP && map[corner + w] != Terrain.TRAP) {
					map[corner] = Terrain.WALL;
					level.traps.remove(corner);
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.left + 1) + (room.bottom - 1) * w;
				if ((Terrain.flags[map[corner]] & Terrain.SOLID) == 0
						&& map[corner - 1] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner - 1))
						&& map[corner + w] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner + w))
						&& map[corner + 1] != Terrain.TRAP && map[corner - w] != Terrain.TRAP) {
					map[corner] = Terrain.WALL;
					level.traps.remove(corner);
				}
			}

			if (Random.Int(s) > 8) {
				int corner = (room.right - 1) + (room.bottom - 1) * w;
				if ((Terrain.flags[map[corner]] & Terrain.SOLID) == 0
						&& map[corner + 1] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner + 1))
						&& map[corner + w] == Terrain.WALL && !room.connected.containsValue(level.cellToPoint(corner + w))
						&& map[corner - 1] != Terrain.TRAP && map[corner - w] != Terrain.TRAP) {
					map[corner] = Terrain.WALL;
					level.traps.remove(corner);
				}
			}
		}

		for (int i = w + 1; i < l - w; i++) {
			if (map[i] == Terrain.EMPTY) {
				int n = 0;
				if (map[i + 1] == Terrain.WALL) {
					n++;
				}
				if (map[i - 1] == Terrain.WALL) {
					n++;
				}
				if (map[i + w] == Terrain.WALL) {
					n++;
				}
				if (map[i - w] == Terrain.WALL) {
					n++;
				}
				if (Random.Int(6) <= n) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}

		// This call retains the old virtual dispatch to MiningLevelPainter.generateGold().
		generateGold(level, rooms);

		//no chasms allowed, replace with ground!
		for (int i = 0; i < level.length(); i++){
			if (level.map[i] == Terrain.CHASM){
				level.map[i] = Terrain.EMPTY;
			}
		}
	}
}
