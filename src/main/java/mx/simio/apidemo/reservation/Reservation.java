package mx.simio.apidemo.reservation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Data;

@Data
public class Reservation implements Serializable {

  private Integer id;
  private String clientFullName;
  private Integer roomNumber;
  private List<LocalDate> reservationDates;

  private static final AtomicInteger count = new AtomicInteger(0);

  @Builder
  public Reservation(String clientFullName, Integer roomNumber, List<LocalDate> reservationDates) {
    this.id = count.incrementAndGet();
    this.clientFullName = clientFullName;
    this.roomNumber = roomNumber;
    this.reservationDates = reservationDates;
  }

  public Reservation(Integer id, String clientFullName, Integer roomNumber,
      List<LocalDate> reservationDates) {
    this.id = id;
    this.clientFullName = clientFullName;
    this.roomNumber = roomNumber;
    this.reservationDates = reservationDates;
  }


  public Reservation() {
    this.id = count.incrementAndGet();
  }

  @Serial
  private static final long serialVersionUID = 1L;
}
