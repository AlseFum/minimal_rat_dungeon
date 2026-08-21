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

package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.io.IOException;

public class Key extends Item {

	public static final float TIME_TO_UNLOCK = 1f;

	public enum Kind {
		WORN(ItemSpriteSheet.WORN_KEY, 1, "worn"),
		CRYSTAL(ItemSpriteSheet.CRYSTAL_KEY, 2, "crystal"),
		GOLDEN(ItemSpriteSheet.GOLDEN_KEY, 3, "golden"),
		IRON(ItemSpriteSheet.IRON_KEY, 4, "iron");

		private final int image;
		private final int displaySlot;
		private final String messageKey;

		Kind(int image, int displaySlot, String messageKey) {
			this.image = image;
			this.displaySlot = displaySlot;
			this.messageKey = messageKey;
		}

		public int displaySlot() {
			return displaySlot;
		}
	}
	
	{
		stackable = true;
		unique = true;
	}

	private Kind kind;

	//TODO currently keys can only appear on branch = 0, add branch support here if that changes
	public int depth;

	public Key() {
		this(Kind.IRON, 0);
	}

	public Key(Kind kind, int depth) {
		setKind(kind);
		this.depth = depth;
	}

	public static Key random(int depth) {
		return new Key(Random.element(Kind.values()), depth);
	}

	public Kind kind() {
		return kind;
	}

	private void setKind(Kind kind) {
		this.kind = kind == null ? Kind.IRON : kind;
		image = this.kind.image;
	}

	@Override
	public String name() {
		return Messages.get(Key.class, kind.messageKey + "_name");
	}

	@Override
	public String desc() {
		return Messages.get(Key.class, kind.messageKey + "_desc");
	}
	
	@Override
	public boolean isSimilar( Item item ) {
		return item instanceof Key && ((Key) item).kind == kind && ((Key) item).depth == depth;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (kind == Kind.WORN && !SPDSettings.supportNagged()) {
			try {
				Dungeon.saveAll();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						ShatteredPixelDungeon.scene().add(new WndSupportPrompt());
					}
				});
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}

		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());
		GameScene.pickUpJournal(this, pos);
		WndJournal.last_index = 0;
		Notes.add(this);
		Sample.INSTANCE.play( Assets.Sounds.ITEM );
		hero.spendAndNext( pickupDelay() );
		GameScene.updateKeyDisplay();

		if (hero.buff(SkeletonKey.KeyReplacementTracker.class) != null){
			hero.buff(SkeletonKey.KeyReplacementTracker.class).processExcessKeys();
		}

		return true;
	}

	private static final String KIND = "kind";
	private static final String DEPTH = "depth";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( KIND, kind );
		bundle.put( DEPTH, depth );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		setKind(bundle.getEnum(KIND, Kind.class));
		depth = bundle.getInt( DEPTH );
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

}
