package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.*;
import ch.heigvd.res.labs.roulette.data.Student;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  
  private Socket socket;
  protected BufferedReader reader;
  protected PrintWriter writer;
  
  protected String read() throws IOException {
    String line;
    do {
        line = reader.readLine();
    } while (line.equalsIgnoreCase(RouletteV1Protocol.RESPONSE_HELLO)
            || line.equalsIgnoreCase(RouletteV1Protocol.RESPONSE_HELP));
    return line;
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  @Override
  public void disconnect() throws IOException {
    writer.println(RouletteV1Protocol.CMD_BYE);
    writer.flush();
    reader.close();
    writer.close();
    socket.close();
  }

  @Override
  public boolean isConnected() {
    if (socket != null) return !socket.isClosed() && socket.isConnected();
    else return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    List<Student> sl = new ArrayList<Student>();
    sl.add(new Student(fullname));
    loadStudents(sl);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    writer.println(RouletteV1Protocol.CMD_LOAD);
    writer.flush();
    
    if(!read().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) throw new IOException("Unexpected server response");
    
    for (Student s : students) writer.println(s.getFullname());
    
    writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    writer.flush();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    writer.println(RouletteV1Protocol.CMD_RANDOM);
    writer.flush();
    
    RandomCommandResponse cr = JsonObjectMapper.parseJson(read(), RandomCommandResponse.class);
    if(cr.getError() != null) throw new EmptyStoreException();
    
    return new Student(cr.getFullname());
  }
  
  protected void endOfData() throws IOException {
    read();
  }
  
  @Override
  public int getNumberOfStudents() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    
    InfoCommandResponse cr = JsonObjectMapper.parseJson(read(), InfoCommandResponse.class);
    return cr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    writer.println(RouletteV1Protocol.CMD_INFO);
    writer.flush();
    
    InfoCommandResponse cr = JsonObjectMapper.parseJson(read(), InfoCommandResponse.class);
    return cr.getProtocolVersion();
  }
}
