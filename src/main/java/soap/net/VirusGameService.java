package soap.net;

import soap.game.GameState;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface VirusGameService {
    @WebMethod
    boolean makeMove(int playerId, int row, int col);

    @WebMethod
    int registerPlayer();

    @WebMethod
    void confirmReady(int playerId);

    @WebMethod
    void surrender(int playerId);

    @WebMethod
    void disconnectPlayer(int playerId);

    @WebMethod
    GameState getGameState();
}
