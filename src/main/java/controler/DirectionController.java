package controler;

import demo.Game;
import helper.Direction;
import helper.Mode;
import javafx.application.Platform;
import model.Player;

public class DirectionController {

    private Game game;

    public DirectionController(Game game) {
        this.game = game;
    }

    public void checkDirection() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                    roundDirection();

            }
        });
    }

    public void roundDirection(Player player) {

        if (player.getDirection() == Direction.UP) {
            System.out.println("player");
            if(!game.isPacman()) {

                game.getClientPlayer().setRotate(270);
                game.getClientPlayer().setScaleY(1);
            }
        } else if (player.getDirection() == Direction.RIGHT) {

            game.getClientPlayer().setRotate(0);
            game.getClientPlayer().setScaleY(1);

        } else if (player.getDirection() == Direction.DOWN) {

            if(!game.isPacman()) {
                game.getClientPlayer().setRotate(90);
                game.getClientPlayer().setScaleY(1);
            }

        } else if (player.getDirection() == Direction.LEFT) {

            game.getClientPlayer().setRotate(180);
            game.getClientPlayer().setScaleY(-1);
        }
    }

    private void roundDirection() {



        if (game.getPlayer().getDirection() == Direction.UP) {
            System.out.println("bez player");
            if (game.isPacman()) {
                game.getHostPlayer().setRotate(270);
                game.getHostPlayer().setScaleY(1);
            }

        } else if (game.getPlayer().getDirection() == Direction.RIGHT) {

            game.getHostPlayer().setRotate(0);
            game.getHostPlayer().setScaleY(1);

        } else if (game.getPlayer().getDirection() == Direction.DOWN) {

            if (game.isPacman()) {

                game.getHostPlayer().setRotate(90);
                game.getHostPlayer().setScaleY(1);

            }
        } else if (game.getPlayer().getDirection() == Direction.LEFT) {

            game.getHostPlayer().setRotate(180);
            game.getHostPlayer().setScaleY(-1);
        }
    }

}