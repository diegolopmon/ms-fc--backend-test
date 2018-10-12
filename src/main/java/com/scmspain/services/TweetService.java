package com.scmspain.services;

import com.scmspain.entities.Tweet;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TweetService {
    private EntityManager entityManager;
    private MetricWriter metricWriter;

    public TweetService(EntityManager entityManager, MetricWriter metricWriter) {
        this.entityManager = entityManager;
        this.metricWriter = metricWriter;
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

            this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
            this.entityManager.persist(tweet);
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
      return this.entityManager.find(Tweet.class, id);
    }

    /**
     * List all tweets from repository
     *
     * @return Tweet list
     */
    public List<Tweet> listAllTweets() {
        List<Tweet> result = new ArrayList<Tweet>();
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        TypedQuery<Long> query = this.entityManager.createQuery("SELECT id FROM Tweet AS tweetId WHERE pre2015MigrationStatus<>99 ORDER BY id DESC", Long.class);
        List<Long> ids = query.getResultList();
        for (Long id : ids) {
            result.add(getTweet(id));
        }
        return result;
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
