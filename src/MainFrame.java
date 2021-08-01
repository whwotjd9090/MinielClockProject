/*
 * Error return Value list
 * 7 : Image Loading Error.
 * 8 : There is no Image File, if just one.
 * 9 : There is no Font File.
 */

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

class SecondLabel extends JLabel implements Runnable{
	private static final long serialVersionUID = 1L;

	LocalTime now_time;
	int second;
	BufferedImage now_img;
	ZoneId timez;
	
	public SecondLabel(BufferedImage _img, ZoneId tz) {
		now_img = _img;
		
		timez = tz;
		second = getTime();
	}
	
	public void setTime(ZoneId tz) {
		timez = tz;
	}
	
	private int getTime() {
		now_time = LocalTime.now(timez);
		return now_time.getSecond();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(imageRotate(6 * second).filter(now_img, null), 139, 101, null);
	}
	
	private AffineTransformOp imageRotate(int my_ang) {
		double rotationRequired = Math.toRadians(my_ang);
	 	double locationX = now_img.getWidth() / 2;
	 	double locationY = now_img.getHeight() / 2;
	 	
	 	AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
	 	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		
	 	return op;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			
			int now_sec = getTime();
			if(second != now_sec) {
				second = now_sec;
				repaint();
			}
		}
	}
}

class MinuteLabel extends JLabel implements Runnable {
	private static final long serialVersionUID = -7791135677710369437L;

	LocalTime now_time;
	int minute;
	BufferedImage now_img;
	BufferedImage[] minute_array = new BufferedImage[60];
	ZoneId timez;
	
	public MinuteLabel(BufferedImage[] _img, ZoneId tz) {
		minute_array = _img;
		timez = tz;
		minute = getTime();
		
		now_img = minute_array[minute];
	}
	
	public void setTime(ZoneId tz) {
		timez = tz;
	}
	
	private int getTime() {
		now_time = LocalTime.now(timez);
		return now_time.getMinute();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(now_img, 109, 54, null);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			
			int temp = getTime();
			if(temp != minute) {
				minute = temp;
				
				now_img = minute_array[minute];
				
				repaint();
			}
		}
	}
}

class HourLabel extends JLabel implements Runnable {
	private static final long serialVersionUID = 1L;
	
	LocalTime now_time;
	int hour_of_day;
	BufferedImage am_img, pm_img;
	BufferedImage[] now_img = new BufferedImage[12];
	ZoneId timez;
	
	final int[][] img_loc = {
			{157, 6}, {204, 17}, {240, 50}, {253, 98},
			{240, 146}, {205, 180}, {158, 190}, {112, 177},
			{80, 142}, {69, 98}, {80, 53}, {112, 21}
	};
	
	public HourLabel(BufferedImage a_img, BufferedImage p_img, ZoneId tz) {
		am_img = a_img;
		pm_img = p_img;
		
		hour_of_day = -1;
		
		timez = tz;
		
		getTime();
	}
	
	public void setTime(ZoneId tz) {
		timez = tz;
	}
	
