package com.shameless.bookingtech.app.rest.api;

import com.shameless.bookingtech.app.model.response.LocationListResponse;
import com.shameless.bookingtech.app.rest.adapter.LocationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/locations")
public class LocationRestController {

    private final LocationAdapter locationAdapter;

    @GetMapping
    public ResponseEntity<LocationListResponse> listLocations(){
        LocationListResponse response = locationAdapter.listLocations();
        return ResponseEntity.ok().body(response);
    }

}
