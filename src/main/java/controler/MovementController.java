package controler;

import demo.Game;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import model.Player;
import modes.NetworkConnection;

public class MovementController {

    private final int STEP = 20;
    private final int PLAYER_SIZE = 30;
    private static int playerXcoord;
    private static int playerYcoord;
    private char[][] walkableBoard;
    private Game game;

    public MovementController(Game game){
        this.game = game;
        this.walkableBoard = new char[680][680];
    }

    public void movement(Scene scene, Shape hostSquare, Shape clientSquare, NetworkConnection networkConnection) {

        prepareTable();
        scene.setOnKeyPressed(event -> {

            try {
                int x = (int) hostSquare.getTranslateX();
                int y = (int) hostSquare.getTranslateY();

                switch (event.getCode()) {

                    case W:
                        checkMoveUp(hostSquare,x, y);
                        break;
                    case S:
                        checkMoveDown(hostSquare, x, y);
                        break;
                    case A:
                        checkMoveLeft(hostSquare, x, y);
                        break;
                    case D:
                        checkMoveRight(hostSquare, x, y);
                        break;
                }

                handleSend(networkConnection);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void checkMoveUp(Shape player, int x, int y) {
        if (player.getTranslateY() > STEP && isAbleToMoveUp(x, y)) {
            player.setTranslateY(player.getTranslateY() - STEP);
        }
    }

    private boolean isAbleToMoveUp(int x, int y) {
        return walkableBoard[x][y - STEP] == 'O' && walkableBoard[x + PLAYER_SIZE][y - STEP] == 'O';
    }

    private void checkMoveDown(Shape player, int x, int y) {
        if (player.getTranslateY() < Game.HEIGHT - 40 && isAbleToMoveDown(x, y)) {
            player.setTranslateY(player.getTranslateY() + STEP);
        }
    }

    private boolean isAbleToMoveDown(int x, int y) {
        return walkableBoard[x][y + STEP + PLAYER_SIZE] == 'O' && walkableBoard[x + PLAYER_SIZE][y + STEP + PLAYER_SIZE] == 'O';
    }

    private void checkMoveLeft(Shape player, int x, int y) {
        if (player.getTranslateX() > STEP && isAbleToMoveLeft(x, y)) {
            player.setTranslateX(player.getTranslateX() - STEP);
        }
    }

    private boolean isAbleToMoveLeft(int x, int y) {
        return walkableBoard[x - STEP][y] == 'O' && walkableBoard[x -STEP][y + PLAYER_SIZE] == 'O';
    }

    private void checkMoveRight(Shape player, int x, int y) {
        if (player.getTranslateX() < Game.WIDTH  && isAbleToMoveRight(x, y)) {
            player.setTranslateX(player.getTranslateX() + STEP);
        }
    }

    private boolean isAbleToMoveRight(int x, int y) {
        return walkableBoard[x + STEP + PLAYER_SIZE][y] == 'O' && walkableBoard[x + STEP + PLAYER_SIZE][y + PLAYER_SIZE] == 'O';
    }

    private void prepareTable() {
        fillTable();
        fillWithWalkableFields();
    }

    private void fillTable() {
        for (int i=0; i<680; i++) {
            for (int j=0; j<680; j++) {
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

    private void handleSend(NetworkConnection networkConnection) throws Exception {

        Player player = new Player(game.getHostPlayer().getTranslateX(), game.getHostPlayer().getTranslateY());

        networkConnection.send(player);
    }

}