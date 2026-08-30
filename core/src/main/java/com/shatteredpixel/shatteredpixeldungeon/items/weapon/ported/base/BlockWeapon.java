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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.base;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

/** Base class for blocking weapons: grants flat DR via {@link #defenseFactor}. */
public class BlockWeapon extends MeleeWeapon {

	{
		image = ItemSpriteSheet.ROUND_SHIELD; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;

		tier = 1;
	}

	public int drBase = 3;
	public int drPerLevel = 1;

	@Override
	public int max(int lvl) {
		return Math.round(3f * (tier + 1)) +   //12 base, down from 20
				lvl * (tier - 1);               //+2 per level, down from +4
	}

	@Override
	public int defenseFactor(Char owner) {
		return DRMax();
	}

	public int DRMax() {
		return DRMax(buffedLvl());
	}

	//4 extra defence, plus 1 per level
	public int DRMax(int lvl) {
		return drBase + drPerLevel * lvl;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		RoundShield.guardAbility(hero, 5 + buffedLvl(), this);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown) {
			return Messages.get(this, "ability_desc", 5 + buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 5);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(5 + level);
	}

	//lunge: jump to a tile adjacent to the target and attack, used by katanas
	public static void lungeAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost, MeleeWeapon wep){
		if (target == null){
			return;
		}

		Char enemy = Actor.findChar(target);
		//duelist can lunge out of her FOV, but this wastes the ability instead of cancelling if there is no target
		if (Dungeon.level.heroFOV[target]) {
			if (enemy == null || enemy == hero || hero.isCharmedBy(enemy)) {
				GLog.w(Messages.get(wep, "ability_no_target"));
				return;
			}
		}

		if (hero.rooted || Dungeon.level.distance(hero.pos, target) < 2
				|| Dungeon.level.distance(hero.pos, target)-1 > wep.reachFactor(hero)){
			GLog.w(Messages.get(wep, "ability_target_range"));
			if (hero.rooted) PixelScene.shake( 1, 1f );
			return;
		}

		int lungeCell = -1;
		for (int i : PathFinder.NEIGHBOURS8){
			if (Dungeon.level.distance(hero.pos+i, target) <= wep.reachFactor(hero)
					&& Actor.findChar(hero.pos+i) == null
					&& (Dungeon.level.passable[hero.pos+i] || (Dungeon.level.avoid[hero.pos+i] && hero.flying))){
				if (lungeCell == -1 || Dungeon.level.trueDistance(hero.pos + i, target) < Dungeon.level.trueDistance(lungeCell, target)){
					lungeCell = hero.pos + i;
				}
			}
		}

		if (lungeCell == -1){
			GLog.w(Messages.get(wep, "ability_target_range"));
			return;
		}

		final int dest = lungeCell;

		hero.busy();
		Sample.INSTANCE.play(Assets.Sounds.MISS);
		hero.sprite.jump(hero.pos, dest, 0, 0.1f, () -> {
			if (Dungeon.level.map[hero.pos] == Terrain.OPEN_DOOR) {
				Door.leave( hero.pos );
			}
			hero.pos = dest;
			Dungeon.level.occupyCell(hero);
			Dungeon.observe();

			hero.belongings.abilityWeapon = wep; //set this early to we can check canAttack
			if (enemy != null && hero.canAttack(enemy)) {
				hero.sprite.attack(enemy.pos, () -> {

					wep.beforeAbilityUsed(hero, enemy);
					AttackIndicator.target(enemy);
					if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {
						Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
						if (!enemy.isAlive()) {
							MeleeWeapon.onAbilityKill(hero, enemy);
						}
					}
					Invisibility.dispel();
					hero.spendAndNext(hero.attackDelay());
					wep.afterAbilityUsed(hero);
				});
			} else {
				//spends charge but otherwise does not count as an ability use
				MeleeWeapon.Charger charger = Buff.affect(hero, MeleeWeapon.Charger.class);
				charger.partialCharge -= 1;
				while (charger.partialCharge < 0 && charger.charges > 0) {
					charger.charges--;
					charger.partialCharge++;
				}
				Item.updateQuickslot();
				GLog.w(Messages.get(wep, "ability_no_target"));
				hero.spendAndNext(1/hero.speed());
			}
		});
	}
}
