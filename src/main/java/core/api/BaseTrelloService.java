package core.api;

import core.AccessDataLoader;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;


import java.util.Map;

import static constants.Parameters.*;
import static org.hamcrest.Matchers.lessThan;

public class BaseTrelloService {
    //API access constants
    public static final String TRELLO_CONSUMER_KEY = AccessDataLoader.getApiData().getProperty(CONSUMER_KEY);
    public static final String TRELLO_ACCESS_TOKEN = AccessDataLoader.getApiData().getProperty(ACCESS_TOKEN);


    //Request variables
    protected Method requestMethod;
    protected Map<String, String> pathParameters;
    protected Map<String, String> queryParameters;


    //Constructor
    public BaseTrelloService(Method requestMethod, Map<String, String> pathParameters, Map<String, String> queryParameters) {
        this.requestMethod = requestMethod;
        this.pathParameters = pathParameters;
        this.queryParameters = queryParameters;
    }


    //Basic response specifications
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
