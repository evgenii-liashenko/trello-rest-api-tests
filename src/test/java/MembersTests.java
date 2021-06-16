import beans.TrelloBoard;
import beans.TrelloBoardStar;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static core.api.TrelloMembersApi.makeBoardStarObject;
import static core.api.TrelloMembersApi.trelloMembersRequestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class MembersTests extends BaseTest {

    @Test
    public void testStarring() {
        //Creating a board
        TrelloBoard board = new TrelloBoard();
        board.setName(generateString(generateNumber(1, 20)));
        String generatedBoardId = boardService.createBoardOnTrello(board);

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
}
