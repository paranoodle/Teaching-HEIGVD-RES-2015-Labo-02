package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.*;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  @Override
  public void clearDataStore() throws IOException {
    writer.println(RouletteV2Protocol.CMD_CLEAR);
    writer.flush();
    
    if(!read().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) throw new IOException("Unexpected server response");
  }

  @Override
  public List<Student> listStudents() throws IOException {
    writer.println(RouletteV2Protocol.CMD_LIST);
    writer.flush();
    
    StudentsList sl = JsonObjectMapper.parseJson(read(), StudentsList.class);
    return sl.getStudents();
  }
  
  protected void endOfData() throws IOException {
    LoadCommandResponse cr = JsonObjectMapper.parseJson(read(), LoadCommandResponse.class);
    if (cr.getStatus().equals("success"))  LOG.log(Level.INFO, "{0} students added.", cr.getNumberOfNewStudents());
    else LOG.severe("Error. No students added.");
  }
}
