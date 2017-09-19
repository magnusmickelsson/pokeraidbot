package dataimport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class GymDataImportTool {
    private static final TypeReference<Map<String, GymResponse>> gymsTypeReference =
            new TypeReference<Map<String, GymResponse>>() {};

    public static void main(String[] args) {
        try {
//            String sessionIdCookie = "v7qgqsl9f098feg65qh94vpa33";
            String ann4Cookie = "1";
            String mapFiltersCookie = "1[##split##]1[##split##]1[##split##]0[##split##]0[##split##]1[##split##]1[##split##]0[##split##]1[##split##]1[##split##]1";
            String ann6Cookie = "1";
            String latlngZoomCookie = "14[##split##]59.84869731029538[##split##]17.579755254187045";
            final DefaultHttpClient httpClient = new DefaultHttpClient();
            RestTemplate restTemplate;
            final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//            final BufferingClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(requestFactory);
//            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//            interceptors.add(new LoggingRequestInterceptor());
            restTemplate = new RestTemplate(requestFactory);
//            restTemplate.setInterceptors(interceptors);
            final BasicCookieStore cookieStore = new BasicCookieStore();
            httpClient.setCookieStore(cookieStore);

            URI uri;
            String address = "https://www.pokemongomap.info";
            uri = new URI(address);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

            System.out.println("Response code for main html: " + responseEntity.getStatusCode());
            final List<String> cookiesFromServer = responseEntity.getHeaders().get("Set-Cookie");
            final String[] cookiesSplit = cookiesFromServer.iterator().next().split(";");
            final String[] sessionCookie = cookiesSplit[0].split("=");
            if (sessionCookie[0] == null || (!sessionCookie[0].equalsIgnoreCase("PHPSESSID"))) {
                throw new RuntimeException("Couldn't get a session cookie from server!");
            }

            String sessionIdCookie = sessionCookie[1];

            cookieStore.addCookie(new BasicClientCookie2("PHPSESSID", sessionIdCookie));
            cookieStore.addCookie(new BasicClientCookie2("announcementnews4", ann4Cookie));
            cookieStore.addCookie(new BasicClientCookie2("mapfilters", mapFiltersCookie));
            cookieStore.addCookie(new BasicClientCookie2("announcementnews6", ann6Cookie));
            cookieStore.addCookie(new BasicClientCookie2("latlngzoom", latlngZoomCookie));

            Thread.sleep(1000);

            address = "https://www.pokemongomap.info/includes/geocode.php";
            uri = new URI(address);
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.put("searchloc", asList("Ängelholm"));
            final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("DNT", asList("1"));
            headers.put("Accept", asList("application/json", "text/javascript", "*/*; q=0.01"));
            headers.put("Content-Type", asList("application/x-www-form-urlencoded; charset=UTF-8"));
            headers.put("Host", asList("www.pokemongomap.info"));
            headers.put("Origin", asList("https://www.pokemongomap.info"));
            headers.put("Referer", asList("www.pokemongomap.info/"));
            headers.put("Connection", asList("keep-alive"));
            headers.put("X-Requested-With", asList("XMLHttpRequest"));
            headers.put("User-Agent", asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36"));
            headers.put("Accept-Encoding", asList(""));
            headers.put("Accept-Language", asList("sv-SE","sv;q=0.8","en-US;q=0.6","en;q=0.4","nb;q=0.2","de;q=0.2"));

            RequestEntity<MultiValueMap<String, String>> request = new RequestEntity<>(body, headers, HttpMethod.POST, uri);
            responseEntity = restTemplate.postForEntity(address, request, String.class);
            System.out.println("Response code: " + responseEntity.getStatusCode());
            final String geoCodeResponseBody = responseEntity.getBody();
            System.out.println(geoCodeResponseBody);
            GeocodeResponse geocodeResponse = new ObjectMapper().readValue(geoCodeResponseBody, GeocodeResponse.class);

            Thread.sleep(2000);

            String pokeStopUrl = "https://www.pokemongomap.info/includes/uy22ewsd1.php";
            address = new String(pokeStopUrl);
            headers.put("Accept", asList("*/*"));
            body = new LinkedMultiValueMap<>();
            final HashMap<String, Object> uriVariables = new HashMap<>();
            final Double lat = new Double(geocodeResponse.getLat());
            final Double lng = new Double(geocodeResponse.getLng());
            body.put("fromlat", asList("59.89893308802174"));
            body.put("tolat", asList("59.91941685254541"));
            body.put("fromlng", asList("17.195944785639313"));
            body.put("tolng", asList("17.22976207689908"));
//            body.put("fromlat", asList("" + (lat - 0.02d)));
//            body.put("tolat", asList("" + (lat + 0.02d)));
//            body.put("fromlng", asList("" + (lng - 0.02d)));
//            body.put("tolng", asList("" + (lng + 0.02d)));
            body.put("fpoke", asList("1"));
            body.put("fgym", asList("1"));
            body.put("farm", asList("0"));
            body.put("nests", asList("0"));
            body.put("raid", asList("1"));
            request = new RequestEntity<>(body, headers, HttpMethod.POST, new URI(pokeStopUrl));
            responseEntity = restTemplate.postForEntity(address, request, String.class);

            System.out.println("Response code: " + responseEntity.getStatusCode());
            final String gymsJson = responseEntity.getBody();
            System.out.println(gymsJson);
            final Map<String, GymResponse> gymsMap = new ObjectMapper().readValue(gymsJson, gymsTypeReference);
            GymsResponse response = new GymsResponse(gymsMap);
            System.out.println("Read " + response.getGyms().size() + " gyms from this query.");

        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
