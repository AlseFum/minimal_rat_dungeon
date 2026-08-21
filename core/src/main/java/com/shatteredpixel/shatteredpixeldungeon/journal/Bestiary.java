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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Minimal showcase bestiary. Rat represents regular enemies and Goo represents
 * bosses; encounter hooks for every other entity safely do nothing.
 */
public enum Bestiary {

	REGIONAL,
	BOSSES;

	private final LinkedHashMap<Class<?>, Boolean> seen = new LinkedHashMap<>();
	private final LinkedHashMap<Class<?>, Integer> encounterCount = new LinkedHashMap<>();

	static {
		REGIONAL.addEntities(Rat.class);
		BOSSES.addEntities(Goo.class);
	}

	private void addEntities(Class<?>... classes) {
		for (Class<?> cls : classes) {
			seen.put(cls, false);
			encounterCount.put(cls, 0);
		}
	}

	public Collection<Class<?>> entities() {
		return seen.keySet();
	}

	public String title() {
		return Messages.get(this, name() + ".title");
	}

	public int totalEntities() {
		return seen.size();
	}

	public int totalSeen() {
		int total = 0;
		for (boolean entitySeen : seen.values()) {
			if (entitySeen) {
				total++;
			}
		}
		return total;
	}

	public static boolean isSeen(Class<?> cls) {
		for (Bestiary category : values()) {
			Boolean value = category.seen.get(cls);
			if (value != null) {
				return value;
			}
		}
		return false;
	}

	public static void setSeen(Class<?> cls) {
		boolean changed = false;
		for (Bestiary category : values()) {
			if (Boolean.FALSE.equals(category.seen.get(cls))) {
				category.seen.put(cls, true);
				changed = true;
			}
		}
		if (changed) {
			Journal.saveNeeded = true;
			Badges.validateCatalogBadges();
		}
	}

	public static int encounterCount(Class<?> cls) {
		for (Bestiary category : values()) {
			Integer value = category.encounterCount.get(cls);
			if (value != null) {
				return value;
			}
		}
		return 0;
	}

	// Boss cleanup temporarily suppresses generated-minion encounters.
	public static boolean skipCountingEncounters = false;

	public static void countEncounter(Class<?> cls) {
		countEncounters(cls, 1);
	}

	public static void countEncounters(Class<?> cls, int encounters) {
		if (skipCountingEncounters) {
			return;
		}
		for (Bestiary category : values()) {
			Integer current = category.encounterCount.get(cls);
			if (current != null && current != Integer.MAX_VALUE) {
				int updated = current + encounters;
				if (updated < -1_000_000_000) {
					updated = Integer.MAX_VALUE;
				}
				category.encounterCount.put(cls, updated);
				Journal.saveNeeded = true;
			}
		}
	}

	private static final String BESTIARY_CLASSES = "bestiary_classes";
	private static final String BESTIARY_SEEN = "bestiary_seen";
	private static final String BESTIARY_ENCOUNTERS = "bestiary_encounters";

	public static void store(Bundle bundle) {
		ArrayList<Class<?>> classes = new ArrayList<>();
		ArrayList<Boolean> seenValues = new ArrayList<>();
		ArrayList<Integer> encounters = new ArrayList<>();

		for (Bestiary category : values()) {
			for (Class<?> entity : category.entities()) {
				if (category.seen.get(entity) || category.encounterCount.get(entity) > 0) {
					classes.add(entity);
					seenValues.add(category.seen.get(entity));
					encounters.add(category.encounterCount.get(entity));
				}
			}
		}

		Class<?>[] storedClasses = new Class<?>[classes.size()];
		boolean[] storedSeen = new boolean[seenValues.size()];
		int[] storedEncounters = new int[encounters.size()];
		for (int i = 0; i < storedClasses.length; i++) {
			storedClasses[i] = classes.get(i);
			storedSeen[i] = seenValues.get(i);
			storedEncounters[i] = encounters.get(i);
		}

		bundle.put(BESTIARY_CLASSES, storedClasses);
		bundle.put(BESTIARY_SEEN, storedSeen);
		bundle.put(BESTIARY_ENCOUNTERS, storedEncounters);
	}

	public static void restore(Bundle bundle) {
		if (!bundle.contains(BESTIARY_CLASSES)
				|| !bundle.contains(BESTIARY_SEEN)
				|| !bundle.contains(BESTIARY_ENCOUNTERS)) {
			return;
		}

		Class<?>[] classes = bundle.getClassArray(BESTIARY_CLASSES);
		boolean[] seenValues = bundle.getBooleanArray(BESTIARY_SEEN);
		int[] encounters = bundle.getIntArray(BESTIARY_ENCOUNTERS);
		int count = Math.min(classes.length, Math.min(seenValues.length, encounters.length));

		for (int i = 0; i < count; i++) {
			for (Bestiary category : values()) {
				if (category.seen.containsKey(classes[i])) {
					category.seen.put(classes[i], seenValues[i]);
					category.encounterCount.put(classes[i], encounters[i]);
				}
			}
		}
	}
}
