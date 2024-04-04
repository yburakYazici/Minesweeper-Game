import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; //
import java.util.Random; // random Library to place mines in random tiles
import javax.swing.*;

public class Minesweeper {  
    private class MineTile extends JButton { // its for row and colm to identify
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public boolean isMine() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'isMine'");
        }
    }

    int tileSize = 55; //pixels
    int numRows = 10;
    int numCols = numRows; // its for square
    int boardWidth = numCols * tileSize; //for equality
    int boardHeight = numRows * tileSize;
    
    JFrame frame = new JFrame("MineSweeper");
    JLabel textLabel = new JLabel();  //for the text
    JPanel textPanel = new JPanel();  //panel for this label
    JPanel boardPanel = new JPanel(); //add another panel board

//mine count and random places

    int mineCount = 10; //if its possible you can try to change rand mine count
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; //goal is to click all tiles except the ones containing mines
    boolean gameOver = false;

    Minesweeper() {
        //for window
        // frame.setVisible(true); // if it is true we can see the window
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); //this we will open the window at the centre of screen
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// when we press on the x button its terminate the program
        frame.setLayout(new BorderLayout()); //we have window

        //for text

        textLabel.setFont(new Font("Pixer", Font.BOLD, 23)); 
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH); // I take the panel and add it the window, Second parameter for where is the text

//  Board panel

        boardPanel.setLayout(new GridLayout(numRows, numCols)); //10x10
        // boardPanel.setBackground(Color.gray); //this is my background for now
        frame.add(boardPanel);

// for minetile

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0)); //it could be bomb,num or flag
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                // tile.setText("💣");
                
                tile.addMouseListener(new MouseAdapter() { //its for click
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        //left click  opening file
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") { //its empty
                                if (mineList.contains(tile)) { //if its mines
                                    revealMines(); 
                                }
                                else {
                                    checkMine(tile.r, tile.c); //check how many mines are nera the button
                                }
                            }
                            //button2 is scrollwheel
                        }
                        //right click putting flag
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) { //after '&&' if the button has not been clicked
                                tile.setText("🚩");
                            }
                            else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }
                        }
                    } 
                });

                boardPanel.add(tile);
                
            }
        }
        for (MineTile mineTile : mineList) {
            mineTile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    revealTile(mineTile);
                    if (mineTile.isMine()) {
                        // If it's a mine, show a popup indicating it's a mine
                        JOptionPane.showMessageDialog(null, "Mine revealed!", "Mine Revealed", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // If it's not a mine, show a popup indicating it's safe
                        JOptionPane.showMessageDialog(null, "Safe move! This tile is not a mine.", "Safe Move", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                private void revealTile(MineTile mineTile) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });
        }

        frame.setVisible(true); //if I click its visible

        setMines();
    }

    void setMines() {
        mineList = new ArrayList<MineTile>();

        // mineList.add(board[2][2]); //I will test code // board[r][c]
        // mineList.add(board[2][3]);
        // mineList.add(board[5][6]);
        // mineList.add(board[3][4]);
        // mineList.add(board[1][1]);
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows); //0-9
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c]; 
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        } 

        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return; //if its bound we exit the code immediately we never make checkmine
        }

        MineTile tile = board[r][c];                                                                                                                            
        if (!tile.isEnabled()) { //it has clicked on we are going to return so if it has not been clicked on we are going to set enable to false and disable the button
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;
//around the square
// r-1   ---
//  r    -*-
// r+1   ---
        //top 3
        minesFound += countMine(r-1, c-1);  //top left
        minesFound += countMine(r-1, c);    //top
        minesFound += countMine(r-1, c+1);  //top right

        //left and right
        minesFound += countMine(r, c-1);    //left
        minesFound += countMine(r, c+1);    //right

        //bottom 3
        minesFound += countMine(r+1, c-1);  //bottom left
        minesFound += countMine(r+1, c);    //bottom
        minesFound += countMine(r+1, c+1);  //bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        }
        else {
            tile.setText("");
            
            //top 3
            checkMine(r-1, c-1);    //top left
            checkMine(r-1, c);      //top
            checkMine(r-1, c+1);    //top right

            //left and right
            checkMine(r, c-1);      //left
            checkMine(r, c+1);      //right

            //bottom 3
            checkMine(r+1, c-1);    //bottom left
            checkMine(r+1, c);      //bottom
            checkMine(r+1, c+1);    //bottom right
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
}

