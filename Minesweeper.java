// https://youtu.be/Yf65-9-jrms

public class Grid {

    private boolean[][] bombGrid;
    private int[][] countGrid;
    private int numRows;
    private int numColumns;
    private int numBombs;

    public Grid() {
        this(10, 10, 25);
    }
 
    public Grid(int rows, int columns) {
        this(rows, columns, 25);
    }

    public Grid(int rows, int columns, int numBombs) {
        this.numRows = rows;
        this.numColumns = columns;
        this.numBombs = numBombs;

        createBombGrid();
        createCountGrid();
    }

    // Getters
    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumBombs() {
        return numBombs;
    }

    // copy of bomb grid
    public boolean[][] getBombGrid() {
        boolean[][] copy = new boolean[numRows][numColumns];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                copy[r][c] = bombGrid[r][c];
            }
        }
        return copy;
    }

    // copy of count grid
    public int[][] getCountGrid() {
        int[][] copy = new int[numRows][numColumns];
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                copy[r][c] = countGrid[r][c];
            }
        }
        return copy;
    }

    // Check if bomb exists at location
    public boolean isBombAtLocation(int row, int column) {
        return bombGrid[row][column];
    }

    // Get the count from the count grid
    public int getCountAtLocation(int row, int column) {
        return countGrid[row][column];
    }

    // random bomb grid
    private void createBombGrid() {
        bombGrid = new boolean[numRows][numColumns];

        int bombsPlaced = 0;
        while (bombsPlaced < numBombs) {
            int r = (int)(Math.random() * numRows);
            int c = (int)(Math.random() * numColumns);

            if (!bombGrid[r][c]) {
                bombGrid[r][c] = true;
                bombsPlaced++;
            }
        }
    }

    // Create count grid  
    private void createCountGrid() {
        countGrid = new int[numRows][numColumns];

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numColumns; c++) {
                int count = 0;

                if (r > 0 && c > 0 && bombGrid[r - 1][c - 1]) 
                	count++;    
                if (r > 0 && bombGrid[r - 1][c])
                	count++;                
                if (r > 0 && c < numColumns - 1 && bombGrid[r - 1][c + 1])
                	count++; 

                if (c > 0 && bombGrid[r][c - 1]) 
                	count++;                 
                if (bombGrid[r][c]) 
                	count++;                              
                if (c < numColumns - 1 && bombGrid[r][c + 1]) 
                	count++;   

                if (r < numRows - 1 && c > 0 && bombGrid[r + 1][c - 1]) 
                	count++;    
                if (r < numRows - 1 && bombGrid[r + 1][c]) 
                	count++;             
                if (r < numRows - 1 && c < numColumns - 1 && bombGrid[r + 1][c + 1]) 
                	count++; 

                countGrid[r][c] = count;
            }
        }
    }

}

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI implements ActionListener {

    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private Grid grid;
    private boolean[][] revealed;

    private int rows;
    private int cols;
    private int bombs;
    private int revealedCount;

    
    //Constructor that sets the grid sizing
    
    public GUI() {
        rows = 10;
        cols = 10;
        bombs = 25;
        startGame();
    }

    //starts game
    
    private void startGame() {
        grid = new Grid(rows, cols, bombs);
        buttons = new JButton[rows][cols];
        revealed = new boolean[rows][cols];
        revealedCount = 0;

        frame = new JFrame("Minesweeper");
        panel = new JPanel();
        panel.setLayout(new GridLayout(rows, cols));

        
        //This makes all the buttons
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton("");
                btn.addActionListener(this);
                buttons[r][c] = btn;
                panel.add(btn);
                revealed[r][c] = false;
            }
        }

        frame.add(panel);
        frame.setSize(600, 600);  //Size of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //all the buttons that can be clicked
    public void actionPerformed(ActionEvent e) {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (e.getSource() == buttons[r][c]) {

                    if (revealed[r][c])
                        return;

                    if (grid.isBombAtLocation(r, c)) {
                        revealAll();
                        askToPlayAgain("You hit a bomb. You lost!");
                        return;
                    }

                    revealCell(r, c);
                    
                    
//if all cells revealed then they win the game
                    if (revealedCount == (rows * cols - bombs)) {
                        revealAll();
                        askToPlayAgain("You won the game!");
                        return;
                    }
                }
            }
        }
    }

    
   // shows a cell but never the same cell twice
    private void revealCell(int r, int c) {

        if (revealed[r][c])
            return;

        revealed[r][c] = true;
        revealedCount++;

        int count = grid.getCountAtLocation(r, c);
        buttons[r][c].setEnabled(false);

        if (count > 0) {
            buttons[r][c].setText("" + count);
        } else {
            buttons[r][c].setText("");
            revealZeros(r, c);
        }
    }
    
    
// shows the cells around when a 0 is clicked
    private void revealZeros(int r, int c) {

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {

                int newRow = r + dr;
                int newCol = c + dc;

                if (newRow >= 0 && newRow < rows &&
                    newCol >= 0 && newCol < cols) {

                    if (!revealed[newRow][newCol] &&
                        !grid.isBombAtLocation(newRow, newCol)) {

                        revealCell(newRow, newCol);
                    }
                }
            }
        }
    }
    
//shows the answers of the grid at the end
    //shows where the bombs are
    private void revealAll() {

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (grid.isBombAtLocation(r, c)) {
                    buttons[r][c].setText("B");
                } else {
                    int count = grid.getCountAtLocation(r, c);
                    if (count > 0)
                        buttons[r][c].setText("" + count);
                }

                buttons[r][c].setEnabled(false);
            }
        }
    }
    
//what pops up after you lose
    //Asks whether u want to play again or not
    
    private void askToPlayAgain(String message) {

        int choice = JOptionPane.showConfirmDialog(
                frame,
                message + "\nPlay again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            new GUI();
        } else {
            frame.dispose();
            System.exit(0);
        }
    }
    
  //main method
    
    public static void main(String[] args) {
        new GUI();
    }
}
