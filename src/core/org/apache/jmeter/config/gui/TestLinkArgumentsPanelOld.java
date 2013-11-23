package org.apache.jmeter.config.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestLinkArgumentsPanelOld extends AbstractConfigGui implements ActionListener {
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final String TEST_LINK_DEFINED_VARIABLES = "test_link_defined_variables_alsjidas";

	private static final long serialVersionUID = 240L;

	private static final String SET_PROPERTIES = "SET_PROPERTIES";
	private static final String TEST_LINK_URL = "TEST_LINK_URL";
	private static final String TEST_API_URL = "TEST_API_URL";
	private static final String TEST_API_KEY_URL = "TEST_API_URL";
	private JTextField testLinkUrl;
	private JTextField testLinkApiUrl;
	private JTextField testLinkApiKey;

	public TestLinkArgumentsPanelOld() {
		super();
		init();
	}

	@Override
	public String getLabelResource() {

		return TEST_LINK_DEFINED_VARIABLES;
	}

	@Override
	public TestElement createTestElement() {
		log.info("createTestElement");
		TestElement el = new ConfigTestElement();
		modifyTestElement(el);
		return el;
	}

	@Override
	public void configure(TestElement el) {
		log.info("configure");
		super.configure(el);

		PropertyIterator iter = el.propertyIterator();
		while (iter.hasNext()) {
			JMeterProperty prop = iter.next();

			log.info("set jmeter properties:" + prop);
		}
//		this.modifyTestElement(el);
	}

	@Override
	public void modifyTestElement(TestElement element) {
		log.info("modifyTestElement");
		super.configureTestElement(element);

		element.setProperty(new StringProperty(TEST_LINK_URL, testLinkUrl.getText()));
		element.setProperty(new StringProperty(TEST_API_URL, testLinkApiUrl.getText()));
		element.setProperty(new StringProperty(TEST_API_KEY_URL, testLinkApiKey.getText()));
		log.info("setted properties:" + testLinkUrl.getText()+"::"+testLinkApiUrl.getText()+":::"+testLinkApiKey.getText() );

	}

	/**
	 * Initialize the components and layout of this component.
	 */
	private void init() {
		setLayout(new BorderLayout());
		add(makeTitlePanel(), BorderLayout.NORTH);

		GridLayout experimentLayout = new GridLayout(3, 2);
		experimentLayout.setVgap(2);

		VerticalPanel mainJPanel = new VerticalPanel();
		testLinkUrl = new JTextField("http://localhost");
		testLinkApiUrl = new JTextField("/testlink/lib/api/xmlrpc/v1/xmlrpc.php");
		testLinkApiKey = new JTextField("");
		mainJPanel.add(createButtonPanel(new JLabel("TestLinkServer"), testLinkUrl));
		mainJPanel.add(createButtonPanel(new JLabel("TestAPIurl"), testLinkApiUrl));
		mainJPanel.add(createButtonPanel(new JLabel("TestAPIKey"), testLinkApiKey));
		mainJPanel.add(createSave(), BorderLayout.CENTER);
		add(mainJPanel, BorderLayout.WEST);

	}

	public JPanel createSave() {
		JPanel panel = new JPanel();
		JButton setProperty = new JButton("Set Properties"); //$NON-NLS-1$
		setProperty.setActionCommand(SET_PROPERTIES);
		setProperty.addActionListener(this);
		setProperty.setEnabled(true);
		panel.add(setProperty);
		return panel;
	}

	private JPanel createButtonPanel(JLabel label, JTextField jtextField) {
		GridLayout gridLayout = new GridLayout(1, 2);
		gridLayout.setVgap(50);
		gridLayout.setHgap(40);
		JPanel buttonPanel = new JPanel(gridLayout);
		
		buttonPanel.add(label);
		
		buttonPanel.add(jtextField);

		return buttonPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#makeTitlePanel()
	 */
	protected Container makeTitlePanel() {
		VerticalPanel titlePanel = new VerticalPanel();
		titlePanel.add(createTitleLabel());
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.setBorder(BorderFactory.createEtchedBorder());
		contentPanel.add(getNamePanel());

		titlePanel.add(contentPanel);
		return titlePanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log.info("testLinkUrl===" + testLinkUrl.getText());

		if (SET_PROPERTIES.equals(e.getActionCommand())) {
			log.info("Button was pressed:testLinkUrl===" + testLinkUrl.getText());

			createTestElement();
		}
		createTestElement();
	}

}
