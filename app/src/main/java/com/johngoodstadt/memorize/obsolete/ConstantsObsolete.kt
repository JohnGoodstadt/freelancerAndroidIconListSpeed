package com.johngoodstadt.memorize.obsolete

import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem
import com.johngoodstadt.memorize.utils.Constants
//import com.johngoodstadt.memorize.utils.ITEM_JOURNEY_STATE_ENUM.JOURNEY_STATE_LEARNING_ID
//import com.johngoodstadt.memorize.utils.RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup
//import com.johngoodstadt.memorize.utils.ITEM_JOURNEY_STATE_ENUM.JOURNEY_STATE_TRAVELLING
import com.johngoodstadt.memorize.utils.ConstantsJava.RANDOM_UPPER_LIMIT_INT
import java.util.*
import java.util.concurrent.ThreadLocalRandom


object ConstantsObsolete {


    ///create data and load into Global Area
    object reloadFromLocalYoga {
        init {


            val sitting = RecallGroup("SITTING")
            var sittingItems = arrayListOf<RecallItem>(
                RecallItem(
                    "Gormukhasana",
                    sitting.UID
                )
            )

            Constants.GlobalVariables.groups.add(sitting)
            Constants.GlobalVariables.busesNoOrderFromDB.addAll(sittingItems)

            /////////////////

            var lying = RecallGroup("SUPINE POSES")
            var lyingItems = arrayListOf<RecallItem>(
                RecallItem(
                    "Baradvajasana",
                    lying.UID


                ), RecallItem(
                    "Dandasana",
                    lying.UID
                )
            )
/*


            ConstantsJava.GlobalVariables.groups.add(lying)
            ConstantsJava.GlobalVariables.busesNoOrderFromDB.addAll(lyingItems)

            /////////////////

            var standing = RecallGroup("STANDING")
            ConstantsJava.GlobalVariables.groups.add(standing)

            var standingItems = arrayListOf<RecallItem>(
                RecallItem(

                    JOURNEY_STATE_SETUP_ID,
                    standing.UID,
                    "Garudasana",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_SETUP_ID,
                    standing.UID,
                    "Ardha Chandrasana",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                )

            )
            ConstantsJava.GlobalVariables.busesNoOrderFromDB.addAll(standingItems)
 */
        }
    }


    object reloadFromLocalMusic {
        init {

            val americanSuite = RecallGroup("BACH - American SUITE NO. 99 - Trio")
            var americanPhrases = arrayListOf<RecallItem>(
                RecallItem(
                    "American No. 99 - Minute - A",
                    americanSuite.UID

                )
            )
/*


            val americanSuite = RecallGroup("BACH - American SUITE NO. 99 - Trio")
            var americanPhrases = arrayListOf<RecallItem>(
                RecallItem(
                    JOURNEY_STATE_SETUP_ID,
                    americanSuite.UID,
                    "American No. 99 - Minute - A",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                )
            )

            ConstantsJava.GlobalVariables.groups.add(americanSuite)
            ConstantsJava.GlobalVariables.busesNoOrderFromDB.addAll(americanPhrases)

            /////////////////

            var frenchSuite = RecallGroup("BACH - FRENCH SUITE NO. 1 - MINUET")
            var frenchPhrases = arrayListOf<RecallItem>(
                RecallItem(
                    JOURNEY_STATE_TRAVELLING,
                    frenchSuite.UID,
                    "FRENCH Suite No. 1 - Minute - A",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                )
            )

            ConstantsJava.GlobalVariables.groups.add(frenchSuite)
            ConstantsJava.GlobalVariables.busesNoOrderFromDB.addAll(frenchPhrases)

            /////////////////


            val randomInteger = ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT)


            var englishSuite4MinuetPiece = RecallGroup("BACH - ENGLISH SUITE NO. 4 - MINUET")
            ConstantsJava.GlobalVariables.groups.add(englishSuite4MinuetPiece)

            var englishSuite4MinuetPhrases = arrayListOf<RecallItem>(
                RecallItem(

                    JOURNEY_STATE_SETUP_ID,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - C",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_SETUP_ID,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - D",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_LEARNING_ID,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - E",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_LEARNING_ID,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - F",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_LEARNING_ID,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - G",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_TRAVELLING,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - B",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                ),
                RecallItem(

                    JOURNEY_STATE_TRAVELLING,
                    englishSuite4MinuetPiece.UID,
                    "English Suite No. 4 - Minute - A",
                    "",
                    "",
                    ThreadLocalRandom.current().nextInt(1, RANDOM_UPPER_LIMIT_INT),
                    UUID.randomUUID().toString(),
                    Date(),
                    Date(),
                    Date()
                )
            )
            ConstantsJava.GlobalVariables.busesNoOrderFromDB.addAll(englishSuite4MinuetPhrases)
 */
        }
    }


}

