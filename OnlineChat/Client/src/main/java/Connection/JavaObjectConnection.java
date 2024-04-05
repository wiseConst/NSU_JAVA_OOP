package Connection;

import DTO.DTO;
import Log.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JavaObjectConnection implements Connection {
    private Socket clientSocket;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    @Override
    public Socket getSocket() {
        return clientSocket;
    }

    @Override
    public DTO receive() throws IOException, ClassNotFoundException {
        if (objectInputStream == null) {
            Log.GetLogger().error("ObjectInputStream is null!");
            return null;
        }

        DTO dto;
        synchronized (objectInputStream) {
            dto = (DTO) objectInputStream.readObject();
        }

        return dto;
    }

    @Override
    public void send(DTO dto) throws IOException {
        if (dto == null) {
            Log.GetLogger().error("DTO is null!");
            return;
        }

        synchronized (objectOutputStream) {
            objectOutputStream.writeObject(dto);
            objectOutputStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (clientSocket != null) clientSocket.close();
        if (objectInputStream != null) objectInputStream.close();
        if (objectOutputStream != null) objectOutputStream.close();
    }

    @Override
    public void connect(Socket socket) throws IOException {
        if (socket == null) {
            Log.GetLogger().error("Socket is null!");
            return;
        }

        clientSocket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }
}
