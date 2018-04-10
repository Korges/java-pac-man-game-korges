import controler.HandleThreads;
import controler.MovementController;
import demo.Game;
import helper.Mode;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modes.Client;
import modes.NetworkConnection;
import modes.Server;


public class App extends Application {

    private static Game game = new Game();
    private MovementController movementController;
    private static NetworkConnection networkConnection;
    private static int port;
    private static String host;
    private static Mode mode;
    private HandleThreads handleThreads;


    @Override
    public void init(){
        movementController = new MovementController(game);

    }

    public void start(Stage primaryStage) throws Exception {

        Pane root = FXMLLoader.load(getClass().getResource("/GameBoard.fxml"));

        Scene scene = new Scene(root);
        Pane pane = (Pane) root.lookup("#scene");

        game.setMode(mode);
        game.createWalls(pane);
        game.createCoins(pane);
        game.setPosition(pane);

        setNetworkConnection();

        movementController.movement(scene, game.getHostPlayer(), networkConnection, pane);
        showPreparedStage(primaryStage, scene);

    }

    private void showPreparedStage(Stage stage, Scene scene) {

        stage.setTitle("Pac-Man");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void setNetworkConnection() throws Exception {

        if (mode == Mode.SERVER) {
            networkConnection = new Server(port, game);

        } else if (mode == Mode.CLIENT) {
            networkConnection = new Client(host, port, game);
        }

        networkConnection.startConnection();
    }

    public static void main(String[] args) {

        manageArgs(args);
        launch(args);
    }

    private static void manageArgs(String[] args) {

        mode = Mode.getInstance(args[0]);

        if (args.length == 2) {
            port = Integer.parseInt(args[1]);

        } else {
            port = Integer.parseInt(args[2]);
            host = args[1];
        }
    }



}
