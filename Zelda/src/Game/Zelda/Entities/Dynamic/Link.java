package Game.Zelda.Entities.Dynamic;

import Game.GameStates.Zelda.ZeldaGameState;
import Game.Zelda.Entities.Statics.DungeonDoor;
import Game.Zelda.Entities.Statics.SectionDoor;
import Game.Zelda.Entities.Statics.SolidStaticEntities;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.sun.javafx.scene.EnteredExitedHandler;

import static Game.GameStates.Zelda.ZeldaGameState.worldScale;
import static Game.Zelda.Entities.Dynamic.Direction.DOWN;
import static Game.Zelda.Entities.Dynamic.Direction.UP;

/**
 * Created by AlexVR on 3/15/2020
 */
public class Link extends BaseMovingEntity {

	private final int animSpeed = 120;
	int newMapX = 0, newMapY = 0, xExtraCounter = 0, yExtraCounter = 0, transSpeed = 3, tickCount = 0, lastTime = 0,
			attackCount = 0;
	public boolean movingMap = false, intersecting = false;
	private boolean attacking = false;
	Direction movingTo;

	Animation rightAttackAnimation = new Animation(50, Images.attackRight);
	Animation upAttackAnimation = new Animation(50, Images.attackUp);
	Animation leftAttackAnimation = new Animation(50, Images.attackLeft);
	Animation downAttackAnimation = new Animation(50, Images.attackDown);

	public Link(int x, int y, BufferedImage[] sprite, Handler handler) {
		super(x, y, sprite, handler);
		speed = 4;
		health = 6;
		BufferedImage[] animList = new BufferedImage[2];
		animList[0] = sprite[4];
		animList[1] = sprite[5];

		animation = new Animation(animSpeed, animList);

	}

