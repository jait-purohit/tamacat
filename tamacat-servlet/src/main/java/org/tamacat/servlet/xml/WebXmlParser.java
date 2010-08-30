package org.tamacat.servlet.xml;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
	private Document document;
	private XPath xpath;
	private ClassLoader loader;
	
	public WebXmlParser(ClassLoader loader) {
		this.loader = loader;
	}
	
	public WebXmlParser() {
		this(ClassUtils.getDefaultClassLoader());
	}
	
	public WebApp parse(String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			URL url = ClassUtils.getURL(path, loader);
			if (url == null) {
				url = new URL("file:" + path);
			}
			
			document = builder.parse(url.openStream());
			xpath = XPathFactory.newInstance().newXPath();
			loadDisplayName();
			loadDescription();
			loadContextParames();
			loadServlets();
			loadServletMappings();
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
	
	void loadServlets() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/servlet/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		List<ServletDefine> defines = getServletDefines(nodes);
		webApp.setServlets(defines);
	}
	
	void loadServletMappings() throws XPathExpressionException {
		XPathExpression expr = xpath.compile("//web-app/servlet-mapping/node()");
		NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
		List<ServletMapping> mappings = getServletMappings(nodes);
		webApp.setServletMappings(mappings);
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
	
	List<ServletDefine> getServletDefines(NodeList nodes) {
		List<ServletDefine> defines = new ArrayList<ServletDefine>();
		ServletDefine define = null;
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String tagName = node.getNodeName();
			if ("servlet-name".equals(tagName)) {
				if (define != null) {
					defines.add(define);
				}
				define = new ServletDefine();
				String servletName = node.getTextContent();
				define.setServletName(servletName);
			} else if ("servlet-class".equals(tagName)) {
				String servletClass = node.getTextContent();
				define.setServletClass(servletClass);
			} else if ("init-param".equals(tagName)) {
				NodeList nlist = node.getChildNodes();
				Map<String,String> params = getParams(nlist);
				define.setInitParams(params);
			}
		}
		if (define != null) {
			defines.add(define);
		}
		return defines;
	}
	
	List<ServletMapping> getServletMappings(NodeList nodes) {
		List<ServletMapping> mappings = new ArrayList<ServletMapping>();
		String servletName = null;
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String tagName = node.getNodeName();
			if ("servlet-name".equals(tagName)) {
				servletName = node.getTextContent();
			} else if ("url-pattern".equals(tagName)) {
				String urlPattern = node.getTextContent();
				ServletMapping mapping = new ServletMapping();
				mapping.setServletName(servletName);
				mapping.setUrlPattern(urlPattern);
				mappings.add(mapping);
			}
		}
		return mappings;
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
