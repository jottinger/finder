package com.redhat.osas.finder.util;

import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;

public class SyndUtil {
	public static String convertToString(SyndContent syndContent) {
		return syndContent.getValue();
	}

	public static String convertToString(List<SyndContent> syndContent) {
		StringBuilder sb = new StringBuilder();
		for (SyndContent content : syndContent) {
			sb.append(convertToString(content));
		}
		return sb.toString();
	}

}
