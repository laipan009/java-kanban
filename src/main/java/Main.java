import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import manager.impl.HttpTaskManager;
import manager.server.HttpTaskServer;
import manager.server.KVServer;
import manager.server.KVTaskClient;
import task.EpicTask;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        EpicTask epicTask = new EpicTask("Test Epic Task", "This is a test epic task");
        epicTask.setId(1L);

        EpicTask epicTask2 = new EpicTask("T", "Th");
        epicTask.setId(2L);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .serializeNulls().create();

        String s = gson.toJson(epicTask);
        System.out.println(epicTask2);
        epicTask2 = gson.fromJson(s, EpicTask.class);

        System.out.println(epicTask2);






    }
}