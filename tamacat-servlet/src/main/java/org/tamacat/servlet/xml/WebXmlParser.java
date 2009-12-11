package org.tamacat.servlet.xml;

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
	
	void loadServlet() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/servlet/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		ServletDefine servletDefine = getServletDefine(nodes);
		webApp.getServlets().add(servletDefine);
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
			}
		}
		return servletDefine;
	}
	
	static class WebXml {
		enum Tag {
			web_app("web-app"),
			display_name("display-name"),
			description("description"),
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
