package org.goodstay.controller;

import lombok.RequiredArgsConstructor;
import org.goodstay.service.FacilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/getFacilities")
    public ResponseEntity<List<String>> getFacilities(
            @RequestParam("hotelIds") List<Long> hotelIds
    ) {
        return ResponseEntity.ok(facilityService.getFacilities(hotelIds));
    }
}
