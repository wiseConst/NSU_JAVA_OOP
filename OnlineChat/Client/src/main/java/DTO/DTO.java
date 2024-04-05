package DTO;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Data
@Builder
@Jacksonized
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
public class DTO implements Serializable {

    public enum Type {
        REQUEST_TYPE, // CLIENT
        RESPONSE_TYPE // SERVER
    }

    public enum Kind {
        LOGIN_KIND,
        USER_LIST_KIND,
        NEW_MESSAGE_KIND,
        LOGOUT_KIND
    }

    public enum Result {
        NONE_RESULT,
        SUCCESS_RESULT,
        ERROR_RESULT
    }

    private Type type;
    private Kind kind;
    private Result result;
    private String username;
    private String message;

    @JsonCreator
    private DTO(@JsonProperty("type") Type type,
                @JsonProperty("kind") Kind kind,
                @JsonProperty("result") Result result,
                @JsonProperty("username") String username,
                @JsonProperty("message") String message) {
        this.type = type;
        this.kind = kind;
        this.result = result;
        this.username = username;
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }

    public final String getUsername() {
        return username;
    }


    public boolean isSuccessResult() {
        return result == Result.SUCCESS_RESULT;
    }

    public boolean isErrorResult() {
        return result == Result.ERROR_RESULT;
    }

    // Client
    public static DTO getLoginRequest(String username) {
        return new DTO(Type.REQUEST_TYPE, Kind.LOGIN_KIND, Result.NONE_RESULT, username, null); // Request type doesn't require other info, but username.
    }

    public static DTO getNewMessageRequest(String username, String message) {
        return new DTO(Type.REQUEST_TYPE, Kind.NEW_MESSAGE_KIND, Result.NONE_RESULT, username, message);
    }

    public static DTO getLogoutRequest(String username) {
        return new DTO(Type.REQUEST_TYPE, Kind.LOGOUT_KIND, Result.NONE_RESULT, username, null);
    }

    public static DTO getUserListRequest(String username) {
        return new DTO(Type.REQUEST_TYPE, Kind.USER_LIST_KIND, Result.NONE_RESULT, username, null);
    }


    // Server
    public static DTO getLoginResponse(Result inResult, String username, String message) {
        return new DTO(Type.RESPONSE_TYPE, Kind.LOGIN_KIND, inResult, username, message); // Request type doesn't require other info, but username.
    }

    public static DTO getNewMessageResponse(String username, String message) {
        return new DTO(Type.RESPONSE_TYPE, Kind.NEW_MESSAGE_KIND, Result.NONE_RESULT, username, message);
    }

    public static DTO getLogoutResponse(Result inResult, String username, String message) {
        return new DTO(Type.RESPONSE_TYPE, Kind.LOGOUT_KIND, inResult, username, message);
    }

    public static DTO getUserListResponse(String username, String message) {
        return new DTO(Type.RESPONSE_TYPE, Kind.USER_LIST_KIND, Result.NONE_RESULT, username, message);
    }

    public boolean isLoginRequest() {
        return type == Type.REQUEST_TYPE && kind == Kind.LOGIN_KIND;
    }

    public boolean isLoginResponse() {
        return type == Type.RESPONSE_TYPE && kind == Kind.LOGIN_KIND;
    }

    public boolean isMessageRequest() {
        return type == Type.REQUEST_TYPE && kind == Kind.NEW_MESSAGE_KIND;
    }

    public boolean isLogoutRequest() {
        return type == Type.REQUEST_TYPE && kind == Kind.LOGOUT_KIND;
    }

    public boolean isUserListRequest() {
        return type == Type.REQUEST_TYPE && kind == Kind.USER_LIST_KIND;
    }

    public boolean isMessageResponse() {
        return type == Type.RESPONSE_TYPE && kind == Kind.NEW_MESSAGE_KIND;
    }

    public boolean isLogoutResponse() {
        return type == Type.RESPONSE_TYPE && kind == Kind.LOGOUT_KIND;
    }

    public boolean isUserListResponse() {
        return type == Type.RESPONSE_TYPE && kind == Kind.USER_LIST_KIND;
    }

}
