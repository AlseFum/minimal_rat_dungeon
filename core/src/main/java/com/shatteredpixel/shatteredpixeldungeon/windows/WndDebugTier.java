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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

/**
 * Debug 工具窗口：调整当前装备武器的 tier（0-5）。
 */
public class WndDebugTier extends Window {

	private static final int WIDTH = 120;

	public WndDebugTier() {
		super();

		KindOfWeapon weapon = Dungeon.hero.belongings.weapon;
		if (!(weapon instanceof MeleeWeapon) && !(weapon instanceof MissileWeapon)) {
			resize(WIDTH, 40);
			RenderedTextBlock msg = PixelScene.renderTextBlock(Messages.get(WndDebugTier.class, "no_tier"), 6);
			msg.maxWidth(WIDTH - 10);
			msg.setPos(5, 10);
			add(msg);
			return;
		}

		final int currentTier = weapon instanceof MeleeWeapon
				? ((MeleeWeapon) weapon).tier : ((MissileWeapon) weapon).tier;

		resize(WIDTH, 40 + 6 * 20);

		RenderedTextBlock title = PixelScene.renderTextBlock(
				Messages.get(WndDebugTier.class, "title", weapon.title(), currentTier), 7);
		title.hardlight(TITLE_COLOR);
		title.maxWidth(WIDTH - 10);
		title.setPos(5, 5);
		add(title);

		for (int t = 0; t <= 5; t++) {
			final int tier = t;
			RedButton btn = new RedButton(Messages.get(WndDebugTier.class,
					t == currentTier ? "tier_current" : "tier_button", t)) {
				@Override
				protected void onClick() {
					super.onClick();
					if (Dungeon.hero.belongings.weapon instanceof MeleeWeapon) {
						((MeleeWeapon) Dungeon.hero.belongings.weapon).tier = tier;
					} else if (Dungeon.hero.belongings.weapon instanceof MissileWeapon) {
						((MissileWeapon) Dungeon.hero.belongings.weapon).tier = tier;
					}
					GLog.p(Messages.get(WndDebugTier.class, "set_tier", tier));
					hide();
				}
			};
			btn.setRect(5, 30 + t * 20, WIDTH - 10, 16);
			add(btn);
		}
	}
}
