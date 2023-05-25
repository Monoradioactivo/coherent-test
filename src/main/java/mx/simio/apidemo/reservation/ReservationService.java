package mx.simio.apidemo.reservation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.simio.apidemo.exception.NotFoundException;
import mx.simio.apidemo.exception.ServiceException;
import mx.simio.apidemo.file.FileOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

  private final FileOperations fileOperations;
  private final Set<Reservation> reservations = new HashSet<>();

  /**
   * This method is invoked at the time of the bean creation. It loads data from a file into the
   * reservations set.
   *
   * @throws ServiceException if an error occurs during reading the data from the file
   */
  @PostConstruct
  public void loadData() {
    try {
      var data = fileOperations.readFromFile();
      reservations.addAll(data);
      log.info("Loaded {} reservations", reservations.size());
    } catch (Exception e) {
      log.error("Error loading data", e);
      throw new ServiceException("Error loading data");
    }
  }

  /**
   * This method is invoked at the time of the bean destruction. It writes the data from the
   * reservations set into a file.
   *
   * @throws ServiceException if an error occurs during writing the data to the file
   */
  @PreDestroy
  public void saveData() {
    try {
      fileOperations.writeToFile(reservations);
      log.info("Saved {} reservations", reservations.size());
    } catch (Exception e) {
      log.error("Error saving data", e);
      throw new ServiceException("Error saving data");
    }
  }

  /**
   * Creates a new reservation and adds it to the reservations set.
   *
   * @param reservationRequest the new reservation to be added
   * @return the created reservation
   * @throws ServiceException if an error occurs during the reservation creation
   */
  public ReservationResponse createReservation(ReservationRequest reservationRequest) {
    var reservation = Reservation.builder()
        .clientFullName(reservationRequest.getClientFullName())
        .roomNumber(reservationRequest.getRoomNumber())
        .reservationDates(reservationRequest.getReservationDates())
        .build();

    try {

      reservations.add(reservation);

      log.info("Created reservation with id {}", reservation.getId());

      return ReservationResponse.builder()
          .id(reservation.getId())
          .clientFullName(reservation.getClientFullName())
          .roomNumber(reservation.getRoomNumber())
          .reservationDates(reservation.getReservationDates())
          .build();
    } catch (Exception e) {
      log.error("Error creating reservation", e);
      throw new ServiceException("Error creating reservation");
    }
  }

  /**
   * Retrieves all the reservations in the reservations set.
   *
   * @return a list of all reservations
   * @throws ServiceException if an error occurs during the retrieval of reservations
   */
  public List<ReservationResponse> getAllReservations() {
    try {
      log.info("Retrieving all reservations");
      return reservations.stream()
          .sorted(Comparator.comparing(Reservation::getId))
          .map(reservation -> ReservationResponse.builder()
              .id(reservation.getId())
              .clientFullName(reservation.getClientFullName())
              .roomNumber(reservation.getRoomNumber())
              .reservationDates(reservation.getReservationDates())
              .build())
          .toList();
    } catch (Exception e) {
      log.error("Error retrieving reservations", e);
      throw new ServiceException("Error retrieving reservations");
    }
  }

  /**
   * Updates the details of an existing reservation in the reservations set.
   *
   * @param id                 The ID of the reservation to update.
   * @param reservationRequest The reservation request containing the updated information.
   * @return The updated reservation response.
   * @throws NotFoundException if the reservation with the provided ID is not found.
   * @throws ServiceException  if an error occurs while updating the reservation.
   */
  public ReservationResponse updateReservation(Integer id, ReservationRequest reservationRequest) {
    Optional<Reservation> optionalReservation = reservations.stream()
        .filter(reservation -> reservation.getId().equals(id))
        .findFirst();

    try {
      if (optionalReservation.isEmpty()) {
        log.error("Reservation with id {} not found", id);
        throw new NotFoundException("Reservation not found");
      }

      Reservation existingReservation = optionalReservation.get();
      reservations.remove(existingReservation);

      Reservation updatedReservation = new Reservation();
      updatedReservation.setId(existingReservation.getId());
      updatedReservation.setClientFullName(reservationRequest.getClientFullName());
      updatedReservation.setRoomNumber(reservationRequest.getRoomNumber());
      updatedReservation.setReservationDates(reservationRequest.getReservationDates());

      reservations.add(updatedReservation);

      log.info("Updated reservation with id {}", updatedReservation.getId());

      return ReservationResponse.builder()
          .id(updatedReservation.getId())
          .clientFullName(updatedReservation.getClientFullName())
          .roomNumber(updatedReservation.getRoomNumber())
          .reservationDates(updatedReservation.getReservationDates())
          .build();
    } catch (NotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error updating reservation", e);
      throw new ServiceException("Error updating reservation", e);
    }
  }
}
