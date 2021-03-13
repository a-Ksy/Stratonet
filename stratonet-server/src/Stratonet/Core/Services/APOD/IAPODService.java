package Stratonet.Core.Services.APOD;

import Stratonet.Core.Models.APODResponse;

public interface IAPODService {
    APODResponse getAPODImage(String date);
}
