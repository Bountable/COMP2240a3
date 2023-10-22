import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class A3 {

    public static void main(String[] args) {

        if (args.length < 3) { // We now need at least 3 arguments: frame number, quantum size, and at least 1 file name.
            System.out.println("Usage: java A3 <numberOfFrames> <quantumSize> <filename1> [<filename2> ... <filenameN>]");
            return;
        }

        int numFrames = Integer.parseInt(args[0]);
        int quantumTime = Integer.parseInt(args[1]);
        ArrayList<ProcessTask> processTasks = new ArrayList<>();

        for (int i = 2; i < args.length; i++) {
            String fileName = args[i];
            processTasks.add(parseFile(fileName));
        }

        FixedLocalClockScheduler flScheduler = new FixedLocalClockScheduler(numFrames, quantumTime, processTasks);
        flScheduler.execute();

        VariableGlobalClockScheduler vgScheduler = new VariableGlobalClockScheduler(numFrames, quantumTime, processTasks);
        vgScheduler.execute();
    }

    /**
     * Parses a given file to extract process details.
     * 
     * @param filename The name of the file to be parsed.
     * @return A ProcessTask object with details extracted from the file.
     */
    private static ProcessTask parseFile(String filename) {
        ProcessTask processTask = new ProcessTask();
        System.out.println("Processing file: " + filename);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // Split the line on ":" to separate the field and value
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String field = parts[0].trim();
                    String value = parts[1].trim();

                    if (field.equals("name")) {
                        value = value.replace("process", "").replace(";", "").trim();
                        String taskName = "Process" + Integer.parseInt(value);
                        processTask.setTaskId(taskName);
                    }

                    if (field.equals("page")) {
                        value = value.replace(";", "").trim();
                        int pageNumber = Integer.parseInt(value);
                        processTask.addInstruction(pageNumber);
                    }
                }
            }

        } catch (NumberFormatException | IOException e) {
            System.err.println("Error processing file: " + filename);
            e.printStackTrace();
        }

        return processTask;
    }
}
