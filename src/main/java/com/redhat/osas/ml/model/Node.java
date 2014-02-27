package com.redhat.osas.ml.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@Entity
@Cacheable
@NamedQueries(
        {
                @NamedQuery(name = "Node.getStrength",
                        query = "select n from Node n where n.from=:from and n.to=:to and n.layer=:layer")
        }
)
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
}
