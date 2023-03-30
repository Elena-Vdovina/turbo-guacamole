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
  private Date date;
  private Boolean check;

  public Event(String name, String dateStr, int status) throws ParseException {
    this.name = name;
    setDate(dateStr);
    setCheck(status);
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

  public Date getDate() {
    return date;
  }

  public String getDateStr(){
    return formatter.format(date);
  }

  public Boolean getCheck() {
    return check;
  }

  @Override
  public String toString() {
    String checkToPrint;
    if (this.check) checkToPrint = ANSI_GREEN+"Сделано"+ANSI_RESET;
    else checkToPrint = ANSI_YELLOW+"Не сделано"+ANSI_RESET;
    return String.format("%-30s %-10s %s",name, formatter.format(date), checkToPrint);
  }
}
