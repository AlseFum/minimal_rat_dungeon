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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/** A blood weapon: deals more damage the lower the hero's HP, and heals from its damage. */
public class BloodWeapon extends MeleeWeapon {

	private float lifestealRatio  = 0.20f;  // fraction of damage healed
	private float lowHpMultiplier = 3.0f;   // max multiplier when HP ≈ 0
	private float baseMultiplier  = 0.4f;   // multiplier at full HP

	private static final String LIFESTEAL_RATIO   = "lifestealRatio";
	private static final String LOW_HP_MULTIPLIER = "lowHpMultiplier";
	private static final String BASE_MULTIPLIER   = "baseMultiplier";

	{
		image = ItemSpriteSheet.SWORD; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.0f;

		tier = 1;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);

		if (attacker instanceof Hero && damage > 0) {
			Hero hero = (Hero) attacker;
			float hpRatio = (float) hero.HP / hero.HT;
			float mult = baseMultiplier + (lowHpMultiplier - baseMultiplier) * (1f - hpRatio);
			damage = Math.round(damage * mult);

			int heal = Math.max(1, Math.round(damage * lifestealRatio));
			hero.HP = Math.min(hero.HT, hero.HP + heal);
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
			CellEmitter.center(defender.pos).burst(Speck.factory(Speck.HEALING), Math.min(10, 2 + heal / 3));
		}

		return damage;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc",
				Math.round(baseMultiplier * 100),
				Math.round(lowHpMultiplier * 100),
				Math.round(lifestealRatio * 100));
	}

	public BloodWeapon randomize() {
		tier = 1;
		level(Random.IntRange(0, 3));
		lifestealRatio = Random.Float(0.12f, 0.28f);
		lowHpMultiplier = Random.Float(2.5f, 4.0f);
		baseMultiplier = Random.Float(0.2f, 0.6f);
		return this;
	}

	@Override
	public Item random() {
		return randomize();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LIFESTEAL_RATIO, lifestealRatio);
		bundle.put(LOW_HP_MULTIPLIER, lowHpMultiplier);
		bundle.put(BASE_MULTIPLIER, baseMultiplier);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(LIFESTEAL_RATIO))   lifestealRatio   = bundle.getFloat(LIFESTEAL_RATIO);
		if (bundle.contains(LOW_HP_MULTIPLIER)) lowHpMultiplier = bundle.getFloat(LOW_HP_MULTIPLIER);
		if (bundle.contains(BASE_MULTIPLIER))   baseMultiplier   = bundle.getFloat(BASE_MULTIPLIER);
	}
}
