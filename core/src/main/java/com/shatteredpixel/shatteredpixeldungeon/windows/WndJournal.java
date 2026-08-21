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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList;
import com.shatteredpixel.shatteredpixeldungeon.ui.CustomNoteButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingGridPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;

public class WndJournal extends WndTabbed {
	
	public static final int WIDTH_P     = 126;
	public static final int HEIGHT_P    = 180;
	
	public static final int WIDTH_L     = 216;
	public static final int HEIGHT_L    = 130;
	
	private static final int ITEM_HEIGHT	= 18;
	
	private GuideTab guideTab;
	private NotesTab notesTab;
	private CatalogTab catalogTab;
	private BadgesTab badgesTab;
	
	public static int last_index = 0;

	private static WndJournal INSTANCE = null;
	
	public WndJournal(){

		if (INSTANCE != null){
			INSTANCE.hide();
		}
		
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
		
		resize(width, height);
		
		guideTab = new GuideTab();
		add(guideTab);
		guideTab.setRect(0, 0, width, height);
		guideTab.updateList();
		
		notesTab = new NotesTab();
		add(notesTab);
		notesTab.setRect(0, 0, width, height);
		notesTab.updateList();
		
		catalogTab = new CatalogTab();
		add(catalogTab);
		catalogTab.setRect(0, 0, width, height);
		catalogTab.updateList();

		badgesTab = new BadgesTab();
		add(badgesTab);
		badgesTab.setRect(0, 0, width, height);
		badgesTab.updateList();

		if (last_index < 0 || last_index > 3) {
			last_index = 0;
		}
		
		Tab[] tabs = {
				new IconTab( Icons.JOURNAL.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						notesTab.active = notesTab.visible = value;
						if (value) last_index = 0;
					}

					@Override
					protected String hoverText() {
						return Messages.get(notesTab, "title");
					}
				},
				new IconTab( new ItemSprite(ItemSpriteSheet.MASTERY, null) ) {
					protected void select( boolean value ) {
						super.select( value );
						guideTab.active = guideTab.visible = value;
						if (value) last_index = 1;
					}

					@Override
					protected String hoverText() {
						return Messages.get(guideTab, "title");
					}
				},
				new IconTab( Icons.CATALOG.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						catalogTab.active = catalogTab.visible = value;
						if (value) last_index = 2;
					}

					@Override
					protected String hoverText() {
						return Messages.get(catalogTab, "title");
					}
				},
				new IconTab( Icons.BADGES.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						badgesTab.active = badgesTab.visible = value;
						if (value) last_index = 3;
					}

