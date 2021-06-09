import beans.TrelloBoard;
import beans.TrelloBoardStar;
import beans.TrelloList;
import core.DataProviders;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.api.TrelloBoardsService.makeBoardObject;
import static core.api.TrelloBoardsService.trelloBoardRequestBuilder;
import static core.api.TrelloBoardsService.goodResponseSpecification;
import static core.api.TrelloBoardsService.badResponseSpecification;
import static core.api.TrelloListService.makeListObject;
import static core.api.TrelloListService.trelloListRequestBuilder;
import static core.api.TrelloMembersService.trelloMembersRequestBuilder;
import static core.api.TrelloMembersService.makeBoardStarObject;
import static core.api.TrelloMembersService.makeBoardObjects;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;


public class TrelloRestTests {

    //Trello board tests

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardCreationAndReading(TrelloBoard boardToBeCreated) {
        //Sending a POST request with some test data
        Response response = trelloBoardRequestBuilder()
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
    public void testBoardModification(TrelloBoard boardToBeModified) {
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

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void checkCorrectBoardPostResponse(TrelloBoard boardToBeCreated) {
        trelloBoardRequestBuilder()
                .setMethod(Method.POST)
                .setName(boardToBeCreated.getName())
                .setDescription(boardToBeCreated.getDesc())
                .buildRequest()
                .sendRequest()
                .then()
                .assertThat()
                .spec(goodResponseSpecification());
    }

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void checkIncorrectBoardPostResponse(TrelloBoard boardToBeCreated) {
        trelloBoardRequestBuilder()
                .setMethod(Method.POST)
                //.setName(boardToBeCreated.getName())
                .setDescription(boardToBeCreated.getDesc())
                .buildRequest()
                .sendRequest()
                .then()
                .assertThat()
                .spec(badResponseSpecification())
                .and()
                .body(containsString("invalid value for name"));
    }


    //Trello list tests

    @Test(dataProvider = "listTestDataProvider", dataProviderClass = DataProviders.class)
    public void testListCreation(TrelloList listData) {
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

    @Test
    public void testListMoving() {
        //Setting up boards
        TrelloBoard firstBoard = new TrelloBoard();
        firstBoard.setName("The board where Test List is created");
        String firstBoardId = createBoardOnTrello(firstBoard);
        TrelloBoard secondBoard = new TrelloBoard();
        secondBoard.setName("The board Test List should be moved to");
        String secondBoardId = createBoardOnTrello(secondBoard);

        //Creating a list on the first board
        String generatedListId = createListOnTrello("Test List", firstBoardId);

        //Moving the list from the fist board to the second one
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setBoardId(secondBoardId)
                .buildRequest()
                .sendRequest();

        //Verifying that the list has moved to the second board
        TrelloList movedList = getListFromTrello(generatedListId);
        String boardWithMovedListId = movedList.getIdBoard();
        assertThat(boardWithMovedListId, equalTo(secondBoardId));
    }

    @Test
    public void testListRenaming() {
        //List names
        String initialName = "Test list";
        String updatedName = "Renamed list";

        //Creating a board and a list on it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName("Test board");
        String generatedBoardId = createBoardOnTrello(trelloBoard);
        String generatedListId = createListOnTrello(initialName, generatedBoardId);

        //Renaming the list
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setName(updatedName)
                .buildRequest()
                .sendRequest();

        //Verifying that the list has been renamed
        TrelloList renamedList = getListFromTrello(generatedListId);
        assertThat(renamedList.getName(), equalTo(updatedName));
    }

    @Test
    public void testListArchiving() {
        //Creating a board and a list on it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName("The board with an archived list");
        String generatedBoardId = createBoardOnTrello(trelloBoard);
        String generatedListId = createListOnTrello("The list to be archived", generatedBoardId);

        //Archiving the list
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setClosed("true")
                .buildRequest()
                .sendRequest();

        //Verifying that the list has been archived
        TrelloList testList = getListFromTrello(generatedListId);
        assertThat(testList.getClosed(), equalTo(true));
    }


    //Trello members tests

    @Test
    public void testStarring() {
        //Creating a board
        TrelloBoard board = new TrelloBoard();
        board.setName("The board to be starred");
        String generatedBoardId = createBoardOnTrello(board);

        //Starring a board
        Response response = trelloMembersRequestBuilder()
                .setMethod(Method.POST)
                .setFirstPathSuffix("me")
                .setSecondPathSuffix("boardStars")
                .setBoardId(generatedBoardId)
                .setPosition("top")
                .buildRequest()
                .sendRequest();

        //Verifying response
        TrelloBoardStar trelloBoardStar = makeBoardStarObject(response);
        assertThat(trelloBoardStar.getIdBoard(), equalTo(generatedBoardId));
    }


    //Auxiliary list testing methods

    public String createListOnTrello(String name, String boardId) {
        Response response = trelloListRequestBuilder()
                .setMethod(Method.POST)
                .setName(name)
                .setBoardId(boardId)
                .buildRequest()
                .sendRequest();
        String generatedId = makeListObject(response).getId();
        return generatedId;
    }

    public TrelloList getListFromTrello(String listId) {
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


    @AfterMethod
    public void tearDown() {
        for (String boardId : getAllBoardIdsInTrello()) {
            deleteBoardFromTrello(boardId);
        }
    }
}
