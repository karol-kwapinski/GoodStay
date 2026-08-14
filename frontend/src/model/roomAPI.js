import {getApi} from "../config/apiClient.js";

export const getAllRoomsByDatesAndHotelId = (hotelId, checkInDate, checkOutDate) => {
    return getApi(`api/rooms/getAllRoomsByDatesAndHotelId/${hotelId}?checkInDate=${checkInDate}&checkOutDate=${checkOutDate}`);
}