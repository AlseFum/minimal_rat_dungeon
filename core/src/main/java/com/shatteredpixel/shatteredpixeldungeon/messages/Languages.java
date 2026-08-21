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

package com.shatteredpixel.shatteredpixeldungeon.messages;

import java.util.Locale;

/** The compact language set shipped with the five-level showcase. */
public enum Languages {
	ENGLISH("english", "en", Status.O_COMPLETE, null, null),
	CHI_SMPL("简体中文", "zh", Status.__UNREVIEW, null, null),
	CHI_TRAD("繁體中文", "zh-hant", Status.__UNREVIEW, null, null);

	public enum Status {
		X_UNFINISH,
		__UNREVIEW,
		O_COMPLETE
	}

	private final String name;
	private final String code;
	private final Status status;
	private final String[] reviewers;
	private final String[] translators;

	Languages(String name, String code, Status status, String[] reviewers, String[] translators) {
		this.name = name;
		this.code = code;
		this.status = status;
		this.reviewers = reviewers;
		this.translators = translators;
	}

	public String nativeName() {
		return name;
	}

	public String code() {
		return code;
	}

	public Status status() {
		return status;
	}

	public String[] reviewers() {
		return reviewers == null ? new String[]{} : reviewers.clone();
	}

	public String[] translators() {
		return translators == null ? new String[]{} : translators.clone();
	}

	public static Languages matchLocale(Locale locale) {
		if (locale.getLanguage().equals("zh") && locale.toString().contains("Hant")) {
			return CHI_TRAD;
		}
		return matchCode(locale.getLanguage());
	}

	public static Languages matchCode(String code) {
		for (Languages lang : Languages.values()) {
			if (lang.code().equals(code)) {
				return lang;
			}
		}
		return ENGLISH;
	}
}
