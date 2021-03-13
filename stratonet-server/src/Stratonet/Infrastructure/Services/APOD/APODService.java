package Stratonet.Infrastructure.Services.APOD;

import Stratonet.Core.Helpers.StratonetLogger;
import Stratonet.Core.Models.APODResponse;
import Stratonet.Core.Services.APOD.IAPODService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

public class APODService implements IAPODService
{

    private StratonetLogger logger;
    private HttpClient client;

    private final String API_KEY = "8pstY7crqf5jsEGDjZugwZh9n0HpxodgKMzPm19A";
    private final String APOD_ENDPOINT = "https://api.nasa.gov/planetary/apod";

    public APODService()
    {
        logger = StratonetLogger.getInstance();
        client = HttpClient.newHttpClient();
    }

    public APODResponse getAPODImage(String date)
    {
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(APOD_ENDPOINT + "?api_key=" + API_KEY +"&date=" + date))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            APODResponse apodResponse = objectMapper.readValue(response.body(), APODResponse.class);

            return apodResponse;
        }
        catch (Exception ex)
        {
            logger.getInstance().log(Level.WARNING, "Exception while fetching APOD API");
        }

        return null;
    }

}
