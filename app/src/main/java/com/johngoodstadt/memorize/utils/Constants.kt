package com.johngoodstadt.memorize.utils

import android.content.res.Resources
import com.johngoodstadt.memorize.Libraries.MyApplication
import com.johngoodstadt.memorize.R
import com.johngoodstadt.memorize.models.RecallGroup
//import com.johngoodstadt.memorize.models.RecallGroup
import com.johngoodstadt.memorize.models.RecallItem

object Constants {



    val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 999


    val SETTINGS_DRAWABLE_ARR: IntArray = intArrayOf(
        R.drawable.ic_avatar,
        R.drawable.ic_time,
        R.drawable.ic_fb,
        R.drawable.ic_youtube,
        R.drawable.ic_calendar,
        R.drawable.ic_report,
        R.drawable.ic_help,
//        R.drawable.ic_hide,
//        R.drawable.ic_upload,
//        R.drawable.ic_instrument,
//        R.drawable.ic_calendar,
        R.drawable.ic_calendar
    )

    val MAIN_TABS = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
    )



    val BROADCAST_RECEIVER_INTENT = "UPDATE_STATE_CHANGED"

    public val ARG_PARAM_UID = "UID"
    val ARG_PARAM_busDepotUID = "busDepotUID"
    val GROUP_NAME_KEY = "group_name_key"
    val POLISH_CHECKED_KEY = "polish_checked_key"
    val POLISH_UNCHECKED_KEY = "polish_unchecked_key"

    val MENU_CROP = "MENU_CROP"

    val ALARM_KEY_ID = "ALARM_KEY_ID"
    val ALARM_KEY_RECALL_ITEM = "ALARM_KEY_RECALL_ITEM"
    val ALARM_KEY_DAILY = "ALARM_KEY_DAILY"
    val NOTIF_KEY_ID = "KEY_ID"
    val ALARM_KEY_NOTIFY = 99999

    val MAIN_TAB_GROUPS = 0
    val MAIN_TAB_TODAY = 1

    object MenuIDs {
        val CROP = 1000
        val TAKE_PHOTO = 1001
        val PHOTOS = 1002
        val GET_IMAGE = 1003
        val REPLACE_IMAGE = 1004
        val SCORE_PAGE = 1005
        val VIEW_SCORE_PAGE = 1006
        val ADD_SCORE_PAGE = 1007
        val REMOVE = 1008
        val SET_UP = 1009
        val VIEW_IMAGE = 1010
        val CHANGE_TITLE = 1011
        val ADD_NEXT_PHRASE = 1012
        val MANAGE_PHOTOS = 1013
        val VIEW_HELP = 1014
        val VIEW_HELP_STEP1 = 1015
        val LEARN_PHRASES = 1016
        val PIECE_PERFORMED = 1017
        val HIDE_PIECE = 1018
        val DELETE_PIECE = 1019
        val DELETE_PHRASE = 1020
        val DELETE_ITEM = 1021
        val LEARN_PIECE = 1022
        val NOTIF_PHRASE = 1023
    }
    object MenuTitles {
        val CROP = "Crop Photo"
        val TAKE_PHOTO = "Take Photo"
        val PHOTOS = "Get Photo"
        val GET_IMAGE = "Get Score Image"
        val REPLACE_IMAGE = "Replace Image"
        val SCORE_PAGE = "Select Score Page"
        val VIEW_SCORE_PAGE = "View Score Page"
        val ADD_SCORE_PAGE = "Add Score Page"
        val REMOVE = "Remove Selected Photo"
        val SET_UP = "Go to setup again"
        val VIEW_IMAGE = "View in full screen"
        val CHANGE_TITLE = MyApplication.getAppContext().resources.getString(R.string.menu_change_title)
        val ADD_NEXT_PHRASE = MyApplication.getAppContext().resources.getString(R.string.menu_add_item)
        val MANAGE_PHOTOS = MyApplication.getAppContext().resources.getString(R.string.menu_manage_photos)
        val VIEW_HELP = MyApplication.getAppContext().resources.getString(R.string.menu_view_help)
        val VIEW_HELP_STEP1 = MyApplication.getAppContext().resources.getString(R.string.menu_view_help_step1)
        val LEARN_PHRASES = MyApplication.getAppContext().resources.getString(R.string.menu_learn_all_phrases)
        val PIECE_PERFORMED = MyApplication.getAppContext().resources.getString(R.string.menu_piece_performed)
        val HIDE_PIECE = MyApplication.getAppContext().resources.getString(R.string.menu_hide_piece)
        val DELETE_PIECE = MyApplication.getAppContext().resources.getString(R.string.menu_delete_piece)
        val DELETE_PHRASE = MyApplication.getAppContext().resources.getString(R.string.menu_delete_phrase)
        val DELETE_ITEM = MyApplication.getAppContext().resources.getString(R.string.menu_delete_item)

        val LEARN_PIECE = MyApplication.getAppContext().resources.getString(R.string.menu_learn_piece)
        val NOTIF_PHRASE = "Notify"


    }
    object RequestCodes {
        val REQUEST_CAMERA = 1000
        val REQUEST_GALLERY = 1001
        val REQUEST_STORAGE_PERMISSION = 1002
        val REQUEST_WRITE_STORAGE_PERMISSION = 1003
        val REQUEST_PIECE_ADDED = 1004
        val REQUEST_ITEM_SETUP = 1005
        val REQUEST_ITEM_LEARN = 1006
        val REQUEST_ITEM_RECALL = 1007
        val REQUEST_PIECE_POLISH = 1008
        val REQUEST_VIEW_IMAGE = 1009
        val REQUEST_GROUP = 1010


    }
    object ViewType {
        val header = 0
        val oneline = 1
        val phrases = 2
        val waiting = 3
        val checkedline = 4
    }

    object RecallItemRefreshObsolete {
        val RI_DATE_LESS_HOUR = 0
        val RI_DATE_GREATER_HOUR = 1
        val RI_DATE_NOT_TODAY = 2

    }

    enum class RecallItemRefresh {
        RI_DATE_LESS_HOUR,RI_DATE_GREATER_HOUR,RI_DATE_NOT_TODAY
    }

//    object JOURNEY_STATE_IDs {
//        val JOURNEY_STATE_SETUP_ID = 0
//        val JOURNEY_STATE_LEARNING_ID = 1
//        val JOURNEY_STATE_AT_STOP = 2
//        val JOURNEY_STATE_TRAVELLING = 3
//        val JOURNEY_STATE_TRAVELLING_OVERDUE = 4
//        val JOURNEY_STATE_ENDED = 5
//        val JOURNEY_STATE_WAITING_FOR_OTHERS = 6
//    }


    object GlobalVariables {


        @JvmField
        var hasImages: Boolean = true
        var groups = arrayListOf<RecallGroup>()
        var busesNoOrderFromDB = arrayListOf<RecallItem>()



    }

    object whichApp {

        enum class target {
            music, yoga, legaldemo, general //add others
        }

        lateinit var isRunning : target


    }

}