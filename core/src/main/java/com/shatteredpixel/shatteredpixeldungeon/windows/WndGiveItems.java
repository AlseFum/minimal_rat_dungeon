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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemRegistry;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.debug.DebugGiveItems;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 给予物品调试窗口：4×4 页签网格，点行即生成一件（首行为"全部生成"）；
 * 末页「删除」列出英雄身上所有物品（含已装备与嵌套包内），点行确认后删除。
 *
 * <p>页签内容由 {@link ItemRegistry} 按物品类型归类得来，注册表增删条目会直接反映到这里。</p>
 */
public class WndGiveItems extends Window {

	private static final int WIDTH = 160;
	private static final int HEIGHT = 168;
	private static final int TAB_W = 38;
	private static final int TAB_H = 13;
	private static final int ROW_H = 15;

	/** 页签顺序即分类键；最后一页是删除页 */
	private static final String[] TAB_KEYS = {
			"weapon", "missile", "armor", "wand",
			"ring", "artifact", "potion", "scroll",
			"stone", "seed", "food", "trinket",
			"bomb", "spell", "misc", "delete"};
	private static final int DELETE_TAB = TAB_KEYS.length - 1;

	private final DebugGiveItems tool;
	private final StyledButton[] tabs = new StyledButton[TAB_KEYS.length];
	private final ColorBlock[] underlines = new ColorBlock[TAB_KEYS.length];
	private final LinkedHashMap<String, ArrayList<Class<? extends Item>>> byCategory = new LinkedHashMap<>();
	/** 当前页建出来的行与观察者；重建前要显式 destroy（clear 不销毁） */
	private final ArrayList<Gizmo> rows = new ArrayList<>();

	private ScrollPane listPane;
	private Component listItems;
	private int tab;

	public WndGiveItems(DebugGiveItems tool) {
		super();
		this.tool = tool;
		resize(WIDTH, HEIGHT);

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(DebugGiveItems.class, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width()) / 2f, 2);
		add(title);

		buildCategories();

		float top = title.bottom() + 4;
		for (int i = 0; i < TAB_KEYS.length; i++) {
			final int idx = i;
			tabs[i] = new StyledButton(Chrome.Type.BLANK, Messages.get(DebugGiveItems.class, "tab_" + TAB_KEYS[i]), 6) {
				@Override
				protected void onClick() {
					super.onClick();
					selectTab(idx);
				}
			};
			tabs[i].setRect(2 + (i % 4) * (TAB_W + 1), top + (i / 4) * (TAB_H + 1), TAB_W, TAB_H);
			add(tabs[i]);

			underlines[i] = new ColorBlock(TAB_W, 2, 0xFFFFFFFF);
			underlines[i].x = tabs[i].left();
			underlines[i].y = tabs[i].bottom() - 2;
			add(underlines[i]);
		}

		float listTop = top + 4 * (TAB_H + 1) + 3;

		listItems = new Component();
		listPane = new ScrollPane(listItems);
		add(listPane);
		listPane.setRect(2, listTop, WIDTH - 4, HEIGHT - listTop - 2);

