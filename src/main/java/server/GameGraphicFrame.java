package server;

import javax.swing.*;

public class GameGraphicFrame extends JFrame {

    public GameGraphicFrame(){
        this.setTitle("jCoinche Server");
        this.setSize(1400, 950);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new GameGraphicPanel());
        this.setResizable(false);

        this.setVisible(true);
    }
}