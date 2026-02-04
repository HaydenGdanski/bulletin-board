import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class BoardPanel extends JPanel {
    private int noteW = 60;     // temporary until handshake
    private int noteH = 40;
    private final List<Note> notes = new ArrayList<>();

    public BoardPanel() {
        setBackground(Color.WHITE); // board background is plain
    }

    public void setNoteSize(int w, int h) {
        noteW = w;
        noteH = h;
        repaint();
    }

    public void setNotes(List<Note> newNotes) {
        notes.clear();
        notes.addAll(newNotes);
        repaint();
    }

    public void addMockNote(Note n) {
        notes.add(n);
        repaint();
    }

    public void clearNotes() {
        notes.clear();
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Note n : notes) {
            g.setColor(new Color(0,0,0,40)); //extra, literally just aesthetic
            g.fillRect(n.x + 3, n.y + 3, noteW, noteH);

            g.setColor(mapColor(n.color));
            g.fillRect(n.x, n.y, noteW, noteH);

            g.setColor(Color.BLACK);
            g.drawRect(n.x, n.y, noteW, noteH);

            drawWrappedText(g, n.message, n.x + 5, n.y + 15, noteW - 10);

            if (n.isPinned()){
                g.setColor(Color.BLACK);
                int cx = n.x + noteW / 2;
                int cy = n.y +8;
                g.fillOval(cx - 4, cy - 4, 8, 8);
            }
        }
    }

    private Color mapColor(String name) {
        if (name == null) return Color.LIGHT_GRAY;
        String c = name.toLowerCase();

        if (c.equals("red")) return Color.PINK;
        if (c.equals("green")) return Color.GREEN;
        if (c.equals("blue")) return Color.CYAN;
        if (c.equals("yellow")) return Color.YELLOW;
        if (c.equals("white")) return Color.WHITE;

        return Color.LIGHT_GRAY;
    }

    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        if (text == null) return;

        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";
        int lineY = y;

        for (String w : words) {
            String test = line.isEmpty() ? w : line + " " + w;
            if (fm.stringWidth(test) > maxWidth) {
                g.drawString(line, x, lineY);
                line = w;
                lineY += fm.getHeight();
            } else {
                line = test;
            }
        }

        if (!line.isEmpty()) g.drawString(line, x, lineY);
    }

    public boolean pinAt(int px, int py){

        boolean hit = false;
        for (Note n : notes) {
            if (containsPoint(n, px, py)){

                n.addPin();
                hit = true;

            }
            }
            if (hit) repaint();
            return hit;
        }

    public boolean unpinAt(int px, int py){

        boolean removedAny = false;
        for (Note n : notes) {

            if (containsPoint(n, px, py)){
                if (n.removePin()) removedAny = true;
            }
        }
        if (removedAny) repaint();
        return removedAny;
    }

    public void shake(){

        Iterator<Note> it = notes.iterator();
        while (it.hasNext()){
            Note n = it.next();
            if (!n.isPinned()) it.remove();
        }
        repaint();
    }

    private boolean containsPoint(Note n, int px, int py){
        return (px >= n.x && px <= n.x + noteW &&
                py >= n.y && py <= n.y + noteH);
    }
}


