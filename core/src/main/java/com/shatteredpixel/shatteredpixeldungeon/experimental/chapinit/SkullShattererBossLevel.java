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
 */

package com.shatteredpixel.shatteredpixeldungeon.experimental.chapinit;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.RatKingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.noosa.audio.Music;

import java.util.ArrayList;

/**
 * 初始章节第五层的 BOSS 层：结构沿用下水道 BOSS 层（入口/出口房、图八布局、
 * 封层与解锁流程），只把中间的 GooBossRoom 换成 {@link SkullBossRoom}。
 */
public class SkullShattererBossLevel extends SewerBossLevel {

	@Override
	protected ArrayList<Room> initRooms() {

		ArrayList<Room> initRooms = new ArrayList<>();

		initRooms.add(roomEntrance = new SewerBossEntranceRoom());
		initRooms.add(roomExit = new SewerBossExitRoom());

		int standards = standardRooms(true);
		for (int i = 0; i < standards; i++) {
			StandardRoom s = StandardRoom.createRoom();
			//force to normal size
			s.setSizeCat(0, 0);
			initRooms.add(s);
		}

		SkullBossRoom bossRoom = new SkullBossRoom();
		initRooms.add(bossRoom);
		((FigureEightBuilder)builder).setLandmarkRoom(bossRoom);

		initRooms.add(new RatKingRoom());

		return initRooms;
	}

	@Override
	public void playLevelMusic() {
		if (locked) {
			Music.INSTANCE.play(Assets.Music.SEWERS_BOSS, true);
			return;
		}

		boolean bossAlive = false;
		for (Mob m : mobs) {
			if (m instanceof SkullShatterer) {
				bossAlive = true;
				break;
			}
		}

		if (bossAlive) {
			Music.INSTANCE.end();
		} else {
			Music.INSTANCE.playTracks(SewerLevel.SEWER_TRACK_LIST, SewerLevel.SEWER_TRACK_CHANCES, false);
		}
	}
}
