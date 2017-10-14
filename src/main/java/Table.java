public class Table
{
    private CardFamily atout;
    private Team teams[];
    private Card midDeck[];
    private Card winningCard;
    private Team winningCardTeam;
    private GameState state;

    public Table()
    {
        this.teams = new Team[2];
        this.teams[0] = new Team();
        this.teams[1] = new Team();
        this.midDeck = new Card[4];
        this.state = GameState.Init;
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