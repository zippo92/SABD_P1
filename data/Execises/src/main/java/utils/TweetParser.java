package utils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * author: Torlone (Roma Tre)
 */
public class TweetParser {

    public static Tweet parseJson(String jsonLine) {

        ObjectMapper objectMapper = new ObjectMapper();
        Tweet tweet = null;

        try {
            tweet = objectMapper.readValue(jsonLine, Tweet.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tweet;
    }

}



