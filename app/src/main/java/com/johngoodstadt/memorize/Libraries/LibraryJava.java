package com.johngoodstadt.memorize.Libraries;

//import android.util.Pair;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.core.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.johngoodstadt.memorize.models.RecallItem;
import com.johngoodstadt.memorize.utils.ConstantsJava;
import com.johngoodstadt.memorize.utils.ConstantsJava.*;

import static com.johngoodstadt.memorize.utils.ConstantsJava.MUSIC_SEPERATOR;
import static com.johngoodstadt.memorize.utils.ConstantsJava.MUSIC_SEPERATOR_WITH_SPACES;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentDay;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentHour;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentMinute;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentMonth;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentWeek;
import static com.johngoodstadt.memorize.utils.ConstantsJava.timeComponentYear;


public class LibraryJava {


    public static String makeUID() {
        String uuid = UUID.randomUUID().toString().toUpperCase();
        return uuid;
    }
    //MARK: - Check state
    /// Check if incoming text has 1 "-" or 2 "-"s for balidity
    ///
    /// - Parameters:
    ///   - text:title of group e.g. "Beatles - blackbird" or "Beethoven - sonata - prelude"
    ///
    /// - Returns: true or false
    public static boolean isSectionTitleMusicFormattedCorrectly(String txt) {
        boolean returnValue = false;
        String arr[] = txt.split("-");
        switch (arr.length) {
            case 2:
            case 3:
                returnValue = true;
                break;
            default:
                break;

        }
        return returnValue;
    }


    /// Check if incoming text has 1 "-" or 2 "-"s for validity
    ///
    /// - Parameters:
    ///   - text:title of group e.g. "Beatles - blackbird" or "Beethoven - sonata - prelude"
    ///
    /// - Returns: true or false
    public static boolean isGroupTitleMusicFormattedCorrectly(String text) {
        return isSectionTitleMusicFormattedCorrectly(text);

    }
    /// Check if incoming text has 1 "-" or 2 "-"s for balidity
    ///
    /// - Parameters:
    ///   - text:title of phrase e.g. "blackbird - A" or "sonata - prelude - B"
    ///
    /// - Returns: true or false
    ///
    /// e.g. incoming format must be "A - B - C" where "A" ans "B" is any word but "C" is an alpha letter from "A" to "Z"
    ///
    public static boolean isMusicPhraseFormattedCorrectly(String txt) {
        boolean returnValue = false;
        String arr[] = txt.split("-");
//NOTE: could check for A-Z,AA-ZZ etc
        if (arr.length == 3) {//2 seperators - 3 sections

            return true;
        }
        return returnValue;
    }
    /// Check if incoming text has 1 "-" or 2 "-"s for balidity
    ///
    /// - Parameters:
    ///   - text:title of piece e.g. "blackbird - A" or "sonata - prelude - B"
    ///
    /// see isGroupTitleMusicFormattedCorrectly()
    ///
    /// - Returns: true or false
    ///
    public static boolean isPieceTitleFormattedCorrectly(String txt) {
        boolean returnValue = false;
        String arr[] = txt.split("-");
        switch (arr.length) {
            case 2:
            case 3:
                returnValue = true;
                break;
            default:
                break;

        }
        return returnValue;
    }
    /// Check if incoming text has 3 parts
    ///
    /// - Parameters:
    ///   - text:title of piece e.g. "sonata - prelude - B"
    ///
    ///
    /// - Returns: true or false
    ///
    public static boolean isSectionTitle3Scheme(String txt) {
        boolean returnValue = false;
        String arr[] = txt.split("-");
        if (arr.length == 3)
            returnValue = true;

        return returnValue;
    }

    // multiplier =2,5
    //intervalType=m,h
    /// test of interval code is 1 day or less
    ///
    /// - Parameters:
    ///   - dateInterval:string e.g. "20m","2H","1D","3D"

    ///
    /// - Returns: true or false
    ///
    public static boolean isDateIntervalLessThan1Day(String txt) {
        boolean returnValue = false;
//        String intervalTemp[] = timingToComponents(txt).split("-");
//        String interval = intervalTemp[1];
        String interval = (String)timingToComponents(txt).second;
        switch (interval) {
            case "m":
                returnValue = true;
            case "H":
                returnValue = true;
            default:
                break;
        }
        return returnValue;
    }

