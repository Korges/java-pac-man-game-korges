package controler;

import demo.Game;
import helper.Mode;
import helper.Direction;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Player;
import modes.NetworkConnection;


public class MovementController {

    private final int STEP = 8;
    private final int PLAYER_SIZE = 30;

    private Direction direction;
    private boolean moved = false;
    private Timeline timeline = new Timeline();

    private char[][] walkableBoard;
    private Game game;
//    Player player;

    public MovementController(Game game) {

        this.game = game;
        this.walkableBoard = new char[680][680];
        direction = Direction.UP;
    }

    public void movement(Scene scene, Pane hostSquare, NetworkConnection networkConnection, Pane pane) {
        handleMovement(scene, hostSquare, networkConnection, pane);
        timeline.play();

    }

    public void handleMovement(Scene scene, Pane hostSquare, NetworkConnection networkConnection, Pane pane) {

        prepareTable();

        scene.setOnKeyPressed(event -> {
            if (moved) {
                int x = (int) hostSquare.getTranslateX();
                int y = (int) hostSquare.getTranslateY();
                switch (event.getCode()) {

                    case W:
                        if (isAbleToMoveUp(hostSquare, x, y)) {
                            direction = Direction.UP;
                            game.getPlayer().setDirection(Direction.UP);
                        }
                        break;
                    case S:
                        if (isAbleToMoveDown(hostSquare, x, y)) {
                            direction = Direction.DOWN;
                            game.getPlayer().setDirection(Direction.DOWN);
                        }
                        break;
                    case A:
                        if (isAbleToMoveLeft(hostSquare, x, y)) {
                            direction = Direction.LEFT;
                            game.getPlayer().setDirection(Direction.LEFT);
                        }
                        break;
                    case D:
                        if (isAbleToMoveRight(hostSquare, x, y)) {
                            direction = Direction.RIGHT;
                            game.getPlayer().setDirection(Direction.RIGHT);
                        }
                        break;
                }
            }
        });

        KeyFrame frame = new KeyFrame(Duration.seconds(0.05), event -> {
            if (networkConnection.isConnected()) {
                try {

                    int x = (int) hostSquare.getTranslateX();
                    int y = (int) hostSquare.getTranslateY();

                    switch (direction) {
                        case UP:
                            checkMoveUp(hostSquare, x, y);
                            break;
                        case DOWN:
                            checkMoveDown(hostSquare, x, y);
                            break;
                        case LEFT:
                            checkMoveLeft(hostSquare, x, y);
                            break;
                        case RIGHT:
                            checkMoveRight(hostSquare, x, y);
                            break;
                    }
                    handleCoins(pane);
                    handleEnd(networkConnection, pane);
                    roundDirection();

                    moved = true;
                    handleSend(networkConnection);

                } catch (Exception e) {

                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void checkMoveUp(Pane player, int x, int y) {
        if (isAbleToMoveUp(player, x, y)) {
            player.setTranslateY(player.getTranslateY() - STEP);
            game.getPlayer().setDirection(Direction.UP);
        }
    }

    private boolean isAbleToMoveUp(Pane player, int x, int y) {
        return player.getTranslateY() > STEP
                && walkableBoard[x][y - STEP] == 'O' && walkableBoard[x + PLAYER_SIZE][y - STEP] == 'O';
    }

    private void checkMoveDown(Pane player, int x, int y) {
        if (isAbleToMoveDown(player, x, y)) {
            player.setTranslateY(player.getTranslateY() + STEP);
            game.getPlayer().setDirection(Direction.DOWN);
        }
    }

    private boolean isAbleToMoveDown(Pane player, int x, int y) {
        return player.getTranslateY() < Game.HEIGHT - 40
                && walkableBoard[x][y + STEP + PLAYER_SIZE] == 'O'
                && walkableBoard[x + PLAYER_SIZE][y + STEP + PLAYER_SIZE] == 'O';
    }

    private void checkMoveLeft(Pane player, int x, int y) {
        if (isAbleToMoveLeft(player, x, y)) {
            player.setTranslateX(player.getTranslateX() - STEP);
            game.getPlayer().setDirection(Direction.LEFT);
        }
    }

    private boolean isAbleToMoveLeft(Pane player, int x, int y) {
        return player.getTranslateX() > STEP
                && walkableBoard[x - STEP][y] == 'O' && walkableBoard[x - STEP][y + PLAYER_SIZE] == 'O';
    }

    private void checkMoveRight(Pane player, int x, int y) {
        if (isAbleToMoveRight(player, x, y)) {
            player.setTranslateX(player.getTranslateX() + STEP);
            game.getPlayer().setDirection(Direction.RIGHT);
        }
    }

    private boolean isAbleToMoveRight(Pane player, int x, int y) {
        return player.getTranslateX() < Game.WIDTH - 40
                && walkableBoard[x + STEP + PLAYER_SIZE][y] == 'O'
                && walkableBoard[x + STEP + PLAYER_SIZE][y + PLAYER_SIZE] == 'O';
    }

    private void prepareTable() {

        fillTable();
        fillWithWalkableFields();
    }

    private void fillTable() {

        for (int i = 0; i < 680; i++) {
            for (int j = 0; j < 680; j++) {
                walkableBoard[i][j] = 'O';
            }
        }
    }

    private void fillWithWalkableFields() {

        for (Rectangle shape : game.getWalls()) {
            for (int i = (int) shape.getLayoutX(); i < shape.getLayoutX() + shape.getWidth(); i++) {
                for (int j = (int) shape.getLayoutY(); j < shape.getLayoutY() + shape.getHeight(); j++) {
                    walkableBoard[i][j] = ' ';
                }
            }
        }
    }

    private void handleCoins(Pane pane){
        if (game.getMode().equals(Mode.SERVER)){
            handleServerCoinPick(pane);
        } else {
            handleClientCoinRemove(pane);
        }
    }

    private void handleSend(NetworkConnection networkConnection) throws Exception {

        double coordinateX = game.getHostPlayer().getTranslateX();
        double coordinateY = game.getHostPlayer().getTranslateY();

        game.getPlayer().setxCoordinate(coordinateX);
        game.getPlayer().setyCoordinate(coordinateY);
        networkConnection.send(new Player(game.getPlayer()));
    }

    private void handleServerCoinPick(Pane pane){

        Circle toRemove = null;
        double coordinateX = game.getHostPlayer().getTranslateX();
        double coordinateY = game.getHostPlayer().getTranslateY();

        for (Circle coin: game.getCoins()){

            double coinCoordinateX = coin.getLayoutX() + coin.getCenterX() - 15;
            double coinCoordinateY = coin.getLayoutY() + coin.getCenterY() - 15;
            if (coordinateX == coinCoordinateX && coordinateY == coinCoordinateY){
                toRemove = coin;
                pane.getChildren().remove(coin);
                break;
            }
        }
        game.getCoins().remove(toRemove);
    }

    private void handleClientCoinRemove(Pane pane) {
        pane.getChildren().removeAll(game.getCoinsToRemove());
        game.getCoins().removeAll(game.getCoinsToRemove());
        game.getCoinsToRemove().clear();
    }


    private void roundDirection() {

        if (game.getPlayer().getDirection() == Direction.UP) {

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

    private void handleEnd(NetworkConnection networkConnection, Pane pane){

        if (game.getCoins().isEmpty()){
            if (game.getMode() == Mode.SERVER){
                handleWin(pane);
            } else {
                handleLose(pane);
            }
            networkConnection.setConnected(false);
        } else if (game.getHostPlayer().getTranslateX() == game.getClientPlayer().getTranslateX()
                && game.getHostPlayer().getTranslateY() == game.getClientPlayer().getTranslateY()){
            if (game.getMode() == Mode.CLIENT){
                handleWin(pane);
            } else {
                handleLose(pane);
            }
            networkConnection.setConnected(false);
        }
    }

    private void handleWin(Pane pane){
        StackPane stackPane = (StackPane) pane.getParent();
        Pane victoryPane = (Pane) stackPane.lookup("#victory");
        victoryPane.setOpacity(1);
    }

    private void handleLose(Pane pane){
        StackPane stackPane = (StackPane) pane.getParent();
        Pane victoryPane = (Pane) stackPane.lookup("#lose");
        victoryPane.setOpacity(1);
    }



}
