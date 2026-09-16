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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * 碎骨的半血狂暴（d:/dungeon-repo/Enemy/SkullShatterer.md「半血时会给自己上掉血狂暴的buff」）：
 * 每回合自伤 {@link #SELF_DAMAGE} 点，换取更高的攻击力（加成见
 * {@link SkullShatterer#damageRoll()}）。无持续回合，挂上即到死。
 */
public class SkullRage extends Buff {

	/** 每回合自伤点数 */
	public static final int SELF_DAMAGE = 1;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (target.isAlive()) {
			Proc.damage(new DamageInfo().defender(target)
					.way(DamageWay.SELF).type(DamageType.PHYSICAL)
					.solidDamage(SELF_DAMAGE));
			spend(TICK);
		} else {
			detach();
		}
		return true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", SELF_DAMAGE);
	}
}
