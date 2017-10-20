package server;

public enum CardName{
    AS,
    Sept,
    Huit,
    Neuf,
    Dix,
    Valet,
    Dame,
    Roi;

    @Override
    public String toString() {
        return super.toString();
    }

    public int getValue(boolean isAtout)
    {
        switch (this.toString().toLowerCase())
        {
            case "as":
                return 11;

            case "sept": case "huit":
                return 0;

            case "neuf":
                if (isAtout)
                    return 14;
                else
                    return 0;

            case "dix":
                return 10;

            case "valet":
                if (isAtout)
                    return 20;
                else
                    return 2;

            case "dame":
                return 3;

            case "roi":
              return 4;

            default:
                return 0;


        }
    }
}