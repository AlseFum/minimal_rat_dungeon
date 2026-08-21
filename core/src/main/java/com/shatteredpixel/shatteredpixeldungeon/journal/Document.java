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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * The showcase keeps one document with one representative page.
 */
public enum Document {

	ADVENTURERS_GUIDE(ItemSpriteSheet.GUIDE_PAGE);

	public static final int NOT_FOUND = 0;
	public static final int FOUND = 1;
	public static final int READ = 2;

	public static final String GUIDE_INTRO = "Intro";

	private final int pageSprite;
	private final LinkedHashMap<String, Integer> pageStates = new LinkedHashMap<>();

	Document(int pageSprite) {
		this.pageSprite = pageSprite;
	}

	static {
		ADVENTURERS_GUIDE.pageStates.put(
				GUIDE_INTRO,
				DeviceCompat.isDebug() ? READ : NOT_FOUND);
	}

	public boolean findPage(String page) {
		if (pageStates.containsKey(page) && pageStates.get(page) == NOT_FOUND) {
			pageStates.put(page, FOUND);
			Journal.saveNeeded = true;
			Badges.validateCatalogBadges();
			return true;
		}
		return false;
	}

	public boolean deletePage(String page) {
		if (pageStates.containsKey(page) && pageStates.get(page) != NOT_FOUND) {
			pageStates.put(page, NOT_FOUND);
			Journal.saveNeeded = true;
			return true;
		}
		return false;
	}

	public boolean isPageFound(String page) {
		return pageStates.containsKey(page) && pageStates.get(page) > NOT_FOUND;
	}

	public boolean readPage(String page) {
		if (pageStates.containsKey(page)) {
			pageStates.put(page, READ);
			Journal.saveNeeded = true;
			Badges.validateCatalogBadges();
			return true;
		}
		return false;
	}

	public boolean isPageRead(String page) {
		return pageStates.containsKey(page) && pageStates.get(page) == READ;
	}

	public Collection<String> pageNames() {
		return pageStates.keySet();
	}

	public Image pageSprite() {
		return new ItemSprite(pageSprite);
	}

	public Image pageSprite(String page) {
		if (GUIDE_INTRO.equals(page) && isPageFound(page)) {
			return new ItemSprite(ItemSpriteSheet.MASTERY);
		}
		return pageSprite();
	}

	public String title() {
		return Messages.get(this, name() + ".title");
	}

	public String pageTitle(String page) {
		return Messages.get(this, name() + "." + page + ".title");
	}

	public String pageBody(String page) {
		return Messages.get(this, name() + "." + page + ".body");
	}

	private static final String DOCUMENTS = "documents";

	public static void store(Bundle bundle) {
		Bundle storedDocuments = new Bundle();

		for (Document document : values()) {
			Bundle storedPages = new Bundle();
			boolean empty = true;
			for (String page : document.pageNames()) {
				int state = document.pageStates.get(page);
				if (state != NOT_FOUND) {
					storedPages.put(page, state);
					empty = false;
				}
			}
			if (!empty) {
				storedDocuments.put(document.name(), storedPages);
			}
		}

		bundle.put(DOCUMENTS, storedDocuments);
	}

	public static void restore(Bundle bundle) {
		if (!bundle.contains(DOCUMENTS)) {
			return;
		}

		Bundle storedDocuments = bundle.getBundle(DOCUMENTS);
		for (Document document : values()) {
			if (!storedDocuments.contains(document.name())) {
				continue;
			}
			Bundle storedPages = storedDocuments.getBundle(document.name());
			for (String page : document.pageNames()) {
				if (storedPages.contains(page)) {
					document.pageStates.put(page, storedPages.getInt(page));
				}
			}
		}
	}
}
