package ru.cource.preferance.records;

import ru.cource.preferance.Game;

import java.util.Scanner;

public class Api {
    public static Scanner in = new Scanner(System.in);
    public static String text = "";

    public static void api1(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).dealing;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api2(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).bidding1+Game.entries.get(number).bidding2;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api3(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).bidding1;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api4(int number){
        if (number < Game.entries.size() && number >= 0) {
            if (!Game.entries.get(number).hoax.equals("")) {
                text += Game.entries.get(number).hoax;
            } else {
                System.out.println("Два игрока спасовали и согласились на условия третьего");
            }
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api5(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).bribes+Game.entries.get(number).score0+
                   Game.entries.get(number).score1+Game.entries.get(number).score2;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api6(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).dealing+Game.entries.get(number).bidding1+
                    Game.entries.get(number).bidding2+Game.entries.get(number).hoax+
                    Game.entries.get(number).bribes+Game.entries.get(number).score0+Game.entries.get(number).bribes+Game.entries.get(number).score1+
                            Game.entries.get(number).bribes+Game.entries.get(number).score2+
                    Game.entries.get(number).finalScore0+Game.entries.get(number).finalScore1+Game.entries.get(number).finalScore2;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api7(int number, int p){
        if (number < Game.entries.size() && number >= 0) {
            if (p <= 2 && p >= 0) {
                switch (p) {
                    case 0 : text += Game.entries.get(number).score0; break;
                    case 1 : text += Game.entries.get(number).score1; break;
                    case 2 : text += Game.entries.get(number).score2; break;
                }
            }
            else System.out.println("Извините, неверный идентификатор игрока");
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api8(int number, int p){
        if (number < Game.entries.size() && number >= 0) {
            if (p <= 2 && p >= 0) {
                switch (p) {
                    case 0 : text += Game.entries.get(number).finalScore0; break;
                    case 1 : text += Game.entries.get(number).finalScore1; break;
                    case 2 : text += Game.entries.get(number).finalScore2; break;
                }
            }
            else System.out.println("Извините, неверный идентификатор игрока");
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api9(int number, int p){
        if (number < Game.entries.size() && number >= 0) {
            if (p <= 2 && p >= 0) {
                for (int i = 0; i <= number; i++) {
                    switch (p) {
                        case 0:
                            text += Game.entries.get(i).games0;
                            break;
                        case 1:
                            text += Game.entries.get(i).games1;
                            break;
                        case 2:
                            text += Game.entries.get(i).games2;
                            break;
                    }
                }
            }
            else System.out.println("Извините, неверный идентификатор игрока");
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }
    public static void api10(int number){
        if (number < Game.entries.size() && number >= 0) {
            text += Game.entries.get(number).finalScore0+Game.entries.get(number).finalScore1+
                    Game.entries.get(number).finalScore2;
        }
        else System.out.println("Извините, не припомню такой раздачи");
    }

    public static void write(String choice){
        if (choice.equals("y") || choice.equals("Y"))
            File.writeFile(text);
        text = "";
        System.out.println("\nЗаписано!\n");
    }
}
