package client;

import frames.ClientIdentification;
import java.io.IOException;

/**
 *
 * @author Lucas Penha de Moura - 1208977
 */
public class Init {
    public static void main(String[] args) throws IOException {
        ClientIdentification cliIndent = new ClientIdentification();
        cliIndent.setVisible(true);
    }
}
