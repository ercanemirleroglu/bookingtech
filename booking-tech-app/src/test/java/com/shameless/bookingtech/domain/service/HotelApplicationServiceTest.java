package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HotelApplicationServiceTest {

    private HotelApplicationService underTest;

    @Mock
    private SearchCriteriaService searchCriteriaService;

    @Mock
    private LocationService locationService;

    @Mock
    private HotelService hotelService;

    @Mock
    private PriceService priceService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new HotelApplicationService(searchCriteriaService, locationService,
                hotelService, priceService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    @Disabled
    public void testSave() {
        // Mock input data
        BookingResultDto bookingResultDto = new BookingResultDto();
        // Set the necessary fields in bookingResultDto

        // Mock searchCriteriaService
        SearchCriteriaDto searchCriteriaDto = new SearchCriteriaDto();
        // Set the necessary fields in searchCriteriaDto
        when(searchCriteriaService.findByParams(any(SearchCriteriaDto.class))).thenReturn(null);

        // Mock locationService
        // Mock the necessary methods in locationService

        // Mock hotelService
        // Mock the necessary methods in hotelService

        // Call the method to be tested
        underTest.save(bookingResultDto);

        // Verify the expected method calls
        verify(searchCriteriaService, times(1)).findByParams(any(SearchCriteriaDto.class));
        verify(searchCriteriaService, times(1)).add(any(SearchCriteriaDto.class));
        verify(locationService, times(1)).addBulk(anyList());
        verify(hotelService, times(1)).addBulk(anyList());
    }
}