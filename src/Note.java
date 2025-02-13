import java.util.Locale;
import java.util.Objects;

public class Note {

    private String post;
    private String id;
    private String name;
    private Double salary;
    private String departmentOrId;
    private boolean isMark = false;

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getSalary() {
        return salary;
    }

    public String getDepartmentOrId() {
        return departmentOrId;
    }

    public Note(String[] fields) {
        post = fields[0];
        id = fields[1];
        name = fields[2];
        salary = Double.valueOf(fields[3]);
        departmentOrId = fields[4];
    }

    @Override
    public String toString() {
        return post + ',' +
                id + ',' +
                name + ',' +
                String.format(Locale.US,"%.2f", salary);
    }

    public String toStringForIncorrect() {
        return post + ',' +
                id + ',' +
                name + ',' +
                String.format(Locale.US,"%.2f", salary) + ',' +
                departmentOrId;
    }

    public Note(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(post, note.post) && Objects.equals(id, note.id) && Objects.equals(name, note.name) && Objects.equals(salary, note.salary) && Objects.equals(departmentOrId, note.departmentOrId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, id, name, salary, departmentOrId);
    }
}
