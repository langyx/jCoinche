import java.net.SocketAddress;
import java.nio.channels.Channel;

public class Table
{
    private CardFamily atout;
    private Team teams[];
    private Card midDeck[];
    private Card mainDeck[];

    private Card winningCard;
    private Player winningCardPlayer;
    private int  sumCardDroppedPli;

    private GameState state;
    private CardFamily firstCard;

    public Table()
    {
        this.teams = new Team[2];
        this.teams[0] = new Team();
        this.teams[1] = new Team();
        this.midDeck = new Card[4];
        this.mainDeck = new Card[32];
        this.state = GameState.Init;
        this.sumCardDroppedPli = 0;
        this.initMainDeck();
    }

    public void initMainDeck()
    {
        CardFamily allFamily[] = CardFamily.values();
        CardName    allCard[] = CardName.values();
        int mainDeckIndex = 0;

        for (int a = 0; a < this.mainDeck.length; a += 1)
            this.mainDeck[a] = null;

        /* 4 family loop */
        for (int i = 0; i < 4; i += 1)
        {
            /* 8 card loop */
            for (int j = 0; j < 8; j += 1)
            {
                this.mainDeck[mainDeckIndex] = new Card(allFamily[i], allCard[j]);
                mainDeckIndex += 1;
            }
        }
    }

    public void AddSumCardDropped(int value)
    {
        this.sumCardDroppedPli += value;
    }

    public int getSumCardDroppedPli() {
        return sumCardDroppedPli;
    }

    public void setSumCardDroppedPli(int sumCardDroppedPli) {
        this.sumCardDroppedPli = sumCardDroppedPli;
    }

    public Card PickRandomCardInMainDeck()
    {
        int randomIndex = Tools.randomInt(0, 32);
        while (this.mainDeck[randomIndex] == null)
           randomIndex = Tools.randomInt(0, 32);
        Card pickedCard = this.mainDeck[randomIndex];
        this.mainDeck[randomIndex] = null;
        return pickedCard;
    }

    public Team getTeamOfPlayer(io.netty.channel.Channel channel)
    {
        SocketAddress playerSock =  channel.remoteAddress();
        if (Server.mainTable.getTeams()[0].getPlayers()[0].getChannel().remoteAddress() == playerSock
                || Server.mainTable.getTeams()[0].getPlayers()[1].getChannel().remoteAddress() == playerSock)
            return Server.mainTable.getTeams()[0];
       else if (Server.mainTable.getTeams()[1].getPlayers()[0].getChannel().remoteAddress() == playerSock
            || Server.mainTable.getTeams()[1].getPlayers()[1].getChannel().remoteAddress() == playerSock)
           return Server.mainTable.getTeams()[1];
        else
            return null;
    }

    public String getFormattedMidDeck()
    {
        String playerDeck = "[Table]";

        if (Server.countArray(this.midDeck) == 0)
            return "[Table] No Card\n";
        for (int i = 0; i < this.midDeck.length; i += 1)
        {
            if (this.midDeck[i] != null)
            {
                playerDeck += "[" + this.midDeck[i].getFamilyCard().toString() + "-" + this.midDeck[i].getFamilyName() + "]";
                if (i < this.midDeck.length)
                    playerDeck += " ";
            }
        }
        playerDeck += "\n";
        return playerDeck;
    }

    public Card[] getMainDeck() {
        return mainDeck;
    }

    public void setMainDeck(Card[] mainDeck) {
        this.mainDeck = mainDeck;
    }

    public CardFamily getAtout() {
        return atout;
    }

    public void setAtout(CardFamily atout) {
        this.atout = atout;
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public Card[] getMidDeck() {
        return midDeck;
    }

    public void setMidDeck(Card[] midDeck) {
        this.midDeck = midDeck;
    }

    public Card getWinningCard() {
        return winningCard;
    }

    public void setWinningCard(Card winningCard) {
        this.winningCard = winningCard;
    }

    public Player getWinningCardPlayer() {
        return winningCardPlayer;
    }

    public void setWinningCardPlayer(Player winningCardPlayer) {
        this.winningCardPlayer = winningCardPlayer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }


    public CardFamily getFirstCardFamily() {
        return firstCard;

    }



    public void setFirstCardFamily(CardFamily firstCard) {
        this.firstCard = firstCard;

    }





    public void PushCardOnMid(Card card, int index)

    {
        this.midDeck[index] = card;

    }





    public String checkValueAtout(String first, String second)

    {
        if (first.equalsIgnoreCase("valet") || first.equalsIgnoreCase("neuf") && (!second.equalsIgnoreCase("valet")) ||
                (first.equalsIgnoreCase("as") && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))) ||
                (first.equalsIgnoreCase("dix") && (!second.equalsIgnoreCase("as")) && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))) ||
                (first.equalsIgnoreCase("roi") && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as")) && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))) ||
                (first.equalsIgnoreCase("dame") && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as")) && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))) ||
                ((first.equalsIgnoreCase("sept") || first.equalsIgnoreCase("huit")) && (!second.equalsIgnoreCase("dame")) && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as")) && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))))
        {
            return first;
        }
        else
        {
            return second;
        }

    }



    public String checkValueNonAtout(String first, String second)

    {
        if (first.equalsIgnoreCase("as") || first.equalsIgnoreCase("dix") && (!second.equalsIgnoreCase("as")) ||
                (first.equalsIgnoreCase("roi") && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as"))) ||
                (first.equalsIgnoreCase("dame") && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as"))) ||
                (first.equalsIgnoreCase("valet") && (!second.equalsIgnoreCase("dame")) && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as"))) ||
                (first.equalsIgnoreCase("dame") && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as")) && (!second.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet"))) ||
                ((first.equalsIgnoreCase("sept") || first.equalsIgnoreCase("huit") || first.equalsIgnoreCase("neuf")) && (!second.equalsIgnoreCase("valet")) && (!second.equalsIgnoreCase("dame")) && (!second.equalsIgnoreCase("roi")) && (!second.equalsIgnoreCase("dix")) && (!second.equalsIgnoreCase("as"))))
        {
            return first;
        }
        else
        {
            return second;
        }

    }



    public boolean checkMid()

    {
        if (this.midDeck[0] == null)
        {
            return true;
        }
        else {
            System.out.println(this.midDeck[0].toString());
            return false;
        }

    }

    public void initMidDeck()

    {
        for (int i = 0; i < this.midDeck.length; i  += 1)
        {
            this.midDeck[i] = null;
        }

    }


}