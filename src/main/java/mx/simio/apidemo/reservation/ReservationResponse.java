package mx.simio.apidemo.reservation;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {

  private Integer id;
  private String clientFullName;
  private Integer roomNumber;
  private List<LocalDate> reservationDates;
}
