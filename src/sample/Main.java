/*Programmed by: Nick N. and Gavin B.
Date Submitted: 4/25/2021
CPT-237 Java Programming Project*/

package sample;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//This class will be used for Main UI functionalities
public class Main extends Application {

    public static Stage window;
    public static BorderPane mainGameScenePane = new BorderPane();
    
    public static Boolean isPaused = false;
    
    public static String[] listOfColors = new String[]{"Red", "Green", "Blue", "Yellow"};
    public static ArrayList<Card> deck = GameLogic.DeckBuilder();
    
    //opponent + player objects
    public static CardView opponent;
    public static CardView player;
    
    //used to show turn
    public static boolean isPlayerTurn = true;
    
    //the opponent hand
    public static List<String> opponentHand = new ArrayList<String>();
    
    //discard and draw piles
    public static ImageView discardPile;
    public static ImageView drawPile;
    
    //current Color
    public static String currentColor;
    
    //Current Number
    public static String currentNumber;
    
    //public gamescene
    public static Scene GameScene;
    
    //call uno button
    public static Button callUno;

    @Override
    public void start(Stage primaryStage) throws Exception{

        window = primaryStage;
        //window = primaryStage;
        window.setResizable(false);
        //Main Title Screen Pane
        BorderPane titleScreenPane = new BorderPane();
        titleScreenPane.setId("TitleScreenPane");

    //-----------Start of BorderPane.Center--------------------------
        //Play button
        Button playButton = new Button("Play");
        playButton.setId("PlayButton");
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GameScene();
            }
        });
        //Quit button
        Button quitButton = new Button("Quit");
        quitButton.setId("QuitButton");
        quitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        //BorderPane Center HBox
        HBox center = new HBox(playButton, quitButton);
        center.setId("CenterHBox");
        BorderPane.setMargin(center, new Insets(430, 0, 0 ,0));
        titleScreenPane.setCenter(center);
        
    //---------End of BorderPane.Center------------------------------

        window.setTitle("Uno Game");
        Scene sceneTitle = new Scene(titleScreenPane, 1000, 600);
        sceneTitle.getStylesheets().add(getClass().getResource("TitlePage.css").toExternalForm());
        window.setScene(sceneTitle);
        window.show();
        window.requestFocus();
        //===================
        try {
            musicPlayer playMusic = new musicPlayer("sounds/casino.wav");
            playMusic.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
        //===================
    }
    private void GameScene(){
        //===================
        //calls sound player to play shuffle sound effect
        try {
            soundPlayer playAudio = new soundPlayer("sounds/shuffle.wav");
            playAudio.play();
        }

        catch (Exception ex) {
            ex.printStackTrace();
        }
        //===================

        //used to allow things to stack on top of one another in the scene where its needed
        StackPane topStack = new StackPane();
        mainGameScenePane.setId("MainGameScenePane");
        topStack.getChildren().add(mainGameScenePane);
        
        
        //-----------Start of BorderPane.Top---------------------------
        //opponent cards GridPane
        GridPane opponentCardHolderGridPane = new GridPane();
        opponentCardHolderGridPane.setId("OpponentCardHolderGridPane");
        opponentCardHolderGridPane.prefWidth(715);
        opponentCardHolderGridPane.maxWidth(715);
        //Card holder HBox
        HBox opponentCardHolder = new HBox();
        opponentCardHolder.setId("OpponentCardHolder");
        opponentCardHolder.toFront();
        HBox.setHgrow(opponentCardHolder, Priority.ALWAYS);
        opponentCardHolderGridPane.add(opponentCardHolder,0,0);
        GridPane.setValignment(opponentCardHolder, VPos.CENTER);
        GridPane.setHalignment(opponentCardHolder, HPos.CENTER);
        //Opponent Label
        Label opponentLabel = new Label("Opponent");
        opponentLabel.setId("OpponentLabel");
        opponentLabel.setViewOrder(100);
        opponentCardHolderGridPane.add(opponentLabel,0,1);
        GridPane.setValignment(opponentLabel, VPos.CENTER);
        GridPane.setHalignment(opponentLabel, HPos.CENTER);
        opponent = new CardView(opponentCardHolder, opponentCardHolderGridPane,true);
        //temporary to display initial 7 cards and back of cards for opponent instead of the top
        opponent.DisplayMultipleCards(GameLogic.DrawCard(deck, 7));
        
        mainGameScenePane.setTop(opponentCardHolderGridPane);
        //----------------------End of Borderpane.Top
        
        
        //-----------------------------------Start of BorderPane.Bottom-----------------------------------
        //UserCardHolderGridPane -----(holds the userCardHolder HBox and the label)-------
        GridPane userCardHolderGridPane = new GridPane();
        userCardHolderGridPane.setId("UserCardHolderGridPane");
        userCardHolderGridPane.prefWidth(715);
        userCardHolderGridPane.maxWidth(715);
        //user Label
        Label userLabel = new Label("User");
        userLabel.setId("UserLabel");
        GridPane.setValignment(userLabel, VPos.CENTER);
        GridPane.setHalignment(userLabel, HPos.CENTER);
        userCardHolderGridPane.add(userLabel, 1, 0);
        //User Card Holder HBox
        HBox userCardHolder = new HBox();
        userCardHolder.setId("UserCardHolder");
        userCardHolderGridPane.add(userCardHolder, 1,1);
        GridPane.setValignment(userCardHolder, VPos.CENTER);
        GridPane.setHalignment(userCardHolder, HPos.CENTER);
        player = new CardView(userCardHolder, userCardHolderGridPane,false);
        //sets the userCardHolderGridPane to the bottom of the main borderpane
        mainGameScenePane.setBottom(userCardHolderGridPane);
        //-----------------------------------End of BorderPane.Bottom-----------------------------------
        
        
        //---------------------------------------------Start of BorderPane.Center-------------------------------------
        //Center Grid Pane
        GridPane centerPane = new GridPane();
        centerPane.setId("CenterGridPane");
        BorderPane.setMargin(centerPane,new Insets(0, 0, 10, 0));
        //-----------HBox setup for labels------
        HBox labelHolderCenter = new HBox();
        labelHolderCenter.setId("LabelHolderCenter");
        labelHolderCenter.setSpacing(70);
        GridPane.setValignment(labelHolderCenter, VPos.CENTER);
        GridPane.setHalignment(labelHolderCenter, HPos.CENTER);
        GridPane.setMargin(labelHolderCenter, new Insets(10, 0, 10, 0));
        //Discard Pile Label
        Label discardPileLabel = new Label("Discard Pile");
        discardPileLabel.setId("DiscardPileLabel");
        //Draw Card Label
        Label drawCardLabel = new Label("Draw Card");
        drawCardLabel.setId("DrawCardLabel");
        //adding labels to HBox
        labelHolderCenter.getChildren().addAll(drawCardLabel, discardPileLabel );
        //--------------------------------------
        //-----------HBox setup for draw and discard pile-----
        HBox drawAndDiscardHBox = new HBox();
        drawAndDiscardHBox.setId("DrawAndDiscardHBox");
        drawAndDiscardHBox.setSpacing(50);
        GridPane.setValignment(drawAndDiscardHBox, VPos.CENTER);
        GridPane.setHalignment(drawAndDiscardHBox, HPos.CENTER);

        //draw pile image view
        drawPile = new ImageView(new Image("backofunocard.png"));
        drawPile.setId("DrawPile");
        drawPile.setStyle("-fx-cursor: hand");
        drawPile.setFitHeight(160);
        drawPile.setFitWidth(90);
        //draws the initial 7 cards for the player
        player.DisplayMultipleCards(GameLogic.DrawCard(deck, 7));
        //sets an action method to drawPile that draws cards when clicked
        drawPile.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //if there is 28 cards or more then the method will exit
                if(userCardHolder.getChildren().size() >= 28) {
                    return;
                }
                //draws a card from the deck
                Card card = GameLogic.DrawCard(deck);
                //displays that card for the player
                player.DisplayCards(card);
                //disables player after drawing card
                player.setClickEventsDisable(true);
                //start Opponent AI
                PauseTransition opponentDelay = new PauseTransition(Duration.seconds(2));
                opponentDelay.setOnFinished(actionevent->{
                    isPlayerTurn = false;
                    player.opponentAI.opponentSearch();
                });
                opponentDelay.play();
                //prints deck size for testing purposes
                System.out.println(deck.size());
                System.out.println(userCardHolder.getHeight());
            }
        });
        //discard pile image view
        //gets next card after drawn cards --temporary because wildcard shouldn't be put there
        Label colorLabel = new Label();
        colorLabel.setId("ColorLabel");
        GridPane.setValignment(colorLabel, VPos.CENTER);
        GridPane.setHalignment(colorLabel, HPos.CENTER);
        GridPane.setMargin(colorLabel, new Insets(5,0,0,0));
        colorLabel.setPadding(new Insets(5,115,5,115));
        Card discardPileCard = GameLogic.DrawCard(deck);
        //checks what color item is in the first placed in the discard pile. If its a wild card, it randomly chooses a color
        if(discardPileCard.color.equals("Wild") || discardPileCard.color.equals("WildDraw")){
            Random r = new Random();
            currentColor = listOfColors[r.nextInt(listOfColors.length)];
            currentNumber = discardPileCard.number;
            setColor(colorLabel);
        }
        //if the card is a normal color, it uses the color
        else{
            currentColor = discardPileCard.color;
            currentNumber = discardPileCard.number;
            setColor(colorLabel);
        }
       //sets the image of the discard pile
        discardPile = new ImageView(discardPileCard.GetImage());
        discardPile.setId("DiscardPile");
        discardPile.setFitHeight(160);
        discardPile.setFitWidth(90);
        drawAndDiscardHBox.getChildren().addAll(drawPile,discardPile);

        //----------------------------------------------------
        //pause menu setup
        window.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ESCAPE)){
                if(!isPaused){
                    CreatePauseMenu();
                }
            }
        });

        //call Uno
        callUno = new Button();
        callUno.setId("CallUno");
        callUno.setOnAction(actionEvent -> {
            CardView.calledUno = true;
            System.out.println("Clicked Uno");
            callUno.setVisible(false);
            if(isPlayerTurn){
                player.setClickEventsDisable(false);
            }
        });
        callUno.setVisible(false);
        //creates an image view that will be used as a grqphic for the call uno button
        ImageView callUnoImageView = new ImageView(new Image("uno_button.png"));
        callUnoImageView.setId("CallUnoImageView");
        callUnoImageView.setFitWidth(140);
        callUnoImageView.setFitHeight(100);
        callUno.setGraphic(callUnoImageView);
        //creates ellipse that will be used to clip the button
        Ellipse ellipseClip = new Ellipse(80,59,77,50.5);
        ellipseClip.setRotate(-27);
        //clips the button to be like the ellipse so that it matches the uno Image better
        callUno.setClip(ellipseClip);
        topStack.getChildren().add(callUno);

        centerPane.add(labelHolderCenter, 0,0);
        centerPane.add(drawAndDiscardHBox, 0,1);
        centerPane.add(colorLabel, 0, 2);
        mainGameScenePane.setCenter(centerPane);
        //---------------------------------------------End of BorderPane.Center-------------------------------------
        //creates the scene, sets the stylesheet and sets the scene
        GameScene = new Scene(topStack,1000,600);
        GameScene.getStylesheets().add(getClass().getResource("GameScene.css").toExternalForm());
        window.setScene(GameScene);
        window.requestFocus();
    }

    public void CreatePauseMenu(){
        if(isPaused){
            return;
        }
        Stage pauseMenuStage = new Stage(StageStyle.TRANSPARENT);
        pauseMenuStage.initOwner(window);
        pauseMenuStage.initModality(Modality.WINDOW_MODAL);
        
        BorderPane pauseRoot = new BorderPane();
        pauseRoot.setId("PauseRoot");
        
        Label pauseLabel = new Label("Paused");
        pauseLabel.setId("PauseLabel");
        pauseLabel.setPadding(new Insets(5, 100, 5,100));
        BorderPane.setAlignment(pauseLabel, Pos.CENTER);
        
        pauseRoot.setTop(pauseLabel);

        GridPane centerGridPane = new GridPane();
        BorderPane.setMargin(centerGridPane, new Insets(0,0,100,0));
        centerGridPane.setId("CenterGridPane");
        centerGridPane.setAlignment(Pos.CENTER);

        //create Resume button
        Button resumeButton = new Button("Resume");
        resumeButton.setId("ResumeButton");
        resumeButton.setOnAction(event -> {
            pauseMenuStage.close();
            isPaused = false;
        });
        resumeButton.setPrefSize(100,50);
        GridPane.setValignment(resumeButton, VPos.CENTER);
        GridPane.setHalignment(resumeButton, HPos.CENTER);
        centerGridPane.add(resumeButton,0,0);

        //Restart button
        Button restartButton = new Button("Restart");
        restartButton.setId("RestartButton");
        restartButton.setOnAction(event -> {
            //temp scene
            mainGameScenePane = new BorderPane();
            pauseMenuStage.close();
            isPaused = false;
            deck = GameLogic.DeckBuilder();
            opponentHand.clear();
            player.setClickEventsDisable(false);
            isPlayerTurn = true;
            GameScene = null;
            GameScene();
        });
        restartButton.setPrefSize(100,50);
        GridPane.setValignment(restartButton, VPos.CENTER);
        GridPane.setHalignment(restartButton, HPos.CENTER);
        centerGridPane.add(restartButton, 0, 1);
        
        //Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setId("ExitButton");
        exitButton.setOnAction(event -> {
            System.exit(0);
        });
        exitButton.setPrefSize(100,50);
        GridPane.setValignment(exitButton, VPos.CENTER);
        GridPane.setHalignment(exitButton, HPos.CENTER);
        centerGridPane.add(exitButton, 0,2);

        //adds button vbox
        pauseRoot.setCenter(centerGridPane);

        Scene PauseScene = new Scene(pauseRoot, 700, 450, Color.TRANSPARENT);
        //sets the pause menu to be in the middle of the Uno screen each time it opens
        //for some reason, the Y value is slightly offset so I added 30
        pauseMenuStage.setY(window.getY() + (window.getScene().getHeight() / 2) - (PauseScene.getHeight() / 2) + 30);
        pauseMenuStage.setX(window.getX() + (window.getScene().getWidth() / 2) - (PauseScene.getWidth() / 2));
        PauseScene.getStylesheets().add(getClass().getResource("PauseMenu.css").toExternalForm());
        pauseMenuStage.setScene(PauseScene);
        pauseMenuStage.show();
        isPaused = true;
    }

    public void CreateWildColorPicker(Label ColorLabel, Stage window, boolean isOpponent){

        if(isOpponent){
            Random r = new Random();
            currentColor = listOfColors[r.nextInt(listOfColors.length)];
            setColor(ColorLabel);
            return;
        }
        //creates base stage
        Stage colorPickerStage = new Stage(StageStyle.UNDECORATED);
        colorPickerStage.initOwner(window);
        colorPickerStage.initModality(Modality.WINDOW_MODAL);
        //Creates main color picker borderpane
        //creates a gridpane for buttons
        GridPane gridPane = new GridPane();

        //red button
        Button redButton = new Button();
        redButton.setId("RedButton");
        redButton.setOnAction(actionEvent -> {
            //sets currentColor to the new color
            currentColor = "Red";
            setColor(ColorLabel);
            colorPickerStage.close();
        });
        redButton.setPrefSize(248,207);
        
        //blue button
        Button blueButton = new Button();
        blueButton.setId("BlueButton");
        blueButton.setOnAction(actionEvent -> {
            currentColor = "Blue";
            setColor(ColorLabel);
            colorPickerStage.close();
        });
        blueButton.setPrefSize(248,207);
        
        //green button
        Button greenButton = new Button();
        greenButton.setId("GreenButton");
        greenButton.setOnAction(actionEvent -> {
            currentColor = "Green";
            setColor(ColorLabel);
            colorPickerStage.close();
        });
        greenButton.setPrefSize(248,207);
        
        //yellow button
        Button yellowButton = new Button();
        yellowButton.setId("YellowButton");
        yellowButton.setOnAction(actionEvent -> {
            currentColor = "Yellow";
            setColor(ColorLabel);
            colorPickerStage.close();
        });
        yellowButton.setPrefSize(248,207);

        gridPane.add(redButton, 0, 0);
        gridPane.add(greenButton, 0, 1);
        gridPane.add(yellowButton, 1, 0);
        gridPane.add(blueButton, 1, 1);

        Scene colorPickerScene = new Scene(gridPane, 496, 414, Color.TRANSPARENT);
        //sets the pause menu to be in the middle of the Uno screen each time it opens
        //for some reason, the Y value is slightly offset so I added 30
        colorPickerStage.setY(window.getY() + (window.getScene().getHeight() / 2) - (colorPickerScene.getHeight() / 2) + 30);
        colorPickerStage.setX(window.getX() + (window.getScene().getWidth() / 2) - (colorPickerScene.getWidth() / 2));
        colorPickerScene.getStylesheets().add(getClass().getResource("ColorPickerMenu.css").toExternalForm());
        colorPickerStage.setScene(colorPickerScene);
        colorPickerStage.showAndWait();
    }

    public void WinScreen(boolean victory){
        if(isPaused){
            return;
        }
        //creates base stage
        Stage winStage = new Stage(StageStyle.TRANSPARENT);
        
        //makes the primary stage the owner of this stage
        winStage.initOwner(window);
        
        //makes the stage overlay on the owner stage
        winStage.initModality(Modality.WINDOW_MODAL);
        
        //create base pause borderPane
        BorderPane mainWinBorderPane = new BorderPane();
        
        mainWinBorderPane.setId("MainWinBorderPane");

        if (victory){
            //PauseLabel
            Label winLabel = new Label("You Win!");
            winLabel.setId("WinLabel");
            winLabel.setPadding(new Insets(5, 100, 5,100));
            winLabel.setTextFill(Color.color(1,1,1));
            BorderPane.setAlignment(winLabel, Pos.CENTER);
            
            //set top of borderpane
            mainWinBorderPane.setTop(winLabel);
            
            //set top of borderpane
            mainWinBorderPane.setTop(winLabel);
        }
        else {
            Label loseLabel = new Label("You Lose!");
            loseLabel.setId("LoseLabel");
            loseLabel.setPadding(new Insets(5, 100, 5,100));
            loseLabel.setTextFill(Color.color(1,1,1));
            BorderPane.setAlignment(loseLabel, Pos.CENTER);
            
            //set top of borderpane
            mainWinBorderPane.setTop(loseLabel);
            
            //set top of borderpane
            mainWinBorderPane.setTop(loseLabel);
        }

        GridPane centerGridPane = new GridPane();
        BorderPane.setMargin(centerGridPane, new Insets(0,0,100,0));
        centerGridPane.setId("CenterGridPane");
        centerGridPane.setAlignment(Pos.CENTER);

        //Restart button
        Button playAgainButton = new Button("Play Again?");
        playAgainButton.setId("PlayAgainButton");
        playAgainButton.setOnAction(event -> {
            //temp scene
            mainGameScenePane = new BorderPane();
            winStage.close();
            isPaused = false;
            deck = GameLogic.DeckBuilder();
            opponentHand.clear();
            player.setClickEventsDisable(false);
            isPlayerTurn = true;
            GameScene = null;
            GameScene();
        });
        playAgainButton.setPrefSize(150,50);
        GridPane.setValignment(playAgainButton, VPos.CENTER);
        GridPane.setHalignment(playAgainButton, HPos.CENTER);
        centerGridPane.add(playAgainButton, 0, 0);
        
        //Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setId("ExitButton");
        exitButton.setOnAction(event -> {
            System.exit(0);
        });
        exitButton.setPrefSize(100,50);
        GridPane.setValignment(exitButton, VPos.CENTER);
        GridPane.setHalignment(exitButton, HPos.CENTER);
        centerGridPane.add(exitButton, 0,1);

        //adds button vbox
        mainWinBorderPane.setCenter(centerGridPane);

        Scene PauseScene = new Scene(mainWinBorderPane, 700, 450, Color.TRANSPARENT);
        winStage.setY(window.getY() + (window.getScene().getHeight() / 2) - (PauseScene.getHeight() / 2) + 30);
        winStage.setX(window.getX() + (window.getScene().getWidth() / 2) - (PauseScene.getWidth() / 2));
        
        if (victory){  
         PauseScene.getStylesheets().add(getClass().getResource("WinScreen.css").toExternalForm());
        }
        else {
         PauseScene.getStylesheets().add(getClass().getResource("LoseScreen.css").toExternalForm());
        }
        winStage.setScene(PauseScene);
        winStage.show();
        isPaused = true;
    }
    public void setColor(Label colorLabel){
        if(currentColor.equals("Blue")){
            colorLabel.setStyle("-fx-background-color: #0098dc");
        }
        else if(currentColor.equals("Green")){
            colorLabel.setStyle("-fx-background-color: #33984b");
        }
        else if(currentColor.equals("Red")){
            colorLabel.setStyle("-fx-background-color: #ea323c");
        }
        else if(currentColor.equals("Yellow")){
            colorLabel.setStyle("-fx-background-color: #ffc825");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
