/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.action;

import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

//  /web/#{Class}/#{method}.html -> 
//  /web/#{method}.html -> DefaultAction#index()
//  /web/ -> DefaultAction#index()
public class ActionHandler {
	static final Log LOG = LogFactory.getLog(ActionHandler.class);
	
	public static Pattern DEFAULT_PATTERN = Pattern.compile("/.*/(.*)/(.*).html");
	private Pattern pattern;
	private String actionPackage;
	
	public void setActionPackage(String actionPackage) {
		this.actionPackage = actionPackage;
	}
	
	public void setPattern(String regex) {
		this.pattern = Pattern.compile(regex);
	}
	
	Pattern getPattern() {
		return pattern != null ? pattern : DEFAULT_PATTERN;
	}
	
	public ActionContext handleAction(
			HttpRequest request, HttpResponse response, HttpContext context) {
//		Matcher matcher = getPattern().matcher(request.getRequestLine().getUri());
//		if (matcher.find()) {
//			String type = matcher.group(1);
//			String methodName =  matcher.group(2);
//			ActionDefine define = new ActionDefine(getActionName(type));
//			Action action = DI.configure(define).getBean(define.getId(), Action.class);
//			LOG.debug("type=" + type + ", action=" + action + ", method=" + methodName);
//			if (action != null) {
//				action.setRequest(request);
//				action.setResponse(response);
//				action.setContext(context);
//				Method method = ClassUtils.getMethod(
//						action.getClass(), methodName);
//				LOG.debug("method=" + method);
//				try {
//					Object result = method.invoke(action);
//					if (result != null && result instanceof ActionContext) {
//						ActionContext actionContext = ((ActionContext)result);
//						if (actionContext != null) return actionContext;
//					}
//				} catch (Exception e) {
//					//TODO handle Exceptions.
//					LOG.trace(e.getMessage());
//				}
//			}
//		}
		return new ActionContext();
	}
	
	protected String getActionName(String type) {
		if (type != null && type.length() >= 2) {
			String name = type.substring(0,1).toUpperCase()+type.substring(1,type.length());
			return actionPackage + "." + name + "Action";
		} else {
			return actionPackage + "." + type + "Action";
		}
	}
}
