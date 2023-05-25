package mx.simio.apidemo.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReservationRequest {

  @NotBlank(message = "Client full name cannot be blank")
  @Size(min = 1, max = 200, message = "Client full name must be between 1 and 200 characters")
  private String clientFullName;

  @NotNull(message = "Room number cannot be null")
  @Positive(message = "Room number should be positive")
  private Integer roomNumber;

  @NotNull(message = "Reservation dates cannot be null")
  @Size(min = 1, message = "At least one reservation date should be provided")
  private List<LocalDate> reservationDates;
}
