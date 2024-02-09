package geometry;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
/**
 * 
 * @author Mario Velez
 * 
 *
 */
public class Window1 extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494605708565768482L;
	public static final int WIDTH = 1500;
	public static final int HEIGHT = 800;
	
	public static Window1 window;
	
	public Window1(String name)
	{
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		this.setSize(windowSize);
	}
	public static void main(String[] args) throws Exception
	{
//		Parabola x = new Parabola(0.0f, 0.0f, 5.0f, -10.0f);
//		Parabola y = new Parabola(0.0f, 8.0f, -8.5f, 2.0f);
//		System.out.println("(" + x.f(2.0f) + ", " + x.getSlope(2.0f) + ")");
//		System.out.println("mag: " + Math.hypot(x.f(6.5f), y.f(6.5f)));
		window = new Window1("Collision Testing");
		
		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
		UIManager.setLookAndFeel(looks[1].getClassName());
		
		Container contentPane = window.getContentPane();
		
		contentPane.setLayout(new BorderLayout());
		
		Field edit2 = new Field(window.getSize());
		contentPane.add(edit2, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = new Insets(5, 5, 5, 5);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 0.0;
		
		JButton render_ray_march = new JButton("Ray March");
		panel.add(render_ray_march, gc);
		render_ray_march.addActionListener(e -> edit2.computeRayMarching());
		
		gc.gridy++;
		
		JButton render_raycast = new JButton("Ray Cast");
		panel.add(render_raycast, gc);
		render_raycast.addActionListener(e -> edit2.computeRaycasting(1));
		
		gc.gridy++;
		
		JButton render_soft = new JButton("Soft Shadow");
		panel.add(render_soft, gc);
		render_soft.addActionListener(e -> edit2.computeSoftShadows());
		
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.gridy++;
		
		JCheckBox ray_march_check = new JCheckBox("Show Ray March");
		panel.add(ray_march_check, gc);
		ray_march_check.addActionListener(e -> edit2.show_ray_march = ray_march_check.isSelected());
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = new Insets(5, 5, 0, 5);
		gc.gridy++;
		
		JLabel rest_label = new JLabel("Restitution");
		panel.add(rest_label, gc);		
		
		gc.insets = new Insets(0, 5, 5, 5);
		gc.gridy++;
		
		JSlider rest_slider = new JSlider();
		rest_slider.setMinimum(0);
		rest_slider.setMaximum(100);
		rest_slider.setPreferredSize(new Dimension(100, rest_slider.getPreferredSize().height));
		rest_slider.addChangeListener(e -> edit2.restitution = (float) rest_slider.getValue() / 100f);
		panel.add(rest_slider, gc);
		
		gc.insets = new Insets(5, 5, 0, 5);
		gc.gridy++;
		
		JLabel fric_label = new JLabel("Friciton");
		panel.add(fric_label, gc);
		
		gc.insets = new Insets(0, 5, 5, 5);
		gc.gridy++;
		
		JSlider fric_slider = new JSlider();
		fric_slider.setMinimum(0);
		fric_slider.setMaximum(100);
		fric_slider.setPreferredSize(new Dimension(100, fric_slider.getPreferredSize().height));
		fric_slider.addChangeListener(e -> edit2.friction = (float) fric_slider.getValue() / 100f);
		panel.add(fric_slider, gc);
		
		gc.anchor = GridBagConstraints.NORTH;
		gc.insets = new Insets(0, 5, 5, 5);
		gc.gridy++;
		gc.weighty = 1;
		
		JButton gjk_button = new JButton("GJK step");
		panel.add(gjk_button, gc);
		gjk_button.addActionListener(e -> edit2.stepGJK());
		
		contentPane.add(panel, BorderLayout.EAST);
		
		window.setVisible(true);
		window.setLocation(200, 100);
		//window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//window.setAlwaysOnTop(true);
		//window.setResizable(false);
	}
	public static void close()
	{
		window.dispose();
	}
}
