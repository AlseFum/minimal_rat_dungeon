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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SentryRoom extends SpecialRoom {

	@Override
	public int minWidth() { return 7; }
	public int minHeight() { return 7; }

	@Override
	public void paint(Level level) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		Door entrance = entrance();

		Point center;
		do {
			center = center();
		} while (center.x == entrance.x || center.y == entrance.y);

		Point sentryPos = new Point();
		Point treasurePos = new Point();

		//determine position of sentry, treasure, and paint safe tiles / statues
		if (entrance.x == left){
			sentryPos.set(right-1, center.y);
			Painter.fill(level, left+1, top+1, 1, height()-2, Terrain.EMPTY);
			if (entrance.y > center.y){
				treasurePos.set(left+1, (top + 1 + center.y)/2);
				Painter.fill(level, left+1, top+1, 2, center.y-top-1, Terrain.EMPTY);
			} else {
				treasurePos.set(left+1, (bottom + center.y)/2);
				Painter.fill(level, left+1, center.y+1, 2, bottom-center.y-1, Terrain.EMPTY);
			}
			for (int x = right-3; x > left; x--){
				if (level.map[x + (center.y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, x, center.y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, x, center.y, Terrain.STATUE);
				}
			}
		} else if (entrance.x == right){
			sentryPos.set(left+1, center.y);
			Painter.fill(level, right-1, top+1, 1, height()-2, Terrain.EMPTY);
			if (entrance.y > center.y){
				treasurePos.set(right-1, (top + 1 + center.y)/2);
				Painter.fill(level, right-2, top+1, 2, center.y-top-1, Terrain.EMPTY);
			} else {
				treasurePos.set(right-1, (bottom + 1 + center.y)/2);
				Painter.fill(level, right-2, center.y+1, 2, bottom-center.y-1, Terrain.EMPTY);
			}
			for (int x = left+3; x < right; x++){
				if (level.map[x + (center.y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, x, center.y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, x, center.y, Terrain.STATUE);
				}
			}
		} else if (entrance.y == top){
			sentryPos.set(center.x, bottom-1);
			Painter.fill(level, left+1, top+1, width()-2, 1, Terrain.EMPTY);
			if (entrance.x > center.x){
				treasurePos.set((left + 1 + center.x)/2, top+1);
				Painter.fill(level, left+1, top+1, center.x-left-1, 2, Terrain.EMPTY);
			} else {
				treasurePos.set((right + center.x)/2, top+1);
				Painter.fill(level, center.x+1, top+1, right - center.x-1, 2, Terrain.EMPTY);
			}
			for (int y = bottom-3; y > top; y--){
				if (level.map[center.x + (y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, center.x, y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, center.x, y, Terrain.STATUE);
				}
			}
 		} else  if (entrance.y == bottom){
			sentryPos.set(center.x, top+1);
			Painter.fill(level, left+1, bottom-1, width()-2, 1, Terrain.EMPTY);
			if (entrance.x > center.x){
				treasurePos.set((left + 1 + center.x)/2, bottom-1);
				Painter.fill(level, left+1, bottom-2, center.x-left-1, 2, Terrain.EMPTY);
			} else {
				treasurePos.set((right + center.x)/2, bottom-1);
				Painter.fill(level, center.x+1, bottom-2, right - center.x-1, 2, Terrain.EMPTY);
			}
			for (int y = top+3; y < bottom; y++){
				if (level.map[center.x + (y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, center.x, y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, center.x, y, Terrain.STATUE);
				}
			}
		}

		Painter.set(level, sentryPos, Terrain.PEDESTAL);
			//The old room-specific sentry NPC is represented by the shared Rat variant.
			Rat sentry = Rat.of(Rat.Variant.SENTRY);
			sentry.pos = level.pointToCell(sentryPos);
			level.mobs.add( sentry );

		Painter.set(level, treasurePos, Terrain.PEDESTAL);
		level.drop( prize( level ), level.pointToCell(treasurePos) ).type = Heap.Type.CHEST;

		level.addItemToSpawn(new PotionOfHaste());

		entrance.set( Door.Type.REGULAR );
	}

	private static Item prize(Level level ) {

		Item prize;

		//50% chance for prize item
		if (Random.Int(2) == 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}

		//1 floor set higher in probability, never cursed
		switch (Random.Int(5)){
			case 0: case 1: default:
				prize = Loot.randomWeapon((Dungeon.depth / 5) + 1);
				if (((Weapon)prize).hasCurseEnchant()){
					((Weapon) prize).enchant(null);
				}
				break;
			case 2:
				prize = Loot.randomMissile((Dungeon.depth / 5) + 1);
				if (((Weapon)prize).hasCurseEnchant()){
					((Weapon) prize).enchant(null);
				}
				break;
			case 3: case 4:
				prize = Loot.randomArmor((Dungeon.depth / 5) + 1);
				if (((Armor)prize).hasCurseGlyph()){
					((Armor) prize).inscribe(null);
				}
				break;
		}
		prize.cursed = false;
		prize.cursedKnown = true;

		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}

		return prize;
	}

	@Override
	public boolean canConnect(Point p) {
		if (!super.canConnect(p)){
			return false;
		}
		//don't place door in the exact center, if that exists
		if (width() % 2 == 1 && p.x == center().x){
			return false;
		}
		if (height() % 2 == 1 && p.y == center().y){
			return false;
		}
		return true;
	}

}
