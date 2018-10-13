package com.scmspain.utils;

import com.scmspain.entities.Tweet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TweetValidator.class)
public class TweetValidatorTest {

    @Autowired
    TweetValidator tweetValidator;

    @Test
    public void shouldValidateAValidTweet() throws Exception {
        Tweet tweet = new Tweet("publisher", "valid tweet", false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test
    public void shouldValidateTooLongTweetsWithUrl() throws Exception {
        String text = "https://www.schibsted.es/ 0htGLagqj5FYMnLPyisXB3RxZSYqceoJwCHVFlk8CujBjsxbKYRh7rkfvee5wGHUGNGTnx8TKggFZEhLfF https://www.coches.net r7XqyBrdZ49YwmQw95B7geXbNEiRSp0ezGARa7e2D1laUbORScJzQj";
        Tweet tweet = new Tweet("publisher", text, false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotValidateTooLongTweets() throws Exception {
        String text = "eZAnem8PJ3cZ7Q3MDI3WmVxNL8VtiQJNcC3Lhsg67vIidvSkJYvqk2JzxvWGGNEsEtYUURJPWB6hKe3o6tEIftYWoaizEzaQbeLHOtHkJxhHKMx5fsmiocLmrmocGupxeJxplCdDUMfl";
        Tweet tweet = new Tweet("publisher", text, false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotValidateAnEmptyPublisher() throws Exception {
        Tweet tweet = new Tweet("", "valid tweet", false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotValidateANullPublisher() throws Exception {
        Tweet tweet = new Tweet(null, "valid tweet", false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotValidateAnEmptyTweet() throws Exception {
        Tweet tweet = new Tweet("publisher", "", false, new Date());
        tweetValidator.validate(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotValidateANullTweet() throws Exception {
        Tweet tweet = new Tweet("publisher", null, false, new Date());
        tweetValidator.validate(tweet);
    }



}
