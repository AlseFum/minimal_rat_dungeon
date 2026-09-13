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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ported;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

/**
 * 由 ZootDungeon 项目 arknights.MainTheme.HourOfAnAwakening.Infantry 移植而来。
 * <p>适配差异：
 * <ul>
 *   <li>本 fork 无经验系统，原 EXP=2 / maxLvl=8 未移植；</li>
 *   <li>Zoot 的 LootRegistry 权重掉落表改为本 fork 的单件掉落模型（见 {@link #createLoot()}）；
 *       原表 Gold3 / 治疗2 / 力量1 / SoU1 / 种子类目1，其中力量药在本 fork 不存在，以加速药替代；</li>
 *   <li>Zoot 的 TextureRegistry + RAT 贴图回退改为直接加载本 fork 的 ported 贴图（无回退）。</li>
 * </ul>
 */
public class Infantry extends Mob {

	{
		spriteClass = InfantrySprite.class;

		HP = HT = 9;
		defenseSkill = 2;

		lootChance = 0.4f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 5);
	}

	@Override
	public int attackSkill(Char target) {
		return 10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 2);
	}

	/** 原 Zoot 权重表（Gold3 / 治疗2 / 力量1 / SoU1 / 种子1）的单件化版本；力量药→加速药。 */
	@Override
	public Item createLoot() {
		switch (Random.chances(new float[]{3, 2, 1, 1, 1})) {
			case 0: return new Gold().random();
			case 1: return new PotionOfHealing();
			case 2: return new PotionOfHaste();
			case 3: return new ScrollOfUpgrade();
			default: return Loot.randomUsingDefaults(Loot.SEED);
		}
	}

	public static class InfantrySprite extends MobSprite {

		/** 单帧 32x32 挤进地图格的比例（0.5 = 16px） */
		private static final float TILE_SCALE = 0.5f;
		private static final int FRAME_SIZE = 32;

		public InfantrySprite() {
			super();

			texture("sprites/ported/infantry.png");
			//800x32 = 25 个 32x32 帧，帧序号沿用 ZootDungeon 原值
			TextureFilm frames = new TextureFilm(texture, 32, 32);

			idle = new Animation(1, true);
			idle.frames(frames, 23);

			run = new Animation(10, true);
			run.frames(frames, 0, 1, 2, 3, 4, 5, 6);

			attack = new Animation(10, false);
			attack.frames(frames, 8, 9, 10, 11, 12);

			die = new Animation(9, false);
			die.frames(frames, 13, 14, 15, 16, 17, 18, 19, 20, 21);

			play(idle);
		}

		/**
		 * 大图素材不走 scale 缩放，按 fork 处理移植贴图的惯例（见 ItemSprite.view）：
		 * UV 保持整帧，重置几何尺寸并重建顶点。必须覆写 frame()——动画每帧都会调用它，
		 * 否则尺寸会被还原成 32x32（这正是原先 scale 看起来"不生效"的原因）。
		 */
		@Override
		public void frame(RectF frame) {
			super.frame(frame);
			width = FRAME_SIZE * TILE_SCALE;
			height = FRAME_SIZE * TILE_SCALE;
			updateFrame();
			updateVertices();
		}
	}
}
