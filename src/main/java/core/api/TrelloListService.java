package core.api;

import beans.TrelloList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.AccessDataLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static constants.Parameters.*;

public class TrelloListService extends BaseTrelloService {

    //Endpoint URL
    public static final URI TRELLO_LIST_URL = URI.create(AccessDataLoader.getApiData().getProperty(LIST_URL));

    //Constructor
    public TrelloListService(Method requestMethod, Map<String, String> pathParameters, Map<String, String> queryParameters) {
        super(requestMethod, pathParameters, queryParameters);
    }

    //Builder implementation
    public static class TrelloListRequestBuilder {
        private Method requestMethod = Method.GET;      //Default method in case no method is set
        private Map<String, String> queryParams = new HashMap<>();
        private Map<String, String> pathParams = new LinkedHashMap<>();

        public TrelloListRequestBuilder setMethod(Method method){
            requestMethod = method;
            return this;
        }

        public TrelloListRequestBuilder setListId(String listId){
            pathParams.put(LIST_ID, listId);
            return this;
        }

        public TrelloListRequestBuilder setClosed(String isClosed){
            queryParams.put(LIST_IS_CLOSED, isClosed);
            return this;
        }

        public TrelloListRequestBuilder setBoardId(String boardId){
            queryParams.put(LIST_BOARD_ID, boardId);
            return this;
        }

        public TrelloListRequestBuilder setName(String listName){
            queryParams.put(LIST_NAME, listName);
            return this;
        }

        public TrelloListService buildRequest(){
            return new TrelloListService(requestMethod, pathParams, queryParams);
        }
    }

    //Builder instantiation
    public static TrelloListRequestBuilder trelloListRequestBuilder(){
        return new TrelloListRequestBuilder();
    }

    //API interaction
    public Response sendRequest() {
        String pathStr = "";
        for (String key : pathParameters.keySet()) {        //This works, but I am not sure that the produced order will always be correct for multiple parameters
            pathStr += "{" + key + "}/";
        }
        return RestAssured
                .given(requestSpecificationLs()).log().all()
                .header("Authorization", "OAuth oauth_consumer_key=\"" + TRELLO_CONSUMER_KEY + "\", oauth_token=\"" + TRELLO_ACCESS_TOKEN + "\"")
                .pathParams(pathParameters)
                .queryParams(queryParameters)
                .request(requestMethod, pathStr)
                .prettyPeek();
    }

    //Conversion of deserialized json from Response to TrelloList
    public static TrelloList makeListObject(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<TrelloList>() {}.getType());
    }

    //Request specification
    public static RequestSpecification requestSpecificationLs() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .setBaseUri(TRELLO_LIST_URL)
                .build();
    }

}
