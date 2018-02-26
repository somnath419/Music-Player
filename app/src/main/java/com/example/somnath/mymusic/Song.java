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
    private int Dduration;
    private String img_Id;
    private int date_added;
    private long album_ID;


    public Song(long id, String artist,String songTitle,String albums,String genres) {
        Id =id;
        title=songTitle;
        Artist=artist;
        a_lbums=albums;
        genre_s=genres;
    }

    public  Song (long id ,String titl)
    {
        Id=id;
        title=titl;
    }
    public  Song(String genre)
    {
        genre_s=genre;
    }

    public  Song (String artist,String albums) {
        a_lbums=albums;
        Artist = artist;
    }
    public Song(String namealbum,String artists,String albumimage) {
        a_lbums=namealbum;
        albumart=albumimage;
        Artist=artists;
    }


    public Song(String namealbum,String artists,String albumimage,long Albums_id) {
        a_lbums=namealbum;
        albumart=albumimage;
        Artist=artists;
        album_ID=Albums_id;


    }




    public Song(long id, String songTitle,String artist,String art_album) {
        Id =id;
        title=songTitle;
        Artist=artist;
        img_Id=art_album;
    }

    public Song(long id, String songTitle,String artist,String art_album,int date_Added) {
        Id =id;
        title=songTitle;
        Artist=artist;
        img_Id=art_album;
        date_added=date_Added;
    }

    public Song(long id, String songTitle,String artist,int date_Added) {
        Id =id;
        title=songTitle;
        Artist=artist;
        date_added=date_Added;
    }




    public String getTitle(){return title;}
    public String getArtist(){return Artist;}
    public String getAlbums(){return  a_lbums;}
    public long getId(){return Id;}
    public String getGenres(){return genre_s;}
    public  String getAlbumart(){return  albumart;}
    public  int getDduration(){return Dduration;}
    public String getImg_Id(){return img_Id;}
    public int getDate_added(){return date_added;}
    public long getAlbum_ID(){ return album_ID;}



}
