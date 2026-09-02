package com.shatteredpixel.shatteredpixeldungeon.items.debug;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.util.ArrayList;

/** Debug-only utility which immediately enters the next floor. */
public class DebugNextFloor extends Item implements DebugTool {

	private static final String AC_USE = "USE";

	{
		image = ItemSpriteSheet.SCROLL_HOLDER;
		defaultAction = AC_USE;
		unique = true;
		stackable = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_USE.equals(action) && Dungeon.level != null) {
			goToNextLevel();
		}
	}

	public static void goToNextLevel() {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		Dungeon.level.beforeTransition();
		InterlevelScene.curTransition = new LevelTransition(
				Dungeon.level, Dungeon.level.exit(), LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE);
		InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		Game.switchScene(InterlevelScene.class);
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 0;
	}
}
