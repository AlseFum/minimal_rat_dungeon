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

package com.shatteredpixel.shatteredpixeldungeon.actors;

/**
 * A proposal of what a Char wants to do this turn, returned by {@link Char#proposeAction()}
 * and executed (or refused) via {@link Char#doAction(ActionSubmission, ActionResult)}.
 *
 * The proposal itself never mutates the world; side effects happen only during execution.
 * {@link #param} holds the intent (a target cell, a target Char, etc.) as decided at
 * proposal time - adjudication may reject it if the world has since changed.
 */
public class ActionSubmission {

	//names of the standard actions. Unknown names are not an error:
	//they adjudicate to OK and the proposing Char handles them in doAction.
	public static final String IDLE       = "IDLE";
	public static final String MOVE       = "MOVE";
	public static final String ATTACK     = "ATTACK";
	public static final String INTERACT   = "INTERACT";
	public static final String PICK_UP    = "PICK_UP";
	public static final String OPEN_CHEST = "OPEN_CHEST";
	public static final String BUY        = "BUY";
	public static final String UNLOCK     = "UNLOCK";
	public static final String MINE       = "MINE";
	public static final String TRANSITION = "TRANSITION";
	public static final String ALCHEMY    = "ALCHEMY";
	public static final String FIRE       = "FIRE"; //used by map devices etc.

	public final String name;
	public final Object param;

	public ActionSubmission( String name, Object param ){
		this.name = name;
		this.param = param;
	}

	public static ActionSubmission idle(){ return new ActionSubmission( IDLE, null ); }

	public static ActionSubmission move( int dst ){ return new ActionSubmission( MOVE, dst ); }

	public static ActionSubmission attack( Char target ){ return new ActionSubmission( ATTACK, target ); }

	public static ActionSubmission interact( Char ch ){ return new ActionSubmission( INTERACT, ch ); }

	@Override
	public String toString() {
		return "ActionSubmission{" + name + ", " + param + '}';
	}
}
