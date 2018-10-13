package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.repositories.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public TweetService(MetricWriter metricWriter, TweetRepository tweetRepository) {
        this.metricWriter = metricWriter;
        this.tweetRepository = tweetRepository;
    }

    /**
     * Push tweet to repository
     *
     * @param publisher Tweet creator
     * @param text      Tweet content
     */
    public void publishTweet(String publisher, String text) {
        Optional.ofNullable(publisher).filter(s -> !s.isEmpty()).orElseThrow(() -> new IllegalArgumentException("Publisher must not be null or empty"));
        Optional.ofNullable(text).filter(s -> !s.isEmpty()).orElseThrow(() -> new IllegalArgumentException("text must not be null or empty"));

        if (checkTextLenght(text)) {
            Tweet tweet = new Tweet();
            tweet.setTweet(text);
            tweet.setPublisher(publisher);
            tweet.setDiscarded(false);
            tweet.setPublicationDate(new Date());

            metricWriter.increment(new Delta<Number>("times-published-tweets", 1));
            tweetRepository.save(tweet);
        } else {
            throw new IllegalArgumentException("Tweet must not be greater than 140 characters");
        }
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


    /**
     * Check if the text contains more than 140 characters
     *
     * @param text text
     * @return result
     */
    private boolean checkTextLenght(String text) {
        String regex = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        String textWithoutUrl = text.replaceAll(regex, "");
        return textWithoutUrl.length() < 140;
    }


}
