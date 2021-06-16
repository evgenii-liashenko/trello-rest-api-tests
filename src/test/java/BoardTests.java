import beans.TrelloBoard;
import utils.DataProviders;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static core.api.TrelloBoardsApi.makeBoardObject;
import static core.api.TrelloBoardsApi.trelloBoardRequestBuilder;
import static core.api.TrelloBoardsApi.goodResponseSpecification;
import static core.api.TrelloBoardsApi.badResponseSpecification;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;


public class BoardTests extends BaseTest{

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
        TrelloBoard createdBoard = boardService.getBoardFromTrello(generatedId);

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
//        String newName = "Renamed board with random number " + Math.random();
//        String newDescription = "Modified description with random number " + Math.random();
        String newName = generateString(generateNumber(1, 20));
        String newDescription = generateString(generateNumber(1, 20));
        Response putResponse =
                trelloBoardRequestBuilder()
                        .setMethod(Method.PUT)
                        .setId(generatedId)
                        .setName(newName)
                        .setDescription(newDescription)
                        .buildRequest()
                        .sendRequest();
        TrelloBoard modifiedBoard = boardService.getBoardFromTrello(generatedId);

        //Verifying that the modified board has the new name and the new description
        assertThat(modifiedBoard.getName(), equalTo(newName));
        assertThat(modifiedBoard.getDesc(), equalTo(newDescription));
    }

    @Test(dataProvider = "boardTestDataProvider", dataProviderClass = DataProviders.class)
    public void testBoardDeletion(TrelloBoard boardData) {
        //In order to test that a board deletion works, a board needs to be created first
        String boardToBeDeletedId = boardService.createBoardOnTrello(boardData);
        boardService.deleteBoardFromTrello(boardToBeDeletedId);

        //Checking that Trello has no boards with the id of the deleted board
        for (String boardId : boardService.getAllBoardIdsInTrello()) {
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

}
