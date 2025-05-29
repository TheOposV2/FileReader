import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class FileReader {

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public static void runAnalysis(Path inputPath, Path configPath, Path outputPath) {

       File outputFile = new File(outputPath.toUri());
        try(
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
        ) {
            List<String> wordsToFilterList;
            List<String> lines;
            Map<String, WordInfo> wordInformation;
            String mostFrequentWord;
            List<String> filteredWords;
            Set<String> wordsToFilter;
            String[] keys= {"Key", "Count", "Lines"};

            logger.info("Reading config file: {}", configPath);
            wordsToFilterList = Files.readAllLines(configPath);

            wordsToFilter = wordsToFilterList.stream().flatMap(str -> Arrays.stream(str.split("\\s+")))
                    .map(str -> str.replaceAll("^[a-zA-Z-]+$", ""))
                    .map(String::toLowerCase)
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toSet());

            logger.info("Reading input file: {}", inputPath);
            lines = Files.readAllLines(inputPath);

            filteredWords = lines.stream().flatMap(str -> Arrays.stream(str.split("\\s+")))
                    .map(str -> str.replaceAll("^[a-zA-Z-]+$", ""))
                    .map(String::toLowerCase)
                    .filter(str -> !str.isEmpty())
                    .toList();
            logger.debug("Filtered words: {}", filteredWords);

            wordInformation = filteredWords.stream().filter(str -> !wordsToFilter.contains(str))
                    .collect(Collectors.groupingBy(s -> s , Collectors.collectingAndThen(Collectors.counting()
                            , Long::intValue)))
                    .entrySet().stream()
                    .sorted(((s1, s2) -> {
                        int whichIsFirst = s2.getValue().compareTo(s1.getValue());
                        return whichIsFirst != 0 ? whichIsFirst : s1.getKey().compareTo(s2.getKey());
                    }))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            entry ->{
                                WordInfo info = new WordInfo();
                                info.setCount(entry.getValue());
                                return info;
                            },(e1,e2) -> e1,LinkedHashMap::new));

            logger.debug("Word information: {}", wordInformation);

            mostFrequentWord = wordInformation.entrySet().stream().max((e1,e2) ->
                    Integer.compare(e1.getValue().getCount(), e2.getValue().getCount())).map(Map.Entry::getKey)
                    .orElseThrow(() -> new RuntimeException("No words"));

            logger.debug("Most Frequent Word: {}", mostFrequentWord);

            for (int i = 0; i < lines.size(); i++) {
                String[] words = Arrays.stream(lines.get(i).split(" "))
                        .map(str -> str.replaceAll("[^a-zA-Z]", ""))
                        .map(String::toLowerCase)
                        .filter(str -> !str.isEmpty()).toArray(String[]::new);
                for (String word : words) {
                    WordInfo info = wordInformation.get(word);
                    if (info != null) {
                        info.setLines(i + 1);
                    }
                }
            }
            writer.newLine();
            writer.write("Most occurred word is: " + mostFrequentWord
                    + " used : " + wordInformation.get(mostFrequentWord).getCount()  );
            writer.newLine();

            for(Map.Entry<String, WordInfo> entry : wordInformation.entrySet()){
                writer.write(entry.getKey() + ": " + entry.getValue().getCount());
                writer.newLine();
            }
            writer.newLine();
            writer.write("Word line occurrences:");
            writer.newLine();
            for (Map.Entry<String, WordInfo> entry : wordInformation.entrySet()) {
                writer.write(entry.getKey() + ": " + new TreeSet<>(entry.getValue().getLines()));
                writer.newLine();
            }
            logger.info("Ended writing");

            UtilClass.saveToJson(wordInformation, "files\\");
            UtilClass.saveToCSV(wordInformation, "files\\", new String[]{"count", "lines"});
        }catch (IOException e){
            logger.error("File not found", e);
        }

    }



}