    //see isDateIntervalLessThan1Day - inverse
    public static boolean isDateIntervalGreaterThan1Day(String dateInterval) {
        return !isDateIntervalLessThan1Day(dateInterval);
    }
    /// calculate and return first part
    ///
    /// - Parameters:
    ///   - text:title of phrase e.g.  "bach - sonata - prelude"
    ///
    /// - Returns: e.g. "bach"
    ///
    /// return last component of string
    ///
    /// - Parameters:
    ///   - text:title of phrase e.g. "blackbird - A" or "sonata - prelude - B"
    ///
    /// - Returns: last letter e.g. "A","B" etc
    ///
    /// e.g. incoming format must be "A - B - C" where "A" ans "B" is any word but "C" is an alpha letter from "A" to "Z"
    ///
    public static String getSuffixLetter(String text) {
        String returnValue = "";
        String arr[] = text.split("-");
        if (arr.length == 3 || arr.length == 2) {
            returnValue = arr[arr.length - 1];
        }
        return returnValue.trim();
    }




    /// return part of title
    ///
    /// - Parameters:
    ///   - text:title of phrase e.g.  "Bach - sonata - prelude"
    ///   - part:first, second or third of 3 parts
    ///
    /// - Returns: e.g. "Beethoven - sonata - prelude" or "Bach - sonata - allegro"
    ///
    /// e.g.
    ///"Bach - Suite 1 - Prelude"  : Composer - Piece - Movement
    ///return "Suite 1"
    public static String getPartFromPiece(String text, WhichPartOfPiece part) {
        String returnValue = text;
        if (isGroupTitleMusicFormattedCorrectly(text)) {
            String arr[] = text.split(MUSIC_SEPERATOR_WITH_SPACES);//guarenteed to be 2 or 3

            switch (part) {
                case composer://just do Piece first section - no Phrases
                    returnValue = arr[0];
                    break;
                case piece:
                    returnValue = arr[1];//middle
                    break;
                case movement:
                    if (arr.length == 3)
                        returnValue = arr[2];//if 2 then also middle
                    else
                        returnValue = "";
            }
        }
        return returnValue;
    }


