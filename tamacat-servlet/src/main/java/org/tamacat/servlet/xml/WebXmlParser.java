package org.tamacat.servlet.xml;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.tamacat.util.ClassUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WebXmlParser {

	private WebApp webApp = new WebApp();
	Document document;
	XPath xpath;
	
	public WebXmlParser() {}
	
	public WebApp parse(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(ClassUtils.getURL(path).openStream());
			xpath = XPathFactory.newInstance().newXPath();
			loadDisplayName();
			loadDescription();
			loadContextParames();
			loadServlet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return webApp;
	}
	
	void loadDisplayName() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/display-name/text()");
		String displayName = (String) expr.evaluate(document, XPathConstants.STRING);
		webApp.setDisplayName(displayName);
	}
	
	void loadDescription() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/description/text()");
		String description = (String) expr.evaluate(document, XPathConstants.STRING);
		webApp.setDescription(description);
	}
	
	void loadContextParames() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/context-param/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		Map<String,String> params = getParams(nodes);
		webApp.setContextParams(params);
	}
	
	void loadServlet() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/servlet/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		ServletDefine servletDefine = getServletDefine(nodes);
		webApp.getServlets().add(servletDefine);
	}
	
	void loadServletMapping() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/servlet-mapping/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		ServletMapping mapping = getServletMapping(nodes);
		webApp.addServletMapping(mapping);
	}
	
	Map<String,String> getParams(NodeList nodes) {
		Map<String,String> params = new LinkedHashMap<String, String>();
		String name = null;
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String tagName = node.getNodeName();
			if ("param-name".equals(tagName)) {
				name = node.getTextContent();
			} else if ("param-value".equals(tagName)) {
				String value = node.getTextContent();
				if (name != null) {
					params.put(name, value);
				}
				name = null;
			}
		}
		return params;
	}
	
	ServletDefine getServletDefine(NodeList nodes) {
		ServletDefine servletDefine = new ServletDefine();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String tagName = node.getNodeName();
			if ("servlet-name".equals(tagName)) {
				String servletName = node.getTextContent();
				servletDefine.setServletName(servletName);
				
			} else if ("servlet-class".equals(tagName)) {
				String servletClass = node.getTextContent();
				servletDefine.setServletClass(servletClass);
			} else if ("init-param".equals(tagName)) {
				NodeList nlist = node.getChildNodes();
				Map<String,String> params = getParams(nlist);
				servletDefine.setInitParams(params);
			}
		}
		return servletDefine;
	}
	
	ServletMapping getServletMapping(NodeList nodes) {
		ServletMapping mapping = new ServletMapping();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String tagName = node.getNodeName();
			if ("servlet-name".equals(tagName)) {
				String servletName = node.getTextContent();
				mapping.setServletName(servletName);
				
			} else if ("url-pattern".equals(tagName)) {
				String urlPattern = node.getTextContent();
				mapping.setUrlPattern(urlPattern);
			}
		}
		return mapping;
	}
	
	static class WebXml {
		enum Tag {
			web_app("web-app"),
			display_name("display-name"),
			description("description"),
			init_param("init-param"),
			param_name("param-name"),
			param_value("param-value"),
			context_param("context-param"),
			servlet("servlet"),
			servlet_mapping("servlet-mapping"),
			servlet_name("servlet-name"),
			url_pattern("url-pattern");
			
			private final String tagName;
			Tag(String tagName) {
				this.tagName = tagName;
			}
			
			public String getTagName() {
				return tagName;
			}
		}
	}
}
