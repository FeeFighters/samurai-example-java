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
import com.feefighers.model.Message;
import com.feefighers.model.Options;
import com.feefighers.model.Transaction;

public class SamuraiDemoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
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
		if("create-transaction".equals(action)) {
			SamuraiGateway gateway = getSamuraiGateway();
			Transaction transaction = gateway.processor().purchase(req.getParameter("payment_method_token"), Double.valueOf(req.getParameter("amount")), new Options());
			replaceVarsMap.put("REFERENCE_ID", transaction.getReferenceId());
			replaceVarsMap.put("AMOUNT", transaction.getAmount());
			replaceVarsMap.put("CURRENCY_CODE", transaction.getCurrencyCode());
			replaceVarsMap.put("CREATED_AT", String.valueOf(transaction.getCreatedAt()));
			replaceVarsMap.put("DESCRIPTOR", transaction.getDescriptor());
			replaceVarsMap.put("BILLING_REFERENCE", transaction.getBillingReference());
			replaceVarsMap.put("CUSTOMER_REFERENCE", transaction.getCustomerReference());
			replaceVarsMap.put("CUSTOM", transaction.getCustom());
			
			replaceVarsMap.put("LAST_FOUR_DIGITS", transaction.getPaymentMethod().getLastFourDigits());
			replaceVarsMap.put("EXPIRY_MONTH", String.valueOf(transaction.getPaymentMethod().getExpiryMonth()));
			replaceVarsMap.put("EXPIRY_YEAR", String.valueOf(transaction.getPaymentMethod().getExpiryYear()));
			
			StringBuilder errorSection = new StringBuilder();
			if(Boolean.TRUE.equals(transaction.getProcessorResponse().getSuccess())) {
				replaceVarsMap.put("SUCCESS", "Successful"); 
			} else {
				replaceVarsMap.put("SUCCESS", "Failed");
				
				errorSection.append("<div id='error_explanation'><h4>This transaction could not be processed:</h4><ul>");
				for(Message message : transaction.getProcessorResponse().getMessageList().getList()) {
					errorSection.append("<li>");
					errorSection.append(getHumanName(message.getContext() + " " + message.getKey()));
					errorSection.append("</li>");
				}
				errorSection.append("</ul></div>");
			}
			replaceVarsMap.put("ERROR_SECTION", errorSection.toString());
		}
	}	

	private static String getHumanName(String name) {
		name = StringUtils.replace(name, ".", " ");
		name = StringUtils.capitalize(name);
		return name;
	}
	
	private SamuraiGateway getSamuraiGateway() {
		final Properties config = new Properties();
		try {
			config.load(getClass().getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Could not open config.properties", e);
		}
				
		return new SamuraiGateway(config.getProperty("merchantKey"), 
				config.getProperty("merchantPassword"), config.getProperty("processorToken"));
	}

	private Map<String, String> generateReplaceVarsMap(HttpServletRequest req) {
		final Map<String, String> replaceVarsMap = new HashMap<String, String>();
		replaceVarsMap.put("URL", req.getRequestURL().toString());
		replaceVarsMap.put("PROCESSOR_TOKEN", getSamuraiGateway().getProcessorToken());

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
			if(entry.getValue() == null) {
				continue;
			}
			
			html = StringUtils.replace(html, "{" + entry.getKey().toUpperCase()
					+ "}", entry.getValue());
			html = StringUtils.replace(html, "{" + entry.getKey().toLowerCase()
					+ "}", entry.getValue());
		}
		return html;
	}
}
