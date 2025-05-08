package by.mrtorex.businessshark.server.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;

import java.lang.reflect.Type;
import java.util.List;

public class Deserializer {
    public <T> T extractData(String data, Type type) {
        try {
            return new Gson().fromJson(data, type);
        } catch (JsonSyntaxException e) {
            AlertUtil.error("Deserializer error", "Deserialization failed.");
            return null;
        }
    }

    public <T> List<T> extractListData(String data, Class<T> classOfT) {
        Type listType = TypeToken.getParameterized(List.class, classOfT).getType();
        return extractData(data, listType);
    }
}
