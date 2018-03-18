package ru.cource.preferance.records;

public class Entry {
    public String dealing = "";
    public String bidding1 = "";
    public String bidding2 = "";
    public String hoax = "";
    public String bribes = "";
    public String score0 = "";
    public String score1 = "";
    public String score2 = "";
    public String finalScore0 = "";
    public String finalScore1 = "";
    public String finalScore2 = "";
    public String games0 = "";
    public String games1 = "";
    public String games2 = "";

    @Override
    public String toString() {
        return dealing+bidding1+bidding2+hoax+bribes+score0+score1+score2+finalScore0+finalScore1+finalScore2;
    }
}
