package sample;

import javafx.scene.Node;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.util.*;

//this class will handle game logic, such as drawing/placing cards, etc.
public class GameLogic{
   public static ArrayList<Card> DeckBuilder(){
      //makes an array list for the deck, and one that's used to make the deck
      ArrayList<Card> deck = new ArrayList<Card>();
      ArrayList<String> deck_nums = new ArrayList<String>();
      
      //adds the numbers for the cards to the numbers list
      for (int i = 0; i < 10; i++)
      {
         deck_nums.add(Integer.toString(i));
         
         if (i > 0){
            deck_nums.add(Integer.toString(i));
         }
         if (i > 7){
            deck_nums.add("Draw");
            deck_nums.add("Reverse");
            deck_nums.add("Skip");
         }
      }
      
      //uses the deck making list to fill the deck with colored cards of each type (nums, action, etc)
      for (int x = 0; x < 4; x++) {
         for (String y : deck_nums)
         {
            switch(x){
               case 0:
                  deck.add(new Card("Red", y));
                  break;
               case 1:
                  deck.add(new Card("Yellow", y));
                  break;
               case 2:
                  deck.add(new Card("Blue", y));
                  break;
               case 3:
                  deck.add(new Card("Green", y));
                  break;
            }
         }
         
         //adds wild cards
         deck.add(new Card("Wild", "0"));
         deck.add(new Card("WildDraw", "0"));
      }
      ShuffleDeck(deck);
      return deck;
   }
   
   //randomizes the deck
   public static ArrayList<Card> ShuffleDeck(ArrayList<Card> deck) {
      Collections.shuffle(deck, new Random());
      return deck;
   }
   
   //draw a card
   public static Card DrawCard(ArrayList<Card> deck) {
      Card card = deck.get(0);
      deck.remove(0);
      return card;
   }
   
   //draw a number of cards
   public static ArrayList<Card> DrawCard(ArrayList<Card> deck, int number) {
      ArrayList<Card> cards = new ArrayList<Card>();
      for (int i = 0; i < number; i++){
         Card card = deck.get(i);
         deck.remove(i);
         cards.add(card);
      }
      return cards;
   }
   
   //remove a card
   public static ArrayList<Card> RemoveCard(ArrayList<Card> deck) {
      deck.remove(0);
      return deck;
   }
   
   //remove a number of cards
   public static ArrayList<Card> RemoveCard(ArrayList<Card> deck, int number) {
      for (int i = 0; i < number; i++) {
         deck.remove(0);
      }
      return deck;
   }
   public static boolean isPlaceable(Card card, boolean isOpponent){
      if(card.color.equals("WildDraw")){
         return isWildPlaceable(card,isOpponent);
      }
      else if(card.color.equals("Wild")){
         return true;
      }
      else if(card.color.equals(Main.currentColor) || card.number.equals(Main.currentNumber)){
         return true;
      }
      else
         return false;
   }
   public static boolean isWildPlaceable(Card cardWild, boolean isOpponent){
      //if it is the player trying to place
      if(!isOpponent){
         //if the wild draw card is the last in the players hand, he cant place it.

         //checks to see if you can place a wild card by seeing if there are any cards in your hand that match the color in the discard pile. if there is, you can't place the wild card
         for(Node i: Main.player.CardHolder.getChildren()){
            ImageView cardImageView = (ImageView) i;
            Card card = new Card(Main.player.getCardImageName(cardImageView));
            if(Main.currentColor.equals(card.color) && !Main.currentColor.equals(cardWild.color) && !Main.currentNumber.equals("0")){
               return false;
            }
         }
      }
      //if it is the opponent trying to place
      else{
         //if the wild draw card is the last in the opponents hand, he cant place it.
         if(Main.opponentHand.size() == 1){
            return false;
         }
         //checks to see if you can place a wild card by seeing if there are any cards in your hand that match the color in the discard pile. if there is, you can't place the wild card
         for(String i : Main.opponentHand){
            Card card = new Card(i);
            if(Main.currentColor.equals(card.color) && !Main.currentColor.equals(cardWild.color) && !Main.currentNumber.equals("0")){
               return false;
            }
         }
      }
      return true;
   }

   public static boolean isLastCard(Boolean isOpponent){
      //checks to see if the card being placed is the last in hand
      if(isOpponent){
         return Main.opponentHand.size() == 1;
      }
      else{
         return Main.player.CardHolder.getChildren().size() == 1;
      }
   }
   //checks to see who the winner is
   public static boolean WinnerCheck(Card CurrentCard, Boolean isOpponent, Boolean isLastCard){
      if(!CurrentCard.color.equals("WildDraw") && !CurrentCard.number.equals("Draw") && isLastCard){
         if(isOpponent){
            Main.player.WinScreen(false);
         }
         else{
            Main.player.WinScreen(true);
         }
         return true;
      }
      return false;
   }
}