package ru.cource.preferance;

import java.util.ArrayList;

public class Player {

    ArrayList<Card> hand; //карты на руках игрока
    int condition; //состояние игрока(ставка/пас/мизер)
    int bribe; //количество предполагаемых взяток игрока, которые он может взять
    private int suit; //какую масть игроку стоит выбрать козырем
    private int nonBribe;
    String name;
    int gora, pool, whist1, whist2, whist;
    float finalScore;

    Player(String name){
        suit = 0;
        nonBribe = 0;
        condition = 29;
        gora = 0;
        pool = 0;
        whist2 = 0;
        whist1 = 0;
        finalScore = 0;
        whist = 0;

        this.name = name;
    }

    public void setCards(ArrayList<Card> cards){
        hand = new ArrayList<Card>();
        hand.addAll(cards);
    }

    void sortCard(){
        bribe = 0;
        nonBribe = 0;
        Card card = new Card(0);
        boolean sorted = false;
        while (!sorted){
            sorted = true;
            for (int i = 0; i < 9; i++){
                if (hand.get(i).value < hand.get(i + 1).value){
                    card.setCard(hand.get(i));
                    hand.get(i).setCard(hand.get(i + 1));
                    hand.get(i + 1).setCard(card);
                    sorted = false;
                }
            }
        }
    }

    void bribe(){ //определяем сколько взяток игрок сможет взять
        int suitBribe = 0;
        int prevSuitBribe = 0;
        for (int i = 0; i < hand.size(); i++){
            if (hand.get(i).value % 8 == 0) {
                bribe++; suitBribe++;
                suitBribe = as(i, suitBribe);
            }
            if (prevSuitBribe < suitBribe){
                prevSuitBribe = suitBribe;
                suit = hand.get(i).suit;
                suitBribe = 0;
            }
        }
    }

    void nonBribe(){
        for (int i = hand.size() - 1; i > 0; i--){
            if ((hand.get(i).value - 1) % 8 == 0){
                nonBribe++;
                as(i);
            }
        }
    }

    private int as(int n, int suitBribe){ //проверим, является ли карта продолжением регрессии, начиная с высшей(туза)
        n++;
        if (n >= hand.size())
            return suitBribe;
        if (hand.get(n - 1).value == hand.get(n).value + 1 && (hand.get(n).value) % 8 != 0) {
            bribe++; suitBribe++;
            as(++n, suitBribe);
        }
        return suitBribe;
    }

    private void as(int n){ //проверим, является ли карта продолжением прогрессии, начиная с низшей(семерки)
        if (n <= 0 || hand.get(n).value % 8 > 3)
            return ;
        if (hand.get(n).value == hand.get(n - 1).value - 1 && (hand.get(n - 1).value) % 8 != 0) {
            nonBribe++;
            as(--n);
        }
    }

    public void bidding1(int condition1, int condition2){ //начальный торг
        if (condition1 < 26 || condition2 < 26) {  //если кто то сделал ставку
            int cond = condition1 > condition2 ? condition1 : condition2;   //получаем наибольшую ставку из 2х др игроков
            if (bribe >= cond / 5 + 3 && cond / 5 < suit) { //если взяток больше
                if (bribe < 6) bribe++;
                if (cond >= bribe) // но все же взяток меньше чем у др игрока
                    condition = 26; // пас
                else {
                    condition = Game.choiceMap[bribe - 6][suit]; //делаем ставку
                }
            }
            if (nonBribe > 5 && bribe == 0 && cond < 9) { // если маленьких карт много
                condition = 25; //мизер
            }
        }
        if (condition1 == 25 && bribe > 8){
            condition = Game.choiceMap[bribe - 6][suit];
        }
        if (condition2 == 25 && bribe > 8){
            condition = Game.choiceMap[bribe - 6][suit];
        }
        if (bribe < 5 && nonBribe < 6){
            condition = 26;
        }
        if ((condition1 == 26 && condition2 == 26) || (condition1 == 29 && condition2 == 29) ||
                (condition1 == 26 && condition2 == 29) || (condition1 == 29 && condition2 == 26)){ //если оба пас или не сделали выбор
            if (bribe >= 3) {
                if (bribe < 6) bribe = 6;
                condition = Game.choiceMap[bribe - 6][suit];
            }
            if (nonBribe > 5)
                condition = 25;
        }
    }

    public void bidding2(int condition1, int condition2){ //второй раунд торгов
        if (condition < 26) {     //если выиграл торги, выбор ставки
            if (condition == 25) return;
            if (bribe < 6) bribe = 6;
            condition = Game.choiceMap[bribe - 6][suit];
        } else {
            if ((condition1 < 26 && condition2 == 26) || (condition2 < 26 && condition1 == 26)) { // если P1 пас, а P2 выиграл торги, то пол виста
                if (bribe >= 2) condition = 28;
                else condition = 27;
            }
            if ((condition1 == 27 || condition2 == 27) || (condition1 == 28 || condition2 == 28)) {
                if (bribe >= 2) condition = 28;
                else condition = 26;
            }
            if ((condition1 < 25 && condition2 == 29) || (condition1 == 29 && condition2 < 25) ||
                    (condition1 < 25 && condition2 == 26) || (condition1 == 26 && condition2 < 25)){
                if (bribe >= 2) condition = 28;
            }
        }
        bribe = 0;
    }

