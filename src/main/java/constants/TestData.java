package constants;

import utils.RandomStringGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class TestData {

    private static int stringLength = generateNumberInRange(1, 20);
    private static RandomStringGenerator generator = new RandomStringGenerator(stringLength);

    public static final String ENG_BOARD_NAME = generator.nextString();
    public static final String RUS_BOARD_NAME = generator.nextString();

    public static final String ENG_BOARD_DESCRIPTION = generator.nextString();
    public static final String RUS_BOARD_DESCRIPTION = generator.nextString();

    public static final Boolean OPEN_BOARD = false;
    public static final Boolean CLOSED_BOARD = true;

    public static final String ENG_LIST_NAME = generator.nextString();


    private static int generateNumberInRange(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}

