package sample;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;


//This class will be used for adding, removing and placing cards for both the user and the opponent. As well as other card functionalities that is UI based
public class CardView extends Main{
    //variables are used for stacking the cards gradually on top of one another
    //the stack variable is used to count all the cards added after the initial 7 cards. After the initial 7 cards, the offset begins
    float opponentStackEquationAnswer = 0;
    float opponentStackEquationVariable = 0;
    float userStackEquationAnswer = 0;
    float userStackEquationVariable = 0;

    public static boolean calledUno = false;
    OpponentAI opponentAI = new OpponentAI();
    //constructor variables
    public GridPane CardHolderGridPane;
    public HBox CardHolder;
    public Boolean isOpponent;

    public CardView(HBox CardHolder, GridPane CardHolderGridPane, Boolean isOpponent){
        this.CardHolder = CardHolder;
        this.CardHolderGridPane = CardHolderGridPane;
        this.isOpponent = isOpponent;
    }
    public CardView() { }

    public void DisplayCards(Card card) {
        //allows the CardHolder to hold up to 28 cards
        if(CardHolder.getChildren().size() >= 28) {
            return;
        }
        //if deck size hits zero, it builds another deck so the game can keep going until someone wins.
        if(deck.size() == 0){
            deck = GameLogic.DeckBuilder();
        }

        if (isOpponent) {
            //sets the opponent stack offset if the stack equation is below or equal to 21
            if(opponentStackEquationVariable <= 21){
                setOpponentStackOffset(1);
            }
        } else {
            //sets the user offset if the stack equation is below or equal to 21
            if(userStackEquationVariable <= 21)
                setUserStackOffset(1);
        }
        //creates a new Image view that hold the card
        ImageView imgView;
        //if its the opponent it doesnt show the image of the card and also rotates it to mimic them holding it in their hand
        if(isOpponent){
            imgView = new ImageView("Back_0.png");
            opponentHand.add(card.GetImage());
            imgView.setRotate(180);
        }
        else {
            imgView = new ImageView(card.GetImage());
        }

        imgView.setFitHeight(90);
        imgView.setFitWidth(55);
        //attaches User Events
        if (!isOpponent) {
            attachUserEvents(imgView);
        }
        if(!isPlayerTurn && !isOpponent){
            imgView.setDisable(true);
            imgView.setEffect(new ColorAdjust(){{setBrightness(-.5);}});
        }
        //adds the image view to the cardholder box
        CardHolder.getChildren().add(imgView);
        //if the gamescene is not null, it plays sound when you place the card
        if(GameScene != null){
            try {
                soundPlayer audioPlayer = new soundPlayer("sounds/draw.wav");
                audioPlayer.play();
            }

            catch (Exception ex) {
                ex.printStackTrace();
            }
            BorderPane.setMargin(CardHolderGridPane, new Insets(5, 120, 5, 120));
        }
    }

    //this displays multiple cards that are in a Card array
    public void DisplayMultipleCards(ArrayList<Card> cards){
        for(Card card : cards){
            DisplayCards(card);
            BorderPane.setMargin(CardHolderGridPane, new Insets(5, 120, 5, 120));
        }
    }

