public class Card
{
    private CardFamily familyCard;
    private CardName familyName;

    public Card(CardFamily familyCard, CardName familyName){
        this.familyCard = familyCard;
        this.familyName = familyName;
    }

    public CardFamily getFamilyCard() {
        return familyCard;
    }

    public CardName getFamilyName() {
        return familyName;
    }
}
