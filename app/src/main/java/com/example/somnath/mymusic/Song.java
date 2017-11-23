package com.example.somnath.mymusic;

/**
 * Created by SOMNATH on 29-04-2017.
 */

public class Song {

    private String title;
    private long Id;
    private String Artist;
    private String a_lbums;
    private String genre_s;


    public Song(long id, String artist,String songTitle,String albums,String genres) {
        Id =id;
        title=songTitle;
        Artist=artist;
        a_lbums=albums;
        genre_s=genres;
    }

    public String getTitle(){return title;}
    public String getArtist(){return Artist;}
    public String getAlbums(){return  a_lbums;}
    public long getId(){return Id;}
    public String getGenres(){return genre_s;}


}
