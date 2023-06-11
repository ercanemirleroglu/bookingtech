package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.BookingResultDto;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HotelApplicationServiceTest {

    @InjectMocks
    private HotelApplicationService hotelApplicationService;

    @Mock
    private SearchCriteriaService searchCriteriaService;

    @Mock
    private LocationService locationService;

    @Mock
    private HotelService hotelService;

    @Test
    public void testSave() {
        MockitoAnnotations.openMocks(this);

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
        hotelApplicationService.save(bookingResultDto);

        // Verify the expected method calls
        verify(searchCriteriaService, times(1)).findByParams(any(SearchCriteriaDto.class));
        verify(searchCriteriaService, times(1)).add(any(SearchCriteriaDto.class));
        verify(searchCriteriaService, never()).update(any(SearchCriteriaDto.class));
        verify(locationService, times(1)).addBulk(anyList());
        verify(hotelService, times(1)).addBulk(anyList());
    }
}