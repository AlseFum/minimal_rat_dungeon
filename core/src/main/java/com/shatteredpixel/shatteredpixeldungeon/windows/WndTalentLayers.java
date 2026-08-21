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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.TalentLayer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ui.Component;

/**
 * Debug 工具窗口：列出全部已注册 TalentLayer，点选即添加到当前角色（可无限加）。
 */
public class WndTalentLayers extends Window {

	private static final int WIDTH = 130;
	private static final int HEIGHT = 140;

	private ScrollPane listPane;
	private Component listItems;

	public WndTalentLayers() {
		super();
		resize(WIDTH, HEIGHT);

		listItems = new Component();
		listPane = new ScrollPane(listItems);
		add(listPane);
		listPane.setRect(0, 0, WIDTH, HEIGHT);

		refreshList();
	}

	private void refreshList() {
		listItems.clear();

		float pos = 0;
		for (TalentLayer layer : TalentLayer.all()) {
			//合并源层（子职业层）不可独立添加，不列出
			if (layer.isMergeSource()) continue;

			final TalentLayer l = layer;
			boolean owned = Dungeon.hero != null && Dungeon.hero.talentLayers.containsKey(layer.id);

			RedButton btn = new RedButton(layer.title() + (owned ? Messages.get(WndTalentLayers.class, "owned") : "")) {
				@Override
				protected void onClick() {
					super.onClick();
					if (Dungeon.hero != null) {
						Dungeon.hero.addTalentLayer(l.id);
						GLog.p(Messages.get(WndTalentLayers.class, "added", l.id));
						refreshList();
					}
				}
			};
			btn.setRect(2, pos, WIDTH - 4, 14);
			listItems.add(btn);
			pos += 16;
		}

		listItems.setSize(WIDTH, pos);
		listPane.content().setSize(WIDTH, pos);
	}
}
