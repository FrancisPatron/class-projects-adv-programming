package Game.GameStates;

import Game.Galaga.Entities.Boss;
import Game.Galaga.Entities.EnemyBee;
import Game.Galaga.Entities.EntityManager;
import Game.Galaga.Entities.Missile;
import Game.Galaga.Entities.NewEnemy;
import Game.Galaga.Entities.NewEnemyLaser;
import Game.Galaga.Entities.PlayerShip;
import Game.Galaga.Entities.PowerUps;
import Main.Handler;
import Resources.Animation;
import Resources.Images;
import Resources.MusicHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.Timer;


/**
 * Created by AlexVR on 1/24/2020.
 */
public class GalagaState extends State {
	
	EntityManager enemies;
	Random random = new Random();
    public EntityManager entityManager;
    public String Mode = "Menu";
    private Animation titleAnimation;
    public int selectPlayers = 1;
    public int startCooldown = 60*7;//seven seconds for the music to finish  
    int explosioncountdown = 131*60;
    private int gameendcountdown = 60*6;
    private int PowerUpTimer = (random.nextInt(10)+10)*60;

    public GalagaState(Handler handler){
        super(handler);
        refresh();
        entityManager = new EntityManager(new PlayerShip(handler.getWidth()/2-64,handler.getHeight()- handler.getHeight()/7,64,64,Images.galagaPlayer[0],handler));
        titleAnimation = new Animation(256,Images.galagaLogo);
    }