    //sets the offset margin number for the opponent
    private void setOpponentStackOffset(int NegativeOrPositiveOne){
        //the value supplied has to either be a 1 or negative 1 to show that 1 is being added or subtracted to get the opponentStackEquationAnswer
        if(NegativeOrPositiveOne != 1 && NegativeOrPositiveOne != -1){
            throw new InvalidParameterException("Parameter has to be either a positive or negative 1 for calculateOpponentStackOffset method");
        }
        //adds or subtracts 1 from the opponent stack variable based on which parameter is supplied if the opponent has 7 or more cards
        if(CardHolder.getChildren().size() >= 7) {
            opponentStackEquationVariable = opponentStackEquationVariable + NegativeOrPositiveOne;
        }
        //calculates opponent stack answer based on the opponentStackVariable
        opponentStackEquationAnswer = ((55 * opponentStackEquationVariable) / (opponentStackEquationVariable + 32) + (0.1844863174285714f * opponentStackEquationVariable));

        //---these for loops set the margin depending on if it is adding or subtracting cards---
        if(NegativeOrPositiveOne == 1){
            //sets the offset for every card when card is added
            for (Node childNode : CardHolder.getChildren()) {
                //stacks the opponents cards from left to right from the opponents direction
                HBox.setMargin(childNode, new Insets(0, (-107 + opponentStackEquationAnswer), 0, 0));
            }
        }
        else{
            //sets all the cards in the hand except the last one to a specific margin when card is removed. Setting last one in the hand causes HBox to become off-centered
            for(int i = 0; i < CardHolder.getChildren().size() - 1; i++){
                if(CardHolder.getChildren().size() < 7){
                    HBox.setMargin(CardHolder.getChildren().get(i), new Insets(0, -107, 0, 0));
                }
                else
                    HBox.setMargin(CardHolder.getChildren().get(i), new Insets(0, (-107 + opponentStackEquationAnswer), 0, 0));
            }
        }
    }

    //sets the offset margin number for the user
    private void setUserStackOffset(int addOrSubtractOne){
        //the value supplied has to either be a 1 or negative 1 to show that 1 is being added or subtracted to get the userStackEquationAnswer
        if(addOrSubtractOne != 1 && addOrSubtractOne != -1){
            throw new InvalidParameterException("Parameter has to be either a positive or negative 1 for calculateUserStackOffset method");
        }
        //adds or subtracts 1 from the user stack variable based on which parameter is supplied if the user has 7 or more cards
        if(CardHolder.getChildren().size() >= 7) {
            userStackEquationVariable = userStackEquationVariable + addOrSubtractOne;
        }
        //calculates opponent stack answer based on the user stack variable
        userStackEquationAnswer = (((55 * userStackEquationVariable) / (userStackEquationVariable + 24))) + (0.1387301587285714f * userStackEquationVariable);

        //---these for loops set the margin depending on if it is adding or subtracting cards---
        if(addOrSubtractOne == 1){
            //sets the offset for every card when card is added
            for (Node childNode : CardHolder.getChildren()) {
                //stacks the users cards from left to right from the opponents direction
                HBox.setMargin(childNode, new Insets(0, (userStackEquationAnswer * -1), 0, 0));
            }
        }
        else{
            //sets all the cards in the hand except the last one to a specific margin when card is removed. Setting last one in the hand causes HBox to become off-centered
            for(int i = 0; i < CardHolder.getChildren().size() - 1; i++){
                if(CardHolder.getChildren().size() <= 7)
                    HBox.setMargin(CardHolder.getChildren().get(i), new Insets(0, 0, 0, 0));
                else
                    HBox.setMargin(CardHolder.getChildren().get(i), new Insets(0, (userStackEquationAnswer * -1), 0, 0));
            }
        }
    }


