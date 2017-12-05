package dataimport.pokemonapi;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.Arrays.asList;

public class PokemonImportTool {
    private static final String baseUrl = "https://pokeapi.co/api/v2/pokemon/";

    public PokemonImportTool() {
    }

    public static HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContexts.custom().build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                new String[]{"TLSv1.2", "TLSv1.1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(100)
                .build();
    }

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        try {
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,TLSv1");
            final HttpClient httpClient = getHttpClient();

            final HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.put("Accept", asList("application/json", "text/javascript", "*/*; q=0.01"));
            headers.put("Connection", asList("keep-alive"));
            headers.put("X-Requested-With", asList("XMLHttpRequest"));
            headers.put("User-Agent", asList("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, " +
                    "like Gecko) Chrome/60.0.3112.113 Safari/537.36"));
            headers.put("Accept-Encoding", asList("UTF-8"));
            headers.put("Accept-Language", asList("en-US;q=0.6","en;q=0.4"));

            for (int i = 350; i < 351; i++) {
                final String url = baseUrl + i;
                URI uri = new URI(url);
                final RequestEntity<Void> requestEntity = RequestEntity.get(uri).acceptCharset(Charset.forName("UTF-8"))
                        .accept(MediaType.APPLICATION_JSON_UTF8).header("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, "+
                                "like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                        .header("Accept-Language", "en-US;q=0.6","en;q=0.4")
                        .build();

                final ResponseEntity<String> jsonResult = restTemplate.exchange(requestEntity, String.class);
                final String body = jsonResult.getBody().replaceAll("[\\\\][/]", "/");
                sb.append(body).append("\n");
                System.out.println("Pokemon " + i + " read: " + body);
            }

        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
        File file = new File("./target/pokemons.json");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes("UTF-8"));
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
