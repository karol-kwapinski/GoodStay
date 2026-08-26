package org.goodstay.service;

import lombok.RequiredArgsConstructor;
import org.goodstay.model.Facility;
import org.goodstay.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    public List<String> getFacilities(List<Long> hotelIds) {

        List<Facility> facilities = facilityRepository.findFacilitiesByHotelIds(hotelIds);

        return facilities.stream()
                .map(Facility::getName)
                .toList();
    }
}
