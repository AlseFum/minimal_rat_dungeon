package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** A registered region which can own and create one or more dungeon coordinates. */
public abstract class DungeonRegion {

	private static final ArrayList<DungeonRegion> REGIONS = new ArrayList<>();
	private static boolean builtinsRegistered;

	public abstract String id();

	/** Returns zero when this region does not apply to the current depth and branch. */
	public abstract float weight();

	/** 具体层类（new SewerLevel() 等；含 boss 分支） */
	protected abstract Level constructLevel();

	/** 该 region 的变体池（默认全部已注册变体；region 可覆写限定自己的池） */
	public RegionVariant[] variants() {
		return RegionVariant.all();
	}

	/**
	 * 生成本层可能的变体（在 level 生成之前调用）。
	 * 默认：每个变体独立概率 roll + 饰品附加变体；region 可覆写（如 finale 固定变体）。
	 */
	public RegionVariant[] generateVariants() {
		ArrayList<RegionVariant> rolled = new ArrayList<>();
		if (Dungeon.depth > 1) {
			for (RegionVariant variant : variants()) {
				if (Random.Float() < variant.weight()) {
					rolled.add(variant);
				}
			}
			//饰品附加变体（如 MossyClump 的草地/水域）
			if (Random.Float() < MossyClump.overrideNormalLevelChance()) {
				RegionVariant v = MossyClump.getNextVariant();
				if (v != null) rolled.add(v);
			}
			if (Random.Float() < TrapMechanism.overrideNormalLevelChance()) {
				RegionVariant v = TrapMechanism.getNextVariant();
				if (v != null) rolled.add(v);
			}
		}
		return rolled.toArray(new RegionVariant[0]);
	}

	/**
	 * 模板：变体先于建层生成 → 建层并挂载变体 → 生成地图（modifySize 钩子在 create 内执行）
	 * → 逐变体 apply。
	 */
	public final Level createLevel() {
		RegionVariant[] variants = generateVariants();
		Level level = constructLevel();
		level.variants = variants;
		level.create();
		for (RegionVariant variant : variants) {
			variant.apply(level);
		}
		return level;
	}

	/** Adjusts generated transitions for this region without changing base level classes. */
	public void configureLevel(Level level) { }

