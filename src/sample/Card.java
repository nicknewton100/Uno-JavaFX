package sample;

public class Card{
   public Card (String color, String number){
      this.color = color;
      this.number = number;
      image = color + "_" + number + ".png";
   }
   //takes imageUrl to create a card --is used to check if card can be placed in discard pile ---
   public Card (String imageUrl){
      String[] imageUrlArray = imageUrl.split("_");
      image = imageUrl;
      color = imageUrlArray[0];
      number = imageUrlArray[1].replaceFirst(".png","");
   }
   //gets the image url
   public String GetImage(){
      System.out.println(image);
      return(image);
   }
   String color;
   String number;
   String image;

}