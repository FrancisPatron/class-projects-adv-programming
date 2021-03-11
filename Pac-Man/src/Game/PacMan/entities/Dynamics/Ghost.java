package Game.PacMan.entities.Dynamics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import Game.PacMan.entities.Statics.BaseStatic;
import Game.PacMan.entities.Statics.BoundBlock;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

public class Ghost extends BaseDynamic{
	protected double velX,velY,speed,ghostSpeed;
	public int facing; //up
	public boolean moving = true,turnFlag = false,home=true,blinking=false;
	private Random random;
	int turnCooldown = 30;
	public Animation ghostblink;
	public int countdown = 60*5;
	public int getouttimer;


	public Ghost(int x, int y, int width, int height, Handler handler, BufferedImage ghostImage, double speed) {
		super(x, y, width, height, handler, ghostImage);
		random = new Random();
		this.ghostSpeed=speed;
		ghostblink = new Animation(128,Images.ghostblink);



	}
	@Override
	public void tick() {	

		if(x==342&&y==342) {
			home=true;
		}		
		if(handler.getPacManState().getBlinking() && countdown>0) {
			ghostblink.tick();
			countdown--;
		}else {
			handler.getPacManState().setBlinking(false);
			countdown=60*5;
		}

		leaveHome();

		if (handler.getPacman().getDeathEvent()) {
			home = true;
			this.speed = 0.0;

		}
		if (!home) {
			switch (facing){
			case 0: //Right
				x+=speed;
				break;
			case 1: //Left
				x-=speed;
				break;
			case 2: //up
				y-=speed;
				break;
			case 3: //down
				y+=speed;
				break;
			}
			if (facing==0 || facing==1){
				checkHorizontalCollision();

			}else{
				checkVerticalCollisions();


			}
		}
	}


	public void checkVerticalCollisions() {	


		Ghost ghost = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		ArrayList<BaseDynamic> enemies = handler.getMap().getEnemiesOnMap();

		boolean ghostDies = false;
		boolean toUp = moving && facing==2;

		Rectangle ghostBounds = toUp ? ghost.getTopBounds() : ghost.getBottomBounds();

		velY = speed;
		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toUp ? brick.getTopBounds() : brick.getBottomBounds();
				if (ghostBounds.intersects(brickBounds)) {
					velY = 0;
					if (toUp)
						ghost.setY(brick.getY() + ghost.getDimension().height);
					else
						ghost.setY(brick.getY() - brick.getDimension().height);
				}
			}if(velY==0) {
				facing = random.nextInt(2);
			}
		}



		for(BaseDynamic enemy : enemies){
			Rectangle enemyBounds = !toUp ? enemy.getTopBounds() : enemy.getBottomBounds();
			if (ghostBounds.intersects(enemyBounds)) {
				ghostDies = true;
				break;
			}
		}
	}


	public boolean checkPreVerticalCollisions(int facing) {
		Ghost ghost = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();


		boolean toUp = moving && facing==2;

		Rectangle ghostBounds = toUp ? ghost.getTopBounds() : ghost.getBottomBounds();

		velY = speed;
		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toUp ? brick.getTopBounds() : brick.getBottomBounds();
				if (ghostBounds.intersects(brickBounds)) {
					return false;
				}
			}
		}
		return true;

	}



	public void checkHorizontalCollision(){

		Ghost ghost = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		ArrayList<BaseDynamic> enemies = handler.getMap().getEnemiesOnMap();
		velX = speed;
		boolean ghostDies = false;
		boolean toRight = moving && facing==0;

		Rectangle ghostBounds = toRight ? ghost.getRightBounds() : ghost.getLeftBounds();

		for(BaseDynamic enemy : enemies){
			Rectangle enemyBounds = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
			if (ghostBounds.intersects(enemyBounds)) {
				facing = 1;
				ghostDies = true;
				break;
			}
		}

		if(ghostDies) {
			//handler.getMap().reset();
		}else {

			for (BaseStatic brick : bricks) {
				if (brick instanceof BoundBlock) {
					Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
					if (ghostBounds.intersects(brickBounds)) {
						velX = 0;				
						if (toRight)
							ghost.setX(brick.getX() - ghost.getDimension().width);
						else
							ghost.setX(brick.getX() + brick.getDimension().width);
					}if(velX==0) {		
						facing = random.nextInt(2)+2;

					}
				}
			}
		}
	}


	public boolean checkPreHorizontalCollision(int facing){
		Ghost ghost = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		velX = speed;
		boolean toRight = moving && facing==0;

		Rectangle ghostBounds = toRight ? ghost.getRightBounds() : ghost.getLeftBounds();

		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
				if (ghostBounds.intersects(brickBounds)) {
					return false;
				}
			}
		}
		return true;

	}


	public double getVelX() {
		return velX;
	}
	public double getVelY() {
		return velY;
	}
	public void setSpeed(double x) {
		speed = x;
	}

	private void leaveHome() {	
		if (home) {;
		if(getouttimer>0) {
			getouttimer--;
		}else {
			speed=1;
				home=false;
				this.speed = ghostSpeed;
				facing=2;
				getouttimer = 60*(random.nextInt(10));			
			}
		}
	}
}


