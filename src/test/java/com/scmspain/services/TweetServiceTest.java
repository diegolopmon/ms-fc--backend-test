package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.utils.TweetValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TweetService.class)
public class TweetServiceTest {

    @Autowired
    private TweetService tweetService;

    @MockBean
    private MetricWriter metricWriter;
    @MockBean
    private TweetRepository tweetRepository;
    @MockBean
    private TweetValidator tweetValidator;

    @Test
    public void shouldInsertANewTweet() {
        tweetService.publishTweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        verify(tweetRepository).save(any(Tweet.class));
        verify(tweetValidator).validate(any(Tweet.class));
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInsertAnInvalidTweet() throws Exception {
        doThrow(new IllegalArgumentException("ex")).when(tweetValidator).validate(any());

        tweetService.publishTweet("Guybrush Threepwood", "0htGLagqj5FYMnLPyisXB3RxZSYqceoJwCHVFlk8C3ujBjsxbKYRh7rkfvee5wGHUGNGTnx8TKggFZEhLfFr7XqyBrdZ49YwmQw95B7geXbNEiRSp0ezGARa7e2D1laUbORScJzQjQw95B7geXbNEiRSp0ezGARa7e2D1laUbORScJzQj ");

        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(metricWriter, never()).increment(any(Delta.class));
    }

    @Test
    public void shouldGetATweet() {
        tweetService.getTweet(1L);
        verify(tweetRepository).findOne(1L);
    }

    @Test
    public void shouldListAllTweets() {
        List<Tweet> tweetList = new ArrayList<>();
        tweetList.add(new Tweet("me", "tweet", false, new Date()));
        when(tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc()).thenReturn(tweetList);

        List<Tweet> returnedTweetList = tweetService.listAllTweets();

        assertThat(returnedTweetList.size()).isEqualTo(1);
        assertThat(returnedTweetList.get(0).getTweet()).isEqualTo(tweetList.get(0).getTweet());
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldListZeroTweets() {
        when(tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc()).thenReturn(emptyList());

        List<Tweet> returnedTweetList = tweetService.listAllTweets();

        assertThat(returnedTweetList.size()).isZero();
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldListAllDiscardedTweets() {
        List<Tweet> tweetList = new ArrayList<>();
        tweetList.add(new Tweet("me", "tweet", true, new Date()));
        when(tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc()).thenReturn(tweetList);

        List<Tweet> returnedTweetList = tweetService.listAllDiscardedTweets();

        assertThat(returnedTweetList.size()).isEqualTo(1);
        assertThat(returnedTweetList.get(0).getTweet()).isEqualTo(tweetList.get(0).getTweet());
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldListZeroDiscardedTweets() {
        when(tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc()).thenReturn(emptyList());

        List<Tweet> returnedTweetList = tweetService.listAllDiscardedTweets();

        assertThat(returnedTweetList.size()).isZero();
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test
    public void shouldDiscardTweet() {
        Tweet spyTweet = spy(new Tweet("me", "tweet", false, new Date()));
        when(tweetRepository.findOne(22L)).thenReturn(spyTweet);

        tweetService.discardTweet(22L);

        assertThat(spyTweet.getDiscarded()).isTrue();
        assertThat(spyTweet.getDiscardedDate()).isNotNull();
        verify(tweetRepository).save(any(Tweet.class));
        verify(metricWriter).increment(any(Delta.class));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldNotDiscardANonExistentTweet() {
        when(tweetRepository.findOne(anyLong())).thenReturn(null);

        tweetService.discardTweet(22L);

        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(metricWriter, never()).increment(any(Delta.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDiscardADiscardedTweet() {
        Tweet spyTweet = spy(new Tweet("me", "tweet", true, new Date()));
        when(tweetRepository.findOne(22L)).thenReturn(spyTweet);

        tweetService.discardTweet(22L);

        verify(tweetRepository, never()).save(any(Tweet.class));
        verify(metricWriter, never()).increment(any(Delta.class));
    }

}
