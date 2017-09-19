package dataimport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@JsonAutoDetect
public class GymResponse {
    /**
    "raid_status":0,
     "raid_timer":0,
     "raid_level":0,
     "lure_timer":0,
     "latitude":"MTEwOTUyMzQ3Ljc=",
     "longitude":"MzE4NzY1ODYuOTY=",
     "g74jsdg":"MA==",
     "xgxg35":"MQ==",
     "y74hda":"MQ==",
     "id":"MTAxODk2NA==",
     "rgqaca":"jarlasa-barhus",
     "rfs21d":"J\u00e4rl\u00e5sa B\u00e5rhus"
     */
    @JsonProperty("raid_status")
    private Integer raidStatus;

    @JsonProperty("raid_timer")
    private Integer raidTimer;

    @JsonProperty("raid_level")
    private Integer raidLevel;

    @JsonProperty("lure_timer")
    private Integer lureTimer;

    @JsonProperty("z3iafj")
    private String latitude;

    @JsonProperty("f24sfvs")
    private String longitude;

    @JsonProperty("g74jsdg")
    private String g74jsdg;

    @JsonProperty("xgxg35")
    private String xgxg35;

    @JsonProperty("y74hda")
    private String y74hda;

    @JsonProperty("zfgs62")
    private String id;

    @JsonProperty("rgqaca")
    private String rgqaca;

    @JsonProperty("rfs21d")
    private String name;

    public GymResponse() {
    }

    public Integer getRaidStatus() {
        return raidStatus;
    }

    public void setRaidStatus(Integer raidStatus) {
        this.raidStatus = raidStatus;
    }

    public Integer getRaidTimer() {
        return raidTimer;
    }

    public void setRaidTimer(Integer raidTimer) {
        this.raidTimer = raidTimer;
    }

    public Integer getRaidLevel() {
        return raidLevel;
    }

    public void setRaidLevel(Integer raidLevel) {
        this.raidLevel = raidLevel;
    }

    public Integer getLureTimer() {
        return lureTimer;
    }

    public void setLureTimer(Integer lureTimer) {
        this.lureTimer = lureTimer;
    }

    public Double getLatitude() { // 531277
        return convertLat(convertFromBase64(latitude));
    }

    private String convertFromBase64(String s) {
        String result;
        try {
            result = new String(Base64.decodeBase64(s.getBytes("UTF-8")), Charset.forName("UTF-8"));
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Double convertLat(String s) {
        return new Double(s) / 1852000;
    }

    private Double convertLong(String s) {
        return new Double(s) / 1852000;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return convertLong(convertFromBase64(longitude));
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getG74jsdg() {
        return convertFromBase64(g74jsdg);
    }

    public void setG74jsdg(String g74jsdg) {
        this.g74jsdg = g74jsdg;
    }

    public String getXgxg35() {
        return convertFromBase64(xgxg35);
    }

    public void setXgxg35(String xgxg35) {
        this.xgxg35 = xgxg35;
    }

    public String getY74hda() {
        return convertFromBase64(y74hda);
    }

    public void setY74hda(String y74hda) {
        this.y74hda = y74hda;
    }

    public String getId() {
        return convertFromBase64(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRgqaca() {
        return rgqaca;
    }

    public void setRgqaca(String rgqaca) {
        this.rgqaca = rgqaca;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "GymResponse{" +
                "raidStatus=" + raidStatus +
                ", raidTimer=" + raidTimer +
                ", raidLevel=" + raidLevel +
                ", lureTimer=" + lureTimer +
                ", latitude='" + getLatitude() + '\'' +
                ", longitude='" + getLongitude() + '\'' +
                ", g74jsdg='" + getG74jsdg() + '\'' +
                ", xgxg35='" + getXgxg35() + '\'' +
                ", y74hda='" + getY74hda() + '\'' +
                ", id='" + getId() + '\'' +
                ", rgqaca='" + getRgqaca() + '\'' +
                ", rfs21d='" + getName() + '\'' +
                '}';
    }
}
