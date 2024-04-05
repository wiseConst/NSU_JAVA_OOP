package Connection;

import DTO.DTO;
import Log.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class JsonConnection implements Connection {
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
            String encodedDTO = (String) objectInputStream.readObject();
            ObjectMapper objectMapper = new ObjectMapper();
            dto = objectMapper.readValue(encodedDTO, DTO.class);
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
            ObjectMapper objectMapper = new ObjectMapper();
            String parsedDTO = objectMapper.writeValueAsString(dto);
            objectOutputStream.writeObject(parsedDTO);
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
