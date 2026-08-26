package org.goodstay.service;

import java.util.List;

public interface FacilityService {
    List<String> getFacilities(List<Long> hotelIds);
}