    public Card hoaxIncreace(int trump, Card card) {  //игра на повышение(игрок набирает взятки)
        Card bestCard = new Card(0);    //лучшая карта котрую может положить
        bestCard.setCard(hand.get(0));
        if (card.value == 0){ // если игрок первым кладет карту на стол
            for (int i = 0; i < hand.size(); i++){
                if (hand.get(i).suit == trump) {
                    bestCard.setCard(hand.get(i));
                    hand.remove(i);
                    return bestCard; //карты отсортированы и возвращена будет наибольшая этой масти
                }
            }
            for (int i = 0; i < hand.size(); i++){
                    bestCard.setCard(hand.get(i));
                    hand.remove(i);
                    return bestCard;
            }
        } else {  //если карта уже лежит на столе
            for (int i = 0; i < hand.size(); i ++){ //есть ли у игрока карта той же масти
                if (i == hand.size() - 1 && hand.get(i).suit == card.suit) {
                    bestCard.setCard(hand.get(i));
                    hand.remove(i);
                    return bestCard;
                }
                if (hand.get(i).suit == card.suit && hand.get(i).value > card.value){
                    bestCard.setCard(hand.get(i));
                    hand.remove(i);
                    return bestCard;
                }
                if (hand.get(i).suit == card.suit && hand.get(i + 1).suit != card.suit){
                    bestCard.setCard(hand.get(i));
                    hand.remove(i);
                    return bestCard;
                }
            }
            for (int i = 0; i < hand.size() - 1; i++){ // если нету карты той же масти, проверим есть ли козырь
                if (hand.get(i).suit == trump) {
                    if (hand.get(i + 1).suit != trump) { // достанем самый низкий козырь
                        bestCard.setCard(hand.get(i));
                        hand.remove(i);
                        return bestCard;
                    } else {
                        bestCard.setCard(hand.get(i + 1));
                        hand.remove(i + 1);
                        return bestCard;
                    }
                }
            }
            for (int i = 0; i < hand.size(); i++) {   //если нет ни той же масти, ни козыря
                int j = 0;
                if ((bestCard.value - 1) % 8 > (hand.get(i).value - 1) % 8) {
                    bestCard.setCard(hand.get(i));
                    j = i;  //сохраним позицию лучшей карты для дальнейшего удаления
                }
                if (i >= hand.size() - 1) {
                    hand.remove(j);
                    return bestCard;
                }
            }
        }
        return bestCard;
    }

    public Card hoaxLowering(Card card){    //игра на понижение(игрок не берет взятки)
        Card bestCard = new Card(0);
        bestCard.setCard(hand.get(0));
        int j = 0;
        if (card.value == 0){   //если игрок делает ход первым
            for (int i = 1; i < hand.size(); i++){
                if ((bestCard.value - 1) % 8 > (hand.get(i).value - 1) % 8){
                    bestCard.setCard(hand.get(i));
                    j = i;
                }
                if (i >= hand.size() - 1){
                    hand.remove(j);
                    return bestCard;
                }
            }
        } else { //и если не ходит первым
            for (int i = hand.size() - 1; i >= 0; i--) { //есть ли у игрока карта той же масти
                if (hand.get(i).suit == card.suit) {
                    if (hand.get(i).value > card.value && i > 0 && hand.get(i - 1).suit == card.suit) {
                        continue;
                    } else {
                        bestCard.setCard(hand.get(i));
                        hand.remove(i);
                        return bestCard;
                    }
                }
            }
            for (int i = 0; i < hand.size(); i++) {   //если нет той же масти
                if ((bestCard.value - 1) % 8 < (hand.get(i).value - 1) % 8){
                    bestCard.setCard(hand.get(i));
                    j = i;
                }
                if (i >= hand.size()){
                    hand.remove(j);
                    return bestCard;
                }
            }
        }
        return bestCard;
    }

    public void prikup(int m){
        if (condition < 26) {
            Card card = new Card(0);
            int j;
            hand.addAll(Game.prikup);
            if (Game.game == 0) {
                for (int n = 0; n < 2; n++) {
                    card.setCard(hand.get(0));
                    j = 0;
                    for (int i = 0; i < hand.size(); i++) {
                        if ((card.value - 1) % 8 > (hand.get(i).value - 1) % 8) {
                            card.setCard(hand.get(i));
                            j = i;
                        }
                    }
                    hand.remove(j);
                }
            }
            if (Game.game == 1 || Game.game == 3) {
                for (int n = 0; n < 2; n++) {
                    card.setCard(hand.get(0));
                    j = 0;
                    for (int i = 0; i < hand.size(); i++) {
                        if ((card.value - 1) % 8 < (hand.get(i).value - 1) % 8) {
                            card.setCard(hand.get(i));
                            j = i;
                        }
                    }
                    hand.remove(j);
                }
            }
            Game.entries.get(m).bidding2 += ". Прикуп забирает "+name;
            Game.writer("Прикуп забирает "+name);
        }
    }

