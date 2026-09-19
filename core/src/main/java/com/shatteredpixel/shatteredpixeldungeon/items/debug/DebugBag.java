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

package com.shatteredpixel.shatteredpixeldungeon.items.debug;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;

import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugPlacer;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugMapTool;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugWeaponPool;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugGiveItems;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugTalentLayers;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugTier;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugTool;
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
		//not literally unbounded: window/UI free-space loops iterate (capacity - filled) times,
		//so an Integer.MAX_VALUE capacity would hang the game when this bag is opened
		return Math.max(20, items.size() + 20);
	}

	@Override
	public boolean canHold( Item item ) {
		return item instanceof DebugTool && super.canHold(item);
	}

	/** Installs the current set of debug tools into a new bag for the hero. */
	public static void install() {
		DebugBag bag = new DebugBag();
		if (bag.collect()) {
			new DebugPlacer().collect();
			new DebugMapTool().collect();
			new DebugWeaponPool().collect();
			new DebugGiveItems().collect();
			new DebugTalentLayers().collect();
			new DebugTier().collect();
		}
	}
}
