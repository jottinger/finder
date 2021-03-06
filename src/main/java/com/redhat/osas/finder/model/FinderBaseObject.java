package com.redhat.osas.finder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@ToString
@NoArgsConstructor
public abstract class FinderBaseObject implements Serializable {
    private static final long serialVersionUID = -2477835675184821359L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    int id;
    @Getter
    @Setter
    String title;
    @Getter
    @Setter
    @URL
    @NotNull
    String uri;
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    Date lastRead;
}
