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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * A blast weapon: hits plant a visible delayed charge on the target which
 * detonates at the start of the next turn, damaging the 3x3 area.
 */
public class BlastWeapon extends MeleeWeapon {

	private float explosionDmgRatio = 0.5f;

	private static final String EXPLOSION_DMG_RATIO = "explosionDmgRatio";

	{
		image = ItemSpriteSheet.MACE; //TODO: dedicated icon
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.0f;

		tier = 1;
	}

	@Override
	public int max(int lvl) {
		return 5 * (tier) + lvl * (tier + 2);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);

		if (defender.isAlive() && Dungeon.level.insideMap(defender.pos)) {
			int boomDmg = Math.max(1, Math.round(damage * explosionDmgRatio));
			ChargeBlob blob = Blob.seed(defender.pos, 1, ChargeBlob.class);
			blob.setDamage(boomDmg);
			blob.setSource(this);
			GameScene.add(blob);
		}

		return damage;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc",
				Math.round(explosionDmgRatio * 100));
	}

	public BlastWeapon randomize() {
		tier = 1;
		level(Random.IntRange(0, 3));
		explosionDmgRatio = Random.Float(0.3f, 0.7f);
		return this;
	}

	@Override
	public Item random() {
		return randomize();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(EXPLOSION_DMG_RATIO, explosionDmgRatio);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(EXPLOSION_DMG_RATIO))
			explosionDmgRatio = bundle.getFloat(EXPLOSION_DMG_RATIO);
	}

	/**
	 * Visible delayed charge. Evolves once at VFX_PRIO (start of next turn),
	 * so it detonates roughly 0.5 turns after placement with visible particles in between.
	 */
	public static class ChargeBlob extends Blob {

		{
			actPriority = VFX_PRIO; //fire at start of next turn for ~0.5-turn delay
		}

		private int damage;
		private Object source;

		void setDamage(int damage) {
			this.damage = damage;
		}

		void setSource(Object source) {
			this.source = source;
		}

		@Override
		protected void evolve() {
			boolean exploded = false;

			for (int i = area.left - 1; i <= area.right; i++) {
				for (int j = area.top - 1; j <= area.bottom; j++) {
					int cell = i + j * Dungeon.level.width();
					if (cur[cell] > 0) {
						cur[cell] = 0;
						off[cell] = 0;
						volume = 0;

						CellEmitter.center(cell).burst(Speck.factory(Speck.LIGHT), 10);
						if (Dungeon.level.heroFOV[cell]) {
							Sample.INSTANCE.play(Assets.Sounds.BLAST);
						}

						for (int n : PathFinder.NEIGHBOURS9) {
							int target = cell + n;
							if (!Dungeon.level.insideMap(target)) continue;
							Char ch = Actor.findChar(target);
							if (ch != null && ch.isAlive()) {
								int dmg = damage;
								if (n != 0) dmg = Math.round(dmg * 0.67f);
								ch.damage(dmg, source instanceof Item ? source : this);
							}
						}
						exploded = true;
					}
				}
			}

			if (exploded) {
				Dungeon.observe();
			}
		}

		@Override
		public void use(BlobEmitter emitter) {
			super.use(emitter);
			emitter.pour(FlameParticle.FACTORY, 0.05f);
		}

		@Override
		public String tileDesc() {
			return Messages.get(this, "desc");
		}
	}
}
