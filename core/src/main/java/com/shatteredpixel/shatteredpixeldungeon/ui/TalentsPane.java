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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.TalentLayer;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class TalentsPane extends ScrollPane {

	ArrayList<TalentTierPane> panes = new ArrayList<>();
	ArrayList<ColorBlock> separators = new ArrayList<>();

	ColorBlock sep;
	ColorBlock blocker;
	RenderedTextBlock blockText;

	public TalentsPane( TalentButton.Mode mode ) {
		this( mode, Dungeon.hero.talentLayers.values(), null );
	}

	public TalentsPane( TalentButton.Mode mode, Collection<Hero.TalentLayerState> states ) {
		this( mode, states, null );
	}

	/**
	 * @param filter 可选：只显示通过谓词的层（INFO 预览用；UPGRADE 模式传 null）
	 */
	public TalentsPane( TalentButton.Mode mode, Collection<Hero.TalentLayerState> states, Predicate<TalentLayer> filter ) {
		super(new Component());

		Ratmogrify.useRatroicEnergy = Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify;

		for (Hero.TalentLayerState state : states){
			if (filter != null && !filter.test(state.layer)) continue;

			TalentTierPane pane = new TalentTierPane(state, mode);
			panes.add(pane);
			content.add(pane);

			ColorBlock sep = new ColorBlock(0, 1, 0xFF000000);
			separators.add(sep);
			content.add(sep);
		}

		sep = new ColorBlock(0, 1, 0xFF000000);
		content.add(sep);

		blocker = new ColorBlock(0, 0, 0xFF222222);
		content.add(blocker);

		//INFO 预览 + 过滤模式：显示未解锁层的提示
		if (mode == TalentButton.Mode.INFO && filter != null && panes.size() < states.size()){
			blockText = PixelScene.renderTextBlock(Messages.get(this, "unlock_tier" + (panes.size() + 1)), 6);
			content.add(blockText);
		} else {
			blockText = null;
		}

		for (int i = panes.size()-1; i >= 0; i--){
			content.bringToFront(panes.get(i));
		}
	}

	@Override
	protected void layout() {
		super.layout();

		float top = 0;
		for (int i = 0; i < panes.size(); i++){
			top += 2;
			panes.get(i).setRect(x, top, width, 0);
			top = panes.get(i).bottom();

			separators.get(i).x = 0;
			separators.get(i).y = top + 2;
			separators.get(i).size(width, 1);

			top += 3;

		}

		float bottom;
		if (blockText != null) {
			bottom = Math.max(height, top + 20);

			blocker.x = 0;
			blocker.y = top;
			blocker.size(width, bottom - top);

			blockText.maxWidth((int) width);
			blockText.align(RenderedTextBlock.CENTER_ALIGN);
			blockText.setPos((width - blockText.width()) / 2f, blocker.y + (bottom - blocker.y - blockText.height()) / 2);
		} else {
			bottom = Math.max(height, top);

			blocker.visible = false;
		}

		content.setSize(width, bottom);
	}

	public static class TalentTierPane extends Component {

		private Hero.TalentLayerState state;

		public RenderedTextBlock title;
		ArrayList<TalentButton> buttons;

		ArrayList<Image> stars = new ArrayList<>();
		IconButton random;

		public TalentTierPane(Hero.TalentLayerState state, TalentButton.Mode mode){
			super();

			this.state = state;
			TalentLayer layer = state.layer;

			title = PixelScene.renderTextBlock(Messages.titleCase(layer.title()), 9);
			title.hardlight(Window.TITLE_COLOR);
			add(title);

			if (mode == TalentButton.Mode.UPGRADE) {
				setupStars();
				if (Dungeon.hero.talentPointsAvailable(layer) > 0){

					random = new IconButton(Icons.SHUFFLE.get()){
						@Override
						protected void onClick() {
							super.onClick();
							GameScene.show(new WndOptions(
									Icons.SHUFFLE.get(),
									Messages.get(TalentsPane.class, "random_title"),
									Messages.get(TalentsPane.class, "random_sure"),
									Messages.get(TalentsPane.class, "random_yes"),
									Messages.get(TalentsPane.class, "random_one"),
									Messages.get(TalentsPane.class, "random_no")) {
								@Override
								protected void onSelect(int index) {
									super.onSelect(index);
									//safety check to ensure previous UI is still there
									if (TalentTierPane.this.parent == null){
										return;
									}
									if (index == 0 || index == 1){
										while (Dungeon.hero.talentPointsAvailable(layer) > 0){
											TalentButton button = Random.element(buttons);
											if (Dungeon.hero.pointsInTalent(button.talent) < button.talent.maxPoints()){
												button.upgradeTalent();
												if (index == 1){
													break;
												}
											}
										};
										setupStars();
										TalentTierPane.this.layout();
									}
								}
							});
						};

						@Override
						public void update() {
							if (Dungeon.hero.lvl >= 3 && Statistics.qualifiedForRandomVictoryBadge){
								icon.tint(1, 1, 1, (float)Math.abs(Math.cos(1.5f*Math.PI*Game.timeTotal)/2f));
							}
							super.update();
						}
					};
					add(random);
				}
			}

			buttons = new ArrayList<>();
			for (Talent talent : state.points.keySet()){
				TalentButton btn = new TalentButton(layer, talent, state.points.get(talent), mode){
					@Override
					public void upgradeTalent() {
						super.upgradeTalent();
						if (parent != null) {
							setupStars();
							TalentTierPane.this.layout();
						}
					}
				};
				buttons.add(btn);
				add(btn);
			}

		}

		//该层点数池的星级跨度（封顶 - 起始）
		private int regStars(){
			int[] th = state.layer.levelThresholds();
			return th[th.length - 1] - th[0];
		}

		private void setupStars(){
			if (!stars.isEmpty()){
				for (Image im : stars){
					im.killAndErase();
				}
				stars.clear();
			}

			int totStars = regStars() + Dungeon.hero.bonusTalentPoints(state.layer);
			int openStars = Dungeon.hero.talentPointsAvailable(state.layer);
			int usedStars = Dungeon.hero.talentPointsSpent(state.layer);
			for (int i = 0; i < totStars; i++){
				Image im = new Speck().image(Speck.STAR);
				stars.add(im);
				add(im);
				if (i >= openStars && i < (openStars + usedStars)){
					im.tint(0.75f, 0.75f, 0.75f, 0.9f);
				} else if (i >= (openStars + usedStars)){
					im.tint(0f, 0f, 0f, 0.9f);
				}
			}

			if (random != null && openStars == 0){
				random.killAndErase();
				random.destroy();
				random = null;
			}
		}

		@Override
		protected void layout() {
			super.layout();

			int regStars = regStars();

			float titleWidth = title.width();
			titleWidth += 2 + Math.min(stars.size(), regStars)*6;
			title.setPos(x + (width - titleWidth)/2f, y);

			float left = title.right() + 2;

			float starTop = title.top();
			if (regStars < stars.size()) starTop -= 2;

			for (Image star : stars){
				star.x = left;
				star.y = starTop;
				PixelScene.align(star);
				left += 6;
				regStars--;
				if (regStars == 0){
					starTop += 6;
					left = title.right() + 2;
				}
			}

			if (random != null){
				random.setRect(width - 16, y-2, 16, 14);
			}

			float gap = (width - buttons.size()*TalentButton.WIDTH)/(buttons.size()+1);
			left = x + gap;
			for (TalentButton btn : buttons){
				btn.setPos(left, title.bottom() + 4);
				PixelScene.align(btn);
				left += btn.width() + gap;
			}

			height = buttons.get(0).bottom() - y;

		}

	}
}
