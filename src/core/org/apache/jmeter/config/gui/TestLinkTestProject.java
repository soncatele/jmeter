package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkTestProject extends TestLinkArgumentsPanel {
	private static final long serialVersionUID = 2496L;
	private static final Logger log = LoggingManager.getLoggerForClass();

	private static final String TITLE = "test_link_test_project";

	public static final String PROJECT_NAME = "PROJECT_NAME";
	public static final String PLAN_NAME = "PLAN_NAME";

	/* Implements JMeterGUIComponent.createTestElement() */
	@Override
	public TestElement createTestElement() {
		log.info("createTestElement");

		tableModel.addRow(new Argument(PROJECT_NAME, "projectName"));
		tableModel.addRow(new Argument(PLAN_NAME, "planName"));

		Arguments args = new Arguments();
		modifyTestElement(args);
		return args;
	}

	@Override
	public String getLabelResource() {
		return TITLE; // $NON-NLS-1$
	}

	public TestLinkTestProject() {
		super(JMeterUtils.getResString(TITLE), null, true, true);// $NON-NLS-1$
	}
}
