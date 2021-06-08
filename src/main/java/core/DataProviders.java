package core;

import beans.TrelloBoard;
import org.testng.annotations.DataProvider;

import static constants.TestDataValues.*;


public class DataProviders {


    @DataProvider
    public static Object[][] boardTestDataProvider() {
        TrelloBoard board1 = new TrelloBoard();
        board1.setName(ENG_NAME);
        board1.setDesc(ENG_DESCRIPTION);

        TrelloBoard board2 = new TrelloBoard();
        board2.setName(RUS_NAME);
        board2.setDesc(RUS_DESCRIPTION);

        return new Object[][]{{board1}, {board2}};
    }
}
