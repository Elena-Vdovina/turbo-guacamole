import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.text.ParseException;
import java.util.Comparator;

public class EventDateNameComparatorTests {

  private final Comparator<Event> comparator = new EventDateNameComparator();

  @Test
  public void differentDateComparison() throws ParseException {
    // сравниваем два дела с разными датами

    // arrange
    Event event1 = new Event("Title1", 1, "01.01.2024", 1);
    Event event2 = new Event("Title2", 1, "02.02.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  @Test
  public void sameDateDifferentNameComparison() throws ParseException {
    // сравниваем два дела с одинаковыми датами и разными названиями

    // arrange
    Event event1 = new Event("FTitle1", 1, "01.01.2024", 1);
    Event event2 = new Event("STitle2", 1, "01.01.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  @Test
  public void sameNameDifferentDateComparison() throws ParseException {
    // сравниваем два дела с одинаковыми названиями и разными датами

    // arrange
    Event event1 = new Event("Title", 1, "01.01.2024", 1);
    Event event2 = new Event("Title", 1, "02.02.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  @Test
  public void sameNameDateDifferentStatusComparison() throws ParseException {
    // сравниваем два дела с одинаковыми датами и названиями, но разным статусом

    // arrange
    Event event1 = new Event("Title", 1, "01.01.2024", 0);
    Event event2 = new Event("Title", 1, "01.01.2024", 1);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }


  @Test
  public void emptyNameDateComparison() throws ParseException {
    // arrange
    Event event1 = new Event("", 1, "01.01.2024", 1);
    Event event2 = new Event("", 1, "02.02.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  @Test
  public void sameTitleAuthorAndEmptyAuthorComparison() throws ParseException {
    // сравниваем два дела с одинаковыми датами и разными названиями

    // arrange
    Event event1 = new Event("", 1, "01.01.2024", 1);
    Event event2 = new Event("FTitle", 1, "01.01.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

}
