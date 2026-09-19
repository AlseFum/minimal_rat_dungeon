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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.hero;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;

/**
 * 战士开局专属剑，忠实还原原作 Worn Shortsword：
 * 一把长期使用磨损的旧短剑。fork 通用单手剑公式 +0 即 1-10，与原作面板一致，
 * 故不重写 min/max——差异体现在名称、描述与发放渠道。
 * <p>开局专属，不入任何掉落池（ItemRegistry / WEP deck）。
 */
public class WornShortsword extends Sword {

	//沿用 Sword 的贴图、面板与一切行为

}