    @Override
    public void tick() {
        PowerUpTimer--;
    	if(PlayerShip.enemybossSpawnable&&PowerUpTimer<=0) {
    		handler.getGalagaState().entityManager.entities.add(new PowerUps(200, 200, 40, 40, handler, enemies));
            PowerUpTimer = (random.nextInt(10)+10)*60;
    	}
    	if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_H)){
    	    if(handler.getScoreManager().getGalagaCurrentScore()>=10000){
                handler.getScoreManager().setGalagaCurrentScore(handler.getScoreManager().getGalagaCurrentScore()+50000);
                if (handler.getScoreManager().getGalagaCurrentScore()>handler.getScoreManager().getGalagaHighScore()) {
                    handler.getScoreManager().setGalagaHighScore(handler.getScoreManager().getGalagaCurrentScore());
                }
                } else {

                    handler.getScoreManager().setGalagaCurrentScore(handler.getScoreManager().getGalagaCurrentScore() + 3000);
                    if (handler.getScoreManager().getGalagaCurrentScore() > handler.getScoreManager().getGalagaHighScore()) {
                        handler.getScoreManager().setGalagaHighScore(handler.getScoreManager().getGalagaCurrentScore());
                    }
                }

        }
        if (Mode.equals("Stage")){
            if (startCooldown<=0) {
                entityManager.tick();
            }else{
                startCooldown--;
            }
        }else{
            titleAnimation.tick();
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP)){
                selectPlayers=1;
            }else if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN)){
                selectPlayers=2;
            }
            if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER)){
                Mode = "Stage";
                handler.getMusicHandler().playEffect("Galaga.wav");
                
            }

        	        	
        }
        if (handler.getScoreManager().getGalagaCurrentScore()>=10000) {
        	explosioncountdown--;
        	if (explosioncountdown<=0&&!PlayerShip.gameWon) {
        		PlayerShip.gameLost=true;
        	}
        }                       

    }

    @Override
    public void render(Graphics g) {
    	Color Light_Blue = new Color(51,153,255);
        g.setColor(Light_Blue);
        g.fillRect(0,0,handler.getWidth(),handler.getHeight());
        g.setColor(Color.BLACK);
        g.fillRect(handler.getWidth()/4,0,handler.getWidth()/2,handler.getHeight());
        Random random = new Random(System.nanoTime());

        for (int j = 1;j < random.nextInt(15)+60;j++) {
            switch (random.nextInt(6)) {
                case 0:
                    g.setColor(Color.RED);
                    break;
                case 1:
                    g.setColor(Color.BLUE);
                    break;
                case 2:
                    g.setColor(Color.YELLOW);
                    break;
                case 3:
                    g.setColor(Color.GREEN);
                    break;
                case 4:
                	g.setColor(Color.MAGENTA); //added from phase1
                	break;
                case 5:
                	g.setColor(Color.WHITE);   //added from phase1
                	break;
            }
            int randX = random.nextInt(handler.getWidth() - handler.getWidth() / 2) + handler.getWidth() / 4;
            int randY = random.nextInt(handler.getHeight());
            g.fillRect(randX, randY, 2, 2);

        }
        if (Mode.equals("Stage")) {
            g.setColor(Color.BLUE);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 62));
            g.drawString("HIGH",handler.getWidth()-handler.getWidth()/4,handler.getHeight()/16);
            g.drawString("SCORE",handler.getWidth()-handler.getWidth()/4+handler.getWidth()/48,handler.getHeight()/8);
            g.drawString(String.valueOf(handler.getScoreManager().getGalagaHighScore()),handler.getWidth()-handler.getWidth()/4+handler.getWidth()/48,handler.getHeight()/5);
            g.setColor(Color.RED);
            //This is the current Score
            g.drawString("SCORE: ",handler.getWidth()/3,handler.getHeight()/16);
            g.drawString(String.valueOf(handler.getScoreManager().getGalagaCurrentScore()),handler.getWidth()/2,handler.getHeight()/16);
           
            if (handler.getScoreManager().getGalagaCurrentScore()>=10000) {
            //This is The Explosion Countdown
            g.setFont(new Font("Arial", Font.PLAIN, 40));
            g.drawString("TIME LEFT:",75,handler.getHeight()-300);
            g.setFont(new Font("Arial", Font.PLAIN, 100));
            g.drawString(String.valueOf(explosioncountdown/60),125,handler.getHeight()-200);
           }
            //gameOver
            
            if (PlayerShip.gameLost) {
            	gameendcountdown--;
            	g.setFont(new Font("Arial", Font.PLAIN, 100));
                g.drawString("Game Over",handler.getWidth()/2-260,handler.getHeight()-350);
                if (gameendcountdown==60*5) {
                handler.getMusicHandler().playEffect("GameOver.wav");
                }            	
            }
            if (PlayerShip.gameWon) {
            	gameendcountdown--;
            	g.setFont(new Font("Arial", Font.PLAIN, 100));
                g.drawString("VICTORY!",handler.getWidth()/2-260,handler.getHeight()-350);
                if (gameendcountdown==60*5) {
                handler.getMusicHandler().playEffect("Victory.wav");
                }            	
            	
            	
            }
            
            if (Missile.equipped&&!PlayerShip.gameLost) {
            	g.setFont(new Font("Arial", Font.PLAIN, 35));
                g.drawString("press SPACE to shoot the missile!",handler.getWidth()/2-260,handler.getHeight()-250);
                g.drawString("you only have one, make it count!",handler.getWidth()/2-250,handler.getHeight()-200);
            }
            
            for (int i = 0; i< entityManager.playerShip.getHealth();i++) {
                g.drawImage(Images.galagaPlayer[0], (handler.getWidth() - handler.getWidth() / 4 + handler.getWidth() / 48) + ((entityManager.playerShip.width*2)*i), handler.getHeight()-handler.getHeight()/4, handler.getWidth() / 18, handler.getHeight() / 18, null);
            }
            if (startCooldown<=0) {
                entityManager.render(g);
            }else{
                g.setFont(new Font("TimesRoman", Font.PLAIN, 48));
                g.setColor(Color.MAGENTA);
                g.drawString("Start",handler.getWidth()/2-handler.getWidth()/18,handler.getHeight()/2);
            }
        }else{

            g.setFont(new Font("TimesRoman", Font.PLAIN, 32));

            g.setColor(Color.MAGENTA);
            g.drawString("HIGH-SCORE:",handler.getWidth()/2-handler.getWidth()/18,32);

            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(handler.getScoreManager().getGalagaHighScore()),handler.getWidth()/2-32,64);

            g.drawImage(titleAnimation.getCurrentFrame(),handler.getWidth()/2-(handler.getWidth()/12),handler.getHeight()/2-handler.getHeight()/3,handler.getWidth()/6,handler.getHeight()/7,null);

            g.drawImage(Images.galagaCopyright,handler.getWidth()/2-(handler.getWidth()/8),handler.getHeight()/2 + handler.getHeight()/3,handler.getWidth()/4,handler.getHeight()/8,null);

            g.setFont(new Font("TimesRoman", Font.PLAIN, 48));
            g.drawString("1   PLAYER",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2);
            g.drawString("2   PLAYER",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2+handler.getHeight()/12);
            if (selectPlayers == 1){
                g.drawImage(Images.galagaSelect,handler.getWidth()/2-handler.getWidth()/12,handler.getHeight()/2-handler.getHeight()/32,32,32,null);
            }else{
                g.drawImage(Images.galagaSelect,handler.getWidth()/2-handler.getWidth()/12,handler.getHeight()/2+handler.getHeight()/18,32,32,null);
            }


        }
    }

    @Override
    public void refresh() {


    }

}
