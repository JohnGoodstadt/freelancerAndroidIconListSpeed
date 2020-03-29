package com.johngoodstadt.memorize.Libraries;

public class ExtensionSQL {

    public interface  RecallGroupLocalSQL <T> {
        String getPropertyFromLocal(String property, String UID);
        T updatePropertyLocal(String property, T value, String UID);
    }


    public interface  RecallItemLocalSQL <T> {
        String getPropertyFromLocal(String property, String UID);
        T updatePropertyLocal(String property, T value, String UID);
    }


    public interface  UserLocalSQL <T> {
        String getPropertyFromLocal(String property, String UID);
        T updatePropertyLocal(String property, T value, String UID);
    }




}
