package dataimport.pokemonapi;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class PokemonImportTool {
    private static final String baseUrl = "https://pokeapi.co/api/v2/pokemon-species/";

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
            for (int i = 1; i < 357; i++) {
                final String url = baseUrl + i;
                URI uri = new URI(url);
                final int number = i;
                RestTemplate restTemplate = new RestTemplate(requestFactory);
                final ResponseEntity<String> jsonResult = restTemplate.getForEntity(uri, String.class);
                final String body = jsonResult.getBody();
                sb.append(body).append("\n");
                System.out.println("Pokemon " + number + " read: " + body);
            }

        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
        File file = new File("./target/pokemons.json");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(sb.toString().replaceAll("\\\\", "").getBytes("UTF-8"));
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
