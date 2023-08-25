package com.shameless.bookingtech.app.rest.api;

import com.shameless.bookingtech.app.model.response.HotelListResponse;
import com.shameless.bookingtech.app.rest.adapter.HotelAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/hotels")
public class HotelRestController {

    private final HotelAdapter hotelAdapter;

    @GetMapping
    public ResponseEntity<HotelListResponse> listHotels(){
        HotelListResponse hotelListResponse = hotelAdapter.listHotels();
        return ResponseEntity.ok().body(hotelListResponse);
    }

}
