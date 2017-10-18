import io.netty.channel.Channel;

public class Player {
    private Card deck[];
    private String name;
    private Channel channel;

    private static int count = 0;

    public Player(String name, Channel channel) {
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

    public boolean addCard(Card newCard) {
        if (newCard == null)
            return false;
        for (int i = 0; i < this.deck.length; i += 1) {
            if (this.deck[i] == null) {
                this.deck[i] = newCard;
                return true;
            }
        }
        return false;
    }

    public String getFormatedDeck() {
        String playerDeck = "[Hand]";

        if (Server.countArray(this.deck) == 0)
            return "[Hand] No Card\n";
        for (int i = 0; i < this.deck.length; i += 1) {
            if (this.deck[i] != null) {
                playerDeck += "[" + this.deck[i].getFamilyCard().toString() + "-" + this.deck[i].getFamilyName() + "]";
                if (i < this.deck.length)
                    playerDeck += " ";
            }
        }
        playerDeck += "\n";
        return playerDeck;
    }

    public boolean searchFirstcardFamily(String str1) {
        int i = 0;
        while (i < this.deck.length) {
            System.out.println(str1);
            if (this.deck[i] == null) {
                i += 1;
            } else if (this.deck[i] != null) {
                if (str1.equalsIgnoreCase(this.deck[i].getFamilyCard().toString()))
                    return true;
                i += 1;
            }
        }
        return false;

    }


    public boolean searchAtout(String str1) {
        int i = 0;
        while (i < this.deck.length) {
            System.out.println(str1);
            if (this.deck[i] == null) {
                i += 1;
            } else if (this.deck[i] != null) {
                if (str1.equalsIgnoreCase(this.deck[i].getFamilyCard().toString()))
                    return true;
                i += 1;
            }
        }
        return false;

    }


    public boolean searchOnDeck(String str1, String str2) {
        int i = 0;
        while (i < this.deck.length) {
            System.out.println(i);
            System.out.println(str1 + "  " + str2);
            if (this.deck[i] == null) {
                i += 1;
            } else if (this.deck[i] != null) {
                if ((str1.equalsIgnoreCase(this.deck[i].getFamilyName().toString())) && (str2.equalsIgnoreCase(this.deck[i].getFamilyCard().toString()))) {
                    return true;
                }
                i += 1;
            }
        }
        return false;

    }


    public boolean removeOnDeck(String str1, String str2)

    {
        Table table = new Table();
        GameEngine gameEngine = new GameEngine();
        if (Server.countArray(this.deck) == 0)
            return false;
        for (int i = 0; i < this.deck.length; i += 1) {
            if (this.deck[i] != null) {
                if ((str1.equalsIgnoreCase(this.deck[i].getFamilyName().toString()))
                        && (str2.equalsIgnoreCase(this.deck[i].getFamilyCard().toString())))
                {
                    Server.mainTable.PushCardOnMid(this.deck[i], count);
                    if ((Server.mainTable.getAtout().toString().equalsIgnoreCase(this.deck[i].getFamilyCard().toString())))
                        Server.mainTable.AddSumCardDropped(this.deck[i].getFamilyName().getValue(true));
                    else
                        Server.mainTable.AddSumCardDropped(this.deck[i].getFamilyName().getValue(false));
                    this.deck[i] = null;
                    count += 1;
                    if (count == 4)
                        count = 0;
                }

            }
        }
        return  true;
    }



}
