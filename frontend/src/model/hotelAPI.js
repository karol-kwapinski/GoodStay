import {getApi, postApi} from "../config/apiClient.js";

export const getAllHotelsByReservationDateAndCityName = (cityName, checkInDate, checkOutDate, params) => {
    return getApi(`/api/hotels/getAllHotelsByResAndCity?cityName=${cityName}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&facilities=${params.toString()}`);
}

export const getHotelById = (hotelId) => {
    return getApi(`/api/hotels/getHotel/${hotelId}`);
}

export const addHotel = (data) => {
    return postApi('/api/hotels/addHotel', data);
}

export const getHotels = (pageNumber, pageSize) => {
    return getApi(`/api/hotels/getHotels?pageNumber=${pageNumber}&pageSize=${pageSize}`);
}