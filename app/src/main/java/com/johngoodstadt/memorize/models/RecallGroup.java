package com.johngoodstadt.memorize.models;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.johngoodstadt.memorize.Libraries.ExtensionSQL;
import com.johngoodstadt.memorize.Libraries.LibraryJava;
import com.johngoodstadt.memorize.utils.ConstantsJava;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.johngoodstadt.memorize.models.RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateInDepotSetup;
import static com.johngoodstadt.memorize.models.RecallItem.ITEM_JOURNEY_STATE_ENUM.JourneyStateWaitingForOthers;

@Entity(tableName = "BusDepot")
public class RecallGroup implements ExtensionSQL.RecallItemLocalSQL, Cloneable  {


    public RecallGroup(String title){
        this.title = title;
    }


    //region Properties

    @NonNull public String title;// = "";

    @NonNull  public Integer busDepotID  = 1;
    @NonNull public boolean hidden = false;

    public Date updated_date = new Date();
    public Date inserted_date = new Date();




    @PrimaryKey @NonNull
    public String UID = LibraryJava.makeUID();

    @NonNull public String recordName = "";
    @NonNull public String recordChangeTag = "";

    @Ignore public boolean isLinked = false;
    @Ignore public boolean isGroupMerged = false;
//    @Ignore public Integer stepNumber  = 1;

    //TODO: dont need in android
    @Ignore public boolean isloading = false;

    @Ignore
    public ArrayList<RecallItem> itemList = new ArrayList<RecallItem>();
    @Ignore public ArrayList<RecallItem> otheritemList = new ArrayList<>();
    //endregion

    public enum PIECE_STEP_NUMBER_ENUM {unknown, step_number1, step_number2, step_number3,step_number4}

    @Ignore
    public PIECE_STEP_NUMBER_ENUM stepNumber = PIECE_STEP_NUMBER_ENUM.step_number1;
    @Ignore
    public boolean isMerged = false;

    public void setTitle(String title) {
        this.title = title;
        if(!isloading){
            updatePropertyLocal("title", title, UID);

        }

    }


    //region ivar functions
    //317
    public void addItem(RecallItem ri) {
        itemList.add(ri);
        ri.recallGroup =this;
        ri.recallGroupUID = UID;
    }

    //    MMSE_Business_Android_RecallGroup_Tests
    //334
    public void removeItem(RecallItem ri) {


        for (int i = 0; i< itemList.size(); i++){
            if (itemList.get(i).UID.startsWith(ri.UID)) {
                itemList.remove(i);
            }
        }
        ri.recallGroupUID = ConstantsJava.DEFAULT_GROUP_UID;
        ri.recallGroup = null;
    }

