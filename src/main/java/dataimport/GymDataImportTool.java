package dataimport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.infrastructure.CSVGymDataReader;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

public class GymDataImportTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymDataImportTool.class);

    private static final TypeReference<Map<String, GymResponse>> gymsTypeReference =
            new TypeReference<Map<String, GymResponse>>() {
            };
    public static final String separator = ";";

    public static void main(String[] args) {
        if (args == null || args.length > 2 || args.length < 2) {
            System.out.println("Wrong use of the import tool! Run " + GymDataImportTool.class.getSimpleName() +
                    " with parameters: [side of map cube, in kilometres] [the location to search for]");
            throw new RuntimeException("Error!");
        }
        FileOutputStream fis = null;
        String location = null;
        try {
            location = StringUtils.join(ArrayUtils.remove(args, 0), " ");
            final Integer widthCube = new Integer(args[0]);
            String ann4Cookie = "1";
            String mapFiltersCookie = "1[##split##]1[##split##]1[##split##]0[##split##]0[##split##]1[##split##]1" +
                    "[##split##]0[##split##]1[##split##]1[##split##]1";
            String ann6Cookie = "1";
            String latlngZoomCookie = "14[##split##]59.84869731029538[##split##]17.579755254187045";
            final BasicCookieStore cookieStore = new BasicCookieStore();
            final CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setMaxConnPerRoute(10)
                    .setMaxConnTotal(10)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault()))
                    .setConnectionManager(new BasicHttpClientConnectionManager())
                    .setDefaultCookieStore(cookieStore)
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                    .disableAutomaticRetries()
                    .disableContentCompression()
                    .disableRedirectHandling()
                    .build();
            RestTemplate restTemplate;
            final HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            restTemplate = new RestTemplate(requestFactory);

            URI uri;
            String address = "https://www.pokemongomap.info";
            uri = new URI(address);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

            System.out.println("Response code for request to get cookies (200 = OK): " +
                    responseEntity.getStatusCode());
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Could not get session cookie from map site!");
            }

            final BasicClientCookie2 announcementnews6 = new BasicClientCookie2("announcementnews6", ann4Cookie);
            announcementnews6.setDomain("www.pokemongomap.info");
            announcementnews6.setPath("/");
            cookieStore.addCookie(announcementnews6);
            final BasicClientCookie2 mapfilters = new BasicClientCookie2("mapfilters", mapFiltersCookie);
            mapfilters.setDomain("www.pokemongomap.info");
            mapfilters.setPath("/");
            cookieStore.addCookie(mapfilters);
            final BasicClientCookie2 announcementnews8 = new BasicClientCookie2("announcementnews8", ann6Cookie);
            announcementnews8.setDomain("www.pokemongomap.info");
            announcementnews8.setPath("/");
            cookieStore.addCookie(announcementnews8);
            final BasicClientCookie2 latlngzoom = new BasicClientCookie2("latlngzoom", latlngZoomCookie);
            latlngzoom.setDomain("www.pokemongomap.info");
            latlngzoom.setPath("/");
            cookieStore.addCookie(latlngzoom);

            // Delays so we don't overload the site and get banned
            Thread.sleep(1000);

            address = "https://www.pokemongomap.info/includes/geocode.php";
            uri = new URI(address);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.put("searchloc", asList(location));
            body.put("fromlat", asList("59.854661870313116"));
            body.put("tolat", asList("59.85956994867656"));
            body.put("fromlng", asList("17.624897788330145"));
            body.put("tolng", asList("17.63279421166999"));
            final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("DNT", asList("1"));
            headers.put("Accept", asList("application/json", "text/javascript", "*/*; q=0.01"));
            headers.put("Content-Type", asList("application/x-www-form-urlencoded; charset=UTF-8"));
            headers.put("upgrade-insecure-requests", asList("1"));
            headers.put("Host", asList("www.pokemongomap.info"));
            headers.put("Origin", asList("https://www.pokemongomap.info"));
            headers.put("Referer", asList("www.pokemongomap.info/"));
            headers.put("Connection", asList("keep-alive"));
            headers.put("X-Requested-With", asList("XMLHttpRequest"));
            headers.put("Accept-Encoding", asList(""));
            headers.put("Accept-Language", asList("sv-SE", "sv;q=0.8", "en-US;q=0.6", "en;q=0.4", "nb;q=0.2", "de;q=0.2"));

            RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
            responseEntity = restTemplate.postForEntity(address, request, String.class);
            System.out.println("Response code for geocode request (200 = OK): " + responseEntity.getStatusCode());
            final String geoCodeResponseBody = responseEntity.getBody();
            System.out.println("Geocode response: " + geoCodeResponseBody);
            GeocodeResponse geocodeResponse = new ObjectMapper().readValue(geoCodeResponseBody, GeocodeResponse.class);

            Thread.sleep(2000);

            String pokeStopUrl = "https://www.pokemongomap.info/includes/it55nmsq9.php";
            address = new String(pokeStopUrl);
            headers.put("Accept", asList("*/*"));
            body = new LinkedMultiValueMap<>();
            final Double lat = new Double(geocodeResponse.getLat());
            final Double lng = new Double(geocodeResponse.getLng());

            Double cubeInDegrees = widthCube / 111.409d;
            body.put("fromlat", asList("" + (lat - cubeInDegrees)));
            body.put("tolat", asList("" + (lat + cubeInDegrees)));
            body.put("fromlng", asList("" + (lng - cubeInDegrees)));
            body.put("tolng", asList("" + (lng + cubeInDegrees)));
            body.put("fpoke", asList("0")); // 1 = include pokestops
            body.put("fgym", asList("1")); // 1 = include gyms
            body.put("farm", asList("0")); // 1 = ??
            body.put("nests", asList("0")); // 1 = include nest locations (don't use this)
            body.put("raid", asList("1")); // 1 = include raids
            request = new RequestEntity<>(body, headers, HttpMethod.POST, new URI(pokeStopUrl));
            responseEntity = restTemplate.postForEntity(address, request, String.class);

            System.out.println("Response code for gym query request (200 = OK): " + responseEntity.getStatusCode());
            final String gymsJson = responseEntity.getBody();
            if (gymsJson.equals("[]")) {
                throw new RuntimeException("Empty result set. Try and use different parameters..");
            }
            final Map<String, GymResponse> gymsMap = new ObjectMapper().readValue(gymsJson, gymsTypeReference);
            GymsResponse response = new GymsResponse(gymsMap);
            System.out.println("Read " + response.getGyms().size() + " gyms from this query.");
            final String fileName = "target/" + location + ".csv";
            fis = new FileOutputStream(fileName);
            fis.write(("ID" + separator + "LOCATION" + separator + "NAME" + separator + "AREA\n")
                    .getBytes("UTF-8"));
            System.out.println("Saving file: " + fileName);
            for (String id : response.getGyms().keySet()) {
                GymResponse gym = response.getGyms().get(id);
                if (gym == null) {
                    throw new RuntimeException("Found null gym for id " + id);
                }
                fis.write((id + separator + "\"" + gym.getLatitude() + separator + gym.getLongitude() + "\"" +
                        separator + gym.getName() +
                         separator + StringUtils.left(location, 1).toUpperCase() +
                        location.substring(1, location.length())+ "\n").getBytes("UTF-8"));
            }
            System.out.println("File: " + fileName + " saved.");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            CSVGymDataReader oldGymDataReader = new CSVGymDataReader("/gyms_" + location + ".csv");
            CSVGymDataReader newGymDataReader = new CSVGymDataReader(new FileInputStream("target/" + location + ".csv"));
            final Set<Gym> oldGyms = oldGymDataReader.readAll();
            final Set<Gym> newGyms = newGymDataReader.readAll();
            int sameCount = 0;
            LinkedList<String> report = new LinkedList<>();
            LinkedList<String> focusReport = new LinkedList<>();
            for (Gym newGym : newGyms) {
                if (!oldGyms.contains(newGym)) {
                    boolean weird = false;
                    for (Gym oldGym : oldGyms) {
                        if (oldGym.getX().equals(newGym.getX()) && oldGym.getY().equals(newGym.getY())) {
                            focusReport.add("New name for old gym? Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        } else if (oldGym.getId().equals(newGym.getId())) {
                            focusReport.add("Reused ID for old gym? Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        } else if (oldGym.getName().trim().equalsIgnoreCase(newGym.getName().trim())) {
                            focusReport.add("Potential duplicate. Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        }
                    }
                    if (!weird) {
                        focusReport.add("New gym: " + newGym);
                    }
                } else {
                    sameCount++;
                }
            }

            for (Gym oldGym : oldGyms) {
                boolean weird = false;
                if (!newGyms.contains(oldGym)) {
                    for (Gym newGym : newGyms) {
                        if (oldGym.getX().equals(newGym.getX()) && oldGym.getY().equals(newGym.getY())) {
                            LOGGER.debug("OLD: New name for old gym? Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        } else if (oldGym.getId().equals(newGym.getId())) {
                            LOGGER.debug("OLD: Reused ID for old gym? Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        } else if (oldGym.getName().equalsIgnoreCase(newGym.getName())) {
                            LOGGER.debug("OLD: Potential duplicate. Old: " + oldGym + " - New: " + newGym);
                            weird = true;
                        }
                    }
                    if (!weird) {
                        report.add("Removed gym: " + oldGym);
                    }
                } else {
                    report.add("Exists in old and new: " + oldGym);
                }
            }

            for (String r : report) {
                System.out.println(r);
            }
            for (String r : focusReport) {
                System.out.println(r);
            }
            System.out.println("\nREPORT:\n\nOld gyms: " + oldGyms.size() + ", new gyms: " + newGyms.size() +
                    ", diff: " + (newGyms.size() - oldGyms.size()) + ", same in both: " + sameCount);
        } catch (Throwable t) {
            System.out.printf("Could not perform a diff between old gym file and new one: " + t.getMessage());
        }
    }
}
