package org.apache.jmeter.config.gui;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkService {
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final String ARRAY_SEPARATOR = "::";

	private static String REG_ID = "<member><name>id</name><value><string>(.*)</string></value></member";

	private String url;
	private String devKey;
	private HttpClient client = new HttpClient();

	public TestLinkService(String url, String devKey) {
		this.url = url;
		this.devKey = devKey;
		client = new HttpClient();
	}

	public String getTestPlanByName(String projectName, String testPlanName) {

		String result = null;
		StringBuilder req = createGetTestPlanByNameReq(projectName, testPlanName);
		PostMethod method = new PostMethod(url);
		String reqBody = req.toString();
		method.setRequestBody(reqBody);
		result = extractResult(REG_ID, method);
		return result;
	}

	public String createBuild(String testplanid, String buildname, String testplanname) {
		String result = null;
		StringBuilder req = createBuildReq(testplanid, buildname, testplanname);
		PostMethod method = new PostMethod(url);
		String reqBody = req.toString();
		method.setRequestBody(reqBody);
		result = extractResult(null, method);
		return result;
	}

	/**
	 * This create a new TestCase or is updating if exists.
	 * 
	 * @param testplanid
	 * @param testcaseexternalid
	 * @param buildid
	 * @param notes
	 * @param status
	 * @param platform
	 * @return
	 */
	public String reportTCResult(String testplanid, String testcaseexternalid,  String notes, String status, String platform) {
		String result = null;
		String buildid=getLastBuildForTestPlanId(testplanid);
		log.info("Last Build id="+buildid);
		StringBuilder req = reportTCResultRequest(testplanid, testcaseexternalid, buildid, notes, status, platform);
		PostMethod method = new PostMethod(url);
		String reqBody = req.toString();
		method.setRequestBody(reqBody);
		result = extractResult(null, method);
		return result;
	}

	public String getLastBuildForTestPlanId(String testplanid) {
		String result = null;
		StringBuilder req = getBuildsForTestPlanReq(testplanid);
		PostMethod method = new PostMethod(url);
		String reqBody = req.toString();
		method.setRequestBody(reqBody);
		result = extractResult(REG_ID, method, ARRAY_SEPARATOR);
		String[] buildsIds = result.split(ARRAY_SEPARATOR);
		int theBigestId = 0;
		for (String buildId : buildsIds) {
			try {
				int i = Integer.parseInt(buildId);
				if (i > theBigestId)
					theBigestId = i;
			} catch (Exception e) {
				// igone it:D
			}
		}
		if (theBigestId > 0) {
			result = "" + theBigestId;
		}

		return result;
	}

	public static void main(String[] args) {
		String projectName = "Endava";
		String testPlanName = "WebSite";
		String url = "http://192.168.1.120/testlink/lib/api/xmlrpc/v1/xmlrpc.php";
		TestLinkService tl = new TestLinkService(url, "fb1251872917d6a0f77622d8f579b2ad");
		// System.out.println(tl.getTestPlanByName(projectName, testPlanName));
		// System.out.println("createdBuild:" + tl.createBuild("16", "OVidiu BuildName", "testplanname"));
		System.out.println(tl.getLastBuildForTestPlanId("16"));

		// String testplanid="16";
		// String testcaseexternalid="en-3";
		// String buildid="53";
		// String notes="Modified by Ovidiu";
		// String status="f";
		// String platform="Windows 8";
		// System.out.println("created:" + tl.reportTCResult(testplanid, testcaseexternalid, buildid, notes, status, platform));

	}

	private StringBuilder createGetTestPlanByNameReq(String projectName, String testPlanName) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("METHOD_NAME", "tl.getTestPlanByName");
		params.put("devKey", devKey);
		params.put("testprojectname", projectName);
		params.put("testplanname", testPlanName);
		StringBuilder req = createRequest(params);
		return req;
	}

	private StringBuilder createBuildReq(String testplanid, String buildname, String testplanname) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("METHOD_NAME", "tl.createBuild");
		params.put("devKey", devKey);
		params.put("testplanid", testplanid);
		params.put("buildname", buildname);
		params.put("testplanname", testplanname);
		params.put("buildnotes", "Created via XML-RPC API from JMeter");
		StringBuilder req = createRequest(params);
		return req;
	}

	private StringBuilder reportTCResultRequest(String testplanid, String testcaseexternalid, String buildid, String notes, String status,
			String platform) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("METHOD_NAME", "tl.reportTCResult");
		params.put("devKey", devKey);
		params.put("testplanid", testplanid);// configured
		params.put("testcaseexternalid", testcaseexternalid);// configured
		params.put("buildid", buildid);// last one
		params.put("notes", notes);
		params.put("status", status);// Passes, Failed
		params.put("platformname", platform);// Windows 8 - by hand
		params.put("overwrite", "true");
		StringBuilder req = createRequest(params);
		return req;
	}

	private StringBuilder getBuildsForTestPlanReq(String testplanid) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("METHOD_NAME", "tl.getBuildsForTestPlan");
		params.put("devKey", devKey);
		params.put("testplanid", testplanid);// configured

		StringBuilder req = createRequest(params);
		return req;
	}

	private StringBuilder createRequest(HashMap<String, String> params) {

		StringBuilder req = new StringBuilder();
		req.append("<?xml version=\"1.0\"?>");
		req.append("<methodCall>");
		req.append("<methodName>" + params.get("METHOD_NAME") + "</methodName>");
		req.append("<params>");
		req.append("	<param><value><struct>");
		for (String key : params.keySet()) {
			req.append(" 		<member><name>" + key + "</name><value><string>" + params.get(key) + "</string></value></member>");
		}
		req.append("	</struct></value></param>");
		req.append("</params></methodCall>");
		return req;
	}

	private String extractResult(String regId, PostMethod method, String... separator) {
		String result = "";
		try {
			int executeMethod = client.executeMethod(method);
			if (executeMethod == 200) {

				String responseBodyAsString = method.getResponseBodyAsString();

				if (regId != null) {
					Pattern p = Pattern.compile(regId);
					Matcher matcher = p.matcher(responseBodyAsString);
					if (separator != null && separator.length > 0 && separator[0] != null) {
						while (matcher.find()) {
							result += matcher.group(1);
							result += separator[0];

						}
					} else {
						if (matcher.find()) {
							result = matcher.group(1);
						} else {
							result = responseBodyAsString;
						}
					}
				} else {
					result = responseBodyAsString;
				}
			}
		} catch (HttpException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return result;
	}
}