	@Override
	public void tick() {

		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) || attacking) {
			attacking = true;
			rightAttackAnimation.tick();
			upAttackAnimation.tick();
			downAttackAnimation.tick();
			leftAttackAnimation.tick();
			if (rightAttackAnimation.end) {
				rightAttackAnimation = new Animation(50, Images.attackRight);
				upAttackAnimation = new Animation(50, Images.attackUp);
				leftAttackAnimation = new Animation(50, Images.attackLeft);
				downAttackAnimation = new Animation(50, Images.attackDown);
				attacking = false;
			}
			if (handler.getZeldaGameState().link.bounds.intersects(handler.getZeldaGameState().enemy.bounds)) {
				attackCount += 1;
				if (attackCount > 100) {
					handler.getZeldaGameState().setEnemyalive(false);

				}
			}

		}

		if (movingMap) {
			switch (movingTo) {
			case RIGHT:
				handler.getZeldaGameState().setCameraOffsetX(handler.getZeldaGameState().cameraOffsetX + transSpeed);
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
				handler.getZeldaGameState().setCameraOffsetX(handler.getZeldaGameState().cameraOffsetX - transSpeed);
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
				handler.getZeldaGameState().setCameraOffsetY(handler.getZeldaGameState().cameraOffsetY - transSpeed);
				newMapY += transSpeed;
				if (yExtraCounter > 0) {
					y -= 2 * transSpeed;
					yExtraCounter -= transSpeed;
					animation.tick();

				} else {
					y += transSpeed;
				}
				break;
			case DOWN:
				handler.getZeldaGameState().setCameraOffsetY(handler.getZeldaGameState().cameraOffsetY + transSpeed);
				newMapY -= transSpeed;
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
		} else if (intersecting == false) {
			if (handler.getKeyManager().key_h && (health >= 0 && health < 6) && tickCount > lastTime) {
				health++;
				lastTime = 50;
				tickCount = 0;
			}

			if (handler.getKeyManager().up) {
				if (direction != UP) {
					BufferedImage[] animList = new BufferedImage[2];
					animList[0] = sprites[4];
					animList[1] = sprites[5];
					animation = new Animation(animSpeed, animList);
					direction = UP;
					sprite = sprites[4];
				}
				animation.tick();
				move(direction);

			} else if (handler.getKeyManager().down) {
				if (direction != DOWN) {
					BufferedImage[] animList = new BufferedImage[2];
					animList[0] = sprites[0];
					animList[1] = sprites[1];
					animation = new Animation(animSpeed, animList);
					direction = DOWN;
					sprite = sprites[0];
				}
				animation.tick();
				move(direction);
			} else if (handler.getKeyManager().left) {
				if (direction != Direction.LEFT) {
					BufferedImage[] animList = new BufferedImage[2];
					animList[0] = Images.flipHorizontal(sprites[2]);
					animList[1] = Images.flipHorizontal(sprites[3]);
					animation = new Animation(animSpeed, animList);
					direction = Direction.LEFT;
					sprite = Images.flipHorizontal(sprites[3]);
				}
				animation.tick();
				move(direction);
			} else if (handler.getKeyManager().right) {
				if (direction != Direction.RIGHT) {
					BufferedImage[] animList = new BufferedImage[2];
					animList[0] = (sprites[2]);
					animList[1] = (sprites[3]);
					animation = new Animation(animSpeed, animList);
					direction = Direction.RIGHT;
					sprite = (sprites[3]);
				}
				animation.tick();
				move(direction);
			} else {
				moving = false;
			}
		}
		tickCount++;
		if (tickCount > 100) {
			tickCount = 0;
		}
		
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_G)&&health>0) {
			health--;
		}
	}

	@Override
	public void render(Graphics g) {

		if (moving && !attacking) {
			g.drawImage(animation.getCurrentFrame(), x, y, width, height, null);

		}
		if (attacking && handler.getZeldaGameState().isSwordEquipped()) {
			switch (direction) {
			case RIGHT:
				g.drawImage(rightAttackAnimation.getCurrentFrame(), x, y,
						rightAttackAnimation.getCurrentFrame().getWidth() * handler.getZeldaGameState().worldScale,
						height, null);
				break;
			case UP:
				g.drawImage(upAttackAnimation.getCurrentFrame(), x, y - upAttackAnimation.getCurrentFrame().getHeight(),
						width, upAttackAnimation.getCurrentFrame().getHeight() * handler.getZeldaGameState().worldScale,
						null);
				break;
			case LEFT:
				g.drawImage(leftAttackAnimation.getCurrentFrame(), x - leftAttackAnimation.getCurrentFrame().getWidth(),
						y, leftAttackAnimation.getCurrentFrame().getWidth() * handler.getZeldaGameState().worldScale,
						height, null);
				break;
			case DOWN:
				g.drawImage(downAttackAnimation.getCurrentFrame(), x, y, width,
						downAttackAnimation.getCurrentFrame().getHeight() * handler.getZeldaGameState().worldScale,
						null);
				break;
			}
		} else {
			if (movingMap) {
				g.drawImage(animation.getCurrentFrame(), x, y, width, height, null);
			}
			g.drawImage(sprite, x, y, width, height, null);
		}
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
						ZeldaGameState.inCave = false;
						x = ((DungeonDoor) objects).nLX;
						y = ((DungeonDoor) objects).nLY;
						direction = DOWN;
						handler.getZeldaGameState().setEnemyalive(true);
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
							ZeldaGameState.inCave = true;
							x = ((DungeonDoor) objects).nLX;
							y = ((DungeonDoor) objects).nLY;
							direction = UP;
							handler.getZeldaGameState().setEnemyalive(false);
						}
					}
				} else if (!(objects instanceof SectionDoor) && intersecting == false
						&& ((objects.bounds.intersects(interactBounds) || (handler.getZeldaGameState().link.bounds
								.intersects(handler.getZeldaGameState().enemy.bounds) && handler.getZeldaGameState().isEnemyalive())))) {

					if (direction == UP && handler.getZeldaGameState().link.bounds
							.intersects(handler.getZeldaGameState().enemy.bounds)) {
						direction = DOWN;
						intersecting = true;
						move(direction);

					}
					if (direction == DOWN && handler.getZeldaGameState().link.bounds
							.intersects(handler.getZeldaGameState().enemy.bounds)) {
						direction = UP;
						intersecting = true;
						move(direction);

					}
					if (direction == Direction.RIGHT && handler.getZeldaGameState().link.bounds
							.intersects(handler.getZeldaGameState().enemy.bounds)) {
						direction = Direction.LEFT;
						intersecting = true;
						move(direction);

					}
					if (direction == Direction.LEFT && handler.getZeldaGameState().link.bounds
							.intersects(handler.getZeldaGameState().enemy.bounds)) {
						direction = Direction.RIGHT;
						intersecting = true;
						move(direction);

					}
					intersecting = false;
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
