package org.apache.jmeter.config.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.Functor;
import org.apache.log.Logger;

/**
 * A GUI panel allowing the user to enter name-value argument pairs. These
 * arguments (or parameters) are usually used to provide configuration values
 * for some other component.
 *
 */
public class MyPanelll extends AbstractConfigGui implements ActionListener {
	private static final Logger log = LoggingManager.getLoggerForClass();
    private static final long serialVersionUID = 240L;

    /** The title label for this component. */
    private JLabel tableLabel;

    /** The table containing the list of arguments. */
    private transient JTable table;

    /** The model for the arguments table. */
    protected transient ObjectTableModel tableModel; // will only contain Argument or HTTPArgument

    /** A button for adding new arguments to the table. */
    private JButton add;

    /** A button for removing arguments from the table. */
    private JButton delete;

    /**
     * Added background support for reporting tool
     */
    private Color background;

    /**
     * Boolean indicating whether this component is a standalone component or it
     * is intended to be used as a subpanel for another component.
     */
    private final boolean standalone;

    /** Button to move a argument up*/
    private JButton up;

    /** Button to move a argument down*/
    private JButton down;

    private final boolean enableUpDown;

    /** Command for adding a row to the table. */
    private static final String ADD = "add"; // $NON-NLS-1$

    /** Command for adding rows from the clipboard */
    private static final String ADD_FROM_CLIPBOARD = "addFromClipboard"; // $NON-NLS-1$

    /** Command for removing a row from the table. */
    private static final String DELETE = "delete"; // $NON-NLS-1$

    /** Command for moving a row up in the table. */
    private static final String UP = "up"; // $NON-NLS-1$

    /** Command for moving a row down in the table. */
    private static final String DOWN = "down"; // $NON-NLS-1$

    /** Command for showing detail. */
    private static final String DETAIL = "detail"; // $NON-NLS-1$

    public static final String COLUMN_RESOURCE_NAMES_0 = "name"; // $NON-NLS-1$

    public static final String COLUMN_RESOURCE_NAMES_1 = "value"; // $NON-NLS-1$

    public static final String COLUMN_RESOURCE_NAMES_2 = "description"; // $NON-NLS-1$

    /**
     * Create a new ArgumentsPanel as a standalone component.
     */
    public MyPanelll() {
        this(JMeterUtils.getResString("user_defined_variables"),null, true, true);// $NON-NLS-1$
    }

    /**
     * Create a new ArgumentsPanel as an embedded component, using the specified
     * title.
     *
     * @param label
     *            the title for the component.
     */
    public MyPanelll(String label) {
        this(label, null, true, false);
    }
    
    /**
     * Create a new ArgumentsPanel as an embedded component, using the specified
     * title.
     *
     * @param label
     *            the title for the component.
     * @param enableUpDown Add up/down buttons
     */
    public MyPanelll(String label, boolean enableUpDown) {
        this(label, null, enableUpDown, false);
    }

    /**
     * Create a new ArgumentsPanel with a border and color background
     * @param label text for label
     * @param bkg background colour
     */
    public MyPanelll(String label, Color bkg) {
        this(label, bkg, true, false);
    }
    
    /**
     * Create a new ArgumentsPanel with a border and color background
     * @param label text for label
     * @param bkg background colour
     * @param enableUpDown Add up/down buttons
     * @param standalone is standalone
     */
    public MyPanelll(String label, Color bkg, boolean enableUpDown, boolean standalone) {
        this(label, bkg, enableUpDown, standalone, null);
    }
       
    /**
     * Create a new ArgumentsPanel with a border and color background
     * @param label text for label
     * @param bkg background colour
     * @param enableUpDown Add up/down buttons
     * @param standalone is standalone
     * @param model the table model to use
     */
    public MyPanelll(String label, Color bkg, boolean enableUpDown, boolean standalone, ObjectTableModel model) {
        tableLabel = new JLabel(label);
        this.enableUpDown = enableUpDown;
        this.background = bkg;
        this.standalone = standalone;
        this.tableModel = model;
        init();
    }

    /**
     * This is the list of menu categories this gui component will be available
     * under.
     *
     * @return a Collection of Strings, where each element is one of the
     *         constants defined in MenuFactory
     */
    @Override
    public Collection<String> getMenuCategories() {
        if (standalone) {
            return super.getMenuCategories();
        }
        return null;
    }

    @Override
    public String getLabelResource() {
        return "test_link_defined_variables"; // $NON-NLS-1$
    }

