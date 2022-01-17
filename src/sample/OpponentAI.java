package sample;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class OpponentAI {

    static CardView opponent;
    static boolean nocardplaceable = true;
    public void opponentSearch(){
        //boolean that will be used to check if there are no placeable cards
        nocardplaceable = true;
        //gets the opponent from main and sets it to opponent
        opponent = Main.opponent;
        //creates a sequential transition that will be used to do actions slowly for the AI
        SequentialTransition cardSearchTransition = new SequentialTransition();
        if(Main.isPlayerTurn){
            return;
        }
        //loops through the apponentHand array which is a list of cards the opponent has
        for(String i : Main.opponentHand){
            //creates a pause transition for rasing the cards
            PauseTransition cardRaiseTransition;
            //if the its the first card, there is no delay. After there that, there will be
            if(Main.opponentHand.indexOf(i) == 0){
                cardRaiseTransition = new PauseTransition(Duration.seconds(0));
            }
            else{
                cardRaiseTransition = new PauseTransition(Duration.seconds(.35));
            }
            //gets the image view of the card in the potion of the opponenthand string
            ImageView cardImageView = (ImageView) opponent.CardHolder.getChildren().get(Main.opponentHand.indexOf(i));
            //sets the cards to raise on finish and sets all other cards to lower
            cardRaiseTransition.setOnFinished(actionevent->{
                for(Node j : opponent.CardHolder.getChildren()){
                    ImageView imgView = (ImageView) j;
                    if(imgView != cardImageView){
                        imgView.setTranslateY(0);
                    }
                }
                if (Main.opponentHand.indexOf(i) > 0) {
                    opponent.CardHolder.getChildren().get(Main.opponentHand.indexOf(i) - 1).setTranslateY(0);
                }
                cardImageView.setTranslateY(55);
            });
            //adds the raise transition
            cardSearchTransition.getChildren().add(cardRaiseTransition);
            if(GameLogic.isPlaceable(new Card(i), true)){
                nocardplaceable = false;
                //if the card is placeable another pause transition will be added which will place the card after .4 seconds. It will also lower any other cards and remove the placed imageview
                PauseTransition placeCard = new PauseTransition(Duration.seconds(.4));
                placeCard.setOnFinished(actionEvent -> {
                    if (opponent.PlaceCard(new ImageView(i))) {
                        System.out.println("Opponent placed: " + i);
                        opponent.RemoveCardImageView(cardImageView);
                        Main.opponentHand.remove(i);
                        for(Node j : opponent.CardHolder.getChildren()){
                            ImageView imgView = (ImageView) j;
                            imgView.setTranslateY(0);
                        }
                    }
                });
                //adds the transition to the sequential transition and breaks so there will not be any more transitions added after that
                cardSearchTransition.getChildren().add(placeCard);
                break;
            }
        }
        //if no card is placeable, then a card will be drawn after .4 seconds and it will then be the players turn
        if(cardSearchTransition.getChildren().size() == Main.opponentHand.size() && nocardplaceable){
            //creates a transition to draw cards
            PauseTransition drawCardTransition = new PauseTransition(Duration.seconds(.4));
            drawCardTransition.setOnFinished(ActionEvent ->{
                for(Node i : opponent.CardHolder.getChildren()){
                    ImageView imgView = (ImageView) i;
                    imgView.setTranslateY(0);
                }
                Card drawCard = GameLogic.DrawCard(Main.deck);
                opponent.DisplayCards(drawCard);
                Main.player.setClickEventsDisable(false);
            });
            //adds the transition to the sequential transition
            cardSearchTransition.getChildren().add(drawCardTransition);
        }
        //plays the sequential transition
        cardSearchTransition.play();
    }
}