    /// calculate date time from now depending on input code e.g. "1D" means 1 day from now
    ///
    /// - Parameters:
    ///   - timing:code - "10m","2H","3D","4W",,,
    ///   - now: - now date to base calc upon
    ///
    /// - Returns: Date() of future date - e.g.' 1 Jan 2019 10:10:10' and "1D "goes to '2 Jan 2019 10:10:10'
    ///
    public static Date StringToDateInterval(String timing, Date now) {
        Date returnValue = now;
        // String strTemp[] = timingToComponents(timing).split("-");
        String interval = null;
        try {
            interval = (String)timingToComponents(timing).second;
        } catch (Exception e) {
            e.printStackTrace();
        }
        int multiplier = 0;
        try {
            multiplier = (int)timingToComponents(timing).first;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        switch (interval) {
            case "m":
                c.add(Calendar.MINUTE, multiplier);
                returnValue = c.getTime();
                break;

            case "H":
                c.add(Calendar.HOUR, multiplier);
                returnValue = c.getTime();
                break;

            case "M":
                c.add(Calendar.MONTH, multiplier);
                returnValue = c.getTime();
                break;

            case "D":
                c.add(Calendar.DATE, multiplier);
                returnValue = c.getTime();//same with c.add(Calendar.DAY_OF_MONTH, 1);
                break;

            case "W":
                long crnt = now.getTime();
                long nxtdat = (long) multiplier * 86400000 * 7;
                Date afterAddingW = new Date(crnt + nxtdat);
                returnValue = afterAddingW;
                break;

            case "Y":
                c.add(Calendar.YEAR, multiplier);
                returnValue = c.getTime();
                break;

            default:
                break;
        }
        return returnValue;
    }

    /// transform number of seconds to human readable text
    ///
    /// - Parameters:
    ///   - totalSeconds:Int e.g. 600 would = 10 minutes
    ///
    /// - Returns: String: "Due in 3 days", "Overdue 3 hours"...
    ///
    public static String secondsToString(int totalSeconds) {
        String returnValue = "";
        boolean overdue = false;
        if (totalSeconds < 0) {
            overdue = true;
        }
        long absSeconds = Math.abs(totalSeconds);
        Date dt = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        long fTime = absSeconds * 1000;
        long aa = dt.getTime();
        long cc = aa + fTime;
        long futureDate = dt.getTime() + fTime;
        long days = daysBetweenDate(dt.getTime(), futureDate);
        long absDays = Math.abs(days);
        long minutes = (absSeconds / 60) % 60;
        long hours = absSeconds / 3600;

        if (absDays == 1) {
            if (overdue)
                returnValue = "overdue 1 day"; // because was overdue tomorrow!
            else
                returnValue = "due tomorrow";//@"1 day";
        } else if (absDays > 0) {
            if (absDays < 7)
                returnValue = "due in " + absDays + " days";
            else {
                long daypart = absDays % 7;
                long weeks = absDays / 7;
                if (weeks == 0) {
                    returnValue = "due in" + daypart + " days";
                } else if (weeks == 1 && daypart == 0) {
                    returnValue = "due in 1 week";
                } else if (weeks == 1 && daypart == 1) {
                    returnValue = "due in 1 week 1 day";
                } else if (weeks == 1) {
                    returnValue = "due in 1 week " + daypart + " days";
                } else if (weeks > 1 && weeks < 4 && daypart == 0) {
                    returnValue = "due in " + weeks + " weeks";
                } else if (weeks > 1 && weeks < 4 && daypart == 1) {
                    returnValue = "due in " + weeks + "weeks 1 day";
                } else if (weeks == 2) {
                    returnValue = "due in 2 weeks " + weeks + " days";
                } else if (weeks == 3) {
                    returnValue = "due in 3 weeks " + daypart + " days";
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(futureDate);
                    Date dtFuture = new Date(calendar.getTimeInMillis());


                    long dateComponentsYear = Math.abs(daysBetweenYear(dt, dtFuture, "YEAR"));
                    long dateComponentsnMonth = Math.abs(daysBetweenYear(dt, dtFuture, "MONTH"));
                    long dateComponentsnDays = Math.abs(daysBetweenYear(dt, dtFuture, "DAY"));

                    if (dateComponentsYear == 0) {
                        if (dateComponentsnDays == 0) {//even months
                            if (dateComponentsnMonth == 1) {
                                returnValue = "due in 1 month";
                            } else {
                                returnValue = "due in " + dateComponentsnMonth + " months";
                            }
                        } else {
                            if (dateComponentsnDays <= 3) {

                                if (dateComponentsnMonth == 1) {
                                    returnValue = "due in about 1 month";
                                } else {
                                    returnValue = "due in about " + dateComponentsnMonth + " months";
                                }
                            } else if (dateComponentsnDays >= 27 && dateComponentsnMonth == 0) {
                                returnValue = "due in about 1 month";
                            } else if (dateComponentsnDays >= 27) {
                                // dateComponentsnMonth+=dateComponentsnMonth+1;
                                if (dateComponentsnMonth == 1)
                                    returnValue = "due in about " + dateComponentsnMonth + " month";
                                else
                                    returnValue = "due in about " + dateComponentsnMonth + " months";
                            } else {
                                returnValue = "due in " + dateComponentsnMonth + " months " + dateComponentsnDays + " days";
                            }
                        }

                    } else {//we re now into years!
                        if (dateComponentsYear == 1 && dateComponentsnMonth == 0 && (dateComponentsnDays <= 27)) {
                            returnValue = "due in about 1 year";
                        } else if (dateComponentsYear == 1 && dateComponentsnMonth == 0) {
                            returnValue = "due in about 1 year";
                        } else if (dateComponentsYear == 1 && dateComponentsnMonth == 1) {
                            returnValue = "due in about 1 year 1 month";
                        } else if (dateComponentsYear == 1 && dateComponentsnMonth > 1) {
                            //returnValue =  "due in about 1 year \(String(describing: dateComponents.month)) months"
                            returnValue = "due in about 1 year " + dateComponentsnMonth + " months";
                        } else if (dateComponentsYear > 1 && dateComponentsnMonth == 1) {
                            //  returnValue =  "due in about \(String(describing: dateComponents.year)) years 1 month"
                            returnValue = "due in about " + dateComponentsYear + " years 1 month";
                        } else if (dateComponentsYear > 1 && dateComponentsnMonth > 1) {
                            // returnValue =  "due in about \(String(describing: dateComponents.year)) years \(String(describing: dateComponents.month)) months"
                            returnValue = "due in about " + dateComponentsYear + " years " + dateComponentsnMonth + " months";
                        } else { //anything else in years
                            //    returnValue =  "due in about \(String(describing: dateComponents.year)) years"
                            returnValue = "due in about " + dateComponentsYear + " years";
                        }
                    }

                }
            }


        } else if (hours == 0) {
            if (overdue) {
                if (minutes >= 0 && minutes <= 1) {//00 to 59 seconds
                    returnValue = "due now";
                } else {
                    returnValue = "overdue " + minutes + " minutes";
                }
            } else {
                if (minutes == 0) { //00 to 59 seconds
                    //#warning TODAY ONLY
                    returnValue = "due now";

                } else {
                    if (minutes >= 0 && minutes <= 1) {//00 to 59 seconds
                        returnValue = "due in 1 minute";
                    } else {
                        returnValue = "due in " + minutes + " minutes";
                    }

                }
            }
        } else if (hours == 1) {
            returnValue = "due in 1 hour";
        } else if (hours >= 24) {
            returnValue = "due in " + hours + ":" + minutes;//if calculated time is tomorrow then work in days
        } else {
            returnValue = "due in " + hours + " hours";
        }
        if (overdue) {
            returnValue = returnValue.trim();
            returnValue = returnValue.replace("due in", "overdue");
        }
        return returnValue;
    }

    /// transform standard group title 'composer - piece - movement' to 'Composer - piece - A'
    ///
    /// - Parameters:
    ///   - text:title - e.g. "Bach - Suite 1 - Menuet I"
    ///
    /// - Returns: compound return success OK and new title if true
    ///
    public static Pair<Boolean,String> transformGroupNameToTitle(String text) {
        String returnValue = text;
        if (!isGroupTitleMusicFormattedCorrectly(text)) {
            return new Pair(false, returnValue);
        }

        String arr[] = text.split(MUSIC_SEPERATOR);
        if (arr.length == 3) {
            String s = arr[2];
            if (s.length() > 1) {
                arr[0] = arr[0].trim();
                arr[1] = arr[2].trim();
                arr[2] = "A";

                returnValue = arr[0] + MUSIC_SEPERATOR_WITH_SPACES + arr[1] + MUSIC_SEPERATOR_WITH_SPACES + arr[2];
            }
        }

        return new Pair(true, returnValue);

    }

    /// transform standard phrase title 'composer - piece - movement' to 'Composer - piece - A'
    ///
    /// - Parameters:
    ///   - text:title - e.g. "Bach - Suite 1 - Menuet I"
    ///
    /// - Returns: compound return success OK and new title if true -  "A" will go to "B"
    ///
    public static Pair transformTitleToNewTitle(String text) {
        String returnValue = text;
        if (!isSectionTitleMusicFormattedCorrectly(text)) {
            return new Pair(false, returnValue);
            //return ("false-" + returnValue);
        }
        try {
            String arr[] = text.split("-");
            if (arr.length == 3) {
                String s = arr[2].trim();
                if (s.length() == 1) {
                    arr[0] = arr[0].trim();
                    arr[1] = arr[1].trim();

                    char[] ch = new char[s.length()];
                    ch[0] = s.charAt(0);
                    int char1 = ch[0];
                    int endChar = (int) 'Z';
                    char1 = char1 + 1;
                    if (char1 > endChar) {
                        char1 = endChar;
                    }
                    arr[2] = Character.toString((char) char1);
                    returnValue = arr[0] + " - " + arr[1] + " - " + arr[2];

                }
            }
            return new Pair(true, returnValue);
        } catch (Exception e) {
            return new Pair(false, returnValue);
        }
    }


    // multiplier =2,5
    //intervalType=m,h
    /// examine 'timing' string and return amount in minutes
    ///
    /// - Parameters:
    ///   - text:string with 2 components e.g. "2D"
    ///
    /// - Returns: number in minutes e.g. 1H = 60, 1D = 1440 etc
    ///
    public static int StringToMinutes(String text) {
        int returnValue = 0;
        try {
            if (!text.isEmpty()) {
//                String strTemp[] = timingToComponents(text).split("-");
//                String interval = strTemp[1];
//                int multiplier = Integer.parseInt(strTemp[0]);
                int multiplier = (int)timingToComponents(text).first;
                String interval = (String)timingToComponents(text).second;


                switch (interval) {
                    case timeComponentMinute:
                        returnValue = multiplier;
                        break;

                    case timeComponentHour:
                        returnValue = multiplier * 60;
                        break;

                    case timeComponentDay:
                        returnValue = multiplier * 1440;
                        break;

                    case timeComponentWeek:
                        returnValue = multiplier * 1440 * 7;
                        break;
                    case timeComponentMonth:
                        returnValue = multiplier * 1440 * 30;
                        break;
                    case timeComponentYear:
                        returnValue = multiplier * 1440 * 365;
                        break;


                    default:
                        break;

                }
                return returnValue;
            }
            return returnValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return returnValue;
        }

    }

    /// examine 'timing' string and return amount as a string
    ///
    /// - Parameters:
    ///   - text:string with 2 components e.g. "2D"
    ///
    /// - Returns: string of time e.g. "2 Days" - ready for display
    ///
    public static String shortIntervalToFullInterval(String text) {
        String returnValue = "Unknown";
        if (!(text.length() > 0)) {
            return returnValue;
        }
        //String strTemp[] = timingToComponents(text).split("-");
        String interval = (String)timingToComponents(text).second;
        int multiplier = (int)timingToComponents(text).first;

        switch (interval) {
            case "m":
                if (multiplier == 1) {
                    returnValue = "1 Minute";
                } else {
                    returnValue = multiplier + " Minutes";
                }
                break;
            case "H":
                if (multiplier == 1) {
                    returnValue = "1 Hour";
                } else {
                    returnValue = multiplier + " Hours";
                }
                break;
            case "D":
                if (multiplier == 1) {
                    returnValue = "1 Day";
                } else {
                    returnValue = multiplier + " Days";
                }
                break;
            case "W":
                if (multiplier == 1) {
                    returnValue = "1 Week";
                } else {
                    returnValue = multiplier + " Weeks";
                }
                break;
            case "M":
                if (multiplier == 1) {
                    returnValue = "1 Month";
                } else {
                    returnValue = multiplier + " Months";
                }
                break;
            case "Y":
                if (multiplier == 1) {
                    returnValue = "1 Year";
                } else {
                    returnValue = multiplier + " Years";
                }
                break;
            default:
                break;

        }
        return returnValue;
    }


    /// combine 3 parts to 1
    ///
    /// - Parameters:
    ///   - piece:String e.g. "Sonata 4"
    ///   - movement:String e.g. "Allegro" - optional
    ///   - phrase:String e.g. "A" - not optional
    ///
    ///
    /// - Returns: string of appended parts e.g. "Beethoven - Sonata 4 - Allegro"
    ///
    public static Pair titleFrom3PartsPhrase(String piece, String movement, String phrase) {

        if (piece.length() > 1 && movement.length() > 1 && phrase.length() == 1) {//phrase could be 'A' to 'Z' - have to handle 'AA' to 'AZ'
            return new Pair(true, piece + MUSIC_SEPERATOR_WITH_SPACES + movement + MUSIC_SEPERATOR_WITH_SPACES + phrase);
            // returnValue = "true," + piece + ConstantsJava.MUSIC_SEPERATOR_WITH_SPACES + movement + ConstantsJava.MUSIC_SEPERATOR_WITH_SPACES + phrase;
        } else {
            return new Pair(false, "");
        }
    }
    /// text : 'piece - movement - suffix'
    /// return 'piece - movement'
    ///
    /// combine 3 parts to 1
    ///
    /// - Parameters:
    ///   - piece:String e.g. "Sonata 4"
    ///   - movement:String e.g. "Allegro" - optional
    ///   - phrase:String e.g. "A" - not optional
    ///
    ///
    /// - Returns: string of appended parts e.g. "Beethoven - Sonata 4 - Allegro"
    ///
    public static String dropLetterSuffixFromTitle(String text) {
        String returnValue = text;
        if (isSectionTitle3Scheme(text)) {
            String arr[] = text.split("-");
            if (arr.length == 3) {
                returnValue = arr[0].trim() + MUSIC_SEPERATOR_WITH_SPACES + arr[1].trim();
            }
        }
        return returnValue;

    }
    /// see titleFrom3PartsPhrase()
    public static Pair<Boolean, String> renamePhraseTitle(String piece, String movement, String phrase) {
        Pair<Boolean, String> pair = titleFrom3PartsPhrase(piece, movement, phrase);
        return pair;
    }

    /// simply work out 1 of 4 values - Well Overdue, Overdue by not much, early by not much, very early
    /// i.e. BUS_TIMINGS_STATE_OVERDUE, BUS_TIMINGS_STATE_IN_WINDOW_LATE, TIMINGS_STATE_EARLY, BUS_TIMINGS_STATE_IN_WINDOW_EARLY,
    /// In here is defined the definition of a 'window' e.g.if a user is 1 minute early for an HOUR interval is is not really early (In the Window)
    ///
    /// - Parameters:
    ///   - busTopTitle:String e.g. "2D", "4W" etc
    ///   - nextEventTime:Date - when the next thing should happen
    ///   - nowTime:Date() - time of this call
    ///
    /// - Returns: BUS_TIMINGS_STATE_ENUM
    ///
    public static RECALLITEM_TIMINGS_STATE_ENUM calcNowState(String busTopTitle, Date nextEventTime, Date nowTime) {
        RECALLITEM_TIMINGS_STATE_ENUM returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE;
        //  String intervalTemp[] = timingToComponents(busTopTitle).split("-");

        String interval = (String)timingToComponents(busTopTitle).second;
        //  String interval = intervalTemp[1];

        double window = 0.0;

        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time1 = sdf.format(nextEventTime);
        Date dtNow = new Date(Calendar.getInstance(Locale.US).getTimeInMillis());
        String time2 = sdf.format(dtNow);
        if (time1.equals(time2) && interval == timeComponentMinute || interval == timeComponentHour) {
            int minutes = StringToMinutes(busTopTitle);
            long diffMinutes = minuteBetweenDate(nowTime.getTime(), nextEventTime.getTime());


            if (minutes >= 0 && minutes <= 20) {
                window = 2;//minutes - 2 before to 2 after
            } else if (minutes >= 21 && minutes <= 120) {//2 hours
                window = 20;
            } else if (minutes >= 121 && minutes <= 240) {//4 hours
                window = 30;
            } else if (minutes >= 241 && minutes <= 480) {//8 hours
                window = 60;
            } else if (minutes >= 481 && minutes <= 1080) {//18 hours
                window = 60;
            } else {
                window = 240;//> 18 Hours
            }

            if (diffMinutes <= 0) {
                if (Math.abs(diffMinutes) <= window) {
                    if (diffMinutes < 0) { // minus - NOTE: where is Zero in this
                        returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY;
                    } else {
                        returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE; //zero is here
                    }
                } else {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_EARLY;
                }
            } else { //in the future - so still to go
                if (diffMinutes <= window) {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE;
                } else {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_OVERDUE;
                }


            }
        } else {
            int days = StringToMinutes(busTopTitle) / 1440;
            long diffDays = daysBetweenDate(nowTime.getTime(), nextEventTime.getTime());

            if (days >= 1 && days <= 3) {
                window = 1;
            } else if (days >= 4 && days <= 6) {
                window = 2;
            } else if (days >= 7 && days <= 13) {// 1 week to 2 weeks
                window = 3;
            } else if (days >= 14 && days <= 21) {//2 weeks to 3 weeks
                window = 5;
            } else {
                window = 7;//week early for any months
            }
            if (diffDays <= 0) {

                if (Math.abs((diffDays)) <= window) {

                    if (diffDays <= 0) { // minus - NOTE: where is Zero in this
                        returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY;
                    } else {
                        returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE; //zero is here
                    }

                } else {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_EARLY;
                }
            } else {

                if ((diffDays) <= window) {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_LATE;
                } else {
                    returnValue = RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_OVERDUE;
                }
            }

        }

        return returnValue;
    }



    /// Check incoming text for words and return just 2 chars representing the whole
    ///
    /// - Parameters:
    ///   - text:title of words e.g. "Beethoven Sonata Prelude" returns "BS", "Prelude with Beethoven" returns "PB"
    ///
    /// - Returns: String - 2 chars only - best fit
    public static String getBest2LetterTitle(String text) {
        String returnValue = "NA";

//        if (true) {
//            return returnValue;
//        }

        String input = text.trim();
        String arrSplit[] = input.split("\\t|,|;|\\.|\\?|!|-|:|@|\\[|\\]|\\(|\\)|\\{|\\}|_|\\*|/|\\s|\\&");
        ArrayList<String> arList = new ArrayList<>();

        for (int i = 0; i < arrSplit.length; i++) {
            if (!arrSplit[i].equals("")) {
                arList.add(arrSplit[i]);
            }

        }
        String arr[] = new String[arList.size()];
        for (int i = 0; i < arList.size(); i++) {

            arr[i] = arList.get(i).trim();
        }
        if (arr.length == 0) {
            return returnValue;
        }

        if (arr.length == 1) {
            if (arr.length > 0) {
                int strLength = arr[0].length();
                if (strLength == 1)
                    returnValue = "" + arr[0].toString().charAt(0);
                else if (strLength == 2)
                    returnValue = arr[0].toString().substring(0, 1);
            } else {
                returnValue = "" + arr[0].toString().charAt(0);
            }
            return returnValue;
        } else if (arr.length == 2) {
            String st1 = "" + arr[0].charAt(0);
            String st2 = "" + arr[1].charAt(0);
            returnValue = st1 + st2;


        }
        //must be 3 or more words
        //unusual exceptions

        //lose and lower case - try only upper case
        ArrayList<String> arLst2 = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            char st1 = arr[i].charAt(0);
            if (Character.isUpperCase(st1)) {
                arLst2.add("" + st1);
            }

        }

        if (arLst2.size() == 2) {
            //   return ""+arr[0].charAt(0)+arr[1].charAt(0);
            return "" + arLst2.get(0).charAt(0) + arLst2.get(1).charAt(0);

        }
        if (arLst2.size() > 2) {
            String retnnValue = "";
            for (int i = 0; i < arLst2.size(); i++) {
                char st1 = arLst2.get(i).charAt(0);
                if (Character.isUpperCase(st1)) {
                    retnnValue += arLst2.get(i);
                }
                if (retnnValue.length() == 2)
                    break;
            }
            return retnnValue;

        }


        String[] regx = new String[0];
//        if (text.contains(" ")) {
//            regx = text.split(" ");
//        } else if (text.contains("-")) {
//            regx = text.split("-");
//        }
        {
            ArrayList<String> arListRegx = new ArrayList<>();
            arListRegx.add("and");
            arListRegx.add("or");
            arListRegx.add("of");
            arListRegx.add("is");
            arListRegx.add("the");
            arListRegx.add("with");
            arListRegx.add("it");
            arListRegx.add("-");
            arListRegx.add(" ");

            String ttLower = text.toLowerCase();
            for (int i = 0; i < arListRegx.size(); i++) {
                if (ttLower.contains(arListRegx.get(i))) {
                    regx = ttLower.split(arListRegx.get(i));
                    break;
                }
            }


        }



       /* String aa[]=new String[arList.size()];
        ArrayList<String> arrayList=new ArrayList<>();
        for (int i = 0; i <arList.size() ; i++) {
             aa[i]= String.valueOf(arList.get(i).split(String.valueOf(regx)));

        }*/

        if (regx.length == 2) {
            return "" + regx[0].trim().charAt(0) + regx[1].trim().charAt(0);
        } else if (regx.length >= 3) {
            return "" + regx[0].trim().charAt(0) + regx[1].trim().charAt(0);
        }

        return returnValue;
    }

