package com.example.thenotoriousrog.tornadomusicplayer;

/**
 * Created by thenotoriousrog on 6/23/17.
 * This template class will a pair of arbitrary objects to be easily bundled together.
 * This class is used heavily with the PlayList support of the app itself.
 */
public class Pair<A, B> {

    private A first;
    private B second;

    public Pair() {}; // default constructor.

    public Pair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    // used more so for the playlist
    public A getName()
    {
        return first;
    }

    // used for when pair is more general.
    public A getFirst() { return  first; }

    public void setName(A first)
    {
        this.first = first;
    }

    // used more so for the playlist
    public B getSongs()
    {
        return second;
    }

    // used for when pair is more general.
    public B getSecond() {return second; }

    public void setSongs(B second)
    {
        this.second = second;
    }

    public String toString()
    {
        return "( " + first + ", " + second + ")"; // prints (first, second)
    }
}