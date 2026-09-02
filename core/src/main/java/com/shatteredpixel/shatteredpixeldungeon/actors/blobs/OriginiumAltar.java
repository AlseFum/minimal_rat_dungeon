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
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MapDeviceSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fixed originum altar which pulses periodically: every
 * {@link #VENT_INTERVAL} turns it directly damages all creatures on the
 * 12 cells around each covered cell.
 *
 * Creatures carrying {@link #exemptProperty} are exempt from the damage,
 * and instead receive 10% of the total damage dealt as healing. If
 * {@link #exemptProperty} is null (the default) nobody is exempt.
 *
 * Each covered cell shows a fixed tile which cycles through three states:
 * idle (dark purple) -> primed (purple) -> pulsing (pale magenta, when
 * damage is dealt).
 */
public class OriginiumAltar extends Blob implements Hero.Doom {

	/** turns between each damaging pulse */
	public static final int VENT_INTERVAL = 10;

	//visual states, matching the vent timer
	private static final int IDLE_COLOR = 0x2b1a3d;     //safe, just pulsed
	private static final int PRIMED_COLOR = 0x8f2ec9;   //about to pulse
	private static final int PULSING_COLOR = 0xd9a0ff;  //pulsing right now

	//pulse damage per creature, roughly 3 at depth 1
	private int ventTimer = VENT_INTERVAL;

	//fixed per-cell visuals
	private HashMap<Integer, MapDeviceSprite> sprites = new HashMap<>();

	/**
	 * Creatures with this property take no damage from the altar and are
	 * instead healed by 10% of the damage it deals. null (the default)
	 * disables the exemption entirely.
	 */
	public Char.Property exemptProperty;

	private static final String EXEMPT = "exempt";

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
			pulse();
		} else {
			ventTimer--;
		}
	}

	//deals damage to the 12 cells around each covered cell, then restarts the timer
	private void pulse() {
		ventTimer = VENT_INTERVAL;

		//victims of this pulse, for the 10% heal-back
		ArrayList<Char> targets = new ArrayList<>();
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
							int c = nx + ny * w;
							Char ch = Actor.findChar(c);
							if (ch != null && ch.isAlive()
									&& (exemptProperty == null || !Char.hasProp(ch, exemptProperty))) {
								targets.add(ch);
							}
							if (Dungeon.level.heroFOV[c]) {
								CellEmitter.get(c).burst(Speck.factory(Speck.STEAM), 3);
							}
						}
					}
				}
			}
		}

		int totalDamage = 0;
		int damage = 2 + Dungeon.scalingDepth()*3;
		for (Char ch : targets) {
			totalDamage += Proc.damage(new DamageInfo().defender(ch)
					.way(DamageWay.ENV).type(DamageType.PHYSICAL)
					.solidDamage(damage));
			if (ch.sprite != null) ch.sprite.flash();
		}

		//exempt creatures are healed by 10% of the damage dealt
		if (exemptProperty != null && totalDamage > 0) {
			int heal = Math.round(totalDamage * 0.1f);
			for (int i = area.left; i < area.right; i++) {
				for (int j = area.top; j < area.bottom; j++) {
					int cell = i + j * w;
					if (cur[cell] > 0) {
						int x = cell % w;
						int y = cell / w;
						for (int dx = -2; dx <= 2; dx++) {
							for (int dy = -2; dy <= 2; dy++) {
								int dist = Math.abs(dx) + Math.abs(dy);
								if (dist == 0 || dist > 2) continue;
								if (dist == 2 && dx != 0 && dy != 0) continue;
								int nx = x + dx;
								int ny = y + dy;
								if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
								Char ch = Actor.findChar(nx + ny * w);
								if (ch != null && ch.isAlive() && Char.hasProp(ch, exemptProperty)) {
									ch.HP = Math.min(ch.HT, ch.HP + heal);
									if (ch.sprite != null) {
										ch.sprite.showStatus(CharSprite.POSITIVE, "+" + heal);
									}
								}
							}
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
			color = PULSING_COLOR;
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
		bundle.put( EXEMPT, exemptProperty != null ? exemptProperty.name() : "" );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		ventTimer = bundle.getInt( VENT_TIMER );
		String name = bundle.getString( EXEMPT );
		exemptProperty = name.isEmpty() ? null : Char.Property.valueOf( name );
	}
}
