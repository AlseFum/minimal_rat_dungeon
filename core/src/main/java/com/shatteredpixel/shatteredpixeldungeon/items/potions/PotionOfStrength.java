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

package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * 力量药水（沿用原作）：永久 +1 力量。
 *
 * <p>不入平铺随机池——在 {@link com.shatteredpixel.shatteredpixeldungeon.items.Loot#POTION} 里概率为 0，
 * 登记只是为了参与药剂鉴定与颜色分配；实际发放由
 * {@link com.shatteredpixel.shatteredpixeldungeon.Dungeon#strNeeded()} 每 5 层保底 2 瓶。</p>
 */
public class PotionOfStrength extends Potion {

	{
		icon = ItemSpriteSheet.Icons.POTION_STRENGTH;

		unique = true;
		bones = true;
	}

	@Override
	public void apply( Hero hero ) {
		identify();
		hero.STR++;
		hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "msg_1") );
		GLog.p( Messages.get(this, "msg_2") );
	}

	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
