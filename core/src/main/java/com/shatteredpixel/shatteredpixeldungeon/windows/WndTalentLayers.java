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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.MiseryShadowBlob;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.TalentLayer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

/**
 * Debug 工具窗口：上半是「天赋层」——列出全部已注册 TalentLayer，点选即添加（可无限加）；
 * 下半是「子职业」——列出全部已注册子职业，点选即切换（含只靠调试可达的 MISERY）。
 */
public class WndTalentLayers extends Window {

	private static final int WIDTH = 130;
	private static final int HEIGHT = 150;
	private static final int ROW_H = 14;

	private ScrollPane listPane;
	private Component listItems;
	/** 当前页建出来的按钮；重建前要显式 destroy（clear 不销毁） */
	private final ArrayList<Gizmo> rows = new ArrayList<>();

	public WndTalentLayers() {
		super();
		resize(WIDTH, HEIGHT);

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(WndTalentLayers.class, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width()) / 2f, 0);
		add(title);

		listItems = new Component();
		listPane = new ScrollPane(listItems);
		add(listPane);
		listPane.setRect(0, title.bottom() + 3, WIDTH, HEIGHT - title.bottom() - 5);

		refreshList();
	}

	private void refreshList() {
		//clear() 只断开父子、不销毁：旧按钮的 PointerArea 仍挂在指针监听上且坐标未变，
		//重建后点同一位置会连旧按钮一起触发，故先显式 destroy（同 WndGiveItems 的处理）
		for (Gizmo g : rows) {
			g.destroy();
		}
		rows.clear();
		listItems.clear();

		float pos = sectionHeader("section_layers", 0);
		pos = talentLayerRows(pos);
		pos = sectionHeader("section_subclass", pos + 4);
		pos = subclassRows(pos);

		listItems.setSize(WIDTH, pos);
		listPane.content().setSize(WIDTH, pos);
	}

	private float sectionHeader( String key, float pos ) {
		RenderedTextBlock header = PixelScene.renderTextBlock(Messages.get(WndTalentLayers.class, key), 6);
		header.hardlight(TITLE_COLOR);
		header.setPos(2, pos);
		listItems.add(header);
		return pos + header.height() + 1;
	}

	private float talentLayerRows( float pos ) {
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
			btn.setRect(2, pos, WIDTH - 4, ROW_H);
			listItems.add(btn);
			rows.add(btn);
			pos += ROW_H + 1;
		}
		return pos;
	}

	private float subclassRows( float pos ) {
		for (final HeroSubClass sub : HeroSubClass.all()) {
			if (sub == HeroSubClass.NONE) continue;

			boolean current = Dungeon.hero != null && Dungeon.hero.subClass == sub;

			RedButton btn = new RedButton(Messages.titleCase(sub.title())
					+ (current ? Messages.get(WndTalentLayers.class, "current") : "")) {
				@Override
				protected void onClick() {
					super.onClick();
					if (Dungeon.hero == null || Dungeon.hero.subClass == sub) return;

					Dungeon.hero.subClass = sub;
					//子职业天赋并入职业 t3 层（同一点数池），同 TengusMask.choose
					Dungeon.hero.mergeSubclassTalents(sub);

					//MISERY 还会把整层的阴影铺开（同原 DebugMisery）
					if (sub == HeroSubClass.MISERY) {
						MiseryShadowBlob.generateForLevel();
						Dungeon.observe();
						GameScene.updateFog();
					}

					GLog.p(Messages.get(WndTalentLayers.class, "subclass_set", Messages.titleCase(sub.title())));
					refreshList();
				}
			};
			btn.setRect(2, pos, WIDTH - 4, ROW_H);
			listItems.add(btn);
			rows.add(btn);
			pos += ROW_H + 1;
		}
		return pos;
	}
}
