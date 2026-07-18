package ca.richasf.loanitemstracker.control;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ca.richasf.loanitemstracker.gson.extras.RuntimeTypeAdapterFactory;
import ca.richasf.loanitemstracker.model.AudioLoanItem;
import ca.richasf.loanitemstracker.model.BookLoanItem;
import ca.richasf.loanitemstracker.model.LoanItem;
import ca.richasf.loanitemstracker.model.VideoLoanItem;

/**
 * Loads and saves loan items.
 */
public class SaveManager {
    private static final Type TYPE = TypeToken.getParameterized(
            List.class,
            LoanItem.class)
            .getType();

    private final String filePath;
    private final Gson gson;

    /**
     * Constructs a new save file manager.
     * @param saveFilePath The path of the save file.
     */
    public SaveManager(String saveFilePath) {
        this.filePath = saveFilePath;
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(LoanItem.class)
                        .registerSubtype(BookLoanItem.class, "Book")
                        .registerSubtype(AudioLoanItem.class, "Audio")
                        .registerSubtype(VideoLoanItem.class, "Video"))
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        out.value(value.toString());
                    }

                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        return LocalDate.parse(in.nextString());
                    }
                })
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                    @Override
                    public void write(JsonWriter out, Duration value) throws IOException {
                        out.value(value.toMillis());
                    }

                    @Override
                    public Duration read(JsonReader in) throws IOException {
                        return Duration.ofMillis(in.nextLong());
                    }
                })
                .create();
    }

    /**
     * Read loans from the save. Empty 
     * 
     * @return The list of loan items. Empty if the file does not exist.
     * @throws JsonIOException     If JSON reading error occurs.
     * @throws JsonSyntaxException If JSON is malformed.
     * @throws IOException If file read failed.
     */
    public List<LoanItem> load() throws JsonIOException, JsonSyntaxException, IOException {
        var path = Path.of(filePath);
        if (!Files.isRegularFile(path)) {
            return new ArrayList<>();
        }

        try (var reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, TYPE);
        }
    }

    /**
     * Write loans to the save file. Creates it if the save file doesn't exist.
     * @param items The items to write to file.
     * @throws IOException If writing loans fails.
     */
    public void save(List<LoanItem> items) throws IOException {
        try (var writer = Files.newBufferedWriter(Path.of(filePath))) {
            writer.write(gson.toJson(items, TYPE));
        }
    }
}
