package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientHandler implements IClientHandler {

  final static Logger LOG = Logger.getLogger(RouletteV1ClientHandler.class.getName());
  private final IStudentsStore store;
  private int ccount = 0;
  
  public RouletteV2ClientHandler(IStudentsStore store) {
    this.store = store;
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os));

    writer.println(RouletteV1Protocol.RESPONSE_HELLO);
    writer.flush();

    String command;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV1Protocol.CMD_RANDOM:
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_HELP:
          writer.println("Commands: " + Arrays.toString(RouletteV1Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV1Protocol.CMD_INFO: //V2 modification
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_LOAD: //V2 modification
          writer.println(RouletteV1Protocol.RESPONSE_LOAD_START);
          writer.flush();
          
          int scount = store.getNumberOfStudents();
          store.importData(reader);
          scount = store.getNumberOfStudents() - scount;
          
          LoadCommandResponse lcr = new LoadCommandResponse("success", scount);
          writer.println(JsonObjectMapper.toJson(lcr));
          writer.flush();
          break;
        case RouletteV1Protocol.CMD_BYE: //V2 modification
          ByeCommandResponse bcr = new ByeCommandResponse("success", ccount);
          writer.println(JsonObjectMapper.toJson(bcr));
          writer.flush();
          done = true;
          break;
        case RouletteV2Protocol.CMD_LIST: //V2 addition
          StudentsList sl = new StudentsList();
          sl.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(sl));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_CLEAR: //V2 addition
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        default:
          writer.println(RouletteV1Protocol.RESPONSE_HELP);
          writer.flush();
          break;
      }
      ccount++; //assuming number of commandes includes ones the server couldn't read
      writer.flush();
    }
  }

}
