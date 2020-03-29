package com.johngoodstadt.memorize.models;

public class Schemes {

    public static String DEFAULT_SCHEME_UID = "1";
    public static String DEFAULT_SCHEME_TITLES = "10m,1H,1D,1W,1M,4M";

     /*
     Oct 2018
     1    Default Phrase    1    20m,1H,4H,1D,3D,1W
     2    Default Piece     2    20m,1H,4H,1D,3D,1W,2W,1M,2M,4M
     3    Phrase Short      1    20m,1H,1D,3D,1W
     4    Piece Short       1    20m,1H,1D,3D,1W,2W,1M,2M,4M
 */


    public static String DEFAULT_SCHEME_DEBUG = "2m,4m,6m,1H,1D,1W,4W,2M,4M";

    public static String DEFAULT_SCHEME_MUSIC_PHRASE_TITLES = "20m,1H,1D,3D,1W,2W,4W,2M,4M"; //though will pull from Local - this is default in case
    public static String DEFAULT_SCHEME_MUSIC_PHRASE_TITLES_EXTRA = "20m,1H,4H,1D,3D,1W,2W,4W,6W,2M,4M";
    public static String DEFAULT_SCHEME_MUSIC_PHRASE_TITLES_EXTRA4 = "20m,1H,1D,4D,1W,2W,1M,3M";
    public static String DEFAULT_SCHEME_MUSIC_PHRASE_TITLES_LESS = "20m,1H,1D,1W,1M,2M,4M";
    public static String DEFAULT_SCHEME_MUSIC_PHRASE_STOP_TITLE = "1W";
}