    /// From a RecallItem object work out message to show to user
    ///
    /// - Parameters:
    ///   - ri:RecallItem
    ///
    /// - Returns: String ready to display to user depending on whether it is overdue or not (using calcNowState)
    ///

     public static String assignStateLabel(RecallItem ri){

         String returnValue = "";

         Date nowTime = new Date();


         long diffInMs = ri.nextEventTime.getTime() - nowTime.getTime();
         long secondsDue = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

         String s = ri.currentStopTitle();
         Date d1 = ri.nextEventTime;
         Date d2 = nowTime;

         RECALLITEM_TIMINGS_STATE_ENUM state = LibraryJava.calcNowState(ri.currentStopTitle(),ri.nextEventTime,nowTime);






         long learnTime = ri.learnTime.getTime();
         long now = nowTime.getTime();


         if ( DateUtils.isToday(ri.learnTime.getTime()) && DateUtils.isToday(ri.nextEventTime.getTime()) ){

             //TODO: DO I need this check?
             //if (state == RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY || state == RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_EARLY ){
                 returnValue = secondsToString((int)secondsDue);
             //}else{

//             }

         }else{ //learnt was on previous day







             if (state == RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_IN_WINDOW_EARLY || state == RECALLITEM_TIMINGS_STATE_ENUM.RECALLITEM_TIMINGS_STATE_EARLY ){
                 if (DateUtils.isToday(ri.nextEventTime.getTime() )){
                     //started today - so minutes/hours

                     //not so clear
                     //case: learnt yesterday but say 8 hours slot  did not get done so have to do it today

                     if (isDateIntervalLessThan1Day(ri.currentStopTitle())){
                        returnValue = secondsToString((int)secondsDue);
                     }else{ returnValue = "due today";
                     }

                 }else{
                     returnValue = secondsToString((int)secondsDue);
                 }



             }else{//BUS_TIMINGS_STATE_OVERDUE or BUS_TIMINGS_STATE_IN_WINDOW_LATE
                 returnValue = secondsToString((int)secondsDue);
             }
         }



        return returnValue;
    }





