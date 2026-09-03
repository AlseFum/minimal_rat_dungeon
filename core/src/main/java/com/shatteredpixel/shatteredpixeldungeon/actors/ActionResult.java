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
 * The verdict of {@link Actor#midAction(Char, ActionSubmission)} on a proposed action:
 * either OK, or a failure with a specific {@link FailCause}.
 *
 * Failure does not automatically consume time - only what the proposing phase already
 * spent (by default half a turn) is lost. Handling the failure is up to the Char.
 */
public class ActionResult {

	public static final ActionResult OK = new ActionResult( null );

	public final FailCause cause; //null when the action is OK

	private ActionResult( FailCause cause ){
		this.cause = cause;
	}

	public boolean isOk(){ return cause == null; }

	public static ActionResult fail( FailCause cause ){
		return new ActionResult( cause );
	}

	@Override
	public String toString() {
		return isOk() ? "OK" : "Fail(" + cause + ')';
	}
}
