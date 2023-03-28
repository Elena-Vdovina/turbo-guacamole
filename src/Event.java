import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

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

  public Boolean getCheck() {
    return check;
  }

  @Override
  public String toString() {
    return String.format("%-30s %-10s %b",name, formatter.format(date), check);
  }

}