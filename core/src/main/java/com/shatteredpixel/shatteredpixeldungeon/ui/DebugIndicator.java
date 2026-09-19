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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDebugActions;
import com.watabou.noosa.Image;
import com.watabou.utils.DeviceCompat;

/**
 * HUD 上的调试入口（同 {@link ActionIndicator} 那一路 Tag）：INDEV 版本常驻在
 * 右侧标签列的顶端，点一下开 {@link WndDebugActions}，动作平铺，一步到位。
 */
public class DebugIndicator extends Tag {

	private Image icon;

	public DebugIndicator() {
		//紫色：一眼能看出这不是游戏本体的 UI
		super(0x9370DB);

		setSize( SIZE, SIZE );

		visible = DeviceCompat.isDebug();
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		icon = Icons.get( Icons.MAGNIFY );
		add( icon );
	}

	@Override
	protected void layout() {
		super.layout();

		if (!flipped)   icon.x = x + (SIZE - icon.width()) / 2f + 1;
		else            icon.x = x + width - (SIZE + icon.width()) / 2f - 1;
		icon.y = y + (height - icon.height) / 2f;
		PixelScene.align(icon);
	}

	@Override
	protected void onClick() {
		super.onClick();
		GameScene.show( new WndDebugActions() );
	}

	@Override
	protected String hoverText() {
		return Messages.titleCase(Messages.get(this, "hover"));
	}
}
