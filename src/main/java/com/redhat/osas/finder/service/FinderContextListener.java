package com.redhat.osas.finder.service;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


@javax.servlet.annotation.WebListener
public class FinderContextListener implements ServletContextListener {
@Inject Logger log;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		log.severe("CONTEXT DESTROYED");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.severe("CONTEXT INITIALIZED");
	}

}
