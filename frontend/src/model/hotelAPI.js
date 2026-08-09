import {postApi} from "../config/apiClient.js";

export const getAllHotelsByReservationDateAndCityName = (form) => {
    return postApi("/api/hotels/getAllHotelsByResAndCity", form);
}