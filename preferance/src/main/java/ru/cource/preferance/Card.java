package ru.cource.preferance;

public class Card {

    int value;  //значение карты
    int suit;
    private String name;

    public String toString (){
        return name;
    }

    Card(int value){
        this.value = value;
        this.suit = (value - 1) / 8;

        name = "Something went wrong";
        switch (value % 8){
            case 1 : name = "семерка "; break;
            case 2 : name = "восьмерка "; break;
            case 3 : name = "девятка "; break;
            case 4 : name = "десятка "; break;
            case 5 : name = "валет "; break;
            case 6 : name = "дама "; break;
            case 7 : name = "король "; break;
            case 0 : name = "туз "; break;
        }
        switch ((value - 1) / 8){
            case 0 : name += "пик"; break;
            case 1 : name += "треф"; break;
            case 2 : name += "бубей"; break;
            case 3 : name += "червей"; break;
        }
    }

    public void setCard(Card card){
        this.value = card.value;
        this.suit = card.suit;
        this.name = card.name;
    }
}
