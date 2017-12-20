package com.example.thenotoriousrog.tornadomusicplayer.Backend;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by thenotoriousrog on 7/17/17.
 * This class is a queue that simply holds two songs at any given time that way when shuffling is going on the user can see the next song that is playing.
 * This queue will automatically fill itself whenever a user pops a song out of the queue. The user can peek to the next song as well.
 */

public class ShuffleQueue {

    private final ArrayList<SongInfo> songs; // once this list is set it will never be set again until a new creation of the Queue that we wish to use.
    private Queue<SongInfo> queue = new LinkedList<>(); // The queue that will be used for shuffling of the songs.

    // When the class is made, the Queue is automatically filled.
    public ShuffleQueue(ArrayList<SongInfo> currentSongList)
    {
        songs = new ArrayList<>(currentSongList); // create a brand new copy of this list so that we can clear it and have the Garbage collector can clean it on its pass.
        // fill the queue with two random songs out of the song list.

        queue.add(songs.get(randomSongPos())); // get random song 1.
        queue.add(songs.get(randomSongPos())); // get random song 2.

    }

    // generates a random number within the bounds of the songList
    public int randomSongPos()
    {
        // todo: test this method like crazy to make sure that there are no bugs.
        int min = 0; // cannot choose a song position less than 0
        int max = songs.size() - 1; // cannot choose a song that is greater than this.

        Random rand = new Random();
        int randomPos = rand.nextInt( (max-min) +1 ) + min;
        return randomPos; // return the random song position which will let us pick a song in random.
    }

    // this method is used to grab the next song in the queue, also triggering the queue to fill the queue with another song.
    public SongInfo popNextSong()
    {
        SongInfo nextSong = queue.remove(); // grabs the first song in the list.

        // // FIXME: 7/17/17 the below line causes an index out of bounds exception for whatever reason.
        queue.add(songs.get(randomSongPos())); // grab a random song and fill the queue one more time.

        return nextSong; // return the next song in the list.
    }

    // This method is used to grab the song name of the next song to be used by the main title displays and notifications.
    public SongInfo peekNxtSong()
    {
        return queue.peek(); // grab the next song and extract the song name and return that to the calling class.
    }

    // clears every list to hold as little data as possible.
    public void clear()
    {
        queue.clear(); // removed all songs from the queue.
        songs.clear(); // removes all songs from the list of songs.
    }


}
