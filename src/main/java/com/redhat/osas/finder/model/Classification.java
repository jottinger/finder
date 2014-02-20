package com.redhat.osas.finder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@ToString
@NoArgsConstructor
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
    String autoClassification;
    @Getter
    @Setter
    String userClassification;

}
