import java.util.ArrayList;

public class Section {

    private String name;
    private ArrayList<Subsection> subsections;

    public Section(String name) {
        this.name = name.trim();
        this.subsections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Subsection> getSubsections() {
        return subsections;
    }

    public void addSubsection(Subsection subsection) {
        subsections.add(subsection);
    }

    public void printSubsections() {
        for (int i = 0; i < subsections.size(); i++) {
            System.out.println((i + 1) + ". " + subsections.get(i).getName());
        }
    }
    public Subsection findSubsection(String name) {
        for (Subsection sub : subsections) {
            if (sub.getName().equalsIgnoreCase(name)) {
                return sub;
            }
        }
        return null;
    }


}
