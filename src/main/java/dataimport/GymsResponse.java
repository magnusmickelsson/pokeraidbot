package dataimport;

import java.util.HashMap;
import java.util.Map;

public class GymsResponse {
    private Map<String, GymResponse> gyms = new HashMap<>();

    public GymsResponse() {
    }

    public GymsResponse(Map<String, GymResponse> valueMap) {
        setGyms(valueMap);
    }

    public Map<String, GymResponse> getGyms() {
        return gyms;
    }

    public void setGyms(Map<String, GymResponse> gyms) {
        this.gyms = gyms;
    }
}
