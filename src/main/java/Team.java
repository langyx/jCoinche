import java.nio.channels.Channel;

public class Team
{
    private Player Players[];
    private int    score;
    private int    bet;
    private int    coinche;
    private int    gameScore;

    public Team()
    {
       this.score = 0;
       this.bet = 0;
       this.coinche = 0;
       this.gameScore = 0;
       this.Players = new Player[2];

    }

    public Player[] getPlayers() {
        return Players;
    }

    public void setPlayers(Player[] players) {
        Players = players;
    }

    public void setNewPlayer(Player newPlayer, int indexPlayer)
    {
        this.Players[indexPlayer] = newPlayer;
    }

    public boolean removePlayer(io.netty.channel.Channel channelPlayer)
    {
        for (int i = 0; i < Server.countArray(this.Players); i += 1)
        {
            if (this.Players[i] != null
                    && this.Players[i].getChannel().remoteAddress() == channelPlayer.remoteAddress())
            {
                this.Players[i] = null;
                return true;
            }
        }
        return false;
    }

    public int getFirstFreePlayerIndex()
    {
        for (int i = 0; i < this.Players.length; i += 1)
        {
            if (this.Players[i]== null)
                return i;
        }
        return -1;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getBet()
    {
        return bet;
    }

    public void setBet(int bet)
    {
        this.bet = bet;
    }

    public int getCoinche()
    {
        return coinche;
    }

    public void setCoinche(int coinche)
    {
        this.coinche = coinche;
    }

    public int getGameScore()
    {
        return gameScore;
    }

    public void setGameScore(int gameScore)
    {
        this.gameScore = gameScore;
    }


}