    /* Implements JMeterGUIComponent.createTestElement() */
    @Override
    public TestElement createTestElement() {
    	log.info("createTestElement");
        Arguments args = new Arguments();
        modifyTestElement(args);
        return args;
    }

    /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
    @Override
    public void modifyTestElement(TestElement args) {
    	log.info("modifyTestElement");
        GuiUtils.stopTableEditing(table);
        Arguments arguments = null;
        if (args instanceof Arguments) {
            arguments = (Arguments) args;
            arguments.clear();
            @SuppressWarnings("unchecked") // only contains Argument (or HTTPArgument)
            Iterator<Argument> modelData = (Iterator<Argument>) tableModel.iterator();
            while (modelData.hasNext()) {
                Argument arg = modelData.next();
                if(StringUtils.isEmpty(arg.getName()) && StringUtils.isEmpty(arg.getValue())) {
                    continue;
                }
                arg.setMetaData("="); // $NON-NLS-1$
                arguments.addArgument(arg);
            }
        }
        this.configureTestElement(args);
    }

    /**
     * A newly created component can be initialized with the contents of a Test
     * Element object by calling this method. The component is responsible for
     * querying the Test Element object for the relevant information to display
     * in its GUI.
     *
     * @param el
     *            the TestElement to configure
     */
    @Override
    public void configure(TestElement el) {
    	log.info("configure");
        super.configure(el);
        if (el instanceof Arguments) {
            tableModel.clearData();
            PropertyIterator iter = ((Arguments) el).iterator();
            while (iter.hasNext()) {
                Argument arg = (Argument) iter.next().getObjectValue();
                tableModel.addRow(arg);
            }
        }
        checkDeleteStatus();
    }

    /**
     * Get the table used to enter arguments.
     *
     * @return the table used to enter arguments
     */
    protected JTable getTable() {
        return table;
    }

    /**
     * Get the title label for this component.
     *
     * @return the title label displayed with the table
     */
    protected JLabel getTableLabel() {
        return tableLabel;
    }

    /**
     * Get the button used to delete rows from the table.
     *
     * @return the button used to delete rows from the table
     */
    protected JButton getDeleteButton() {
        return delete;
    }

    /**
     * Get the button used to add rows to the table.
     *
     * @return the button used to add rows to the table
     */
    protected JButton getAddButton() {
        return add;
    }

    /**
     * Enable or disable the delete button depending on whether or not there is
     * a row to be deleted.
     */
    protected void checkDeleteStatus() {
        // Disable DELETE if there are no rows in the table to delete.
        if (tableModel.getRowCount() == 0) {
            delete.setEnabled(false);
        } else {
            delete.setEnabled(true);
        }
        
        if(enableUpDown && tableModel.getRowCount()>1) {
            up.setEnabled(true);
            down.setEnabled(true);
        }
    }

    @Override
    public void clearGui(){
        super.clearGui();
        clear();
    }

    /**
     * Clear all rows from the table. T.Elanjchezhiyan(chezhiyan@siptech.co.in)
     */
    public void clear() {
        GuiUtils.stopTableEditing(table);
        tableModel.clearData();
    }

    /**
     * Invoked when an action occurs. This implementation supports the add and
     * delete buttons.
     *
     * @param e
     *            the event that has occurred
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    	log.info("actionPerformed");
        String action = e.getActionCommand();
        if (action.equals(DELETE)) {
            deleteArgument();
        } else if (action.equals(ADD)) {
            addArgument();
        } else if (action.equals(ADD_FROM_CLIPBOARD)) {
            addFromClipboard();
        } else if (action.equals(UP)) {
            moveUp();
        } else if (action.equals(DOWN)) {
            moveDown();
        } else if (action.equals(DETAIL)) {
            showDetail();
        }
    }

    /**
     * Cancel cell editing if it is being edited
     */
    private void cancelEditing() {
        // If a table cell is being edited, we must cancel the editing before
        // deleting the row
        if (table.isEditing()) {
            TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
            cellEditor.cancelCellEditing();
        }
    }
    
