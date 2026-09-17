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

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;

/**
 * 盗贼开局专属匕首，与 WornShortsword/Sword 同先例：
 * 一把长期使用磨损的旧匕首。沿用 Dagger 的贴图、面板（1-8）与偷袭加成、sneak 武技，
 * 故不重写任何行为——差异体现在名称、描述与发放渠道。
 * <p>开局专属，不入任何掉落池（ItemDropRegistry / WEP deck）；
 * Dagger 本体改作一般武池武器。
 */
public class WornDagger extends Dagger {

	//沿用 Dagger 的贴图、面板与一切行为

}