    public String score(int cond1, int cond2, int bribe1, int bribe2){    //подсчет очков игрока
        if (condition < 25){
            if (bribe >= (condition / 5) + 6) {
                gora += (condition / 5) * 2 + 2;
                return "Выбрал ставку "+ getCondition()+". Выиграл";
            } else {
                pool += 2 + (condition / 5) * 2;
                return "Выбрал ставку "+ getCondition()+". Проиграл";
            }
        }
        if (condition == 25){
            if (bribe == 0) {
                pool += 10;
                return "Выбрал мизер. Выиграл";
            } else {
                gora += 10 * bribe;
                return "Выбрал мизер. Проиграл";
            }
        }
        if (condition == 26 && cond1 == 26 && cond2 == 26){
            if (bribe == 0){
                pool += 1;
                return "Распасы. Выиграл(не взял ни одной взятки)";
            }
            int bribeAmnesty = bribe1 > bribe2 ? bribe2 : bribe1;
            bribeAmnesty = bribe > bribeAmnesty ? bribeAmnesty : bribe;
            if (bribe - bribeAmnesty != 0) {
                gora += bribe - bribeAmnesty;
                return "Распасы. Проиграл";
            }
        }
        if (condition == 27){
            if (cond1 < 25) {
                whist1 += (2 + Math.round((10 - cond1 / 5 - 6) / 2)) * 2;
                return "Выбрал полвиста";
            }
            if (cond2 < 25) {
                whist2 += (2 + Math.round((10 - cond2 / 5 - 6) / 2)) * 2;
                return "Выбрал полвиста";
            }
        }
        if (condition == 28){
            //пас и вист. проигрыш вистующего
            if (cond1 < 25 && cond2 == 26 && bribe < (10 - cond1 / 5 - 6) / 2) {
                gora += ((10 - cond1 / 5 - 6) / 2 - bribe) * (2 + 2 * (cond1 / 5));
                return "Вистовал. Проигрыш";
            }
            if (cond2 < 25 && cond1 == 26 && bribe < (10 - cond2 / 5 - 6) / 2) {
                gora += ((10 - cond2 / 5 - 6) / 2 - bribe) * (2 + 2 * (cond2 / 5));
                return "Вистовал. Проигрыш";
            }
            //пас и вист. выигрыш вистующего
            if (cond1 < 25 && cond2 == 26 && bribe >= (10 - cond1 / 5 - 6) / 2) {
                whist1 += ((10 - cond1 / 5 - 6) / 2 - bribe) * (2 + 2 * (cond1 / 5));
                return "Вистовал. Успешно";
            }
            if (cond2 < 25 && cond1 == 26 && bribe >= (10 - cond2 / 5 - 6) / 2) {
                whist2 += ((10 - cond2 / 5 - 6) / 2 - bribe) * (2 + 2 * (cond2 / 5));
                return "Вистовал. Успешно";
            }
            //вист и вист. выигрыш
            if (cond1 < 25 && cond2 == 28 && bribe + bribe2 >= (10 - cond1 / 5 - 6)){
                whist1 += bribe * (2 + 2 * (cond1 / 5));
                return "Вистовал. Успешно";
            }
            if (cond2 < 25 && cond1 == 28 && bribe + bribe1 >= (10 - cond2 / 5 - 6)){
                whist2 += bribe * (2 + 2 * (cond2 / 5));
                return "Вистовал. Успешно";
            }
            //вист и вист. проигрыш
            if ((cond1 < 25 && cond2 == 28 && bribe + bribe2 < (10 - cond1 / 5 - 6)) ||
                    (cond2 < 25 && cond1 == 28 && bribe + bribe1 < (10 - cond2 / 5 - 6))){
                gora += (bribe - (10 - cond2 / 5 - 6) / 2) * (2 + 2 * (cond1 / 5));
                return "Вистовал. Проигрыш";
            }
        }
        return "Пасовал";
    }

    public String getScore(String name1, String name2){
        return ". Гора-"+gora+ ", пуля-"+ pool +", вистов на игрока "+name1+"-"+whist1+", вистов на игрока "+name2+"-"+whist2;
    }

    public String getCondition(){
        String sCondition = "Something went wrong";
        switch (condition / 5){
            case 0 : sCondition = "шесть "; break;
            case 1 : sCondition = "семь "; break;
            case 2 : sCondition = "восемь "; break;
            case 3 : sCondition = "девять "; break;
            case 4 : sCondition = "десять "; break;
        }
        switch (condition % 5){
            case 0 : sCondition += "первых"; break;
            case 1 : sCondition += "вторых"; break;
            case 2 : sCondition += "третьих"; break;
            case 3 : sCondition += "четвертых"; break;
            case 4 : sCondition += "пятых"; break;
        }
        switch (condition){
            case 25 : sCondition = "мизер"; break;
            case 26 : sCondition = "пас"; break;
            case 27 : sCondition = "полвиста"; break;
            case 28 : sCondition = "вист в темную"; break;
            case 29 : sCondition = "я еще не сделал выбор"; break;
        }
        return sCondition;
    }
}
