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
     "z3iafj":"MTEwOTUyMzQ3Ljc=",
     "f24sfvs":"MzE4NzY1ODYuOTY=",
     "g74jsdg":"MA==",
     "xgxg35":"MQ==",
     "y74hda":"MQ==",
     "zfgs62":"MTAxODk2NA==",
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
    private String z3iafj;

    @JsonProperty("f24sfvs")
    private String f24sfvs;

    @JsonProperty("g74jsdg")
    private String g74jsdg;

    @JsonProperty("xgxg35")
    private String xgxg35;

    @JsonProperty("y74hda")
    private String y74hda;

    @JsonProperty("zfgs62")
    private String zfgs62;

    @JsonProperty("rgqaca")
    private String rgqaca;

    @JsonProperty("rfs21d")
    private String rfs21d;

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

    public Double getZ3iafj() { // 531277
        return convertFromObfuscation(convertFromBase64(z3iafj));
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

    private Double convertFromObfuscation(String s) {
        return new Double(s) / 531277;
    }

    public void setZ3iafj(String z3iafj) {
        this.z3iafj = z3iafj;
    }

    public Double getF24sfvs() {
        return convertFromObfuscation(convertFromBase64(f24sfvs));
    }

    public void setF24sfvs(String f24sfvs) {
        this.f24sfvs = f24sfvs;
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

    public String getZfgs62() {
        return convertFromBase64(zfgs62);
    }

    public void setZfgs62(String zfgs62) {
        this.zfgs62 = zfgs62;
    }

    public String getRgqaca() {
        return convertFromBase64(rgqaca);
    }

    public void setRgqaca(String rgqaca) {
        this.rgqaca = rgqaca;
    }

    public String getRfs21d() {
        return convertFromBase64(rfs21d);
    }

    public void setRfs21d(String rfs21d) {
        this.rfs21d = rfs21d;
    }
}
