import java.awt.*;
import javax.swing.*;

public class ClientGUI {
    private BBConnection connection = new BBConnection();

    private JTextField hostField = new JTextField("127.0.0.1");
    private JTextField portField = new JTextField("4554");
    private JTextArea outputArea = new JTextArea();
    private BoardPanel boardPanel = new BoardPanel();

    public ClientGUI() {
        JFrame frame = new JFrame("Bulletin Board Client");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(1, 5));
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(120, 0));

        JButton connectBtn = new JButton("Connect");
        JButton disconnectBtn = new JButton("Disconnect");
        
        JButton postBtn = new JButton("POST");
        JButton getBtn = new JButton("GET");
        JButton pinBtn = new JButton("PIN");
        JButton unpinBtn = new JButton("UNPIN");
        JButton shakeBtn = new JButton("SHAKE");
        JButton clearBtn = new JButton("CLEAR");

        top.add(new JLabel("Host:"));
        top.add(hostField);
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(connectBtn);
        
        left.add(postBtn);
        left.add(getBtn);
        left.add(pinBtn);
        left.add(unpinBtn);
        left.add(shakeBtn);
        left.add(clearBtn);
        left.add(disconnectBtn);

        outputArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(outputArea);

        frame.setLayout(new BorderLayout());

        frame.add(top, BorderLayout.NORTH);
        frame.add(left, BorderLayout.WEST);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(scroll, BorderLayout.SOUTH);


        connectBtn.addActionListener(e -> connect());
        disconnectBtn.addActionListener(e -> disconnect());
        clearBtn.addActionListener(e -> { //MAYBE CHANGE THIS TO FIT LIKE THE OTHERS
            String cmd = CommandBuilder.buildClear();
            outputArea.append("> " + cmd + "\n");
            boardPanel.clearNotes(); // offline effect
        });
        postBtn.addActionListener(e -> doPost());
        pinBtn.addActionListener(e -> doPin());
        unpinBtn.addActionListener(e -> doUnpin());
        shakeBtn.addActionListener(e -> doShake());

        
        frame.setVisible(true);
    }

    private void connect() {
        try {
            connection.connect(
                hostField.getText(),
                Integer.parseInt(portField.getText())
            );
            outputArea.append("Connected to server\n");
        } catch (Exception ex) {
            outputArea.append("Connection failed: " + ex.getMessage() + "\n");
        }
    }

    private void disconnect() {
        try {
            connection.disconnect();
            outputArea.append("Disconnected\n");
        } catch (Exception ex) {
            outputArea.append("Disconnect error\n");
        }
    }

    private void doPost() {
    try {
        String xs = JOptionPane.showInputDialog(null, "x coordinate:");
        if (xs == null) return;
        String ys = JOptionPane.showInputDialog(null, "y coordinate:");
        if (ys == null) return;
        String color = JOptionPane.showInputDialog(null, "color (e.g., yellow, red):");
        if (color == null) return;
        String msg = JOptionPane.showInputDialog(null, "message:");
        if (msg == null) return;

        int x = Integer.parseInt(xs.trim());
        int y = Integer.parseInt(ys.trim());

        if (x < 0 || y < 0) {
            outputArea.append("Client validation: coordinates must be non-negative\n");
            return;
        }
        if (msg.trim().isEmpty()) {
            outputArea.append("Client validation: message cannot be empty\n");
            return;
        }

        String cmd = CommandBuilder.buildPost(x, y, color.trim(), msg.trim());
        outputArea.append("> " + cmd + "\n");

        // OFFLINE behavior: add the note locally
        boardPanel.addMockNote(new Note(x, y, color.trim(), msg.trim()));

    } catch (NumberFormatException ex) {
        outputArea.append("Client validation: x and y must be integers\n");
    }
}

    private void doPin() {
        try {
            String xs = JOptionPane.showInputDialog(null, "PIN x:");
            if (xs == null) return;
            String ys = JOptionPane.showInputDialog(null, "PIN y:");
            if (ys == null) return;

            int x = Integer.parseInt(xs.trim());
            int y = Integer.parseInt(ys.trim());

            String cmd = CommandBuilder.buildPin(x, y);
            outputArea.append("> " + cmd + "\n");

            boolean ok = boardPanel.pinAt(x, y);
            if (!ok) outputArea.append("(offline) ERROR NO_NOTE_AT_COORDINATE\n");

        } catch (NumberFormatException ex) {
        outputArea.append("Client validation: x and y must be integers\n");
        }
    }

    private void doUnpin() {
        try {
            String xs = JOptionPane.showInputDialog(null, "UNPIN x:");
            if (xs == null) return;
            String ys = JOptionPane.showInputDialog(null, "UNPIN y:");
            if (ys == null) return;

            int x = Integer.parseInt(xs.trim());
            int y = Integer.parseInt(ys.trim());

            String cmd = CommandBuilder.buildUnpin(x, y);
            outputArea.append("> " + cmd + "\n");

            boolean ok = boardPanel.unpinAt(x, y);
            if (!ok) outputArea.append("(offline) ERROR PIN_NOT_FOUND\n");

        } catch (NumberFormatException ex) {
            outputArea.append("Client validation: x and y must be integers\n");
        }
    }

    private void doShake() {
        String cmd = CommandBuilder.buildShake();
        outputArea.append("> " + cmd + "\n");
        boardPanel.shake();
    }


}