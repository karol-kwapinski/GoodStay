import {getApi} from "../config/apiClient.js";

export const getFacilities = (params) => {
    return getApi(`api/facilities/getFacilities?${params.toString()}`)
}