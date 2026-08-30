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
 * A fixed ice crystal altar which periodically vents a small wisp of
 * freezing vapor onto its own covered cells. The vapor lingers for a long
 * time, but vents are infrequent.
 *
 * Each covered cell shows a fixed tile which cycles through three states:
 * idle (dark blue) -> primed (blue) -> venting (pale ice blue, when vapor
 * is released).
 */
public class IceCrystalAltar extends Blob implements Hero.Doom {

	/** turns between each freezing vent */
	public static final int VENT_INTERVAL = 10;

	//visual states, matching the vent timer
	private static final int IDLE_COLOR = 0x17324a;     //safe, just vented
	private static final int PRIMED_COLOR = 0x2e8fc9;   //about to vent
	private static final int VENTING_COLOR = 0xc9f0ff;  //venting right now

	//amount of vapor seeded per covered cell
	private static final int VAPOR_AMOUNT = 20;

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
		//so the altar neither spreads nor fades away
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

	//seeds small wisps of freezing vapor onto the 12 cells around each
	//covered cell (the 8 adjacent cells plus the 4 cells two steps out in
	//cardinal directions), then restarts the timer
	private void vent() {
		ventTimer = VENT_INTERVAL;

		int w = Dungeon.level.width();
		int h = Dungeon.level.height();

		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				int cell = i + j * w;
				if (cur[cell] > 0) {
					int x = cell % w;
					int y = cell / w;
					for (int dx = -2; dx <= 2; dx++) {
						for (int dy = -2; dy <= 2; dy++) {
							int dist = Math.abs(dx) + Math.abs(dy);
							//only the 8 adjacent cells and the 4 cardinal cells two steps out
							if (dist == 0 || dist > 2) continue;
							if (dist == 2 && dx != 0 && dy != 0) continue;
							int nx = x + dx;
							int ny = y + dy;
							if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
							GameScene.add(Blob.seed(nx + ny * w, VAPOR_AMOUNT, FrostVapor.class));
						}
					}
					if (Dungeon.level.heroFOV[cell]) {
						CellEmitter.get(cell).burst(Speck.factory(Speck.STEAM), 5);
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
