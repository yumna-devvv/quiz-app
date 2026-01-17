import java.util.ArrayList;

public class Subsection {

    private String name;
    private ArrayList<Question> questions;

    public Subsection(String name) {
        this.name = name.trim();
        this.questions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public int getQuestionCount(){
        return questions.size();
    }
    public boolean hasQuestions() {
        return !questions.isEmpty();
    }
}

