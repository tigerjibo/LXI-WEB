import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

//画布类
class Mycanvas extends Canvas
{
	int x,y,r;
	int red,green,blue;
	static short[] vshPixels=new short[128];

	private int iPixelsCount = vshPixels.length;

	private short sMaxPixelsValue=255;
	private int iAppletWidth=800;
	private int iAppletHeight=256;
	private Image offImg=null;
	Graphics offG=null;

	Mycanvas()
	{
		setSize(500,256);
		setBackground(Color.black);
	}

	public void setX(int x1)
	{
		this.x=x1;System.out.println(x);
	}

	public void setY(int y1)
	{
		this.y=y1;System.out.println(y);
	}

	public void setPixel(short vsh)
	{
			for(int i=0;i<127;i++)
			{
				vshPixels[i]=vshPixels[i+1];
			}
			this.vshPixels[127]=vsh;
			System.out.println("vsh="+vsh);
	}

	public void paint(Graphics g)
	{
		offImg =createImage(getSize().width ,getSize().height);
		offG = offImg.getGraphics();
		Color oldColor=offG.getColor();
		/***打表格****/
		offG.setColor(Color.white);
		//画出横线
		for(int iLineY=1;iLineY <20;iLineY++)
		{
			offG.drawLine(0,iLineY*iAppletHeight/20,iAppletWidth-1,iLineY*iAppletHeight/20);	
		}
		//画出纵线
		for(int iLineX=1;iLineX<80;iLineX++)
		{
			offG.drawLine(iLineX*iAppletWidth/80,0,iLineX*iAppletWidth/80,iAppletHeight);
		}
		//画出边框
		offG.setColor(Color.white);
		offG.drawRect(0,0,iAppletHeight-1,iAppletWidth-1);
		//绘制曲线
		offG.setColor(Color.yellow);
		for(int iPixel=0;iPixel<iPixelsCount-1;iPixel++)
		{
			offG.drawLine(2*iPixel,sMaxPixelsValue-vshPixels[iPixel],2*(iPixel+1),sMaxPixelsValue-vshPixels[iPixel+1]);
		}
		offG.setColor(oldColor);
		g.drawImage(offImg,0,0,null);
	}

	public void update(Graphics g)
	{
		paint(g);
	}
}
public class TV_see extends Applet implements Runnable,ActionListener
{
	//定义 布局，面板，按钮，标签 
	BorderLayout Border = new BorderLayout();
	GridLayout Grid = new GridLayout(6,1);
	FlowLayout Flow = new FlowLayout();
	Mycanvas can;
	Panel P_left,P_right;
	Button B_start,B_stop;
	Label L_maxV;
	Label L_minV;
	TextField T_maxV;
	TextField T_minV;
	Socket sock =null;
	BufferedReader in = null;
	PrintWriter out =null;
	Thread thr1;
	Thread thr2;

	public void init()
	{
		this.setLayout(Border);
		P_left=new Panel(Flow);
		P_left.setBackground(Color.blue);
        can = new Mycanvas();

		P_right=new Panel(Grid);
	    P_right.setBackground(Color.gray);
		L_maxV =new Label("Max Value:",Label.CENTER);
		T_maxV =new TextField(6);
		L_minV =new Label("Min Value:",Label.CENTER);
		T_minV =new TextField(6);

		B_start =new Button("Start");
		B_start.addActionListener(this);
		B_stop = new Button("Stop");
		//第一次 分左右两块布局
		P_left.add(can);
		P_right.add(L_maxV);
		P_right.add(T_maxV);
		P_right.add(L_minV);
		P_right.add(T_minV);
		P_right.add(B_start);
		P_right.add(B_stop);
		//整体布局 第二次布局
		this.add(P_left,Border.WEST);
		this.add(P_right,Border.EAST);
		
		thr1 = new Thread(this);
		thr2 = new Thread(this);
	}
	public void start()
	{
		//建立TCP连接端口号1234
		try
		{
			sock = new Socket(this.getCodeBase().getHost(),1235);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(),true);
		}

	catch(IOException e){}
	thr1.start();
	thr2.start();
}	
public void run()
{
	String s=null;
	short NUM;
	while(true)
	{
		try
		{
			if(Thread.currentThread()==thr2)
			{
				thr2.sleep(10);//延迟10ms 
				out.println("b");
				System.out.println("T1");
				s=in.readLine();
				System.out.println("R1");
				System.out.println(s);
				int count=s.length();
				if(count>1)
				{
					NUM =Short.parseShort(s.substring(0,count-1));
					System.out.println(s.substring(0,count-1));
					System.out.println("NUM="+NUM);
					can.setPixel(NUM);
				}
			}
			else if(Thread.currentThread()==thr1)
			{
				thr1.sleep(1000);
				can.repaint();
			}
		}
		catch(IOException e)
		{
			break;
		}
		catch(InterruptedException e){}
	}
}
public void actionPerformed(ActionEvent e)
{
	short x;
	int y;
	try
	{
		x=Short.parseShort(T_maxV.getText());
		y=Integer.parseInt(T_minV.getText());
		System.out.println(x);
		System.out.println(y);
		can.setPixel(x);
		can.repaint();
	}

	catch (NumberFormatException ee)
	{
		x=0;
		y=0;
	}
}
}