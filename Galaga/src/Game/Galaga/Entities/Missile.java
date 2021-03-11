package Game.Galaga.Entities;

import Main.Handler;

import java.awt.image.BufferedImage;


public class Missile extends BaseEntity {

    EntityManager enemies;
    int speed = 1;
    public static boolean equipped = false;
    public Missile(int x, int y, int width, int height, BufferedImage sprite, Handler handler,EntityManager enemies) {
        super(x, y, width, height, sprite, handler);
        this.enemies=enemies;
        
    }

    @Override
    public void tick() {
        if (!remove) {
            super.tick();
            y -= speed;
            bounds.y = y;
            for (BaseEntity enemy : enemies.entities) {
                if (enemy instanceof PlayerShip || enemy instanceof PlayerLaser) {
                    continue;
                }
                if (Boss.getBounds().intersects(bounds)) {
                    enemy.damage(this);
                    PlayerShip.gameWon=true;
                }
                else if (!arena.getBounds().contains(this.bounds)) {
                	PlayerShip.gameLost=true;
                }
            }
        }
    }
}
