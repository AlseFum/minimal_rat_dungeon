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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Minimal showcase catalog. Generic catalog hooks remain valid, but only gold is
 * registered and therefore persisted or displayed.
 */
public enum Catalog {

	MISC_CONSUMABLES;

	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	private final LinkedHashMap<Class<?>, Integer> useCount = new LinkedHashMap<>();

	static {
		MISC_CONSUMABLES.addItems(Gold.class);
	}

	private void addItems(Class<?>... items) {
		for (Class<?> item : items) {
			seen.put(item, false);
			useCount.put(item, 0);
		}
	}

	public Collection<Class<?>> items() {
		return seen.keySet();
	}

	public String title() {
		return Messages.get(this, name() + ".title");
	}

	public int totalItems() {
		return seen.size();
	}

	public int totalSeen() {
		int total = 0;
		for (boolean itemSeen : seen.values()) {
			if (itemSeen) {
				total++;
			}
		}
		return total;
	}

	public static boolean isSeen(Class<?> cls) {
		for (Catalog catalog : values()) {
			Boolean value = catalog.seen.get(cls);
			if (value != null) {
				return value;
			}
		}
		return false;
	}

	public static void setSeen(Class<?> cls) {
		boolean changed = false;
		for (Catalog catalog : values()) {
			if (Boolean.FALSE.equals(catalog.seen.get(cls))) {
				catalog.seen.put(cls, true);
				changed = true;
			}
		}
		if (changed) {
			Journal.saveNeeded = true;
			Badges.validateCatalogBadges();
		}
	}

	public static int useCount(Class<?> cls) {
		for (Catalog catalog : values()) {
			Integer value = catalog.useCount.get(cls);
			if (value != null) {
				return value;
			}
		}
		return 0;
	}

	public static void countUse(Class<?> cls) {
		countUses(cls, 1);
	}

	public static void countUses(Class<?> cls, int uses) {
		// Uses in the vault tester are intentionally ignored.
		if (Dungeon.depth > 15 && Dungeon.branch > 0) {
			return;
		}
		for (Catalog catalog : values()) {
			Integer current = catalog.useCount.get(cls);
			if (current != null && current != Integer.MAX_VALUE) {
				int updated = current + uses;
				if (updated < -1_000_000_000) {
					updated = Integer.MAX_VALUE;
				}
				catalog.useCount.put(cls, updated);
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String CATALOG_CLASSES = "catalog_classes";
	private static final String CATALOG_SEEN = "catalog_seen";
	private static final String CATALOG_USES = "catalog_uses";

	public static void store(Bundle bundle) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seenValues = new ArrayList<>();
		ArrayList<Integer> uses = new ArrayList<>();

		for (Catalog catalog : values()) {
			for (Class<?> item : catalog.items()) {
				if (catalog.seen.get(item) || catalog.useCount.get(item) > 0) {
					classes.add(item);
					seenValues.add(catalog.seen.get(item));
					uses.add(catalog.useCount.get(item));
				}
			}
		}

		Class<?>[] storedClasses = new Class<?>[classes.size()];
		boolean[] storedSeen = new boolean[seenValues.size()];
		int[] storedUses = new int[uses.size()];
		for (int i = 0; i < storedClasses.length; i++) {
			storedClasses[i] = classes.get(i);
			storedSeen[i] = seenValues.get(i);
			storedUses[i] = uses.get(i);
		}

		bundle.put(CATALOG_CLASSES, storedClasses);
		bundle.put(CATALOG_SEEN, storedSeen);
		bundle.put(CATALOG_USES, storedUses);
	}

	public static void restore(Bundle bundle) {
		if (!bundle.contains(CATALOG_CLASSES)
				|| !bundle.contains(CATALOG_SEEN)
				|| !bundle.contains(CATALOG_USES)) {
			return;
		}

		Class<?>[] classes = bundle.getClassArray(CATALOG_CLASSES);
		boolean[] seenValues = bundle.getBooleanArray(CATALOG_SEEN);
		int[] uses = bundle.getIntArray(CATALOG_USES);
		int count = Math.min(classes.length, Math.min(seenValues.length, uses.length));

		for (int i = 0; i < count; i++) {
			for (Catalog catalog : values()) {
				if (catalog.seen.containsKey(classes[i])) {
					catalog.seen.put(classes[i], seenValues[i]);
					catalog.useCount.put(classes[i], uses[i]);
				}
			}
		}
	}
}
