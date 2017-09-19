package dataimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GeocodeResponseTest {
    @Test
    public void convertFromJson() throws Exception {
        String json = "{\"lat\":\"59.8594126\",\"lng\":\"17.64112\",\"class\":\"place\",\"type\":\"city\"," +
                "\"display_name\":\"Uppsala, Landskapet Uppland, Uppsala l\\u00e4n, Svealand, Sverige\",\"success\":1}";
        ObjectMapper mapper = new ObjectMapper();
        final GeocodeResponse response = mapper.readValue(json, GeocodeResponse.class);
        assertThat(response != null, is(true));
        assertThat(response.getLat(), is("59.8594126"));
    }
}
