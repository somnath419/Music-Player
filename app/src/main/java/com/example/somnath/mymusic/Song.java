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
    private String albumart;


    public Song(long id, String artist,String songTitle,String albums,String genres) {
        Id =id;
        title=songTitle;
        Artist=artist;
        a_lbums=albums;
        genre_s=genres;
    }

    public Song(String namealbum,String artists,String albumimage) {
        a_lbums=namealbum;
        albumart=albumimage;
        Artist=artists;

    }
    public Song(long id, String artist,String songTitle,String albums,String genres,String albumimage) {
        Id =id;
        title=songTitle;
        Artist=artist;
        a_lbums=albums;
        genre_s=genres;
        albumart=albumimage;
    }



    public String getTitle(){return title;}
    public String getArtist(){return Artist;}
    public String getAlbums(){return  a_lbums;}
    public long getId(){return Id;}
    public String getGenres(){return genre_s;}
    public  String getAlbumart(){return  albumart;}


}
