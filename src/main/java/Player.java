import io.netty.channel.Channel;

public class Player
{
    private Card deck[];
    private String name;
    private Channel channel;

    public Player(String name, Channel channel)
    {
        this.deck = new Card[8];
        this.name = name;
        this.channel = channel;
    }

    public Card[] getDeck() {
        return deck;
    }

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addCard(Card newCard)
    {
        if (newCard == null)
            return false;
        for (int i = 0; i < this.deck.length; i += 1)
        {
            if (this.deck[i] == null)
            {
                this.deck[i] = newCard;
                return true;
            }
        }
        return false;
    }

    public String getFormatedDeck()
    {
        String playerDeck = "[Hand]";

        if (Server.countArray(this.deck) == 0)
            return "[Hand] No Card\n";
        for (int i = 0; i < this.deck.length; i += 1)
        {
            if (this.deck[i] != null)
            {
                playerDeck += "[" + this.deck[i].getFamilyCard().toString() + "-" + this.deck[i].getFamilyName() + "]";
                if (i < this.deck.length)
                    playerDeck += " ";
            }
        }
        playerDeck += "\n";
        return playerDeck;
    }
}
