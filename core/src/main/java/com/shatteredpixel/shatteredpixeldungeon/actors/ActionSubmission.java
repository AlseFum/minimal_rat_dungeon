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

	/**
	 * The verdict of {@link Actor#midAction(Char, ActionSubmission)} on a proposed action:
	 * either OK, or a failure with a specific {@link FailCause}.
	 *
	 * Failure does not automatically consume time - only what the proposing phase already
	 * spent (by default half a turn) is lost. Handling the failure is up to the Char.
	 */
	public static class ActionResult {

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
}
