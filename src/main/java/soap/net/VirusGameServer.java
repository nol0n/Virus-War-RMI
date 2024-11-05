package soap.net;

import soap.game.ServerGameModel;
import javax.xml.ws.Endpoint;

public class VirusGameServer {
    public static final String name = "VirusGame";
    public static final int port = 8080;

    public static void main(String[] args) {
        String url = String.format("http://localhost:%d/%s", port, name);
        ServerGameModel gameModel = new ServerGameModel();
        Endpoint.publish(url, gameModel);
        System.out.println("Сервер работает: " + url);
    }
}
