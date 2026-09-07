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
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

/**
 * 回旋武器：向选定方向沿直线飞出，穿透单位与可通行地形，在撞墙处短暂停留后再实时追踪飞回英雄当前格；
 * 去程与回程各对路径上的敌人结算一次物理伤害（无需装备在手，伤害按本武器自身骰伤结算，见 {@link #hitAlongPath}）。
 * <p>
 * 远端等待通过挂在英雄身上的 {@link ChakramReturn} 消耗 {@link Actor#TICK} 推进时间（与重回旋镖 CircleBack 同理），
 * 避免 {@code Actor.addDelayed} 在英雄尚未 {@code spend} 时卡住全局 Actor 调度。
 *
 * <p>由 ZootDungeon 项目的 items.weapon.Chakram 移植而来；
 * 原基类 base.Weapon 对应 minimal 的 items.weapon.Weapon。
 */
public class Chakram extends Weapon {

	/** 抵达远端后，再经过多少个全局时间单位（每次 {@link Actor#TICK}）开始折返 */
	private static final int HOVER_TICKS = 2;

	private transient boolean throwing = false;

	{
		image = ItemSpriteSheet.THROWING_KNIFE;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.2f;

		defaultAction = Item.AC_THROW;
		usesTargeting = true;

		DLY = 1f;
		RCH = 1;
		ACC = 1f;
	}

