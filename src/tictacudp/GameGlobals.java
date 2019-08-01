package tictacudp;

import java.awt.Dimension;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


enum PlayerId {
    LOCAL, REMOTE, NONE
};

class GameGlobals {
    // Game state

    static boolean isServer;
    static boolean isLocalPlayerTurn;
    static boolean localPlayAgain;  // Does local player want to play again?
    static String localPlayerMarker = "O";
    static String remotePlayerMarker = "X";
    static int numberCellsFilled;
    static boolean isGameOver = false;
// Games played. 
// Server goes first when numberGamesCompleted is even,
// client goes first when numberGamesCompleted is odd.
    static int numberGamesCompleted;
    static int numberGamesWon = 0;
    static int numberGamesLost = 0;
// User Interface Components
    static final TTTButton[][] tttBoard;    // The 9 tic tac toe buttons
    static final JTextArea chatHistoryTextArea;
    static final JTextField chatMessageTextField;
    static final JLabel gameStatusLabel;   // Used to announce win or loss

// Use a static constructor to create the user interface components
    //used to initialiazed once
    static {
        tttBoard = new TTTButton[3][3];
        // create all the buttons and add them
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tttBoard[i][j] = new TTTButton(i, j);
                tttBoard[i][j].setPreferredSize(new Dimension(50, 50));
                tttBoard[i][j].setText("");
                tttBoard[i][j].setPlayerId(PlayerId.NONE);
                tttBoard[i][j].addActionListener(new BoardListener());
            }

        }

// create the chat stuff and the status label
        chatHistoryTextArea = new JTextArea(14, 14);
        chatHistoryTextArea.setLineWrap(true);

        chatMessageTextField = new JTextField("Type to your opponent here");
        gameStatusLabel = new JLabel("Status label");

// add listeners
        chatMessageTextField.addActionListener(new ChatListener());

    }//end static

