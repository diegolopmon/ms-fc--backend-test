package com.scmspain.repositories;

import com.scmspain.configuration.TestConfiguration;
import com.scmspain.entities.Tweet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class TweetRepositoryTest {

    @Autowired
    TweetRepository tweetRepository;

    @Test
    public void shouldFindAllByDiscardedFalseOrderByPublicationDateDesc() {
        int numberOfTweets = 20;
        List<Tweet> tweetList = new ArrayList<>();
        IntStream.range(0, numberOfTweets).forEach(x -> tweetList.add(
                new Tweet(String.format("publisher%d", x), String.format("tweet number: %d", x), false, dateWithOffset(new Date(), x))));
        tweetRepository.save(tweetList);

        List<Tweet> resultedTweetList = tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc();

        assertThat(resultedTweetList.size()).isEqualTo(numberOfTweets);
        assertThat(resultedTweetList.get(0).getTweet()).isEqualTo(String.format("tweet number: %d", numberOfTweets - 1));
        assertThat(resultedTweetList.get(--numberOfTweets).getTweet()).isEqualTo("tweet number: 0");
        assertThat(tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc().size()).isZero();
    }

    @Test
    public void shouldFindAllByDiscardedTrueOrderByPublicationDateDesc() {
        int numberOfTweets = 20;
        List<Tweet> tweetList = new ArrayList<>();
        IntStream.range(0, numberOfTweets).forEach(x -> tweetList.add(
                getDiscardedTweet(String.format("publisher%d", x), String.format("tweet number: %d", x), dateWithOffset(new Date(), x))));
        tweetRepository.save(tweetList);

        List<Tweet> resultedTweetList = tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();

        assertThat(resultedTweetList.size()).isEqualTo(numberOfTweets);
        assertThat(resultedTweetList.get(0).getTweet()).isEqualTo(String.format("tweet number: %d", numberOfTweets - 1));
        assertThat(resultedTweetList.get(--numberOfTweets).getTweet()).isEqualTo("tweet number: 0");
        assertThat(tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc().size()).isZero();
    }

    private Date dateWithOffset(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    private Tweet getDiscardedTweet(String publisher, String text, Date date) {
        Tweet tweet = new Tweet(publisher, text, true, date);
        tweet.setDiscardedDate(dateWithOffset(date, 20));
        return tweet;
    }
}
