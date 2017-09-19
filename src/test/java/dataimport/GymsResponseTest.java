package dataimport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GymsResponseTest {
    private static final String json = "{\n" +
            "  \"1018964\": {\n" +
            "    \"raid_status\": 0,\n" +
            "    \"raid_timer\": 0,\n" +
            "    \"raid_level\": 0,\n" +
            "    \"lure_timer\": 0,\n" +
            "    \"z3iafj\": \"MTEwOTUyMzQ3Ljc=\",\n" +
            "    \"f24sfvs\": \"MzE4NzY1ODYuOTY=\",\n" +
            "    \"g74jsdg\": \"MA==\",\n" +
            "    \"xgxg35\": \"MQ==\",\n" +
            "    \"y74hda\": \"MQ==\",\n" +
            "    \"zfgs62\": \"MTAxODk2NA==\",\n" +
            "    \"rgqaca\": \"jarlasa-barhus\",\n" +
            "    \"rfs21d\": \"J\\u00e4rl\\u00e5sa B\\u00e5rhus\"\n" +
            "  },\n" +
            "  \"49424655\": {\n" +
            "    \"raid_status\": 0,\n" +
            "    \"raid_timer\": 0,\n" +
            "    \"raid_level\": 0,\n" +
            "    \"lure_timer\": 0,\n" +
            "    \"z3iafj\": \"MTEwOTUxMTEyLjQxNg==\",\n" +
            "    \"f24sfvs\": \"MzE4NzA4NjIuNDI4\",\n" +
            "    \"g74jsdg\": \"MA==\",\n" +
            "    \"xgxg35\": \"MQ==\",\n" +
            "    \"y74hda\": \"MQ==\",\n" +
            "    \"zfgs62\": \"NDk0MjQ2NTU=\",\n" +
            "    \"rgqaca\": \"jarlasa-milsten\",\n" +
            "    \"rfs21d\": \"J\\u00e4rl\\u00e5sa Milsten\"\n" +
            "  },\n" +
            "  \"118412\": {\n" +
            "    \"raid_status\": 0,\n" +
            "    \"raid_timer\": 0,\n" +
            "    \"raid_level\": 0,\n" +
            "    \"z3iafj\": \"MTEwOTUxODg4LjQwNA==\",\n" +
            "    \"f24sfvs\": \"MzE4NzgyNTkuMzE2\",\n" +
            "    \"g74jsdg\": \"MQ==\",\n" +
            "    \"xgxg35\": \"Mg==\",\n" +
            "    \"y74hda\": \"Mg==\",\n" +
            "    \"zfgs62\": \"MTE4NDEy\",\n" +
            "    \"rgqaca\": \"jarlasa-kyrka\",\n" +
            "    \"rfs21d\": \"J\\u00e4rl\\u00e5sa Kyrka\"\n" +
            "  }\n" +
            "}";

    @Test
    public void convertJson() throws Exception {
        GymsResponse response = new GymsResponse(new ObjectMapper().readValue(json, new TypeReference<Map<String, GymResponse>>(){}));
        assertThat(response != null, is(true));
        assertThat(response.getGyms().size(), is(3));
        final GymResponse firstGym = response.getGyms().values().iterator().next();
        final Double coord = new Double(firstGym.getF24sfvs());
        for (int i = 1; i < 100000000; i++) {
            Double c = coord / i;
            if ((c < 60d && c > 59d) || (c < 18d && c > 17d)) {
                System.out.println("Divider is: " + i);
                break;
            }
        }
        // 31876586.96
        //"markerlat":59.909227,"markerlng":17.212883
        assertThat(firstGym.getF24sfvs(), is(new Double("59.909227")));
        assertThat(firstGym.getG74jsdg(), is(""));
        assertThat(firstGym.getRfs21d(), is(""));
        assertThat(firstGym.getRgqaca(), is(""));
        assertThat(firstGym.getXgxg35(), is(""));
        assertThat(firstGym.getY74hda(), is(""));
        assertThat(firstGym.getZ3iafj(), is(""));
        assertThat(firstGym.getZfgs62(), is(""));
        assertThat(firstGym.getLureTimer(), is(0));
        assertThat(firstGym.getRaidLevel(), is(0));
        assertThat(firstGym.getRaidStatus(), is(0));
        assertThat(firstGym.getRaidTimer(), is(0));
    }
}
