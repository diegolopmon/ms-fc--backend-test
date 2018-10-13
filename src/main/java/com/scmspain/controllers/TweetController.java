package com.scmspain.controllers;

import com.scmspain.controllers.command.DiscardTweetCommand;
import com.scmspain.controllers.command.PublishTweetCommand;
import com.scmspain.controllers.exception.ExceptionMessage;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class TweetController {

    private TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/tweet")
    public List<Tweet> listAllTweets() {
        return tweetService.listAllTweets();
    }

    @GetMapping("/discarded")
    public List<Tweet> listAllDiscardedTweets() {
        return tweetService.listAllDiscardedTweets();
    }

    @PostMapping("/tweet")
    @ResponseStatus(CREATED)
    public void publishTweet(@RequestBody PublishTweetCommand publishTweetCommand) {
        tweetService.publishTweet(publishTweetCommand.getPublisher(), publishTweetCommand.getTweet());
    }

    @PostMapping("/discarded")
    public void discardTweet(@RequestBody DiscardTweetCommand discardTweetCommand) {
        tweetService.discardTweet(discardTweetCommand.getTweet());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ExceptionMessage invalidArgumentException(IllegalArgumentException ex) {
        return new ExceptionMessage(ex);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Object noSucHElementException(NoSuchElementException ex) {
        return new ExceptionMessage(ex);
    }
}
