package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
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
  private BufferedReader reader;
  private PrintWriter writer;
  
  private String read() throws IOException {
    return reader.readLine();
  }
  
  @Override
  public void connect(String server, int port) throws IOException {
    socket = new Socket(server, port);
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  @Override
  public void disconnect() throws IOException {
    reader.close();
    writer.close();
    socket.close();
  }

  @Override
  public boolean isConnected() {
    if (socket != null) return socket.isConnected();
    else return false;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    writer.println(fullname);
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    for (Student s : students) writer.println(s.getFullname());
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    RandomCommandResponse cr = JsonObjectMapper.parseJson(read(), RandomCommandResponse.class);
    return new Student(cr.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    InfoCommandResponse cr = JsonObjectMapper.parseJson(read(), InfoCommandResponse.class);
    return cr.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    InfoCommandResponse cr = JsonObjectMapper.parseJson(read(), InfoCommandResponse.class);
    return cr.getProtocolVersion();
  }



}
