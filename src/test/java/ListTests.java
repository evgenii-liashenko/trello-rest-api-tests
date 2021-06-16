import beans.TrelloBoard;
import beans.TrelloList;
import utils.DataProviders;
import io.restassured.http.Method;
import org.testng.annotations.Test;


import static core.api.TrelloListApi.trelloListRequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ListTests extends BaseTest{


    @Test(dataProvider = "listTestDataProvider", dataProviderClass = DataProviders.class)
    public void testListCreation(TrelloList listData) {
        String listName = listData.getName();
        String boardName = generateString(generateNumber(1, 20));

        //Creating a new board and a list within it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName(boardName);
        String generatedBoardId = boardService.createBoardOnTrello(trelloBoard);
        String generatedListId = listService.createListOnTrello(listName, generatedBoardId);
        TrelloList createdList = listService.getListFromTrello(generatedListId);

        assertThat(createdList.getName(), equalTo(listName));
    }

    @Test
    public void testListMoving() {
        //Setting up boards
        TrelloBoard firstBoard = new TrelloBoard();
        firstBoard.setName(generateString(generateNumber(1, 20)));
        String firstBoardId = boardService.createBoardOnTrello(firstBoard);
        TrelloBoard secondBoard = new TrelloBoard();
        secondBoard.setName(generateString(generateNumber(1, 20)));
        String secondBoardId = boardService.createBoardOnTrello(secondBoard);

        //Creating a list on the first board
        String generatedListId = listService.createListOnTrello(generateString(generateNumber(1, 20)), firstBoardId);

        //Moving the list from the fist board to the second one
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setBoardId(secondBoardId)
                .buildRequest()
                .sendRequest();

        //Verifying that the list has moved to the second board
        TrelloList movedList = listService.getListFromTrello(generatedListId);
        String boardWithMovedListId = movedList.getIdBoard();
        assertThat(boardWithMovedListId, equalTo(secondBoardId));
    }

    @Test
    public void testListRenaming() {
        //List names
        String initialName = generateString(generateNumber(1, 20));
        String updatedName = generateString(generateNumber(1, 20));

        //Creating a board and a list on it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName(generateString(generateNumber(1, 20)));
        String generatedBoardId = boardService.createBoardOnTrello(trelloBoard);
        String generatedListId = listService.createListOnTrello(initialName, generatedBoardId);

        //Renaming the list
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setName(updatedName)
                .buildRequest()
                .sendRequest();

        //Verifying that the list has been renamed
        TrelloList renamedList = listService.getListFromTrello(generatedListId);
        assertThat(renamedList.getName(), equalTo(updatedName));
    }

    @Test
    public void testListArchiving() {
        //Creating a board and a list on it
        TrelloBoard trelloBoard = new TrelloBoard();
        trelloBoard.setName(generateString(generateNumber(1, 20)));
        String generatedBoardId = boardService.createBoardOnTrello(trelloBoard);
        String generatedListId = listService.createListOnTrello(generateString(generateNumber(1, 20)), generatedBoardId);

        //Archiving the list
        trelloListRequestBuilder()
                .setMethod(Method.PUT)
                .setListId(generatedListId)
                .setClosed("true")
                .buildRequest()
                .sendRequest();

        //Verifying that the list has been archived
        TrelloList testList = listService.getListFromTrello(generatedListId);
        assertThat(testList.getClosed(), equalTo(true));
    }
}
