package mx.simio.apidemo.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

  @Mock
  private ReservationService reservationService;

  @InjectMocks
  private ReservationController reservationController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void createReservation_success() throws Exception {
    ReservationRequest request = new ReservationRequest();
    ReservationResponse response = new ReservationResponse(1, "John Doe", 101,
        List.of(LocalDate.now()));

    when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/v1/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(reservationService, times(1)).createReservation(any(ReservationRequest.class));
  }

  @Test
  void getAllReservations_success() throws Exception {
    ReservationResponse response1 = new ReservationResponse(1, "John Doe", 101,
        List.of(LocalDate.now()));
    ReservationResponse response2 = new ReservationResponse(2, "Jane Smith", 102,
        List.of(LocalDate.now().plusDays(1)));

    List<ReservationResponse> responses = List.of(response1, response2);

    when(reservationService.getAllReservations()).thenReturn(responses);

    mockMvc.perform(get("/api/v1/reservations"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(responses)));

    verify(reservationService, times(1)).getAllReservations();
  }

  @Test
  void updateReservation_success() throws Exception {
    Integer id = 1;
    ReservationRequest request = new ReservationRequest();
    ReservationResponse response = new ReservationResponse(id, "John Doe", 101,
        List.of(LocalDate.now()));

    when(reservationService.updateReservation(eq(id), any(ReservationRequest.class))).thenReturn(
        response);

    mockMvc.perform(put("/api/v1/reservations/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(response)));

    verify(reservationService, times(1)).updateReservation(eq(id), any(ReservationRequest.class));
  }
}