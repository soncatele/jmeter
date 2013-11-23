package org.apache.jmeter.config.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.protocol.http.sampler.SoapSampler;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkPublishTestCase extends AbstractVisualizer {
	private static final Logger log = LoggingManager.getLoggerForClass();
	public static String TEST_ID = "TEST_ID";

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
		for (AssertionResult ar : assertionResults) {
			if (ar.isError() || ar.isFailure()) {
				log.info("this test has to be published in test link:" + ar.getFailureMessage());
				log.info("****************************");
				log.info(ToStringBuilder.reflectionToString(ar));
				log.info("****************************");
			} else {
				log.info("This one failed:" + ToStringBuilder.reflectionToString(ar));
			}
		}
		
		JMeterVariables variables = JMeterContextService.getContext().getVariables();
		String urlData = variables.get(TestLinkArgumentsPanel.TEST_LINK_URL)+variables.get(TestLinkArgumentsPanel.TEST_API_URL);
		log.info("urlData= "+ urlData);
		log.info("TEST_API_URL= "+ variables.get(TestLinkArgumentsPanel.TEST_API_URL));
		log.info("TEST_API_KEY_URL= "+ variables.get(TestLinkArgumentsPanel.TEST_API_KEY_URL));
		
		
		 SoapSampler sampler = new  SoapSampler();
		 
         sampler.setURLData(urlData);
//         sampler.setXmlData(soapXml.getText());
//         sampler.setXmlFile(soapXmlFile.getFilename());
//         sampler.setSOAPAction(soapAction.getText());
//         sampler.setSendSOAPAction(sendSoapAction.isSelected());
         boolean keepAlive=true;;
		sampler.setUseKeepAlive(keepAlive);
//		sampler.
		log.info("testId="+jTextField.getText());
	}

	private void init() {
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		setBorder(makeBorder());

		box.add(makeTitlePanel());
		box.add(createPanel());
		// box.add(createFieldPanel());
		// box.add(createTypePanel());
		add(box, BorderLayout.NORTH);
		// add(createStringPanel(), BorderLayout.CENTER);
	}

	JTextField jTextField = null;

	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 2));
		panel.add(new JLabel("Test Id:"));
		jTextField = new JTextField("test_id");
		panel.add(jTextField);
		JButton jButton = new JButton("Set Test Id");
		panel.add(jButton);
		return panel;
	}

	@Override
	public void clearData() {
	

	}

}
