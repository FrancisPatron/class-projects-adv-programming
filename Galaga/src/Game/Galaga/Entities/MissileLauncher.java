package Game.Galaga.Entities;

import Main.Handler;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Created by AlexVR on 1/25/2020
 */
public class MissileLauncher extends BaseEntity {

    EntityManager enemies;
    int speed = 1;

    public MissileLauncher(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite, handler);
        this.enemies=enemies;
    }

    @Override
    public void tick() {
            super.tick();
            if(y!=handler.getHeight()-100) {
            	y += speed;
            }   
            bounds.y = y;
                if (handler.getGalagaState().entityManager.playerShip.bounds.intersects(new Rectangle(this.x-20,this.y-20,this.bounds.width+40,bounds.height+40))) {
                    remove = true;
                    handler.getMusicHandler().playEffect("GunUpgrade.wav");
                    Missile.equipped=true;
                
            }
        }
    }

