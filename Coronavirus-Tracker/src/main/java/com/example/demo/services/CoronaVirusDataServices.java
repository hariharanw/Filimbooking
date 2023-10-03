package com.example.demo.services;

import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.demo.model.LocationStates;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CoronaVirusDataServices {

    private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final Logger LOGGER = Logger.getLogger(CoronaVirusDataServices.class.getName());

    private List<LocationStates> allStats = new ArrayList<>();

    public List<LocationStates> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() {
        try {
            List<LocationStates> newStats = new ArrayList<>();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            StringReader csvBodyReader = new StringReader(httpResponse.body());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
            for (CSVRecord record : records) {
                LocationStates locationStat = new LocationStates();
                locationStat.setState(record.get("Province/State"));
                locationStat.setCountry(record.get("Country/Region"));
               
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStat.setLatestTotalDeaths(latestCases);
                locationStat.setDifferFromPrevDay(latestCases - prevDayCases);
                newStats.add(locationStat);
            }
            this.allStats = newStats;
        } catch (SSLHandshakeException e) {
            LOGGER.log(Level.SEVERE, "SSL Handshake Exception occurred", e);
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "IO Exception or Interrupted Exception occurred", ex);
        }
    }

	
}