import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println(Arrays.stream(args).toList());
        Map<String, String> parameters;
        parameters = validateCommands(args);
        List<String> ids = new ArrayList<>();
        if (!parameters.containsKey("input")) {
            System.out.println("Не указан параметр input!");
            return;
        } else if (!parameters.get("input").endsWith(".txt")) {
            parameters.put("input", parameters.get("input") + ".txt");
            System.out.println("Название файла не заканчиватся на .txt   !!!");
            System.out.println("Я поищу файлы с таким названием и расширением .txt");
        }

        List<String> incorrectLines = new ArrayList<>();
        List<Note> notes = new ArrayList<>();
        List<Department> departments = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(parameters.get("input")));
        } catch (Exception e) {
            System.out.println("Не удается открыть input файл");
            return;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] fields = line.split(",");
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
            }
//            System.out.println(fields[1]);
            if (!isCorrectLine(fields) || ids.contains(fields[1])) {
                System.out.println(fields[1]);
                incorrectLines.add(line);
                continue;
            } else if (fields[0].equals("Manager")) {
                ids.add(fields[1]);
                departments.add(new Department(new Note(fields)));
            } else {
                ids.add(fields[1]);
                notes.add(new Note(fields));
            }
        }
        scanner.close();
        departments.sort(Comparator.comparing(Department::getId));
//        notes.sort(Comparator.comparing(Note::getDepartmentOrId));
        for (Department item : departments) {
            for (Note note : notes) {
                if (note.getDepartmentOrId().equals(item.getId())) {
                    item.addNote(note);
                    note.setMark(true);
                }
            }
        }
        for (Note value : notes) {
//            Note sd = new Note();
            if (!value.isMark()) {
            incorrectLines.add(value.toStringForIncorrect());
            }
        }
        departments.sort(Comparator.comparing(Department::getName));
        if (parameters.get("output").equals("file")) {
            try (
                    FileWriter writer = new FileWriter(parameters.get("path"), false)
            ) {
                for (Department department : departments) {
                    writer.append(department.stringForPrintWithSort(
                                    parameters.get("sort"), parameters.get("order")
                            )
                    );
                }
                writer.append("\nНекорректные данные:");
                for (String line : incorrectLines) {
                    writer.append("\n");
                    writer.append(line);
                }
                writer.flush();
            } catch (IOException ex) {
                System.out.println("Не удается открыть файл для записи");
                System.out.println("Результат выведен в консоль");
                printToConsole(departments, parameters, incorrectLines);
            }
        } else {
            printToConsole(departments, parameters, incorrectLines);
        }
    }

    private static void printToConsole(List<Department> departments, Map<String,
            String> parameters, List<String> incorrectLines) {
        for (Department department : departments) {
            System.out.println(department.stringForPrintWithSort(
                            parameters.get("sort"), parameters.get("order"))
            );
        }
        System.out.println("Некорректные данные:");
        for (String line : incorrectLines) {
            System.out.println(line);
        }
    }

    private static boolean isCorrectLine(String[] fields) {
        if (fields.length != 5) {
            return false;
        }
        for (String field : fields) {
            if (field.isEmpty()) {
                return false;
            }
        }
        try {
            Double.valueOf(fields[3]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (!fields[0].equals("Manager") && !fields[0].equals("Employee")) {
            return false;
        } else if (fields[0].equals("Manager") && fields[4].charAt(0) < 'A' && fields[4].charAt(0) > 'Z') {
            return false;
        } else if (fields[0].equals("Employee") && fields[4].charAt(0) < '0' && fields[4].charAt(0) > '9') {
            return false;
        } else if (fields[3].charAt(0) == '-') {
            return false;
        }
        return true;
    }

    private static Map<String, String> validateCommands(String[] args) {
        Map<String, String> result = new LinkedHashMap<>();
        List<String> commands = new ArrayList<>();
        commands.add("input");
        commands.add("output");
        commands.add("path");
        commands.add("sort");
        commands.add("order");

        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("o", "output");
        parameters.put("s", "sort");
        for (String arg : args) {
            if (!arg.contains("=") || arg.charAt(arg.length() - 1) == '=') {
                System.out.println("ОШИБКА! Некорректный параметр (" + arg + ")");
                System.out.println("Он будет отброшен");
                continue;
            }
            String[] parts = arg.split("=", 2);
            String key = parts[0];
            if (key.startsWith("--")) {
                key = key.substring(2);
            } else {
                key = key.substring(1);
            }
            if (parameters.containsKey(key)) {
                key = parameters.get(key);
            }
            if(result.containsKey(key) || result.containsKey(parameters.get(key))) {
                System.out.println("ОШИБКА! Каждый параметр можно использовать только 1 раз");
                System.out.println(arg + " этот параметр будет пропущен");
                continue;
            }
            String value = parts[1];
            if (commands.contains(key)) {
                result.put(key, value);
            } else {
                System.out.println("Введен неизвестный параметр!!! (" + arg + ")\nОн будет проигнорирован");
            }
        }
        if (!result.containsKey("input")) {
            System.out.println("ОШИБКА! Не указан путь к input файлу");
            return new LinkedHashMap<>();
        }
        if (!result.containsKey("output") && !result.containsKey("path")) {
            result.put("output", "console");
        } else if (!result.containsKey("output") && result.containsKey("path")) {
            System.out.println("ОШИБКА! Указан путь, но не указано, куда должен выводиться результат (консоль или файл)");
            System.out.println("Результат будет записан по указанному пути(" + result.get("path") + ")");
            result.put("output", "file");
        } else if (result.containsKey("output") && !result.containsKey("path")) {
            System.out.println("ОШИБКА! Указан вывод в файл, но путь к файлу не указан!");
            System.out.println("Результат будет выведен в консоль");
            result.put("output", "console");
        } else if (result.containsKey("output") && result.containsKey("path")) {
            if (!result.get("output").equals("console") && !result.get("output").equals("file")) {
                System.out.println("ОШИБКА! Указан не валилный способ вывода");
                System.out.println("Результат будет записан по указанному пути(" + result.get("path") + ")");
                System.out.println("Если по этому пути неполучится открыть файл, то результат будет выведен в консоль");
                result.put("output", "file");
            }
        }
        if (!result.containsKey("sort") && result.containsKey("order")) {
            System.out.println("ОШИБКА! Не указан параметр для сортировки\n" +
                    "Строки не будет отсортированы");
        } else if (result.containsKey("sort") && !result.containsKey("order")) {
            result.put("order", "ask");
        } else if (result.containsKey("sort") && result.containsKey("order")) {
            if (!result.get("sort").equals("name") && !result.get("sort").equals("salary")) {
                System.out.println("ОШИБКА! Неверный параметр сортировки (--sort=" + result.get("sort") + ")");
                System.out.println("Строки не будет отсортированы");
                result.remove("sort");
                result.remove("order");
            } else if (result.get("sort").equals("name") || result.get("sort").equals("salary")) {
                if (!result.get("order").equals("ask") && !result.get("order").equals("desk")) {
                    System.out.println("ОШИБКА! Указан неверный порядок сортировки(--order=" + result.get("order") + ")");
                    System.out.println("Будет выбран прямой порядок");
                    result.put("order", "ask");
                }
            }
        }
        return result;
    }
}