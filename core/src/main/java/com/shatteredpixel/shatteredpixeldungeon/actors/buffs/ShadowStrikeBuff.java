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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/**
 * MISERY feature: temporary buff granted by teleporting through shadows.
 * The hero's next attack deals bonus damage (+100% on ambush, +50% otherwise),
 * then the buff is consumed in {@link com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero#attackProc}.
 */
public class ShadowStrikeBuff extends FlavourBuff {

	{
		type = buffType.POSITIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.WEAPON;
	}
}
