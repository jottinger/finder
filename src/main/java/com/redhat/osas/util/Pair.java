package com.redhat.osas.util;

import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Pair<K extends Serializable, V extends Serializable> {
    @Setter
    @Getter
    K k;
    @Setter
    @Getter
    V v;
}
