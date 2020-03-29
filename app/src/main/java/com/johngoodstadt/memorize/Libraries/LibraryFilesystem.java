package com.johngoodstadt.memorize.Libraries;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.johngoodstadt.memorize.utils.Constants;
import com.johngoodstadt.memorize.utils.ConstantsJava;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.johngoodstadt.memorize.utils.ConstantsJava.LARGE_SUFFIX;
import static com.johngoodstadt.memorize.utils.ConstantsJava.MUSIC_SUFFIX;
import static com.johngoodstadt.memorize.utils.ConstantsJava.PROMPT_SMALL_SUFFIX;
import static com.johngoodstadt.memorize.utils.ConstantsJava.PROMPT_SUFFIX;
import static com.johngoodstadt.memorize.utils.ConstantsJava.SMALL_SUFFIX;

public class LibraryFilesystem extends AppCompatActivity {





    //MARK: - file paths


    public static String getDocumentsDirectoryString() {
        File cacheDirectory = MyApplication.getAppContext().getExternalFilesDir(null);
        if (cacheDirectory == null) {
            return "";
        }

        return cacheDirectory.toString();
    }
    public static Uri getUriFromFilename(String filename) {

        String path = getDocumentsDirectoryString() + "/" + filename;


        File imgFile = new  File(path);
        if(imgFile.exists())
        {
            return Uri.fromFile(imgFile);

        }


        return null;
    }




