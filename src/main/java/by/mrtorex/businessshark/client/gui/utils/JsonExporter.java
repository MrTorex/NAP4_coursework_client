package by.mrtorex.businessshark.client.gui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Утилита для экспорта данных в формате JSON.
 * Позволяет создавать и сохранять JSON-файлы с данными.
 */
public class JsonExporter {

    /**
     * Экспортирует данные в JSON-файл.
     *
     * @param data  список данных для экспорта
     * @param out   выходной поток для записи JSON-файла
     * @param title заголовок для JSON-документа
     * @throws Exception если произошла ошибка при экспорте
     */
    public void export(List<Map<String, Object>> data, OutputStream out, String title) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("title", title);
        wrapper.put("data", data);

        String json = gson.toJson(wrapper);
        out.write(json.getBytes(StandardCharsets.UTF_8));
    }
}
