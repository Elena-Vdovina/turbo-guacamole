import java.util.Comparator;

public class EventDateNameComparator implements Comparator<Event> {

  @Override
  public int compare(Event o1, Event o2) {
    if (!o1.getDate().equals(o2.getDate())) {
      return o1.getDate().compareTo(o2.getDate());
    }
    return o1.getName().compareTo(o2.getName());
  }

}
