package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.edu.fatecfranca.api.dtos.CarRequest;
import br.edu.fatecfranca.api.repositories.CarRepository;
import br.edu.fatecfranca.api.services.CarService;

class CarServiceTests {

    @Test
    void duplicatePlateMustReturnConflictBeforeOtherCarValidations() {
        CarRepository repository = mock(CarRepository.class);
        when(repository.existsByPlates("KWY9J35")).thenReturn(true);
        CarService service = new CarService(repository);

        CarRequest request = new CarRequest(
                "Volkswagen",
                "Gol GTI",
                "Azul",
                1994,
                false,
                "KWY9J35",
                LocalDate.of(2026, 11, 23),
                null,
                null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void negativeSellingPriceMustReturnConflict() {
        CarRepository repository = mock(CarRepository.class);
        when(repository.existsByPlates("ABC1D23")).thenReturn(false);
        CarService service = new CarService(repository);

        CarRequest request = new CarRequest(
                "Volkswagen",
                "Gol GTI",
                "Azul",
                1994,
                false,
                "ABC1D23",
                LocalDate.of(2026, 11, 23),
                new BigDecimal("-400"),
                null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }
}
