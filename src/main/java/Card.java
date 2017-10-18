import java.util.List;

public class Card
{
    private CardFamily familyCard;
    private CardName familyName;

    private List<Card> cards;

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


    public void removecard(Card card)
     {
         if (cards.contains(card))
             cards.remove(cards.indexOf(card));
     }
}
