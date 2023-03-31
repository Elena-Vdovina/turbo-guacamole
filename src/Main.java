import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// Сделать класс для списка дел. Команды:
// - посмотреть весь список дел,
// - посмотреть список на день,
// - добавить дело,
// - отметить выполнение.
// Отсортировать вывод по факту выполнения.
// Если есть файл, сохраненный ранее, он читает его. Если нет, создает.
// Возможность делать напоминания (сравнить текущую дату и дату дела со статусом "не выполнено")
//

public class Main {

  enum Command {
    VIEW, // Посмотреть дела (по умолчанию весь список; по дате, если ввести ее?)
    PLANS, // посмотреть невыполненные дела
    TODAY, // посмотреть дела на текущую дату
    CREATE, //Создать новый список дел
    ADD, // добавить дело (строку: дело и дата)
    PRIOR, // изменить приоритет дела
    CHECKDATE, // изменить дату
    CHECK, //изменить статус выполнения
    FILE, // другой файл списка дел
    EXIT, // выход из программы
  }

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RED = "\u001B[31m";

  private static final List<Map<Command, String>> commands = new ArrayList<>();

  static { // статический константный словарь
    commands.add(Collections.singletonMap(Command.VIEW, "Посмотреть список дел"));
    commands.add(Collections.singletonMap(Command.PLANS, "Посмотреть невыполненные дела"));
    commands.add(Collections.singletonMap(Command.TODAY, "Посмотреть дела на сегодня"));
    commands.add(Collections.singletonMap(Command.CREATE, "Создать новый список дел"));
    commands.add(Collections.singletonMap(Command.ADD, "Добавить запись"));
    commands.add(Collections.singletonMap(Command.PRIOR, "Изменить приоритет дела"));
    commands.add(Collections.singletonMap(Command.CHECKDATE, "Изменить дату выполнения"));
    commands.add(Collections.singletonMap(Command.CHECK, "Изменить статус дела"));
    commands.add(Collections.singletonMap(Command.FILE, "Другой файл списка дел"));
    commands.add(Collections.singletonMap(Command.EXIT, "Выход"));
  }

  public static void main(String[] args) throws IOException, ParseException {
    String pathToFile = "src/todolist.txt";
    Command command = readCommand();
    while (command != Command.EXIT) { // основной рабочий цикл программы, обрабатывающий команды
      switch (command) {
        case VIEW -> printList(pathToFile); // вывод всего списка
        case PLANS -> printListNoCheck(pathToFile); // вывод списка невыполненных дел
        case TODAY -> printListForDay(pathToFile); // вывод списка дел на текущую дату
        case CREATE -> createNewList(pathToFile); // создание нового списка дел
        case ADD -> addEvent(pathToFile); // добавление новой записи
        case PRIOR -> setPrior(pathToFile); //
        case CHECKDATE -> setData(pathToFile); // изменение даты выполнения
        case CHECK -> setCheck(pathToFile); // изменение статуса дела
        case FILE -> pathToFile = changeFile(pathToFile); // другой файл списка дел
      }
      command = readCommand(); // команда EXIT просто завершит цикл
    }
    System.out.println("До свидания!");
  }