    public void RemoveCardImageView(ImageView imgView){
        //When you deleted the card last in the array, it causes the HBox
        //to be off centered so to fix that, I got the value of the card that is at the end of the array and then
        //set its margins equal to the card right before it before Image View is deleted
        if(CardHolder.getChildren().get(CardHolder.getChildren().size() - 1) == imgView && CardHolder.getChildren().size() > 1){
            HBox.setMargin(CardHolder.getChildren().get(CardHolder.getChildren().size() - 2), HBox.getMargin(imgView));
        }
        //deletes Image View
        CardHolder.getChildren().remove(imgView);
        //This whole section makes it so that as you start deleting cards, they gradually become un-overlapped
        if(!isOpponent){
            //calculates the card offset if the userStackEquationVariable is greater than 0 when a card is deleted
            if(userStackEquationVariable > 0)
                setUserStackOffset(-1);
        }
        else{
            //gets the offset for when cards are removed for the opponent
            if(opponentStackEquationVariable > 0)
                setOpponentStackOffset(-1);
        }
    }
    public Boolean PlaceCard(ImageView imgView){
        int cardNumber = CardHolder.getChildren().size();
        //gets the current card and uses it to create a new card object to easily check the color and number
        Card CurrentCard = new Card(getCardImageName(imgView));
        //Card discardPileCard = new Card(getCardImageName(discardPile));
        Label colorLabel = (Label)mainGameScenePane.getCenter().lookup("#ColorLabel");
        //sets the discardPile card color to the color of the label in case there is a wild card showing
        //discardPileCard.color = currentColor;
        boolean isPlaceable = GameLogic.isPlaceable(CurrentCard, isOpponent);
        //used to check if its the last card
        boolean isLastCard = GameLogic.isLastCard(isOpponent);
        //checks if the card is placeable and what type of card it is
        if(isPlaceable && CurrentCard.color.equals("Wild")){
            CreateWildColorPicker(colorLabel,window, isOpponent);
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            isPlayerTurn = isOpponent;
            System.out.println("Can place wild");
        }
        else if(isPlaceable && CurrentCard.color.equals("WildDraw")){
            CreateWildColorPicker(colorLabel,window, isOpponent);
            //sets discard pile image to the user card
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            //draw 4 for the other player
            ArrayList<Card> drawCards = GameLogic.DrawCard(deck,4);
            //adds the cards to the opponents hands slowly
            playCardTransition(drawCards);
            //if its the last card, it makes it no longer that users turn
            if(isLastCard){
                isPlayerTurn = isOpponent;
            }
            else{
                isPlayerTurn = !isOpponent;
            }
            System.out.println("Can place wilddraw");
        }
        else if(isPlaceable && CurrentCard.number.equals("Skip")){
            //sets image for the discard pile
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            //sets the color of the Label
            currentColor = CurrentCard.color;
            currentNumber = CurrentCard.number;
            setColor(colorLabel);
            //sets the player turn to the opposite of whoever is placing
            isPlayerTurn = !isOpponent;
        }
        else if (isPlaceable && CurrentCard.number.equals("Reverse")){
            //sets image for the discard pile
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            //sets the color of the Label
            currentColor = CurrentCard.color;
            currentNumber = CurrentCard.number;
            setColor(colorLabel);
            //sets the player turn to the opposite of whoever is placing
            isPlayerTurn = !isOpponent;
        }
        else if(isPlaceable && CurrentCard.number.equals("Draw")){
            //sets image for the discard pile
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            //gets the two cards off the top of the deck to add to the opponent
            ArrayList<Card> drawCards = GameLogic.DrawCard(deck,2);
            //adds the cards to the opponents hands slowly
            playCardTransition(drawCards);
            //sets the color of the Label
            currentColor = CurrentCard.color;
            currentNumber = CurrentCard.number;
            setColor(colorLabel);
            if(isLastCard){
                isPlayerTurn = isOpponent;
            }
            else{
                isPlayerTurn = !isOpponent;
            }
        }
        else if(isPlaceable){
            //sets image for the discard pile
            discardPile.setImage(new Image(CurrentCard.GetImage()));
            //sets the color of the Label
            currentColor = CurrentCard.color;
            currentNumber = CurrentCard.number;
            setColor(colorLabel);
            //sets the player turn to the opposite of whoever is placing
            isPlayerTurn = isOpponent;
        }
        else{
            //if card cant be placed it returns false
            return false;
        }
        //plays sound if card is placed
        try {
            soundPlayer audioPlayer = new soundPlayer("sounds/draw.wav");
            audioPlayer.play();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        //disables the player events depending on whose turn it is
        player.setClickEventsDisable(!isPlayerTurn);
        //creates a sequential transition to allow the transitions to be called one after another since they are normally async.
        SequentialTransition sequentialTransition = new SequentialTransition();
        //checks to see if the player called uno and if they didnt, then they have to draw 2 cards
        if(cardNumber == 2 && !isOpponent){
            //sets the call uno button to visible
            callUno.setVisible(true);
            //sets player click events to disabled
            player.setClickEventsDisable(true);
            //creates a transition that waits 2 seconds and then checks to see if uno was click. If it wasn't, 2 cards must be drawn
            PauseTransition unocallTransition = new PauseTransition(Duration.seconds(2));
            unocallTransition.setOnFinished(actionEvent -> {
                if(!calledUno){
                    player.playCardTransition(GameLogic.DrawCard(deck, 2));
                    if(isPlayerTurn){
                        isPlayerTurn = false;
                        PauseTransition newopponentDelay = new PauseTransition(Duration.seconds(2));
                        newopponentDelay.setOnFinished(actionEvent1 -> {
                            opponentAI.opponentSearch();
                        });
                    }
                }
                calledUno = false;
                callUno.setVisible(false);
            });
            //adds the transition to the sequential transition
            sequentialTransition.getChildren().add(unocallTransition);
        }
        //creates a delay for when the opponentAi starts depending on if cards are being placed or not
        PauseTransition opponentDelay;
        if(CurrentCard.color.equals("WildDraw")){
            opponentDelay = new PauseTransition(Duration.seconds(2.6));
        }
        else{
            opponentDelay = new PauseTransition(Duration.seconds(2));
        }
        //checks if winner is true and then returns true to stop the opponentAI from going
        if(GameLogic.WinnerCheck(CurrentCard, isOpponent, isLastCard)){
            return true;
        }
        //allows the opponent to go whenever it is the opponents turn and allows for skips
        if(!isOpponent && !isPlayerTurn){
            opponentDelay.setOnFinished(ActionEvent -> {
                opponentAI.opponentSearch();
            });
            sequentialTransition.getChildren().add(opponentDelay);
        }
        else if(isOpponent && !isPlayerTurn){
            opponentDelay.setOnFinished(ActionEvent -> {
                opponentAI.opponentSearch();
            });
            sequentialTransition.getChildren().add(opponentDelay);
        }
        //plays the transition
        sequentialTransition.play();
        return true;
    }
    //plays card add transition
    public void playCardTransition(ArrayList<Card> cards){
        //using wait to slowly add the cards does not work because it stops the whole thread. By using a Sequential transition,
        // it plays the transition and doesnt stop other code from functioning
        boolean isLastCard = GameLogic.isLastCard(isOpponent);
        SequentialTransition cardDelayTransition = new SequentialTransition();
        for(Card i : cards){
            //sets a transition for the amount of cards that need to be placed. so that they are placed gradually and not all at once
            PauseTransition cardDelay = new PauseTransition(Duration.seconds(.4));
            cardDelay.setOnFinished(ActionEvent -> {
                //this is backwards because the opponent would make the user draw cards and the player will make the opponent draw cards
                if(isOpponent){
                    //if the opponent places a wild card or draw card for last card, it causes them to draw that amount of cards because it is an illegal move. else it makes the user draw cards
                    if(isLastCard){
                        opponent.DisplayCards(i);
                    }
                    else{
                        player.DisplayCards(i);
                    }
                }
                else{
                    //if the user places a wild card or draw card for last card, it causes them to draw that amount of cards because it is an illegal move. else it makes the opponent draw cards
                    if(isLastCard){
                        player.DisplayCards(i);
                        isPlayerTurn = false;
                        setClickEventsDisable(true);
                    }
                    else {
                        opponent.DisplayCards(i);
                        isPlayerTurn = !isOpponent;
                    }
                }
            });
            cardDelayTransition.getChildren().add(cardDelay);
        }
        System.out.println(cardDelayTransition.getChildren().size());
        cardDelayTransition.play();
    }
    //disables or enables click events
    public void setClickEventsDisable(boolean bool) {
        if(isOpponent){
            return;
        }
        for (Node i : CardHolder.getChildren()) {
            //if true it disables the imageView and then sets the brightness to lower
            ImageView card = (ImageView) i;
            card.setDisable(bool);
            if(bool)
                card.setEffect(new ColorAdjust(){{setBrightness(-.5);}});
            else{
                card.setEffect(new ColorAdjust(){{setBrightness(0);}});
                isPlayerTurn = true;
            }

        }
        //also sets the draw pile as well depending on the supplied boolean
        if(bool)
            drawPile.setEffect(new ColorAdjust(){{setBrightness(-.5);}});
        else
            drawPile.setEffect(new ColorAdjust(){{setBrightness(0);}});
        drawPile.setDisable(bool);
    }
    //gets the card Image name from the ImageView
    public String getCardImageName(ImageView imgView){
        //splits the image url by '/' and then gets the last part in the array which would be the image name and returns it
        String[] cardNameArray = imgView.getImage().getUrl().split("/");
        return cardNameArray[cardNameArray.length - 1];
    }

    private void attachUserEvents(ImageView imgView){
        //sets the image view for the on mouse moved event
        setCurrentImageView(imgView);
    }

    //sets the currently hovered over image view and calls the any methods that require it for events
    private void setCurrentImageView(ImageView imgView){
        //uses setOnMouseEntered to set the imgView to the currently entered ImageView object
        imgView.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //calls the attachMouseMoveEvent using the imageView that the mouse is currently in bounds of
                attachMouseMovedEvents((ImageView) mouseEvent.getTarget());
            }
        });
    }
    //attaches the setonMouseMoved events
    private void attachMouseMovedEvents(ImageView imgView){
        mainGameScenePane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                attachRaiseCardEvent(imgView, mouseEvent);
                attachRemoveEvent(imgView);
            }
        });
    }
    //checks to see if the ImageView supplied has been deleted
    private boolean isImageViewDeleted(ImageView imgView){
        for(Node IV : CardHolder.getChildren()){
            if(imgView == (ImageView) IV){
                return false;
            }
        }
        window.getScene().setCursor(Cursor.DEFAULT);
        return true;
    }

    //this method causes trigger the cards to raise when the mouse is hovering over them
    private void attachRaiseCardEvent(ImageView imgView, MouseEvent mouseEvent){
        //checks to see if the image view has been deleted. if it has, it exits method
        if(isImageViewDeleted(imgView)){
            return;
        }
        //finds the localAreaOffset
        double localAreaOffset = setLocalAreaOffset(imgView);
        //gets the bounds of the card to the right of the current card to create a more accurate clickable area if there is a card to the right in the array
        ImageView cardToRight = imgView;
        if(CardHolder.getChildren().indexOf(imgView) +1 <= CardHolder.getChildren().size() - 1){
            cardToRight = (ImageView) CardHolder.getChildren().get(CardHolder.getChildren().indexOf(imgView) +1);
        }
        //check to see if the mouse is in the bounds of the imgView object and sets the imgView object to raise and then lowers any other object
        if(mouseEvent.getScreenY() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenY() < imgView.localToScreen(imgView.getBoundsInLocal()).getMaxY() + 55 && mouseEvent.getScreenX() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinX() && mouseEvent.getScreenX() < localAreaOffset || mouseEvent.getScreenY() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenY() < cardToRight.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenX() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinX() && mouseEvent.getScreenX() < imgView.localToScreen(imgView.getBoundsInLocal()).getMaxX()){
            //raises ImageView
            imgView.setTranslateY(-55);
            //If the mouse is on the currently raise card, it will show the cursor Hand.
            //This is done this way because for some reason when you translate an object, their clickable area stays in the same place even though the object has moved
            if(mouseEvent.getScreenY() < imgView.localToScreen(imgView.getBoundsInLocal()).getMaxY())
                window.getScene().setCursor(Cursor.HAND);
            else
                window.getScene().setCursor(Cursor.DEFAULT);
            //lowers every other ImageView object that isn't being hovered over
            for(Node childNode : CardHolder.getChildren()){
                if(childNode != imgView){
                    //childNode has to be cast to ImageView before translating
                    ImageView imageView = (ImageView) childNode;
                    imageView.setTranslateY(imageView.getY());
                }
            }
        }
        else{
            imgView.setTranslateY(imgView.getY());
            window.getScene().setCursor(Cursor.DEFAULT);
        }
        //for some reason, the cards on the ends wouldn't lower after you were no longer hovering over them and if you went off screen with the cursor,
        // sometimes the imgView would still be raise. This is lowers all the cards if they are out of bounds of the HBox which holds the cards.
        if(mouseEvent.getScreenX() > CardHolder.localToScreen(CardHolder.getBoundsInLocal()).getMaxX() || mouseEvent.getScreenX() < CardHolder.localToScreen(CardHolder.getBoundsInLocal()).getMinX() || mouseEvent.getScreenY() > CardHolder.localToScreen(CardHolder.getBoundsInLocal()).getMaxY() || mouseEvent.getScreenY() < CardHolder.localToScreen(CardHolder.getBoundsInLocal()).getMinY()){
            for(Node childNode : CardHolder.getChildren()){
                ImageView imageView = (ImageView) childNode;
                imageView.setTranslateY(imageView.getY());
            }
        }
    }

    //this method removes the ImageView that holds the card from the users hand when the user clicks and releases on the card
    private void attachRemoveEvent(ImageView imgView){
        //creates a set on mouse released event that will use the currently hovered over imgView object
        mainGameScenePane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //checks to see if the image view has been deleted. if it has, it exits method
                if(isImageViewDeleted(imgView)){
                    return;
                }
                //gets the localAreaOffset by calling setLocalAreaOffset method
                double localAreaOffset = setLocalAreaOffset(imgView);
                //gets card to the right make sure the clickable area is not being overlapped by the card next to it
                ImageView cardToRight = imgView;
                if(CardHolder.getChildren().indexOf(imgView) +1 <= CardHolder.getChildren().size() - 1){
                    cardToRight = (ImageView) CardHolder.getChildren().get(CardHolder.getChildren().indexOf(imgView) +1);
                }
                //checks if the mouse is in bounds of the ImageView and if it is, when its clicked, it removes the ImageView and sets the cursor to default
                if(mouseEvent.getScreenY() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenY() < imgView.localToScreen(imgView.getBoundsInLocal()).getMaxY() && mouseEvent.getScreenX() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinX() && mouseEvent.getScreenX() < localAreaOffset || mouseEvent.getScreenY() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenY() < cardToRight.localToScreen(imgView.getBoundsInLocal()).getMinY() && mouseEvent.getScreenX() > imgView.localToScreen(imgView.getBoundsInLocal()).getMinX() && mouseEvent.getScreenX() < imgView.localToScreen(imgView.getBoundsInLocal()).getMaxX() ){
                    //if placing card was successful, the card will be deleted
                    if(PlaceCard(imgView)){
                        RemoveCardImageView(imgView);
                    }
                    window.getScene().setCursor(Cursor.DEFAULT);
                }
            }
        });
    }
    //sets localAreaOffset for the cards clickable bounds
    private double setLocalAreaOffset(ImageView imgView){
        //local area offset is used to make sure that the size of the area needed to raise the card, changes when the cards are stacked on top of one another.
        //The last one on the right has no area offset as it is not being overlapped
        double localAreaOffset;
        if(CardHolder.getChildren().get(CardHolder.getChildren().size() - 1) != imgView)
            localAreaOffset = imgView.localToScreen(imgView.getBoundsInLocal()).getMaxX() - userStackEquationAnswer;
        else
            localAreaOffset = imgView.localToScreen(imgView.getBoundsInLocal()).getMaxX();
        return localAreaOffset;
    }
}
