package mx.simio.apidemo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mx.simio.apidemo.exception.ServiceException;
import mx.simio.apidemo.reservation.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUtilImplTest {

  @TempDir
  Path tempDir;
  File tempFile;

  @InjectMocks
  private FileUtilImpl fileUtil;

  private Reservation reservation;

  @BeforeEach
  void setUp() {
    reservation = new Reservation(1, "John Doe", 101, List.of(LocalDate.now()));
    tempFile = tempDir.resolve("reservations.txt").toFile();
    fileUtil = new FileUtilImpl(tempFile.getPath());
  }

  @AfterEach
  void tearDown() {
    tempFile.setReadable(true);
    tempFile.setWritable(true);
  }

  @Test
  void shouldWriteToFileSuccessfully() throws IOException, ClassNotFoundException {
    Set<Reservation> reservationSet = new HashSet<>();
    reservationSet.add(reservation);

    fileUtil.writeToFile(reservationSet);

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile))) {
      Set<Reservation> result = (Set<Reservation>) ois.readObject();

      assertFalse(result.isEmpty());
      assertEquals(reservationSet.size(), result.size());
      assertTrue(result.contains(reservation));
    }
  }

  @Test
  void shouldThrowServiceExceptionWhenUnableToWriteToFile() {
    String nonExistentDirectory = "/non_existent_directory/reservations.txt";
    FileUtilImpl fileUtil = new FileUtilImpl(nonExistentDirectory);

    Set<Reservation> reservationSet = new HashSet<>();
    reservationSet.add(reservation);

    assertThrows(ServiceException.class, () -> fileUtil.writeToFile(reservationSet));
  }

  @Test
  void shouldWriteEmptySetToFile() throws IOException, ClassNotFoundException {
    Set<Reservation> emptySet = new HashSet<>();

    fileUtil.writeToFile(emptySet);

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile))) {
      Set<Reservation> result = (Set<Reservation>) ois.readObject();

      assertTrue(result.isEmpty());
    }
  }

  @Test
  void shouldReturnEmptySetIfFileDoesNotExist() {
    // Given
    FileUtilImpl fileUtil = new FileUtilImpl(tempDir.resolve("not_existing_file.txt").toString());

    // When
    Set<Reservation> result = fileUtil.readFromFile();

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptySetIfFileExistsButIsNotASet() throws IOException {
    // Given
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
      oos.writeObject("Not a set");
    }

    // When
    Set<Reservation> result = fileUtil.readFromFile();

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptySetIfFileExistsButNotAllObjectsAreReservations() throws IOException {
    // Given
    Set<Object> mixedSet = new HashSet<>();
    mixedSet.add(reservation);
    mixedSet.add("Not a reservation object");

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
      oos.writeObject(mixedSet);
    }

    // When
    Set<Reservation> result = fileUtil.readFromFile();

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnReservationSetIfFileExistsAndAllObjectsAreReservations() throws IOException {
    // Given
    Set<Reservation> reservationSet = new HashSet<>();
    reservationSet.add(reservation);

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))) {
      oos.writeObject(reservationSet);
    }

    // When
    Set<Reservation> result = fileUtil.readFromFile();

    // Then
    assertFalse(result.isEmpty());
    assertEquals(reservationSet.size(), result.size());
    assertTrue(result.contains(reservation));
  }
}
