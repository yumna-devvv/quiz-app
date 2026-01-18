import java.util.Scanner;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class QuizApp{
   private static Scanner input= new Scanner(System.in);
   public static ArrayList<Section> sections = new ArrayList<>();

   public static void main(String[] args){

    loadData();
    System.out.println("Loaded sections: " + sections.size());

 
    boolean running= true;
    while(running){
       System.out.println("\n ===== Quiz App Menu ====");
       System.out.println("1. Add Section" );
       System.out.println("2. Add subsection" );
       System.out.println("3. Add question (custom or AI generated) " );
       System.out.println("4. Delete ");
       System.out.println("5. Take Test" );
       System.out.println("6. Exit" );
       System.out.println("Choose an option " );
       int choice= input.nextInt();
       input.nextLine(); 


       switch(choice){
        case 1:
            addSection();
            break;
        case 2:
            addSubsection();
            break;
        case 3:
            addQuestion();
            break;
        case 4:
            delete();
            break;
        case 5:
            takeTest();
            break;
        case 6:
            saveData();
            running= false;
            System.out.println(" Exiting");
            break;
        default:
            System.out.println("Invalid choice");
       }
     }
   }
  
   public static void addSection(){
        System.out.println("Enter the Section name:");
        String name=input.nextLine();
        sections.add(new Section(name));
        System.out.println("Section added");

        FileManager.save("Section| "+ name);
   }

   public static void addSubsection(){
        if (sections.size()==0){
            System.out.println("No sections available. Please add a section first.");
            return;
        }
        System.out.println("Choose the section: (by number) ");
        if (sections.size()>=1){
            for (int i=0; i<sections.size(); i++){
                System.out.println( (i+1)+ "."+ sections.get(i).getName());
            }
        }
        int sectionNumber=input.nextInt();
        input.nextLine(); 
        int index = sectionNumber - 1;
        if (index < 0 || index >= sections.size()) {
            System.out.println("Invalid section number.");
            return;
        }

        Section selectedSection = sections.get(index);
        System.out.println("Enter the subSection name:");
        String SubSectionName= input.nextLine();

        Subsection subsection = new Subsection(SubSectionName);
        selectedSection.addSubsection(subsection);

        System.out.println("Subsection added successfully!");
        FileManager.save("Subsection| "+ SubSectionName);
        
   }

    public static void addSubsection(Section section, String subName) {
        if (section != null) {
            section.addSubsection(new Subsection(subName));
            System.out.println("Subsection added: " + subName);
        }
    }

    public static void addQuestion() {

    // 1. Check if sections exist
    if (sections.size() == 0) {
        System.out.println("No sections available. Please add a section first.");
        return;
    }

    // 2. Choose section
    System.out.println("Choose the section (by number):");
    for (int i = 0; i < sections.size(); i++) {
        System.out.println((i + 1) + ". " + sections.get(i).getName());
    }

    int sectionNumber = input.nextInt();
    input.nextLine(); // consume newline

    int sectionIndex = sectionNumber - 1;
    if (sectionIndex < 0 || sectionIndex >= sections.size()) {
        System.out.println("Invalid section number.");
        return;
    }

    Section selectedSection = sections.get(sectionIndex);

    // 3. Choose subsection
    ArrayList<Subsection> subsections = selectedSection.getSubsections();

    if (subsections.size() == 0) {
        System.out.println("No subsections available. Please add a subsection first.");
        return;
    }

    System.out.println("Choose a subsection:");
    for (int i = 0; i < subsections.size(); i++) {
        System.out.println((i + 1) + ". " + subsections.get(i).getName());
    }

    int subsectionChoice = input.nextInt();
    input.nextLine(); 

    int subsectionIndex = subsectionChoice - 1;
    if (subsectionIndex < 0 || subsectionIndex >= subsections.size()) {
        System.out.println("Invalid subsection choice.");
        return;
    }

    Subsection selectedSubsection = subsections.get(subsectionIndex);


    // 4. Add questions
    System.out.println("Do you want to (1) Add your own question or (2) Get an AI-generated question?");
    int choice = input.nextInt();
    input.nextLine();
    

    if (choice == 1){
        boolean addMoreQuestions = true;

        while (addMoreQuestions) {

            System.out.println("Enter the question:");
            String questionText = input.nextLine();

            String[] options = new String[4];
            for (int i = 0; i < 4; i++) {
                System.out.println("Enter option " + (i + 1) + ":");
                options[i] = input.nextLine();
            }

            int correctIndex = -1;

            while (true) {
                try {
                    System.out.print("Enter correct option (1-4): ");
                    int correctChoice = input.nextInt();
                    input.nextLine(); 

                    if (correctChoice < 1 || correctChoice > 4) {
                        System.out.println("Please enter a number between 1 and 4.");
                        continue;
                    }

                    correctIndex = correctChoice - 1;
                    break;

                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter a number.");
                    input.nextLine();
                }
            }

            Question question = new Question(questionText, options, correctIndex);
            selectedSubsection.addQuestion(question);

            FileManager.save("Question| "+ questionText);
            for (String opt: options){
                FileManager.save("OPTION| "+ opt);
            }
            FileManager.save("ANSWER| "+ correctIndex);


            System.out.println("Question added successfully!");

            System.out.println("Add another question? (yes/no)");
            String answer = input.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                addMoreQuestions = false;
            }
        }}
        else if(choice == 2){
           // cal the AI API to generate the questions 
           String aiQuestions = fetchAIQuestion();
           if(aiQuestions != null){
            System.out.println("Ai generated questions: "+ aiQuestions);
           }
           else {
            System.out.println("Failed to fetch ai generated quetions");
           }

        }
        else {
            System.out.println("Invalid choice.");
        }
   }
   
   public static String fetchAIQuestion(){
    System.out.print("Enter a topic for AI-generated question(s): ");
    String topic = input.nextLine();
    try{
        URL url = new URL("https://api.openai.com/v1/chat/completions");

        HttpURLConnection conn =(HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer YOUR_OPENAI_API_KEY_HERE");
        conn.setDoOutput(true);

        String jsonInputString = String.format(
    """
    {
      "model": "gpt-3.5-turbo",
      "messages": [
        {
          "role": "system",
          "content": "You are a helpful quiz question generator. Return your output strictly in JSON format: { 'question': '...', 'options': ['...','...','...','...'], 'answerIndex': 0 }"
        },
        {
          "role": "user",
          "content": "Generate 1 multiple-choice question on %s"
        }
      ]
    }
    """,
    topic
);


        try(OutputStream os =conn.getOutputStream()){
            byte[] input=jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode=conn.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){

        BufferedReader br= new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;

        while((responseLine = br.readLine()) != null){
            response.append(responseLine.trim());

        }
        return extractQuestionFromResponse(response.toString());
    } else {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
        StringBuilder errorResponse = new StringBuilder();
        String errorLine;

        while((errorLine = br.readLine())!= null){
            errorResponse.append(errorLine.trim());
        }
        System.out.println("Error Response: "+ errorResponse.toString());
        return null;
    }

    } catch (Exception e){
        e.printStackTrace();
        return null;
    }

   }


   private static String extractQuestionFromResponse(String jsonResponse) {   
       int startIndex = jsonResponse.indexOf("\"content\":\"") + 12; 
       int endIndex = jsonResponse.indexOf("\"}", startIndex);    
       return jsonResponse.substring(startIndex, endIndex);
    }



    public static void addQuestion(Subsection subsection, String questionText, String[] options, int correctIndex) {
        if (subsection != null) {
            subsection.addQuestion(new Question(questionText, options, correctIndex));
            System.out.println("Question added: " + questionText);
        }
    }

    public static void delete(){
        System.out.println("What do you want to delete? ");
        System.out.println("1. Section ");
        System.out.println("2. Subsection ");
        System.out.println("3. question ");

        int NumToDelete = input.nextInt();
        input.nextLine();

        if (NumToDelete == 1){
            if (sections.isEmpty()) {
                System.out.println("No sections available.");
                return;
            }
            System.out.println("Choose section to delete: ");
            for(int i=0; i<sections.size(); i++){
                System.out.println((i+1) + sections.get(i).getName());
            }
            int index= input.nextInt() -1;
            input.nextLine();

            if (index < 0 || index >= sections.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            sections.remove(index);
        }

        else if (NumToDelete == 2){
            System.out.println("Choose section: ");
            for(int i=0; i<sections.size(); i++){
                System.out.println((i+1) + sections.get(i).getName());
            }
            int sectionNumber= input.nextInt();
            int sectionIndex = sectionNumber - 1;
            if (sectionIndex < 0 || sectionIndex >= sections.size()) {
                System.out.println("Invalid section number.");
                return;
            }

            Section selectedSection = sections.get(sectionIndex);
            ArrayList<Subsection> subsections = selectedSection.getSubsections();
            if (subsections.size() == 0) {
                System.out.println("No subsections available.");
                return;
            }

            System.out.println("Choose a subsection:");
            for (int i = 0; i < subsections.size(); i++) {
                 System.out.println((i + 1) + ". " + subsections.get(i).getName());
            }

            int subsectionChoice = input.nextInt();
            input.nextLine();

            int subsectionIndex = subsectionChoice - 1;
            if (subsectionIndex < 0 || subsectionIndex >= subsections.size()) {
                System.out.println("Invalid subsection choice.");
                return;
            }
            subsections.remove(subsectionIndex);
        }

        else if (NumToDelete==3){
            System.out.println("Choose section: ");
            for(int i=0; i<sections.size(); i++){
                System.out.println((i+1) + sections.get(i).getName());
            }
            int sectionNumber= input.nextInt();
            int sectionIndex = sectionNumber - 1;
            if (sectionIndex < 0 || sectionIndex >= sections.size()) {
                System.out.println("Invalid section number.");
                return;
            }

            Section selectedSection = sections.get(sectionIndex);
            ArrayList<Subsection> subsections = selectedSection.getSubsections();
            if (subsections.size() == 0) {
                System.out.println("No subsections available.");
                return;
            }

            System.out.println("Choose a subsection:");
            for (int i = 0; i < subsections.size(); i++) {
                 System.out.println((i + 1) + ". " + subsections.get(i).getName());
            }

            int subsectionChoice = input.nextInt();
            input.nextLine();

            int subsectionIndex = subsectionChoice - 1;
            if (subsectionIndex < 0 || subsectionIndex >= subsections.size()) {
                System.out.println("Invalid subsection choice.");
                return;
            }

            Subsection selectedSubsection = subsections.get(subsectionIndex);
            ArrayList<Question> questions=selectedSubsection.getQuestions();
            if(!selectedSubsection.hasQuestions()){
                System.out.println("No questions available in this subsection");
                return;
            }

            System.out.println("Choose a Question to delete: ");
            int i=1;
            for (Question q : questions){
                System.out.println(i + ". "+ q.getQuestionText());
                i++;
            }
            int questionChoice= input.nextInt();
            input.nextLine();

            int questionIndex= questionChoice - 1;

            questions.remove(questionIndex);
        }

        else { System.out.println("Invalid option.");
            return;
        }
         FileManager.clear();
        for (Section s : sections) {
            FileManager.save("SECTION|" + s.getName());
            for (Subsection sub : s.getSubsections()) {
                FileManager.save("SUBSECTION|" + sub.getName());
                for (Question q : sub.getQuestions()) {
                    FileManager.saveQuestion(s.getName(), sub.getName(), q);
                } } }
        System.out.println("Deleted successfully.");
    }

  public static void takeTest(){
    // 1. Check if sections exist
    if (sections.size() == 0) {
        System.out.println("No sections available. Please add a section first.");
        return;
    }

    // 2. Choose section
    System.out.println("Choose the section (by number):");
    for (int i = 0; i < sections.size(); i++) {
        System.out.println((i + 1) + ". " + sections.get(i).getName());
    }

    int sectionNumber = input.nextInt();
    input.nextLine(); // consume newline

    int sectionIndex = sectionNumber - 1;
    if (sectionIndex < 0 || sectionIndex >= sections.size()) {
        System.out.println("Invalid section number.");
        return;
    }

    Section selectedSection = sections.get(sectionIndex);

    // 3. Choose subsection
    ArrayList<Subsection> subsections = selectedSection.getSubsections();

    if (subsections.size() == 0) {
        System.out.println("No subsections available. Please add a subsection first.");
        return;
    }

    System.out.println("Choose a subsection:");
    for (int i = 0; i < subsections.size(); i++) {
        System.out.println((i + 1) + ". " + subsections.get(i).getName());
    }

    int subsectionChoice = input.nextInt();
    input.nextLine(); 

    int subsectionIndex = subsectionChoice - 1;
    if (subsectionIndex < 0 || subsectionIndex >= subsections.size()) {
        System.out.println("Invalid subsection choice.");
        return;
    }
    Subsection selectedSubsection = subsections.get(subsectionIndex);
    ArrayList<Question> questions=selectedSubsection.getQuestions();
    if(!selectedSubsection.hasQuestions()){
        System.out.println("No questions available in this subsection");
        return;
    }
    int score=0;
    for(int i=0; i<questions.size(); i++){
        Question q= questions.get(i);
        System.out.println("\n Question "+ (i+1)+ ":"+ q.getQuestionText());
        String[] options=q.getOptions();
        for(int j=0; j< options.length; j++){
            System.out.println((j+1)+"."+ options[j]);
        }
        int answer= -1;
        while(true){
            try{
                System.out.print("Your answer(1-4): ");
                answer=input.nextInt();
                input.nextLine();

                if( answer<1 || answer> 4){
                    System.out.print("Please enter a number between 1 and 4.");
                    continue;
                }
                break;
            }
            catch(Exception e){
                System.out.print("Invalid input. Enter number between 1 and 4.");
                input.nextLine();
            }
        }
        if (q.isCorrect(answer - 1)) {
            System.out.println("Correct !");
            score++;
        } else {
            System.out.println("Wrong! Correct answer: "+ (q.getCorrectIndex()));
        }
    }
    System.out.println("\nTest completed! Your score: " + score + "/" + questions.size());
   }

  public static void loadData() {
    ArrayList<String> lines = FileManager.load(); // reads lines from your file

    Section currentSection = null;
    Subsection currentSubsection = null;

    String questionText = null;
    String[] options = new String[4];
    int optionIndex = 0;
    int correctIndex = -1;

    for (String line : lines) {
        if (line == null || line.trim().isEmpty()) continue; // skip empty lines
        String[] parts = line.split("\\|", 2); // split into at most 2 parts
        if (parts.length < 2) continue; // skip bad lines

        String type = parts[0].trim();
        String value = parts[1].trim();
        if (value.isEmpty()) continue; // skip if value is empty

        switch (type) {
            case "SECTION":
                currentSection = new Section(value);
                sections.add(currentSection);
                break;
            case "SUBSECTION":
                currentSubsection = new Subsection(value);
                if (currentSection != null) {
                    currentSection.addSubsection(currentSubsection);
                }
                break;
            case "QUESTION":
                questionText = value;
                optionIndex = 0;
                break;
            case "OPTION":
                if (optionIndex < 4) {
                    options[optionIndex++] = value;
                }
                break;
            case "ANSWER":
                try {
                    correctIndex = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    correctIndex = 0;
                }
                if (currentSubsection != null && questionText != null) {
                    Question q = new Question(questionText, options.clone(), correctIndex);
                    currentSubsection.addQuestion(q);
                }
                break;
        }
    }

    for (Section s : QuizApp.getSections()) {
        System.out.println("Section: " + s.getName());
        for (Subsection sub : s.getSubsections()) {
            System.out.println("  Subsection: " + sub.getName());
        }
    }

}

    public static void saveData() {
        FileManager.clear();

        for (Section section : sections) {
            FileManager.save("SECTION|" + section.getName());

            for (Subsection sub : section.getSubsections()) {
                FileManager.save("SUBSECTION|" + sub.getName());

                for (Question q : sub.getQuestions()) {
                    FileManager.save("QUESTION|" + q.getQuestionText());

                    for (String opt : q.getOptions()) {
                        FileManager.save("OPTION|" + opt);
                    }

                    FileManager.save("ANSWER|" + q.getCorrectIndex());
                }
            }
        }
    }


    public static void addSubsection(String sectionName, String subsectionName) {
        sectionName = sectionName.trim();

        for (Section s : sections) {
            if (s.getName().trim().equalsIgnoreCase(sectionName)) {

                Subsection sub = new Subsection(subsectionName);
                s.addSubsection(sub);

                FileManager.save("SUBSECTION|" + subsectionName.trim());
                return;
            }
        }

        System.out.println("DEBUG: Section not found -> [" + sectionName + "]");
    }


    public static void addQuestion(
        String sectionName,
        String subsectionName,
        String questionText,
        String[] options,
        int correctIndex) {

        sectionName = sectionName.trim();
        subsectionName = subsectionName.trim();

        for (Section s : sections) {
            if (s.getName().equals(sectionName)) {
                for (Subsection sub : s.getSubsections()) {
                    if (sub.getName().equals(subsectionName)) {

                        Question q = new Question(questionText, options, correctIndex);
                        sub.addQuestion(q);

                        FileManager.save("QUESTION|" + questionText);
                        for (String opt : options) {
                            FileManager.save("OPTION|" + opt);
                        }
                        FileManager.save("ANSWER|" + correctIndex);

                        return;
                    }
                }
            }
        }
        System.out.println("Section or Subsection not found");
    }

    // INSIDE QuizApp class

    public static ArrayList<Section> getSections() {
        return sections;
    }

    public static Section findSection(String name) {
        for (Section s : sections) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
 }


  
