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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.MiseryShadowBlob;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.ArrayList;

/**
 * Debug tool: grants the MISERY subclass to the current hero on the spot -
 * the same flow as choosing a mastery (TengusMask.choose), plus generating
 * the level's shadow cells.
 */
public class DebugMisery extends Item implements DebugTool {

	private static final String AC_APPLY = "APPLY";

	{
		image = ItemSpriteSheet.ARTIFACT_CLOAK;
		defaultAction = AC_APPLY;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute(hero, action);
		if (AC_APPLY.equals(action) && Dungeon.level != null) {

			if (hero.subClass == HeroSubClass.MISERY){
				GLog.i(Messages.get(this, "already"));
				return;
			}

			hero.subClass = HeroSubClass.MISERY;
			hero.mergeSubclassTalents(HeroSubClass.MISERY);

			MiseryShadowBlob.generateForLevel();
			Dungeon.observe();
			GameScene.updateFog();

			GLog.i(Messages.get(this, "applied"));
		}
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
}
