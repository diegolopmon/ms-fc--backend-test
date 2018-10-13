package com.scmspain.repositories;

import com.scmspain.entities.Tweet;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TweetRepository extends PagingAndSortingRepository<Tweet,Long> {
    List<Tweet> findAllByDiscardedFalseOrderByPublicationDateDesc();
    List<Tweet> findAllByDiscardedTrueOrderByDiscardedDateDesc();
}
