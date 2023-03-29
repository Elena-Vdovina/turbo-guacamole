import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.print.Book;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventDateNameComparatorTests {

  private final Comparator<Event> comparator = new EventDateNameComparator();

  @Test
  public void differentDateComparison() throws ParseException {
    // сравниваем два дела с разными датами

    // arrange
    Event event1 = new Event("Title1", "01.01.2024", 1);
    Event event2 = new Event("Title2", "02.02.2024", 0);

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
    Event event1 = new Event("FTitle1", "01.01.2024", 1);
    Event event2 = new Event("STitle2", "01.01.2024", 0);

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
    Event event1 = new Event("Title", "01.01.2024", 1);
    Event event2 = new Event("Title", "02.02.2024", 0);

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
    Event event1 = new Event("Title", "01.01.2024", 0);
    Event event2 = new Event("Title", "01.01.2024", 1);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  @Test
  public void differentFieldsComparison() throws ParseException {
    // сравниваем двa дела с разными полями
    // проверяем, что приоритет остаётся за датой

    // arrange
    Event event1 = new Event("STitle", "01.01.2024", 1);
    Event event2 = new Event("FTitle", "02.02.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  // проверяем краевые случаи
//  @Test - будет ParseException
//  public void emptyDateComparison() throws ParseException {
//    // arrange
//    Event event1 = new Event("FTitle", "", 1);
//    Event event2 = new Event("STitle", "", 0);
//    // act
//    int result = comparator.compare(event1, event2);
//    int result2 = comparator.compare(event2, event1);
//
//    // assert
//    assertTrue(result < 0);
//    assertTrue(result2 > 0);
//  }

  @Test
  public void emptyNameDateComparison() throws ParseException {
    // arrange
    Event event1 = new Event("", "01.01.2024", 1);
    Event event2 = new Event("", "02.02.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

//  @Test - будет ParseException
//  public void emptyFieldsAndStatusComparison() throws ParseException {
//    // arrange
//    Event event1 = new Event("", "", 1);
//    Event event2 = new Event("", "", 0);
//
//    // act
//    int result = comparator.compare(event1, event2);
//    int result2 = comparator.compare(event2, event1);
//
//    // assert
//    assertTrue(result < 0);
//    assertTrue(result2 > 0);
//  }

//  @Test - будет ParseException
//  public void NameAndEmptyDateComparison() throws ParseException {
//    // сравниваем два дела с разными названиями
//
//    // arrange
//    Event event1 = new Event("FTitle", "02.02.2024", 1);
//    Event event2 = new Event("STitle", "", 0);
//
//    // act
//    int result = comparator.compare(event1, event2);
//    int result2 = comparator.compare(event2, event1);
//    // assert
//    assertTrue(result < 0);
//    assertTrue(result2 > 0);
//  }

  @Test
  public void sameTitleAuthorAndEmptyAuthorComparison() throws ParseException {
    // сравниваем два дела с одинаковыми датами и разными названиями

    // arrange
    Event event1 = new Event("", "01.01.2024", 1);
    Event event2 = new Event("FTitle", "01.01.2024", 0);

    // act
    int result = comparator.compare(event1, event2);
    int result2 = comparator.compare(event2, event1);

    // assert
    assertTrue(result < 0);
    assertTrue(result2 > 0);
  }

  // можно запретить null в конструкторе или проверить сравнение null здесь
  // запретить null в конструкторе: @NotNull или условие-стражник
//  @Test
//  public void nullTitle() {
//    // arrange
//    Book book1 = new Book("A", null, 1);
//    Book book2 = new Book("B", "B", 2);
//
//    // act-assert - проверяем исключение
//    assertThrowsExactly(NullPointerException.class, () -> comparator.compare(book1, book2));
//  }


}
