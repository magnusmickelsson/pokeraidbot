package dataimport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties("onesec")
public class GeocodeResponse {
    @JsonProperty("lat")
    private String lat;
    @JsonProperty("lng")
    private String lng;
    @JsonProperty("class")
    private String clas;
    @JsonProperty("type")
    private String type;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("success")
    private Boolean success;

    public GeocodeResponse() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getClas() {
        return clas;
    }

    public void setClas(String clas) {
        this.clas = clas;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "GeocodeResponse{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", clas='" + clas + '\'' +
                ", type='" + type + '\'' +
                ", displayName='" + displayName + '\'' +
                ", success=" + success +
                '}';
    }
}
