/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.shatteredpixel.shatteredpixeldungeon.messages;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** A small locale-aware bundle backed by {@link Utf8Properties}. */
final class Utf8MessageBundle {

	private final Map<String, String> values;
	private final Utf8MessageBundle parent;

	private Utf8MessageBundle(Map<String, String> values, Utf8MessageBundle parent) {
		this.values = values;
		this.parent = parent;
	}

	static Utf8MessageBundle create(FileHandle baseFile, Locale locale) {
		Utf8MessageBundle bundle = loadIfPresent(baseFile, "", null);
		for (String suffix : localeSuffixes(locale)) {
			Utf8MessageBundle localized = loadIfPresent(baseFile, suffix, bundle);
			if (localized != null) {
				bundle = localized;
			}
		}

		if (bundle == null) {
			throw new GdxRuntimeException("No properties files found for bundle: " + baseFile.path());
		}
		return bundle;
	}

	String get(String key) {
		if (values.containsKey(key)) {
			return values.get(key);
		}
		return parent == null ? null : parent.get(key);
	}

	private static Utf8MessageBundle loadIfPresent(FileHandle baseFile, String suffix,
			Utf8MessageBundle parent) {
		FileHandle file = baseFile.sibling(baseFile.name() + suffix + ".properties");
		if (!file.exists()) {
			return null;
		}
		return new Utf8MessageBundle(Utf8Properties.load(file), parent);
	}

	private static List<String> localeSuffixes(Locale locale) {
		List<String> suffixes = new ArrayList<>(3);
		if (locale == null || locale.equals(Locale.ROOT) || locale.getLanguage().isEmpty()) {
			return suffixes;
		}

		String language = locale.getLanguage();
		suffixes.add("_" + language);
		if (!locale.getCountry().isEmpty()) {
			suffixes.add("_" + language + "_" + locale.getCountry());
			if (!locale.getVariant().isEmpty()) {
				suffixes.add("_" + language + "_" + locale.getCountry() + "_" + locale.getVariant());
			}
		}
		return suffixes;
	}
}
