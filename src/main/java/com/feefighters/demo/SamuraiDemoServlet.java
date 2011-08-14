package com.feefighters.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.feefighers.SamuraiGateway;
import com.feefighers.model.Options;

public class SamuraiDemoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter writer = null;

		final String action = StringUtils.defaultIfBlank(
				req.getParameter("action"), "index");

		try {
			writer = resp.getWriter();

			String html = IOUtils.toString(getClass().getResourceAsStream(
					"/" + action + ".html"));

			final Map<String, String> replaceVarsMap = generateReplaceVarsMap(req);
			
			processAction(action, req, replaceVarsMap);
			
			html = replaceVars(html, replaceVarsMap);
			writer.write(html);

		} finally {
			if (writer != null) {
				IOUtils.closeQuietly(writer);
			}
		}
	}

	private void processAction(String action, HttpServletRequest req, Map<String, String> replaceVarsMap) throws IOException {
//		if("new-transaction".equals(action)) {
//			SamuraiGateway gateway = getSamuraiGateway();
//			gateway.processor().purchase(req.getParameter("payment_method_token"), Double.valueOf(req.getParameter("amount")), new Options());
//			replaceVarsMap.put("transaction", "TODO");
//		}
	}

	private SamuraiGateway getSamuraiGateway() throws IOException {
		final Properties config = new Properties();
		config.load(getClass().getResourceAsStream("/config.properties"));
				
		return new SamuraiGateway(config.getProperty("merchantKey"), 
				config.getProperty("merchantPassword"), config.getProperty("processorToken"));
	}

	private Map<String, String> generateReplaceVarsMap(HttpServletRequest req) {
		final Map<String, String> replaceVarsMap = new HashMap<String, String>();
		replaceVarsMap.put("URL", req.getRequestURL().toString());

		final Enumeration en = req.getParameterNames();
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = req.getParameter(paramName);

			replaceVarsMap.put(paramName, paramValue);
		}

		return replaceVarsMap;
	}

	private String replaceVars(String html, Map<String, String> replaceVarsMap) {
		for (Map.Entry<String, String> entry : replaceVarsMap.entrySet()) {
			html = StringUtils.replace(html, "{" + entry.getKey().toUpperCase()
					+ "}", entry.getValue());
			html = StringUtils.replace(html, "{" + entry.getKey().toLowerCase()
					+ "}", entry.getValue());
		}
		return html;
	}
}
