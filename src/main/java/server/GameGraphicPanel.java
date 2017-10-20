package server;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameGraphicPanel extends JPanel {

    private int Player1OriginX = 1200;
    private int Player1OriginY = 0;

    private int Player2OriginX = 8;
    private int Player2OriginY = 880;

    private int Player3OriginX = 8;
    private int Player3OriginY = 10;

    private int Player4OriginX = 1180;
    private int Player4OriginY = 880;

    private int PlayerBoxHeight = 50;
    private int PlayerBoxWidth = 200;

    private int PlayerNameMarging = 15;
    private int PlayerIpMarging = 34;
    private int PlayerCotentBoxMargin = 5;

    private int CardHeight = 130;
    private int CardWidth = 90;

    public void paintComponent(Graphics g){
        this.drawBackground(g);
        this.drawPlayerBox(g);
        this.drawMainDeck(g);
        this.drawPlayersDeck(g);
        this.drawPartyInformations(g);
    }

    public void drawBackground(Graphics g)
    {
        try {
            Image img = ImageIO.read(new File("bg.jpg"));
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawPlayersDeck(Graphics g)
    {
        Team[] gameTeams = Server.mainTable.getTeams();
        Player[] teamOne = gameTeams[0].getPlayers();
        Player[] teamTwo = gameTeams[1].getPlayers();

        int incrementCardSpacing = 101;

        /* Player1 */
        if( teamOne[0] != null)
        {
            int xOriginP1Cards = Player1OriginX + CardHeight / 2;
            int yOriginP1Cards = Player1OriginY + 5 + PlayerBoxHeight;

            for (int i = 0; i < teamOne[0].getDeck().length; i += 1)
            {
                try {
                    String filename = "";
                    if (teamOne[0].getDeck()[i] == null)
                        filename = "neutreCard.png";
                    else
                        filename = teamOne[0].getDeck()[i].getFamilyCard().toString().toLowerCase() + "_" + teamOne[0].getDeck()[i].getFamilyName().toString().toLowerCase() + ".png";
                    Image img = ImageIO.read(new File(filename));
                    g.drawImage((Image) Tools.rotateCw((BufferedImage) img), xOriginP1Cards, yOriginP1Cards, CardHeight, CardWidth, this);
                }catch (IOException e) {
                    System.exit(0);
                    e.printStackTrace();
                }
                yOriginP1Cards += incrementCardSpacing;
            }
        }

        /* End Player1 */

        /* Player2 */
        if( teamOne[1] != null)
        {
            int xOriginP1Cards = Player2OriginX;
            int yOriginP1Cards = Player2OriginY - 5 - PlayerBoxHeight * 2;

            for (int i = 0; i < teamOne[1].getDeck().length; i += 1)
            {
                try {
                    String filename = "";
                    if (teamOne[1].getDeck()[i] == null)
                        filename = "neutreCard.png";
                    else
                        filename = teamOne[1].getDeck()[i].getFamilyCard().toString().toLowerCase() + "_" + teamOne[1].getDeck()[i].getFamilyName().toString().toLowerCase() + ".png";
                    Image img = ImageIO.read(new File(filename));
                    g.drawImage((Image) Tools.rotateCw((BufferedImage) img), xOriginP1Cards, yOriginP1Cards, CardHeight, CardWidth, this);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                yOriginP1Cards -= incrementCardSpacing;
            }
        }

        /* End Player2 */

        /* Player3 */
        if( teamTwo[0] != null)
        {
            int xOriginP1Cards = Player3OriginX + 15 + PlayerBoxWidth;
            int yOriginP1Cards = Player3OriginY;

            for (int i = 0; i < teamTwo[0].getDeck().length; i += 1)
            {
                try {
                    String filename = "";
                    if (teamTwo[0].getDeck()[i] == null)
                        filename = "neutreCard.png";
                    else
                        filename = teamTwo[0].getDeck()[i].getFamilyCard().toString().toLowerCase() + "_" + teamTwo[0].getDeck()[i].getFamilyName().toString().toLowerCase() + ".png";
                    Image img = ImageIO.read(new File(filename));
                    g.drawImage(img, xOriginP1Cards, yOriginP1Cards, CardWidth, CardHeight, this);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                xOriginP1Cards += incrementCardSpacing;
            }
        }
        /* End Player3 */


        /* Player4 */
        if( teamTwo[1] != null)
        {
            int xOriginP1Cards = Player4OriginX - 5 - CardWidth;
            int yOriginP1Cards = Player4OriginY - CardHeight / 2 - 10;

            for (int i = 0; i < teamTwo[1].getDeck().length; i += 1)
            {
                try {
                    String filename = "";
                    if (teamTwo[1].getDeck()[i] == null)
                        filename = "neutreCard.png";
                    else
                        filename = teamTwo[1].getDeck()[i].getFamilyCard().toString().toLowerCase() + "_" + teamTwo[1].getDeck()[i].getFamilyName().toString().toLowerCase() + ".png";
                    Image img = ImageIO.read(new File(filename));
                    g.drawImage(img, xOriginP1Cards, yOriginP1Cards, CardWidth, CardHeight, this);
                }catch (IOException e) {
                    e.printStackTrace();
                }
                xOriginP1Cards -= incrementCardSpacing;
            }
        }
        /* End Player4 */

    }

    public void drawPartyInformations(Graphics g)
    {
        int yOrigin = 300;
        int xOrigin = 480;

        Font font = new Font("Montserrat", Font.PLAIN, 15);
        Font Boldfont = new Font("Montserrat", Font.BOLD, 15);

        g.setFont(Boldfont);
        g.setColor(Color.white);
        g.drawString("Game State :", xOrigin, yOrigin);
        g.setFont(font);
        String stateStr = "";

        g.drawString(Server.mainTable.getState().toString(), xOrigin + 100, yOrigin);

        yOrigin += CardHeight / 2 + 10;

        g.setFont(Boldfont);
        g.drawString("Atout :  ", xOrigin, yOrigin);
        if (Server.mainTable.getAtout() == null)
        {
            g.setFont(font);
            g.drawString("Not defined !", xOrigin + 100, yOrigin);
        }
        else
        {
            String fileName = Server.mainTable.getAtout().toString().toLowerCase() + ".png";
            try {
                Image img = ImageIO.read(new File(fileName));
                g.drawImage(img, xOrigin + 100, yOrigin - CardHeight / 2, CardWidth, CardHeight,this);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void drawMainDeck(Graphics g)
    {
        int yOrigin = 460;
        int xOrigin = 480;

        int xIncrement = 106;

        Card[] midDeck = Server.mainTable.getMidDeck();

        for (int i = 0; i < midDeck.length; i += 1)
        {
            String fileName = "";

            if (midDeck[i] == null)
                fileName = "neutreCard.png";
            else
                fileName = midDeck[i].getFamilyCard().toString().toLowerCase() + "_" + midDeck[i].getFamilyName().toString().toLowerCase() + ".png";
            try {
                Image img = ImageIO.read(new File(fileName));
                g.drawImage(img, xOrigin, yOrigin, CardWidth, CardHeight,this);
            }catch (IOException e) {
                e.printStackTrace();
            }

            xOrigin += xIncrement;
        }
    }

    public void drawPlayerBox(Graphics g)
    {
        Team[] gameTeams = Server.mainTable.getTeams();
        Player[] teamOne = gameTeams[0].getPlayers();
        Player[] teamTwo = gameTeams[1].getPlayers();

        Font font = new Font("Montserrat", Font.PLAIN, 15);
        g.setFont(font);
        g.setColor(Color.white);

        /* server.Player One */
        String PlayerOneName = null;
        String PlayerOneIp = null;
        if (teamOne[0] != null) {
            PlayerOneName = teamOne[0].getName();
            PlayerOneIp = teamOne[0].getChannel().remoteAddress().toString();
        }
        else
        {
            PlayerOneName = "No server.Player";
            PlayerOneIp = "__";
        }
        g.drawRect(Player1OriginX, Player1OriginY, PlayerBoxWidth, PlayerBoxHeight);
        g.drawString(PlayerOneName, Player1OriginX + PlayerCotentBoxMargin, Player1OriginY + PlayerNameMarging);
        g.drawString(PlayerOneIp, Player1OriginX + PlayerCotentBoxMargin, Player1OriginY + PlayerIpMarging);
        /* End server.Player One */

        /* server.Player Two */
        String PlayerTwoName = null;
        String PlayerTwoIp = null;
        if (teamOne[1] != null) {
            PlayerTwoName = teamOne[1].getName();
            PlayerTwoIp = teamOne[1].getChannel().remoteAddress().toString();
        }
        else
        {
            PlayerTwoName = "No server.Player";
            PlayerTwoIp = "__";
        }
        g.drawRect(Player2OriginX, Player2OriginY, PlayerBoxWidth, PlayerBoxHeight);
        g.drawString(PlayerTwoName, Player2OriginX + PlayerCotentBoxMargin, Player2OriginY + PlayerNameMarging);
        g.drawString(PlayerTwoIp, Player2OriginX + PlayerCotentBoxMargin, Player2OriginY + PlayerIpMarging);
        /* End server.Player Two */

        /* server.Player Three */
        String PlayerThreeName = null;
        String PlayerThreeIp = null;
        if (teamTwo[0] != null) {
            PlayerThreeName = teamTwo[0].getName();
            PlayerThreeIp = teamTwo[0].getChannel().remoteAddress().toString();
        }
        else
        {
            PlayerThreeName = "No server.Player";
            PlayerThreeIp = "__";
        }
        g.drawRect(Player3OriginX, Player3OriginY, PlayerBoxWidth, PlayerBoxHeight);
        g.drawString(PlayerThreeName, Player3OriginX + PlayerCotentBoxMargin, Player3OriginY + PlayerNameMarging);
        g.drawString(PlayerThreeIp, Player3OriginX + PlayerCotentBoxMargin, Player3OriginY + PlayerIpMarging);
        /* End server.Player Three */


        /* server.Player Four */
        String PlayerFourName = null;
        String PlayerFourIp = null;
        if (teamTwo[1] != null) {
            PlayerFourName = teamTwo[1].getName();
            PlayerFourIp = teamTwo[1].getChannel().remoteAddress().toString();
        }
        else
        {
            PlayerFourName = "No server.Player";
            PlayerFourIp = "__";
        }
        g.drawRect(Player4OriginX, Player4OriginY, PlayerBoxWidth, PlayerBoxHeight);
        g.drawString(PlayerFourName, Player4OriginX + PlayerCotentBoxMargin, Player4OriginY + PlayerNameMarging);
        g.drawString(PlayerFourIp, Player4OriginX + PlayerCotentBoxMargin, Player4OriginY + PlayerIpMarging);
        /* End server.Player Four */


    }
}