package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 * 
 * @author Eleonore d'Agostino
 */
public class RouletteV1ParanoodleTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);
  
  @Test
  @TestAuthor(githubId = "paranoodle")
  public void theServerShouldReturnOnlyStudentOnRandom() throws IOException, EmptyStoreException {
    IRouletteV1Client client = roulettePair.getClient();
    client.loadStudent("lapin");
    assertEquals("lapin", client.pickRandomStudent().getFullname());
  }
  
}
