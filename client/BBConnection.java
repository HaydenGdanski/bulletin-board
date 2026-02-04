import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BBConnection {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public List<String> sendCommand(String command) throws IOException {
        List<String> response = new ArrayList<>();

        out.println(command);

        String line;
        while ((line = in.readLine()) != null) {
            response.add(line);
            if (line.startsWith("OK") || line.startsWith("ERROR")) {
                break;
            }
        }

        return response;
    }

    public void disconnect() throws IOException {
        if (socket != null) socket.close();
    }
}

