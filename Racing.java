import java.awt.*;
import javax.sound.sampled.FloatControl;
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
        WINWIDTH = 1280;
        WINHEIGHT = 720;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endgame = false;
        counted = false;

        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH, WINHEIGHT);

        JPanel keyPanel = new JPanel();

        bindKey(keyPanel, "W");
        bindKey(keyPanel, "A");
        bindKey(keyPanel, "S");
        bindKey(keyPanel, "D");
        bindKey(keyPanel, "UP");
        bindKey(keyPanel, "DOWN");
        bindKey(keyPanel, "LEFT");
        bindKey(keyPanel, "RIGHT");

        appFrame.add(keyPanel);
        // lap count
        // best time 2 dec
        // p1Speed 0 dec
        // p2Speed 0 dec
        // implement initial player locations

        // image processing
        try { // TODO: add images
            background = ImageIO.read(new File ("images/track.png"));
            green_light = ImageIO.read(new File ("images/green_light.png"));
            red_light = ImageIO.read(new File ("images/red_light.png"));
        }
        catch (IOException e) {

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
            // initialize graphics
            Graphics g = appFrame.getGraphics();

            // initialize panel for graphics
            JPanel gamePanel = new JPanel();
            gamePanel.setSize(WINWIDTH, WINHEIGHT);
            gamePanel.setVisible(true);
            appFrame.add(gamePanel);

            while (!endgame){
                drawBackground(g);
            }
        }
        // draw the background
        private void drawBackground(Graphics g) {
            g.drawImage(background, 0, 0, null);
            g.dispose();
        }
    }

    private static class Music implements Runnable {

        public Music() {
            musicVolumeMultiplier = 0.2f;
        }

        @Override
        public void run() {
            playSound();
        }

        // TODO: redo changing selected song
        private void playSound(){
            File song = endgame ?
                    new File("audio/music/endBGM.wav") : new File("audio/music/bgm.wav");
            AudioInputStream inputStream = null;

            // basic play music with looping
            try {
                Clip clip = AudioSystem.getClip();
                inputStream = AudioSystem.getAudioInputStream(song);
                clip.open(inputStream);

                // Get the volume control from the clip
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Adjust the volume by providing a multiplier
                float currentGain = gainControl.getValue();
                float targetGain = currentGain + (20.0f * (float) Math.log10(musicVolumeMultiplier));
                gainControl.setValue(targetGain);
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
            System.out.println("Start Game");
            menuBar.setVisible(false);

            // initialize variables for game state
            endgame = true;
            endgame =  false;
            wPressed = false;
            aPressed = false;
            sPressed = false;
            dPressed = false;

            // define threads
            Thread t1 = new Thread(new Countdown());
            Thread t2 = new Thread(new Animate());

            // start threads
            t1.start();
            t2.start();
        }
    }

    private static class Countdown implements Runnable {
        @Override
        public void run() {
            // set countdown timer to 3000 milliseconds
            long countdownDuration = 3000;

            // basic countdown
            try {
                System.out.println("Get Ready!");

                while (countdownDuration > 0) {
                    System.out.println(countdownDuration / 1000);

                    // Pause the execution for 1 second (1000 milliseconds)
                    Thread.sleep(1000);

                    // Subtract 1000 milliseconds (1 second) from the countdown duration
                    countdownDuration -= 1000;
                }

                System.out.println("Go!");

                counted = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            // listen for specific key and change pressed variable
            if(action.equals("W")){
                System.out.println("W");
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
                System.out.println("UP");
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

    // TODO: redo confirm quit window
    private static class ConfirmQuit implements ActionListener {
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

    // Option window to change the volume of the game
    // TODO: change options window from JFrame to JPanel
    // TODO: implement volume adjustment
    private static class Options implements ActionListener {
        private void popup(){
            System.out.println("Open options");

            JFrame optionsFrame = new JFrame("Options");
            optionsFrame.setSize(200, 200);
            optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel optionsPanel = new JPanel();
            optionsPanel.setBackground(Color.LIGHT_GRAY);

            optionsFrame.add(optionsPanel);

            // Center the optionsFrame on the screen
            optionsFrame.setLocationRelativeTo(null);

            optionsFrame.setVisible(true);
        }
        public void actionPerformed(ActionEvent e) {
            popup();
        }
    }

    // select car menu
    private static class SelectCar implements ActionListener{
        private SelectCar(){

        }

        public void openMenu(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

    // Main Menu which has the selection buttons
    private static class MainMenu implements Runnable{
        public MainMenu(){
            run();
        }
        @Override
        public void run() {

            // initialize menu bar JPanel
            System.out.println("main menu");
            menuBar = new JPanel();
            menuBar.setBackground(new Color(0, 0, 0, 0));
            menuBar.setLayout(new FlowLayout(FlowLayout.CENTER));

            // define buttons
            JButton startGameButton = createButton("Start Game", new StartGame(), menuButtonSize);
            JButton carSelectButton = createButton("Select Car", null, menuButtonSize);
            JButton optionsButton = createButton("Options", new Options(), menuButtonSize);
            JButton exitButton = createButton("Quit Game", new ConfirmQuit(), menuButtonSize);

            // add buttons to menu bar
            menuBar.add(Box.createVerticalStrut(menuButtonHeight * 2));
            menuBar.add(startGameButton);
            menuBar.add(Box.createVerticalStrut(menuButtonHeight * 2));
            menuBar.add(carSelectButton);
            menuBar.add(Box.createVerticalStrut(menuButtonHeight * 2));
            menuBar.add(optionsButton);
            menuBar.add(Box.createVerticalStrut(menuButtonHeight * 2));
            menuBar.add(exitButton);

            menuBar.setVisible(true);
            appFrame.add(menuBar, BorderLayout.SOUTH);
        }
    }

    // basic function to create custom button
    private static JButton createButton(String title, ActionListener actionListener, Dimension dim) {
        JButton button = new JButton(title);

//        button.setBackground(new Color(235, 235, 235));
//        button.setContentAreaFilled(false); // transparent background
//        button.setBackground(null);        button.setMaximumSize(dim);
        button.setBorder(null);
        button.setFont(new Font("Proxy 1", Font.PLAIN, 12));

        if (actionListener != null) {
            button.addActionListener(actionListener);
        }

        return button;
    }

    // detects which key was pressed and released
    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    public static void main(String[] args) {
        setup();
        new MainMenu();

        appFrame.setLocationRelativeTo(null);
        appFrame.setVisible(true);

//        Music audio = new Music();
//        audio.playSound();
    }

    private static final int menuButtonHeight = 50;
    private static final Dimension menuButtonSize = new Dimension(200, menuButtonHeight);

    private static boolean endgame;
    private static boolean counted;

    private static BufferedImage background;
    private static BufferedImage green_light;
    private static BufferedImage red_light;
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

    private static double p1X;
    private static double p1Y;
    private static double cameraX;
    private static double cameraY;

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
    private static JPanel menuBar;

    private static float musicVolumeMultiplier;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}
