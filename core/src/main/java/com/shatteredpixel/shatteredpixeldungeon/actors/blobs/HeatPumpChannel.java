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
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MapDeviceSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.HashMap;

/**
 * A fixed floor channel which periodically vents scalding hot air: its
 * coverage neither spreads nor decays, and every {@link #VENT_INTERVAL}
 * turns it damages all creatures standing on it.
 *
 * Each covered cell shows a fixed tile which cycles through three states:
 * idle (blue) -> primed (yellow) -> venting (red, when damage is dealt).
 */
public class HeatPumpChannel extends Blob implements Hero.Doom {

	/** turns between each damaging vent */
	public static final int VENT_INTERVAL = 3;

	//visual states, matching the vent timer
	private static final int IDLE_COLOR = 0x4a1c15;     //safe, just vented
	private static final int PRIMED_COLOR = 0xc92e2e;   //about to vent
	private static final int VENTING_COLOR = 0xc40c0c;  //venting right now

	//vent damage per creature, roughly 3 at depth 1
	private int ventTimer = VENT_INTERVAL;

	//fixed per-cell visuals
	private HashMap<Integer, MapDeviceSprite> sprites = new HashMap<>();

	@Override
	protected void onAdd() {
		super.onAdd();
		updateSprites();
	}

	@Override
	protected void onRemove() {
		super.onRemove();
		for (MapDeviceSprite s : sprites.values()) {
			s.killAndErase();
		}
		sprites.clear();
	}

	@Override
	protected void evolve() {
		//keep the coverage unchanged: copy cur into off and recount volume,
		//so the channel neither spreads nor fades away
		volume = 0;
		for (int cell = 0; cell < cur.length; cell++) {
			off[cell] = cur[cell];
			volume += cur[cell];
		}

		updateSprites();
		applyTint();

		if (ventTimer == 0) {
			vent();
		} else {
			ventTimer--;
		}
	}

	//deals the periodic damage and restarts the timer
	private void vent() {
		ventTimer = VENT_INTERVAL;

		int damage = 2 + Dungeon.scalingDepth();
		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				int cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0) {
					Char ch = Actor.findChar(cell);
					if (ch != null && !ch.isImmune(getClass())) {
						Proc.damage(new DamageInfo().defender(ch)
								.way(DamageWay.ENV).type(DamageType.FIRE)
								.solidDamage(damage));
						if (ch.sprite != null) ch.sprite.flash();
						if (Dungeon.level.heroFOV[cell]) {
							CellEmitter.get(cell).burst(Speck.factory(Speck.RED_LIGHT), 5);
						}
					}
				}
			}
		}
	}

	//keeps a fixed tile sprite in sync with the covered cells
	private void updateSprites() {
		for (int cell = 0; cell < cur.length; cell++) {
			MapDeviceSprite s = sprites.get(cell);
			if (cur[cell] > 0) {
				if (s == null) {
					s = new MapDeviceSprite();
					s.place(cell);
					sprites.put(cell, s);
				}
				//the scene may not exist yet when onAdd runs (Actor.init happens
				//before GameScene.create), so re-attach until it sticks
				if (s.parent == null) {
					GameScene.addLevelVisual(s);
				}
				s.visible = Dungeon.level.heroFOV[cell];
			} else if (s != null) {
				s.killAndErase();
				sprites.remove(cell);
			}
		}
	}

	private void applyTint() {
		int color;
		if (ventTimer == 0) {
			color = VENTING_COLOR;
		} else if (ventTimer >= VENT_INTERVAL) {
			color = IDLE_COLOR;
		} else {
			color = PRIMED_COLOR;
		}
		for (MapDeviceSprite s : sprites.values()) {
			s.hardlight(color);
		}
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void onDeath() {
		Dungeon.fail(this);
		GLog.n(Messages.get(this, "ondeath"));
	}

	private static final String VENT_TIMER = "vent_timer";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( VENT_TIMER, ventTimer );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		ventTimer = bundle.getInt( VENT_TIMER );
	}
}