    /// calc how many days between 2 dates
    ///
    ///Deos not need seperate testing as others call this help
    ///
    /// - Parameters:
    ///   - fromDate:Date()
    ///   - andDate:Date()
    ///
    /// - Returns: Int: days diff
    ///
    public static long daysBetweenYear(Date time, Date futureDate, String whichTime) {
        Calendar calendarNow2 = Calendar.getInstance();
        calendarNow2.setTime(time);
        Calendar calendardtIner2 = Calendar.getInstance();
        calendardtIner2.setTime(futureDate);
        long diffYear = 0;
        if (whichTime.equals("YEAR"))
            diffYear = calendardtIner2.get(Calendar.YEAR) - calendarNow2.get(Calendar.YEAR);
        else if (whichTime.equals("MONTH"))
            diffYear = calendardtIner2.get(Calendar.MONTH) - calendarNow2.get(Calendar.MONTH);
        else if (whichTime.equals("DAY")) {
            long aa = calendardtIner2.getTimeInMillis();
            long bb = calendarNow2.getTimeInMillis();
            long cc = Math.abs(aa - bb);
            diffYear = TimeUnit.MILLISECONDS.toDays(cc);
        }
        return diffYear;

    }



    private static long minuteBetweenDate(long time, long futureDate) {
        //long duration  = futureDate.getTime() - time.getTime();
        long duration = futureDate - time;

//        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        return TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    private static long daysBetweenDate(long time, long futureDate) {
        //long duration  = futureDate.getTime() - time.getTime();
        long duration = futureDate - time;

//        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
        return TimeUnit.MILLISECONDS.toDays(duration);
    }

