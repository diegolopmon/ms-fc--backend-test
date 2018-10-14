package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import com.scmspain.utils.TweetValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class TweetService {

    private MetricWriter metricWriter;
    private TweetRepository tweetRepository;
    private TweetValidator tweetValidator;

    @Autowired
    public TweetService(@Qualifier("scm") MetricWriter metricWriter, TweetRepository tweetRepository, TweetValidator tweetValidator) {
        this.metricWriter = metricWriter;
        this.tweetRepository = tweetRepository;
        this.tweetValidator = tweetValidator;
    }

    /**
     * Push tweet to repository
     *
     * @param publisher Tweet creator
     * @param text      Tweet content
     */
    public void publishTweet(String publisher, String text) {

        Tweet tweet = new Tweet();
        tweet.setTweet(text);
        tweet.setPublisher(publisher);
        tweet.setDiscarded(false);
        tweet.setPublicationDate(new Date());

        tweetValidator.validate(tweet);

        metricWriter.increment(new Delta<Number>("times-published-tweets", 1));
        tweetRepository.save(tweet);
    }

    /**
     * Recover tweet from repository
     *
     * @param id id of the Tweet to retrieve
     * @return retrieved Tweet
     */
    public Tweet getTweet(Long id) {
        return tweetRepository.findOne(id);
    }

    /**
     * List all tweets from repository
     *
     * @return Tweet list
     */
    public List<Tweet> listAllTweets() {
        metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        return tweetRepository.findAllByDiscardedFalseOrderByPublicationDateDesc();
    }

    /**
     * List all discarded tweets from repository
     *
     * @return Tweet list
     */
    public List<Tweet> listAllDiscardedTweets() {
        metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        return tweetRepository.findAllByDiscardedTrueOrderByDiscardedDateDesc();
    }

    /**
     * Discard a tweet from repository
     *
     * @param id id of tweet to be removed
     */
    public void discardTweet(Long id) {
        Tweet tweet = Optional.ofNullable(getTweet(id)).orElseThrow(() -> new NoSuchElementException("Tweet does not exits"));
        if (tweet.getDiscarded()) {
            throw new IllegalArgumentException("Tweet already discarded");
        }

        tweet.setDiscarded(true);
        tweet.setDiscardedDate(new Date());
        tweetRepository.save(tweet);
        metricWriter.increment(new Delta<Number>("times-discarded-tweets", 1));
    }
}
