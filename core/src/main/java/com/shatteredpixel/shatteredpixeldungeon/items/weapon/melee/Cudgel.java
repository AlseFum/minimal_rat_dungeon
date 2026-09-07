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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

/**
 * 牧师开局专属短棍，忠实还原原作 Cudgel：
 * 一支为贵族防身打造的小型黄铜棍。沿 Mace 面板（fork +0 即 1-8，同原作），
 * 但去掉硬头锤的高命中加成——朴实钝器。
 * <p>开局专属，不入任何掉落池。
 */
public class Cudgel extends Mace {

	{
		ACC = 1f; //黄铜短棍无硬头锤的命中加成
	}

}
