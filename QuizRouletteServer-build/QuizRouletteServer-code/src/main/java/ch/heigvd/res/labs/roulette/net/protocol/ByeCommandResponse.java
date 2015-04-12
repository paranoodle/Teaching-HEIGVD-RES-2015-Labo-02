package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize responses for the "BYE" command
 *
 * @author Eleonore d'Agostino
 */
 
public class ByeCommandResponse {
    private String status;
    private int numberOfCommands;
    
    public ByeCommandResponse() {}
    
    public ByeCommandResponse(String s, int i) {
        status = s;
        numberOfCommands = i;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String s) {
        status = s;
    }
    
    public int getNumberOfCommands() {
        return numberOfCommands;
    }
    
    public void setNumberOfCommands(int i) {
        numberOfCommands = i;
    }
}