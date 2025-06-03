package com.shatteredpixel.shatteredpixeldungeon.minigames;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class BlackJack {

     private final static ArrayList<Integer> defaultCards = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,
                                                                                         1,2,3,4,5,6,7,8,9,10,11,12,13,
                                                                                         1,2,3,4,5,6,7,8,9,10,11,12,13,
                                                                                         1,2,3,4,5,6,7,8,9,10,11,12,13));
    private static final ArrayList<Integer> cards = new ArrayList<>();
    public static ArrayList<Integer> PCcards = new ArrayList<>();
    public static ArrayList<Integer> AIcards = new ArrayList<>();

    public static int PCscore;
    public static int AIscore;

    public static int PCnum = 0;
    public static int AInum = 0;

    public static boolean PCdraw;
    public static boolean AIdraw;

    public static int turn;

     public static void shuffle() {

         for (int card : defaultCards) {
             int number = card;
             cards.add(number);
         }
     }

    public static void resetHand() {

        if (PCcards.size() > 0) {
            PCcards.subList(0, PCcards.size()).clear();
        }
        PCnum = 0;

        if (AIcards.size() > 0) {
            AIcards.subList(0, AIcards.size()).clear();
        }
        AInum = 0;

    }

     public static int randomCard() {

         if (cards.size() == 0) {
             shuffle();
         }

         int a = Random.Int(cards.size());
         int b = cards.get(a);
         cards.remove(a);

         if (cards.size() == 0) {
             shuffle();
         }

         return b;
     }

     public static int count(ArrayList<Integer> arrayList) {

         int totalNum = 0;
         boolean haveA = false;

         for (int card : arrayList) {
             totalNum += Math.min(card, 10);
             if (card == 1) {
                 haveA = true;
             }
         }

         if (totalNum > 21) {return 0;}

         if (totalNum <= 11 && haveA) {
             totalNum += 10;
         }

         if (totalNum == 21 && haveA) {
             totalNum ++;
         }

         return totalNum;
     }

     public static void drawCard(ArrayList<Integer> arrayList) {

         int card = randomCard();

         arrayList.add(card);
     }

     public static void gameStart() {

         resetHand();
         turn = 1;

         PCdraw = false;
         AIdraw = false;

         drawCard(PCcards);
         drawCard(PCcards);
         drawCard(AIcards);
         drawCard(AIcards);

         PCnum = count(PCcards);
         AInum = count(AIcards);
     }

     public static void nextTurn() {

         turn++;

         AI();

         if (!PCdraw && !AIdraw) {
             endGame();
             return;
         }
         if (PCdraw) {
             drawCard(PCcards);
         }

         if (AIdraw) {
             drawCard(AIcards);
         }

         PCnum = count(PCcards);
         AInum = count(AIcards);



         GameScene.show(new WndBlackJack(false));
     }

     public static void endGame() {

         GameScene.show(new WndBlackJack(true));

//         if (PCnum > AInum) {
//             GameScene.show(new WndMessage("你赢了，真棒！"));
//             PCscore++;
//
//         } else if (PCnum < AInum) {
//             GameScene.show(new WndMessage("你输了，不要灰心"));
//             AIscore++;
//         } else {
//             GameScene.show(new WndMessage("平局，继续加油"));
//         }
     }

     public static void AI() {

         float num = 0;

         for (int card : cards) {
             num += Math.min(card, 10);
         }

         num = num / cards.size();

         if (AInum > 20 || AInum == 0 || AIcards.size() == 5) {
             AIdraw = false;
             return;
         }

         if (AInum < 12) {
             AIdraw = true;
             return;
         }

         if (num + AInum <= 21) {
             AIdraw = !(Random.Float() < Math.pow(0.5f, Math.pow(((21 - AInum) / num), 3)) * AInum / (21 - num));
         }

         if (num + AInum > 21) {
             AIdraw = Random.Float() < Math.pow((21 - AInum) / num, 2);
         }
     }
}
