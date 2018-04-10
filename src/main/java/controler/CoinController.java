package controler;

import demo.Game;
import helper.Mode;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class CoinController {


    private Game game;
    private Pane pane;

    public CoinController(Game game, Pane pane) {
        this.game = game;
        this.pane = pane;
    }

    public void checkCoins() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (game.getMode() == Mode.SERVER){
                    handleServerCoinPick();
                } else {
                    handleClientCoinRemove(game.getClientPlayer());
                }
            }
        });
    }

    private void handleServerCoinPick() {

        Circle toRemove = null;

        for (Circle coin: game.getCoins()){

            if (game.getHostPlayer().getBoundsInParent().contains(coin.getBoundsInParent())){
                toRemove = coin;
                pane.getChildren().remove(coin);
                break;
            }
        }
        game.getCoins().remove(toRemove);
    }

    private void handleClientCoinRemove(Pane pacMan) {

        Circle toRemove = null;

        for (Circle coin: game.getCoins()){

            if (pacMan.getBoundsInParent().contains(coin.getBoundsInParent())) {
                toRemove = coin;
            }
        }
        game.getCoins().remove(toRemove);
        pane.getChildren().remove(toRemove);
    }
}
