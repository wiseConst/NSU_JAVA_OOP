package Connection;

import DTO.DTO;

import java.io.IOException;
import java.net.Socket;

public interface Connection extends AutoCloseable {

    public Socket getSocket();

    public DTO receive() throws IOException, ClassNotFoundException;

    public void send(DTO dto) throws IOException;

    public void close() throws IOException;

    public void connect(Socket socket) throws IOException;
}