					@Override
					protected String hoverText() {
						return Messages.get(badgesTab, "title");
					}
				}
		};

		for (Tab tab : tabs) {
			add( tab );
		}
		
		layoutTabs();
		
		select(last_index);

		INSTANCE = this;
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.JOURNAL) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		guideTab.layout();
		notesTab.layout();
		catalogTab.layout();
	}
	
	public static class GuideTab extends Component {

		private ScrollingListPane list;
		
		@Override
		protected void createChildren() {
			list = new ScrollingListPane();
			add( list );
		}
		
		@Override
		protected void layout() {
			super.layout();
			list.setRect( x, y, width, height);
		}
		
		public void updateList(){
			list.addTitle(Document.ADVENTURERS_GUIDE.title());

			for (String page : Document.ADVENTURERS_GUIDE.pageNames()){
				boolean found = Document.ADVENTURERS_GUIDE.isPageFound(page);
				ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
						Document.ADVENTURERS_GUIDE.pageSprite(page),
						null,
						found ? Messages.titleCase(Document.ADVENTURERS_GUIDE.pageTitle(page)) : Messages.titleCase(Messages.get( this, "missing" ))
				){
					@Override
					public boolean onClick(float x, float y) {
						if (inside( x, y ) && found) {
							ShatteredPixelDungeon.scene().addToFront( new WndStory( Document.ADVENTURERS_GUIDE.pageSprite(page),
									Document.ADVENTURERS_GUIDE.pageTitle(page),
									Document.ADVENTURERS_GUIDE.pageBody(page) ));
							Document.ADVENTURERS_GUIDE.readPage(page);
							return true;
						} else {
							return false;
						}
					}
				};
				if (!found){
					item.hardlight(0x999999);
					item.hardlightIcon(0x999999);
				}
				list.addItem(item);
			}

			list.setRect(x, y, width, height);
		}

	}
	
	private static class NotesTab extends Component {
		
		private ScrollingGridPane grid;
		private CustomNoteButton custom;
		
		@Override
		protected void createChildren() {
			grid = new ScrollingGridPane();
			add(grid);
		}
		
		@Override
		protected void layout() {
			super.layout();
			grid.setRect( x, y, width, height);
		}
		
		private void updateList(){

			grid.addHeader("_" + Messages.get(this, "title") + "_", 9, true);

			grid.addHeader(Messages.get(this, "desc"), 6, true);

			ArrayList<Notes.CustomRecord> customRecs = Notes.getRecords(Notes.CustomRecord.class);

			if (!customRecs.isEmpty()){
				grid.addHeader("_" + Messages.get(this, "custom_notes") + "_ (" + customRecs.size() + "/" + Notes.customRecordLimit() + ")");

				for (Notes.CustomRecord rec : customRecs){
					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new CustomNoteButton.CustomNoteWindow(rec, WndJournal.INSTANCE));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			for (int i = Statistics.deepestFloor; i > 0; i--){

				ArrayList<Notes.Record> recs = Notes.getRecords(i);

				if (i == Dungeon.depth) {
					grid.addHeader("_" + Messages.get(this, "floor_header", i) + "_");
				} else {
					grid.addHeader(Messages.get(this, "floor_header", i));
				}
				for( Notes.Record rec : recs){

					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new WndJournalItem(rec.icon(),
										Messages.titleCase(rec.title()),
										rec.desc()));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			custom = new CustomNoteButton();
			grid.content().add(custom);
			custom.setPos(width-custom.width()-1, 0);

			grid.setRect(x, y, width, height);

		}
		
	}
	
	public static class CatalogTab extends Component{
		
		private RedButton[] itemButtons;
		private static final int NUM_BUTTONS = 2;

		public static int currentItemIdx   = 0;
		private static float[] scrollPositions = new float[NUM_BUTTONS];
		
		private static final int ITEMS_IDX = 0;
		private static final int BESTIARY_IDX = 1;

		private ScrollingGridPane grid;
		
		@Override
		protected void createChildren() {
			itemButtons = new RedButton[NUM_BUTTONS];
			for (int i = 0; i < NUM_BUTTONS; i++){
				final int idx = i;
				itemButtons[i] = new RedButton( "" ){
					@Override
					protected void onClick() {
						currentItemIdx = idx;
						updateList();
					}
				};
				add( itemButtons[i] );
			}
			itemButtons[ITEMS_IDX].icon(new ItemSprite(ItemSpriteSheet.GOLD));
			itemButtons[BESTIARY_IDX].icon(new ItemSprite(ItemSpriteSheet.MOB_HOLDER));

			grid = new ScrollingGridPane(){
				@Override
				public synchronized void update() {
					super.update();
					scrollPositions[currentItemIdx] = content.camera.scroll.y;
				}
			};
			add( grid );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			int perRow = NUM_BUTTONS;
			float buttonWidth = width()/perRow;
			
			for (int i = 0; i < NUM_BUTTONS; i++) {
				itemButtons[i].setRect(x +(i%perRow) * (buttonWidth),
						y + (i/perRow) * (ITEM_HEIGHT ),
						buttonWidth, ITEM_HEIGHT);
				PixelScene.align(itemButtons[i]);
			}
			
			grid.setRect(x,
					itemButtons[NUM_BUTTONS-1].bottom() + 1,
					width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);
		}
		
		public void updateList() {

			if (currentItemIdx < 0 || currentItemIdx >= NUM_BUTTONS) {
				currentItemIdx = ITEMS_IDX;
			}
			
			grid.clear();
			
			for (int i = 0; i < NUM_BUTTONS; i++){
				if (i == currentItemIdx){
					itemButtons[i].icon().color(TITLE_COLOR);
				} else {
					itemButtons[i].icon().resetColor();
				}
			}
			
			grid.scrollTo( 0, 0 );

			if (currentItemIdx == ITEMS_IDX) {
				Catalog catalog = Catalog.MISC_CONSUMABLES;
				grid.addHeader("_" + Messages.get(this, "title_consumables") + "_ ("
						+ catalog.totalSeen() + "/" + catalog.totalItems() + ")", 9, true);
				grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ ("
						+ catalog.totalSeen() + "/" + catalog.totalItems() + "):");
				addGridItems(grid, catalog.items());

			} else {
				int totalItems = 0;
				int totalSeen = 0;
				for (Bestiary bestiary : Bestiary.values()){
					totalItems += bestiary.totalEntities();
					totalSeen += bestiary.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_bestiary") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Bestiary bestiary : Bestiary.values()){
					grid.addHeader("_" + Messages.titleCase(bestiary.title()) + "_ (" + bestiary.totalSeen() + "/" + bestiary.totalEntities() + "):");
					addGridEntities(grid, bestiary.entities());
				}
			}

			grid.setRect(x, itemButtons[NUM_BUTTONS-1].bottom() + 1, width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);

			grid.scrollTo(0, scrollPositions[currentItemIdx]);
		}
		
	}

	//The showcase catalog currently contains only concrete item classes.
	private static void addGridItems( ScrollingGridPane grid, Collection<Class<?>> classes) {
		for (Class<?> itemClass : classes) {

			boolean seen = Catalog.isSeen(itemClass);
			Item item = (Item) Reflection.newInstance(itemClass);
			ItemSprite sprite = new ItemSprite(item.image, seen ? item.glowing() : null);
			String title;
			String desc;

			if (seen) {
				title = Messages.titleCase(item.name());
				desc = item.info();
				if (Catalog.useCount(itemClass) > 1) {
					desc += "\n\n" + Messages.get(
							CatalogTab.class, "gold_count", Catalog.useCount(itemClass));
				}
			} else {
				sprite.lightness(0);
				title = "???";
				desc = Messages.get(CatalogTab.class, "not_seen_item")
						+ "\n\n" + Messages.get(item, "discover_hint");
			}

			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image sprite = new ItemSprite();
						sprite.copy(icon);
						if (ShatteredPixelDungeon.scene() instanceof GameScene){
							GameScene.show(new WndJournalItem(sprite, finalTitle, finalDesc));
						} else {
							ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(sprite, finalTitle, finalDesc));
						}
						return true;
					} else {
						return false;
					}
				}
			};
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	}

	private static void addGridEntities(ScrollingGridPane grid, Collection<Class<?>> classes) {
		for (Class<?> entityCls : classes){

			boolean seen = Bestiary.isSeen(entityCls);
			Mob mob = (Mob) Reflection.newInstance(entityCls);
			CharSprite sprite = mob.sprite();
			sprite.idle();
			Image icon = new Image(sprite);
			String title;
			String desc;

			if (seen) {
				title = Messages.titleCase(mob.name());
				desc = mob.description();
				if (Bestiary.encounterCount(entityCls) > 1) {
					desc += "\n\n" + Messages.get(
							CatalogTab.class, "enemy_count", Bestiary.encounterCount(entityCls));
				}
			} else {
				icon.lightness(0f);
				title = "???";
				desc = Messages.get(CatalogTab.class, "not_seen_enemy")
						+ "\n\n" + Messages.get(mob, "discover_hint");
			}

			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(icon) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image image = seen ? mob.sprite() : new Image(icon);

						if (ShatteredPixelDungeon.scene() instanceof GameScene){
							GameScene.show(new WndJournalItem(image, finalTitle, finalDesc));
						} else {
							ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(image, finalTitle, finalDesc));
						}

						return true;
					} else {
						return false;
					}
				}
			};
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	}

	public static class BadgesTab extends Component {

		private RedButton btnLocal;
		private RedButton btnGlobal;

		private RenderedTextBlock title;

		private Component badgesLocal;
		private Component badgesGlobal;

		public static boolean global = false;

		@Override
		protected void createChildren() {

			if (Dungeon.hero != null) {
				btnLocal = new RedButton(Messages.get(this, "this_run")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = false;
						updateList();
					}
				};
				btnLocal.icon(Icons.BADGES.get());
				add(btnLocal);

				btnGlobal = new RedButton(Messages.get(this, "overall")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = true;
						updateList();
					}
				};
				btnGlobal.icon(Icons.BADGES.get());
				add(btnGlobal);

				if (Badges.filterReplacedBadges(false).size() <= 8){
					badgesLocal = new BadgesList(false);
				} else {
					badgesLocal = new BadgesGrid(false);
				}
				add( badgesLocal );
			} else {
				title = PixelScene.renderTextBlock(Messages.get(this, "title_main_menu"), 9);
				title.hardlight(Window.TITLE_COLOR);
				add(title);
			}

			badgesGlobal = new BadgesGrid(true);
			add( badgesGlobal );
		}

		@Override
		protected void layout() {
			super.layout();

			if (btnLocal != null) {
				btnLocal.setRect(x, y, width / 2, 18);
				btnGlobal.setRect(x + width / 2, y, width / 2, 18);

				badgesLocal.setRect(x, y + 20, width, height-20);
				badgesGlobal.setRect( x, y + 20, width, height-20);
			} else {
				title.setPos( x + (width - title.width())/2, y + (12-title.height())/2);

				badgesGlobal.setRect( x, y + 14, width, height-14);
			}
		}

		private void updateList(){
			if (btnLocal != null) {
				badgesLocal.visible = badgesLocal.active = !global;
				badgesGlobal.visible = badgesGlobal.active = global;

				btnLocal.textColor(global ? Window.WHITE : Window.TITLE_COLOR);
				btnGlobal.textColor(global ? Window.TITLE_COLOR : Window.WHITE);
			} else {
				badgesGlobal.visible = badgesGlobal.active = true;
			}
		}

	}
	
}
