package com.scmspain.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scmspain.controllers.message.ExceptionMessage;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TweetControllerTest {

    private static String PUBLISHER = "Yo";
    private static String TWEET = "How are you?";
    private static String EXCEPTION_MESSAGE = "Exception Message";

    private static String TWEET_ENDPOINT = "/tweet";
    private static String DISCARDED_ENDPOINT = "/discarded";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TweetService tweetService;

    @Test
    public void shouldReturn200WhenInsertingAValidTweet() throws Exception {
        mockMvc.perform(newTweet(PUBLISHER, TWEET))
                .andExpect(status().is(CREATED.value()));

        verify(tweetService).publishTweet(PUBLISHER, TWEET);
    }

    @Test
    public void shouldReturn400WhenInsertingAnInvalidTweet() throws Exception {
        doThrow(new IllegalArgumentException(EXCEPTION_MESSAGE)).when(tweetService).publishTweet(anyString(), anyString());

        MvcResult getResult = mockMvc.perform(newTweet("Schibsted Spain", "Hi everyone! We are Schibsted Spain (look at our home page http://www.schibsted.es/), we own Vibbo, InfoJobs, fotocasa, habitaclia, coches.net and milanuncios. Welcome!"))
                .andExpect(status().is(BAD_REQUEST.value())).andReturn();

        String content = getResult.getResponse().getContentAsString();
        ExceptionMessage result = new ObjectMapper().readValue(content, ExceptionMessage.class);
        assertThat(result.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
        assertThat(result.getExceptionClass()).isEqualTo("IllegalArgumentException");
    }



    @Test
    public void shouldReturn200WhenDiscardingAValidTweet() throws Exception {
        Long tweetId = 1L;
        mockMvc.perform(discardTweet(tweetId))
                .andExpect(status().is(OK.value()));

        verify(tweetService).discardTweet(tweetId);
    }

    @Test
    public void shouldReturn400WhenDiscardingANonExistentTweet() throws Exception {
        doThrow(new NoSuchElementException(EXCEPTION_MESSAGE)).when(tweetService).discardTweet(anyLong());

        MvcResult getResult = mockMvc.perform(discardTweet(2L))
                .andExpect(status().is(BAD_REQUEST.value())).andReturn();

        String content = getResult.getResponse().getContentAsString();
        ExceptionMessage result = new ObjectMapper().readValue(content, ExceptionMessage.class);
        assertThat(result.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
        assertThat(result.getExceptionClass()).isEqualTo("NoSuchElementException");
    }

    @Test
    public void shouldReturn400WhenDiscardingAnAlreadyDiscardedTweet() throws Exception {
        doThrow(new IllegalArgumentException(EXCEPTION_MESSAGE)).when(tweetService).discardTweet(anyLong());

        MvcResult getResult = mockMvc.perform(discardTweet(2L))
                .andExpect(status().is(BAD_REQUEST.value())).andReturn();

        String content = getResult.getResponse().getContentAsString();
        ExceptionMessage result = new ObjectMapper().readValue(content, ExceptionMessage.class);
        assertThat(result.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
        assertThat(result.getExceptionClass()).isEqualTo("IllegalArgumentException");
    }

    @Test
    public void shouldReturnAllPublishedTweets() throws Exception {
        int numberOfTweets = 10;
        when(tweetService.listAllTweets()).thenReturn(listOfTweets(numberOfTweets, false));

        MvcResult getResult = mockMvc.perform(get(TWEET_ENDPOINT))
                .andExpect(status().is(OK.value()))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(numberOfTweets);
        List<Tweet> tweetList = new ObjectMapper().readValue(content, new TypeReference<List<Tweet>>() {
        });
        assertThat(tweetList.get(0).getPublisher()).isEqualTo("publisher0");
        assertThat(tweetList.get(0).getTweet()).isEqualTo("tweet number: 0");
    }

    @Test
    public void shouldReturnAllDiscardedTweets() throws Exception {
        int numberOfTweets = 10;
        when(tweetService.listAllDiscardedTweets()).thenReturn(listOfTweets(numberOfTweets, true));

        MvcResult getResult = mockMvc.perform(get(DISCARDED_ENDPOINT))
                .andExpect(status().is(OK.value()))
                .andReturn();

        String content = getResult.getResponse().getContentAsString();
        assertThat(new ObjectMapper().readValue(content, List.class).size()).isEqualTo(numberOfTweets);
        List<Tweet> tweetList = new ObjectMapper().readValue(content, new TypeReference<List<Tweet>>() {});
        assertThat(tweetList.get(0).getPublisher()).isEqualTo("publisher0");
        assertThat(tweetList.get(0).getTweet()).isEqualTo("tweet number: 0");
    }

    private static MockHttpServletRequestBuilder newTweet(String publisher, String tweet) {
        return post(TWEET_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"publisher\": \"%s\", \"tweet\": \"%s\"}", publisher, tweet));
    }

    private static MockHttpServletRequestBuilder discardTweet(Long id) {
        return post(DISCARDED_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(format("{\"tweet\": \"%d\"}", id));
    }

    private static List<Tweet> listOfTweets(int numberOfTweets, boolean discarded) {
        List<Tweet> tweetList = new ArrayList<>();
        IntStream.range(0, numberOfTweets).forEach(x -> tweetList.add(
                new Tweet(String.format("publisher%d", x), String.format("tweet number: %d", x), discarded, new Date())));
        return tweetList;
    }
}
