package Game.Galaga.Entities;

import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.stream.Collectors;

import javax.swing.text.DefaultEditorKit.BeepAction;

import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.swing.internal.plaf.basic.resources.basic;

import java.util.*;
/**
 * Created by AlexVR on 1/25/2020
 */
public class PlayerShip extends BaseEntity{


		public static boolean BeeCountDown = false;
		public static boolean NewBeeCountDown = false;


		public static int gunUpgrade = 0;
		public static int laserspeed = 0;
		public static int gunUpgraded = 0;
		public static boolean gameLost = false;
		public static boolean gameWon = false;
		public static boolean killBoss=false;
		private static boolean MissileLauncherSpawned=false;
		public int DificultyMultiplier = 1;
		EntityManager enemies;
		private int health = 3,attackCooldown = 30,speed =6,destroyedCoolDown = 60*7;
		private boolean attacking = false;
    	public boolean destroyed = false;
    	private Animation deathAnimation;
  //EnemyBee available Formations
    	public static Point[] AvilableSpots = new Point[] {new Point(0,3),new Point(1,3),new Point(2,3),new Point(3,3),new Point(4,3),new Point(5,3),new Point(6,3),new Point(7,3),new Point(0,4),new Point(1,4),new Point(2,4),new Point(3,4),new Point(4,4),new Point(5,4),new Point(6,4),new Point(7,4)};
  	 	public static List<Point> AvilableSpotsList = Arrays.stream(AvilableSpots).collect(Collectors.toCollection(ArrayList::new));  
  //NewEnemy available Formations
  	 	public static Point[] AvilableSpotsNewEnemy = new Point[] {new Point(0,1),new Point(1,1),new Point(2,1),new Point(3,1),new Point(4,1),new Point(5,1),new Point(6,1),new Point(0,2),new Point(1,2),new Point(2,2),new Point(3,2),new Point(4,2),new Point(5,2),new Point(6,2)};
  	 	public static List<Point> AvilableSpotsListNewEnemy = Arrays.stream(AvilableSpotsNewEnemy).collect(Collectors.toCollection(ArrayList::new));
  //Timers For Enemies Spawns And Laser Attack
  	 	public static int beetimer = 0;
  	   	public static int newenemytimer = 0;
  		int startingformationtimer = 0;
  		public static int laserattacktimer=0;
   //IndexesToMoveThroughLists
  		private int beeindex;
  		private int newenemyindex;
   //Bossrelated		
  		public static boolean enemybossSpawnable = true;
  		int BossBattletimer = 60;
  		
  		
  		
     public PlayerShip(int x, int y, int width, int height, BufferedImage sprite, Handler handler) {
        super(x, y, width, height, sprite, handler);

        deathAnimation = new Animation(256,Images.galagaPlayerDeath);
    }
     
