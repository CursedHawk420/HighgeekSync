package eu.highgeek.highgeeksync.objects;

import com.google.gson.Gson;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.utils.ConfigManager;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusCode {
    public String action;
    public String message;
    public String playerMessage;
    public String stacktrace;
    public String dateTime;
    public String serverName = ConfigManager.getString("chat.servername");
    public String playerName;
    public String errorUuid;

    public StatusCode(String playerMessage){
        this.playerMessage = playerMessage;
    }

    public StatusCode(String action, String message, String stacktrace, String playerName){
        this.action = action;
        this.message = message;
        this.stacktrace = stacktrace;
        this.dateTime = LocalDateTime.now().toString();
        this.playerName = playerName;
        this.errorUuid = UUID.randomUUID().toString();
        this.playerMessage = "Something went wrong, contact administrator with this code:\n " + this.errorUuid;
        setErrorInRedis();
    }

    public void setErrorInRedis(){
        RedisManager.setRedis("errors:java:"+ serverName + ":" + errorUuid, new Gson().toJson(this, StatusCode.class));
    }
}
