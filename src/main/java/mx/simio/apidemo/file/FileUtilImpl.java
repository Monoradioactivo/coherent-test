package mx.simio.apidemo.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import mx.simio.apidemo.exception.ServiceException;
import mx.simio.apidemo.reservation.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileUtilImpl implements FileOperations {

  private final String filePath;

  public FileUtilImpl(@Value("${file.path:reservations.txt}") String filePath) {
    this.filePath = filePath;
  }

  @Override
  public void writeToFile(Set<Reservation> reservations) {
    try (FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(reservations);
      oos.flush();
    } catch (IOException e) {
      log.error("Error writing to file", e);
      throw new ServiceException("Error writing to file");
    }
  }

  @Override
  public Set<Reservation> readFromFile() {
    File file = new File(filePath);

    if (!file.exists()) {
      return new HashSet<>();
    }

    try (FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis)) {
      Object obj = ois.readObject();

      if (obj instanceof Set) {
        Set<?> set = (Set<?>) obj;
        if (set.stream().allMatch(o -> o instanceof Reservation)) {
          return (Set<Reservation>) obj;
        } else {
          log.error("Error reading from file: Not all objects were Reservations");
        }
      } else {
        log.error("Error reading from file: Data was not a Set");
      }
    } catch (IOException e) {
      log.error("Error reading from file: IO exception", e);
    } catch (ClassNotFoundException e) {
      log.error("Error reading from file: Class not found", e);
    } catch (ClassCastException e) {
      log.error("Error reading from file: Class cast exception", e);
    }

    return new HashSet<>();
  }
}
