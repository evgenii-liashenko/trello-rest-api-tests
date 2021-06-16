package core.api;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import beans.TrelloBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import utils.AccessDataLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import static constants.Parameters.*;


public class TrelloBoardsApi extends BaseTrelloApi {

    //Endpoint URL
    public static final URI TRELLO_BOARD_URL = URI.create(AccessDataLoader.getApiData().getProperty(BOARD_URL));

    //Constructor
    public TrelloBoardsApi(Method requestMethod, Map<String, String> pathParameters, Map<String, String> queryParameters) {
        super(requestMethod, pathParameters, queryParameters);
    }

    //Builder implementation
    public static class TrelloBoardRequestBuilder {
        private Method requestMethod = Method.GET;      //Default method in case no method is set
        private Map<String, String> queryParams = new HashMap<>();
        private Map<String, String> pathParams = new LinkedHashMap<>();


        public TrelloBoardRequestBuilder setMethod (Method method){
            requestMethod = method;
            return this;
        }

        public TrelloBoardRequestBuilder setId(String boardId){
            pathParams.put(BOARD_ID, boardId);
            return this;
        }

        public TrelloBoardRequestBuilder setName (String boardName){
            queryParams.put(BOARD_NAME, boardName);
            return this;
        }

        public TrelloBoardRequestBuilder setDescription (String boardDescription){
            queryParams.put(BOARD_DESCRIPTION, boardDescription);
            return this;
        }

        public TrelloBoardRequestBuilder setIsClosed(Boolean boardStatus){
            queryParams.put(IS_CLOSED, boardStatus.toString());
            return this;
        }

        public TrelloBoardsApi buildRequest() {
            return new TrelloBoardsApi(requestMethod, pathParams, queryParams);
        }
    }

    //Builder instantiation
    public static TrelloBoardRequestBuilder trelloBoardRequestBuilder() {
        return new TrelloBoardRequestBuilder();
    }

    //API interaction
    public Response sendRequest() {
        String pathStr = "";
        for (String key : pathParameters.keySet()) {
            pathStr += "{" + key + "}/";
        }
        return RestAssured
                .given(requestSpecification()).log().all()
                .header("Authorization", "OAuth oauth_consumer_key=\"" + TRELLO_CONSUMER_KEY + "\", oauth_token=\"" + TRELLO_ACCESS_TOKEN + "\"")
                .pathParams(pathParameters)
                .queryParams(queryParameters)
                .request(requestMethod ,pathStr)
                .prettyPeek();
    }

    //Conversion of deserialized json from Response to TrelloBoard
    public static TrelloBoard makeBoardObject(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<TrelloBoard>() {}.getType());
    }

    //Request specification
    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .setBaseUri(TRELLO_BOARD_URL)
                .build();
    }

}
