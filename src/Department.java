import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Department {
    private String name;
    private String id;
    private Note manager;
    private List<Note> notes;

    public Department(Note manager) {
        this.name = manager.getDepartmentOrId();
        this.id = manager.getId();
        this.manager = manager;
        notes = new ArrayList<Note>();
    }

    public final void addNote(Note employee) {
        this.notes.add(employee);
    }

    @Override
    public String toString() {
        return name + '\n' + manager + '\n' +
                notes.stream().map(Note::toString)
                        .collect(Collectors.joining("\n")) + '\n' +
                notes.size() + ',' +
                String.format(Locale.US,"%.2f", countAverageSalary()) + '\n'; //Решить вопрос с 2 знаками поселе запятой!!!!
    }

    private double countAverageSalary() {
        double sum = manager.getSalary();
        for (Note note : notes) {
            sum += note.getSalary();
        }
        return sum / (notes.size() + 1);
    }

    public final String stringForPrintWithSort(String sort, String order) {
        if (sort == null && order == null) {
            return this.toString();
        } else if (sort.equals("name")) {
            sortByAksDesk(order, Comparator.comparing(Note::getName));
        } else {
            sortByAksDesk(order, Comparator.comparing(Note::getSalary));
        }
        return this.toString();

    }

    private void sortByAksDesk(String order, Comparator<Note> comparing) {
        if (order.equals("desk")) {
            notes.sort(comparing.reversed());
        } else {
            notes.sort(comparing);
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
