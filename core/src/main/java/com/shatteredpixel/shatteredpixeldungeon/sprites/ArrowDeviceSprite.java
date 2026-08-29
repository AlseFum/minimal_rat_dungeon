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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

/**
 * Sprite for the {@link com.shatteredpixel.shatteredpixeldungeon.actors.mapDevice.ArrowDevice}.
 *
 * The device's facing direction is shown by rotating the tile in 45° steps,
 * and its charging state by a red tint (blue when idle).
 */
public class ArrowDeviceSprite extends MapDeviceSprite {

	private static final int IDLE_COLOR = 0x99CCFF;
	private static final int CHARGING_COLOR = 0xFF5533;
	private static final int COOLDOWN_COLOR = 0x667799;

	private int facing = -1;
	private boolean charging = false;
	private boolean cooldown = false;

	public void setFacing(int facing) {
		if (this.facing == facing) return;
		this.facing = facing;
		originToCenter();
		angle = facing * (float) Math.PI / 4f;
	}

	public void setCharging(boolean charging) {
		if (this.charging == charging) return;
		this.charging = charging;
		refreshColor();
	}

	public void setCooldown(boolean cooldown) {
		if (this.cooldown == cooldown) return;
		this.cooldown = cooldown;
		refreshColor();
	}

	private void refreshColor() {
		if (charging)       hardlight(CHARGING_COLOR);
		else if (cooldown)  hardlight(COOLDOWN_COLOR);
		else                hardlight(IDLE_COLOR);
	}
}
