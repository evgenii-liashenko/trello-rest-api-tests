import org.testng.annotations.AfterMethod;
import services.BoardService;
import services.ListService;
import utils.RandomStringGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class BaseTest {
    ListService listService = new ListService();
    BoardService boardService = new BoardService();

    String generateString(int length){
        return new RandomStringGenerator(length).nextString();
    }

    int generateNumber(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    @AfterMethod
    public void tearDown() {
        for (String boardId : boardService.getAllBoardIdsInTrello()) {
            boardService.deleteBoardFromTrello(boardId);
        }
    }
}
