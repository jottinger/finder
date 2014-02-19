package com.redhat.osas.finder.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@ToString
@NoArgsConstructor
public abstract class FinderBaseObject implements Serializable {
	/**
	 * 
	 */
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
	String uri;
	@Getter @Setter
	@Temporal(TemporalType.TIMESTAMP)
	Date lastRead;
}
