package Game.PacMan.entities.Statics;

import java.util.Random;
import Game.PacMan.entities.Dynamics.Ghost;
import Game.PacMan.entities.Statics.BaseStatic;
import Main.Handler;
import Resources.Images;

public class GhostSpawner extends BaseStatic{
    public GhostSpawner(int x, int y, int width, int height, Handler handler) {
        super(x, y, width, height, handler,Images.spawner);
    }	
    
    public static void addNewGhost (Handler handler) {
    	Random random = new Random();
    	handler.getMap().addEnemy(new Ghost(342, 342, 18, 18, handler, Images.ghost[random.nextInt(4)], random.nextInt(2)+1));
    }


}
