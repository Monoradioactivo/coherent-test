package mx.simio.apidemo.file;

import java.util.Set;
import mx.simio.apidemo.reservation.Reservation;

public interface FileOperations {

  void writeToFile(Set<Reservation> reservations);

  Set<Reservation> readFromFile();
}
