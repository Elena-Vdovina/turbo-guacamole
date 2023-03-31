import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  private static final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
  private final String name;
  private int priority;
  private Date date;
  private Boolean check;

  public Event(String name, int priority, String dateStr, int status) throws ParseException {
    this.name = name;
    this.priority = priority;
    setDate(dateStr);
    setCheck(status);
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public void setDate(String dateStr) throws ParseException {
    this.date = formatter.parse(dateStr);
  }

  public void setCheck(int status) {
    if (status > 0) {
      this.check = true;
    } else {
      this.check = false;
    }
  }

  public String getName() {
    return name;
  }

  public int getPriority() {
    return priority;
  }

  public String getPriorityStr() {
    String prioritet = "";
    switch (this.priority) {
      case 1 -> prioritet = "Очень важно";
      case 2 -> prioritet = "Важно";
      case 3 -> prioritet = "Не важно";
      case 4 -> prioritet = "Неизвестно";
    }
    return prioritet;
  }

  public Date getDate() {
    return date;
  }

  public String getDateStr() {
    return formatter.format(date);
  }

  public Boolean getCheck() {
    return check;
  }

  @Override
  public String toString() {
    String checkToPrint;
    if (this.check) {
      checkToPrint = ANSI_GREEN + "Выполнено" + ANSI_RESET;
    } else {
      checkToPrint = ANSI_YELLOW + "Не выполнено" + ANSI_RESET;
    }
    String prioritet = getPriorityStr();
    return String.format("%-30s %-15s %-10s %s", name, prioritet, formatter.format(date), checkToPrint);
  }
}