	/**
	 * Picks up to {@code count} random open cells (passable, no mob, trap or
	 * entity), each at least {@code minDist} tiles away from the others.
	 * Returns fewer when the level does not have enough open space.
	 */
	protected ArrayList<Integer> pickOpenCells(Level level, int count, int minDist) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i = 0; i < level.length(); i++) {
			if (level.passable[i] && !level.solid[i]
					&& level.findMob(i) == null
					&& level.traps.get(i) == null
					&& level.entityAt(i) == null) {
				candidates.add(i);
			}
		}
		ArrayList<Integer> picked = new ArrayList<>();
		while (picked.size() < count && !candidates.isEmpty()) {
			int cell = candidates.remove(Random.Int(candidates.size()));
			picked.add(cell);
			candidates.removeIf(c -> level.distance(c, cell) < minDist);
		}
		return picked;
	}

	public boolean isBossLevel() { return false; }

	public boolean shopOnLevel() { return false; }

	public static void register(DungeonRegion region) {
		if (region == null || region.id() == null || region.id().isEmpty()) {
			throw new IllegalArgumentException("Region and region id are required");
		}
		for (DungeonRegion existing : REGIONS) {
			if (existing.id().equals(region.id())) {
				throw new IllegalArgumentException("Duplicate dungeon region: " + region.id());
			}
		}
		REGIONS.add(region);
	}

	public static DungeonRegion resolve() {
		registerBuiltins();
		ArrayList<DungeonRegion> candidates = new ArrayList<>();
		ArrayList<Float> weights = new ArrayList<>();
		float total = 0;
		for (DungeonRegion region : REGIONS) {
			float weight = region.weight();
			if (weight > 0) {
				candidates.add(region);
				weights.add(weight);
				total += weight;
			}
		}
		if (total <= 0) {
			throw new IllegalStateException("No dungeon region for depth " + Dungeon.depth
					+ ", branch " + Dungeon.branch);
		}
		float choice = Random.Float(total);
		for (int i = 0; i < candidates.size(); i++) {
			float weight = weights.get(i);
			if (choice < weight) return candidates.get(i);
			choice -= weight;
		}
		throw new IllegalStateException("Failed to select dungeon region");
	}

	public static DungeonRegion find(String id) {
		registerBuiltins();
		for (DungeonRegion region : REGIONS) {
			if (region.id().equals(id)) return region;
		}
		return null;
	}

	private static void registerBuiltins() {
		if (builtinsRegistered) return;
		builtinsRegistered = true;
		register(new ActIRegion());
		register(new ChapVRegion());
		register(new ChapVIRegion());
		register(new ChapVIIRegion());
		register(new DungeonRegion() {
			public String id() { return "sewers"; }
			public float weight() { return Dungeon.mode == Dungeon.Mode.DEMO && Dungeon.branch == 0 && Dungeon.depth >= 1 && Dungeon.depth <= 5 ? 1 : 0; }
			protected Level constructLevel() {
				Level level = isBossLevel() ? new SewerBossLevel() : new SewerLevel();
				level.regionId = id();
				return level;
			}
			public boolean isBossLevel() { return Dungeon.depth == 5 && Dungeon.branch == 0; }
			public boolean shopOnLevel() { return Dungeon.depth == 2 && Dungeon.branch == 0; }
			public void configureLevel(Level level) {
				if (isBossLevel()) {
					LevelTransition exit = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
					if (exit != null) {
						exit.destDepth = 6;
						exit.destBranch = 63;
						exit.destType = LevelTransition.Type.REGULAR_ENTRANCE;
					}
				}
			}
		});
		register(new DungeonRegion() {
			public String id() { return "infinite"; }
			public float weight() {
				return Dungeon.mode == Dungeon.Mode.INFINITE
						&& Dungeon.depth >= 1
						&& Dungeon.branch == 0 ? 1 : 0;
			}
			protected Level constructLevel() {
				Level level = isBossLevel() ? new SewerBossLevel() : new SewerLevel();
				level.regionId = id();
				return level;
			}
			public boolean isBossLevel() { return Dungeon.depth % 5 == 0; }
			public boolean shopOnLevel() { return Dungeon.depth % 5 == 1; }
			public void configureLevel(Level level) {
				if (isBossLevel()) {
					LevelTransition exit = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
					if (exit != null) {
						exit.destDepth = Dungeon.depth + 1;
						exit.destBranch = 63;
						exit.destType = LevelTransition.Type.REGULAR_ENTRANCE;
					}
				}
			}
		});
		register(new DungeonRegion() {
			public String id() { return "infinite-finale"; }
			public float weight() {
				return Dungeon.mode == Dungeon.Mode.INFINITE
						&& Dungeon.branch == 63
						&& Dungeon.depth % 5 == 1 ? 1 : 0;
			}
			protected Level constructLevel() {
				Level level = new LastLevel();
				level.regionId = id();
				return level;
			}
			public void configureLevel(Level level) {
				LevelTransition entry = level.getTransition(LevelTransition.Type.REGULAR_ENTRANCE);
				if (entry != null) {
					entry.destDepth = Dungeon.depth - 1;
					entry.destBranch = 0;
					entry.destType = LevelTransition.Type.REGULAR_EXIT;
				}
			}
		});
		register(new DungeonRegion() {
			public String id() { return "mining"; }
			public float weight() { return Dungeon.depth == 2 && Dungeon.branch == 1 ? 1 : 0; }
			protected Level constructLevel() {
				Level level = new MiningLevel();
				level.regionId = id();
				return level;
			}
		});
		register(new DungeonRegion() {
			public String id() { return "finale"; }
			public float weight() { return Dungeon.mode == Dungeon.Mode.DEMO && Dungeon.depth == 6 && Dungeon.branch == 63 ? 1 : 0; }
			protected Level constructLevel() {
				Level level = new LastLevel();
				level.regionId = id();
				return level;
			}
		});
	}
}
