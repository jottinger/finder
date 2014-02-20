package com.redhat.osas.finder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@ToString
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    int id;
    @NotNull
    @Getter
    @Setter
    @Size(min = 7, max = 20)
    String username;
    @NotNull
    @Getter
    @Setter
    @Size(min = 8)
    String password;
    @Getter
    @Setter
    @NotNull
    @Email
    String email;
    @Getter
    @Setter
    @URL
    String url;
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    Date lastActivity;
}
