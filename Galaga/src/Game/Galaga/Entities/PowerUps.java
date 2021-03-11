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

public class PowerUps extends BaseEntity {
	
	EntityManager enemies;
    int playerY;
	int row,col;//row 3-4, col 0-7
    boolean justSpawned=true,attacking=false, positioned=false,hit=false,centered = false,allignedX=false;
    Animation idle,turn90Left;
    int spawnPos;//0 is left 1 is top, 2 is right, 3 is bottom
    int formationX,formationY,speed,centerCoolDown=60;
    int timeAlive=0;
    int x,y;
    int attacktimer = 0;
    int attacktimerdeath=0;
    double playerX = handler.getGalagaState().entityManager.playerShip.bounds.getCenterX();
  
    
    public PowerUps(int x, int y, int width, int height, Handler handler,EntityManager enemies) {	
        super(x, y, width, height, Images.galagaPowerUps, handler);
        this.enemies=enemies;        
        
        BufferedImage[] idleAnimList= new BufferedImage[0];
//        idleAnimList[0] = Images.galagaPowerUps;
//        idleAnimList[1] = Images.galagaPowerUps;
        idle = new Animation(512,idleAnimList);
        spawn();
        speed = 6;     
    }

    private void spawn() {
          		y=0;
                x = random.nextInt(handler.getWidth()/2+handler.getWidth()/4);                        	            
        bounds.x=x;
        bounds.y=y;
    }
    
    
    @SuppressWarnings("unused")
	@Override
    public void tick() {
        super.tick();
        idle.tick();
        if (handler.getGalagaState().entityManager.playerShip.bounds.intersects(new Rectangle(this.x-20,this.y-20,80,80))) {
        	remove=true;
        	Random rand = new Random();   
            int powerSelection = rand.nextInt(5);
            switch(powerSelection){		
            	case 1:
            		handler.getMusicHandler().playEffect("achieve.wav");
            		powerUp1();break;
            	case 2:
            		handler.getMusicHandler().playEffect("achieve.wav");
            		powerUp2();break;
            	
            	default:
            		handler.getMusicHandler().playEffect("badLucky.wav");
            		System.out.println("No PowerUps.");break;
            }
        	
        }
        if (justSpawned){
           y+=speed; 
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
                g.drawImage(Images.galagaPowerUps, x, y, width, height, null);

            }
        }
    }

    @Override
    public void damage(BaseEntity damageSource) {
        if (handler.getGalagaState().entityManager.playerShip.bounds.intersects(new Rectangle(this.x-20,this.y-20,80,80))) {
        	hit=true;
            Random rand = new Random();   
            int powerSelection = rand.nextInt(5);
            switch(powerSelection){		
            	case 1:
            		handler.getMusicHandler().playEffect("achieve.wav");
            		powerUp1();break;
            	case 2:
            		handler.getMusicHandler().playEffect("achieve.wav");
            		powerUp2();break;
            	
            	default:
            		handler.getMusicHandler().playEffect("badLucky.wav");
            		break;
            }   
        }
       
    } 
    private void powerUp1() {
		//Bonus points
		handler.getScoreManager().setGalagaCurrentScore(handler.getScoreManager().getGalagaCurrentScore()+250);
    	if (handler.getScoreManager().getGalagaCurrentScore()>handler.getScoreManager().getGalagaHighScore()){
        	handler.getScoreManager().setGalagaHighScore(handler.getScoreManager().getGalagaCurrentScore());
        }
		
		
	}private void powerUp2() {
		//Give 1 life or 150 bonus points if have all health points
		if(handler.getGalagaState().entityManager.playerShip.getHealth() == 3) {
			handler.getScoreManager().setGalagaCurrentScore(handler.getScoreManager().getGalagaCurrentScore()+250);
			if (handler.getScoreManager().getGalagaCurrentScore()>handler.getScoreManager().getGalagaHighScore()){
	        	handler.getScoreManager().setGalagaHighScore(handler.getScoreManager().getGalagaCurrentScore());
	        	}
		}
		else{
			handler.getGalagaState().entityManager.playerShip.setHealth(handler.getGalagaState().entityManager.playerShip.getHealth() + 1);
		}
	}


    		
    
} 
