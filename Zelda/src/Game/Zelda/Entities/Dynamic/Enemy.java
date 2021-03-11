package Game.Zelda.Entities.Dynamic;

import Game.GameStates.Zelda.ZeldaGameState;
import Game.Zelda.Entities.Statics.DungeonDoor;
import Game.Zelda.Entities.Statics.SectionDoor;
import Game.Zelda.Entities.Statics.SolidStaticEntities;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;

import java.awt.image.BufferedImage;
import java.util.Random;

import static Game.GameStates.Zelda.ZeldaGameState.worldScale;
import static Game.Zelda.Entities.Dynamic.Direction.DOWN;
import static Game.Zelda.Entities.Dynamic.Direction.UP;

/**
 * Created by AlexVR on 3/15/2020
 */
public class Enemy extends BaseMovingEntity {

	private final int animSpeed = 120;
	int newMapX = 0, newMapY = 0, xExtraCounter = 0, yExtraCounter = 0, transSpeed = 3, tickCount = 0, lastTime = 0,
			option = -1;
	public boolean movingMap = false;
	Direction movingTo;

	Animation rightAttackAnimation = new Animation(50, Images.attackRight);
	Animation upAttackAnimation = new Animation(50, Images.attackUp);
	Animation leftAttackAnimation = new Animation(50, Images.attackLeft);
	Animation downAttackAnimation = new Animation(50, Images.attackDown);

	public Enemy(int x, int y, BufferedImage[] sprite, Handler handler) {
		super(x, y, sprite, handler);
		speed = 4;
		BufferedImage[] animList = new BufferedImage[2];
		animList[0] = sprite[0];
		animList[1] = sprite[1];

		animation = new Animation(animSpeed, animList);

	}

	@Override
	public void tick() {
		if (handler.getZeldaGameState().link.bounds.intersects(handler.getZeldaGameState().enemy.bounds)) {
			
		}

		if (handler.getZeldaGameState().link.movingMap) {
			switch (handler.getZeldaGameState().link.movingTo) {
			case RIGHT:

				newMapX += transSpeed;
				if (xExtraCounter > 0) {
					x += 2 * transSpeed;
					xExtraCounter -= transSpeed;
					animation.tick();

				} else {
					x -= transSpeed;
				}
				break;
			case LEFT:

				newMapX -= transSpeed;
				if (xExtraCounter > 0) {
					x -= 2 * transSpeed;
					xExtraCounter -= transSpeed;
					animation.tick();

				} else {
					x += transSpeed;
				}
				break;
			case UP:

				if (yExtraCounter > 0) {
					y -= 2 * transSpeed;
					yExtraCounter -= transSpeed;
					animation.tick();

				} else {
					y += transSpeed;
				}
				break;
			case DOWN:

				if (yExtraCounter > 0) {
					y += 2 * transSpeed;
					yExtraCounter -= transSpeed;
					animation.tick();
				} else {
					y -= transSpeed;
				}
				break;
			}
			bounds = new Rectangle(x, y, width, height);
			changeIntersectingBounds();
			if (newMapX >= -2 && newMapX <= 2 && newMapY >= -1 && newMapY <= 1) {
				movingMap = false;
				movingTo = null;
				newMapX = 0;
				newMapY = 0;
			}

			////////////////////////////////////////////////////////////////////////////////////////

		} else {
			Random number = new Random();
			option = number.nextInt(4);
			int speedRandom = number.nextInt(31);
			if (speedRandom != 0) {
				speed = speedRandom;
			}

			if (option == 3 && tickCount > lastTime ) {

				if (direction != UP) {
					direction = UP;
				}

				animation.tick();
				move(direction);

				lastTime = 30;
				tickCount = 0;

			} else if (option == 2 && tickCount > lastTime ) {

				if (direction != DOWN) {
					direction = DOWN;
				}

				animation.tick();
				move(direction);

				lastTime = 30;
				tickCount = 0;

			} else if (option == 1 && tickCount > lastTime ) {

				if (direction != Direction.LEFT) {
					direction = Direction.LEFT;
				}

				animation.tick();
				move(direction);

				lastTime = 30;
				tickCount = 0;

			} else if (option == 0 && tickCount > lastTime  ) {
				if (direction != Direction.RIGHT) {
					direction = Direction.RIGHT;

				}
				animation.tick();
				move(direction);

				lastTime = 30;
				tickCount = 0;
			}
		}
		tickCount++;

	}

	@Override
	public void render(Graphics g) {

		g.drawImage(animation.getCurrentFrame(), x, y, width, height, null);

		g.drawImage(sprite, x, y, width, height, null);

	}

	@Override
	public void move(Direction direction) {
		moving = true;
		changeIntersectingBounds();
		// chack for collisions
		if (ZeldaGameState.inCave) {
			for (SolidStaticEntities objects : handler.getZeldaGameState().caveObjects) {
				if ((objects instanceof DungeonDoor) && objects.bounds.intersects(bounds)
						&& direction == ((DungeonDoor) objects).direction) {
					if (((DungeonDoor) objects).name.equals("caveStartLeave")) {

					}
				} else if (!(objects instanceof DungeonDoor) && objects.bounds.intersects(interactBounds)) {
					// dont move
					return;
				}
			}
		} else {
			for (SolidStaticEntities objects : handler.getZeldaGameState().objects.get(handler.getZeldaGameState().mapX)
					.get(handler.getZeldaGameState().mapY)) {
				if ((objects instanceof SectionDoor) && objects.bounds.intersects(bounds)
						&& direction == ((SectionDoor) objects).direction) {
					if (!(objects instanceof DungeonDoor)) {
						movingMap = true;
						movingTo = ((SectionDoor) objects).direction;
						switch (((SectionDoor) objects).direction) {
						case RIGHT:
							newMapX = -(((handler.getZeldaGameState().mapWidth) + 1) * worldScale);
							newMapY = 0;
							handler.getZeldaGameState().mapX++;
							xExtraCounter = 8 * worldScale + (2 * worldScale);
							break;
						case LEFT:
							newMapX = (((handler.getZeldaGameState().mapWidth) + 1) * worldScale);
							newMapY = 0;
							handler.getZeldaGameState().mapX--;
							xExtraCounter = 8 * worldScale + (2 * worldScale);
							break;
						case UP:
							newMapX = 0;
							newMapY = -(((handler.getZeldaGameState().mapHeight) + 1) * worldScale);
							handler.getZeldaGameState().mapY--;
							yExtraCounter = 8 * worldScale + (2 * worldScale);
							break;
						case DOWN:
							newMapX = 0;
							newMapY = (((handler.getZeldaGameState().mapHeight) + 1) * worldScale);
							handler.getZeldaGameState().mapY++;
							yExtraCounter = 8 * worldScale + (2 * worldScale);
							break;
						}
						return;
					} else {
						if (((DungeonDoor) objects).name.equals("caveStartEnter")) {
							//
						}
					}
					// dont move
				} else if (!(objects instanceof SectionDoor) && (objects.bounds.intersects(interactBounds)
						|| handler.getZeldaGameState().link.bounds.intersects(handler.getZeldaGameState().enemy.bounds ))){
					// dont move
					return;
				}
				
			}

		}
		switch (direction) {
		case RIGHT:
			x += speed;
			break;
		case LEFT:
			x -= speed;

			break;
		case UP:
			y -= speed;
			break;
		case DOWN:
			y += speed;

			break;
		}
		bounds.x = x;
		bounds.y = y;
		changeIntersectingBounds();

	}
}
