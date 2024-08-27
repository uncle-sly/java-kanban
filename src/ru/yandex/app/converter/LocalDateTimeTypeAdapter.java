package ru.yandex.app.converter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        try {
            jsonWriter.value(localDateTime.format(dtf));
        } catch (NullPointerException e) {
            jsonWriter.value(LocalDateTime.MIN.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        final String text = jsonReader.nextString();
        if (text == null) {
            return null;
        }
        return LocalDateTime.parse(text, dtf);
    }
}
