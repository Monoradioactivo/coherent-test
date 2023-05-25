package mx.simio.apidemo.reservation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
    ReservationResponse createdReservation = reservationService.createReservation(request);
    return ResponseEntity.ok(createdReservation);
  }

  @GetMapping
  public ResponseEntity<List<ReservationResponse>> getAllReservations() {
    List<ReservationResponse> reservations = reservationService.getAllReservations();
    return ResponseEntity.ok(reservations);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Integer id, @RequestBody ReservationRequest request) {
    ReservationResponse updatedReservation = reservationService.updateReservation(id, request);
    return ResponseEntity.ok(updatedReservation);
  }
}
