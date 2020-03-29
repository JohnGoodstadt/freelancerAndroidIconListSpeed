package com.johngoodstadt.memorize.models;


import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.johngoodstadt.memorize.Libraries.ExtensionSQL;
import com.johngoodstadt.memorize.Libraries.LibraryFilesystem;
import com.johngoodstadt.memorize.Libraries.LibraryJava;
import com.johngoodstadt.memorize.utils.Constants;
import com.johngoodstadt.memorize.utils.ConstantsJava;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "Bus")
public class RecallItem implements ExtensionSQL.RecallItemLocalSQL, Cloneable  {


    //region Init
    public RecallItem() {

    }

    public RecallItem(String title, String groupUID) {

        this.title = title;
        this.busDepotUID = groupUID;

        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
            schemeTitles =  Schemes.DEFAULT_SCHEME_MUSIC_PHRASE_TITLES;
        }else{
            schemeTitles =  Schemes.DEFAULT_SCHEME_TITLES;
        }

        //just in time stops list
        if (schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
        }


    }
    //endregion




    //region Properties

    @NonNull public String title = "";
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT) @NonNull  public String busDepotUID = "";

    @NonNull public int busID = 20;
    @NonNull public ITEM_JOURNEY_STATE_ENUM journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup;
    @NonNull public ANSWERED_ENUM answeredState = ANSWERED_ENUM.NotAnswered;
    @NonNull public String phrase = "";
    @NonNull public String cue = "";
    @NonNull public RecallItemCategory_Enum busCategory = RecallItemCategory_Enum.RecallItemCategoryWords;
    @NonNull public String words = "";
    @NonNull public Date goTime = new Date();
    @NonNull public Date learnTime = new Date();
    @NonNull public Date prevEventTime = new Date();
    @NonNull public Date nextEventTime = new Date();
    @NonNull public int currentBusStopNumber = 0;
    @NonNull public String extraInfo = "";

    @NonNull public String recordName = "";
    @NonNull public String recordChangeTag = "";

    @PrimaryKey @NonNull public String UID = LibraryJava.makeUID();

    @NonNull public String metronome = "";
    @NonNull public String busStopTitles = "";
    @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
    @NonNull public int busRouteUniqueID = 1;

    @NonNull public int intUID = ThreadLocalRandom.current().nextInt(1, ConstantsJava.RANDOM_UPPER_LIMIT_INT);

    //Integer.parseInt(LibraryJava.makeUID(com.johngoodstadt.memorize.utils.ConstantsJava.RANDOM_UPPER_LIMIT_INT));

    @NonNull public String linkedPhrases = "";
    @NonNull public String schemeUID = "1";
    @NonNull public String schemeTitles = Schemes.DEFAULT_SCHEME_TITLES;


    @Ignore public RecallGroup recallGroup;
    @Ignore public String recallGroupUID = "";


    //@Ignore public int currentStopNumber = 1;
    //MARK: - properties
    @Ignore public boolean isloading = false;//so we dont update whilst reading in

    @Ignore public int DBID = 0;

    //Random rand = new Random();
    @Ignore public int busDepotID = 0;

    @Ignore public int stopCount;


    @Ignore public String stops[] = new String [0];//filled when necessary with schemeTitles as array




    @Ignore int mCurrentITEM_JOURNEY_STATE;

    @Ignore public boolean isCombined = false; //do I need this? - Step 3?
    @Ignore public String phraseStopTiming = "";
    @Ignore public String recallItemCategory = getDescription(RecallItemCategory_Enum.RecallItemCategoryWords);

     public String  updated_date = "";//new Date();
     public String  inserted_date = "";//new Date();

    //endregion







    //315
    public boolean canILearn() {
        return canIGo();
    }




    //323
    public boolean canIGo() {

        boolean returnValue = false;

        switch (recallItemCategory) {
            case "Words":
                if (title.length() > 0) {
                    if (schemeTitles.length() > 0) {
                        return true;
                    }
                }

            case "Music":
                if (title.length() > 0) {
                    if (schemeTitles.length() > 0) {
                        return true;
                    }
                }
        }

        return returnValue;
    }


    public boolean Go() {
        if (!canIGo()) {
            return false;
        }
        answeredState = ANSWERED_ENUM.NotAnswered;
        currentBusStopNumber = 1;
        journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling;

        Date now = new Date(Calendar.getInstance(Locale.UK).getTimeInMillis());
        goTime = now;
        prevEventTime = nextEventTime; //needed to calc update alarm on reboot
        learnTime = now;
        if (stops.length == 0) {
            if (schemeTitles.length() > 0) {
                stops = schemeTitles.split(",");
            }
        }



        Date eventTime = LibraryJava.StringToDateInterval(currentStopTitle(), now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(eventTime);
        calendar.add(Calendar.SECOND, 30);//trick so user will see, for 20 Minute interval, 20 mins to go, not 19 as seconds have past.

        nextEventTime = calendar.getTime();
        return true;
    }


    public boolean LEARN() {
        boolean returnValue = false;

        if (canIGo()) {
            answeredState = ANSWERED_ENUM.NotAnswered;
            currentBusStopNumber = 0;
            journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning;

            learnTime = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
            prevEventTime = learnTime;
            nextEventTime = learnTime;
            returnValue = true;
        }

        return returnValue;
    }


    public boolean isCurrentStopOnLearningDayButIsNotToday() {
        boolean returnValue = false;

        Date date = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        if (learnTime == null) {
            learnTime = date;
        } else {
            return returnValue;
        }

        if (date.getDay() == learnTime.getDay() && date.getYear() == learnTime.getYear() && date.getDate() == learnTime.getDate()) {
            return returnValue;
        }

        if (currentBusStopNumber == 0) {
            return returnValue;
        }

        if (stops.length == 0 && schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
        }

        if (stops.length <= 0) {
            return returnValue;
        }

        if (currentBusStopNumber > stopCount) {
            currentBusStopNumber = 1;
        }

        String title = stops[currentBusStopNumber - 1];

        if (LibraryJava.isDateIntervalGreaterThan1Day(title)) {
            returnValue = true;
        }

        return returnValue;
    }


    public boolean isCurrentStopOnLearningDay() {
        boolean returnValue = false;


        if (currentBusStopNumber == 0) {
            return returnValue;
        }

        if (stops.length == 0 && schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
        }

        if (stops.length <= 0) {
            return returnValue;
        }

        if (currentBusStopNumber > stopCount) {
            currentBusStopNumber = 1;

        }

        String title = stops[currentBusStopNumber];
        Date date = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());

        if (!(date.getDay() == learnTime.getDay() && date.getYear() == learnTime.getYear() && date.getDate() == learnTime.getDate()) && LibraryJava.isDateIntervalGreaterThan1Day(title)) {

            returnValue = true;
        }


        return returnValue;
    }


    public int findIndexOfNextNonLearningDay() {
        int returnValue = currentBusStopNumber;

        if (currentBusStopNumber == 0) {
            return 0;
        }

        if (stops.length == 0 && schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
        }

        if (currentBusStopNumber >= stops.length) {
            return stops.length - 1;
        }

        int zeroIndex = currentBusStopNumber - 1;

        for (int i = 0; i < stops.length; i++) {
            String title = stops[i];

            if (LibraryJava.isDateIntervalGreaterThan1Day(title)) {
                returnValue = i + 1;
                break;
            }
        }
        return returnValue;
    }











    public enum ITEM_JOURNEY_STATE_ENUM {
        JourneyStateInDepotSetup, JourneyStateInDepotLearning, JourneyStateAtStop, JourneyStateTravellingOverdue, JourneyStateTravelling, JourneyStateEnded, JourneyStateWaitingForOthers, Unknown
    }



    public static ITEM_JOURNEY_STATE_ENUM getJourneyStateInt(Integer i) {

        switch (i) {
            case 0:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup;

            case 1:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning;

            case 2:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop;

            case 3:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateTravellingOverdue;

            case 4:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling;

            case 5:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateEnded;

            case 6:
                return ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers;
            default:
                return ITEM_JOURNEY_STATE_ENUM.Unknown;

        }

    }

    public String getJourneyState(ITEM_JOURNEY_STATE_ENUM i) {
        String returnValue = "";
        switch (i) {
            case JourneyStateInDepotSetup:
                return "Setup";

            case JourneyStateInDepotLearning:
                return "Learning";

            case JourneyStateAtStop:
                return "Recalling";

            case JourneyStateTravellingOverdue:
                return "Ready for Prompting";

            case JourneyStateTravelling:
                return "Not Yet Ready For Prompting";

            case JourneyStateEnded:
                return "Ended";

            case JourneyStateWaitingForOthers:
                return "Waiting for Others";
            case Unknown:
                return "Unknown";

        }
        return returnValue;
    }


    //1254
    public String tableStateToString(TABLE_SECTION_STATE_ENUM state) {
        return getTableState(state);
    }

    public enum TABLE_SECTION_STATE_ENUM {
        TableSectionStateSetup, TableSectionStateLearning, TableSectionStateStop,TableSectionStateOverdue,
        TableSectionStateTravelling,TableSectionStateEnded,TableSectionStateUnknown
    }


    public String getTableState(TABLE_SECTION_STATE_ENUM state) {
        String returnValue = "";
        switch (state) {
            case TableSectionStateSetup:
                return "Setup";
            case TableSectionStateLearning:
                return "Learning";
            case TableSectionStateStop:
                return "Recalling";

            case TableSectionStateOverdue:
                return "Ready for Prompting";

            case TableSectionStateTravelling:
                return "Not Yet Ready For Prompting";

            case TableSectionStateEnded:
                return "Ended";
            case TableSectionStateUnknown:
                return "Unknown";

        }
        return returnValue;
    }
