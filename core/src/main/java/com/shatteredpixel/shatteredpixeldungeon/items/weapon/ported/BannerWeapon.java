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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * A banner weapon: COMMAND grants an ally a barrier, or makes a ranged
 * attack on an enemy with bonus damage. Randomizes reach/barrier/damage.
 */
public class BannerWeapon extends MeleeWeapon {

	public static final String AC_COMMAND = "COMMAND";

	private int reach      = 2;
	private int barrierAmt = 10;
	private float dmgBonus = 0.3f;

	private static final String REACH       = "reach";
	private static final String BARRIER_AMT = "barrierAmt";
	private static final String DMG_BONUS   = "dmgBonus";

	{
		image = ItemSpriteSheet.RUNIC_BLADE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.0f;
		tier = 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COMMAND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_COMMAND)) {
			curUser = hero;
			curItem = this;
			GameScene.selectCell(commander);
		}
	}

	@Override
	public int max(int lvl) {
		return 5 * (tier + 2) + lvl * (tier + 1);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", reach, barrierAmt, Math.round(dmgBonus * 100));
	}

	public BannerWeapon randomize() {
		tier = 1;
		level(Random.IntRange(0, 3));
		reach = Random.IntRange(1, 3);
		barrierAmt = Random.IntRange(6, 18);
		dmgBonus = Random.Float(0.15f, 0.45f);
		return this;
	}

	@Override
	public Item random() {
		return randomize();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(REACH, reach);
		bundle.put(BARRIER_AMT, barrierAmt);
		bundle.put(DMG_BONUS, dmgBonus);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(REACH))       reach       = bundle.getInt(REACH);
		if (bundle.contains(BARRIER_AMT)) barrierAmt = bundle.getInt(BARRIER_AMT);
		if (bundle.contains(DMG_BONUS))   dmgBonus    = bundle.getFloat(DMG_BONUS);
	}

	private CellSelector.Listener commander = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) return;

			int dist = Dungeon.level.distance(curUser.pos, target);
			if (dist < 1 || dist > reach || !Dungeon.level.heroFOV[target]) {
				curUser.spendAndNext(curUser.attackDelay());
				return;
			}

			curUser.sprite.zap(target);
			curUser.busy();

			Char ch = Actor.findChar(target);

			if (ch != null && ch.alignment == Char.Alignment.ALLY && ch != curUser) {
				//ally → grant barrier
				Buff.affect(ch, Barrier.class).setShield(barrierAmt);
				ch.sprite.showStatusWithIcon(CharSprite.POSITIVE,
						Integer.toString(barrierAmt),
						FloatingText.SHIELDING);
				CellEmitter.center(target).burst(Speck.factory(Speck.UP), 8);
				Sample.INSTANCE.play(Assets.Sounds.EVOKE);

			} else if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
				//enemy → ranged attack with bonus
				int dmg = damageRoll(curUser);
				dmg = Math.round(dmg * (1f + dmgBonus));
				dmg = curUser.attackProc(ch, dmg);
				ch.damage(dmg, BannerWeapon.this);
				Sample.INSTANCE.play(hitSound, 1, hitSoundPitch);

			} else {
				//empty tile → visual only
				CellEmitter.center(target).burst(Speck.factory(Speck.UP), 4);
				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			}

			curUser.spendAndNext(curUser.attackDelay());
		}

		@Override
		public String prompt() {
			return Messages.get(BannerWeapon.class, "prompt");
		}
	};
}
