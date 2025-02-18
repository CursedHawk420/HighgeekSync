package eu.highgeek.highgeeksync.data.sql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

import java.math.BigInteger;
import java.time.Instant;

@Entity
@Table(name="discord_codes"
        ,catalog="mcserver_maindb"
)
public class DiscordLinkingCode implements java.io.Serializable {

    private String code;
    private String uuid;
    private long expiration;

    public DiscordLinkingCode() {}

    public DiscordLinkingCode(String code){
        this.code = code;
    }

    public DiscordLinkingCode(String code, String playerUuid){
        this.code = code;
        this.uuid = playerUuid;
        this.expiration = Instant.now().toEpochMilli() + 172800000;
    }


    @Id



    @Column(name="code", unique=true, nullable=false, length=6)
    public String getCode() {
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    @Column(name="uuid", length=36)
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    @Column(name = "expiration", length = 20)
    public long getExpiration(){
        return this.expiration;
    }
    public void setExpiration(long expiration){
        this.expiration = expiration;
    }

}
