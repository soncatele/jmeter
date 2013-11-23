package com.endava.jmeter.testlink;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkPublishTestCase extends AbstractVisualizer {
	private static final Logger log = LoggingManager.getLoggerForClass();
	public static String TEST_ID = "TEST_ID";
	JTextField jTextField = null;
	JTextField notes = null;
	JTextField errors = null;

	JTextField platform = null;
	JPanel mainPanel = null;

	public TestLinkPublishTestCase() {
		init();
	}

	@Override
	public String getLabelResource() {
		return "test_link_publish_testcase";
	}

	public void add(SampleResult sample) {
		log.info("add(SampleResult sample)::::" + ToStringBuilder.reflectionToString(sample));

		AssertionResult[] assertionResults = sample.getAssertionResults();
		JMeterVariables variables = JMeterContextService.getContext().getVariables();
		String urlData = variables.get(TestLinkArgumentsPanel.TEST_LINK_URL) + variables.get(TestLinkArgumentsPanel.TEST_API_URL);

		TestLinkService tlS = new TestLinkService(urlData, variables.get(TestLinkArgumentsPanel.TEST_API_KEY));

		String status = null;
		for (AssertionResult ar : assertionResults) {
			if (ar.isError() || ar.isFailure()) {
				log.info("this test has to be published in test link:" + ar.getFailureMessage());
				log.info("****************************");
				log.info(ToStringBuilder.reflectionToString(ar));
				log.info("****************************");

				status = "f";
			} else {

				status = "p";
				log.info("This one is success:" + ToStringBuilder.reflectionToString(ar));
			}
		}
		if (status != null) {
			String projectName = variables.get(TestLinkTestProject.PROJECT_NAME);
			String testPlanName = variables.get(TestLinkTestProject.PLAN_NAME);

			String testPlanId_testProjectId = tlS.getTestPlanByName(projectName, testPlanName);
			String testPlanId = "";
			String testProjectId = "";
			if (StringUtils.isNotBlank(testPlanId_testProjectId)) {
				String[] arr = testPlanId_testProjectId.split(TestLinkService.ARRAY_SEPARATOR);
				if (arr != null && arr.length == 2) {
					testPlanId = arr[0];
					testProjectId = arr[1];
				}else{
					addError("testPlanId_testProjectId not OK:"+testPlanId_testProjectId );
				}
			}
			if (StringUtils.isNotBlank(testPlanId)) {
				String buildid = tlS.getLastBuildForTestPlanId(testPlanId);
				String testcaseexternalid = jTextField.getText();

				log.info("urlData= " + urlData);
				log.info("TEST_API_URL= " + variables.get(TestLinkArgumentsPanel.TEST_API_URL));
				log.info("TEST_API_KEY= " + variables.get(TestLinkArgumentsPanel.TEST_API_KEY));
				log.info("PROJECT_NAME= " + projectName);
				log.info("PLAN_NAME= " + testPlanName);
				log.info("testPlanId= " + testPlanId);
				log.info("testProjectId= " + testProjectId);
				log.info("buildid= " + buildid);
				log.info("testcaseexternalid= " + testcaseexternalid);
				log.info("status= " + status);

				String reportTCResult = tlS.reportTCResult(testPlanId,testProjectId, testcaseexternalid, notes.getText(), status, platform.getText());
				log.info("reportTCResult=" + reportTCResult);
				log.info("************************************************");
			}else{
				String errorMsg = "testPlanId is blank";
				addError(errorMsg);
			}
		}

	}

	private void addError(String errorMsg) {
		if(errors==null){
			errors=new JTextField(errorMsg);
			mainPanel.add(errors);
		}else{
			errors.setText(errorMsg);
			
		}
	}

	private void init() {
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		setBorder(makeBorder());

		box.add(makeTitlePanel());
		mainPanel = createPanel();
		box.add(mainPanel, BorderLayout.WEST);
		add(box, BorderLayout.NORTH);

	}

	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("Test Id:"));
		jTextField = new JTextField("test_id");
		panel.add(jTextField);

		panel.add(new JLabel("Notes:"));
		notes = new JTextField("notes");
		panel.add(notes);

		// platform
		panel.add(new JLabel("Platform:"));
		platform = new JTextField("platform");
		panel.add(platform);

		return panel;
	}

	@Override
	public void clearData() {

	}

}
