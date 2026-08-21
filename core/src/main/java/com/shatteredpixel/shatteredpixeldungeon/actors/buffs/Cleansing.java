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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

/** Shared cleansing immunity used by class abilities and cleansing darts. */
public class Cleansing extends FlavourBuff {

	public static final float DURATION = 5f;

	{
		type = buffType.POSITIVE;
	}

	public static void cleanse(Char ch) {
		cleanse(ch, DURATION);
	}

	public static void cleanse(Char ch, float duration) {
		for (Buff buff : ch.buffs()) {
			if (buff.type == buffType.NEGATIVE
					&& !(buff instanceof AllyBuff)
					&& !(buff instanceof LostInventory)) {
				buff.detach();
			}
			if (buff instanceof Hunger) {
				((Hunger) buff).satisfy(Hunger.STARVING);
			}
		}
		Buff.prolong(ch, Cleansing.class, duration);
	}

	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0f, 2f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
}
