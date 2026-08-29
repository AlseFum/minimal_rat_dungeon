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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.actors.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * A wisp of freezing vapor. It stays exactly where it was seeded and
 * decays very slowly, lingering for a long time while harming and
 * chilling creatures standing in it.
 */
public class FrostVapor extends Blob {

	//a seeded wisp (20 volume) burns off in about 3 turns
	private static final int DECAY_PER_TURN = 7;

	@Override
	protected void evolve() {
		//no spreading: the vapor stays put and loses several units of
		//volume per turn, so a seeded wisp lasts about 3 turns
		volume = 0;
		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				int cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0) {
					off[cell] = Math.max(cur[cell] - DECAY_PER_TURN, 0);
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}

		int damage = 1 + Dungeon.scalingDepth()/5;

		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0 && (ch = Actor.findChar( cell )) != null) {
					if (!ch.isImmune(getClass()) && !ch.isImmune(Chill.class)) {
						Proc.damage(new DamageInfo().defender(ch)
								.way(DamageWay.DOT).type(DamageType.FROST)
								.solidDamage(damage));
						Buff.prolong(ch, Chill.class, 2f);
					}
				}
			}
		}
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( Speck.factory( Speck.STEAM ), 0.4f );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
