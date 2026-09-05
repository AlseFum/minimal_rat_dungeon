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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

/**
 * MISERY feature: enemies standing in cripple miasma are slowed and easier to hit.
 * Speed is reduced by 33% (see {@link com.shatteredpixel.shatteredpixeldungeon.actors.Char#spend}),
 * evasion by 30% (see {@link Mob#defenseSkill}).
 */
public class CrippleDebuff extends FlavourBuff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 0.3f, 0.7f);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}

	/** Reduce speed by 33% while crippled. */
	public float speedFactor() {
		return 0.67f;
	}

	/** Returns an evasion multiplier for a mob under CrippleDebuff (-30% evasion). */
	public static float evasionMultiplier(Mob mob) {
		CrippleDebuff debuff = mob.buff(CrippleDebuff.class);
		return debuff != null ? 0.7f : 1f;
	}
}
