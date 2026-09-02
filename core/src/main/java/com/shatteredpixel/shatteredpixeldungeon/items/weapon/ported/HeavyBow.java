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

import com.shatteredpixel.shatteredpixeldungeon.sprites.SpriteRegistry;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * A short-range hunting crossbow. Shots deal +60% damage when the target
 * is exactly 1-2 tiles away in a cardinal direction.
 */
public class HeavyBow extends Weapon {

	static {
		SpriteRegistry.r("ported.province_bow", "sprites/ported/province_bow.png", 0, 0, 32, 32);
	}

	@Override
	public String spriteRegion() {
		return "ported.province_bow";
}

	public static final String AC_SHOOT = "SHOOT";

	/** consistent with {@link com.shatteredpixel.shatteredpixeldungeon.levels.Level#distance} (chebyshev) */
	public static final int MAX_SHOOT_DISTANCE = 3;

	protected static final float ORTHO_BONUS = 1.6f;

	protected int targetPos;

	{
		image = ItemSpriteSheet.SPIRIT_BOW; //TODO: dedicated icon
		defaultAction = AC_SHOOT;
		usesTargeting = true;

		DLY = 1f;
		RCH = 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(EquipableItem.AC_EQUIP);
		actions.add(AC_SHOOT);
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_SHOOT.equals(action)) {
			return Messages.get(this, "ac_shoot");
		}
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_SHOOT)) {
			curUser = hero;
			curItem = this;
			GameScene.selectCell(shooter);
		}
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockBolt().targetingPos(user, dst);
	}

	@Override
	public int STRReq(int lvl) {
		return STRReq(2, lvl);
	}

	@Override
	public int min(int lvl) {
		return 1 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 4 + 2 * lvl;
	}

	/** shot damage minimum (bolt), higher than the melee bash */
	public int shootMin(int lvl) {
		return 4 + 2 * lvl;
	}

	/** shot damage maximum (bolt) */
	public int shootMax(int lvl) {
		return 12 + 3 * lvl;
	}

	@Override
	public int damageRoll(Char owner) {
		//melee bash: no orthogonal bonus
		return augment.damageFactor(super.damageRoll(owner));
	}

	/** True when the two cells share a row or column and are 1-2 tiles apart. */
	public static boolean hasOrthogonalShortLineBonus(int from, int to) {
		int w = Dungeon.level.width();
		int fx = from % w, fy = from / w;
		int tx = to % w, ty = to / w;
		int dx = tx - fx, dy = ty - fy;
		if (dx != 0 && dy != 0) {
			return false;
		}
		int steps = Math.abs(dx) + Math.abs(dy);
		return steps == 1 || steps == 2;
	}

	public MissileWeapon knockBolt() {
		return new ProximityBolt();
	}

	@Override
	public String info() {
		String info = super.info();
		if (levelKnown) {
			info += "\n\n" + Messages.get(this, "stats_desc", shootMin(level()), shootMax(level()));
		}
		return info;
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target != null) {
				knockBolt().cast(curUser, target);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(HeavyBow.class, "prompt");
		}
	};

	public class ProximityBolt extends MissileWeapon {

		{
			image = ItemSpriteSheet.SPIRIT_ARROW;
			hitSound = Assets.Sounds.HIT_ARROW;
			spawnedForEffect = true;
			sticky = false;
		}

		@Override
		public int damageRoll(Char owner) {
			int dmg = augment.damageFactor(
					Random.NormalIntRange(
							shootMin(HeavyBow.this.buffedLvl()),
							shootMax(HeavyBow.this.buffedLvl())));
			if (owner != null && Dungeon.level != null
					&& hasOrthogonalShortLineBonus(owner.pos, HeavyBow.this.targetPos)) {
				dmg = Math.round(dmg * ORTHO_BONUS);
			}
			return dmg;
		}

		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return HeavyBow.this.hasEnchant(type, owner);
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return HeavyBow.this.proc(attacker, defender, damage);
		}

		@Override
		public float delayFactor(Char user) {
			return HeavyBow.this.delayFactor(user);
		}

		@Override
		public int STRReq(int lvl) {
			return HeavyBow.this.STRReq();
		}

		@Override
		protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) {
				parent = null;
				Splash.at(cell, 0xCC99FFFF, 1);
			} else {
				if (!curUser.shoot(enemy, this)) {
					Splash.at(cell, 0xCC99FFFF, 1);
				}
			}
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play(Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f));
		}

		@Override
		public void cast(Hero user, int dst) {
			if (Dungeon.level.distance(user.pos, dst) > MAX_SHOOT_DISTANCE) {
				GLog.w(Messages.get(HeavyBow.this, "out_of_range"));
				return;
			}
			HeavyBow.this.targetPos = throwPos(user, dst);
			super.cast(user, dst);
		}
	}
}
