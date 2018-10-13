package com.scmspain.utils;

import com.scmspain.entities.Tweet;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TweetValidator {

    private static final String HTTP_REGEX = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final int TWEET_MAX_LENGTH = 140;

    /**
     * Validate a given tweet
     *
     * @param tweet Tweet to be validated
     * @throws IllegalArgumentException When tweet is invalid
     */
    public void validate(Tweet tweet) throws IllegalArgumentException {
        validatePublisher(tweet.getPublisher());
        validateText(tweet.getTweet());
    }

    private void validatePublisher(String publisher) throws IllegalArgumentException {
        Optional.ofNullable(publisher).filter(s -> !s.isEmpty()).orElseThrow(() -> new IllegalArgumentException("Publisher must not be null or empty"));
    }

    private void validateText(String text) throws IllegalArgumentException  {
        Optional.ofNullable(text).filter(s -> !s.isEmpty()).orElseThrow(() -> new IllegalArgumentException("text must not be null or empty"));
        checkTextLenght(text);
    }

    private void checkTextLenght(String text) throws IllegalArgumentException  {
        String regex = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String textWithoutUrl = text.replaceAll(regex, "");
        if (textWithoutUrl.length() >= 140) {
            throw new IllegalArgumentException("Tweet must not be greater than 140 character");
        }
    }
}