    //357
    public void moveItem(RecallItem ri) {

        RecallGroup rg = ri.recallGroup;
        if (UID.equals(rg.UID)) {
            return;
        }

        RecallGroup fromGroup = ri.recallGroup;

        for (int i=0;i<fromGroup.itemList.size();i++){
            if(fromGroup.itemList.get(i).UID.equals(ri.UID)){
                fromGroup.itemList.remove(i);
                break; //found
            }
        }

        addItem(ri);
        ri.recallGroup = this;
    }
    public RecallItem getItem(String UID) {

        for (RecallItem ri : this.itemList) {
            if (ri.UID.equals(UID)) {
                return ri;
            }
        }
//
//        for (int i=0;i < this.itemList.size();i++){
//
//            if(this.itemList.get(i).UID.equals(UID)){
//                return this.itemList.get(i);
//            }
//        }
        return null; //should not come here
    }
    public RecallItem getItem(RecallItem ri_) {

       for ( RecallItem ri : this.itemList){
           if(ri == ri_){
               return ri;
           }
       }

        return null; //should not come here
    }
    //389
    public ArrayList<RecallItem> removeAllExceptFirst() {
        ArrayList<RecallItem> list = itemList;
        ArrayList<RecallItem> otherPhrases = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).linkedPhrases.length() == 0) {
                otherPhrases.add(itemList.get(i));
            }
        }
        itemList.subList(0, itemList.size()).clear();
        return otherPhrases;
    }

    //endregion


    @Override
    public String getPropertyFromLocal(String property, String UID) {
        return null;
    }

    @Override
    public Object updatePropertyLocal(String property, Object value, String UID) {
        return null;
    }


    //region STEPS 1 to 4
    public boolean goToStep1() {
        boolean returnValue = false;

        switch (stepNumber) { //where we have come from
            case step_number2:

                if (false){
                    RecallGroup.getRowsFromDBRoom(this);
                    return returnValue;
                }


                if (true && canIUnmergeAllPhrases()) {//guarenteed 1 item - if NOT isGroupMerged then nothing to do


                    //2A get details
                    //var APhraseBus:Bus? = nil
                    RecallItem mergedItem = itemList.get(0);

                    if (mergedItem != null) {
                        mergedItem.moveLearn();
                        if (otheritemList.isEmpty() == false){
                            mergedItem.linkedPhrases = "";

                            isMerged = false;
                            //TODO what is extraInfo for?
                            if ( itemList.get(0).extraInfo.isEmpty() == false){
                                mergedItem.title = itemList.get(0).extraInfo;
                                mergedItem.extraInfo = "";
                            }

                            for (RecallItem ri : otheritemList) {
//                                ri.recallGroup = this;
                                ri.moveLearn();
//                                item.customTitle = "";
                                if (ri.linkedPhrases.isEmpty() == false){
                                    ri.linkedPhrases = "";
                                    if (ri.extraInfo.isEmpty() == false && ri.title != ri.extraInfo){
                                        ri.title = ri.extraInfo;//old name before merged title added
                                    }
                                }
                                this.addItem(ri);
                            }

                            //itemList.addAll(otheritemList);
                            otheritemList.clear();
                        }else{
                            //TODO: for now assign to otherList when going from step 1 to step 2
                            // have not got in memory list so go to Local DB
                            //READ IN HERE
                            //let array = loadRowsForDepotUIDFromLocal
                           // RecallGroup.getRowsFromDBRoom(this);




//
//                             val list = RecallItem.readRecallItemsFromLocal(byGroupUID: self.UID)
//
//                             if(list.count > 0) {
//
//                                 for item in list {
//
//                                     item.moveLearn()
//
//                                     if item.linkedPhrases.isEmpty == false {
//                                         item.linkedPhrases = ""
//                                         if item.extraInfo.isEmpty == false && item.title != item.extraInfo {
//                                             item.title = item.extraInfo
//                                             item.extraInfo = ""
//                                         }
//                                     }
//                                 }
//                                 itemList = list
//
//
//                                 isMerged = false
//
//                             }
//
                        }



                    }
                    stepNumber = PIECE_STEP_NUMBER_ENUM.step_number1;
                    returnValue = true;
                }
                break;
            case step_number3:
                for (RecallItem item : itemList) {
                    item.linkedPhrases = "";
                    item.moveLearn();
                }
                stepNumber = PIECE_STEP_NUMBER_ENUM.step_number1;
                returnValue = true;
                break;
            default:
                break;

        }
        return returnValue;
    }
    public boolean goToStep2() {
        boolean returnValue = false;
        switch (stepNumber) {
            case step_number1:
                if (canIMergeAllPhrases()) {
                    mergeAllPhrases();
                    stepNumber = PIECE_STEP_NUMBER_ENUM.step_number2;
                    returnValue = true;
                }
                break;
            case step_number3:
                if (canIMergeAllPhrases()) {
                    mergeAllPhrases();
                    returnValue = true;
                }
            default:
                break;
        }
        return returnValue;

    }

    public boolean goToStep3(ArrayList<RecallItem> checked, ArrayList<RecallItem> unchecked) {
        boolean returnValue = false;
        switch (stepNumber) {
            case step_number2:
                if (canIGo2Step3()) {
                    for (RecallItem ri : unchecked) {
                        ri.journeyState = JourneyStateWaitingForOthers;
                        ri.moveEnd();
                        ri.linkedPhrases = "";
                    }
                    for (RecallItem ri : checked) {
                        ri.LEARN();
                        ri.linkedPhrases = "";

                    }
                    isMerged = false;
                    stepNumber = PIECE_STEP_NUMBER_ENUM.step_number3;
                    returnValue = true;
                }
                break;
            case step_number3:
                if (canIGo2Step3()) {
                    for (RecallItem ri : unchecked) {
                        ri.journeyState = JourneyStateWaitingForOthers;
                        ri.moveEnd();
                        ri.linkedPhrases = "";
                    }
                    for (RecallItem ri : checked) {
                        ri.LEARN();
                        ri.linkedPhrases = "";

                    }
                    isMerged = false;
                    stepNumber = PIECE_STEP_NUMBER_ENUM.step_number3;
                    returnValue = true;
                }
                break;
            default:
                break;
        }


        return returnValue;
    }
    public boolean canIGo2Step3() {
        boolean returnValue = false;

        if (stepNumber.equals(PIECE_STEP_NUMBER_ENUM.step_number2) || stepNumber.equals(PIECE_STEP_NUMBER_ENUM.step_number3)) {
            returnValue = true;
        }
        return returnValue;
    }
    //MusicPieceViewController.swift

    /// canIMergeAllPhrases - test if all items in the list are in a state to allow collapsing into 1
    ///
    /// - Parameters:
    ///   - list:array of RecallItems
    ///
    /// - Returns: Bool - Are all phrases in the correct state to allow merging to 1
    ///
    public boolean canIGotoStep2() {
        boolean returnValue = false;

        //1. I have a list of buses - one of which is an 'A' phrase
        //2. All are later then Learning
        for (RecallItem item : itemList) {
            if (item.journeyState == JourneyStateInDepotSetup) {
                return returnValue;
            }
        }

        //for here all past learning
        for (RecallItem ri : itemList) {
           if (LibraryJava.isMusicPhraseFormattedCorrectly(ri.title)){
               String letter = LibraryJava.getSuffixLetter(ri.title);
               if (letter.equals("A")){
                   returnValue = true;
                   break;
               }
           }else{
               Log.w("error","invalid title " + ri.title);
           }
        }


        return returnValue;
    }
    private boolean canIMergeAllPhrases() {

        boolean returnValue = false;
        for (RecallItem item : itemList) {
            if (item.journeyState == JourneyStateInDepotSetup) {
                return returnValue;
            }
        }

        for (RecallItem item : itemList) {
            if (LibraryJava.isMusicPhraseFormattedCorrectly(item.title)) {
                String letter = LibraryJava.getSuffixLetter(item.title);
                if (letter.equals("A")) {
//                    if (item.canILearn()) {
////                        print("I Can Learn - To Do")
//                    }
                    returnValue = true;
                    break;
                }
            }
        }
        return returnValue;
    }

    public boolean canIUnmergeAllPhrases() {
        boolean returnValue = false;
        if (isMerged && itemList.size() == 1) {
            RecallItem onlyItem = itemList.get(0);
            if (onlyItem.linkedPhrases.isEmpty() == false) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    public void mergeAllPhrases() {
        if (canIMergeAllPhrases()) {

            ArrayList<String> AllBuseUIDs = new ArrayList<>();


            for (RecallItem ri : itemList) {
                if (LibraryJava.isMusicPhraseFormattedCorrectly(ri.title)) {
                    AllBuseUIDs.add(ri.UID);
                }
            }

            RecallItem riA = null;
            //now we have all UIDs - find 'A'
            //set 'A'
            //Remove Others
            for (RecallItem ri : itemList) {
                if (LibraryJava.isMusicPhraseFormattedCorrectly(ri.title)) {
                    String letter = LibraryJava.getSuffixLetter(ri.title);
                    if (letter.equals("A")) {
                        riA = ri; //for out of loop
                        ri.isCombined = true;
                        ri.moveLearn();
                        //2B. Save 'A' with details
                        for (String s : AllBuseUIDs) {
                            if (ri.linkedPhrases.length() == 0) {
                                ri.linkedPhrases = s;
                            } else {
                                ri.linkedPhrases = ri.linkedPhrases + "," + s;
                            }
                        }

                        isMerged = true;

                        stepNumber = PIECE_STEP_NUMBER_ENUM.step_number2;


                        for (RecallItem item : itemList) {
                            if (item.linkedPhrases.length() == 0) {
                                otheritemList.add(item);
                            }
                        }

                        //itemList.add(ri);
                        break;

                    }
                }
            }


            //TODO: Is this the best way?
            itemList = new ArrayList<RecallItem>();
            itemList.add(riA);





        }// cannot merge - e.g. invalid title format (User has custom titles)
    }
    public void markCurrentStep() {
        stepNumber = PIECE_STEP_NUMBER_ENUM.step_number1;
        isMerged = false;


        if (itemList.isEmpty()) {
            return;
        }

        if (itemList.size() == 1 && (!itemList.get(0).linkedPhrases.isEmpty())) {
            stepNumber = PIECE_STEP_NUMBER_ENUM.step_number2;
            isMerged = true;
            return;
        }

        if (itemList.size() > 1) {
            for (RecallItem ri : itemList) {

                if (ri.journeyState == JourneyStateWaitingForOthers || (!ri.linkedPhrases.isEmpty() )) {
                    stepNumber = PIECE_STEP_NUMBER_ENUM.step_number3;
                    return;
                }
            }
        }
    }
    //endregion


    //region kotlin functions
    public static List<RecallItem> removeFromAllBusesAllCombinedPhrases(List<RecallItem> recallItemsNoOrder){

        return ExtensionRecallGroup.Companion.removeFromAllBusesAllCombinedPhrases(recallItemsNoOrder);

    }
    public static List<RecallItem> moveToOtherListAllBusesAllCombinedPhrases(List<RecallItem> recallItemsNoOrder){

        return ExtensionRecallGroup.Companion.moveToOtherListAllBusesAllCombinedPhrases(recallItemsNoOrder);

    }
    public static void getRowsFromDBRoom(RecallGroup rg){


        ExtensionRecallGroup.Companion.getRowsFromDBRoom(rg.UID);

    }
    //endregion

}