  // изменение текущего файла
  public static String changeFile(String pathToFile) throws IOException, ParseException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Текущий файл: " + pathToFile);
    System.out.print("Введите имя файла со списком дел: ");
    String newPath = br.readLine();
    String pathStr="";
    try {
      Path path = Paths.get(newPath);
      boolean exists = Files.isRegularFile(path);
      if (exists) {
        String s = Files.probeContentType(path);
        if (!s.equals("text/plain")) {
          System.out.println(ANSI_RED + "Файл \"" + newPath +
              "\" не является текстовым! Будет использоваться текущий файл!" + ANSI_RESET);
          pathStr= pathToFile;
        } else {
          pathStr= newPath;
        }
      } else {
        System.out.println(ANSI_RED + "Нет файла " + newPath + " со списком дел" + ANSI_RESET);
        pathStr=newFile(pathToFile, newPath);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return pathStr;
  }

  // создание нового файла
  public static String newFile(String pathToFile, String newPath) throws IOException, ParseException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("1 - Создать новый список дел в текущем файле ");
    System.out.println("2 - Создать список дел в файле " + newPath);
    System.out.println("3 - Вернуться в меню и остаться в файле ");
    String file = (br.readLine());
    String pathStr="";
    while (!(file.equals("1") || file.equals("2")|| file.equals("3"))) {
      // проверка на соответствующее значение
      System.out.print(ANSI_RED + "Некорректное значение. Попробуйте еще раз: " + ANSI_RESET);
      file = br.readLine();
    }
    switch (file) {
      case "1" -> {   // создать новый список дел в текущем файле
        createNewList(pathToFile);
        pathStr= pathToFile;
      }
      case "2" -> {  // создать список дел в новом файле
        createNewList(newPath);
        pathStr= newPath;
      }
      case "3" -> pathStr=pathToFile;  // вернуться в меню с текущим файлом
    }
    return pathStr;
  }

  public static void printMenu() {
    System.out.println(); // пустая строка для красоты
    System.out.println("Список команд:");

    for (Map<Command, String> command : commands) {
      String str = command.toString();
      String sub = str.substring(1, str.length() - 1);
      List<String> title = List.of(sub.split("=", -1));
      System.out.printf(ANSI_BLUE + "%-10s   " + ANSI_RESET + ANSI_GREEN + "%s\n" + ANSI_RESET,
          title.get(0), title.get(1));
    }
  }

  public static Command readCommand() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    printMenu();
    System.out.println();
    System.out.print("Введите команду: ");
    String command = br.readLine().toUpperCase();

