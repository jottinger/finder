package com.redhat.osas.finder.model;

import com.sun.syndication.feed.synd.SyndFeed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Feed.findByUri", query = "select f from Feed f where f.uri=:uri and f.active=true"),
        @NamedQuery(name = "Feed.findFeedsToRead", query = "select f from Feed f where f.active=true and f.nextRead < :now"),
        @NamedQuery(name = "Feed.findAll", query = "select f from Feed f"),
})
@ToString
@XmlRootElement
public class Feed extends FinderBaseObject {
    private static final long serialVersionUID = 7759440105124773514L;
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    Date nextRead;
    @Getter
    @Setter
    boolean active;

    /**
     * Populates the feed's data - NOT the entries' data
     *
     * @param syndFeed
     */
    public Feed(SyndFeed syndFeed) {
        setTitle(syndFeed.getTitle());
        setUri(syndFeed.getUri());
    }
}
