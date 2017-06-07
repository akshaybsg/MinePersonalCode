package com.tcs.EformsTesting;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File; 
import javax.imageio.ImageIO;
import javax.swing.*;


public class HelloWorldFrame extends JFrame implements ActionListener {

JButton b;
public HelloWorldFrame() {
    this.setVisible(true);
    this.setLayout(null);
    b = new JButton("Click Here");
    b.setBounds(380, 290, 120, 60);
    b.setBackground(Color.red);
    b.setVisible(true);
    b.addActionListener(this);
    add(b);
    setSize(1000, 700);
}
public void actionPerformed(ActionEvent e)
{
    if (e.getSource() == b) 
    {
        this.dispose();
        try {
            Thread.sleep(1000);
            Toolkit tk = Toolkit.getDefaultToolkit(); 
            Dimension d = tk.getScreenSize();
            Rectangle rec = new Rectangle(0, 0, d.width, d.height);  
            Robot ro = new Robot();
            BufferedImage img = ro.createScreenCapture(rec);
            File f = new File("C:\\Users\\559544\\Desktop\\scr.png");//set appropriate path
            
            ImageIO.write(img, "jpg", f);
    //      captureFrame(this, "C:\\Users\\559544\\Desktop\\scr.png");
            
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}

public static void main(String[] args) {
    HelloWorldFrame obj = new HelloWorldFrame();
}
}