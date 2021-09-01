import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

class SlidePuzzleModel {
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private Tile[][] _contents; // Τα 8 Tiles.
    private Tile     _emptyTile; // Το κενο Tile.
    int moves=0;
    public SlidePuzzleModel() {
        _contents = new Tile[ROWS][COLS];
        reset();
    }
    String getFace(int row, int col) {
        return _contents[row][col].getFace();
    }
    public void reset() {
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                _contents[r][c] = new Tile(r, c, "" + (r*COLS+c+1));
            }
        }
        _emptyTile = _contents[ROWS-1][COLS-1];
        _emptyTile.setFace(null);
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                exchangeTiles(r, c, (int)(Math.random()*ROWS)
                                  , (int)(Math.random()*COLS));
            }
        }
    }
    public boolean moveTile(int r, int c) {
        return checkEmpty(r, c, -1, 0) || checkEmpty(r, c, 1, 0)
            || checkEmpty(r, c, 0, -1) || checkEmpty(r, c, 0, 1);
    }
    private boolean checkEmpty(int r, int c, int rdelta, int cdelta) {
        int rNeighbor = r + rdelta;
        int cNeighbor = c + cdelta;
        if (isLegalRowCol(rNeighbor, cNeighbor) 
                  && _contents[rNeighbor][cNeighbor] == _emptyTile) {
            exchangeTiles(r, c, rNeighbor, cNeighbor);
            return true;
        }
        return false;
    }
    public boolean isLegalRowCol(int r, int c) {
        return r>=0 && r<ROWS && c>=0 && c<COLS;
    }
    private void exchangeTiles(int r1, int c1, int r2, int c2) {
        Tile temp = _contents[r1][c1];
        _contents[r1][c1] = _contents[r2][c2];
        _contents[r2][c2] = temp;
    }
        public boolean isGameOver() {
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<ROWS; c++) {
                Tile trc = _contents[r][c];
                return trc.isInFinalPosition(r, c);
            }
        }
        return true;    
}
}

class Tile {
    private int _row;
    private int _col;
    private String _face;
    public Tile(int row, int col, String face) {
        _row = row;
        _col = col;
        _face = face;
    }
    public void setFace(String newFace) {
        _face = newFace;
    }
    public String getFace() {
        return _face;
    }
    public boolean isInFinalPosition(int r, int c) {
        return r==_row && c==_col;
    }
}


public class PuzzleGame extends JApplet
{
    
private GraphicsPanel _puzzleGraphics;
private SlidePuzzleModel _puzzleModel = new SlidePuzzleModel();
static int moves = 0;
final Label l1 = new Label("");
JPanel controlPanel = new JPanel();
JPanel controlPanel2 = new JPanel();

public PuzzleGame() {
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new NewGameAction());
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(newGameButton);
        _puzzleGraphics = new GraphicsPanel();  
        l1.setText("Moves: 0");
        this.setLayout(new BorderLayout());
        this.add(controlPanel, BorderLayout.NORTH);
        this.add(l1,BorderLayout.SOUTH);
        this.add(_puzzleGraphics, BorderLayout.CENTER);
    }
class GraphicsPanel extends JPanel implements MouseListener {
        private static final int ROWS = 3;
        private static final int COLS = 3;
        private static final int CELL_SIZE = 80;
        private Font _biggerFont;
        
        public GraphicsPanel() {
            _biggerFont = new Font("SansSerif", Font.BOLD, CELL_SIZE/2);
            this.setPreferredSize(
            new Dimension(CELL_SIZE * COLS, CELL_SIZE*ROWS));
            this.setBackground(Color.black);
            this.addMouseListener(this);
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int r=0; r<ROWS; r++) {
                for (int c=0; c<COLS; c++) {
                    int x = c * CELL_SIZE;
                    int y = r * CELL_SIZE;
                    String text = _puzzleModel.getFace(r, c);
                    if (text != null) {
                        g.setColor(Color.gray);
                        g.fillRect(x+2, y+2, CELL_SIZE-4, CELL_SIZE-4);
                        g.setColor(Color.black);
                        g.setFont(_biggerFont);
                        g.drawString(text, x+20, y+(3*CELL_SIZE)/4);
                    }
                }
            }
        }
        
        public void mousePressed(MouseEvent e) {
            int col = e.getX()/CELL_SIZE;
            int row = e.getY()/CELL_SIZE;
            if (_puzzleModel.moveTile(row, col)){
                moves++;
                String s = Integer.toString(moves);
                l1.setText("Moves: "+s);
            }
            else
            {
                // Μηνυμα λαθους καθως δεν γινεται μετατοπιση αυτου του κελιου.
                Toolkit.getDefaultToolkit().beep();
            }
            // Εμφανιζει τυχον αλλαγες που εχουν γινει.
            this.repaint();
        }
        
        public void mouseClicked (MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered (MouseEvent e) {}
        public void mouseExited  (MouseEvent e) {}
    }
public class NewGameAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            _puzzleModel.reset();
            _puzzleGraphics.repaint();
            moves=0;
            l1.setText("Moves: 0");
        }
    }
    
    
public static void main(String args[]){
JFrame frame = new JFrame("Code Converter");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
PuzzleGame a = new PuzzleGame();
a.init();
frame.setContentPane(new PuzzleGame());
frame.pack();
frame.show();
}
}
