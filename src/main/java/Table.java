import java.net.SocketAddress;
import java.nio.channels.Channel;

public class Table
{
    private CardFamily atout;
    private Team teams[];
    private Card midDeck[];
    private Card mainDeck[];
    private Card winningCard;
    private Team winningCardTeam;
    private GameState state;

    public Table()
    {
        this.teams = new Team[2];
        this.teams[0] = new Team();
        this.teams[1] = new Team();
        this.midDeck = new Card[4];
        this.mainDeck = new Card[32];
        this.state = GameState.Init;
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

    public Team getWinningCardTeam() {
        return winningCardTeam;
    }

    public void setWinningCardTeam(Team winningCardTeam) {
        this.winningCardTeam = winningCardTeam;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}