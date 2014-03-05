package com.redhat.osas.finder.model;

import com.redhat.osas.ml.model.Token;
import lombok.*;
import lombok.experimental.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@NamedQueries(
        {
                @NamedQuery(name = "Classification.deleteForEntry", query = "delete from Classification c where c.entry=:entry"),
                @NamedQuery(name = "Classification.findUnclassifiedEntries", query = "select c from Classification c where c.autoClassification=null"),
        }
)
public class Classification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    int id;
    @ManyToOne
    @Getter
    @Setter
    @NotNull
    Entry entry;
    @ManyToOne
    @Getter
    @Setter
    @NotNull
    User user;
    @Getter
    @Setter
    @ManyToOne
    Token autoClassification;
    @Getter
    @Setter
    @ManyToOne
    Token userClassification;
    @Getter
    @Setter
    @Column(length = 1024)
    String scoresJSON;
}
