package core;

import beans.TrelloBoard;
import beans.TrelloList;
import org.testng.annotations.DataProvider;

import static constants.TestData.*;


public class DataProviders {


    @DataProvider
    public static Object[][] boardTestDataProvider() {
        TrelloBoard board1 = new TrelloBoard();
        board1.setName(ENG_BOARD_NAME);
        board1.setDesc(ENG_BOARD_DESCRIPTION);

        TrelloBoard board2 = new TrelloBoard();
        board2.setName(RUS_BOARD_NAME);
        board2.setDesc(RUS_BOARD_DESCRIPTION);

        return new Object[][]{{board1}, {board2}};
    }


    @DataProvider
    public static Object[][] listTestDataProvider() {
        TrelloList list1 = new TrelloList();
        list1.setName(ENG_LIST_NAME);
        return new Object[][]{{list1}};
    }
}