    // multiplier =2,5
    //intervalType=m,h
    /// examine 'timing' string and return seperate tokens
    ///
    /// - Parameters:
    ///   - text:string with 2 components e.g. "2D","7H","14D"
    ///
    /// - Returns: 2 seperate components e.g. "(2,D)","(7,H)","(14,D)"
    ///
    public static Pair timingToComponents(String txt) {

        Pair<Integer, String> returnValue =  new Pair<>(1, "H");

        if (txt.length() == 0)
            return returnValue;
        try {
            String number = "";
            String letter = "";
            for (int i = 0; i < txt.length(); i++) {
                char a = txt.charAt(i);
                if (Character.isDigit(a)) {
                    number = number + a;

                } else {
                    letter = letter + a;

                }
            }
            //System.out.println("Alphas in string:" + letter);
            //System.out.println("Numbers in String:" + number);
            returnValue = new Pair( Integer.parseInt(number), letter);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            //default should not happen
            //return returnValue;
        }

        return returnValue;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    public static boolean isDateTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }
    public static Boolean DatabaseExists(Context context){


        File outDBMainPath = context.getDatabasePath("memorize.db");

        return outDBMainPath.exists();


    }
    public static void DatabaseCopier(Context context){


        String inDBName = "memorize.db";

        String outDBName = "memorize.db";
        String outDBNameSHM = "memorize.db-shm";
        String outDBNameWAL = "memorize.db-wal";

        File outDBMainPath = context.getDatabasePath(outDBName);
        File outDBSHMPath = context.getDatabasePath(outDBNameSHM);
        File outDBMWALPath = context.getDatabasePath(outDBNameWAL);

        try {

            if (outDBMainPath.exists()) {
                Log.d(DatabaseCopier.class.getSimpleName(), "DB Main Exists 1");
                outDBMainPath.delete();
            }

            if (outDBSHMPath.exists()) {
                Log.d(DatabaseCopier.class.getSimpleName(), "DB SHM Exists 1");
                outDBSHMPath.delete();
            }

            if (outDBMWALPath.exists()) {
                Log.d(DatabaseCopier.class.getSimpleName(), "DB WAL Exists 1");
                outDBMWALPath.delete();
            }


            final InputStream inputStream = context.getAssets().open("databases/" + inDBName);
            final OutputStream output = new FileOutputStream(outDBMainPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }


            if (outDBMainPath.exists()) {
                Log.d(DatabaseCopier.class.getSimpleName(), "DB Exists 2");
            }

        } catch (IOException e) {
            Log.d(DatabaseCopier.class.getSimpleName(), "Failed to open file", e);
            e.printStackTrace();
        }



    }


}

