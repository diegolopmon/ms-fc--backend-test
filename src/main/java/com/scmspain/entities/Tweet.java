package com.scmspain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String publisher;
    @Column(nullable = false)
    private String tweet;
    @JsonIgnore
    @Column(nullable = false)
    private boolean discarded;
    @JsonIgnore
    @Column(nullable = false)
    private Date publicationDate;
    @JsonIgnore
    @Column
    private Date discardedDate;
    @Column
    private Long pre2015MigrationStatus = 0L;

    public Tweet() {
    }

    public Tweet(String publisher, String tweet, boolean discarded, Date publicationDate) {
        this.publisher = publisher;
        this.tweet = tweet;
        this.discarded = discarded;
        this.publicationDate = publicationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public boolean getDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getDiscardedDate() {
        return discardedDate;
    }

    public void setDiscardedDate(Date discardedDate) {
        this.discardedDate = discardedDate;
    }

    public Long getPre2015MigrationStatus() {
        return pre2015MigrationStatus;
    }

    public void setPre2015MigrationStatus(Long pre2015MigrationStatus) {
        this.pre2015MigrationStatus = pre2015MigrationStatus;
    }

}
