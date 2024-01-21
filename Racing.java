import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.border.Border;

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


        // lap count
        // best time 2 dec
        // p1Speed 0 dec
        // p2Speed 0 dec


        // implement initial player locations

        try { // image processing
            background = ImageIO.read(new File ("/images/track.png"));
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

    // Method to set rounded border on JButton
    private static void setRoundedBorder(AbstractButton button, int radius) {
        Border border = BorderFactory.createEmptyBorder(5, 15, 5, 15); // Add padding if needed
        Border roundedBorder = new RoundBorder(radius);
        button.setBorder(BorderFactory.createCompoundBorder(border, roundedBorder));
    }

    // Custom Border class for rounded corners
    private static class RoundBorder implements Border {
        private int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+2, this.radius+2, this.radius+2, this.radius+2);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground());
            g.fillRoundRect(x, y, width, height, radius, radius);
        }
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

    private static class Animate implements Runnable {
        public void run() {
        }
    }

    private static class Music implements Runnable{
        public Music() {
        }

        @Override
        public void run() {
            playSound();
        }

        private void playSound(){
            File song = endgame ?
                    new File("audio/music/endBGM.wav") : new File("audio/music/bgm.wav");
            AudioInputStream inputStream = null;

            try {
                Clip clip = AudioSystem.getClip();
                inputStream = AudioSystem.getAudioInputStream(song);
                clip.open(inputStream);
                clip.start();
                System.out.println("Playing");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class StartGame implements ActionListener{
        public StartGame(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            endgame =  false;
            wPressed = false;
            aPressed = false;
            sPressed = false;
            dPressed = false;

            Thread t1 = new Thread(new PlayerMover());
        }
    }

    private static class PlayerMover implements Runnable {

        private double velocityStep;
        private double rotateStep;
        public PlayerMover() {
            velocityStep = 0.01;
            rotateStep = 0.01;
        }
        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class KeyPressed extends AbstractAction {
        private String action;
        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input){
            action = input;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(action.equals("W")){
                wPressed = true;
            }
            if(action.equals("A")){
                aPressed = true;
            }
            if(action.equals("S")){
                sPressed = true;
            }
            if(action.equals("D")){
                dPressed = true;
            }
            if(action.equals("UP")){
                upPressed = true;
            }
            if(action.equals("DOWN")){
                downPressed = true;
            }
            if(action.equals("LEFT")){
                leftPressed = true;
            }
            if(action.equals("RIGHT")){
                rightPressed = true;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;
        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input){
            action = input;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(action.equals("W")){
                wPressed = false;
            }
            if(action.equals("A")){
                aPressed = false;
            }
            if(action.equals("S")){
                sPressed = false;
            }
            if(action.equals("D")){
                dPressed = false;
            }
            if(action.equals("UP")){
                upPressed = false;
            }
            if(action.equals("DOWN")){
                downPressed = false;
            }
            if(action.equals("LEFT")){
                leftPressed = false;
            }
            if(action.equals("RIGHT")){
                rightPressed = false;
            }
        }
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
            System.out.println("Prompt Quit");
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


    private static void launch(){
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1280, 720);

        JPanel menuBar = new JPanel();
        menuBar.setBackground(Color.GREEN);
        menuBar.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton startGameButton = new JButton("Start Game");
        menuBar.add(startGameButton);
        startGameButton.addActionListener(new StartGame());

        JButton carSelectButton = new JButton("Select Car");
        menuBar.add(carSelectButton);

        JButton exitButton = new JButton("Quit Game");
        menuBar.add(exitButton);
        exitButton.addActionListener(new ConfirmQuit());

        appFrame.add(menuBar, BorderLayout.SOUTH);

        appFrame.setLocationRelativeTo(null);
        appFrame.setVisible(true);
    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    public static void main(String[] args) {
        setup();
        launch();
        Music test = new Music();
        test.playSound();

        JPanel myPanel = new JPanel();

        bindKey(myPanel, "W");
        bindKey(myPanel, "A");
        bindKey(myPanel, "S");
        bindKey(myPanel, "D");

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
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
