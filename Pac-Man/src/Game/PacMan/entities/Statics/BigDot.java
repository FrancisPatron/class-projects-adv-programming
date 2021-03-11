package Game.PacMan.entities.Statics;

import Game.GameStates.PacManState;
import Main.Handler;

public class BigDot extends BaseStatic{
    public BigDot(int x, int y, int width, int height, Handler handler) {
        super(x, y, width, height, handler,PacManState.getBigDotBlinkAnimation());
    }	

}
