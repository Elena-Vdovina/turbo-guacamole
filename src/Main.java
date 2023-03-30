import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


// Сделать класс для списка дел. Команды:
// - посмотреть весь список дел,
// - посмотреть список на день,
// - добавить дело,
// - отметить выполнение.
// Отсортировать вывод по датам (на день, неделю), по факту выполнения.
//
// Если есть файл, сохраненный ранее, он читает его. Если нет, создает.
//
// Возможность делать напоминания (сравнить текущую дату и дату дела со статусом "не выполнено")?


public class Main {

  enum Command {
    VIEW, // Посмотреть дела (по умолчанию весь список; по дате, если ввести ее?)
    PLANS, // посмотреть невыполненные дела
    TODAY, // посмотреть дела на текущую дату
    CREATE, //Создать новый список дел
    ADD, // добавить дело (строку: дело и дата)
    CHECKDATE, // изменить дату
    CHECK, //изменить статус выполнения
    EXIT, // выход из программы
  }

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RED = "\u001B[31m";

  private static final Map<Command, String> commands = new HashMap<>();

  static { // статический константный словарь
    commands.put(Command.VIEW, "Посмотреть список дел");
    commands.put(Command.PLANS, "Посмотреть невыполненные дела");
    commands.put(Command.TODAY, "Посмотреть дела на сегодня");
    commands.put(Command.CREATE, "Создать новый список дел");
    commands.put(Command.ADD, "Добавить запись");
    commands.put(Command.CHECKDATE, "Изменить дату выполнения");
    commands.put(Command.CHECK, "Изменить статус дела");
    commands.put(Command.EXIT, "Выход");
  }

  public static void main(String[] args) throws IOException, ParseException {
    Command command = readCommand();
    while (command != Command.EXIT) { // основной рабочий цикл программы, обрабатывающий команды
      switch (command) {
        case VIEW -> printList(); // вывод всего списка (или по дате?)
        case PLANS -> printListNoCheck(); // вывод списка невыполненных дел
        case TODAY -> printListForDay(); // вывод списка дел на текущую дату
        case CREATE -> createNewList(); // создание нового списка дел
        case ADD -> addEvent(); // добавление новой записи
        case CHECKDATE -> setData(); // изменение даты выполнения
        case CHECK -> setCheck(); // изменение статуса дела
      }
      command = readCommand(); // команда EXIT просто завершит цикл
    }
    System.out.println("До свидания!");
  }

  public static void printMenu() {


    System.out.println(); // пустая строка для красоты
    System.out.println("Список команд:");
    for (Command command : commands.keySet()) {
      System.out.println(ANSI_BLUE + "- " + command + ANSI_RESET + ": " + ANSI_GREEN
          + commands.get(command) + ANSI_RESET);
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

    System.out.println(); // пустая строка для красоты
    return result;
  }

  // Читает список дел из файла в начале работы программы
  public static List<Event> readFile() throws IOException, ParseException {
    List<Event> events = new ArrayList<>();
    try {
      List<String> lines = new ArrayList<>();
      BufferedReader fr = new BufferedReader(new FileReader("src/todolist.txt"));
      String line;
      while ((line = fr.readLine()) != null) {
        lines.add(line);
      }
      for (String s : lines) {
        List<String> columns = List.of(s.split(";", -1));
        int status = Integer.parseInt(columns.get(2));
        Event event = new Event(columns.get(0), columns.get(1), status);
        events.add(event);
      }
      fr.close();
    } catch (IOException e) {
      System.out.println("У Вас не обнаружен список дел");
      createNewList();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return events;
  }

  // Выводит список дел на экран
  public static void printList() throws IOException, ParseException {
    List<Event> events = readFile();
    int i = 0;
    for (Event event : events) {
      System.out.printf(" %3d ", i + 1);
      System.out.println(event);
      ++i;
    }
  }

  // Выводит список невыполненных дел с сортировкой по дате и алфавиту
  public static void printListNoCheck() throws IOException, ParseException {
    List<Event> events = readFile();
    List<Event> noCheckEvents = new ArrayList<>();
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
  public static void printListForDay() throws IOException, ParseException {
    List<Event> events = readFile(); // читаем список дел
    Date current = new Date(); // записываем текущую системную дату
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    String currentDate = formatter.format(current); // переводим системную дату в строку
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
  public static void addEvent() throws IOException, ParseException {
    List<Event> events = readFile();
    // прочитали
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Новая запись в списке дел:");
    System.out.print("Что надо сделать - ");
    String name = br.readLine();
    while (name.isEmpty()) { // проверка на пустоту для названия
      System.out.println(ANSI_RED + "Запись не может быть пустой. Введите дело:" + ANSI_RESET);
      name = br.readLine();
    }
    System.out.print("До какого числа (\"дд.мм.гггг\") - ");
    String dateStr = DateValidation(br); // проверка формата ввода
    System.out.print("Выполнено/не выполнено (1/0) - ");
    int status = CheckValidation(br); // проверка ввода
    // добавили
    Event event = new Event(name, dateStr, status);
    events.add(event);
    // записали в файл
    writeFile(events);
    printList();
  }

  public static String DateValidation(BufferedReader br) {
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

  public static int CheckValidation(BufferedReader br) throws IOException {
    String strStatus = br.readLine();
    while (!(strStatus.equals("0") || strStatus.equals("1"))) {
      // проверка на соответствующее значение
      System.out.print(ANSI_RED + "Некорректное значение. Введите 1 или 0:" + ANSI_RESET);
      strStatus = br.readLine();
    }
    return Integer.parseInt(strStatus);
  }

  // Записываем список дел в файл каждый раз заново
  public static void writeFile(List<Event> events) throws IOException {
    FileWriter fr = new FileWriter("src/todolist.txt");
    for (Event event : events) {
      String line = event.getName() + ";" + event.getDateStr() + ";";
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
  public static void setCheck() throws IOException, ParseException {
    List<Event> events = readFile();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printList();
    // прочитали
    System.out.print("Номер записи для изменения статуса ");
    int n = Integer.parseInt(br.readLine());
    System.out.print("Введите новый статус - выполнено/не выполнено (1/0) - ");
    int status = CheckValidation(br); // проверка ввода
    // записали
    Event event = events.get(n - 1);
    Event event1 = new Event(event.getName(), event.getDateStr(), status);
    events.set(n - 1, event1);
    writeFile(events);
    printList();
  }

  // изменение даты выполнения дела
  public static void setData() throws IOException, ParseException {
    List<Event> events = readFile();
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    printList();
    // прочитали
    System.out.print("Номер записи для изменения даты ");
    int n = Integer.parseInt(br.readLine());
    System.out.print("Введите новую дату (дд.мм.гггг) - ");
    String dateStr = DateValidation(br); // проверка формата ввода
    // записали
    Event event = events.get(n - 1);
    int status;
    if (event.getCheck()) {
      status = 1;
    } else {
      status = 0;
    }
    Event event1 = new Event(event.getName(), dateStr, status);
    events.set(n - 1, event1);
    writeFile(events);
    printList();
  }

  // создание нового списка дел
  public static void createNewList() throws IOException, ParseException {
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
      System.out.print("До какого числа (\"дд.мм.гггг\") - ");
      String dateStr = br.readLine();
      System.out.print("Выполнено/не выполнено (1/0) - ");
      int status = Integer.parseInt(br.readLine());
      // добавили
      Event event = new Event(name, dateStr, status);
      events.add(event);
      System.out.println();
      System.out.print("Добавить новую запись (1-да, 2-выход): ");
      i = Integer.parseInt(br.readLine());
    }
    writeFile(events);
    printList();
  }
}