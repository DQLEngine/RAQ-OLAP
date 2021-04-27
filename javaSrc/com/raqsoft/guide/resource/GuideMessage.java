package com.raqsoft.guide.resource;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.raqsoft.common.MessageManager;

public class GuideMessage {

	private GuideMessage() {}

	public static MessageManager get() {
		return MessageManager.getManager("com.raqsoft.guide.resource.guideMessage");
	}

	public static MessageManager get(Locale locale) {
		return MessageManager.getManager("com.raqsoft.guide.resource.guideMessage", locale);
	}

	public static MessageManager get(HttpServletRequest req) {
		return get(req.getLocale());
	}

}
