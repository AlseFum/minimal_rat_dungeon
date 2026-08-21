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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Honeypot extends Item {
	
	public static final String AC_SHATTER	= "SHATTER";
	
	{
		image = ItemSpriteSheet.HONEYPOT;

		defaultAction = AC_THROW;
		usesTargeting = true;

		stackable = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_SHATTER );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_SHATTER )) {
			
			hero.sprite.zap( hero.pos );
			
			detach( hero.belongings.backpack );
			Catalog.countUse(getClass());

			Item item = shatter( hero, hero.pos );
			if (!item.collect()){
				Dungeon.level.drop(item, hero.pos);
				if (item instanceof ShatteredPot){
					((ShatteredPot) item).dropPot(hero, hero.pos);
				}
			}

			hero.next();

		}
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.pit[cell]) {
			super.onThrow( cell );
		} else {
			Catalog.countUse(getClass());
			Dungeon.level.drop(shatter( null, cell ), cell);
		}
	}
	
	public Item shatter( Char owner, int pos ) {
		
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Splash.at( pos, 0xffd500, 5 );
		}
		
		int newPos = pos;
		if (Actor.findChar( pos ) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			
			for (int n : PathFinder.NEIGHBOURS4) {
				int c = pos + n;
				if (!Dungeon.level.solid[c] && Actor.findChar( c ) == null) {
					candidates.add( c );
				}
			}
	
			newPos = candidates.size() > 0 ? Random.element( candidates ) : -1;
		}
		
		if (newPos != -1) {
			//Honeypot summons the single Rat class; CHARMER preserves the old
			//honeyed ally/enemy interaction remains on the shared Rat class.
			Rat bee = Rat.of(Rat.Variant.CHARMER);
			bee.setPotInfo(pos, owner);
			bee.HP = bee.HT;
			bee.pos = newPos;
			
			GameScene.add( bee );
			if (newPos != pos) Actor.add( new Pushing( bee, pos, newPos ) );

			bee.sprite.alpha( 0 );
			bee.sprite.parent.add( new AlphaTweener( bee.sprite, 1, 0.15f ) );
			
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			return new ShatteredPot();
		} else {
			return this;
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	//The bee's broken 'home', all this item does is let its bee know where it is, and who owns it (if anyone).
	public static class ShatteredPot extends Item {

		{
			image = ItemSpriteSheet.SHATTPOT;
			stackable = true;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			if ( super.doPickUp(hero, pos) ){
				pickupPot( hero );
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void doDrop(Hero hero) {
			super.doDrop(hero);
			dropPot(hero, hero.pos);
		}

		@Override
		protected void onThrow(int cell) {
			super.onThrow(cell);
			dropPot(curUser, cell);
		}

		public void pickupPot(Char holder){
			for (Rat rat : findRats(holder.pos)) {
				updateRat(rat, -1, holder);
			}
		}
		
		public void dropPot( Char holder, int dropPos ) {
			for (Rat rat : findRats(holder)) {
				updateRat(rat, dropPos, null);
			}
		}

		public void movePot( int oldpos, int movePos) {
			for (Rat rat : findRats(oldpos)) {
				updateRat(rat, movePos, null);
			}
		}

		public void destroyPot( int potPos ) {
			for (Rat rat : findRats(potPos)) {
				updateRat(rat, -1, null);
			}
		}

		private void updateRat(Rat rat, int cell, Char holder) {
			if (rat != null && rat.variant() == Rat.Variant.CHARMER
					&& rat.alignment == Char.Alignment.ENEMY) {
				rat.setPotInfo(cell, holder);
			}
		}

		private ArrayList<Rat> findRats(int cell) {
			ArrayList<Rat> rats = new ArrayList<>();
			for (Char character : Actor.chars()) {
				if (character instanceof Rat && ((Rat) character).potPos() == cell) {
					rats.add((Rat) character);
					if (rats.size() >= quantity) break;
				}
			}
			return rats;
		}

		private ArrayList<Rat> findRats(Char holder) {
			ArrayList<Rat> rats = new ArrayList<>();
			if (holder == null) return rats;
			for (Char character : Actor.chars()) {
				if (character instanceof Rat && ((Rat) character).potHolderID() == holder.id()) {
					rats.add((Rat) character);
					if (rats.size() >= quantity) break;
				}
			}
			return rats;
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public int value() {
			return 5 * quantity;
		}
	}
}