    /// loop in Documents directory and count how many score images
    ///
    /// - Parameters:
    ///   - UID: unique UID of RecallGroup
    ///   e.g. if only file is depot_9857CA13-8516-454B-947B-C0F5A5F2959A_large_1 then return 1 - 1 page
    ///
    /// - Returns: INt, count of score pages
    public static int getCountOfPhotoScorePages(String UID) {
        ArrayList<String> myData = new ArrayList<String>();
        String path = getDocumentsDirectoryString();
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return 0;
        }
        String startString = ConstantsJava.GROUP_PREFIX + "_" + UID + "_" + LARGE_SUFFIX + "_";
        String endString = ".png";
        String[] files = directory.list();
        if (files == null)
            return 0;
        for (int i = 0; i < files.length; i++) {
            String strChk = files[i];
            if (strChk.startsWith(startString) && strChk.endsWith(endString)) {
                myData.add(files[i]);
            }
        }
        return myData.size();

    }



    public static boolean exists(String name) {
        String path = getDocumentsDirectoryString() + "/" + name;

        File directory = new File(path);
        if (directory.exists()) {
            return true;
        } else {
            return false;
        }

    }



    /// find if UID filename exists in Documents folder
    ///
    /// - Parameters:
    ///   - UID: used to calc full name
    ///
    /// - Returns: Bool - true it exists - false - does not exist

    public static boolean doesLargeImageExist(int UID) {
        return exists(getFileNameLargeFirstBusID(UID));
    }
    public static boolean doesPromptImageExist(int UID) {
        return exists(getFileNamePromptImage(UID));
    }
    public static boolean doesThumbnailImageExist(int UID) {
        return exists(getFileNameThumbnailImage(UID));
    }


    //MARK: - Create files
    /// write image file to Documents folder - use UID to name file
    ///
    /// - Parameters:
    ///   - UID: use to make up filename
    ///   - image: image to write
    ///
    public static void createImageLocalFirstScorePhotoByDepotUID(String UID, Bitmap bitmap){
        String filename = getFileNameFirstScorePhotoByDepotUID(UID);
        writeImageFileToFileSystem(bitmap, filename);
    }


    /// write image file to Documents folder - use UID to name phrase file
    ///
    /// - Parameters:
    ///   - UID: use to make up filename
    ///   - image: image to write
    ///
    public static void createImageMainMusicImage(int UID, Bitmap bitmap) {
        String fileName = getFileNameMainMusicImageByBusID(UID);
        writeImageFileToFileSystem(bitmap, fileName);
    }
    /// get filename of first (1) phrase page
    ///
    /// e.g. UID = '12345' then return 'bus_12345_large_1.png'
    ///
    /// - Parameters:
    ///   - UID: String
    public static String getFileNameMainMusicImageByBusID(int UID) {
        // return "bus_\(UID)_\(Constants.LARGE_SUFFIX)_1.png"
        return "bus_" + UID + "_" + ConstantsJava.LARGE_SUFFIX + "_1.png";
    }



    /// write image file to Documents folder - filename and inage
    ///
    /// - Parameters:
    ///   - filename: of file to write
    ///   - image: image to write
    ///
    public static void writeImageFileToFileSystem(Bitmap btmp, String fName) {



        File fImageThumb = new File(MyApplication.getAppContext().getExternalFilesDir(null), fName);
        try {
            fImageThumb.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            btmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(fImageThumb);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeImageFileToDIRECTORY_DOWNLOADS(Bitmap btmp, String fName) {



        File fImageThumb = new File(MyApplication.getAppContext().getExternalFilesDir(null), fName);
        try {
            fImageThumb.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            btmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(fImageThumb);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// delete filename
    ///
    /// - Parameters:
    ///   - filename: filename to fullRefreshDataAndView from Documents
    ///
    public static boolean removeFile(String fileName) {
        try {
            File dir = MyApplication.getAppContext().getExternalFilesDir(null);
            if (dir != null && dir.isDirectory()) {
                //String[] children = dir.list();
                // for (int i = 0; i < children.length; i++) {
                // boolean success = deleteDir(new File(dir, children[i]));
                boolean success = deleteDir(new File(dir, fileName));
                if (!success) {
                    return false;
                }
                // }
                return dir.delete();
            } else if (dir != null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }


        } catch (Exception e) {
            Log.e("", "" + e);
        }
        return false;

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    //MARK: - filenames
    /// get filename of first (1) score page
    ///
    /// e.g. UID = '12345' then return 'depot_12345_large_1.png'
    ///
    /// - Parameters:
    ///   - UID: from
    public static String getFileNameFirstScorePhotoByDepotUID(String UID) {
        //  return "\(ConstantsJava.GROUP_PREFIX)_\(UID)_\(ConstantsJava.LARGE_SUFFIX)_1.png"
        return ConstantsJava.GROUP_PREFIX + "_" + UID + "_" + LARGE_SUFFIX + "_" + "1.png";


    }


    /// get filename of first (1) phrase page
    ///
    /// e.g. UID = '12345' then return 'bus_12345_large_1.png'
    ///
    /// - Parameters:
    ///   - UID: String
    public static String getFileNameThumbnailImage(int intUID) {

        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
            return getFileNameSmallMusicCueByBusID(intUID);
        }else{
            return getFileNameSmallPromptImage(intUID);
        }
        //return "bus_" + intUID + "_" + LARGE_SUFFIX + "_1.png";
    }
    /// get filename of thumbnail - either for music or non music
    ///
    /// e.g. UID = 12345 then return 'bus_12345_music_cue_small.png'
    ///
    /// - Parameters:
    ///   - intUID: Int
//    public static String getFileNameSmallMusicCueByBusIDDupliate(int intUID) {
//        return "bus_" + intUID + "_" + MUSIC_SUFFIX +"_"+ ConstantsJava.THUMBNAIL_SUFFIX + ".png";
//    }
    /// get filename of thumbnail phrase page
    ///
    /// e.g. UID = 12345 then return 'bus_12345_music_cue_small.png'
    ///
    /// - Parameters:
    ///   - intUID: Int
    public static String getFileNameSmallMusicCueByBusID(int intUID) {
        return "bus_" + intUID + "_" + MUSIC_SUFFIX +"_"+ ConstantsJava.THUMBNAIL_SUFFIX + ".png";
    }
    /// get filename of large prompt phrase page
    ///
    /// e.g. UID = 12345 then return 'bus_12345_music_cue_large.png'
    ///
    /// - Parameters:
    ///   - intUID: Int
    public static String getFileNameLargeMusicCueByBusID(int intUID) {

        return "bus_" + intUID + "_" + MUSIC_SUFFIX +"_"+ LARGE_SUFFIX + ".png";
    }
    /// get filename of first (1) main phrase page
    ///
    /// e.g. UID = 12345 then return 'bus_12345_large_1.png'
    ///
    /// - Parameters:
    ///   - UID: Int
    public static String getFileNameLargeFirstBusID(int UID) {
        return "bus_" + UID + "_large_1.png";
    }



    /// get filename of first (1) main image
    ///
    /// e.g. UID = '12345' then return 'bus_12345_large_1.png'
    ///
    /// - Parameters:
    ///   - UID: String
    public static String getFileNameMainImageByBusID(int UID) {

        return "bus_" + UID + "_" + LARGE_SUFFIX + "_1.png";
    }
    /// write image file to folder - use UID to name main file
    ///
    /// - Parameters:
    ///   - UID: use to make up filename
    ///   - image: image to write
    ///
    public static void createImageMainImage(int UID, Bitmap bitmap) {
        String fileName = getFileNameMainImageByBusID(UID);
        writeImageFileToFileSystem(bitmap, fileName);
    }
    public static void createImageThumbnail(int intUID, Bitmap bitmap) {

        writeImageFileToFileSystem(bitmap, getFileNameSmallPromptImage(intUID));
    }
    /// get filename of prompt image
    ///
    /// e.g. UID = '12345' then return 'bus_12345_prompt.png'
    ///
    /// - Parameters:
    ///   - UID: String
    public static String getFileNamePromptImage(int intUID) {

        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
            return getFileNameLargeMusicCueByBusID(intUID);
        }else{
            return "bus_" + intUID + "_" + PROMPT_SUFFIX + ".png";
        }

        //return "bus_" + intUID + "_" + PROMPT_SUFFIX + ".png";
    }
    /// get filename of prompt image
    ///
    /// e.g. UID = '12345' then return 'bus_12345_prompt.png'
    ///
    /// - Parameters:
    ///   - UID: String

    public static String getFileNameSmallPromptImage(int intUID) {

        if (Constants.whichApp.isRunning == Constants.whichApp.target.music) {
            return getFileNameSmallMusicCueByBusID(intUID);
        }else{
            return "bus_" + intUID + "_" + PROMPT_SMALL_SUFFIX + ".png";
        }

        //return "bus_" + UID + "_" + PROMPT_SUFFIX + "_" + SMALL_SUFFIX + ".png";
        //return "bus_${UID}_${PROMPT_SUFFIX}_${SMALL_SUFFIX}.png";
    }
    /// write image file to folder - use UID to name prompt file
    ///
    /// - Parameters:
    ///   - UID: use to make up filename
    ///   - image: image to write
    ///
    public static void createImagePromptImage(int UID, Bitmap bitmap) {
        String fileName = getFileNamePromptImage(UID);
        writeImageFileToFileSystem(bitmap, fileName);
    }

    public static Uri readMainImage(int UID) {
        String fileName = getFileNameMainImageByBusID(UID);

        String path = getDocumentsDirectoryString() + "/" + fileName;


        File imgFile = new  File(path);
        if(imgFile.exists())
        {
            return Uri.fromFile(imgFile);

        }

        return null;
    }
    public static Uri readPromptImage(int UID) {
        String fileName = getFileNamePromptImage(UID);

        String path = getDocumentsDirectoryString() + "/" + fileName;


        File imgFile = new  File(path);
        if(imgFile.exists())
        {
            return Uri.fromFile(imgFile);

        }

        return null;
    }

    public static Uri getUriFromFirstScorePage(String UID){
        String filename = getFileNameFirstScorePhotoByDepotUID(UID);

        String path = getDocumentsDirectoryString() + "/" + filename;


        File imgFile = new  File(path);
        if(imgFile.exists())
        {
            return Uri.fromFile(imgFile);

        }

        return null;
    }
//    public static Uri getUriFromFilename(String filename) {
//
//        String path = getDocumentsDirectoryString() + "/" + filename;
//
//        File imgFile = new  File(path);
//        if(imgFile.exists())
//        {
//            return Uri.fromFile(imgFile);
//
//        }
//
//        return null;
//    }

}