	private boolean getTime() {
		now_time = LocalTime.now(timez);
		int temp_hour_of_day = now_time.getHour();
		
		if(hour_of_day != temp_hour_of_day) {
			hour_of_day = temp_hour_of_day;
			
			if(hour_of_day == 0) {
				for(int i = 0; i < 12; i++) {
					now_img[i] = pm_img;
				}
			}
			else if(1 <= hour_of_day && hour_of_day <= 12) {
				for(int i = 1; i <= hour_of_day; i++) {
					if(i == 12) {
						now_img[0] = am_img;
					} 
					else {
						now_img[i] = am_img;
					}
				}
				for(int i = hour_of_day + 1; i <= 12; i++) {
					if(i == 12){
						now_img[0] = pm_img;
					}
					else {
						now_img[i] = pm_img;
					}
				}
			}
			else {
				for(int i = 13; i<= hour_of_day; i++) {
					now_img[i - 12] = pm_img;
				}
				for(int i = hour_of_day + 1; i <= 24; i++) {
					if(i == 24) {
						now_img[0] = am_img;
					}
					else {
						now_img[i - 12] = am_img;
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for(int i = 0; i < 12; i++) {
			g.drawImage(imageRotate(now_img[i], 30 * i).filter(now_img[i], null), img_loc[i][0], img_loc[i][1], null);
		}
	}
	
	private AffineTransformOp imageRotate(BufferedImage temp_img, int my_ang) {
		double rotationRequired = Math.toRadians(my_ang);
	 	double locationX = temp_img.getWidth() / 2;
	 	double locationY = temp_img.getHeight() / 2;
	 	
	 	AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
	 	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		
	 	return op;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			
			if(getTime()) {
				repaint();
			}
		}
	}
}

class TimeLabel extends JLabel implements Runnable {
	private static final long serialVersionUID = 1L;

	LocalTime now_time;
	String hour, minute, second;
	String time_str;
	Font TL_ttf;
	ZoneId timez;
	
	public TimeLabel(ZoneId tz) {
		timez = tz;
		
		try {
			TL_ttf = new Font("Benegraphic", Font.BOLD, 27);
			
			if(TL_ttf.getName() != "Benegraphic") {
				throw new Exception();
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "There is no 'Wet Dream' Font file.\nPlease download 'Wet Dream.ttf'", "Font Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		getTime();
	}
	
	public void setTime(ZoneId tz) {
		timez = tz;
	}
	
	private void getTime() {
		now_time = LocalTime.now(timez);
		
		hour = String.format("%02d", now_time.getHour());
		minute = String.format("%02d", now_time.getMinute());
		second = String.format("%02d", now_time.getSecond());
		
		time_str = hour + ":" + minute + ":" + second;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(new Color(43, 43, 43));
		g.setFont(TL_ttf);
		g.drawString(time_str, 143, 290);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			
			getTime();
			
			repaint();
		}
	}
}

public class MainFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 3049716722945313634L;

	Thread second_th;
	Thread minute_th;
	Thread hour_th;
	Thread time_th;
	
	JPanel my_panel;
	SecondLabel label_second;
	MinuteLabel label_minute;
	HourLabel label_hour;
	TimeLabel label_time;
	
	MediaTracker MDT;
	ZoneId tz;
	String sel_time;
	
	BufferedImage second_buff;
	BufferedImage[] minute_buff;
	BufferedImage am_meter_buff;
	BufferedImage pm_meter_buff;
	ImageIcon bg_imgic;
	Image workIcon;
	
	RightClickMenuPopUp rcpop;
	
	boolean lt_view_check, AOT_check;
	
	public void createAndShow() {
		ImageLoading();
		
		setMainFrame();
		
		this.setLayout(null);
		this.setBackground(new Color(0, 255, 0, 0));
		
		bg_imgic = new ImageIcon("ClockImage/TestBase.png");
		my_panel = new JPanel() {
			private static final long serialVersionUID = -7165716294510137562L;
			
			@Override
			public void paintComponent(Graphics g) {
				this.setOpaque(false);
				super.paintComponent(g);
				
				g.drawImage(bg_imgic.getImage(), 0, 0, null);

				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
				g2d.setColor(getBackground());
				g2d.fill(getBounds());
				g2d.dispose();
			}
		};
		this.setContentPane(my_panel);
		this.getContentPane().setBackground(Color.BLACK);
		my_panel.setLayout(new BorderLayout());
		
		tz = ZoneId.of("Asia/Seoul");

		label_second = new SecondLabel(second_buff, tz);
		second_th = new Thread(label_second);
		label_second.setLayout(new BorderLayout());
		
		label_minute = new MinuteLabel(minute_buff, tz);
		minute_th = new Thread(label_minute);
		label_minute.setLayout(new BorderLayout());
		
		label_hour = new HourLabel(am_meter_buff, pm_meter_buff, tz);
		hour_th = new Thread(label_hour);
		label_hour.setLayout(new BorderLayout());
		
		label_time = new TimeLabel(tz);
		time_th = new Thread(label_time);
		
		label_hour.add(label_time);
		label_minute.add(label_hour);
		label_second.add(label_minute);
		my_panel.add(label_second);
		
		second_th.start();
		minute_th.start();
		hour_th.start();
		time_th.start();
		
		HotkeySet();
		
		new FrameMouseEvent(this, rcpop);
		
		HelpDialog();
		this.setVisible(true);
	}
	
	private void HotkeySet() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fr_exit");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "fr_help");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "fr_WT");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('D', KeyEvent.CTRL_MASK), "fr_DView");
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('T', KeyEvent.CTRL_MASK), "fr_AOnTop");
		getRootPane().getActionMap().put("fr_exit", new AbstractAction() {
			private static final long serialVersionUID = -1208993189730222884L;

			@Override
			public void actionPerformed(ActionEvent e) {
				second_th.interrupt();
				minute_th.interrupt();
				hour_th.interrupt();
				time_th.interrupt();
				System.exit(0);
			}
		});
		getRootPane().getActionMap().put("fr_help", new AbstractAction() {
			private static final long serialVersionUID = -9058369853242848285L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDialog();
			}
		});
		getRootPane().getActionMap().put("fr_WT", new AbstractAction() {
			private static final long serialVersionUID = -6277963023013200891L;

			@Override
			public void actionPerformed(ActionEvent e) {
				WTDialog();
			}
		});
		
		lt_view_check = true;
		AOT_check = false;
		
		rcpop = new RightClickMenuPopUp();
		rcpop.help_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDialog();
			}
		});
		
		rcpop.dcview_check_item.setSelected(true);
		rcpop.dcview_check_item.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					label_time.setVisible(true);
				}
				else {
					label_time.setVisible(false);
				}
			}
		});
		getRootPane().getActionMap().put("fr_DView", new AbstractAction() {
			private static final long serialVersionUID = -6277963023013200891L;

			@Override
			public void actionPerformed(ActionEvent e) {
				lt_view_check = !lt_view_check;
				rcpop.dcview_check_item.setSelected(lt_view_check);
				label_time.setVisible(lt_view_check);
			}
		});
		
		rcpop.on_top_focus.setSelected(false);
		rcpop.on_top_focus.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					setAlwaysOnTop(true);
				}
				else {
					setAlwaysOnTop(false);
				}
			}
		});
		getRootPane().getActionMap().put("fr_AOnTop", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AOT_check = !AOT_check;
				rcpop.on_top_focus.setSelected(AOT_check);
				setAlwaysOnTop(AOT_check);
			}
		});
		
		sel_time = "Asia/Seoul";
		rcpop.world_time.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				WTDialog();
			}
		});
		rcpop.exit_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				second_th.interrupt();
				minute_th.interrupt();
				hour_th.interrupt();
				time_th.interrupt();
				System.exit(0);
			}
		});
	}
	
	private void WTDialog() {
		sel_time = (String)JOptionPane.showInputDialog(null, "지역", "세계 시간", JOptionPane.PLAIN_MESSAGE, null, rcpop.time_zone, sel_time);
		
		if(sel_time != null) {
			tz = ZoneId.of(sel_time);
			
			label_second.setTime(tz);
			label_minute.setTime(tz);
			label_hour.setTime(tz);
			label_time.setTime(tz);
		}
	}
	
	private static void HelpDialog() {	
		JOptionPane.showMessageDialog(null, "단축키 및 기능\n"
				+ "F1: 도움말 보기.\n"
				+ "F2: 세계 시간 설정\n"
				+ "Ctrl + D: 디지털 시계 표시.\n"
				+ "Ctrl + T: 항상 위에.\n"
				+ "ESC: 종료.\n\n"
				+ "또는 시계에 오른쪽 클릭해주세요.",
				"Help - Ver. 1.3.0.0", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void ImageLoading() {
		minute_buff = new BufferedImage[60];
		
		MDT = new MediaTracker(this);
		
		try { 
			second_buff = ImageIO.read(new File("ClockImage/TestSecond.png"));
			MDT.addImage(second_buff, 0);
			am_meter_buff = ImageIO.read(new File("ClockImage/TestAmMeter.png"));
			MDT.addImage(am_meter_buff, 1);
			pm_meter_buff = ImageIO.read(new File("ClockImage/TestPmMeter.png"));
			MDT.addImage(pm_meter_buff, 2);
			workIcon = Toolkit.getDefaultToolkit().getImage("ClockImage/TestIcon.png");
			MDT.addImage(workIcon, 3);
			for(int i = 1; i <= 60; i++) {
				minute_buff[i - 1] = ImageIO.read(new File("ClockImage/TestMinute/TestMinute" + i + ".png"));
				MDT.addImage(minute_buff[i - 1], 4 + i);
			}
			
			MDT.waitForAll();
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "이미지 파일을 찾을 수 없습니다.\n재설치를 부탁드립니다.", "Image File Error", JOptionPane.ERROR_MESSAGE);
			System.exit(8);
		} catch(InterruptedException e) {
			JOptionPane.showMessageDialog(null, "이미지를 로딩할 수 없습니다.\n프로그램을 다시 시작하거나 재설치를 해주시기 바랍니다.", "Image Loading Error", JOptionPane.ERROR_MESSAGE);
			System.exit(7);
		}
	}
	
	private void setMainFrame() {
		setIconImage(workIcon);
		this.setTitle("미니엘 시계");
		this.setSize(400, 541);
		this.setUndecorated(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				second_th.interrupt();
				minute_th.interrupt();
				hour_th.interrupt();
				time_th.interrupt();
				System.exit(0);
			}
		});
	}
	
	@Override
	public void run() {
		createAndShow();
	}
	
	public static void main(String[] argv) {
		SwingUtilities.invokeLater(new MainFrame());
	}
}

