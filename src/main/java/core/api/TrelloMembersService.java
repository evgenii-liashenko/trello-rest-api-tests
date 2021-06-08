package core.api;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import beans.TrelloBoard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import core.AccessDataLoader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import static constants.Parameters.*;


public class TrelloMembersService extends BaseTrelloService {

    //Endpoint URL
    public static final URI TRELLO_MEMBERS_URL = URI.create(AccessDataLoader.getApiData().getProperty(MEMBERS_URL));

    //Constructor
    public TrelloMembersService(Method requestMethod, Map<String, String> pathParameters, Map<String, String> queryParameters) {
        super(requestMethod, pathParameters, queryParameters);
    }

    //Builder implementation
    public static class TrelloMembersRequestBuilder {
        private Method requestMethod = Method.GET;      //Default method in case no method is set
        private Map<String, String> queryParams = new HashMap<>();
        private Map<String, String> pathParams = new LinkedHashMap<>();


        public TrelloMembersRequestBuilder setMethod (Method method){
            requestMethod = method;
            return this;
        }

        public TrelloMembersRequestBuilder setFirstPathSuffix(String pathSuffix1){
            pathParams.put("pathSuffix1", pathSuffix1);
            return this;
        }

        public TrelloMembersRequestBuilder setSecondPathSuffix(String pathSuffix2){
            pathParams.put("pathSuffix2", pathSuffix2);
            return this;
        }

        public TrelloMembersRequestBuilder setField (String field){
            queryParams.put("fields", field);
            return this;
        }

        public TrelloMembersService buildRequest() {
            return new TrelloMembersService(requestMethod, pathParams, queryParams);
        }
    }

    //Builder instantiation
    public static TrelloMembersRequestBuilder trelloMembersRequestBuilder() {
        return new TrelloMembersRequestBuilder();
    }


    //API interaction
    public Response sendRequest() {
        String pathStr = "";
        for (String key : pathParameters.keySet()) {        //This works, but I am not sure that the produced order will always be correct for multiple parameters
            pathStr += "{" + key + "}/";
        }
        return RestAssured
                .given(requestSpecificationMs()).log().all()
                .header("Authorization", "OAuth oauth_consumer_key=\"" + TRELLO_CONSUMER_KEY + "\", oauth_token=\"" + TRELLO_ACCESS_TOKEN + "\"")
                .pathParams(pathParameters)
                .queryParams(queryParameters)
                .request(requestMethod, pathStr)
                .prettyPeek();
    }

    //Conversion of deserialized json from Response to TrelloBoard
    public static List<TrelloBoard> makeBoardObjects(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<TrelloBoard>>() {}.getType());
    }


    //Request and response specifications
    public static RequestSpecification requestSpecificationMs() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .setBaseUri(TRELLO_MEMBERS_URL)
                .build();
    }

}
