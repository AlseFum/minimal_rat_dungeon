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

package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.DebugNextFloor;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugMapDevice;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugArrowDevice;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugHeatPumpChannel;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugIceCrystalAltar;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugOriginiumAltar;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugTalentLayers;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugTier;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugTool;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** A debug-only container for tools used while developing the game. */
public class DebugBag extends Bag {

	{
		image = ItemSpriteSheet.BACKPACK;
		keptThoughLostInvent = true;
	}

	@Override
	public int capacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canHold( Item item ) {
		return item instanceof DebugTool && super.canHold(item);
	}

	/** Installs the current set of debug tools into a new bag for the hero. */
	public static void install() {
		DebugBag bag = new DebugBag();
		if (bag.collect()) {
			new DebugNextFloor().collect();
			new DebugMapDevice().collect();
			new DebugArrowDevice().collect();
			new DebugHeatPumpChannel().collect();
			new DebugIceCrystalAltar().collect();
			new DebugOriginiumAltar().collect();
			new DebugTalentLayers().collect();
			new DebugTier().collect();
		}
	}
}
