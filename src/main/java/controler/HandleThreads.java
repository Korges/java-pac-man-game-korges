package controler;

import demo.Game;
import javafx.scene.layout.Pane;
import modes.NetworkConnection;

public class HandleThreads implements Runnable {

    private Game game;
    private Pane pane;
    private NetworkConnection networkConnection;
    private CoinController coinController;
    private EndController endController;
    private DirectionController directionController;

    public HandleThreads(Game game, Pane pane, NetworkConnection networkConnection){
        this.game = game;
        this.pane = pane;
        this.networkConnection = networkConnection;
        startThreads(game, pane, networkConnection);
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                Thread.currentThread().sleep(20);
                if (networkConnection.isConnected()){
                    coinController.checkCoins();
                    endController.checkEnd();
                    directionController.checkDirectionHost();
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void startThreads(Game game, Pane pane, NetworkConnection networkConnection){
        coinController = new CoinController(game ,pane);
        endController = new EndController(game, pane, networkConnection);
        directionController = new DirectionController(game);
    }
}
