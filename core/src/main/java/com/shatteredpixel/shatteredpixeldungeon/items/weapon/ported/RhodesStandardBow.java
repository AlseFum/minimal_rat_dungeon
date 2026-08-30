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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RhodesStandardBow extends Weapon {

	public static final String AC_SHOOT  = "SHOOT";
	public static final String AC_RECALL = "RECALL";

	static final int MAX_ARROWS = 6;
	int arrows = MAX_ARROWS;

	{
		image = ItemSpriteSheet.SPIRIT_BOW;

		defaultAction = AC_SHOOT;
		usesTargeting = true;

		unique = true;
		bones = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		if (hero.buff(RhodesArrowStuck.class) != null) {
			actions.add(AC_RECALL);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_SHOOT)) {
			if (arrows <= 0 && hero.buff(RhodesArrowStuck.class) == null) {
				GLog.w(Messages.get(this, "no_arrows"));
				return;
			}
			curUser = hero;
			curItem = this;
			GameScene.selectCell(shooter);
		} else if (action.equals(AC_RECALL)) {
			recallArrows(hero);
		}
	}

	private void recallArrows(Hero hero) {
		RhodesArrowStuck stuck = hero.buff(RhodesArrowStuck.class);
		int recovered = stuck != null ? stuck.count() : 0;
		if (recovered > 0) {
			arrows = Math.min(MAX_ARROWS, arrows + recovered);
			updateQuickslot();
		}
		// Detach stuck buff first so pierced.detach() won't double-return
		if (stuck != null) {
			stuck.detach();
		}
		// Clean up pierced markers on current-depth enemies (no-ops since stuck is gone)
		for (Char ch : Actor.chars()) {
			RhodesArrowPierced pierced = ch.buff(RhodesArrowPierced.class);
			if (pierced != null) {
				pierced.detach();
			}
		}
		if (recovered > 0) {
			GLog.p(Messages.get(this, "recall", recovered));
		}
	}

	@Override
	public int min(int lvl) {
		return 3;
	}

	@Override
	public int max(int lvl) {
		return 8;
	}

	@Override
	public int STRReq(int lvl) {
		return STRReq(2, lvl);
	}

	@Override
	public String info() {
		String info = super.info();
		info += "\n\n" + Messages.get(this, "stats", min(), max(), STRReq());
		info += "\n\n" + Messages.get(this, "arrows", arrows, MAX_ARROWS);
		return info;
	}

	@Override
	public String status() {
		return arrows + "/" + MAX_ARROWS;
	}

	private static final String ARROWS = "arrows";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ARROWS, arrows);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arrows = bundle.getInt(ARROWS);
	}

	public RhodesArrow knockArrow() {
		return new RhodesArrow();
	}

	public class RhodesArrow extends MissileWeapon {

		{
			image = ItemSpriteSheet.SPIRIT_ARROW;
			hitSound = Assets.Sounds.HIT_ARROW;
			tier = 1;
		}

		@Override
		public Emitter emitter() {
			Emitter e = new Emitter();
			e.pos(5, 5);
			e.fillTarget = false;
			e.pour(Speck.factory(Speck.LIGHT), 0.08f);
			return e;
		}

		@Override
		public int min(int lvl) {
			return 1 + lvl;
		}

		@Override
		public int max(int lvl) {
			return 3 + 2 * lvl;
		}

		@Override
		public int STRReq(int lvl) {
			return RhodesStandardBow.this.STRReq();
		}

		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return RhodesStandardBow.this.hasEnchant(type, owner);
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return RhodesStandardBow.this.proc(attacker, defender, damage);
		}

		@Override
		protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) {
				parent = null;
				CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 3);
				return;
			}
			if (!curUser.shoot(enemy, this)) {
				CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 3);
				return;
			}
			// Only consume arrow on successful hit
			RhodesStandardBow.this.arrows--;
			updateQuickslot();
			// Arrow sticks into enemy
			RhodesArrowStuck stuck = Buff.affect(curUser, RhodesArrowStuck.class);
			stuck.incStuck();
			// Mark enemy for auto-recovery on death
			RhodesArrowPierced pierced = Buff.affect(enemy, RhodesArrowPierced.class);
			pierced.addArrow();
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play(Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f));
		}

		@Override
		public void cast(final Hero user, final int dst) {
			if (arrows <= 0) {
				GLog.w(Messages.get(RhodesStandardBow.class, "no_arrows"));
				return;
			}
			super.cast(user, dst);
		}
	}

	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target != null) {
				knockArrow().cast(curUser, target);
			}
		}

		@Override
		public String prompt() {
			return Messages.get(RhodesStandardBow.class, "prompt");
		}
	};

	/**
	 * Tracks arrows stuck in enemies. Each stack = one arrow that can be recalled.
	 */
	public static class RhodesArrowStuck extends FlavourBuff {

		{
			type = buffType.NEGATIVE;
			revivePersists = true;
		}

		private int stuckCount = 0;

		public void incStuck() {
			stuckCount++;
			BuffIndicator.refreshHero();
		}

		public void decCount(int n) {
			stuckCount = Math.max(0, stuckCount - n);
			if (stuckCount <= 0) {
				detach();
			} else {
				BuffIndicator.refreshHero();
			}
		}

		public int count() {
			return stuckCount;
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.6f, 0.4f, 0.2f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(stuckCount);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", stuckCount);
		}

		private static final String STUCK_COUNT = "stuck_count";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(STUCK_COUNT, stuckCount);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			stuckCount = bundle.getInt(STUCK_COUNT);
		}
	}

	/**
	 * Marker buff placed on enemies that have arrows stuck in them.
	 * Periodically checks if the enemy is still alive; if not, arrows are
	 * automatically returned to the hero's quiver.
	 */
	public static class RhodesArrowPierced extends Buff {

		private int piercedCount = 0;

		public void addArrow() {
			piercedCount++;
		}

		public int count() {
			return piercedCount;
		}

		@Override
		public boolean act() {
			if (target == null || !target.isAlive()) {
				returnArrows();
				detach();
				return true;
			}
			spend(TICK);
			return true;
		}

		private void returnArrows() {
			if (piercedCount <= 0) return;
			Hero hero = Dungeon.hero;
			if (hero == null || !hero.isAlive()) return;
			RhodesStandardBow bow = hero.belongings.getItem(RhodesStandardBow.class);
			if (bow == null) return;
			int toReturn = Math.min(piercedCount, MAX_ARROWS - bow.arrows);
			if (toReturn > 0) {
				bow.arrows = Math.min(MAX_ARROWS, bow.arrows + toReturn);
				Item.updateQuickslot();
			}
			// Also clean up stuck counter if it still exists
			RhodesArrowStuck stuck = hero.buff(RhodesArrowStuck.class);
			if (stuck != null) {
				stuck.decCount(toReturn);
			}
		}

		private static final String PIERCED_COUNT = "pierced_count";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(PIERCED_COUNT, piercedCount);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			piercedCount = bundle.getInt(PIERCED_COUNT);
		}
	}
}
