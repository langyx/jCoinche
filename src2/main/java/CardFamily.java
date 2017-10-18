public enum CardFamily{
    Pique,
    Coeur,
    Trefle,
    Carreau;

    public static CardFamily getCardFamilyFromString(String name)
    {
        switch (name.toLowerCase())
        {
            case "pique":
                return Pique;

            case "coeur":
                return Coeur;

            case "trefle":
                return Trefle;

            case "carreau":
                return Carreau;

        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}