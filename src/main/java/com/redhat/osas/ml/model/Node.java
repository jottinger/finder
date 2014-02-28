package com.redhat.osas.ml.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@SuppressWarnings("deprecation")
@Entity
@Cacheable
@NamedQueries(
        {
                @NamedQuery(name = "Node.getStrength",
                        query = "select n from Node n where n.from=:from and n.to=:to and n.layer=:layer"),
                @NamedQuery(name = "Node.byFromAndLayer",
                        query = "select n from Node n where n.from=:from and n.layer=:layer"),
                @NamedQuery(name = "Node.byToAndLayer",
                        query = "select n from Node n where n.to=:to and n.layer=:layer"),
                @NamedQuery(name = "Node.getAllOutputsByLayer",
                        query = "select distinct n.to from Node n where n.layer=:layer"),
        }
)
@org.hibernate.annotations.Table(appliesTo = "Node", indexes = {
        @org.hibernate.annotations.Index(name = "fromLayer", columnNames = {"from_id", "layer"}),
        @org.hibernate.annotations.Index(name = "toLayer", columnNames = {"to_id", "layer"}),
})
public class Node implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    int id;
    @ManyToOne
    @Getter
    @Setter
    @NotNull
    Token from;
    @ManyToOne
    @Getter
    @Setter
    @NotNull
    Token to;
    @Getter
    @Setter
    @NotNull
    double strength;
    @Setter
    @Getter
    @NotNull
    Layer layer;

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.printf("Node:[id=%4d, from=%s, to=%s, strength=%15f, layer=%s]",
                getId(), getFrom(), getTo(), getStrength(), getLayer());
        return sw.toString();
    }
}
