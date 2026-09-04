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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import java.util.LinkedHashMap;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/**
 * 区域变体（原 Level.Feeling）：同一层可挂多个变体组合（如"又暗又大又多陷阱"）。
 * 变体由 {@link DungeonRegion#generateVariants()} 在 level 生成前 roll，
 * 随层持久化（保存 id 数组），每个变体自带显示（title/desc）与生成钩子。
 */
public abstract class RegionVariant {

	public abstract String id();

	public abstract String title();

	public abstract String desc();

	/** 独立出现概率（每层逐变体判定；7 个变体各 0.09 ≈ 约 48% 的层至少有一个变体） */
	public float weight() {
		return 0.09f;
	}

	/** 生成后修改（地形/陷阱/视野等；painter 阶段生效，读 level.variants） */
	public void apply(Level level) {
	}

	/** 生成前修改尺寸（如 LARGE；Level.create 开头调用） */
	public void modifySize(Level level) {
	}

	// ==================== 注册表 ====================

	private static final LinkedHashMap<String, RegionVariant> REGISTRY = new LinkedHashMap<>();

	/** 注册变体并返回实例；重复/空 id 抛 IllegalArgumentException */
	public static RegionVariant register(RegionVariant variant) {
		if (variant == null || variant.id() == null || variant.id().isEmpty()) {
			throw new IllegalArgumentException("region variant id must not be null or empty");
		}
		if (REGISTRY.containsKey(variant.id())) {
			throw new IllegalArgumentException("region variant '" + variant.id() + "' is already registered");
		}
		REGISTRY.put(variant.id(), variant);
		return variant;
	}

	/** 按 id 查找；未知 id 返回 null */
	public static RegionVariant find(String id) {
		return REGISTRY.get(id);
	}

	/** 全部已注册变体（注册序） */
	public static RegionVariant[] all() {
		return REGISTRY.values().toArray(new RegionVariant[0]);
	}

	// ==================== 内置变体 ====================

	public static class Chasm extends RegionVariant {
		@Override
		public String id() {
			return "chasm";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static class Water extends RegionVariant {
		@Override
		public String id() {
			return "water";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static class Grass extends RegionVariant {
		@Override
		public String id() {
			return "grass";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static class Dark extends RegionVariant {
		@Override
		public String id() {
			return "dark";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void apply(Level level) {
			level.viewDistance = Math.round(5 * level.viewDistance / 8f);
		}
	}

	public static class Large extends RegionVariant {
		@Override
		public String id() {
			return "large";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}

		@Override
		public void modifySize(Level level) {
			level.addItemToSpawn(com.shatteredpixel.shatteredpixeldungeon.items.Loot.random(
					com.shatteredpixel.shatteredpixeldungeon.items.Loot.FOOD));
		}
	}

	public static class Traps extends RegionVariant {
		@Override
		public String id() {
			return "traps";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static class Secrets extends RegionVariant {
		@Override
		public String id() {
			return "secrets";
		}

		@Override
		public String title() {
			return Messages.get(this, "title");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc");
		}
	}

	public static final RegionVariant CHASM   = register(new Chasm());
	public static final RegionVariant WATER   = register(new Water());
	public static final RegionVariant GRASS   = register(new Grass());
	public static final RegionVariant DARK    = register(new Dark());
	public static final RegionVariant LARGE   = register(new Large());
	public static final RegionVariant TRAPS   = register(new Traps());
	public static final RegionVariant SECRETS = register(new Secrets());
}