//TODO: is this ENUM accurate - do I need it?
    public enum RecallItemCategory_Enum {
        RecallItemCategoryWords,
        RecallItemCategoryFaces,
        RecallItemCategoryMusic,
        RecallItemCategoryPlants,
        RecallItemCategoryLanguage,
        RecallItemCategoryArt,
        RecallItemCategoryLegal,
        RecallItemCategoryPeriodic,
        RecallItemCategoryPlanet,
        RecallItemCategoryYoga
    }



    public String getDescription(RecallItemCategory_Enum state) {
        switch (state) {
            case RecallItemCategoryWords:
                return "Words";
            case RecallItemCategoryMusic:
                return "Music";
        }
        return "Unknown";
    }

    public String print() {

        return String.format(" %s, journey:%s stop:%s next:%s ",title,getJourneyState(journeyState),currentStopTitle(), nextEventTime);
    }

    public int getCurrentState() {
        return mCurrentITEM_JOURNEY_STATE;
    }


    public enum ANSWERED_ENUM {NotAnswered, AnsweredInCorrect, AnsweredCorrect}



    public void setAnsweredState(ANSWERED_ENUM answeredState) {
        this.answeredState = answeredState;
        if (!isloading) {
            updatePropertyLocal("answeredState", answeredState, UID);
        }
    }



    public void setStopCount(int stopCount) {
        this.stopCount = stopCount;
    }

    public int getStopCount() {
        int returnValue = 0;

        if (schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
            returnValue = stops.length;
        }
        setStopCount(returnValue);
        return returnValue;
    }








    public void setRecallItemCategory(String recallItemCategory) {
        this.recallItemCategory = recallItemCategory;

    }


    public void setNextEventTime(Date nextEventTime) {
        this.nextEventTime = nextEventTime;

        if (!isloading) {
            updatePropertyLocal("nextEventTime", nextEventTime, UID);
        }
    }


    public void setLearnTime(Date learnTime) {
        this.learnTime = learnTime;
    }


    public void setGoTime(Date goTime) {
        this.goTime = goTime;
        if (!isloading) {
            updatePropertyLocal("goTime", goTime, UID);
        }

    }

    public void setPrevEventTime(Date prevEventTime) {
        this.prevEventTime = prevEventTime;
        if (!isloading) {
            updatePropertyLocal("prevEventTime", prevEventTime, UID);
        }
    }

    //NOTE: this should cater for NOT JUST music
    //line#250
    public void setSchemeTitles(String schemeTitles) {
        //wiil set
        this.schemeTitles = schemeTitles; //set
        //did Set
        if (!isloading) {
            updatePropertyLocal("busDepotUID", schemeTitles, UID);
        }
    }

    //279



    public void setPhraseStopTiming(String phraseStopTiming) {
        this.phraseStopTiming = phraseStopTiming;
        //did set
        if (this.phraseStopTiming.length() == 0) {
            // this.phraseStopTiming = phraseStopTiming;
        } else {
            String match = findClosestTimingMatch(this.phraseStopTiming);
            this.phraseStopTiming = match;
        }
    }








    public void setTitle(String title) {
        this.title = title;
        if (!isloading) {
            updatePropertyLocal("title", title, UID);
        }
    }


    public void setLinkedPhrases(String linkedPhrases) {
        this.linkedPhrases = linkedPhrases;
        if (!isloading) {
            updatePropertyLocal("linkedPhrases", linkedPhrases, UID);
        }

    }


    public void setWords(String words) {
        this.words = words;
        if (!isloading) {
            updatePropertyLocal("words", words, UID);
        }
    }

    public void setRecallGroupUID(String recallGroupUID) {
        this.recallGroupUID = recallGroupUID;
        if (!isloading) {
            updatePropertyLocal("busDepotUID", recallGroupUID, UID);
        }
    }





    @Override
    public String getPropertyFromLocal(String property, String UID) {


        return null;
    }

    @Override
    public Object updatePropertyLocal(String property, Object value, String UID) {
        return null;
    }


    public enum RECALLITEM_TIMINGS_STATE_ENUM {
        RECALLITEM_TIMINGS_STATE_OVERDUE(0), RECALLITEM_TIMINGS_STATE_EARLY(1), RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY(2), RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE(3);

        private int action;

        // getter method
        public int getAction() {
            return this.action;
        }

        RECALLITEM_TIMINGS_STATE_ENUM(int action) {
            this.action = action;
        }

    }

    //line# 642
    public String currentStopTitle() {
        if (currentBusStopNumber >= 1) {
            int zeroBasedIndex = currentBusStopNumber - 1;
            stopCount = getStopCount();
            if (zeroBasedIndex >= stopCount) {
                return "ended";
            }
            if (stops.length == 0) {
                if (schemeTitles.length() > 0) {
                    stops = schemeTitles.split(",");
                    return stops[zeroBasedIndex];
                }
            } else {
                if (schemeTitles.length() > 0) {
                    stops = schemeTitles.split(",");
                    return stops[zeroBasedIndex];
                }
            }
        } else { //0 is recallgroup
            return "learning";
        }
        return "Error";
    }

    //795
    public boolean onLastStop() {
        boolean returnValue = false;
        if (phraseStopTiming.length() > 0) {
            //music - stops are split into short part and full part - e.g. "10m,1H,1D,1W" and  "10m,1H,1D,1W.2W,1M,2M"
            if (phraseStopTiming == currentStopTitle()) {
                returnValue = true;
            } else if (currentBusStopNumber == stopCount) {
                returnValue = true;
            } else {
                returnValue = false;
            }
        } else if (currentBusStopNumber == stopCount) {
            returnValue = true;
        }
        return returnValue;
    }


    /// synonym for onLastStop()
    public boolean isLastStop() {
        return onLastStop();
    }

    //820
    public int lastStopNumber() {

        return stopCount;
    }

    //969
    public boolean haveIACue() {
        boolean returnValue = false;
        if (words.length() > 0) {
            returnValue = true;
        }
        return returnValue;
    }

    //987
    public boolean haveIATitle() {
        boolean returnValue = false;
        if (title.length() > 0) {
            returnValue = true;
        }
        return returnValue;
    }

    //1003
    public int currentBusStopIndex() {

        if (currentBusStopNumber < 1) { //0 is recallgroup
            return 0; //first
        }

        return currentBusStopNumber - 1; //zeroBase

    }

    //1016
    public boolean isBusWaitingOverdue() {
        boolean returnValue = false;

        if (journeyState != ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling) {
            return returnValue;
        }

        if (learnTime == null) {
            learnTime = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        }

        if (nextEventTime == null) {
            nextEventTime = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        }

        long nextEventTimevalue = nextEventTime.getTime();
        long difference = nextEventTimevalue - System.currentTimeMillis();

        if (difference < 0) {
            return true;
        }

        return false;
    }

    //1054
    public boolean haveIABusRoute() {
        boolean returnValue = false;

        if (schemeTitles.length() > 0) {
            return true;
        }
        return false;
    }

    //1069
    public String stopTitle(int number) {
        int stopNumber = number;
        if (stops.length == 0 && schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");

        }

        if (stops.length <= 0) {
            return "learning";
        }

        if (stopNumber < 1) {
            stopNumber = 1;
        }

        if (stopNumber > stops.length) {
            stopNumber = stops.length - 1;
        }

        int zeroBasedIndex = stopNumber - 1;

        return stops[zeroBasedIndex];
    }

    //1100
    public String currentBusStopFullTitle() {
        String shortTitle = currentStopTitle();

        String longTitle = LibraryJava.shortIntervalToFullInterval(shortTitle);

        return longTitle;
    }

    //1114
    public String nextBusStopTitle() {

        if (currentBusStopNumber > 0) {

        } else {
            return "1H";
        }

        int zeroBasedIndex = currentBusStopNumber - 1;
        zeroBasedIndex += 1;

        if (stops.length == 0 && schemeTitles.length() > 0) {
            stops = schemeTitles.split(",");
        }

        if (stops.length <= 0) {
            return "1H";
        }


        if (zeroBasedIndex >= stopCount) {
            zeroBasedIndex = stopCount - 1;
        }
        return stops[zeroBasedIndex];
    }

    //1144
    public String getPreviousRelativeBusStopTitle() {

        String returnValue = currentStopTitle();

        int index = currentBusStopIndex();
        index -= 1;

        if (index < stops.length) {

        } else {
            return returnValue;
        }

        if (index >= 0) {
            returnValue = stops[index];
        }
        return returnValue;

    }

    //1166
    public boolean canIFinishSetup() {

        boolean returnValue = false;

        if (canIGo()) {
            if (haveIAPromptImage(intUID)) {
                returnValue = true;
            }
        }

        return false;
    }

    //1182
    public boolean haveIAPromptImage(int intUID) {
        boolean returnValue = false;
        if (recallItemCategory.equals(getDescription(RecallItemCategory_Enum.RecallItemCategoryMusic))) {
            String promptfilename = LibraryFilesystem.getFileNameMainMusicImageByBusID(intUID);
            if (LibraryFilesystem.exists(promptfilename)) {
                return true;
            }
        }

        return returnValue;
    }

    //1202
    public int secondsToGo() {
        int returnValue = 0;

        if (goTime == null) {
            return returnValue;
        }

        if (currentBusStopNumber <= 0) {
            return returnValue;
        }
        String currentInterval = stops[currentBusStopNumber - 1];
        Date now = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        if (goTime != null) {
            now = goTime;
        }
        Date eventTime = LibraryJava.StringToDateInterval(currentInterval, now);

        long start = System.currentTimeMillis();
        long Millisecond = eventTime.getTime();
        long difference = Millisecond - start;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
        returnValue = (int) seconds;

        return returnValue;
    }

    //1234
    public int minutesToGo() {
        int returnValue = 0;


        int seconds = secondsToGo();

        if (seconds > 60) {
            returnValue = seconds / 60;
        }

        return returnValue;
    }

    //1254
    public String journeyStateToString(ITEM_JOURNEY_STATE_ENUM i) {
        return getJourneyState(i);
    }


    //447
    public boolean leaveStop() {
        boolean returnValue = false;
        switch (journeyState) {
            case JourneyStateAtStop:
                if (isLastStop()) {
                    journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling;
                    currentBusStopNumber = stops.length;
                } else {
                    moveNext();
                    answeredState = ANSWERED_ENUM.NotAnswered;
                    journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateTravelling;
                    Date now = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
                    prevEventTime = nextEventTime;
                    Date refDate = now;
                    boolean isUnusual = RecallItem.isStopMoveUnusual(currentStopTitle(), refDate);

                    if (isUnusual) {
                        boolean changed = false;
                        int currentbusstopindex = currentBusStopIndex();
                        for (int i = 0; i < stops.length; i++) {
                            String stopTitle = stops[i];

                            Date stopDate = LibraryJava.StringToDateInterval(stopTitle, now);

                            if (now.getTime() > stopDate.getTime()) {
                                moveToStop(i + 1);

                                changed = true;
                                break;
                            }
                        }

                        if (!changed) {
                            moveEnd();
                            nextEventTime = LibraryJava.StringToDateInterval(currentStopTitle(), now);

                        }

                    } else {
                        nextEventTime = LibraryJava.StringToDateInterval(currentStopTitle(), now);

                    }

                }
                returnValue = true;
            default:
                break;


        }

        return returnValue;
    }

    //562
    public void moveToStop(int stopNumber) {
        if (stopNumber < 0) {
            moveStart();
        } else if (stopNumber > stops.length) {
            moveEnd();
        } else {
            currentBusStopNumber = stopNumber;
        }
        answeredState = ANSWERED_ENUM.NotAnswered;
    }

    //597
    public void moveLearn() {
        currentBusStopNumber = 1;
        answeredState =ANSWERED_ENUM.NotAnswered;
        journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotLearning;
    }

    //577
    public void moveEnd() {
        if (recallItemCategory.equals(getDescription(RecallItemCategory_Enum.RecallItemCategoryMusic)) && phraseStopTiming.length() == 0) {
            phraseStopTiming = Schemes.DEFAULT_SCHEME_MUSIC_PHRASE_STOP_TITLE;
        }
        if (recallItemCategory.equals(getDescription(RecallItemCategory_Enum.RecallItemCategoryMusic)) && phraseStopTiming.length() > 0) {
            int s = phraseStopTimingStopCount();
            moveToStop(s);
        } else {
            currentBusStopNumber = stopCount;
            answeredState = ANSWERED_ENUM.NotAnswered;
        }

    }

    public int phraseStopTimingStopCount() {
        int returnValue = stops.length;
        if (phraseStopTiming.length() > 0) {
            String match = findClosestTimingMatch(phraseStopTiming);

//    returnValue=stops[]

            phraseStopTiming = match;
        }

        return returnValue;
    }

    //554
    public void moveStart() {
        currentBusStopNumber = 1;
        answeredState = ANSWERED_ENUM.NotAnswered;
        journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup;
    }


    public static boolean isStopMoveUnusual(String interval, Date date) {
        Date now = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        Date date1 = LibraryJava.StringToDateInterval(interval, date);
        if (date1.getTime() < now.getTime()) {
            return true;
        } else {
            return false;
        }

    }

    //518
    public void moveNext() {
        if (phraseStopTiming.length() > 0) {
            if (currentBusStopNumber > (stops.length - 1)) {
                currentBusStopNumber = stops.length;
            } else if (phraseStopTiming.equals(currentStopTitle())) {

            } else {
                currentBusStopNumber += 1;
            }
        } else {
            if (currentBusStopNumber > (stops.length - 1)) {
                currentBusStopNumber = stops.length;
            } else {
                currentBusStopNumber += 1;
            }

            answeredState = ANSWERED_ENUM.NotAnswered;
            journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop;
        }
    }

    //543
    public void movePrev() {
        if (currentBusStopNumber <= 1) {
            currentBusStopNumber = 1;
        } else {
            currentBusStopNumber -= 1;
        }
        answeredState =ANSWERED_ENUM.NotAnswered;

    }

    //605
    public boolean arriveAtNextStop() {
        boolean returnValue = false;
        switch (journeyState) {
            case JourneyStateTravelling:
                journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop;
                answeredState = ANSWERED_ENUM.NotAnswered;
                returnValue = true;
            case JourneyStateTravellingOverdue:
                journeyState = ITEM_JOURNEY_STATE_ENUM.JourneyStateAtStop;
                answeredState = ANSWERED_ENUM.NotAnswered;
                returnValue = true;
            default:
                break;
        }
        return returnValue;
    }


    public String findClosestTimingMatch(String stopTiming) {
        String returnValue = stopTiming;
        getStopCount();
        if (stops.length == 0) {
            schemeTitles = stopTiming;
            if (schemeTitles.length() > 0) {
                stops = schemeTitles.split(",");
            }
        }
        if (!(stops.length > 0)) {
            // invalid set
            return returnValue;
        }
//quick test and exit - full match
        for (String stop : stops) {
            if (stop == stopTiming)
                return stopTiming;
        }
        Pair stopTimingComponents = LibraryJava.timingToComponents(stopTiming); //e.g. 10m,1H,2D,1W,1M or 1Y
        String interval = (String) stopTimingComponents.second;
        //before first?
        if (stops[0].length() > 0) {
            Pair firstTiming = LibraryJava.timingToComponents(stops[0]);
            if ((String) firstTiming.second == interval && (int) stopTimingComponents.first < (int) firstTiming.first) {
                return stops[0];
            }
        }
        int topIndex = stops.length - 1;
        switch (interval) {

            case "m":
                for (int i = topIndex; i >= 0; i--) {
                    int index = i;
                    String stopTitle = stops[index];
                    if ("m" == LibraryJava.timingToComponents(stopTitle).second) {
                        return stopTitle;

                    }
                }
                if (stops[topIndex] == null) {
                    return "10m";
                } else {
                    return stops[topIndex];
                }


            case "H":
                for (int i = topIndex; i >= 0; i--) {
                    int index = i;
                    String stopTitle = stops[index];
                    if ("H" == LibraryJava.timingToComponents(stopTitle).second) {
                        return stopTitle;

                    }
                }
                if (stops[topIndex] == null) {
                    return "1H";
                } else {
                    return stops[topIndex];
                }

            case "D":
                for (int i = topIndex; i >= 0; i--) {
                    int index = i;
                    String stopTitle = stops[index];
                    if ("D" == LibraryJava.timingToComponents(stopTitle).second) {
                        return stopTitle;

                    }
                }
                if (stops[topIndex] == null) {
                    return "1D";
                } else {
                    return stops[topIndex];
                }

            case "W":
                for (int i = topIndex; i >= 0; i--) {
                    int index = i;
                    String stopTitle = stops[index];
                    if ("W" == LibraryJava.timingToComponents(stopTitle).second) {
                        return stopTitle;

                    }
                }
                if (stops[topIndex] == null) {
                    return "1W";
                } else {
                    return stops[topIndex];
                }

            case "Y":
                for (int i = topIndex; i >= 0; i--) {
                    int index = i;
                    String stopTitle = stops[index];
                    if ("Y" == LibraryJava.timingToComponents(stopTitle).second) {
                        return stopTitle;
                    }
                }
                if (stops[topIndex] == null) {
                    return "1Y";
                } else {
                    return stops[topIndex];
                }

            default:
                break;
        }
        return returnValue;
    }
}
