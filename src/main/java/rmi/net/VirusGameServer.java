package rmi.net;

import rmi.game.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VirusGameServer {
    public static final String name = "VirusServer";
    public static final int port = 8080;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            ServerGameModel gameModel = new ServerGameModel();
            RemoteVirusGame proxy = (RemoteVirusGame)
                    UnicastRemoteObject.exportObject(gameModel, port);
            registry.rebind(name, proxy);
            System.out.println("Сервер работает...");
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
}
