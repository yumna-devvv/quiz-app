import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static final String FILE_NAME="quiz_data.txt";

    public static void save(String data){
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(FILE_NAME, true))){
            writer.write(data);
            writer.newLine();
        } 
        catch(IOException e) {
            System.out.println("Error saving data");

        }
    }
    // to load the q when the program starts
    public static ArrayList<String> load(){
        ArrayList<String> lines = new ArrayList<>();

        try(BufferedReader reader= new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line= reader.readLine()) != null){
                lines.add(line);
            } 
        } catch (IOException e) {
                // File might not exist yet – ignore
            }
        return lines;
    } 
    public static void clear() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
        // overwrite file (empty)
    } catch (IOException e) {
        System.out.println("Error clearing file");
    }
    }
    public static void saveQuestion(String section, String subsection, Question q) {
        save("SECTION|" + section);       // make sure section is saved
        save("SUBSECTION|" + subsection);
        save("QUESTION|" + q.getQuestionText());

        for (String opt : q.getOptions()) {
            save("OPTION|" + opt);
        }

        save("ANSWER|" + q.getCorrectIndex());
    }




}
