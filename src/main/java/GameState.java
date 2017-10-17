public enum GameState{
    Init,
    Waitting,
    Bet,
    BetTraitement,
    Pli;

    @Override
    public String toString() {
        switch (super.toString())
        {
            case "Init":
                return "Waitting for players...";
            case "Waitting":
                return "Having break.";
            case "Bet":
                return "Bet round.";
            case "BetTraitement":
                return "Result of bet round.";
            case "Pli":
                return "Playing!";
            default:
                return "Idle.";
        }
    }
}