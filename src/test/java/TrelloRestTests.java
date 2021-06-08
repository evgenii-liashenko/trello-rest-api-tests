import beans.TrelloBoard;
import core.DataProviders;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.TrelloBoardsService.makeBoardObject;
import static core.TrelloBoardsService.trelloRequestBuilder;
import static core.TrelloMembersService.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TrelloRestTests {

    //TODO Add matchers

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardCreation(TrelloBoard boardToBeCreated) {
        //Sending a POST request with some test data
        Response response =
                trelloRequestBuilder()
                        .setMethod(Method.POST)
                        .setName(boardToBeCreated.getName())
                        .setDescription(boardToBeCreated.getDesc())
                        .buildRequest()
                        .sendRequest();
        String generatedId = makeBoardObject(response).getId();

        //Gathering actual data from the created board with GET request
        TrelloBoard createdBoard = getBoardFromTrello(generatedId);

        //Checking for equality
        assertEquals(createdBoard.getName(), boardToBeCreated.getName());
        assertEquals(createdBoard.getDesc(), boardToBeCreated.getDesc());
    }

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardDeletion(TrelloBoard boardData) {
        //In order to test that a board deletion works, a board needs to be created first
        String generatedBoardId = createBoardOnTrello(boardData);
        deleteBoardFromTrello(generatedBoardId);

        //TODO get a list of all ids of boards present in trello
        //TODO assert that the list does not contain the deleted id
    }



    public String createBoardOnTrello(TrelloBoard boardData) {
        Response response =
                trelloRequestBuilder()
                        .setMethod(Method.POST)
                        .setName(boardData.getName())
                        .setDescription(boardData.getDesc())
                        .buildRequest()
                        .sendRequest();
        String generatedId = makeBoardObject(response).getId();
        return generatedId;
    }

    public TrelloBoard getBoardFromTrello(String boardId) {        //String boardId
        Response response =
                trelloRequestBuilder()
                        .setId(boardId)
                        .buildRequest()
                        .sendRequest();
        return makeBoardObject(response);
    }

    public void deleteBoardFromTrello(String boardId) {
        trelloRequestBuilder()
                .setMethod(Method.DELETE)
                .setId(boardId)
                .buildRequest()
                .sendRequest();
    }

    public List<String> getAllBoardIdsInTrello() {
        Response response = trelloMembersRequestBuilder()
                .setFirstPathSuffix("me")
                .setSecondPathSuffix("boards")
                .setField("id")
                .buildRequest()
                .sendRequest();

        List<TrelloBoard> boardObjects = makeBoardObjects(response);
        List<String> idsOnly = new ArrayList<>();
        for (TrelloBoard item : boardObjects) {
            idsOnly.add(item.getId());
        }

        return idsOnly;
    }


    @AfterSuite
    public void tearDown() {
        for (String boardId : getAllBoardIdsInTrello()) {
            deleteBoardFromTrello(boardId);
        }
    }
}
