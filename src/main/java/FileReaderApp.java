import picocli.CommandLine;
import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "FileReader", mixinStandardHelpOptions = true, version = "1.0",
        description = "Reads, filters, and analyzes a text file.")
public class FileReaderApp implements Callable<Integer> {

    @CommandLine.Option(names = {"-i", "--input"}, description = "Input file",
            defaultValue = "files/input.txt")
    private File input;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Config file",
            defaultValue = "files/config.txt")
    private File config;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Output directory",
            defaultValue = "files/output.txt")
    private File outputDir;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FileReaderApp()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {

        if (!input.exists() || !config.exists()) {
            System.out.println("Input or config file does not exist.");
            return 1;
        }

        FileReader.runAnalysis(input.toPath(), config.toPath(), outputDir.toPath());
        return 0;
    }
}
