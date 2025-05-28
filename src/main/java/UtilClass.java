import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.slf4j.Logger;
import java.util.stream.Collectors;

public class UtilClass {
    private static final Logger logger = LoggerFactory.getLogger(UtilClass.class);
    public static void saveToJson(Object data , String pathToOutput){
        String path = Paths.get(pathToOutput, "output.json").toString();
        logger.info("Started write to file: {}", path);
    try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jacksonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        writer.write(jacksonData);
        logger.info("Completed");
    }catch (JsonProcessingException e){
        logger.error("Failed to save JSON", e);
    }catch (IOException e){
        logger.error("File not found", e);
    }
    }

    public static <T> void saveToCSV(Map<String, T> data, String pathToOutput, String[] fieldNames) {
        String path = Paths.get(pathToOutput, "output.json").toString();
        logger.info("Started writing CSV to: {}", path);
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            String[] header = new String[fieldNames.length + 1];
            header[0] = "Key";
            System.arraycopy(fieldNames, 0, header, 1, fieldNames.length);
            writer.writeNext(header);

            for (Map.Entry<String, T> entry : data.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(entry.getKey());

                for (String fieldName : fieldNames) {
                    try {
                        Field field = entry.getValue().getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = field.get(entry.getValue());
                        row.add(String.valueOf(value));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        logger.warn("Could not read field {} from {}", fieldName, entry.getValue().getClass().getSimpleName());
                        row.add("N/A");
                    }
                }

                writer.writeNext(row.toArray(new String[0]));
            }

            logger.info("CSV writing completed.");
        } catch (IOException e) {
            logger.error("Failed to write CSV to {}", pathToOutput, e);
        }
    }
}
