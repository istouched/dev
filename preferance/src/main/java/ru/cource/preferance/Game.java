package ru.cource.preferance;

import ru.cource.preferance.records.Api;
import ru.cource.preferance.records.Entry;
import ru.cource.preferance.records.File;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static ArrayList<Entry> entries;

    private static int order; //очередность ходов
    private int trump; //козырь игры
    private ArrayList<Card> deck;
    public static ArrayList<Card> prikup;
    private Random random;
    public static int[][] choiceMap; //таблица выбора ставки
    public static int game;   //какая игра будет(брать/не брать взяток)      0-взятки
                                                            // 1-мизер
    private static Player player[];                          // 2-пас и полвиста
                                                            // 3-распас
    Game(){                                                 // 4-двое пас
        entries = new ArrayList<Entry>();
        order = 0;
        trump = 0;
        deck = new ArrayList<Card>();
        prikup = new ArrayList<Card>();
        random = new Random();
        player = new Player[3];
        player[0] = new Player("Стефан");
        player[1] = new Player("Ипполит");
        player[2] = new Player("Борис");
        game = 0;
        for (int i = 1; i <= 32; i ++){ //инициализируем карты
            Card card = new Card(i);
            deck.add(card);
        }
        choiceMap = new int[9][]; //инициализируем таблицу ставками
        for (int i = 0; i < 9; i++){                    //0  1  2  3  4
            if (i < 5) {                                //5  6  7  8  9
                choiceMap[i] = new int[5];              //10 11 12 13 14
                for (int j = 0; j < 5; j++) {           //15 16 17 18 19
                    choiceMap[i][j] = j + i * 5;        //20 21 22 23 24
                }                                       //25 - мизер
            } else {                                    //26 - пас
                choiceMap[i] = new int[1];              //27 - полвиста
                choiceMap[i][0] = i + 20;               //28 - вист
            }                                           //29 - выбор игрока еще не сделан(в таблице отстутствует)
        }
    }

    private void shuffle(){ //перетасовка карт
        Card card = new Card(0);
        int rand;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 32; i++) {
                rand = random.nextInt(32);
                card.setCard(deck.get(rand));
                deck.get(rand).setCard(deck.get(i));
                deck.get(i).setCard(card);
            }
        }
    }

    private void dealing(){ //раздача карт игрокам
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.addAll(deck.subList(0, 10));
        player[0].setCards(cards);
        cards.clear();
        cards.addAll(deck.subList(10, 20));
        player[1].setCards(cards);
        prikup.clear();
        prikup.addAll(deck.subList(20, 22));
        cards.clear();
        cards.addAll(deck.subList(22, 32));
        player[2].setCards(cards);
    }

    private void bidding(int n){ //торги между игроками. целиком и полностью
        // 1 round
        while ((player[order].condition != 26 && player[(order + 1) % 3].condition != 26) ||
                (player[(order + 1) % 3].condition != 26 &&  player[(order + 2) % 3].condition != 26) ||
                (player[order].condition != 26 &&  player[(order + 2) % 3].condition != 26)) {
            player[order].bidding1(player[(order + 1) % 3].condition, player[(order + 2) % 3].condition);
            player[(order + 1) % 3].bidding1(player[order].condition, player[(order + 2) % 3].condition);
            player[(order + 2) % 3].bidding1(player[order].condition, player[(order + 1) % 3].condition);

            entries.get(n).bidding1 += "\nПервый раунд торгов\n"+player[order].name+" делает заявку "+player[order].getCondition();
            entries.get(n).bidding1 += ", "+player[(order + 1) % 3].name+" делает заявку "+player[(order + 1) % 3].getCondition();
            entries.get(n).bidding1 += ", "+player[(order + 2) % 3].name+" делает заявку "+player[(order + 2) % 3].getCondition();

            writer("\nПервый раунд торгов\n"+player[order].name+" делает заявку "+player[order].getCondition());
            writer(player[(order + 1) % 3].name+" делает заявку "+player[(order + 1) % 3].getCondition());
            writer(player[(order + 2) % 3].name+" делает заявку "+player[(order + 2) % 3].getCondition());
        }
        // 2 round
        for (int i = 0; i < 2; i++) {   //2 т.к. кто то может выбрать полвиста
            player[order].bidding2(player[(order + 1) % 3].condition, player[(order + 2) % 3].condition);
            player[(order + 1) % 3].bidding2(player[order].condition, player[(order + 2) % 3].condition);
            player[(order + 2) % 3].bidding2(player[order].condition, player[(order + 1) % 3].condition);
        }

        entries.get(n).bidding2 += "\nВторой раунд торгов\n"+player[order].name+" делает заявку "+player[order].getCondition();
        entries.get(n).bidding2 += ", "+player[(order + 1) % 3].name+" делает заявку "+player[(order + 1) % 3].getCondition();
        entries.get(n).bidding2 += ", "+player[(order + 2) % 3].name+" делает заявку "+player[(order + 2) % 3].getCondition();

        writer("\nВторой раунд торгов\n"+player[order].name+" делает заявку "+player[order].getCondition());
        writer(player[(order + 1) % 3].name+" делает заявку "+player[(order + 1) % 3].getCondition());
        writer(player[(order + 2) % 3].name+" делает заявку "+player[(order + 2) % 3].getCondition());
    }

    private int preHoax(){
        if (player[order].condition < 25)
            trump = player[order].condition / 5;
        if (player[(order + 1) % 3].condition < 25)
            trump = player[(order + 1) % 3].condition / 5;
        if (player[(order + 2) % 3].condition < 25)
            trump = player[(order + 2) % 3].condition / 5;

        if (player[0].condition == 26 || player[1].condition == 26 || player[2].condition == 26){ //если есть 1 пас
            if (player[0].condition == 27 || player[1].condition == 27 || player[2].condition == 27)    //если есть пол виста
                return 2;
            if (player[0].condition == 26 && player[1].condition == 26 && player[2].condition == 26) //если распас
                return 3;
            if ((player[0].condition == 26 && player[1].condition == 26) || (player[1].condition == 26 && player[2].condition == 26)
                    || (player[0].condition == 26 && player[2].condition == 26))
                return 4;
        }
        if (player[0].condition == 25 || player[1].condition == 25 || player[2].condition == 25) //если мизер
            return 1;
        return 0; //если ставка и 2 виста
    }

    private void comparsionIncreace(Card card1, Card card2, Card card3){
        if (((card1.value - 1) % 8 > (card2.value - 1) % 8 && (card1.value - 1) % 8 > (card3.value - 1) % 8) ||
                (card1.suit == trump || (card2.suit != trump && card3.suit != trump)) ||
                (card2.suit != card1.suit && card3.suit != card1.suit)){
                player[order].bribe++;
        }
        if ((card2.value - 1) % 8 > (card1.value - 1) % 8 && (card2.value - 1) % 8 > (card3.value - 1) % 8){
            if (card2.suit == trump || (card2.suit == card1.suit && card3.suit != trump)) {
                order = (order + 1) % 3;  //передача хода тому, кто взял взятку
                player[order].bribe++;
            }
        }
        if ((card3.value - 1) % 8 > (card1.value - 1) % 8 && (card3.value - 1) % 8 > (card2.value - 1) % 8){
            if (card3.suit == trump || (card3.suit == card1.suit && card3.suit != trump)) {
                order = (order + 2) % 3;
                player[order].bribe++;
            }
        }
    }

    private void comparsionLowering(Card card1, Card card2, Card card3){
        if (((card1.value - 1) % 8 > (card2.value - 1) % 8 || card2.suit != card1.suit) && ((card1.value - 1) % 8 > (card3.value - 1) % 8 || card3.suit != card1.suit)){
                player[order].bribe++;
        }
        if ((card2.value - 1) % 8 > (card1.value - 1) % 8 && ((card2.value - 1) % 8 > (card3.value - 1) % 8 || card3.suit != card1.suit)){
            if (card2.suit == card1.suit) {
                order = (order + 1) % 3;
                player[order].bribe++;
            }
        }
        if ((card3.value - 1) % 8 > (card1.value - 1) % 8 && ((card3.value - 1) % 8 > (card2.value - 1) % 8 || card2.suit != card1.suit)){
            if (card3 .suit == card1.suit) {
                order = (order + 2) % 3;
                player[order].bribe++;
            }
        }
    }

    public void hoax(){
        Card card1 = new Card(0);
        Card card2;
        Card card3 = new Card(0);

        System.out.println("Добро пожаловать в преферанс!\nКонвенция сочи\n");

        System.out.println("1 - Скрипт 1\n2 - Скрипт 2\n0 - Задать самому");
        int choice = Api.in.nextInt();
        int inNumber = 0;
        switch (choice){
            case 0 : System.out.println("Сколько партий играть?");
                inNumber = Api.in.nextInt();
                break;
            case 1 :
            case 2 : inNumber = 12;
            break;
        }

        for (int i = 0; i < inNumber; i ++) {
            order = i % 3;
            shuffle();
            dealing();

            player[order].sortCard();
            player[(order + 1) % 3].sortCard();
            player[(order + 2) % 3].sortCard();

            entries.add(new Entry());
            entries.get(i).dealing = "\n\nРаздача № "+(i+1)+"\n"+player[order].name+" получил карты "+player[order].hand;
            entries.get(i).dealing += "\n"+player[(order + 1) % 3].name+" получил карты "+player[(order + 1) % 3].hand;
            entries.get(i).dealing += "\n"+player[(order + 2) % 3].name+" получил карты "+player[(order + 2) % 3].hand;
            entries.get(i).dealing += "\nПрикуп "+prikup+"\nПервым ходит "+player[order].name+", вторым "+player[(order + 1) % 3].name+", третьим "+player[(order + 2) % 3].name;

            writer(entries.get(i).dealing);

            player[order].bribe();
            player[(order + 1) % 3].bribe();
            player[(order + 2) % 3].bribe();

            player[order].nonBribe();
            player[(order + 1) % 3].nonBribe();
            player[(order + 2) % 3].nonBribe();

            entries.get(i).bidding1 = "\nТорговля.";
            writer("Торговля.");

            bidding(i);
            game = preHoax();
            if (player[order].condition == 26 && player[(order + 1) % 3].condition == 26 &&  player[(order + 2) % 3].condition == 26) {
                entries.get(i).bidding2 += "\nРаспасовка, прикуп остается на столе.";
                writer("Распасовка, прикуп остается на столе.");
            } else {
                player[order].prikup(i);
                player[(order + 1) % 3].prikup(i);
                player[(order + 2) % 3].prikup(i);
            }

            int j = 0;
            if (game == 0){
                while (j < 10) {
                    card1 = player[order].hoaxIncreace(trump, card1);
                    entries.get(i).hoax += "\n\nХодит "+player[order].name+". Кладет на стол карту "+card1;
                    writer("Ходит "+player[order].name+". Кладет на стол карту "+card1);
                    card2 = player[(order + 1) % 3].hoaxIncreace(trump, card1);
                    entries.get(i).hoax += "\nХодит "+player[(order + 1) % 3].name+". Кладет на стол карту "+card2;
                    writer("Ходит "+player[(order + 1) % 3].name+". Кладет на стол карту "+card2);
                    //проверим какая карта старше
                    if ((card1.suit != trump && card2.suit == trump) || ((card1.value - 1) % 8 < (card2.value - 1) % 8 && card1.suit == card2.suit))
                        card3.setCard(card2);
                    else card3.setCard(card1);
                    card3 = player[(order + 2) % 3].hoaxIncreace(trump, card3);
                    entries.get(i).hoax += "\nХодит "+player[(order + 2) % 3].name+". Кладет на стол карту "+card3;
                    writer("Ходит "+player[(order + 2) % 3].name+". Кладет на стол карту "+card3);

                    comparsionIncreace(card1, card2, card3);

                    entries.get(i).hoax += "\nВзятку забирает "+player[order].name;
                    writer("Взятку забирает "+player[order].name);

                    j++;
                    card1.value = 0;
                }
            }
            if (game == 1 || game == 3) {
                while (j < 10) {
                    if(prikup.size() != 0 && game == 3) {
                        card1.setCard(prikup.get(0));
                        entries.get(i).hoax += "\n\nКарта из прикупа "+card1;
                        writer("Карта из прикупа "+card1);
                        prikup.remove(0);
                    }
                    card1 = player[order].hoaxLowering(card1);
                    entries.get(i).hoax += "\n\nХодит "+player[order].name+". Кладет на стол карту "+card1;
                    writer("Ходит "+player[order].name+". Кладет на стол карту "+card1);
                    card2 = player[(order + 1) % 3].hoaxLowering(card1);
                    entries.get(i).hoax += "\nХодит "+player[(order + 1) % 3].name+". Кладет на стол карту "+card2;
                    writer("Ходит "+player[(order + 1) % 3].name+". Кладет на стол карту "+card2);
                    //проверим какая карта младше
                    if ((card1.value - 1) % 8 > (card2.value - 1) % 8 && card1.suit == card2.suit)
                        card3.setCard(card2);
                    else card3.setCard(card1);
                    card3 = player[(order + 2) % 3].hoaxLowering(card3);
                    entries.get(i).hoax += "\nХодит "+player[(order + 2) % 3].name+". Кладет на стол карту "+card3;
                    writer("Ходит "+player[(order + 2) % 3].name+". Кладет на стол карту "+card3);

                    comparsionLowering(card1, card2, card3);

                    entries.get(i).hoax += "\nВзятку забирает "+player[order].name;
                    writer("Взятку забирает "+player[order].name);

                    j++;
                    card1.value = 0;
                }
            }

            entries.get(i).games0 += "\nРаунд "+(i+1)+". "+player[0].score(player[1].condition, player[2].condition, player[1].bribe, player[2].bribe);
            entries.get(i).games1 += "\nРаунд "+(i+1)+". "+player[1].score(player[0].condition, player[2].condition, player[0].bribe, player[2].bribe);
            entries.get(i).games2 += "\nРаунд "+(i+1)+". "+player[2].score(player[0].condition, player[1].condition, player[0].bribe, player[1].bribe);

            finalScore(i);

            entries.get(i).bribes += "\n"+player[0].name+" взял взяток "+player[0].bribe;
            entries.get(i).bribes += "\n"+player[1].name+" взял взяток "+player[1].bribe;
            entries.get(i).bribes += "\n"+player[2].name+" взял взяток "+player[2].bribe;

            entries.get(i).score0 += "\n"+player[0].name+player[0].getScore(player[1].name, player[2].name);
            entries.get(i).score1 += "\n"+player[1].name+player[1].getScore(player[2].name, player[0].name);
            entries.get(i).score2 += "\n"+player[2].name+player[2].getScore(player[1].name, player[0].name);

            writer(player[0].name+player[0].getScore(player[1].name, player[2].name));
            writer(player[1].name+player[1].getScore(player[2].name, player[0].name));
            writer(player[2].name+player[2].getScore(player[1].name, player[0].name));

            entries.get(i).finalScore0 += "\n"+player[0].name+" итоговый счет "+player[0].finalScore;
            entries.get(i).finalScore1 += "\n"+player[1].name+" итоговый счет "+player[1].finalScore;
            entries.get(i).finalScore2 += "\n"+player[2].name+" итоговый счет "+player[2].finalScore;

            writer(player[0].name+" итоговый счет "+player[0].finalScore);
            writer(player[1].name+" итоговый счет "+player[1].finalScore);
            writer(player[2].name+" итоговый счет "+player[2].finalScore);
        }

        if (choice == 1){
            Api.api1(2); Api.api1(4);
            Api.api2(3);
            Api.api3(2);
            Api.api4(6);
            Api.api5(5);
            Api.api7(7, 0);
            Api.api8(9, 1);
            Api.api9(9, 2);
            Api.api10(11);

            System.out.println(Api.text);
            System.out.println("\nЗаписать в файл?(src/main/resources/file.txt)\ny,Y - да\nn,N - нет");
            Api.write(Api.in.next());
        }
        if (choice == 2){
            for (int i = 0; i < 12; i++){
                Api.api6(i);
            }

            System.out.println(Api.text);
            System.out.println("\nЗаписать в файл?(src/main/resources/file.txt)\ny,Y - да\nn,N - нет");
            Api.write(Api.in.next());
        }
            while (inNumber != 0) {
                System.out.println("Что вы хотите сделать?\n" +
                        "1 - api1\n" +
                        "2 - api2\n" +
                        "3 - api3\n" +
                        "4 - api4\n" +
                        "5 - api5\n" +
                        "6 - api6\n" +
                        "7 - api7\n" +
                        "8 - api8\n" +
                        "9 - api9\n" +
                        "10 - api10\n" +
                        "0 - выход");
                inNumber = Api.in.nextInt();
                int p;
                switch (inNumber) {
                    case 1:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api1(inNumber - 1);
                        break;
                    case 2:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api2(inNumber - 1);
                        break;
                    case 3:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api3(inNumber - 1);
                        break;
                    case 4:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api4(inNumber - 1);
                        break;
                    case 5:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api5(inNumber - 1);
                        break;
                    case 6:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api6(inNumber - 1);
                        break;
                    case 7:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        System.out.println("Номера игроков:\n0-Стефан\n1-Ипполит\n2-Борис");
                        p = Api.in.nextInt();
                        Api.api7(inNumber - 1, p);
                        break;
                    case 8:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        System.out.println("Номера игроков: \n0-Стефан\n1-Ипполит\n2-Борис");
                        p = Api.in.nextInt();
                        Api.api8(inNumber - 1, p);
                        break;
                    case 9:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        System.out.println("Номера игроков: \n0-Стефан\n1-Ипполит\n2-Борис");
                        p = Api.in.nextInt();
                        Api.api9(inNumber - 1, p);
                        break;
                    case 10:
                        System.out.println("Какой номер раздачи?");
                        inNumber = Api.in.nextInt();
                        Api.api10(inNumber - 1);
                        break;
                }
                if (inNumber != 0) {
                    System.out.println(Api.text);
                    System.out.println("\nЗаписать в файл?(src/main/resources/file.txt)\ny,Y - да\nn,N - нет");
                    Api.write(Api.in.next());
                }
            }
    }

    private void finalScore(int m){
        player[0].finalScore = player[0].gora + (m - player[0].pool);
        player[1].finalScore = player[1].gora + (m - player[1].pool);
        player[2].finalScore = player[2].gora + (m - player[2].pool);

        float mediumGora = (player[0].finalScore + player[1].finalScore + player[2].finalScore) * 10 / 3;

        player[0].finalScore = mediumGora - player[0].finalScore * 10;
        player[1].finalScore = mediumGora - player[1].finalScore * 10;
        player[2].finalScore = mediumGora - player[2].finalScore * 10;

        player[0].whist = player[0].whist1 - player[1].whist2 + player[0].whist2 - player[2].whist2;
        player[1].whist = player[1].whist1 - player[2].whist1 + player[1].whist2 - player[0].whist1;
        player[2].whist = player[2].whist1 - player[1].whist1 + player[2].whist2 - player[0].whist2;

        player[0].finalScore = player[0].finalScore + player[0].whist;
        player[1].finalScore = player[1].finalScore + player[1].whist;
        player[2].finalScore = player[2].finalScore + player[2].whist;

        player[0].condition = 29;
        player[1].condition = 29;
        player[2].condition = 29;
    }

    public static void writer(String text){
        System.out.println(text);
        File.logFile(text);
    }
}
