import {getApi} from "../config/apiClient.js";

export const getAllHotelsByReservationDateAndCityName = (cityName, checkInDate, checkOutDate, params) => {
    return getApi(`/api/hotels/getAllHotelsByResAndCity?cityName=${cityName}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&facilities=${params.toString()}`);
}