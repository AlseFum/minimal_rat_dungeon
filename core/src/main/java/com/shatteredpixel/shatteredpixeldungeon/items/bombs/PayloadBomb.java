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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageInfo;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Proc.DamageWay;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** One implementation for bombs that differ only by their explosion payload. */
public class PayloadBomb extends Bomb {

	public enum Payload {
		FIRE(ItemSpriteSheet.FIRE_BOMB, "firebomb", 50),
		FROST(ItemSpriteSheet.FROST_BOMB, "frostbomb", 50),
		FLASH(ItemSpriteSheet.FLASHBANG, "flashbangbomb", 50),
		SMOKE(ItemSpriteSheet.SMOKE_BOMB, "smokebomb", 60),
		ARCANE(ItemSpriteSheet.ARCANE_BOMB, "arcanebomb", 50),
		SHRAPNEL(ItemSpriteSheet.SHRAPNEL_BOMB, "shrapnelbomb", 70);

		private final int image;
		private final String messageKey;
		private final int value;

		Payload(int image, String messageKey, int value) {
			this.image = image;
			this.messageKey = messageKey;
			this.value = value;
		}
	}

	private static final String PAYLOAD = "payload";

	private Payload payload;

	public PayloadBomb() {
		this(Payload.FIRE);
	}

	public PayloadBomb(Payload payload) {
		setPayload(payload);
	}

	public static PayloadBomb randomPayload() {
		return new PayloadBomb(Random.element(Payload.values()));
	}

	public Payload payload() {
		return payload;
	}

	private void setPayload(Payload payload) {
		this.payload = payload == null ? Payload.FIRE : payload;
		image = this.payload.image;
	}

	@Override
	public boolean isSimilar(Item item) {
		return item instanceof PayloadBomb
				&& payload == ((PayloadBomb) item).payload
				&& super.isSimilar(item);
	}

	@Override
	public String name() {
		return Messages.get("items.bombs." + payload.messageKey + ".name");
	}

	@Override
	public String desc() {
		int depth = Dungeon.hero == null ? 1 : Dungeon.scalingDepth();
		String result = Messages.get("items.bombs." + payload.messageKey + ".desc", 4 + depth, 12 + 3 * depth);
		return result + "\n\n" + Messages.get(Bomb.class, fuse == null ? "desc_fuse" : "desc_burning");
	}

	@Override
	public boolean explodesDestructively() {
		return payload != Payload.ARCANE && payload != Payload.SHRAPNEL;
	}

	@Override
	protected int explosionRange() {
		return payload == Payload.SHRAPNEL ? 8 : 2;
	}

	@Override
	protected Fuse createFuse() {
		return payload == Payload.ARCANE ? new ArcaneFuse() : super.createFuse();
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		switch (payload) {
			case FIRE:
				explodeFire(cell);
				break;
			case FROST:
				explodeFrost(cell);
				break;
			case FLASH:
				explodeFlash(cell);
				break;
			case SMOKE:
				explodeSmoke(cell);
				break;
			case ARCANE:
				explodeArcane(cell);
				break;
			case SHRAPNEL:
				explodeShrapnel(cell);
				break;
		}
	}

	private void explodeFire(int cell) {
		buildDistanceMap(cell, 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add(Blob.seed(i, Dungeon.level.pit[i] ? 2 : 10, Fire.class));
				CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}

	private void explodeFrost(int cell) {
		buildDistanceMap(cell, 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add(Blob.seed(i, 10, Freezing.class));
				Char ch = Actor.findChar(i);
				if (ch != null) Buff.affect(ch, Frost.class, 2f);
			}
		}
	}

