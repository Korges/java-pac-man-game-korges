package modes;

import controler.DirectionController;
import demo.Game;
import model.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class NetworkConnection extends Thread {

    private Socket socket;
    private ServerSocket server;
    private ObjectOutputStream outputStream;
    private Game game;
    private Player player;
    private boolean connected;
    private boolean isRunning;
    private DirectionController directionController;

    public NetworkConnection(Game game){

        this.isRunning = true;
        this.game = game;
        this.connected = false;
        this.isRunning = true;
        this.directionController = new DirectionController(game);
    }

    public void startConnection() throws Exception {

        start();
    }

    public void send(Serializable data) throws Exception {

        outputStream.writeObject(data);
        outputStream.flush();
    }

    public void closeConnection() throws Exception {

        socket.close();
        isRunning = false;
    }

    protected abstract boolean isServer();

    protected abstract String getIP();

    protected abstract int getPort();

    @Override
    public void run() {

        try {
            setServerAndSocket();

            connected = true;

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            this.outputStream = out;

            while (isRunning) {
                player = (Player) in.readObject();
                moveOponent(player);
            }
        } catch (IOException e){

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setServerAndSocket() throws IOException {
        if (isServer()) {
            server = new ServerSocket(getPort());
            socket = server.accept();
        } else {
            socket = new Socket(getIP(), getPort());
        }
    }

    private void moveOponent(Player player){

        game.getClientPlayer().setTranslateY(player.getyCoordinate());
        game.getClientPlayer().setTranslateX(player.getxCoordinate());
        directionController.checkDirectionClient(player);
    }



    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

}