package controler;

import demo.Game;
import helper.Direction;
import helper.WalkableBoard;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
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
    private HandleThreads handleThreads;

    public MovementController(Game game) {

        this.game = game;
        direction = Direction.UP;
    }

    public void movement(Scene scene, Pane hostSquare, NetworkConnection networkConnection, Pane pane) {
        handleMovement(scene, hostSquare, networkConnection, pane);
        timeline.play();

    }

    private void handleMovement(Scene scene, Pane hostSquare, NetworkConnection networkConnection, Pane pane) {

        WalkableBoard board = new WalkableBoard();
        walkableBoard = board.prepareTable(game.getWalls());
        startThreads(pane, networkConnection);

        scene.setOnKeyPressed(event -> {

            if (moved) {
                setDesiredDirection(event);
            }
        });

        KeyFrame frame = new KeyFrame(Duration.seconds(0.05), event -> {
            if (networkConnection.isConnected()) {

                try {
                    int x = (int) hostSquare.getTranslateX();
                    int y = (int) hostSquare.getTranslateY();

                    changeDirection(hostSquare, x, y);

                    moved = true;
                    handleSend(networkConnection);

                } catch (Exception e) {
                    System.out.println("impossible to send");
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void setDesiredDirection(KeyEvent event) {
        switch (event.getCode()) {

            case W:
                direction = Direction.UP;
                break;
            case S:
                direction = Direction.DOWN;
                break;
            case A:
                direction = Direction.LEFT;
                break;
            case D:
                direction = Direction.RIGHT;
                break;
        }
    }

    private void changeDirection(Pane square, int x, int y) {
        switch (direction) {
            case UP:
                checkMoveUp(square, x, y);
                break;
            case DOWN:
                checkMoveDown(square, x, y);
                break;
            case LEFT:
                checkMoveLeft(square, x, y);
                break;
            case RIGHT:
                checkMoveRight(square, x, y);
                break;
        }
    }

    private void checkMoveUp(Pane player, int x, int y) {
        if (isAbleToMoveUp(player, x, y)) {
            player.setTranslateY(player.getTranslateY() - STEP);
            game.getPlayer().setDirection(Direction.UP);
        } else {
            continueMoving(player, game.getPlayer().getDirection(), x, y);
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
        } else {
            continueMoving(player, game.getPlayer().getDirection(), x, y);
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
        } else {
            continueMoving(player, game.getPlayer().getDirection(), x, y);
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
        } else {
            continueMoving(player, game.getPlayer().getDirection(), x, y);
        }
    }

    private boolean isAbleToMoveRight(Pane player, int x, int y) {
        return player.getTranslateX() < Game.WIDTH - 40
                && walkableBoard[x + STEP + PLAYER_SIZE][y] == 'O'
                && walkableBoard[x + STEP + PLAYER_SIZE][y + PLAYER_SIZE] == 'O';
    }

    private void continueMoving(Pane player, Direction direction, int x, int y) {
        switch (direction) {
            case UP:
                if (isAbleToMoveUp(player, x, y))
                    player.setTranslateY(player.getTranslateY() - STEP);
                break;
            case DOWN:
                if (isAbleToMoveDown(player, x, y))
                    player.setTranslateY(player.getTranslateY() + STEP);
                break;
            case RIGHT:
                if (isAbleToMoveRight(player, x, y))
                    player.setTranslateX(player.getTranslateX() + STEP);
                break;
            case LEFT:
                if (isAbleToMoveLeft(player, x, y))
                    player.setTranslateX(player.getTranslateX() - STEP);
                break;
        }
    }

    private void handleSend(NetworkConnection networkConnection) throws Exception {

        double coordinateX = game.getHostPlayer().getTranslateX();
        double coordinateY = game.getHostPlayer().getTranslateY();

        game.getPlayer().setxCoordinate(coordinateX);
        game.getPlayer().setyCoordinate(coordinateY);

        networkConnection.send(new Player(game.getPlayer()));
    }

    private void startThreads(Pane pane, NetworkConnection networkConnection) {
        handleThreads = new HandleThreads(game, pane, networkConnection);
        Thread thread = new Thread(handleThreads);
        thread.start();
    }
}