	private void explodeFlash(int cell) {
		ArrayList<Char> affected = affectedChars(cell, 2);
		ArrayList<Lightning.Arc> arcs = new ArrayList<>();
		for (Char ch : affected) {
			int damage = Math.round(Random.NormalIntRange(4 + Dungeon.scalingDepth(),
					12 + 3 * Dungeon.scalingDepth()) / 4f);
			Proc.damage(new DamageInfo().defender(ch).way(DamageWay.ENV).type(DamageType.ELECTRIC).solidDamage(damage));
			if (ch.isAlive()) Buff.prolong(ch, Paralysis.class, Paralysis.DURATION);
			arcs.add(new Lightning.Arc(DungeonTilemap.tileCenterToWorld(cell), ch.sprite.center()));
			if (ch == Dungeon.hero) GameScene.flash(0x80FFFFFF);
			if (ch == Dungeon.hero && !ch.isAlive()) {
				Badges.validateDeathFromFriendlyMagic();
				GLog.n(Messages.get(Bomb.class, "ondeath"));
				Dungeon.fail(this);
			}
		}
		CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
		Dungeon.hero.sprite.parent.addToFront(new Lightning(arcs, null));
		Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
	}

	private void explodeSmoke(int cell) {
		int centerVolume = 1000;
		buildDistanceMap(cell, 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add(Blob.seed(i, 40, SmokeScreen.class));
				centerVolume -= 40;
			}
		}
		if (centerVolume > 0) GameScene.add(Blob.seed(cell, centerVolume, SmokeScreen.class));
	}

	private void explodeArcane(int cell) {
		for (Char ch : affectedChars(cell, 2)) {
			int damage = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth());
			Proc.damage(new DamageInfo().defender(ch).way(DamageWay.ENV).type(DamageType.PHYSICAL).solidDamage(damage));
			if (ch == Dungeon.hero && !ch.isAlive()) {
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(this);
			}
		}
		buildDistanceMap(cell, 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);
		}
	}

	private void explodeShrapnel(int cell) {
		boolean[] fov = new boolean[Dungeon.level.length()];
		Point center = Dungeon.level.cellToPoint(cell);
		ShadowCaster.castShadow(center.x, center.y, Dungeon.level.width(), fov,
				Dungeon.level.losBlocking, explosionRange());
		for (int i = 0; i < fov.length; i++) {
			if (!fov[i]) continue;
			if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
				CellEmitter.center(i).burst(BlastParticle.FACTORY, 5);
			}
			Char ch = Actor.findChar(i);
			if (ch != null) {
				int damage = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth());
				Proc.damage(new DamageInfo().defender(ch).way(DamageWay.ENV).type(DamageType.PHYSICAL).solidDamage(damage - ch.drRoll()));
				if (ch == Dungeon.hero && !ch.isAlive()) Dungeon.fail(this);
			}
		}
	}

	private static ArrayList<Char> affectedChars(int cell, int range) {
		ArrayList<Char> affected = new ArrayList<>();
		buildDistanceMap(cell, range);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char ch = Actor.findChar(i);
				if (ch != null) affected.add(ch);
			}
		}
		return affected;
	}

	private static void buildDistanceMap(int cell, int range) {
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), range);
	}

	@Override
	public int value() {
		return quantity * payload.value;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PAYLOAD, payload);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		setPayload(bundle.getEnum(PAYLOAD, Payload.class));
	}

	public static class ArcaneDamage {
	}

	public static class ArcaneFuse extends Fuse {

		private final ArrayList<Emitter> warningEmitters = new ArrayList<>();

		@Override
		public Fuse ignite(Bomb bomb) {
			super.ignite(bomb);
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					int bombPos = -1;
					for (Heap heap : Dungeon.level.heaps.valueList()) {
						if (heap.items.contains(bomb)) bombPos = heap.pos;
					}
					if (bombPos != -1) {
						buildDistanceMap(bombPos, bomb.explosionRange());
						for (int i = 0; i < PathFinder.distance.length; i++) {
							if (PathFinder.distance[i] < Integer.MAX_VALUE) {
								Emitter emitter = CellEmitter.get(i);
								if (emitter != null) {
									emitter.pour(GooSprite.GooParticle.FACTORY, 0.03f);
									warningEmitters.add(emitter);
								}
							}
						}
					}
					Actor.remove(this);
					return true;
				}
			});
			return this;
		}

		@Override
		public void snuff() {
			super.snuff();
			for (Emitter emitter : warningEmitters) emitter.on = false;
			warningEmitters.clear();
		}
	}
}