		selectTab(0);
	}

	/**
	 * 物品目录 = {@link ItemRegistry}，按物品类型归到各页签。注册表里既含掉落池条目，
	 * 也含权重 0 的脚本化物品（升级卷、附魔/强化符石、开局武、移植武、剧情件……），
	 * 所以这里不做任何硬编码清单：注册表加了什么，这里就有什么。
	 */
	private void buildCategories() {
		for (int i = 0; i < DELETE_TAB; i++) {
			byCategory.put(TAB_KEYS[i], new ArrayList<>());
		}
		for (ItemRegistry.Entry entry : ItemRegistry.entries()) {
			ArrayList<Class<? extends Item>> list = byCategory.get(category(entry.type()));
			if (list != null && !list.contains(entry.type())) {
				list.add(entry.type());
			}
		}
	}

	private static String category(Class<? extends Item> type) {
		//催化剂本身只是普通 Item，按用途归到饰品页
		if (type == TrinketCatalyst.class) return "trinket";
		if (Armor.class.isAssignableFrom(type)) return "armor";
		if (MissileWeapon.class.isAssignableFrom(type)) return "missile";
		if (MeleeWeapon.class.isAssignableFrom(type)) return "weapon";
		//直挂 Weapon 的也归武器页（如 SpiritBow、移植的 Chakram/HeavyBow）
		if (Weapon.class.isAssignableFrom(type)) return "weapon";
		if (Wand.class.isAssignableFrom(type)) return "wand";
		if (Ring.class.isAssignableFrom(type)) return "ring";
		if (Artifact.class.isAssignableFrom(type)) return "artifact";
		if (Potion.class.isAssignableFrom(type)) return "potion";
		if (Scroll.class.isAssignableFrom(type)) return "scroll";
		if (Runestone.class.isAssignableFrom(type)) return "stone";
		if (Plant.Seed.class.isAssignableFrom(type)) return "seed";
		if (Food.class.isAssignableFrom(type)) return "food";
		if (Trinket.class.isAssignableFrom(type)) return "trinket";
		if (Bomb.class.isAssignableFrom(type)) return "bomb";
		if (Spell.class.isAssignableFrom(type)) return "spell";
		return "misc";
	}

	private void selectTab(int idx) {
		tab = idx;
		for (int i = 0; i < tabs.length; i++) {
			tabs[i].textColor(i == idx ? TITLE_COLOR : 0x777777);
			underlines[i].visible = i == idx;
		}
		refreshList();
	}

	private void refreshList() {
		//clear() 只断开父子、不销毁：旧行的 PointerArea 会继续挂在全局指针监听上，
		//坐标又停在原处（camera 也是之前缓存下来的），换页后点同一位置会连旧行的动作一起触发，
		//而且旧 hotArea 会挡下 DOWN，让 ScrollPane 收不到拖拽。所以重建前必须显式 destroy。
		for (Gizmo g : rows) {
			g.destroy();
		}
		rows.clear();
		listItems.clear();

		float pos = tab == DELETE_TAB ? buildDeleteRows(0) : buildGiveRows(0);

		listItems.setSize(WIDTH - 4, Math.max(pos, 1));
		listPane.content().setSize(WIDTH - 4, Math.max(pos, 1));
	}

	private float buildGiveRows(float pos) {
		ArrayList<Class<? extends Item>> classes = byCategory.get(TAB_KEYS[tab]);
		if (classes == null || classes.isEmpty()) {
			return addNote(Messages.get(DebugGiveItems.class, "empty_tab"), pos);
		}

		RowButton all = new RowButton(Messages.get(DebugGiveItems.class, "all"), () -> {
			for (Class<? extends Item> cls : byCategory.get(TAB_KEYS[tab])) {
				tool.give(cls);
			}
		});
		addRow(all, pos);
		pos += ROW_H + 1;

		for (Class<? extends Item> cls : classes) {
			final Class<? extends Item> type = cls;
			Item preview = preview(type);

			RowButton row = new RowButton(preview == null ? type.getSimpleName() : preview.name(),
					() -> tool.give(type));
			row.icon(preview == null ? null : new ItemSprite(preview));
			addRow(row, pos);
			pos += ROW_H + 1;
		}

		return pos;
	}

	private float buildDeleteRows(float pos) {
		ArrayList<Item> items = heroItems();
		if (items.isEmpty()) {
			return addNote(Messages.get(DebugGiveItems.class, "empty"), pos);
		}

		for (Item item : items) {
			final Item target = item;

			String label = item.name();
			if (item.isEquipped(Dungeon.hero)) {
				label += " " + Messages.get(DebugGiveItems.class, "equipped");
			}
			if (item.quantity() > 1) {
				label += " x" + item.quantity();
			}

			RowButton row = new RowButton(label, () -> confirmDelete(target));
			row.icon(new ItemSprite(item));
			addRow(row, pos);
			pos += ROW_H + 1;
		}

		return pos;
	}

	/** 摆一行：本体与拖拽观察者都放进内容层，坐标同为内容坐标（观察者靠这个才能命中） */
	private void addRow(RowButton row, float top) {
		row.place(2, top, WIDTH - 4, ROW_H);
		listItems.add(row);
		listItems.add(row.watcher);
		rows.add(row);
		rows.add(row.watcher);
	}

	private void confirmDelete(final Item item) {
		ShatteredPixelDungeon.scene().addToFront(new WndOptions(
				Messages.get(DebugGiveItems.class, "delete_title"),
				Messages.get(DebugGiveItems.class, "delete_body", item.name()),
				Messages.get(DebugGiveItems.class, "delete_yes"),
				Messages.get(DebugGiveItems.class, "delete_no")) {
			@Override
			protected void onSelect(int index) {
				if (index != 0) {
					return;
				}
				if (tool.delete(item)) {
					if (item == tool) {
						hide(); //删的就是本工具，窗口随物品一起关掉
					} else {
						refreshList();
					}
				}
			}
		});
	}

	/** 英雄身上的全部物品：已装备的槽位 + 背包（Bag 迭代器已递归展开嵌套包）。 */
	private ArrayList<Item> heroItems() {
		ArrayList<Item> result = new ArrayList<>();
		Hero hero = Dungeon.hero;
		if (hero == null) {
			return result;
		}

		Item[] equipped = {
				hero.belongings.weapon,
				hero.belongings.armor,
				hero.belongings.artifact,
				hero.belongings.misc,
				hero.belongings.ring,
				hero.belongings.secondWep};
		for (Item item : equipped) {
			if (item != null) {
				result.add(item);
			}
		}
		//根背包本身不是"一件物品"，不入列表；其中的嵌套包会被迭代器一并列出
		for (Item item : hero.belongings.backpack) {
			result.add(item);
		}

		return result;
	}

	/** 行首预览件：注册表条目走 build（含 modifier），未注册的反射构造。 */
	private static Item preview(Class<? extends Item> cls) {
		Item item = ItemRegistry.build(cls);
		if (item == null) {
			item = Reflection.newInstance(cls);
		}
		if (item != null) {
			item.identify();
		}
		return item;
	}

	private float addNote(String text, float pos) {
		RenderedTextBlock note = PixelScene.renderTextBlock(text, 6);
		note.maxWidth(WIDTH - 8);
		note.setPos(3, pos);
		listItems.add(note);
		return pos + note.height() + 2;
	}

	/**
	 * 行按钮。默认的按钮会吃掉指针 DOWN（PointerArea 只挡不传），铺满列表的行会把整个
	 * 内容区堵死，外层 ScrollPane 的 PointerController 就收不到 DOWN、也没法跟踪拖拽——
	 * 表现就是"列表拖不动"。所以这里按 {@link PointerArea#NEVER_BLOCK} 放行
	 * （同 {@code TalentButton} 的先例：滚动区里的按钮都该这么设），拖拽照常传给 ScrollPane。
	 * <p>代价是拖完松手若还落在本行上，按钮仍会收到 onClick，故用 {@link DragWatcher}
	 * 忽略已经构成拖拽的那一次。</p>
	 */
	private class RowButton extends StyledButton {

		private final DragWatcher watcher = new DragWatcher();
		private final Runnable action;

		RowButton(String label, Runnable action) {
			super(Chrome.Type.GREY_BUTTON_TR, label, 6);
			this.action = action;
			hotArea.blockLevel = PointerArea.NEVER_BLOCK;
			leftJustify = true;
		}

		void place(float x, float y, float w, float h) {
			setRect(x, y, w, h);
			//PointerArea 是 Visual，没有 setRect，直接摆字段
			watcher.x = x;
			watcher.y = y;
			watcher.width = w;
			watcher.height = h;
		}

		@Override
		protected void onClick() {
			if (!watcher.dragged) {
				action.run();
			}
		}
	}

	/** 只观察不拦截：记录这一次指针动作是否已经拖出了 ScrollPane 的滚动阈值。 */
	private static class DragWatcher extends PointerArea {

		//与 ScrollPane.PointerController 的拖拽阈值保持一致
		private static final float THRESHOLD = PixelScene.defaultZoom * 8;

		private boolean dragged;

		DragWatcher() {
			super(0, 0, 16, 16);
			blockLevel = NEVER_BLOCK;
		}

		@Override
		protected void onPointerDown(PointerEvent event) {
			dragged = false;
		}

		@Override
		protected void onDrag(PointerEvent event) {
			if (PointF.distance(event.current, event.start) > THRESHOLD) {
				dragged = true;
			}
		}
	}
}
