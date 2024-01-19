package PACKAGE_NAME;
import java.awt.*;

import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Vector;

public class Racing { // incorporating audio, starting traffic light, start menu with car selection
    public Racing() {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("Racing");
        XOFFSET = 0;
        YOFFSET = 40;
        WINWIDTH = 500;
        WINHEIGHT = 500;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endgame = false;


        //lap count
        //best time 2 dec
        // p1Speed 0 dec
        // p2Speed 0 dec


        // implement initial player locations

        try {
            background = ImageIO.read(new File ("track.png"));
            supra = ImageIO.read(new File("supra.png"));
            porche = ImageIO.read(new File("porche.png"));
            brz = ImageIO.read(new File("brz.png"));
            avSupra = ImageIO.read(new File("avSupra.png"));
            avPorche = ImageIO.read(new File("avPorche.png"));
            avBrz = ImageIO.read(new File("avBrz.png"));
        }
        catch (IOException e) {

        }
    }

    private static void backgroundDraw(){

    }

    private static class ImageObject{
        private double x;
        private double y;
        private double xwidth;
        private double yheight;
        private double angle; // radians
        private double internalangle; // radians
        private Vector<Double> coords;

        public ImageObject(){

        }

        public ImageObject(double xinput, double yinput, double xwidthinput,
                           double yheightinput, double angleinput){
            x = xinput;
            y = yinput;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getXwidth() {
            return xwidth;
        }

        public double getHeight() {
            return yheight;
        }

        public double getAngle() {
            return angle;
        }

        public double getInternalAngle() {
            return internalangle;
        }

        public Vector<Double> getCoords() {
            return coords;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public void setInternalAngle(double internalangle) {
            this.internalangle = internalangle;
        }

        public void setCoords(Vector<Double> coords) {
            this.coords = coords;
        }
    }

    private static class StartGame{
    }

    private static class PlayerMove{

    }

    private static class ConfirmQuit implements ActionListener {
        private ConfirmQuit(){

        }

        public void popup(){
            int quit = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?",
                    "Confirm Quit",
                    JOptionPane.YES_NO_OPTION);

            if (quit == 0) System.exit(0);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            popup();
        }
    }

    private static class SelectCar implements ActionListener{
        private SelectCar(){

        }

        public void openMenu(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }


    public static void main(String[] args) {
        setup();
        launch();
    }

    private static void launch(){
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1280, 720);

        JPanel menuBar = new JPanel();
        menuBar.setBackground(Color.GREEN);
        menuBar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton startGameButton = new JButton("Start Game");
        menuBar.add(startGameButton);

        JButton carSelectButton = new JButton("Select Car");
        menuBar.add(carSelectButton);

        JButton exitButton = new JButton("Quit Game");
        menuBar.add(exitButton);
        exitButton.addActionListener(new ConfirmQuit());

        appFrame.add(menuBar, BorderLayout.SOUTH);

        appFrame.setLocationRelativeTo(null);
        appFrame.setVisible(true);
    }

    private static boolean endgame;
    private static BufferedImage background;
    private static BufferedImage supra;
    private static BufferedImage porche;
    private static BufferedImage brz;
    private static BufferedImage avSupra;
    private static BufferedImage avPorche;
    private static BufferedImage avBrz;
    private static BufferedImage player1;
    private static BufferedImage player2;

    private static boolean upPressed;
    private static boolean downPressed;
    private static boolean leftPressed;
    private static boolean rightPressed;
    private static boolean wPressed;
    private static boolean aPressed;
    private static boolean sPressed;
    private static boolean dPressed;

    private static ImageObject p1;
    private static ImageObject p2;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;
    private static double p2width;
    private static double p2height;
    private static double p2originalX;
    private static double p2originalY;
    private static double p2velocity;

    private static int level;

    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;

    private static double pi;
    private static double twoPi;

    private static JFrame appFrame;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}
