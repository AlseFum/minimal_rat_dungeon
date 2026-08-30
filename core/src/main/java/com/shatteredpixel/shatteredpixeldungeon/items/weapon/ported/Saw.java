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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Saw extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SWORD; //TODO: replace with saw sprite
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;

		tier = 1;
		DLY = 0.9f;
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target) {
		return 1;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		Saw.sawAbility(hero, target, 1.2f, 0, this);
	}

	public static void sawAbility(Hero hero, Integer target, float dmgMulti, int dmgBonus, MeleeWeapon wep) {
		if (target == null) return;

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(wep, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = wep;
		if (!hero.canAttack(enemy)) {
			GLog.w(Messages.get(wep, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, () -> {
			wep.beforeAbilityUsed(hero, enemy);
			AttackIndicator.target(enemy);
			if (hero.attack(enemy, dmgMulti, dmgBonus, 1f)) {
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}
			Invisibility.dispel();
			wep.afterAbilityUsed(hero);
			hero.spendAndNext(hero.attackDelay());
		});
	}

	@Override
	public String abilityInfo() {
		return Messages.get(this, "ability_desc");
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(level + 3);
	}
}
