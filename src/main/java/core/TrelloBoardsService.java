package core;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import beans.TrelloBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static constants.ConstantParameterValues.*;
import static org.hamcrest.Matchers.lessThan;


public class TrelloBoardsService {

    //API access constants
    public static final URI TRELLO_BOARD_URL = URI.create(AccessDataLoader.getApiData().getProperty(BOARD_URL));
    public static final String TRELLO_CONSUMER_KEY = AccessDataLoader.getApiData().getProperty(CONSUMER_KEY);
    public static final String TRELLO_ACCESS_TOKEN = AccessDataLoader.getApiData().getProperty(ACCESS_TOKEN);


    //Request variables
    private Method requestMethod;
    private Map<String, String> pathParameters;
    private Map<String, String> queryParameters;

    //Constructor
    public TrelloBoardsService(Method requestMethod, Map<String, String> pathParameters, Map<String, String> queryParameters) {
        this.requestMethod = requestMethod;
        this.pathParameters = pathParameters;
        this.queryParameters = queryParameters;
    }

    //Builder implementation
    public static class TrelloRequestBuilder {
        private Method requestMethod = Method.GET;      //Default method in case no method is set
        private Map<String, String> queryParams = new HashMap<>();
        private Map<String, String> pathParams = new LinkedHashMap<>();


        public TrelloRequestBuilder setMethod (Method method){
            requestMethod = method;
            return this;
        }

        public TrelloRequestBuilder setId(String boardId){
            pathParams.put(BOARD_ID, boardId);
            return this;
        }

        public TrelloRequestBuilder setName (String boardName){
            queryParams.put(BOARD_NAME, boardName);
            return this;
        }

        public TrelloRequestBuilder setDescription (String boardDescription){
            queryParams.put(BOARD_DESCRIPTION, boardDescription);
            return this;
        }

        public TrelloRequestBuilder setIsClosed(Boolean boardStatus){
            queryParams.put(IS_CLOSED, boardStatus.toString());
            return this;
        }

        public TrelloBoardsService buildRequest() {
            return new TrelloBoardsService(requestMethod, pathParams, queryParams);
        }
    }

    //Builder instantiation
    public static TrelloRequestBuilder trelloRequestBuilder() {
        return new TrelloRequestBuilder();
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


    //Request and response specifications
    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .setBaseUri(TRELLO_BOARD_URL)
                .build();
    }
    public static ResponseSpecification goodResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(10000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }
    public static ResponseSpecification badResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.TEXT)
                .expectResponseTime(lessThan(10000L))
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }


}
