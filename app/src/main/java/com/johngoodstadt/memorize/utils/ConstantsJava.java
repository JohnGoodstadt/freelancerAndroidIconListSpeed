package com.johngoodstadt.memorize.utils;

//NOTE: Duplicate of main project - Move here when needed
public class ConstantsJava {

    public static String MUSIC_SUFFIX = "music_cue";
    public static String LARGE_SUFFIX = "large";
    public static String SMALL_SUFFIX = "small";
    public static String PROMPT_SUFFIX = "prompt";
    public static String PROMPT_SMALL_SUFFIX = "prompt_small";
    public static String GROUP_PREFIX = "depot";
    public static String THUMBNAIL_SUFFIX = SMALL_SUFFIX;
    public static String MUSIC_SEPERATOR = "-";
    public static String MUSIC_SEPERATOR_WITH_SPACES = " - ";
    public static String DEFAULT_GROUP_UID = "ungrouped";
    public static String DBNAME = "memorize.db";
    public static String DEPOT_BEFORE_CREATE_PLACEHOLDER = "PLACEHOLDER";
    public static Integer THUMBNAIL_SIZE = 100;
    // public static Signed RANDOM_UPPER_LIMIT_INT:UInt32 = 999999   ;

    public static Integer RANDOM_UPPER_LIMIT_INT = 999999;

    public enum timeComponentEnum {
        timeComponentMinute("m"), timeComponentHour("H"), timeComponentDay("D"),
        timeComponentWeek("W"), timeComponentMonth("M"), timeComponentYear("Y");
        private
        String value;

        timeComponentEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static final String timeComponentMinute = "m";
    public static final String timeComponentHour = "H";
    public static final String timeComponentDay = "D";
    public static final String timeComponentWeek = "W";
    public static final String timeComponentMonth = "M";
    public static final String timeComponentYear = "Y";

    public
    enum WhichPartOfPiece {
        composer, piece, movement
    }

    public
    enum WhichPartOfPhrase {
        piece, movement, letter //not letter (Letter now added for getLetter)
    }


    public
    enum CustmCalendra{
        minute,hour,day,weekdayOrdinal,month,year
    }

    public enum RECALLITEM_TIMINGS_STATE_ENUM{
        RECALLITEM_TIMINGS_STATE_OVERDUE(0),
        RECALLITEM_TIMINGS_STATE_EARLY(1),
        RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY(2),
        RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE(3);

        private int action;
        // getter method
        public int getAction()
        {
            return this.action;
        }
        RECALLITEM_TIMINGS_STATE_ENUM(int action) {
            this.action = action;
        }
    }

}
