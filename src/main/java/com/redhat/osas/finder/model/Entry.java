package com.redhat.osas.finder.model;

import com.sun.syndication.feed.synd.SyndEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

import static com.redhat.osas.finder.util.SyndUtil.convertToString;

/**
 * Entity implementation class for Entity: Entry
 */
@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Entry.findByUri", query = "select e from Entry e where e.uri=:uri and e.feed=:feed"),
        @NamedQuery(name = "Entry.clearOldEntries", query = "delete from Entry e where e.feed=:feed and e.lastRead <> :now"),
})
@ToString(exclude = "feed")
public class Entry extends FinderBaseObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5300856257692046276L;
    @Lob
    @Getter
    @Setter
    String content;
    @Lob
    @Getter
    @Setter
    String description;
    @Getter
    @Setter
    @ManyToOne
    Feed feed;

    /**
     * Populates the entry's fields
     */
    public Entry(Feed feed, SyndEntry syndEntry) {
        setFeed(feed);
        setTitle(syndEntry.getTitle());
        setUri(syndEntry.getUri());
        setDescription(convertToString(syndEntry.getDescription()));
        setContent(convertToString(syndEntry.getContents()));
    }
}
