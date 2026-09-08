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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Loot;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugWeaponPool;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.AscalonAOE;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.Chakram;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.DeployablewCrossBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ported.PhantomKnife;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/**
 * 近战武器调试列表（tab 式）：上半为两个页签「常规武池 / Zoot 移植」，
 * 下方内容区为当前页的图标行（ItemSprite 图标 + 右侧文本，带框）。点行即生成入包。
 */
public class WndWeaponPool extends Window {

	private static final int WIDTH = 128;
	private static final int ROW_H = 16;

	/** Zoot 移植武器（不参与随机掉落，仅调试生成） */
	private static final Class<?>[] PORTED = new Class<?>[]{
			AscalonAOE.class,
			Chakram.class,
			DeployablewCrossBow.class,
			PhantomKnife.class
	};

	private final DebugWeaponPool tool;
	private final StyledButton[] tabs = new StyledButton[2];
	private final ColorBlock[] underlines = new ColorBlock[2];
	private final ArrayList<StyledButton> rows = new ArrayList<>();

	private int tab = 0;
	private float rowTop;

	public WndWeaponPool(DebugWeaponPool tool) {
		this.tool = tool;

		//页签
		float tabW = (WIDTH - 4 - 2) / 2f;
		String[] labels = new String[]{
				Messages.get(DebugWeaponPool.class, "pool_header"),
				Messages.get(DebugWeaponPool.class, "ported_header")
		};
		for (int i = 0; i < 2; i++) {
			final int idx = i;
			tabs[i] = new StyledButton(Chrome.Type.BLANK, labels[i], 7) {
				@Override
				protected void onClick() {
					super.onClick();
					showTab(idx);
				}
			};
			tabs[i].setRect(2 + i * (tabW + 2), 0, tabW, 16);
			add(tabs[i]);

			underlines[i] = new ColorBlock(tabW, 2, 0xFFFFFFFF);
			underlines[i].x = tabs[i].left();
			underlines[i].y = tabs[i].bottom() - 2;
			add(underlines[i]);
		}

		rowTop = tabs[0].bottom() + 3;
		showTab(0);
	}

	private void showTab(int idx) {
		tab = idx;
		for (StyledButton row : rows) {
			row.killAndErase();
		}
		rows.clear();

		for (int i = 0; i < 2; i++) {
			tabs[i].textColor(i == idx ? Window.TITLE_COLOR : 0x777777);
			underlines[i].visible = i == idx;
		}

		float pos = rowTop;
		Class<?>[] list = tab == 0 ? Loot.classes(Loot.WEP_T1) : PORTED;
		if (list != null) {
			if (tab == 0) {
				addRow(null, Messages.get(DebugWeaponPool.class, "all"), pos);
				pos += ROW_H + 1;
			}
			for (Class<?> cls : list) {
				addRow(cls, Messages.get(cls, "name"), pos);
				pos += ROW_H + 1;
			}
		}

		resize(WIDTH, (int) pos);
	}

	private void addRow(final Class<?> cls, String label, float top) {
		StyledButton row = new StyledButton(Chrome.Type.GREY_BUTTON_TR, label, 6) {
			@Override
			protected void onClick() {
				super.onClick();
				if (cls == null) {
					for (Class<?> c : Loot.classes(Loot.WEP_T1)) {
						tool.generate(c);
					}
				} else {
					tool.generate(cls);
				}
			}
		};
		row.leftJustify = true;
		if (cls != null) {
			row.icon(iconFor(cls));
		}
		row.setRect(2, top, WIDTH - 4, ROW_H);
		add(row);
		rows.add(row);
	}

	@SuppressWarnings("unchecked")
	private static ItemSprite iconFor(Class<?> cls) {
		Item item = Reflection.newInstance((Class<? extends Item>) cls);
		if (item == null) {
			return null;
		}
		item.identify();
		return new ItemSprite(item);
	}
}
