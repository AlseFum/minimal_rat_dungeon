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
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission;
import com.shatteredpixel.shatteredpixeldungeon.actors.ActionSubmission.ActionResult;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.HeavyBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

/**
 * 机弩：{@link HeavyBow 重弩}的可部署变体。
 * 可将弩台部署到视野内任意空格；朝向在部署时由「英雄→落点」位移的主轴取正四向（投掷大方向）。
 * 可在相邻格交互再次「调整朝向」。每回合沿朝向射击最多 3 格，带 zap + 弹道动画。
 */
public class DeployablewCrossBow extends HeavyBow {

	public static final String AC_DEPLOY = "DEPLOY";

	//note: ZootDungeon registered a dedicated texture here (TextureRegistry); minimal has no TextureRegistry
	{
		image = ItemSpriteSheet.CROSSBOW; //TODO: dedicated icon
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		DLY = 1f;
		RCH = 1;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_SHOOT);             // 父类无条件添加了，子类只在装备时展示
		if (isEquipped(hero)) {
			actions.add(AC_SHOOT);
			actions.add(AC_DEPLOY);
		} else {
			actions.add(EquipableItem.AC_EQUIP); // 父类移除了 EQUIP，子类需要恢复
		}
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_SHOOT.equals(action)) {
			return Messages.get(this, "ac_shoot");
		}
		if (AC_DEPLOY.equals(action)) {
			return Messages.get(this, "ac_deploy");
		}
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			curItem = this;
			GameScene.selectCell(shooter);
		} else if (AC_DEPLOY.equals(action)) {
			curUser = hero;
			curItem = this;
			GameScene.selectCell(deployer);
		}
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockBolt().targetingPos(user, dst);
	}

	@Override
	public LineBolt knockBolt() {
		return new LineBolt();
	}

	LineBolt lineBolt() {
		return new LineBolt();
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	/**
	 * 投掷大方向：从 {@code from} 指向 {@code to} 的位移取主轴，映射为单层正四向一步（水平位移更大则东/西，否则南/北）。
	 */
	private static int snapDeployFacing(int from, int to) {
		int w = Dungeon.level.width();
		int fx = from % w, fy = from / w;
		int tx = to % w, ty = to / w;
		int dx = tx - fx, dy = ty - fy;
		if (dx == 0 && dy == 0) {
			return 0;
		}
		if (Math.abs(dx) >= Math.abs(dy)) {
			if (dx > 0) {
				return 1;
			}
			if (dx < 0) {
				return -1;
			}
			return dy > 0 ? w : -w;
		}
		if (dy > 0) {
			return w;
		}
		return -w;
	}

	private static int cardinalStep(int from, int to) {
		int w = Dungeon.level.width();
		int fx = from % w, fy = from / w;
		int tx = to % w, ty = to / w;
		int dx = tx - fx, dy = ty - fy;
		if (dx != 0 && dy != 0) {
			return 0;
		}
		if (dx == 0 && dy == 0) {
			return 0;
		}
		if (dx > 0) {
			return 1;
		}
		if (dx < 0) {
			return -1;
		}
		if (dy > 0) {
			return w;
		}
		return -w;
	}

	private static boolean isBoltHostile(Char ch, Hero owner) {
		return ch != null && ch != owner && ch.isAlive()
				&& ch.alignment == Char.Alignment.ENEMY;
		//note: ZootDungeon also treated Mimic (alignment NEUTRAL) as hostile;
		//actors.mobs.Mimic is not ported to minimal, so that branch is dropped.
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) {
				return;
			}
			Char ch = Actor.findChar(target);
			if (ch == null || !ch.isAlive() || ch == curUser) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "bad_target"));
				return;
			}
			if (Dungeon.level.distance(curUser.pos, target) > MAX_SHOOT_DISTANCE) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "out_of_range"));
				return;
			}
			knockBolt().cast(curUser, target);
		}

		@Override
		public String prompt() {
			return Messages.get(DeployablewCrossBow.class, "prompt_shoot");
		}
	};

	private final CellSelector.Listener deployer = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) {
				return;
			}
			if (!Dungeon.level.heroFOV[target]) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "not_visible"));
				return;
			}
			if (!Dungeon.level.passable[target] && !Dungeon.level.avoid[target]) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "bad_deploy"));
				return;
			}
			if (Actor.findChar(target) != null) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "blocked_spawn"));
				return;
			}

			int facing = snapDeployFacing(curUser.pos, target);
			if (facing == 0) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "bad_deploy"));
				return;
			}

			DeployablewCrossBow blade = DeployablewCrossBow.this;
			Hero hero = curUser;

			DeployedTurretBuff oldB = hero.buff(DeployedTurretBuff.class);
			if (oldB != null && oldB.turret != null && oldB.turret.isAlive()) {
				oldB.turret.dismissTurret();
			}

			boolean equipped = blade.isEquipped(hero);
			if (equipped) {
				if (!blade.doUnequip(hero, true, true)) {
					return;
				}
			}

			Item detached = blade.detach(hero.belongings.backpack);
			if (detached == null) {
				GLog.w(Messages.get(DeployablewCrossBow.this, "deploy_failed"));
				return;
			}

			if (!equipped) {
				hero.spendAndNext(1f);
			}

			Avatar mob = new Avatar(hero, (DeployablewCrossBow) detached, target, facing);
			mob.pos = target;
			GameScene.add(mob);
			//note: ZootDungeon used ItemEffects.appear(...); minimal equivalent is ScrollOfTeleportation.appear(...)
			ScrollOfTeleportation.appear(mob, target);
			Dungeon.level.occupyCell(mob);

			DeployedTurretBuff nb = Buff.affect(hero, DeployedTurretBuff.class);
			nb.turret = mob;
		}

		@Override
		public String prompt() {
			return Messages.get(DeployablewCrossBow.class, "prompt_deploy");
		}
	};

	public class LineBolt extends MissileWeapon {

		{
			image = ItemSpriteSheet.SPIRIT_ARROW; //TODO: dedicated icon (THROWING_SPIKE is not in minimal ItemSpriteSheet)
			hitSound = Assets.Sounds.HIT_STAB;
			spawnedForEffect = true;
			sticky = false;
		}

		@Override
		public int damageRoll(Char owner) {
			int dmg = augment.damageFactor(
					Random.NormalIntRange(
							shootMin(DeployablewCrossBow.this.buffedLvl()),
							shootMax(DeployablewCrossBow.this.buffedLvl())));
			if (owner != null && Dungeon.level != null
					&& hasOrthogonalShortLineBonus(owner.pos, DeployablewCrossBow.this.targetPos)) {
				dmg = Math.round(dmg * ORTHO_BONUS);
			}
			return dmg;
		}

		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return DeployablewCrossBow.this.hasEnchant(type, owner);
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return DeployablewCrossBow.this.proc(attacker, defender, damage);
		}

		@Override
		public float delayFactor(Char user) {
			return DeployablewCrossBow.this.delayFactor(user);
		}

		@Override
		public int STRReq(int lvl) {
			return DeployablewCrossBow.this.STRReq();
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
			Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW, 1, Random.Float(0.87f, 1.15f));
		}

		@Override
		public void cast(Hero user, int dst) {
			DeployablewCrossBow.this.targetPos = throwPos(user, dst);
			super.cast(user, dst);
		}
	}

	/** 标记当前英雄有一座本武器的部署弩台（用于读档后由 Avatar 重新关联）。 */
	public static class DeployedTurretBuff extends Buff {

		public Avatar turret;

		{
			type = buffType.NEUTRAL;
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}
	}

	public static class Avatar extends Mob {

		private static final String TAG_BLADE = "dlb_blade";
		private static final String TAG_OWNER = "dlb_owner";
		private static final String TAG_STEP = "dlb_step";

		private DeployablewCrossBow blade;
		private Hero owner;
		private int ownerId = -1;
		private int stepDelta;

		private final CellSelector.Listener aimListener = new CellSelector.Listener() {
			@Override
			public void onSelect(Integer target) {
				if (target == null) {
					return;
				}
				int st = cardinalStep(Avatar.this.pos, target);
				if (st == 0) {
					GLog.w(Messages.get(DeployablewCrossBow.class, "bad_aim"));
					return;
				}
				Avatar.this.stepDelta = st;
				GLog.p(Messages.get(DeployablewCrossBow.class, "aim_set"));
				if (Avatar.this.owner != null) {
					Avatar.this.owner.spendAndNext(1f);
				}
			}

			@Override
			public String prompt() {
				return Messages.get(DeployablewCrossBow.class, "prompt_aim");
			}
		};
		//note: ZootDungeon highlighted the 4 candidate facing cells with CellSelector.Select +
		//GameScene.selectCellWithView(...); minimal has neither, so aiming falls back to a
		//plain cell selection (no range highlight).

		public Avatar() {
			super();
		}

		public Avatar(Hero owner, DeployablewCrossBow blade, int pos, int stepDelta) {
			this.owner = owner;
			this.blade = blade;
			this.pos = pos;
			this.stepDelta = stepDelta;
		}

		{
			spriteClass = AvatarSprite.class;
			alignment = Alignment.ALLY;
			state = PASSIVE;
			//note: ZootDungeon also set EXP = 0 and maxLvl = -1 here; minimal Mob has no such fields
			HT = HP = 15;
			defenseSkill = 4;
			properties.add(Property.IMMOVABLE);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TAG_BLADE, blade);
			bundle.put(TAG_STEP, stepDelta);
			if (owner != null) {
				bundle.put(TAG_OWNER, owner.id());
			} else {
				bundle.put(TAG_OWNER, -1);
			}
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			blade = (DeployablewCrossBow) bundle.get(TAG_BLADE);
			stepDelta = bundle.getInt(TAG_STEP);
			ownerId = bundle.getInt(TAG_OWNER);
		}

		@Override
		public void restoreEnemy() {
			super.restoreEnemy();
			if (owner == null && ownerId != -1) {
				owner = (Hero) Actor.findById(ownerId);
			}
			if (owner != null) {
				DeployedTurretBuff b = owner.buff(DeployedTurretBuff.class);
				if (b == null) {
					b = Buff.affect(owner, DeployedTurretBuff.class);
				}
				b.turret = this;
			}
		}

		@Override
		public boolean canInteract(Char c) {
			return c == owner && owner != null && owner.isAlive()
					&& Dungeon.level.adjacent(pos, c.pos);
		}

		@Override
		public boolean interact(Char c) {
			if (c != owner || owner == null || !owner.isAlive()) {
				return true;
			}
			final Hero who = owner;
			Game.runOnRenderThread(() -> {
				GameScene.show(new WndOptions(sprite(),
						Messages.get(DeployablewCrossBow.class, "turret_menu_title"),
						Messages.get(DeployablewCrossBow.class, "turret_menu_body"),
						Messages.get(DeployablewCrossBow.class, "opt_pickup"),
						Messages.get(DeployablewCrossBow.class, "opt_aim")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							GLog.i(Messages.get(DeployablewCrossBow.class, "recalled"));
							dismissTurret();
							who.spendAndNext(1f);
						} else if (index == 1) {
							blade.setCurrent(who);
							GameScene.selectCell(Avatar.this.aimListener);
						}
					}
				});
			});
			return true;
		}

		@Override
		public void die(Object cause) {
			returnBladeToOwner();
			super.die(cause);
		}

		void dismissTurret() {
			die(null);
		}

		private void returnBladeToOwner() {
			Hero h = owner;
			if (h != null) {
				DeployedTurretBuff tb = h.buff(DeployedTurretBuff.class);
				if (tb != null && tb.turret == this) {
					tb.turret = null;
					tb.detach();
				}
			}
			DeployablewCrossBow b = blade;
			int dropPos = pos;
			blade = null;
			owner = null;
			if (b != null) {
				if (h != null && h.isAlive()) {
					if (!b.collect(h.belongings.backpack)) {
						Dungeon.level.drop(b, h.pos).sprite.drop();
					}
				} else {
					Dungeon.level.drop(b, dropPos).sprite.drop();
				}
			}
		}

		@Override
		protected ActionSubmission proposeAction() {
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
				fieldOfView = new boolean[Dungeon.level.length()];
			}
			Dungeon.level.updateFieldOfView(this, fieldOfView);

			if (blade == null || owner == null || !owner.isAlive()) {
				returnBladeToOwner();
				if (isAlive()) {
					die(null);
				} else {
					spend(TICK);
				}
				return ActionSubmission.idle();
			}

			spend(TICK);

			if (stepDelta == 0) {
				return ActionSubmission.idle();
			}
			Char shootTarget = null;
			for (int k = 1; k <= MAX_SHOOT_DISTANCE; k++) {
				int c = pos + k * stepDelta;
				if (!Dungeon.level.insideMap(c)) {
					break;
				}
				if (Dungeon.level.solid[c]) {
					break;
				}
				Char occ = Actor.findChar(c);
				if (occ != null) {
					if (isBoltHostile(occ, owner)) {
						shootTarget = occ;
					}
					break;
				}
			}
			if (shootTarget != null) {
				return new ActionSubmission( ActionSubmission.FIRE, shootTarget );
			}

			return ActionSubmission.idle();
		}

		@Override
		protected boolean doAction( ActionSubmission sub, ActionResult result ) {
			if (sub.name == ActionSubmission.FIRE && result.isOk()) {
				final Char tgt = (Char)sub.param;
				if (sprite != null) {
					final LineBolt bolt = blade.lineBolt();
					Sample.INSTANCE.play(Assets.Sounds.ATK_CROSSBOW, 1, Random.Float(0.87f, 1.15f));
					sprite.zap(tgt.pos, () -> {
						sprite.idle();
						if (sprite.parent == null) {
							blade.targetPos = tgt.pos;
							owner.shoot(tgt, bolt);
							return;
						}
						Callback onHit = () -> {
							blade.targetPos = tgt.pos;
							owner.shoot(tgt, bolt);
						};
						MissileSprite ms = (MissileSprite) sprite.parent.recycle(MissileSprite.class);
						if (tgt.sprite != null) {
							ms.reset(sprite, tgt.sprite, bolt, onHit);
						} else {
							ms.reset(sprite, tgt.pos, bolt, onHit);
						}
					});
				}
				return true;
			}
			return super.doAction( sub, result );
		}
	}

	public static class AvatarSprite extends MobSprite {

		public AvatarSprite() {
			super();
			texture(Assets.Sprites.ITEMS);
			TextureFilm frames = ItemSpriteSheet.film;
			idle = new Animation(0, true);
			idle.frames(frames, ItemSpriteSheet.CROSSBOW);
			run = idle;
			attack = new Animation(10, false);
			attack.frames(frames, ItemSpriteSheet.CROSSBOW);
			zap = new Animation(8, false);
			zap.frames(frames, ItemSpriteSheet.CROSSBOW);
			die = new Animation(8, false);
			die.frames(frames, ItemSpriteSheet.CROSSBOW);
			play(idle);
		}
	}
}