    @Override
    public void tick() {
        super.tick();
        //timers
        if (!gameLost&&!gameWon) {
			startingformationtimer++;
        	beetimer--;
			newenemytimer--;
			laserattacktimer++;
        	if (beetimer==0&&AvilableSpotsList.size()>0) {
				SpawnBee();
			}
			if (newenemytimer==0&&AvilableSpotsListNewEnemy.size()>0) {
				SpawnBee2();
			}
        BossAttacks();
        laserAttack();

        //Starting Formation
        if(startingformationtimer<60*5) {
        	if((AvilableSpotsList.size()>0)) {
        		SpawnBee();
        	}
        	if ((AvilableSpotsListNewEnemy.size()>0)) {
        		SpawnBee2();
            }
        }
        
        //This Creates Random Enemy Waves
        if (startingformationtimer>=(random.nextInt(20)+20)*60){
        	startingformationtimer = 0;
        }

        //Spawn Enemies With Keys
        if((handler.getKeyManager().keyJustPressed(KeyEvent.VK_P)&&(AvilableSpotsList.size()>0))) {
        	SpawnBee();
        }
        if((handler.getKeyManager().keyJustPressed(KeyEvent.VK_O)&&(AvilableSpotsListNewEnemy.size()>0))) {
        	SpawnBee2();
        }
        
        
        //Missile Launcher
        if (handler.getScoreManager().getGalagaCurrentScore()>=10000&&enemybossSpawnable){        	
        	handler.getGalagaState().entityManager.entities.add(new Boss(0, 0,200, 200, handler)); 
        	handler.getMusicHandler().startMusic("EnemyBossMusic.wav");
        	enemybossSpawnable=false;
        } if (handler.getScoreManager().getGalagaCurrentScore()>=50000&&!MissileLauncherSpawned) {
        	handler.getGalagaState().entityManager.entities.add(new MissileLauncher(handler.getWidth()/2, 0, 100, 40, Images.MissileLauncher));                                                              
        	MissileLauncherSpawned=true;
        }
        
        //Game Over Flag
        if(health<1) {
        	gameLost=true;
        }
        
        //This is PlayerShip related
        if (destroyed){
        	if(destroyedCoolDown==60*7) {
        		handler.getMusicHandler().playEffect("PlayerShipExplosion.wav");
        		health--;
        	}
            if (destroyedCoolDown<=0){
                destroyedCoolDown=60*7;
                destroyed=false;
                deathAnimation.reset();
                bounds.x=x;
            }else{
                deathAnimation.tick();
                destroyedCoolDown--;
            }
        }else {
            if (attacking) {
                if (attackCooldown <= 0) {
                    attacking = false;
                } else {
                    attackCooldown--;
                	}
            }    //Player laser attacks                
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) && !attacking) {
                handler.getMusicHandler().playEffect("laser.wav");
                attackCooldown = 30;
                attacking = true;                
                handler.getGalagaState().entityManager.entities.add(new PlayerLaser(this.x + (width/2) , this.y - 3, width / 5, height / 2, Images.galagaPlayerLaser, handler, handler.getGalagaState().entityManager));
                
                if (handler.getScoreManager().getGalagaCurrentScore()>=3000) { 
                	if (gunUpgrade == 0) {
                		handler.getMusicHandler().playEffect("GunUpgrade.wav");
                		gunUpgrade++;
                	}
                	handler.getGalagaState().entityManager.entities.add(new PlayerLaser(this.x + (width), this.y - 3, width / 5, height / 2, Images.galagaPlayerLaser, handler, handler.getGalagaState().entityManager));
                	handler.getGalagaState().entityManager.entities.add(new PlayerLaser(this.x, this.y - 3, width / 5, height / 2, Images.galagaPlayerLaser, handler, handler.getGalagaState().entityManager));
                }
                if (handler.getScoreManager().getGalagaCurrentScore()>=8000) {
                	if (gunUpgrade == 1) {
                		handler.getMusicHandler().playEffect("GunUpgrade.wav");
                		gunUpgrade++;
                	}
                	attackCooldown = 15;               	
                }
                if (handler.getScoreManager().getGalagaCurrentScore()>=15000) {
                	if (gunUpgrade == 2) {
                		handler.getMusicHandler().playEffect("GunUpgrade.wav");
                		gunUpgrade++;
                }
                	laserspeed = 6;
                
                }
                
                }
            }
        	//MISSILE ATTACK
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_SPACE)&&Missile.equipped==true) {
            	handler.getGalagaState().entityManager.entities.add(new Missile(this.x, this.y - 100, 50, 100, Images.Missile, handler, handler.getGalagaState().entityManager));
            	handler.getMusicHandler().playEffect("MissileSoundEffect.wav");
            	killBoss=true;
            	Missile.equipped=false;
            }	
           
            //MOVEMENT OF PLAYER
            if ((handler.getKeyManager().left)&&(x > arena.x+2)&&(!destroyed)) {
                x -= (speed);
            }
            if ((handler.getKeyManager().right)&&(x + this.width < arena.x+arena.width)&&(!destroyed)) {
                x += (speed);
            }

            if ((handler.getKeyManager().keyJustPressed(KeyEvent.VK_L))&&(health<3)) {
            	health++;
            }
            
            if ((handler.getKeyManager().keyJustPressed(KeyEvent.VK_K))) {
            	destroyed = true;
            }
            
            
            //Debugging toggle
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_B)){
            	if (handler.DEBUG==false) {
            	handler.DEBUG=true;
            	} else {
            		handler.DEBUG=false;
            	}
            }
                                    
            bounds.x = x;
        }
        
        
        
    }
    @Override
    public void render(Graphics g) {
         if (destroyed){
             if (deathAnimation.end){
                 g.drawString("READY",handler.getWidth()/2-handler.getWidth()/12,handler.getHeight()/2);
             }else {
                 g.drawImage(deathAnimation.getCurrentFrame(), x, y, width, height, null);
             }
         }else {
             super.render(g);
         }
    }

    @Override
    public void damage(BaseEntity damageSource) {
        if (damageSource instanceof PlayerLaser){
            return;
        }
        destroyed = true;
        bounds.x = -10;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
    
    
	public void SpawnBee2() {
		newenemyindex = random.nextInt(AvilableSpotsListNewEnemy.size());
    	handler.getGalagaState().entityManager.entities.add(new NewEnemy(AvilableSpotsListNewEnemy.get(newenemyindex).x,AvilableSpotsListNewEnemy.get(newenemyindex).y,0,0,32,32, handler,enemies));
    	newenemytimer=0;
    	AvilableSpotsListNewEnemy.remove(newenemyindex);		
	}
	public void SpawnBee() {
			beeindex = random.nextInt(AvilableSpotsList.size());
			handler.getGalagaState().entityManager.entities.add(new EnemyBee(AvilableSpotsList.get(beeindex).x, AvilableSpotsList.get(beeindex).y, 0, 0, 32, 32, handler, enemies));
			beetimer = 0;
			AvilableSpotsList.remove(beeindex);

	}
	public void laserAttack() {
		Point laserlocation = AvilableSpotsNewEnemy[random.nextInt(14)];
		if ((laserattacktimer>=(random.nextInt(3)+1)*60)&&!destroyed&&(startingformationtimer>8*60)) {
    		     		              
	    	if (!AvilableSpotsListNewEnemy.contains(laserlocation)) {
	    		handler.getMusicHandler().playEffect("laser2.wav");
	    		handler.getGalagaState().entityManager.entities.add(new NewEnemyLaser((((handler.getWidth()/4)+(laserlocation.x*((handler.getWidth()/2)/8))+8+75+16)), ((laserlocation.y*(handler.getHeight()/10))+8), width / 5, height / 2, Images.galagaEnemyLaser, handler, handler.getGalagaState().entityManager));
	    		laserattacktimer=0;
	    	
	    	}else {
	    		laserattacktimer=0;
	    	} 
    	
		}	
	}
	
	public void BossAttacks() {
		
		if((handler.getScoreManager().getGalagaCurrentScore()>=10000)&&Boss.formationIndex==1&&destroyed==false) {
			BossBattletimer--;
			if (BossBattletimer<=0) {
			
			handler.getGalagaState().entityManager.entities.add(new NewEnemyLaser(Boss.x+100, Boss.y+100, width / 5, height / 2, Images.galagaEnemyLaser, handler, handler.getGalagaState().entityManager));
			BossBattletimer=10;
			handler.getMusicHandler().playEffect("laser2.wav");
			}
		}
		if((handler.getScoreManager().getGalagaCurrentScore()>=10000)&&Boss.formationIndex==0&&destroyed==false) {
				if((AvilableSpotsList.size()>0)) {
	        		SpawnBee();
	        	}
	        	if ((AvilableSpotsListNewEnemy.size()>0)) {
	        		SpawnBee2();
			}
		}
		
		if((handler.getScoreManager().getGalagaCurrentScore()>=10000)&&Boss.formationIndex==2&&destroyed==false) {
			if((AvilableSpotsList.size()>0)) {
        		SpawnBee();
        	}
        	if ((AvilableSpotsListNewEnemy.size()>0)) {
        		SpawnBee2();
            }       	
		
		}
	}
		
	
	

    	
    
}
