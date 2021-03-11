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

public class EnemyBee extends BaseEntity {
	
	EntityManager enemies;
    int playerY;
	int row,col;//row 3-4, col 0-7
    boolean justSpawned=true,attacking=false, positioned=false,hit=false,centered = false,allignedX=false;
    Animation idle,turn90Left;
    int spawnPos;//0 is left 1 is top, 2 is right, 3 is bottom
    int formationX,formationY,speed,centerCoolDown=60;
    int timeAlive=0;
    int x,y;
    int attacktimer = random.nextInt(10)*60;
    int attacktimerdeath=0;
    double playerX = handler.getGalagaState().entityManager.playerShip.bounds.getCenterX();
  
    
    public EnemyBee(int col,int row,int x, int y, int width, int height, Handler handler,EntityManager enemies) {	
        super(x, y, width, height, Images.galagaEnemyBee[0], handler);
        this.enemies=enemies;        
        this.row=row;
        this.col=col;
        
        BufferedImage[] idleAnimList= new BufferedImage[2];
        idleAnimList[0] = Images.galagaEnemyBee[0];
        idleAnimList[1] = Images.galagaEnemyBee[1];
        idle = new Animation(512,idleAnimList);
        turn90Left = new Animation(128,Images.galagaEnemyBee);
        spawn();
        speed = 4;
        formationX=(handler.getWidth()/4)+(col*((handler.getWidth()/2)/8))+30;
        formationY=(row*(handler.getHeight()/10))+8;
    }

    private void spawn() {
        spawnPos = random.nextInt(3);
        switch (spawnPos){
            case 0://left          	
                x = (handler.getWidth()/4)-width;
                y = random.nextInt(handler.getHeight()-handler.getHeight()/8);
                break;
            case 1://top
                x = random.nextInt((handler.getWidth()-handler.getWidth()/2))+handler.getWidth()/4;
                y = -height;
                break;
            case 2://right
                x = (handler.getWidth()/2)+ width + (handler.getWidth()/4);
                y = random.nextInt(handler.getHeight()-handler.getHeight()/8);
                break;                
        	    
        }
        bounds.x=x;
        bounds.y=y;
    }
    
    
    @SuppressWarnings("unused")
	@Override
    public void tick() {
        super.tick();
        idle.tick();
        if (positioned==true) {
        	attacktimer--;
        }
        if (hit){
            if (enemyDeath.end){

                PlayerShip.beetimer=random.nextInt(10)*60;
            	PlayerShip.AvilableSpotsList.add(new Point(col,row));
                remove = true;      
                return;
            }
            enemyDeath.tick();
        }
        if (justSpawned){
            timeAlive++;
            if (!centered && Point.distance(x,y,handler.getWidth()/2,handler.getHeight()/2)>speed){//reach center of screen
                switch (spawnPos){
                    case 0://left
                        x+=speed;
                        if (Point.distance(x,y,x,handler.getHeight()/2)>speed) {
                            if (y > handler.getHeight() / 2) {
                                y -= speed;
                            } else {
                                y += speed;
                            }
                        }
                        break;
                    case 1://top
                        y+=speed;
                        if (Point.distance(x,y,handler.getWidth()/2,y)>speed) {
                            if (x > handler.getWidth() / 2) {
                                x -= speed;
                            } else {
                                x += speed;
                            }
                        }
                        break;
                    case 2://right
                        x-=speed;
                        if (Point.distance(x,y,x,handler.getHeight()/2)>speed) {
                            if (y > handler.getHeight() / 2) {
                                y -= speed;
                            } else {
                                y += speed;
                            }
                        }
                        break;
                    case 3://down
                        y-=speed;
                        if (Point.distance(x,y,handler.getWidth()/2,y)>speed) {
                            if (x > handler.getWidth() / 2) {
                                x -= speed;
                            } else {
                                x += speed;
                            }
                        }
                        break;
                }
                if (timeAlive>=60*6){
                    //more than 6secs in this state then die
                    //60 ticks in a second, times 60 is a minute, times 2 is a minute
                    remove = true;
                    PlayerShip.AvilableSpotsList.add(new Point(col,row));
                }

            }else {//move to formation
                
                if (!centered){
                    centered = true;
                    timeAlive = 0;
                }
                if ((centerCoolDown<=0)){
                    if (Point.distance(x, y, formationX, formationY) > speed) {//reach center of screen
                        if (Math.abs(y-formationY)>6) {
                            y -= speed;
                        }
                        if (Point.distance(x,y,formationX,y)>speed/2) {
                            if (x >formationX) {
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
                if (timeAlive>=60*60*2){
                    //more than 2 minutes in this state then die
                    //60 ticks in a second, times 60 is a minute, times 2 is a minute
                	PlayerShip.AvilableSpotsList.add(new Point(col,row));
                    remove = true;
                }
            }
        }else if ((positioned==true)&&(attacktimer==0)){
        	attacking=true;
        	
        }else if (attacking==true){        	
        	attack();
        	}
        	      
        bounds.x=x;
        bounds.y=y;
        
    }

    @Override
    public void render(Graphics g) {
    	if (handler.DEBUG==true) {
        ((Graphics2D)g).draw(new Rectangle(formationX,formationY,32,32));
    	}
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
        if (damageSource instanceof PlayerLaser){
            hit=true;
            handler.getMusicHandler().playEffect("PlayerShipExplosion.wav");
            damageSource.remove = true;     
            handler.getScoreManager().setGalagaCurrentScore(handler.getScoreManager().getGalagaCurrentScore()+100);
            if (handler.getScoreManager().getGalagaCurrentScore()>handler.getScoreManager().getGalagaHighScore()){
            	handler.getScoreManager().setGalagaHighScore(handler.getScoreManager().getGalagaCurrentScore());
      
            }
        }
       
    } 
   	
    private void attack() {
        attacktimerdeath++;
        if ((attacktimerdeath >= 60*6)) {
            PlayerShip.AvilableSpotsList.add(new Point(col, row));
            remove = true;
        }
            if(!PlayerShip.gameLost&&!PlayerShip.gameWon&&!handler.getGalagaState().entityManager.playerShip.destroyed) {
         double Distance = Point.distance(handler.getGalagaState().entityManager.playerShip.bounds.getCenterX(), 0, this.bounds.getCenterX(), 0);


    	if ((this.bounds.getCenterX()==handler.getGalagaState().entityManager.playerShip.bounds.getCenterX())||Distance<=speed) {
    		allignedX=true;    		
    	}
    	if ((bounds.getCenterX()< handler.getGalagaState().entityManager.playerShip.bounds.getCenterX())&&(allignedX==false)&&(hit==false)){
    		x+=speed;
    		y+=speed;    		
    	}
    	if ((bounds.getCenterX()>handler.getGalagaState().entityManager.playerShip.bounds.getCenterX())&&(allignedX==false)&&(hit==false)){
    		x-=speed;
    		y+=speed;   		
    	} else {
    		y+=speed;    		
    	}
    	
    }	
    }
} 



