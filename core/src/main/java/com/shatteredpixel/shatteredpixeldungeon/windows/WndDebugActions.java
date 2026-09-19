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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugTool;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

/**
 * HUD 调试入口的菜单：把英雄身上所有 {@link DebugTool} 列成一行行（图标 + 名字），
 * 点一行开该工具的动作清单（{@link WndUseItem}）——省掉"翻背包找工具"这一步。
 * 工具是从背包里现扫的，以后新增调试工具会自动出现在这里。
 *
 * <p>只列工具、不直接平铺所有动作：本 fork 的 UI 相机只有 240 高，
 * 十几个动作排下来会超出屏幕，两级下来每级都放得下。</p>
 */
public class WndDebugActions extends Window {

	private static final int WIDTH = 140;
	private static final int ROW_H = 18;

	public WndDebugActions() {
		super();

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width()) / 2f, 0);
		add(title);

		float pos = title.bottom() + 2;

		if (Dungeon.hero != null) {
			for (final Item tool : Dungeon.hero.belongings) {
				if (!(tool instanceof DebugTool)) continue;

				StyledButton row = new StyledButton(Chrome.Type.GREY_BUTTON_TR, tool.name(), 6) {
					@Override
					protected void onClick() {
						super.onClick();
						hide();
						if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(tool)) {
							GameScene.show( new WndUseItem( null, tool ) );
						}
					}
				};
				row.leftJustify = true;
				row.icon( new ItemSprite(tool) );
				row.setRect(2, pos, WIDTH - 4, ROW_H);
				add(row);
				pos += ROW_H + 1;
			}
		}

		resize(WIDTH, (int)pos);
	}
}
