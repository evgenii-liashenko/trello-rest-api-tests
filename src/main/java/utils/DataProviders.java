package utils;

import beans.TrelloBoard;
import beans.TrelloList;
import org.testng.annotations.DataProvider;

import java.util.concurrent.ThreadLocalRandom;


public class DataProviders {


    @DataProvider
    public static Object[][] boardTestDataProvider() {
        int stringLength = generateNumberInRange(1, 20);
        RandomStringGenerator generator = new RandomStringGenerator(stringLength);

        TrelloBoard board1 = new TrelloBoard();
        board1.setName(generator.nextString());
        board1.setDesc(generator.nextString());

        TrelloBoard board2 = new TrelloBoard();
        board2.setName(generator.nextString());
        board2.setDesc(generator.nextString());

        return new Object[][]{{board1}, {board2}};
    }


    @DataProvider
    public static Object[][] listTestDataProvider() {
        int stringLength = generateNumberInRange(1, 20);
        RandomStringGenerator generator = new RandomStringGenerator(stringLength);
        TrelloList list1 = new TrelloList();
        list1.setName(generator.nextString());
        return new Object[][]{{list1}};
    }

    private static int generateNumberInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