class RightClickMenuPopUp extends JPopupMenu {
	private static final long serialVersionUID = 1588266300554612753L;
	
	JCheckBoxMenuItem dcview_check_item;
	JCheckBoxMenuItem on_top_focus;
	JMenuItem help_item;
	JMenuItem world_time;
	JMenuItem exit_item;
	String[] time_zone;
	
    public RightClickMenuPopUp(){
    	time_zone = TimeZone.getAvailableIDs();
    	dcview_check_item = new JCheckBoxMenuItem("디지털 시계 표시 - Digital Clock View");
    	dcview_check_item.setAccelerator(KeyStroke.getKeyStroke('D', KeyEvent.CTRL_MASK));
    	on_top_focus = new JCheckBoxMenuItem("항상 위에 - Always on top");
    	on_top_focus.setAccelerator(KeyStroke.getKeyStroke('T', KeyEvent.CTRL_MASK));
    	help_item = new JMenuItem("도움말 - Help");
    	help_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    	world_time = new JMenuItem("세계 시간 - World Time");
    	world_time.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    	exit_item = new JMenuItem("종료 - Exit");
    	exit_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    	
    	this.add(help_item);
    	this.addSeparator();
   		this.add(dcview_check_item);
   		this.add(on_top_focus);
   		this.addSeparator();
   		this.add(world_time);
   		this.addSeparator();
   		this.add(exit_item);
    }
}

class FrameMouseEvent {
	private int pos_x = 0, pos_y = 0;
	
	public FrameMouseEvent(JFrame this_frame, RightClickMenuPopUp this_rcMenu) {
		this_frame.addMouseListener(new MouseAdapter() {
			private void doPop(MouseEvent e){
				this_rcMenu.show(e.getComponent(), e.getX(), e.getY());
		    }
			
			@Override
			public void mousePressed(MouseEvent e) {
				pos_x = e.getX();
				pos_y = e.getY();
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				if (e.isPopupTrigger()) {
		        	doPop(e);
		        }
			}
		});

		this_frame.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				this_frame.setLocation(evt.getXOnScreen() - pos_x, evt.getYOnScreen() - pos_y);
			}
		});
	}
}