    Command result = null;
    while (result == null) { // пока команда не установлена
      try {
        result = Command.valueOf(command); // пытаемся установить команду
      } catch (IllegalArgumentException e) {
        System.out.println(ANSI_RED + "Некорректная команда: " + command + ANSI_RESET);
        System.out.print("Введите корректную команду: ");
        command = br.readLine().toUpperCase();
      }
    }
    return result;
  }

  // Читает список дел из файла в начале работы программы
  public static List<Event> readFile(String pathToFile) throws IOException, ParseException {
    List<Event> events = new ArrayList<>();
    try {
      List<String> lines = new ArrayList<>();
      BufferedReader fr = new BufferedReader(new FileReader(pathToFile));
      String line;
      while ((line = fr.readLine()) != null) {
        lines.add(line);
      }
      for (String s : lines) {
        List<String> columns = List.of(s.split(";", -1));
        int status = Integer.parseInt(columns.get(3));
        int priority = Integer.parseInt(columns.get(1));
        Event event = new Event(columns.get(0), priority, columns.get(2), status);
        events.add(event);
      }
      fr.close();
    } catch (IOException e) {
      System.out.println("У Вас не обнаружен список дел");
      createNewList(pathToFile);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (IndexOutOfBoundsException e) {
      System.out.println(ANSI_RED + "В текущем файле нет списка дел!" + ANSI_RESET);
      createNewList(pathToFile);
    }
    return events;
  }

  // Выводит список дел на экран
  public static void printList(String pathToFile) throws IOException, ParseException {
    System.out.println();
    System.out.println("Актуальный список дел");
    List<Event> events = readFile(pathToFile);
    int i = 0;
    for (Event event : events) {
      System.out.printf(" %3d ", i + 1);
      System.out.println(event);
      ++i;
    }
  }

  // Выводит список невыполненных дел с сортировкой по дате и алфавиту
  public static void printListNoCheck(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile);
    List<Event> noCheckEvents = new ArrayList<>();
    System.out.println();
    for (Event event : events) {
      if (!event.getCheck()) {
        noCheckEvents.add(event);
      }
    }
    noCheckEvents.sort(new EventDateNameComparator());
    int i = 0;
    for (Event event : noCheckEvents) {
      System.out.printf(" %3d ", i + 1);
      System.out.println(event);
      ++i;
    }
  }

  // Выводит список дел на текущую дату
  public static void printListForDay(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile); // читаем список дел
    Date current = new Date(); // записываем текущую системную дату
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    String currentDate = formatter.format(current); // переводим системную дату в строку
    System.out.println();
    int i = 0; // счетчик
    boolean y = false; // флаг для определения пустого списка
    System.out.println("Список дел на сегодня");
    for (Event event : events) {
      if (currentDate.equals(event.getDateStr())) { // сравниваем текущую дату с датой дела
        System.out.printf(" %3d ", i + 1);
        System.out.println(event);
        ++i;
        y = true;
      }
    }
    if (!y) {
      System.out.println("На сегодня дел нет");
    }
  }

  // Добавляет новую запись в список дел и сохраняет ее в файл
  public static void addEvent(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile);
    // прочитали
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Новая запись в списке дел:");
    System.out.print("Что надо сделать - ");
    String name = br.readLine();
    while (name.isEmpty()) { // проверка на пустоту для названия
      System.out.println(ANSI_RED + "Запись не может быть пустой. Введите дело:" + ANSI_RESET);
      name = br.readLine();
    }
    System.out.println("Определите важность дела");
    System.out.println("ОЧЕНЬ ВАЖНО/ВАЖНО/НЕ ВАЖНО/НЕИЗВЕСТНО");
    System.out.print("     1     /  2  /    3   /    4     ");
    int prioritet = priorityValidation(br);
    System.out.print("До какого числа (\"дд.мм.гггг\") - ");
    String dateStr = dateValidation(br); // проверка формата ввода
    System.out.print("Выполнено/не выполнено (1/0) - ");
    int status = checkValidation(br); // проверка ввода
    // добавили
    Event event = new Event(name, prioritet, dateStr, status);
    events.add(event);
    // записали в файл
    writeFile(events, pathToFile);
    printList(pathToFile);
  }

  public static int priorityValidation(BufferedReader br) throws IOException {
    String prStr = br.readLine().toUpperCase();
    while (!(prStr.equals("1") || prStr.equals("2") || prStr.equals("3") || prStr.equals("4") ||
        prStr.equals("ОЧЕНЬ ВАЖНО") || prStr.equals("ВАЖНО") ||
        prStr.equals("НЕ ВАЖНО") || prStr.equals("НЕИЗВЕСТНО"))) {
      // проверка на соответствующее значение
      System.out.print(ANSI_RED + "Некорректное значение. Попробуйте еще раз: " + ANSI_RESET);
      prStr = br.readLine().toUpperCase();
    }
    int prioritet = 0;
    switch (prStr) {
      case "1", "ОЧЕНЬ ВАЖНО" -> prioritet = 1;
      case "2", "ВАЖНО" -> prioritet = 2;
      case "3", "НЕ ВАЖНО" -> prioritet = 3;
      case "4", "НЕИЗВЕСТНО" -> prioritet = 4;
    }
    return prioritet;
  }

  public static String dateValidation(BufferedReader br) {
    String dateStr = "";
    boolean tr = false; // флаг для проверки условий
    while (!tr) {
      try {
        dateStr = br.readLine();
        if (dateStr.isEmpty()) { // сообщение, если пустая строка
          System.out.print(ANSI_RED + "Дата не может быть пустой. " + ANSI_RESET);
        }
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateTest = String.valueOf(formatter.parse(dateStr));
        tr = true;
      } catch (ParseException e) { //ошибка, если некорректный формат
        System.out.print(ANSI_RED + "Неправильный формат ввода! Попробуйте еще раз: " + ANSI_RESET);
        tr = false;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return dateStr;
  }

  public static int checkValidation(BufferedReader br) throws IOException {
    String strStatus = br.readLine().toUpperCase();
    while (!(strStatus.equals("0") || strStatus.equals("1") || strStatus.equals("ВЫПОЛНЕНО") ||
        strStatus.equals("НЕ ВЫПОЛНЕНО"))) {
      // проверка на соответствующее значение
      System.out.print(ANSI_RED + "Некорректное значение. Попробуйте еще раз: " + ANSI_RESET);
      strStatus = br.readLine().toUpperCase();
    }
    switch (strStatus) {
      case "0", "НЕ ВЫПОЛНЕНО" -> strStatus = "0"; // не выполнено
      case "1", "ВЫПОЛНЕНО" -> strStatus = "1"; // выполнено
    }
    return Integer.parseInt(strStatus);
  }

  // Записываем список дел в файл каждый раз заново
  public static void writeFile(List<Event> events, String pathToFile) throws IOException {
    FileWriter fr = new FileWriter(pathToFile);
    for (Event event : events) {
      String line = event.getName() + ";" + event.getPriority() + ";" + event.getDateStr() + ";";
      if (event.getCheck()) {
        line += "1";
      } else {
        line += "0";
      }
      line += "\n";
      fr.write(line);
    }
    fr.close();
  }

  // установление значения для параметра "выполнено"
  public static void setCheck(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printList(pathToFile);
    // прочитали
    System.out.print("Номер записи для изменения статуса ");
    int n = Integer.parseInt(br.readLine());
    System.out.print("Введите новый статус - выполнено/не выполнено (1/0) - ");
    int status = checkValidation(br); // проверка ввода
    // записали
    Event event = events.get(n - 1);
    Event event1 = new Event(event.getName(), event.getPriority(), event.getDateStr(), status);
    events.set(n - 1, event1);
    writeFile(events, pathToFile);
    printList(pathToFile);
  }

  // изменение даты выполнения дела
  public static void setData(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printList(pathToFile);
    // прочитали
    System.out.print("Номер записи для изменения даты ");
    int n = Integer.parseInt(br.readLine());
    System.out.print("Введите новую дату (дд.мм.гггг) - ");
    String dateStr = dateValidation(br); // проверка формата ввода
    // записали
    Event event = events.get(n - 1);
    int status;
    if (event.getCheck()) {
      status = 1;
    } else {
      status = 0;
    }
    Event event1 = new Event(event.getName(), event.getPriority(), dateStr, status);
    events.set(n - 1, event1);
    writeFile(events, pathToFile);
    printList(pathToFile);
  }

  public static void setPrior(String pathToFile) throws IOException, ParseException {
    List<Event> events = readFile(pathToFile);
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printList(pathToFile);
    // прочитали
    System.out.print("Номер записи для изменения приоритета дела ");
    int n = Integer.parseInt(br.readLine());
    System.out.println("Введите новый приоритет ");
    System.out.println("ОЧЕНЬ ВАЖНО/ВАЖНО/НЕ ВАЖНО/НЕИЗВЕСТНО");
    System.out.println("     1     /  2  /    3   /    4     ");
    int prioritet = priorityValidation(br); // проверка формата ввода
    // записали
    Event event = events.get(n - 1);
    int status;
    if (event.getCheck()) {
      status = 1;
    } else {
      status = 0;
    }
    Event event1 = new Event(event.getName(), prioritet, event.getDateStr(), status);
    events.set(n - 1, event1);
    writeFile(events, pathToFile);
    printList(pathToFile);

  }

  // создание нового списка дел
  public static void createNewList(String pathToFile) throws IOException, ParseException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Создание нового списка дел: ");
    List<Event> events = new ArrayList<>();
    // прочитали
    int i = 1;
    while (i == 1) { //
      System.out.println();
      System.out.println("Новая запись в списке дел:");
      System.out.print("Что надо сделать - ");
      String name = br.readLine();
      System.out.println("Определите важность дела");
      System.out.println("ОЧЕНЬ ВАЖНО/ВАЖНО/НЕ ВАЖНО/НЕИЗВЕСТНО");
      System.out.print("     1     /  2  /    3   /    4     ");
      int prioritet = priorityValidation(br);
      System.out.print("До какого числа (\"дд.мм.гггг\") - ");
      String dateStr = dateValidation(br);
      System.out.print("Выполнено/не выполнено (1/0) - ");
      int status = checkValidation(br);
      // добавили
      Event event = new Event(name, prioritet, dateStr, status);
      events.add(event);
      System.out.println();
      System.out.print("Добавить новую запись (1-да, 2-выход): ");
      i = Integer.parseInt(br.readLine());
    }
    writeFile(events, pathToFile);
    printList(pathToFile);
  }
}
