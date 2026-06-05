import java.io.IOException;
import java.time.LocalDate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Type adapter for {@link LocalDate}.
 */
public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
    /**
     * Write to JSON.
     * 
     * @param out   Output to write to.
     * @param value The given value.
     * @throws IOException If error occurs during write.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value.toString());
    }

    /**
     * Read from JSON.
     * 
     * @param in Input to read from.
     * @throws IOException If error occurs during read.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return LocalDate.parse(in.nextString());
    }
}