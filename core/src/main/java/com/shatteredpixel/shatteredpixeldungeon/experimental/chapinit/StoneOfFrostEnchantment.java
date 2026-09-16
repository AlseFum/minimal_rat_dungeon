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

package com.shatteredpixel.shatteredpixeldungeon.experimental.chapinit;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * 寒霜附魔符石（霜星掉落，d:/dungeon-repo/Enemy/QuestSpecified/Frostnova(StarterLayer4).md
 * 「只附魔寒霜的附魔符石」）：只对武器生效，且只上固定的寒霜附魔，
 * 不像原版附魔符石那样出随机附魔。
 */
public class StoneOfFrostEnchantment extends InventoryStone {

	{
		preferredBag = Belongings.Backpack.class;
		image = ItemSpriteSheet.STONE_ENCHANT;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item instanceof Weapon;
	}

	@Override
	protected void onItemSelected(Item item) {

		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}

		((Weapon)item).enchant(new Chilling());

		curUser.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.1f, 5);
		Enchanting.show(curUser, item);

		GLog.p(Messages.get(this, "weapon"));

		useAnimation();
	}

	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 5 * quantity;
	}
}
