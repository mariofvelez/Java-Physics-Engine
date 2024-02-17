package test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author Mario Velez
 * 
 *
 */
public class Window extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494605708565768482L;
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	
	public static Window window;
	
	public Window(String name)
	{
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		this.setSize(windowSize);
		this.setLayout(new BorderLayout());
	}
	public static void main(String[] args) throws Exception
	{
		window = new Window("Physics Engine");
		
		Container contentPane = window.getContentPane();
		
		Field field = new Field(window.getSize());
		contentPane.add(field, BorderLayout.CENTER);
		
		JPanel debug_panel = new JPanel();
		debug_panel.setLayout(new GridBagLayout());
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(5, 5, 5, 5);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 0;
		
		JComboBox<String> tests_combo = new JComboBox<String>();
		
		DefaultComboBoxModel<String> tests_model = new DefaultComboBoxModel<String>();
		tests_model.addElement("Circle Stack Test");
		tests_model.addElement("Friction Test");
		tests_model.addElement("Polygon Test");
		tests_model.addElement("Spring Test");
		tests_model.addElement("Voronoi Test");
		
		tests_combo.setModel(tests_model);
		tests_combo.setSelectedIndex(1);
		tests_combo.setEditable(false);
		
		tests_combo.addActionListener(e -> {
			String selected = (String) tests_combo.getSelectedItem();
			selected = selected.replaceAll("\\s", "");
			field.setTest(selected);
		});
		
		debug_panel.add(tests_combo, gc);
		
		JCheckBox show_centroid_check = new JCheckBox("Show Centroid");
		show_centroid_check.addActionListener(e -> {
			field.getDebuginfo().show_centroid = show_centroid_check.isSelected();
		});
		gc.gridy = 1;
		debug_panel.add(show_centroid_check, gc);
		
		JCheckBox show_edge_normals_check = new JCheckBox("Show Edge Normals");
		show_edge_normals_check.addActionListener(e -> {
			field.getDebuginfo().show_edge_normals = show_edge_normals_check.isSelected();
		});
		gc.gridy = 2;
		debug_panel.add(show_edge_normals_check, gc);
		
		JCheckBox show_poc_check = new JCheckBox("Show Points of Collision");
		show_poc_check.addActionListener(e -> {
			field.getDebuginfo().show_poc = show_poc_check.isSelected();
		});
		gc.gridy = 3;
		debug_panel.add(show_poc_check, gc);
		
		JCheckBox show_vertex_vel_check = new JCheckBox("Show Vertex Velocities");
		show_vertex_vel_check.addActionListener(e -> {
			field.getDebuginfo().show_vertex_velocities = show_vertex_vel_check.isSelected();
		});
		gc.gridy = 4;
		gc.weighty = 1;
		debug_panel.add(show_vertex_vel_check, gc);
		
		window.add(debug_panel, BorderLayout.EAST);
		
		window.setVisible(true);
		window.setLocationRelativeTo(null);
	}
	public static void close()
	{
		window.dispose();
	}
}