	@Override
	public void cast(final Hero user, final int dst) {
		if (throwing) {
			return;
		}
		//无需装备在手：收在背包/快捷栏中同样可掷，伤害按本武器自身骰伤结算（见 hitAlongPath 的 thrownWeapon 挂载）
		if (dst == user.pos) {
			return;
		}

		final int heroPos = user.pos;
		final Ballistica outbound = new Ballistica(heroPos, dst, Ballistica.STOP_SOLID);
		final int endCell = outbound.collisionPos;
		if (outbound.dist <= 0 || endCell == heroPos) {
			GLog.w("无法朝这个方向掷出。");
			return;
		}

		throwing = true;
		user.sprite.zap(endCell);
		user.busy();
		throwSound();

		((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).reset(
				user.sprite,
				endCell,
				this,
				() -> {
					hitAlongPath(user, heroPos, outbound);
					Sample.INSTANCE.play(hitSound, 1f, hitSoundPitch);

					final ItemSprite hover = new ItemSprite(Chakram.this);
					hover.place(endCell);
					user.sprite.parent.add(hover);

					// 先结算去程的「一投」时间，让全局 now 与怪物回合能前进；否则 addDelayed / 调度会僵死
					user.spendAndNext(delayFactor(user));

					Buff.append(user, ChakramReturn.class).setup(Chakram.this, hover, endCell);
				});
	}

	/**
	 * 对 ballistica 路径上从起点下一格到碰撞格（含）之间的每个敌人各打一次。
	 *
	 * @param refPos 用于点射天赋等的参照格（通常掷出时为英雄掷出时的位置）
	 */
	private void hitAlongPath(Hero hero, int refPos, Ballistica b) {
		if (b.path.isEmpty() || b.dist < 1) {
			return;
		}
		for (int i = 1; i <= b.dist && i < b.path.size(); i++) {
			int cell = b.path.get(i);
			Char ch = Actor.findChar(cell);
			if (ch != null && ch != hero && ch.isAlive()) {
				float acc = throwAcc(hero, refPos, ch);
				// 原 ZootDungeon 为 Damage.physical(hero, ch, 1f, 0f, acc)（含命中判定、DR、
				// 防御/攻击 proc 与伤害结算）。minimal 无该 Damage 类：统一伤害入口是 actors.Proc 体系，
				// Char.attack 内部即走 Proc.damage(...)（offender/weapon/way 正确归因），因此直接调用攻击管线，
				// 保留命中、DR 与附魔/圣刻 proc 等全部机制。
				// 回旋镖未必装备在手（可从背包掷出）：临时挂到 thrownWeapon 上让攻击管线按本武器自身骰伤结算，
				// 与 Hero.shoot() 对投掷物的处理一致（Hero.java:555-560）。
				hero.belongings.thrownWeapon = this;
				try {
					hero.attack(ch, 1f, 0f, acc);
				} finally {
					hero.belongings.thrownWeapon = null;
				}
			}
		}
	}

	/** 近似投掷武器的远近距命中修正，再乘武器 {@link #accuracyFactor}。 */
	private float throwAcc(Hero hero, int refPos, Char target) {
		float f = accuracyFactor(hero, target);
		if (Dungeon.level.adjacent(refPos, target.pos)) {
			f *= 0.5f + 0.2f * hero.pointsInTalent(Talent.POINT_BLANK);
		} else {
			f *= 1.5f;
		}
		return f;
	}

	@Override
	public float castDelay(Char user, int dst) {
		return delayFactor(user) * 2f;
	}

	@Override
	public int min(int lvl) {
		return 5 + 3 * lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 5 * lvl;
	}

	@Override
	public int STRReq(int lvl) {
		return 10 + lvl;
	}

	@Override
	public String name() {
		return "巨型回旋镖";
	}

	@Override
	public String desc() {
		return "沿直线掷出，穿透路径上的敌人，在撞墙后会短暂停留再折返飞回手中；去程与回程都会各造成一次伤害。\n\n"
				+ "_无需装备在手：收在背包或快捷栏中即可掷出，掷出期间不影响主手武器，飞回后回到原处（手中或背包）。_\n\n"
				+ "力量需求：" + STRReq(buffedLvl()) + "\n"
				+ "伤害：" + min() + "-" + max();
	}

	/**
	 * 悬停计时用 Buff 每次 {@code spend(TICK)} 推进时间线（与 HeavyBoomerang.CircleBack 同理），
	 * 折返飞行本身由 {@link HomingReturn} 每帧实时追踪英雄当前格，保证落回英雄本身而非其上一格。
	 */
	public static class ChakramReturn extends Buff {

		private Chakram chakram;
		private ItemSprite hover;
		private int endCell;
		private int throwDepth;
		private int throwBranch;
		private int ticksLeft;

		private static final String CHAKRAM = "chakram";
		private static final String END = "end_cell";
		private static final String DEPTH = "throw_depth";
		private static final String BRANCH = "throw_branch";
		private static final String TICKS = "ticks_left";

		{
			type = buffType.NEUTRAL;
			announced = false;
		}

		public void setup(Chakram weapon, ItemSprite hoverSprite, int end) {
			chakram = weapon;
			hover = hoverSprite;
			endCell = end;
			throwDepth = Dungeon.depth;
			throwBranch = Dungeon.branch;
			ticksLeft = HOVER_TICKS;
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}

		@Override
		public boolean act() {
			if (chakram == null || target == null || !target.isAlive()) {
				killHover();
				if (chakram != null) {
					chakram.throwing = false;
				}
				detach();
				return true;
			}

			ticksLeft--;
			if (ticksLeft > 0) {
				spend(TICK);
				return true;
			}

			killHover();

			Hero hero = (Hero) target;
			if (!hero.isAlive()) {
				chakram.throwing = false;
				detach();
				return true;
			}

			//悬停期间英雄若已换层/换分支（走楼梯），旧层的远端停留点已无意义，直接收回状态
			if (throwDepth != Dungeon.depth || throwBranch != Dungeon.branch) {
				chakram.throwing = false;
				detach();
				return true;
			}

			//折返：由 HomingReturn 实时追踪英雄位置，抵达瞬间才按英雄当前格结算回程扫掠
			if (hero.sprite == null || hero.sprite.parent == null) {
				chakram.throwing = false;
				detach();
				return true;
			}
			HomingReturn flight = new HomingReturn(chakram, hero, endCell);
			hero.sprite.parent.add(flight);

			detach();
			// return false 会让 Actor 线程 wait 且 current 仍指向本 Buff，GameScene 无法 notify。
			return true;
		}

		private void killHover() {
			if (hover != null) {
				hover.killAndErase();
				hover = null;
			}
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(CHAKRAM, chakram);
			bundle.put(END, endCell);
			bundle.put(DEPTH, throwDepth);
			bundle.put(BRANCH, throwBranch);
			bundle.put(TICKS, ticksLeft);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			chakram = (Chakram) bundle.get(CHAKRAM);
			endCell = bundle.getInt(END);
			throwDepth = bundle.getInt(DEPTH);
			throwBranch = bundle.getInt(BRANCH);
			ticksLeft = bundle.getInt(TICKS);
			hover = null;
		}
	}

	/**
	 * 回程飞行体：不用一次性固定落点的 MissileSprite（英雄悬停/飞行途中移动会落到其上一格），
	 * 而是每帧朝 {@code hero.sprite} 当前中心飞行，命中瞬间再以英雄当前格结算回程扫掠与行动代价。
	 */
	private static class HomingReturn extends ItemSprite {

		/** 像素速度，与 {@link MissileSprite} 的 SPEED 一致 */
		private static final float SPEED = 240f;
		private static final float SPIN = 720f; //每帧自旋角度（度/秒）

		private final Chakram chakram;
		private final Hero hero;
		private final int fromCell;
		private boolean done = false;

		HomingReturn(Chakram chakram, Hero hero, int fromCell) {
			this.chakram = chakram;
			this.hero = hero;
			this.fromCell = fromCell;

			view(chakram);
			originToCenter();
			//起点取远端停留格的像素中心
			PointF start = DungeonTilemap.raisedTileCenterToWorld(fromCell);
			center(start);
		}

		@Override
		public void update() {
			super.update();
			if (done) {
				return;
			}

			if (hero.sprite == null || hero.sprite.parent == null || !hero.isAlive()) {
				//英雄中途死亡/离场：飞行体直接消失，状态由下次掷出时重建
				done = true;
				killAndErase();
				chakram.throwing = false;
				return;
			}

			angle += SPIN * Game.elapsed;

			PointF target = hero.sprite.center();
			PointF cur = center();
			float dx = target.x - cur.x;
			float dy = target.y - cur.y;
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			float step = SPEED * Game.elapsed;

			if (dist <= step || dist < 1f) {
				//抵达英雄：精确落在其当前中心
				center(target);
				arrive();
			} else {
				center(new PointF(cur.x + dx / dist * step, cur.y + dy / dist * step));
			}
		}

		private void arrive() {
			done = true;
			killAndErase();
			chakram.throwing = false;

			if (!hero.isAlive() || Dungeon.hero != hero) {
				return;
			}

			//抵达时才按英雄当前位置结算回程扫掠，避免悬停/飞行中移动造成的旧轨迹
			int returnTo = hero.pos;
			Ballistica inbound = new Ballistica(fromCell, returnTo, Ballistica.STOP_SOLID);
			chakram.hitAlongPath(hero, returnTo, inbound);
			Sample.INSTANCE.play(chakram.hitSound, 1f, chakram.hitSoundPitch * 0.9f);
			hero.spendAndNext(chakram.castDelay(hero, fromCell));
		}
	}
}
