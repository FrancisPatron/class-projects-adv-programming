package Game.GameStates;

import Game.PacMan.World.MapBuilder;
import Game.PacMan.entities.Dynamics.BaseDynamic;
import Game.PacMan.entities.Statics.BaseStatic;
import Game.PacMan.entities.Statics.BigDot;
import Game.PacMan.entities.Statics.Dot;
import Game.PacMan.entities.Statics.Fruits;
import Game.PacMan.entities.Statics.GhostSpawner;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class PacManState extends State {

	private String Mode = "Intro";
	private int startCooldown = 60*4;//seven seconds for the music to finish
	private static Animation BigDotBlinkingAnimation;
	private boolean blinking;
	Random random;

	public PacManState(Handler handler){
		super(handler);
		BigDotBlinkingAnimation = new Animation(300,Images.pacmanBigDots);
		handler.setMap(MapBuilder.createMap(Images.map1, handler));
		random = new Random();

	}

	@Override
	public void tick() {
		if (handler.getScoreManager().getPacmanCurrentScore()>handler.getScoreManager().getPacmanHighScore()) {
			handler.getScoreManager().setPacmanHighScore(handler.getScoreManager().getPacmanCurrentScore());
		}
		BigDotBlinkingAnimation.tick();
		if (Mode.equals("Stage")){
			if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_C)) {
				GhostSpawner.addNewGhost(handler);
			}
			if (startCooldown<=0) {
				if (startCooldown == 0) {
					startCooldown--;
					GhostSpawner.addNewGhost(handler);
					GhostSpawner.addNewGhost(handler);
					GhostSpawner.addNewGhost(handler);					
					GhostSpawner.addNewGhost(handler);
				}
				for (BaseDynamic entity : handler.getMap().getEnemiesOnMap()) {
					entity.tick();
				}
				ArrayList<BaseStatic> toREmove = new ArrayList<>();
				for (BaseStatic blocks: handler.getMap().getBlocksOnMap()){
					if (blocks instanceof Dot){
						if (blocks.getBounds().intersects(handler.getPacman().getBounds())){
							handler.getMusicHandler().playEffect("pacman_chomp.wav");
							toREmove.add(blocks);
							handler.getScoreManager().addPacmanCurrentScore(10);
						}
					} if (blocks instanceof BigDot){
						if (blocks.getBounds().intersects(handler.getPacman().getBounds())){
							handler.getMusicHandler().playEffect("ScaredGhost.wav");
							toREmove.add(blocks);
							handler.getScoreManager().addPacmanCurrentScore(100);
							blinking = true;

						}
					} if (blocks instanceof Fruits) {
						if (blocks.getBounds().intersects(handler.getPacman().getBounds())){
							handler.getScoreManager().addPacmanCurrentScore(120);
							toREmove.add(blocks);
							handler.getMusicHandler().playEffect("FruitChomp.wav");
						}
					}
				}
				for (BaseStatic removing: toREmove){
					handler.getMap().getBlocksOnMap().remove(removing);
				}
				if(handler.getPacman().getHealth()<=0) {
					Mode = "End";
				}
			}else{
				startCooldown--;
			}
		}else if (Mode.equals("Menu")){
			if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER)){
				Mode = "Stage";
				handler.getMusicHandler().playEffect("pacman_beginning.wav");

			}
		}else{
			if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER)){
				Mode = "Menu";
			}
		}



	}

	@Override
	public void render(Graphics g) {

		if (Mode.equals("Stage")){
			Graphics2D g2 = (Graphics2D) g.create();
			handler.getMap().drawMap(g2);
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 32));
			g.drawString("Score: " + handler.getScoreManager().getPacmanCurrentScore(),(handler.getWidth()/2) + handler.getWidth()/6, 25);
			g.drawString("High-Score: " + handler.getScoreManager().getPacmanHighScore(),(handler.getWidth()/2) + handler.getWidth()/6, 75);
			for (int i = 0; i< handler.getPacman().getHealth();i++) {
				g.drawImage(Images.pacmanRight[0], (handler.getWidth() - handler.getWidth() / 2 + handler.getWidth() / 48) + ((handler.getPacman().width*5)*i), handler.getHeight()-handler.getHeight()/4, handler.getWidth() / 18, handler.getHeight() / 18, null);

			}
		}else if (Mode.equals("Menu")){
			g.drawImage(Images.start,0,0,handler.getWidth()/2,handler.getHeight(),null);

		}else if(Mode.equals("End")) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial Black", Font.PLAIN, 76));
			if(handler.getScoreManager().getPacmanCurrentScore()>handler.getScoreManager().getPacmanHighScore()) {
				g.drawString("High-Score: " + handler.getScoreManager().getPacmanCurrentScore(),(handler.getWidth()/2)-300, handler.getHeight()/2);

			}else
				g.drawString("High-Score: " + handler.getScoreManager().getPacmanHighScore(),(handler.getWidth()/2)-300, handler.getHeight()/2);
			g.drawString("GAME OVER",(handler.getWidth()/2)-250, handler.getHeight()/2-200);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial", Font.PLAIN, 40));
			g.drawString("Press ENTER To Consume (1) Credit And Try Again",300, handler.getHeight()-100);
			handler.getPacman().setHealth(3);
			handler.getScoreManager().setPacmanCurrentScore(0);
			handler.getPacManState().restartStartCooldown();
			handler.setMap(MapBuilder.createMap(Images.map1, handler));

		}else{
			g.drawImage(Images.intro,0,0,handler.getWidth()/2,handler.getHeight(),null);

		}
	}

	@Override
	public void refresh() {

	}

	public void restartStartCooldown() {
		startCooldown = 60*4;
	}

	public boolean getBlinking() {
		return blinking;
	}

	public void setBlinking(boolean x) {
		blinking = x;
	}

	public static BufferedImage getBigDotBlinkAnimation() {
		return BigDotBlinkingAnimation.getCurrentFrame();
	}
}