    /**
     * Move a row down
     */
    private void moveDown() {
        cancelEditing();

        int[] rowsSelected = table.getSelectedRows();
        if (rowsSelected.length > 0 && rowsSelected[rowsSelected.length - 1] < table.getRowCount() - 1) {
            table.clearSelection();
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                int rowSelected = rowsSelected[i];
                tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected + 1);
            }
            for (int rowSelected : rowsSelected) {
                table.addRowSelectionInterval(rowSelected + 1, rowSelected + 1);
            }
        }
    }

    /**
     *  Move a row down
     */
    private void moveUp() {
        cancelEditing();

        int[] rowsSelected = table.getSelectedRows();
        if (rowsSelected.length > 0 && rowsSelected[0] > 0) {
            table.clearSelection();
            for (int rowSelected : rowsSelected) {
                tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected - 1);
            }
            for (int rowSelected : rowsSelected) {
                table.addRowSelectionInterval(rowSelected - 1, rowSelected - 1);
            }
        }
    }

    /**
     * Show Row Detail
     */
    private void showDetail() {
        cancelEditing();

        int[] rowsSelected = table.getSelectedRows();
        if (rowsSelected.length == 1) {
            table.clearSelection();
            RowDetailDialog detailDialog = new RowDetailDialog(tableModel, rowsSelected[0]);
            detailDialog.setVisible(true);
        } 
    }
    
    /**
     * Remove the currently selected argument from the table.
     */
    protected void deleteArgument() {
        cancelEditing();

        int[] rowsSelected = table.getSelectedRows();
        int anchorSelection = table.getSelectionModel().getAnchorSelectionIndex();
        table.clearSelection();
        if (rowsSelected.length > 0) {
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                tableModel.removeRow(rowsSelected[i]);
            }

            // Disable DELETE if there are no rows in the table to delete.
            if (tableModel.getRowCount() == 0) {
                delete.setEnabled(false);
            }
            // Table still contains one or more rows, so highlight (select)
            // the appropriate one.
            else if (tableModel.getRowCount() > 0) {
                if (anchorSelection >= tableModel.getRowCount()) {
                    anchorSelection = tableModel.getRowCount() - 1;
                }
                table.setRowSelectionInterval(anchorSelection, anchorSelection);
            }
            
            if(enableUpDown && tableModel.getRowCount()>1) {
                up.setEnabled(true);
                down.setEnabled(true);
            }
        }
    }

    /**
     * Add a new argument row to the table.
     */
    protected void addArgument() {
    	log.info("addArgument");
        // If a table cell is being edited, we should accept the current value
        // and stop the editing before adding a new row.
        GuiUtils.stopTableEditing(table);

        tableModel.addRow(makeNewArgument());

        // Enable DELETE (which may already be enabled, but it won't hurt)
        delete.setEnabled(true);
        if(enableUpDown && tableModel.getRowCount()>1) {
            up.setEnabled(true);
            down.setEnabled(true);
        }
        // Highlight (select) the appropriate row.
        int rowToSelect = tableModel.getRowCount() - 1;
        table.setRowSelectionInterval(rowToSelect, rowToSelect);
    }

    /**
     * Add values from the clipboard
     */
    protected void addFromClipboard() {
        GuiUtils.stopTableEditing(table);
        int rowCount = table.getRowCount();
        try {
            String clipboardContent = GuiUtils.getPastedText();
            String[] clipboardLines = clipboardContent.split("\n");
            for (String clipboardLine : clipboardLines) {
                String[] clipboardCols = clipboardLine.split("\t");
                if (clipboardCols.length > 0) {
                    Argument argument = makeNewArgument();
                    argument.setName(clipboardCols[0]);
                    if (clipboardCols.length > 1) {
                        argument.setValue(clipboardCols[1]);
                        if (clipboardCols.length > 2) {
                            argument.setDescription(clipboardCols[2]);
                        }
                    }
                    tableModel.addRow(argument);
                }
            }
            if (table.getRowCount() > rowCount) {
                // Enable DELETE (which may already be enabled, but it won't hurt)
                delete.setEnabled(true);

                // Highlight (select) the appropriate rows.
                int rowToSelect = tableModel.getRowCount() - 1;
                table.setRowSelectionInterval(rowCount, rowToSelect);
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add read arguments from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedFlavorException ufe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add retrieve " + DataFlavor.stringFlavor.getHumanPresentableName()
                            + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create a new Argument object.
     *
     * @return a new Argument object
     */
    protected Argument makeNewArgument() {
        return new Argument("", ""); // $NON-NLS-1$ // $NON-NLS-2$
    }

    /**
     * Stop any editing that is currently being done on the table. This will
     * save any changes that have already been made.
     * Needed for subclasses
     */
    protected void stopTableEditing() {
        GuiUtils.stopTableEditing(table);
    }
    
    /**
     * Initialize the table model used for the arguments table.
     */
    protected void initializeTableModel() {
    	log.info("initializeTableModel");
    if (tableModel == null) {
        if(standalone) {
            tableModel = new ObjectTableModel(new String[] { COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1, COLUMN_RESOURCE_NAMES_2 },
                    Argument.class,
                    new Functor[] {
                    new Functor("getName"), // $NON-NLS-1$
                    new Functor("getValue"),  // $NON-NLS-1$
                    new Functor("getDescription") },  // $NON-NLS-1$
                    new Functor[] {
                    new Functor("setName"), // $NON-NLS-1$
                    new Functor("setValue"), // $NON-NLS-1$
                    new Functor("setDescription") },  // $NON-NLS-1$
                    new Class[] { String.class, String.class, String.class });
        } else {
            tableModel = new ObjectTableModel(new String[] { COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1 },
                    Argument.class,
                    new Functor[] {
                    new Functor("getName"), // $NON-NLS-1$
                    new Functor("getValue") },  // $NON-NLS-1$
                    new Functor[] {
                    new Functor("setName"), // $NON-NLS-1$
                    new Functor("setValue") }, // $NON-NLS-1$
                    new Class[] { String.class, String.class });
            }
        }
    }

    public static boolean testFunctors(){
    	log.info("testFunctors");
        ArgumentsPanel instance = new ArgumentsPanel();
        instance.initializeTableModel();
        return instance.tableModel.checkFunctors(null,instance.getClass());
    }

    /**
     * Resize the table columns to appropriate widths.
     *
     * @param _table
     *            the table to resize columns for
     */
    protected void sizeColumns(JTable _table) {
    }

    /**
     * Create the main GUI panel which contains the argument table.
     *
     * @return the main GUI panel
     */
    private Component makeMainPanel() {
        initializeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (this.background != null) {
            table.setBackground(this.background);
        }
        return makeScrollPane(table);
    }

    /**
     * Create a panel containing the title label for the table.
     *
     * @return a panel containing the title label
     */
    protected Component makeLabelPanel() {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(tableLabel);
        if (this.background != null) {
            labelPanel.setBackground(this.background);
        }
        return labelPanel;
    }

    /**
     * Create a panel containing the add and delete buttons.
     *
     * @return a GUI panel containing the buttons
     */
    private JPanel makeButtonPanel() {
        JButton showDetail = new JButton(JMeterUtils.getResString("detail")); // $NON-NLS-1$
        showDetail.setActionCommand(DETAIL);
        showDetail.setEnabled(true);
        
        add = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
        add.setActionCommand(ADD);
        add.setEnabled(true);
        /** A button for adding new arguments to the table from the clipboard. */
        JButton addFromClipboard = new JButton(JMeterUtils.getResString("add_from_clipboard")); // $NON-NLS-1$
        addFromClipboard.setActionCommand(ADD_FROM_CLIPBOARD);
        addFromClipboard.setEnabled(true);

        delete = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
        delete.setActionCommand(DELETE);

        if(enableUpDown) {
            up = new JButton(JMeterUtils.getResString("up")); // $NON-NLS-1$
            up.setActionCommand(UP);
    
            down = new JButton(JMeterUtils.getResString("down")); // $NON-NLS-1$
            down.setActionCommand(DOWN);
        }
        checkDeleteStatus();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        if (this.background != null) {
            buttonPanel.setBackground(this.background);
        }
        showDetail.addActionListener(this);
        add.addActionListener(this);
        addFromClipboard.addActionListener(this);
        delete.addActionListener(this);
        buttonPanel.add(showDetail);
        buttonPanel.add(add);
        buttonPanel.add(addFromClipboard);
        buttonPanel.add(delete);
        if(enableUpDown) {
            up.addActionListener(this);
            down.addActionListener(this);
            buttonPanel.add(up);
            buttonPanel.add(down);
        }
        return buttonPanel;
    }

    /**
     * Initialize the components and layout of this component.
     */
    private void init() {
        JPanel p = this;

        if (standalone) {
            setLayout(new BorderLayout(0, 5));
            setBorder(makeBorder());
            add(makeTitlePanel(), BorderLayout.NORTH);
            p = new JPanel();
        }

        p.setLayout(new BorderLayout());

        p.add(makeLabelPanel(), BorderLayout.NORTH);
        p.add(makeMainPanel(), BorderLayout.CENTER);
        // Force a minimum table height of 70 pixels
        p.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        p.add(makeButtonPanel(), BorderLayout.SOUTH);

        if (standalone) {
            add(p, BorderLayout.CENTER);
        }

        table.revalidate();
        sizeColumns(table);
    }
}

