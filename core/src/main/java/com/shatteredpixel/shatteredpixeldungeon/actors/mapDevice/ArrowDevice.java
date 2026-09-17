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

package com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.hero.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ArrowDeviceSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * A stationary device which watches one fixed direction (its facing).
 *
 * Each turn it scans the straight ray extending from itself in that direction;
 * if the hero is standing anywhere along that ray, it starts charging.
 * After {@link #CHARGE_TIME} turns of charging it fires a volley of arrows
 * down the ray, damaging every creature on it until the first wall or closed
 * door. The arrows pass through creatures rather than stopping at them.
 *
 * Facing is an index 0..7 over the 8 compass directions (see the DIRS table),
 * set at placement time by the debug tool and mirrored on the sprite by
 * rotating it in {@link ArrowDeviceSprite}.
 */
public class ArrowDevice extends MapDevice {

	public static final int E = 0, NE = 1, N = 2, NW = 3, W = 4, SW = 5, S = 6, SE = 7;

	//unit direction vectors per facing index
	private static final int[][] DIRS = {
			{ 1, 0}, { 1,-1}, { 0,-1}, {-1,-1},
			{-1, 0}, {-1, 1}, { 0, 1}, { 1, 1}
	};

	/** turns spent charging before the arrows are released */
	public static final int CHARGE_TIME = 3;

	/** turns spent cooling down after a volley before it can start charging again */
	public static final int COOLDOWN_TIME = 5;

	private static final int IDLE = 0;
	private static final int CHARGING = 1;
	private static final int COOLDOWN = 2;

	private int facing = E;
	private int state = IDLE;
	private int chargeTurns = 0;
	private int cooldownTurns = 0;

	//shared arrow image for the missile sprites
	private static final SpiritBow.SpiritArrow ARROW = new SpiritBow().knockArrow();

	{
		spriteClass = ArrowDeviceSprite.class;
	}

	public int facing() {
		return facing;
	}

	public void facing(int facing) {
		this.facing = facing;
	}

	/** Converts a signed (dx,dy) into the closest facing index, 0..7. */
	public static int facingFor(int dx, int dy) {
		dx = Integer.compare(dx, 0);
		dy = Integer.compare(dy, 0);
		for (int i = 0; i < DIRS.length; i++) {
			if (DIRS[i][0] == dx && DIRS[i][1] == dy) return i;
		}
		return E;
	}

	@Override
	protected ActionSubmission proposeAction() {
		switch (state) {
			case IDLE:
				if (heroInRay()) {
					System.out.println("[ARROW] device at " + pos + " spotted hero, charging " + CHARGE_TIME);
					startCharging();
				}
				break;
			case CHARGING:
				if (--chargeTurns <= 0) {
					System.out.println("[ARROW] device at " + pos + " firing");
					fire();
				} else {
					updateSprite();
				}
				break;
			case COOLDOWN:
				if (--cooldownTurns <= 0) {
					state = IDLE;
					updateSprite();
				}
				break;
		}
		spend(TICK);
		return ActionSubmission.idle();
	}

	//all cells the arrows would fly through, stopping at walls and closed doors
	private ArrayList<Integer> rayCells() {
		ArrayList<Integer> cells = new ArrayList<>();
		int w = Dungeon.level.width();
		int h = Dungeon.level.height();
		int x = pos % w;
		int y = pos / w;
		int dx = DIRS[facing][0];
		int dy = DIRS[facing][1];
		while (true) {
			x += dx;
			y += dy;
			if (x < 0 || x >= w || y < 0 || y >= h) break;
			int c = x + y * w;
			if (Dungeon.level.solid[c] || (!Dungeon.level.passable[c] && !Dungeon.level.avoid[c])) break;
			cells.add(c);
		}
		return cells;
	}

	private boolean heroInRay() {
		return Dungeon.hero != null && Dungeon.hero.isAlive() && rayCells().contains(Dungeon.hero.pos);
	}

	private void startCharging() {
		state = CHARGING;
		chargeTurns = CHARGE_TIME;
		if (Dungeon.level.heroFOV[pos]) {
			GLog.i(Messages.get(ArrowDevice.class, "charging"));
			Sample.INSTANCE.play(Assets.Sounds.TRAP);
		}
		updateSprite();
	}

	private void fire() {
		state = COOLDOWN;
		chargeTurns = 0;
		cooldownTurns = COOLDOWN_TIME;
		updateSprite();

		final ArrayList<Integer> ray = rayCells();
		ArrayList<Char> targets = new ArrayList<>();
		for (int c : ray) {
			Char ch = Actor.findChar(c);
			if (ch != null && ch != this && ch.isAlive()) targets.add(ch);
		}
		//arrow has nowhere to fly (device flush against a wall)
		if (ray.isEmpty()) return;

		//show the volley if either the device or any target is on screen
		boolean visible = Dungeon.level.heroFOV[pos];
		if (!visible) {
			for (Char ch : targets) {
				if (Dungeon.level.heroFOV[ch.pos]) {
					visible = true;
					break;
				}
			}
		}

		//even with no targets an arrow still flies to the end of the line
		final int endCell = ray.get(ray.size() - 1);

		if (visible) {
			//we handle the visuals inside of a separate actor so the game
			//pauses until the last arrow has landed
			final ArrayList<Char> finalTargets = targets;
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					Actor.remove(this);
					Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW, 1f, 0.9f);
					int[] remaining = { Math.max(finalTargets.size(), 1) };
					for (Char ch : finalTargets) {
						final Char target = ch;
						((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).
								reset(pos, target.sprite, ARROW, new Callback() {
									@Override
									public void call() {
										hit(target);
										if (--remaining[0] <= 0) next();
									}
								});
					}
					if (finalTargets.isEmpty()) {
						((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).
								reset(pos, endCell, ARROW, new Callback() {
									@Override
									public void call() {
										if (--remaining[0] <= 0) next();
									}
								});
					}
					return false;
				}

			});
		} else if (!targets.isEmpty()) {
			for (Char ch : targets) hit(ch);
		}
	}

	private void hit(Char ch) {
		if (ch == null || !ch.isAlive()) return;

		int dmg = Random.NormalIntRange(4, 8) + Dungeon.scalingDepth() - ch.drRoll();
		Proc.damage(new DamageInfo().offender(this).defender(ch)
				.way(DamageWay.TRAP).type(DamageType.PHYSICAL)
				.solidDamage(Math.max(dmg, 0)));

		if (ch == Dungeon.hero && !ch.isAlive()) {
			Dungeon.fail(this);
			GLog.n(Messages.get(ArrowDevice.class, "ondeath"));
		}

		ch.sprite.bloodBurstA(ch.sprite.center(), Math.max(dmg, 0));
		ch.sprite.flash();
	}

	private void updateSprite() {
		if (sprite instanceof ArrowDeviceSprite) {
			((ArrowDeviceSprite) sprite).setFacing(facing);
			((ArrowDeviceSprite) sprite).setCharging(state == CHARGING);
			((ArrowDeviceSprite) sprite).setCooldown(state == COOLDOWN);
		}
	}

	private static final String FACING = "facing";
	private static final String STATE = "state";
	private static final String CHARGE = "charge";
	private static final String COOLDOWN_TURNS = "cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FACING, facing);
		bundle.put(STATE, state);
		bundle.put(CHARGE, chargeTurns);
		bundle.put(COOLDOWN_TURNS, cooldownTurns);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		facing = bundle.getInt(FACING);
		state = bundle.getInt(STATE);
		chargeTurns = bundle.getInt(CHARGE);
		cooldownTurns = bundle.getInt(COOLDOWN_TURNS);
	}
}
