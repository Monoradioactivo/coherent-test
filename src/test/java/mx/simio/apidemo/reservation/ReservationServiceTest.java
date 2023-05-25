package mx.simio.apidemo.reservation;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.simio.apidemo.exception.NotFoundException;
import mx.simio.apidemo.exception.ServiceException;
import mx.simio.apidemo.file.FileOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

  @Mock
  private FileOperations fileOperations;

  private ReservationService reservationService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    reservationService = new ReservationService(fileOperations);
  }

  @Test
  void testLoadData_Success() {
    Set<Reservation> testData = new HashSet<>();
    testData.add(new Reservation(1, "John Doe", 101, Collections.singletonList(LocalDate.now())));
    when(fileOperations.readFromFile()).thenReturn(testData);

    reservationService.loadData();

    verify(fileOperations, times(1)).readFromFile();
  }

  @Test
  void testLoadData_ExceptionThrown() {
    when(fileOperations.readFromFile()).thenThrow(new ServiceException("File read error"));

    ServiceException exception = assertThrows(ServiceException.class,
        () -> reservationService.loadData());
    assertEquals("Error loading data", exception.getMessage());
    verify(fileOperations, times(1)).readFromFile();
  }

  @Test
  void testSaveData_Success() {
    Set<Reservation> testData = new HashSet<>();
    testData.add(new Reservation(1, "John Doe", 101, Collections.singletonList(LocalDate.now())));

    doNothing().when(fileOperations).writeToFile(any());

    assertDoesNotThrow(() -> reservationService.saveData());

    verify(fileOperations).writeToFile(any());
  }

  @Test
  void testSaveData_ExceptionThrown() {
    Set<Reservation> testData = new HashSet<>();
    testData.add(new Reservation(1, "John Doe", 101, Collections.singletonList(LocalDate.now())));

    doAnswer(invocation -> {
      throw new ServiceException("File write error");
    }).when(fileOperations).writeToFile(any());

    ServiceException exception = assertThrows(ServiceException.class,
        () -> reservationService.saveData());
    assertEquals("Error saving data", exception.getMessage());
    verify(fileOperations, times(1)).writeToFile(any());
  }

  @Test
  void testCreateReservation() {
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setClientFullName("John Doe");
    reservationRequest.setRoomNumber(101);
    reservationRequest.setReservationDates(Collections.singletonList(LocalDate.now()));

    ReservationResponse createdReservation = reservationService.createReservation(
        reservationRequest);

    assertNotNull(createdReservation.getId());
    assertEquals(reservationRequest.getClientFullName(), createdReservation.getClientFullName());
    assertEquals(reservationRequest.getRoomNumber(), createdReservation.getRoomNumber());
    assertEquals(reservationRequest.getReservationDates(),
        createdReservation.getReservationDates());
  }

  @Test
  void testUpdateReservation_Success() {
    Reservation existingReservation = new Reservation(1, "John Doe", 101,
        Collections.singletonList(LocalDate.now()));
    Set<Reservation> data = new HashSet<>();
    data.add(existingReservation);

    when(fileOperations.readFromFile()).thenReturn(data);

    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setClientFullName("Updated Name");
    reservationRequest.setRoomNumber(102);
    reservationRequest.setReservationDates(Collections.singletonList(LocalDate.now().plusDays(1)));

    ReservationService reservationServiceSpy = spy(new ReservationService(fileOperations));
    reservationServiceSpy.loadData(); // Load the mock data

    doCallRealMethod().when(reservationServiceSpy)
        .updateReservation(anyInt(), any(ReservationRequest.class));

    ReservationResponse updatedReservation = reservationServiceSpy.updateReservation(
        existingReservation.getId(), reservationRequest);

    assertNotNull(updatedReservation.getId());
    assertEquals(reservationRequest.getClientFullName(), updatedReservation.getClientFullName());
    assertEquals(reservationRequest.getRoomNumber(), updatedReservation.getRoomNumber());
    assertEquals(reservationRequest.getReservationDates(),
        updatedReservation.getReservationDates());
  }

  @Test
  void testUpdateReservation_ReservationNotFound() {
    int nonExistingReservationId = 999;
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setClientFullName("Updated Name");
    reservationRequest.setRoomNumber(102);
    reservationRequest.setReservationDates(Collections.singletonList(LocalDate.now().plusDays(1)));

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> reservationService.updateReservation(nonExistingReservationId, reservationRequest));

    assertEquals("Reservation not found", exception.getMessage());
  }

  @Test
  void testUpdateReservation_ErrorUpdatingReservation() {
    int existingReservationId = 1;
    ReservationRequest reservationRequest = new ReservationRequest();
    reservationRequest.setClientFullName("Updated Name");
    reservationRequest.setRoomNumber(102);
    reservationRequest.setReservationDates(Collections.singletonList(LocalDate.now().plusDays(1)));

    when(fileOperations.readFromFile()).thenThrow(new NotFoundException("Reservation not found"));

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> reservationService.updateReservation(existingReservationId, reservationRequest));

    assertEquals("Reservation not found", exception.getMessage());
  }

  @Test
  void testGetAllReservations() {
    Reservation reservation = new Reservation(1, "John Doe", 101,
        Collections.singletonList(LocalDate.now()));
    Set<Reservation> data = new HashSet<>();
    data.add(reservation);

    when(fileOperations.readFromFile()).thenReturn(data);

    reservationService.loadData();

    List<ReservationResponse> reservations = reservationService.getAllReservations();

    assertEquals(1, reservations.size());
    ReservationResponse reservationResponse = reservations.get(0);
    assertEquals(reservation.getId(), reservationResponse.getId());
    assertEquals(reservation.getClientFullName(), reservationResponse.getClientFullName());
    assertEquals(reservation.getRoomNumber(), reservationResponse.getRoomNumber());
    assertEquals(reservation.getReservationDates(), reservationResponse.getReservationDates());
  }

  @Test
  void testGetAllReservations_EmptyReservationsSet() {

    Set<Reservation> emptyData = Collections.emptySet();

    when(fileOperations.readFromFile()).thenReturn(emptyData);

    List<ReservationResponse> reservations = reservationService.getAllReservations();

    assertTrue(reservations.isEmpty());
  }
}
