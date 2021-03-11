
package Game.Galaga.Entities;

import Main.Handler;

import Resources.Animation;
import Resources.Images;
import javafx.scene.shape.Line;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import java.util.Arrays;


public class Boss extends BaseEntity {
	
	EntityManager enemies;
    boolean justSpawned=true,attacking=false, positioned=false,hit=false,centered = false;
    Animation idle,turn90Left;
    int formationX,formationY,speed,centerCoolDown=60*4;
    int timeAlive=0;
    static int x,y;
   int attacktype;
    int formationChangezTimer=60*10;
  public static int attacktimer=60;
  public static int formationIndex=0;
  private Point formation1 = new Point(handler.getWidth()/4+100, handler.getHeight()/8);
  private Point formarion2 = new Point(handler.getWidth()/2-100,handler.getHeight()/2);
  private Point formation3 = new Point(handler.getWidth()-handler.getWidth()/4-300,handler.getHeight()/8);
  public Point[] formations = new Point[] {formation1,formarion2,formation3};
    public Boss(int x, int y, int width, int height, Handler handler) {	
        super(x, y, width, height, Images.galagaEnemyBoss, handler);
        
        BufferedImage[] idleAnimList= new BufferedImage[2];
        idleAnimList[0] = Images.galagaEnemyBoss;
        idleAnimList[1] = Images.galagaEnemyBoss;
        idle = new Animation(512,idleAnimList);
        turn90Left = new Animation(128,Images.galagaEnemyBee);
        spawn();
        speed = 1;
        
     
    }


	private void spawn() {
        x=0;
        y=handler.getHeight()/2;
        bounds.x=x;
        bounds.y=y;
    }
    
    
    @SuppressWarnings("unused")
	@Override
    public void tick() {
        super.tick();
        idle.tick();
        if (hit){
            if (enemyDeath.end){ 
            	remove=true;
                return;
            }
            enemyDeath.tick();
        }
        if (justSpawned){
            timeAlive++;
            if (!centered && Point.distance(x,y,handler.getWidth()/2-100,handler.getHeight()/2)>speed){//reach center of screen
              x+=speed;


            }else {//move to formation
                
                if (!centered){
                    centered = true;
                    timeAlive = 0;
                }
                if ((centerCoolDown<=0)){
                    if (Point.distance(x, y, formations[formationIndex].x, formations[formationIndex].y) > speed) {//reach center of screen
                        if ((y>formations[formationIndex].y)) {
                            y-= speed;
                        }
                        if ((y<formations[formationIndex].y)) {
                            y+= speed;
                        }
                        
                        
                        if (Point.distance(x,y,formations[formationIndex].x,y)>speed/2) {
                            if (x >formations[formationIndex].x) {
                                x -= speed;
                            } else {
                                x += speed;
                            }
                        }
                    }else{
                        positioned =true;
                        justSpawned = false;
                    }
                }else{
                    centerCoolDown--;
                }

            }
        } else if(positioned==true){
        	 formationChangezTimer--;
        	 if (formationChangezTimer<=0) {
        		 justSpawned=true;
        		 formationChangezTimer=60*10;
        		if (formationIndex==2) {
        		formationIndex=0;
        		} else {        		
        		formationIndex++;
        			}          	
        	 }        	
        }
        bounds.x=x;
        bounds.y=y;
        
    }

    @Override
    public void render(Graphics g) {
        if (arena.contains(bounds)) {
           if (hit){
                g.drawImage(enemyDeath.getCurrentFrame(), x, y, width, height, null);
            }else{
                g.drawImage(idle.getCurrentFrame(), x, y, width, height, null);

            }
        }
    }

    @Override
    public void damage(BaseEntity damageSource) {
        super.damage(damageSource);
        if (damageSource instanceof Missile&&PlayerShip.killBoss==true){
        	hit=true;
            handler.getMusicHandler().playEffect("PlayerShipExplosion.wav");
            damageSource.remove = true;   
            PlayerShip.gameWon=true;

        }
       
    } 	
    
    
    public static int getX() {
    	return x;
    }

    public static int getFormationIndex() {
    	return getFormationIndex();
    }
    
    public static Rectangle getBounds() {
    	return new Rectangle(x,y,200,200);
    }

} 




