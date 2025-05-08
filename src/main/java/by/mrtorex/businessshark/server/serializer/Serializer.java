package by.mrtorex.businessshark.server.serializer;

import com.google.gson.Gson;

public class Serializer {
    public static String toJson(Object obj) {
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize object to JSON", e);
        }
    }
}
