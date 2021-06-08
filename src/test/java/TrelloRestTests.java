import beans.TrelloBoard;
import beans.TrelloList;
import core.DataProviders;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.api.TrelloBoardsService.makeBoardObject;
import static core.api.TrelloBoardsService.trelloBoardRequestBuilder;
import static core.api.TrelloListService.makeListObject;
import static core.api.TrelloListService.trelloListRequestBuilder;
import static core.api.TrelloMembersService.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TrelloRestTests {

    //CRUD Trello board tests

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardCreationAndReading(TrelloBoard boardToBeCreated) {
        //Sending a POST request with some test data
        Response response =
                trelloBoardRequestBuilder()
                        .setMethod(Method.POST)
                        .setName(boardToBeCreated.getName())
                        .setDescription(boardToBeCreated.getDesc())
                        .buildRequest()
                        .sendRequest();
        String generatedId = makeBoardObject(response).getId();

        //Reading actual data from the created board with GET request
        TrelloBoard createdBoard = getBoardFromTrello(generatedId);

        //Checking for equality
        assertThat(createdBoard.getName(), equalTo(boardToBeCreated.getName()));
        assertThat(createdBoard.getDesc(), equalTo(boardToBeCreated.getDesc()));
    }

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardModification(TrelloBoard boardToBeModified){
        //Creating a board
        Response postResponse =
                trelloBoardRequestBuilder()
                        .setMethod(Method.POST)
                        .setName(boardToBeModified.getName())
                        .setDescription(boardToBeModified.getDesc())
                        .buildRequest()
                        .sendRequest();
        String generatedId = makeBoardObject(postResponse).getId();

        //Modifying the board
        String newName = "Renamed board with random number " + Math.random();
        String newDescription = "Modified description with random number " + Math.random();
        Response putResponse =
                trelloBoardRequestBuilder()
                        .setMethod(Method.PUT)
                        .setId(generatedId)
                        .setName(newName)
                        .setDescription(newDescription)
                        .buildRequest()
                        .sendRequest();
        TrelloBoard modifiedBoard = getBoardFromTrello(generatedId);

        //Verifying that the modified board has the new name and the new description
        assertThat(modifiedBoard.getName(), equalTo(newName));
        assertThat(modifiedBoard.getDesc(), equalTo(newDescription));
    }

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardDeletion(TrelloBoard boardData) {
        //In order to test that a board deletion works, a board needs to be created first
        String boardToBeDeletedId = createBoardOnTrello(boardData);
        deleteBoardFromTrello(boardToBeDeletedId);

        //Checking that Trello has no boards with the id of the deleted board
        for (String boardId : getAllBoardIdsInTrello()) {
            assertThat(boardId, is(not(equalTo(boardToBeDeletedId))));
        }
    }


    //Trello list tests
    @Test(dataProvider = "listTestDataProvider", dataProviderClass = DataProviders.class)
    public void testListCreation(TrelloList listData){
        String listName = listData.getName();
        String boardName = "This board should contain a list named " + listName;

        //Creating a new board and a list within it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName(boardName);
        String generatedBoardId = createBoardOnTrello(trelloBoard);
        String generatedListId = createListOnTrello(listName, generatedBoardId);
        TrelloList createdList = getListFromTrello(generatedListId);

        assertThat(createdList.getName(), equalTo(listName));
    }



    //Auxiliary list testing methods
    public String createListOnTrello(String name, String boardId){
        Response response = trelloListRequestBuilder()
                .setMethod(Method.POST)
                .setName(name)
                .setBoardId(boardId)
                .buildRequest()
                .sendRequest();
        String generatedId = makeListObject(response).getId();
        return generatedId;
    }
    public TrelloList getListFromTrello(String listId){
        Response response = trelloListRequestBuilder()
                .setListId(listId)
                .buildRequest()
                .sendRequest();
        return makeListObject(response);
    }





    //Auxiliary board testing methods
    public String createBoardOnTrello(TrelloBoard boardData) {
        Response response = trelloBoardRequestBuilder()
                        .setMethod(Method.POST)
                        .setName(boardData.getName())
                        .setDescription(boardData.getDesc())
                        .buildRequest()
                        .sendRequest();
        String generatedId = makeBoardObject(response).getId();
        return generatedId;
    }
    public TrelloBoard getBoardFromTrello(String boardId) {        //String boardId
        Response response = trelloBoardRequestBuilder()
                        .setId(boardId)
                        .buildRequest()
                        .sendRequest();
        return makeBoardObject(response);
    }
    public void deleteBoardFromTrello(String boardId) {
        trelloBoardRequestBuilder()
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
