package demo;

import helper.Direction;
import helper.Mode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import model.Player;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private Mode mode;
    private static final int BLOCK_SIZE = 40;
    public static final int WIDTH = 17 * BLOCK_SIZE;
    public static final int HEIGHT = 17 * BLOCK_SIZE;
    private Pane hostPlayer;
    private Pane clientPlayer;
    private Player player;
    private List<Rectangle> walls;
    private List<Circle> coins;

    public Game(){
        walls = new ArrayList<>();
        coins = new ArrayList<>();
    }

    public void setPosition(Pane pane) {

        if (mode.equals(Mode.SERVER)) {
            setServerPosition(pane);
        } else {
            setClientPosition(pane);
        }
        player = new Player(hostPlayer.getTranslateX(), hostPlayer.getTranslateY());
        player.setDirection(Direction.UP);
    }

    private void setServerPosition (Pane pane) {

        hostPlayer = (Pane) pane.lookup("#server");
        clientPlayer = (Pane) pane.lookup("#client");
        hostPlayer.setTranslateX(325); hostPlayer.setTranslateY(645);
        clientPlayer.setTranslateX(325); clientPlayer.setTranslateY(365);
    }

    private void setClientPosition(Pane pane) {

        hostPlayer = (Pane) pane.lookup("#client");
        clientPlayer = (Pane) pane.lookup("#server");
        hostPlayer.setTranslateX(325); hostPlayer.setTranslateY(365);
        clientPlayer.setTranslateX(325); clientPlayer.setTranslateY(645);
    }

    public void createWalls(Pane pane) {

        for (int i = 0; i < pane.getChildren().size(); i++){
            if(pane.getChildren().get(i) instanceof Rectangle
                    && pane.getChildren().get(i) != hostPlayer
                    && pane.getChildren().get(i) != clientPlayer) {

                walls.add((Rectangle) pane.getChildren().get(i));
            }
        }
    }

    public void createCoins(Pane pane){
        for (int i = 0; i < pane.getChildren().size(); i++){
            if(pane.getChildren().get(i) instanceof Circle){
                coins.add((Circle) pane.getChildren().get(i));
            }
        }
    }

    public Pane getHostPlayer() {
        return hostPlayer;
    }

    public Pane getClientPlayer() {
        return clientPlayer;
    }

    public List<Rectangle> getWalls() {
        return walls;
    }

    public List<Circle> getCoins() {
        return coins;
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPacman() {
        if (mode.getValue().equals("server")) {
            return true;
        }
        return false;
    }
}
