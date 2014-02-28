package com.redhat.osas.ml.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@Entity
@Cacheable
@NoArgsConstructor
@NamedQueries(
        {
                @NamedQuery(name = "Token.findByWord", query="select t from Token t where t.word=:word")
        }
)
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

    public String toString() {
        StringWriter sw=new StringWriter();
        PrintWriter out=new PrintWriter(sw);
        out.printf("Token[id=%4d, word=%15s]", getId(), getWord());
        return sw.toString();
    }
}
