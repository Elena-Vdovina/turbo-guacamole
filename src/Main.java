import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


// Сделать класс для списка дел. Команды:
// - посмотреть весь список дел,
// - посмотреть спискок на день,
// - добавить дело,
// - отметить выполнение.
// Отсортировать вывод по датам (на день, неделю), по факту выполнения.
//
// Если есть файл, сохраненный ранее, он читает его. Если нет, создает.
//
// ?Возможность делать напоминания (сравнить текущую дату и дату дела со статусом "не выполнено")?


public class Main {
  enum Command {
    VIEW, // Посмотреть дела (по умолчанию весь список; по дате, если ввести ее?)
    VIEW_PLANS, // посмотреть невыполненные дела (?с будущей датой?)
    ADD, // добавить дело (строку: дело и дата)
    CHECK, // отметить выполненным (ввести дату и номер номер)
    EXIT, // выход из программы


    private static final Map<Command, String> commands = new HashMap<>();

    static { // статический константный словарь
      commands.put(Command.VIEW, "Посмотреть список дел");
      commands.put(Command.VIEW_PLANS, "Посмотреть невыполненные дела");
      commands.put(Command.ADD, "Добавить запись");
      commands.put(Command.CHECK, "Отметить дела выполненными");
      commands.put(Command.EXIT, "Выход");
    }

    public static void main(String[] args) throws IOException {
//      ToDoList list = new ToDoList(); // объект класса ToDoList

      Command command = readCommand();
      while (command != Command.EXIT) { // основной рабочий цикл программы, обрабатывающий команды
        switch (command) {
          case ADD -> {
    //        ListLine line = ListLine.readListLine(); // добавление строки
    //        list.addLine(line);
          }
//          case CHECK -> list.set(); // установление значения для параметра "выполнено"
//          case VIEW -> list.printList(); // вывод всего списка (или по дате?)
//          case VIEW_PLANS -> list.printList_toParametr(); // вывод списка невыполненных дел
        }
        command = readCommand(); // команда EXIT просто завершит цикл
      }
      System.out.println("До свидания!");
    }

    public static void printMenu() {
      System.out.println(); // пустая строка для красоты
      System.out.println("Список команд:");
      for (Command command : commands.keySet()) {
        System.out.println("- " + command + ": " + commands.get(command));
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
          System.out.println("Некорректная команда: " + command);
          System.out.print("Введите корректную команду: ");
          command = br.readLine().toUpperCase();
        }
      }

      System.out.println(); // пустая строка для красоты
      return result;
    }
    }
}