// Does all start of game initializations and starts the
// the netInputMonitorTimer.
    static void init() {
        //netInputMonitorTimer.stop();
        if (isServer == true) {
           
            if (GameGlobals.numberGamesCompleted % 2 == 0) {
                GameGlobals.localPlayerMarker = "O";
                GameGlobals.remotePlayerMarker = "X";
                GameGlobals.isLocalPlayerTurn = true;
                GameGlobals.gameStatusLabel.setText("Make your move."
                        +" Wins: "+ numberGamesWon 
                        + ". Losses: " + numberGamesLost
                        + ". Game Count: " + numberGamesCompleted);
            } else {
                GameGlobals.localPlayerMarker = "X";
                GameGlobals.remotePlayerMarker = "O";
                GameGlobals.isLocalPlayerTurn = false;
                GameGlobals.gameStatusLabel.setText("Wait your turn."
                        +" Wins: " + numberGamesWon 
                        + ". Losses: " + numberGamesLost
                        + ". Game Count: " + numberGamesCompleted);
            }
        } else {
            if (GameGlobals.numberGamesCompleted % 2 != 0) {
                GameGlobals.localPlayerMarker = "X";
                GameGlobals.remotePlayerMarker = "O";
                GameGlobals.isLocalPlayerTurn = true;
                GameGlobals.gameStatusLabel.setText("Make your move."
                        +" Wins: "+ numberGamesWon 
                        + ". Losses: " + numberGamesLost
                        + ". Game Count: " + numberGamesCompleted);
            } else {
                GameGlobals.localPlayerMarker = "O";
                GameGlobals.remotePlayerMarker = "X";
                GameGlobals.isLocalPlayerTurn = false;
                GameGlobals.gameStatusLabel.setText("Wait your turn."
                        +" Wins: " + numberGamesWon 
                        + ". Losses: " + numberGamesLost
                        + ". Game Count: " + numberGamesCompleted);
            }
        }

        numberGamesCompleted++;
        chatHistoryTextArea.setText("");
        numberCellsFilled = 0;
        isGameOver = false;
        //clear board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tttBoard[i][j].setText("");
                tttBoard[i][j].setPlayerId(PlayerId.NONE);
            }
        }
    }

    /**
     * Process a line of input read sent from the remote side
     *
     * @param input
     */
    static void processInput(String input){
       
        Scanner s = new Scanner(input);
        String string = s.next();
        //System.out.println("process input string = "+string);
        switch (string) {
            case "chat":
                //System.out.println("in case: chat. Input is: " + input);
                if (isServer == true) {
                    chatHistoryTextArea.append("Client: ");
                } else {
                    chatHistoryTextArea.append("Server: ");
                }
                while (s.hasNext()) {
                    chatHistoryTextArea.append(s.next() + " ");
                }
                chatHistoryTextArea.append("\n");
                break;
            case "move":
                //System.out.println("in case: move");
                processRemoteMove(s.nextInt(), s.nextInt());
                if (GameGlobals.isLocalPlayerTurn == true) {
                    gameStatusLabel.setText("Make your move");
                }
                break;
            case "playAgain":
                //System.out.println("in case: playAgain");
                processPlayAgainMessage(s.nextBoolean());
                break;
        }
    }

    /**
     * Process a remote user move to given row and column
     *
     * @param row
     * @param col
     */
    static void processRemoteMove(int row, int col) {
        if (isServer == true) {
            tttBoard[row][col].setText(remotePlayerMarker);
        } else {
            tttBoard[row][col].setText(localPlayerMarker);
        }
        tttBoard[row][col].setPlayerId(PlayerId.REMOTE);

        GameGlobals.numberCellsFilled++;

        if (GameGlobals.hasWon(PlayerId.REMOTE) == true) {
            GameGlobals.gameStatusLabel.setText("You lost!");
            numberGamesLost++;
        } else {
            if (GameGlobals.numberCellsFilled == 9) {
                GameGlobals.gameStatusLabel.setText("Draw!");       
            } else {
                GameGlobals.isLocalPlayerTurn = true;
                if (isServer == true) {
                    tttBoard[row][col].setText(remotePlayerMarker);
                } else {
                    tttBoard[row][col].setText(localPlayerMarker);
                }
                gameStatusLabel.setText("Make your move");
            }
        }
    }

    /**
     * Check to see if a player has won.
     *
     * @param player
     * @return
     */
    static boolean hasWon(PlayerId player) {
        //Check Rows and columns
        int i;
        //horizontal
        for (i = 0; i < 3; i++) {
            if (GameGlobals.tttBoard[i][0].playerId == player
                    && GameGlobals.tttBoard[i][1].playerId == player
                    && GameGlobals.tttBoard[i][2].playerId == player) {
                return true;
            }
            //vertical
            if (GameGlobals.tttBoard[0][i].playerId == player
                    && GameGlobals.tttBoard[1][i].playerId == player
                    && GameGlobals.tttBoard[2][i].playerId == player) {
                return true;
            }
        }

        //diagonal bottom left - top right
        if (GameGlobals.tttBoard[2][0].playerId == player
                && GameGlobals.tttBoard[1][1].playerId == player
                && GameGlobals.tttBoard[0][2].playerId == player) {
            return true;
        }
        //diagonal top left - bottom right
        if (GameGlobals.tttBoard[0][0].playerId == player
                && GameGlobals.tttBoard[1][1].playerId == player
                && GameGlobals.tttBoard[2][2].playerId == player) {
            return true;
        }

        return false;
    }

    /**
     * Process a play again message from the remote player Note: You get this
     * only after the local player has made his or her decision, which is then
     * stored in GameGlobals.localPlayAgain?
     *
     * @param flag
     */
    static void processPlayAgainMessage(boolean flag) {
        if (flag == true) {
            int playAgainOption = JOptionPane.showConfirmDialog(null,
                    "Do you want to play again?",
                    "Game over", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == playAgainOption) {
                GameGlobals.init();
            } else {
                
                Packet.sendPacket("playAgain " + false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The other player quit.");
           
            Packet.sendPacket("playAgain " + false);           
            System.exit(0);
        }
    }

    /**
     * Process a move by the local player
     *
     * @param row
     * @param col
     */
    static void processLocalMove(int row, int col) {
        if (isServer) {
            tttBoard[row][col].setText(localPlayerMarker);//localPlayerMarker
        } else {
            tttBoard[row][col].setText(remotePlayerMarker);//remotePlayerMarker            
        }
        GameGlobals.tttBoard[row][col].setPlayerId(PlayerId.LOCAL);
        GameGlobals.numberCellsFilled++;
        
        Packet.sendPacket("move " + row + " " + col);
        //Checking win condition
        if (GameGlobals.hasWon(PlayerId.LOCAL) == true) {
            GameGlobals.gameStatusLabel.setText("You won!");
            numberGamesWon++;
            int playAgainOption = JOptionPane.showConfirmDialog(null,
                    "Do you want to play again?",
                    "Game over", JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == playAgainOption) {
               
                Packet.sendPacket("playAgain " + true);
                GameGlobals.localPlayAgain = true;
                GameGlobals.init();
            } else {
                
                Packet.sendPacket("playAgain " + false);
                GameGlobals.localPlayAgain = false;
                System.exit(0);
            }
        } else {
            if (GameGlobals.numberCellsFilled == 9) {
                GameGlobals.gameStatusLabel.setText("Draw!");
                int playAgainOption = JOptionPane.showConfirmDialog(null,
                        "Do you want to play again?",
                        "Game over", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION == playAgainOption) {
                    GameGlobals.localPlayAgain = true;
                    
                    Packet.sendPacket("playAgain " + true);
                    GameGlobals.init();
                } else {
                    GameGlobals.localPlayAgain = false;
                    
                    Packet.sendPacket("playAgain " + false);
                    System.exit(0);
                }
            } else {
                GameGlobals.isLocalPlayerTurn = false;
                GameGlobals.gameStatusLabel.setText("Waiting for other player");
            }
        }
    }
}
