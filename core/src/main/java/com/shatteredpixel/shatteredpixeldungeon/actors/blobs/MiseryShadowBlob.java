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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowStrikeBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowBlobParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

/**
 * MISERY feature: persistent shadow rims cast along the walls of a level.
 * Heroes standing in shadow always surprise-attack; MISERY can teleport
 * between shadow cells at an HP cost.
 */
public class MiseryShadowBlob extends Blob {

	{
		actPriority = HERO_PRIO;
	}

	/** One-time generation: create continuous shadow rims along all walls. */
	public static void generateForLevel() {
		if (Dungeon.level == null) return;
		MiseryShadowBlob blob = new MiseryShadowBlob();
		Dungeon.level.blobs.put(MiseryShadowBlob.class, blob);
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell]) continue;
			//this is a wall - shade all of its passable neighbors (creates a continuous rim)
			for (int n : PathFinder.NEIGHBOURS4) {
				int neighbor = cell + n;
				if (neighbor >= 0 && neighbor < Dungeon.level.length()
						&& Dungeon.level.passable[neighbor]) {
					//seed() uses +=, so cells touching multiple walls (corners, alcoves) get denser
					blob.seed(Dungeon.level, neighbor, Random.Int(3, 8));
				}
			}
		}
		GameScene.add(blob);
	}

	@Override
	protected void evolve() {
		int cell;
		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				off[cell] = cur[cell]; //map shadows persist
				volume += off[cell];
			}
		}
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(ShadowBlobParticle.FACTORY, 0.15f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	/** Check if a cell has any shadow (map shadow or cripple miasma). */
	public static boolean isShadowCell(int cell) {
		if (Dungeon.level == null) return false;
		MiseryShadowBlob blob = (MiseryShadowBlob) Dungeon.level.blobs.get(MiseryShadowBlob.class);
		if (blob != null && blob.cur != null && blob.cur[cell] > 0) return true;
		return CrippleBlob.isCrippleCell(cell);
	}

	/** Seed a shadow cell with the given intensity (for talent-created shadows). */
	public void addShadowCell(int cell, int amount) {
		seed(Dungeon.level, cell, amount);
	}

	/** Teleport hero to a shadow cell. Costs HP proportional to distance. */
	public void teleportTo(Hero hero, int cell) {
		if (!isShadowCell(cell)) return;

		boolean freeTeleport = CrippleBlob.isCrippleCell(cell);
		if (!freeTeleport) {
			int dist = Dungeon.level.distance(hero.pos, cell);
			float hpFraction = Math.min(0.5f, dist * 0.03f);
			if (hero.hasTalent(Talent.MISERY_SHADOW_TELEPORT)) {
				int pts = hero.pointsInTalent(Talent.MISERY_SHADOW_TELEPORT);
				hpFraction *= (1f - pts * 0.15f); //-15% cost per point
			}
			int hpCost = Math.max(1, Math.round(hero.HP * hpFraction));
			if (hero.HP < hpCost) return;
			hero.HP -= hpCost;
		}

		ScrollOfTeleportation.appear(hero, cell);
		Dungeon.observe();
		GameScene.updateFog();

		Buff.affect(hero, ShadowStrikeBuff.class, 5f);
		if (hero.hasTalent(Talent.MISERY_SHADOW_TELEPORT)
				&& hero.pointsInTalent(Talent.MISERY_SHADOW_TELEPORT) >= 3) {
			Buff.affect(hero, Invisibility.class, 3f);
		}
	}

	/** Stealth multiplier: MISERY heroes in shadow are much harder to detect. */
	public static float stealthMultiplier(Char ch) {
		if (!(ch instanceof Hero)) return 1f;
		if (isShadowCell(ch.pos)) return 0.5f;
		return 1f;
	}

	/** Find the nearest shadow cell. Returns -1 if none exist. */
	public int findNearestShadow(int fromCell) {
		int nearest = -1;
		int minDist = Integer.MAX_VALUE;
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (cell != fromCell && isShadowCell(cell)) {
				int dist = Dungeon.level.distance(fromCell, cell);
				if (dist < minDist) {
					minDist = dist;
					nearest = cell;
				}
			}
		}
		return nearest;
	}

	/** SOUL_REAP talent: ambush kills increase loot drops. */
	public static float soulReapDropChance(Hero hero, Mob mob) {
		if (hero.subClass == HeroSubClass.MISERY
				&& hero.hasTalent(Talent.MISERY_SOUL_REAP)
				&& mob.surprisedBy(hero)) {
			int pts = hero.pointsInTalent(Talent.MISERY_SOUL_REAP);
			return 1f + 0.15f * pts;
		}
		return 1f;
	}
}
