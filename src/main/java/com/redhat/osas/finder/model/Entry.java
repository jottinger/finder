package com.redhat.osas.finder.model;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.redhat.osas.finder.util.SyndUtil.convertToString;

/**
 * Entity implementation class for Entity: Entry
 */
@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Entry.findByUri", query = "select e from Entry e where e.uri=:uri and e.feed=:feed"),
        @NamedQuery(name = "Entry.getOldEntries", query = "select e from Entry e where e.feed=:feed and e.lastRead <> :now"),
        @NamedQuery(name = "Entry.allOrdered", query = "select e from Entry e order by e.created"),
})
@ToString(exclude = "feed")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    Date created;
    @Getter
    @Setter
    @ManyToOne
    @NotNull
    @XmlTransient
    Feed feed;
    @ElementCollection
    @Getter
    @Setter
    Set<String> tags;

    /**
     * Populates the entry's fields
     */
    public Entry(Feed feed, SyndEntry syndEntry) {
        setFeed(feed);
        setTitle(syndEntry.getTitle());
        setUri(syndEntry.getUri());
        setDescription(convertToString(syndEntry.getDescription()));
        setContent(convertToString(syndEntry.getContents()));
        setCreated(syndEntry.getPublishedDate());
        Set<String> tags = new HashSet<>();
        for (SyndCategory syndCategory : syndEntry.getCategories()) {
            tags.add(syndCategory.getName().trim().toLowerCase());
        }
        setTags(tags);
    }
}
