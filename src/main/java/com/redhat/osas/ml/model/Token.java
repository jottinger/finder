package com.redhat.osas.ml.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@SuppressWarnings("deprecation")
@Entity
@Cacheable
@NoArgsConstructor
@NamedQueries(
        {
                @NamedQuery(name = "Token.findByWord", query = "select t from Token t where t.word=:word"),
                @NamedQuery(name = "Token.findByHashCode", query = "select t from Token t where t.hashCode=:hashCode"),
        }
)
@org.hibernate.annotations.Table(appliesTo = "Token", indexes = {
        @org.hibernate.annotations.Index(name = "hashCode", columnNames = {"hashCode"}),
})
public class Token implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    int id;

    @Getter
    @Setter
    @NotNull
    @Column(unique = true)
    String word;

    @Getter
    @Setter
    Integer hashCode;

    @PrePersist
    public void setHashcodeFromWord() {
        hashCode = word.hashCode();
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.printf("Token[id=%4d, word=%15s]", getId(), getWord());
        return sw.toString();
    }
}
