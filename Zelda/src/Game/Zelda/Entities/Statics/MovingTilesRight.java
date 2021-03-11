package Game.Zelda.Entities.Statics;

import Game.GameStates.Zelda.ZeldaMMGameState;
import Game.Zelda.Entities.MMBaseEntity;
import Game.Zelda.Entities.Dynamic.Direction;
import Game.Zelda.Entities.Dynamic.MMLink;
import Main.Handler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by AlexVR on 3/14/2020
 */
public class MovingTilesRight extends MMBaseEntity {
	public MovingTilesRight (int x, int y, BufferedImage sprite, Handler handler) {
		super(x, y, sprite,handler);
		bounds = new Rectangle(x ,y ,width,height);
	}

	@Override
	public void tick() {
		if (((ZeldaMMGameState)handler.getState()).map.link.interactBounds.intersects(bounds)){
			int tilesLenght = checkAdjacentTiles(x, y);
			moveLink(Direction.RIGHT, tilesLenght);
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(sprite,x ,y,width,height,null);
	}

	public boolean isAMoveTile(int x, int y) {
		for(MMBaseEntity z : ((ZeldaMMGameState)handler.getState()).map.getBlocksOnMap()) {
			if(z instanceof MovingTilesRight) {
				if (z.x==x&&z.y==y) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void moveLink(Direction direction,int distance) {
		if (this.x + this.width +  distance*this.width != ((ZeldaMMGameState)handler.getState()).map.link.y) {
		((ZeldaMMGameState)handler.getState()).map.link.move(direction);
		}
	}
	
	public int checkAdjacentTiles(int x, int y) { //recursive method
		int number = 1;
		if (isAMoveTile(x+2*this.width,y)) {
			number++;
			checkAdjacentTiles(x+2*this.width, y);
			return number;
		} else {
			return number;
		}
		
				
	}
}
