package soap.net;

import soap.game.ClientGameModel;
import soap.game.GameFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class VirusGameClient {
    public static final int port = 8080;
    static final String name = "VirusGame";

    private ClientGameModel gameModel;
    private GameFrame gameFrame;

    public void start() {
        try {
            URL url = new URL(String.format("http://localhost:%d/%s&wsdl", port, name));
            QName qname = new QName("http://game.soap/", "ServerGameModelService");
            Service service = Service.create(url, qname);
            VirusGameService server = service.getPort(VirusGameService.class);

            int playerId = server.registerPlayer();
            if (playerId == -1) {
                JOptionPane.showMessageDialog(null, "Игра уже заполнена");
                return;
            }

            gameModel = new ClientGameModel(server, playerId);
            SwingUtilities.invokeLater(() -> gameFrame = new GameFrame(gameModel));
            startUpdateThread();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к серверу: " + e.getMessage());
        }
    }

    private void startUpdateThread() {
        Thread updateThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                SwingUtilities.invokeLater(() -> gameFrame.refresh());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public static void main(String[] args) {
        new VirusGameClient().start();
    }
}
