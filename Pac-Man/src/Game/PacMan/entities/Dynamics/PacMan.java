package Game.PacMan.entities.Dynamics;

import Game.PacMan.entities.Statics.BaseStatic;
import Game.PacMan.entities.Statics.BoundBlock;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class PacMan extends BaseDynamic{

	protected double velX,velY,speed = 1;
	private int health = 3;
	public String facing = "Left";
	public boolean moving = true,turnFlag = false;
	public Animation leftAnim,rightAnim,upAnim,downAnim,deathAnimation;
	int turnCooldown = 15;
	private boolean deathEvent = false;
	private boolean ghostdeath;
	private int deathTimer = 60*2;


	public PacMan(int x, int y, int width, int height, Handler handler) {
		super(x, y, width, height, handler, Images.pacmanRight[0]);
		leftAnim = new Animation(128,Images.pacmanLeft);
		rightAnim = new Animation(128,Images.pacmanRight);
		upAnim = new Animation(128,Images.pacmanUp);
		downAnim = new Animation(128,Images.pacmanDown);
		deathAnimation = new Animation(128, Images.deathAnimation);
	}

	@Override
	public void tick(){
		checkPacmanHit();
		if (deathEvent&&health>0) {
			pacmanDeath();
		}

		if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_N)&&health<3) {
			health++;
		}

		switch (facing){
		case "Right":
			x+=velX;
			rightAnim.tick();
			break;
		case "Left":
			x-=velX;
			leftAnim.tick();
			break;
		case "Up":
			y-=velY;
			upAnim.tick();
			break;
		case "Down":
			y+=velY;
			downAnim.tick();
			break;
		}
		if (turnCooldown<=0){
			turnFlag= false;
		}
		if (turnFlag){
			turnCooldown--;
		}

		if ((!deathEvent)&&(handler.getKeyManager().keyJustPressed(KeyEvent.VK_RIGHT)  || handler.getKeyManager().keyJustPressed(KeyEvent.VK_D)) && !turnFlag && checkPreHorizontalCollision("Right")){
			facing = "Right";
			turnFlag = true;
			turnCooldown = 20;
		}else if ((!deathEvent)&&(handler.getKeyManager().keyJustPressed(KeyEvent.VK_LEFT) || handler.getKeyManager().keyJustPressed(KeyEvent.VK_A)) && !turnFlag&& checkPreHorizontalCollision("left")){
			facing = "Left";
			turnFlag = true;
			turnCooldown = 20;
		}else if ((!deathEvent)&&(handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP)  ||handler.getKeyManager().keyJustPressed(KeyEvent.VK_W)) && !turnFlag&& checkPreVerticalCollisions("Up")){
			facing = "Up";
			turnFlag = true;
			turnCooldown = 20;
		}else if ((!deathEvent)&&(handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN)  || handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)) && !turnFlag&& checkPreVerticalCollisions("Down")){
			facing = "Down";
			turnFlag = true;
			turnCooldown = 20;
		}

		if (facing.equals("Right") || facing.equals("Left")){
			checkHorizontalCollision();
		}else{
			checkVerticalCollisions();
		}

	}
	public void pacmanDeath() {
		deathTimer--;
		facing="Dead";
		if (deathTimer<=0) {
			deathAnimation.tick();
			if (deathTimer==0) {
				handler.getMusicHandler().playEffect("pacman_death.wav");
			}
			if (deathAnimation.end) {
				handler.getMap().reset();         
				health--;
				deathTimer=60*2;
				deathEvent=false;
				facing="Left";
				deathAnimation = new Animation(128, Images.deathAnimation); 
			}
		}
	}



	public void checkPacmanHit() {
		PacMan pacman = this;
		ArrayList<BaseDynamic> enemies = handler.getMap().getEnemiesOnMap();
		boolean toRight = facing.equals("Right");
		boolean toUp = facing.equals("Up");
		Rectangle pacmanBoundsX = toRight ? pacman.getRightBounds() : pacman.getLeftBounds();
		Rectangle pacmanBoundsY = toUp ? pacman.getTopBounds() : pacman.getBottomBounds();      
		for(BaseDynamic enemy : enemies){
			Rectangle enemyBoundsY = !toUp ? enemy.getTopBounds() : enemy.getBottomBounds();
			Rectangle enemyBoundsX = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
			if (((!deathEvent)&&handler.getPacManState().getBlinking()==false && (pacmanBoundsX.intersects(enemyBoundsX)||pacmanBoundsY.intersects(enemyBoundsY)))||handler.getKeyManager().keyJustPressed(KeyEvent.VK_P)) {            
				deathEvent = true;
				break;
			}else if(((!deathEvent)&&handler.getPacManState().getBlinking()==true && (pacmanBoundsX.intersects(enemyBoundsX)||pacmanBoundsY.intersects(enemyBoundsY)))) {
				ghostdeath=true;
				handler.getScoreManager().addPacmanCurrentScore(500);
				enemy.x = 342;
				enemy.y = 342;

			}
		}
	}

	public void checkVerticalCollisions() {
		PacMan pacman = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		ArrayList<BaseDynamic> enemies = handler.getMap().getEnemiesOnMap();
		boolean toUp = moving && facing.equals("Up");

		Rectangle pacmanBounds = toUp ? pacman.getTopBounds() : pacman.getBottomBounds();

		velY = speed;
		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toUp ? brick.getTopBounds() : brick.getBottomBounds();
				if (pacmanBounds.intersects(brickBounds)) {
					velY = 0;
					if (toUp)
						pacman.setY(brick.getY() + pacman.getDimension().height);
					else
						pacman.setY(brick.getY() - brick.getDimension().height);
				}
			}
		}

		for(BaseDynamic enemy : enemies){
			Rectangle enemyBounds = !toUp ? enemy.getTopBounds() : enemy.getBottomBounds();
			if (pacmanBounds.intersects(enemyBounds)) {       
				break;
			}
		}    
	}

	public boolean checkPreVerticalCollisions(String facing) {
		PacMan pacman = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();

		boolean toUp = moving && facing.equals("Up");

		Rectangle pacmanBounds = toUp ? pacman.getTopBounds() : pacman.getBottomBounds();

		velY = speed;
		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toUp ? brick.getTopBounds() : brick.getBottomBounds();
				if (pacmanBounds.intersects(brickBounds)) {
					return false;
				}
			}
		}
		return true;
	}

	public void checkHorizontalCollision(){
		PacMan pacman = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		velX = speed;
		boolean toRight = moving && facing.equals("Right");

		Rectangle pacmanBounds = toRight ? pacman.getRightBounds() : pacman.getLeftBounds();
		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
				if (pacmanBounds.intersects(brickBounds)) {
					velX = 0;
					if (toRight)
						pacman.setX(brick.getX() - pacman.getDimension().width);
					else
						pacman.setX(brick.getX() + brick.getDimension().width);
				}
			}
		}
	}

	public boolean checkPreHorizontalCollision(String facing){
		PacMan pacman = this;
		ArrayList<BaseStatic> bricks = handler.getMap().getBlocksOnMap();
		velX = speed;
		boolean toRight = moving && facing.equals("Right");

		Rectangle pacmanBounds = toRight ? pacman.getRightBounds() : pacman.getLeftBounds();

		for (BaseStatic brick : bricks) {
			if (brick instanceof BoundBlock) {
				Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
				if (pacmanBounds.intersects(brickBounds)) {
					return false;
				}
			}
		}
		return true;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int x) {
		this.health = x;
	}
	public double getVelX() {
		return velX;
	}
	public double getVelY() {
		return velY;
	}

	public boolean getDeathEvent() {
		return deathEvent;
	}
}