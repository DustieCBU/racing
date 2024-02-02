import java.awt.*;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Racing { // incorporating audio, starting traffic light, start menu with car selection
    public Racing() {
        setup();
        displayMenu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Racing();
        });
    }

    public static void setup() {
        XOFFSET = 0;
        YOFFSET = -20; // so the background doesn't go out of frame at the bottom
        WINWIDTH = 1280;
        WINHEIGHT = 720;
        p1originalX = 590;
        p1originalY = 200;
        p2originalX = 590;
        p2originalY = 250;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endGame = false;
        countdownDuration = 3000;
        counted = false;
        p1Lap = 0;
        p2Lap = 0;
        lightScale = 0.1;
        lightX = 550;
        lightY = 20;
        musicVolumeMultiplier = 0.2f;
        maxSpeed = 4.5;
        playing = false;
        menuButtonHeight = 50;
        buttonColor = new Color(0, 230, 64);

        // image processing
        try {
            // track background
            background = ImageIO.read(new File ("images/bg.png"));
            street = ImageIO.read(new File ("images/street.png"));
            border = ImageIO.read(new File ("images/border.png"));

            // countdown lights
            green_light = ImageIO.read(new File ("images/green_light.png"));
            yellow_light = ImageIO.read(new File ("images/yellow_light.png"));
            red_light = ImageIO.read(new File ("images/red_light.png"));

            // cars
            carGreen = ImageIO.read(new File ("images/carGreen_small.png"));
            carBlue = ImageIO.read(new File ("images/carBlue_small.png"));
            carRed = ImageIO.read(new File ("images/carRed_small.png"));
            carOrange = ImageIO.read(new File ("images/carOrange_small.png"));
            carPink = ImageIO.read(new File ("images/carPink_small.png"));
            carYellow = ImageIO.read(new File ("images/carYellow_small.png"));
            carCyan = ImageIO.read(new File ("images/carCyan_small.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // scale down countdown images
        green_light = scaleImage(green_light, lightScale);
        yellow_light = scaleImage(yellow_light, lightScale);
        red_light = scaleImage(red_light, lightScale);

        p1Image = carGreen; // TODO: temporary needs to be replaced in selectcar
        p2Image = carBlue;
        carWidth = 45.0;
        carHeight = 56.0;
    }

    private static void displayMenu() {
        appFrame = new JFrame("Racing");
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH, WINHEIGHT);
        appFrame.setResizable(false);

        // keybinding panel
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

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = menuButtonHeight;
        gbc.ipadx = 100;
        gbc.insets = new Insets(0, 10, 50, 10);

        // game panel
        JPanel gamePanel = new GamePanel();
        gamePanel.setLayout(new GridBagLayout());

        // options panel
        JPanel optionsPanel = new SliderPanel();
        optionsPanel.setVisible(false);

        // initialize menu bar JPanel
        menuBar = new JPanel();
        menuBar.setLayout(new GridBagLayout());

        // define buttons
        JButton startGameButton = createButton("Start Game", new StartGame((GamePanel) gamePanel));
        JButton carSelectButton = createButton("Select Car", null);
        JButton optionsButton = createButton("Music Volume", new Options(optionsPanel));
        JButton exitButton = createButton("Quit Game", new ConfirmQuit());

        // add buttons to menu bar
        menuBar.add(startGameButton, gbc);
        menuBar.add(carSelectButton, gbc);
        menuBar.add(optionsButton, gbc);
        menuBar.add(exitButton, gbc);

        menuBar.setVisible(true);
        appFrame.add(menuBar, BorderLayout.SOUTH);
        appFrame.setLocationRelativeTo(null);
        appFrame.getContentPane().add(gamePanel);
        appFrame.getContentPane().add(optionsPanel);
        appFrame.setVisible(true);

        Music audio = new Music();
        audio.play();
    }

//    |---------------------- MAIN MENU ----------------------|

    private static class ConfirmQuit implements ActionListener {
        private static void showCustomOptionPane() {
            // Create a custom font
            Font customFont = new Font("Proxy 1", Font.PLAIN, 12);

            // Create a custom JButton with the custom font
            JButton customButton = new JButton("Confirm");
            customButton.setFont(customFont);

            // Create a custom ActionListener for the button
            ActionListener customActionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Handle button click
                    System.exit(0);
                }
            };

            // Create a custom JOptionPane with the custom font and button
            JOptionPane optionPane = new JOptionPane("Would you like to quit?", JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION, null, new Object[]{customButton});
            optionPane.setFont(customFont);
            customButton.addActionListener(customActionListener);

            // Create a custom JDialog to contain the JOptionPane
            JDialog dialog = optionPane.createDialog("Confirm Quit?");
            dialog.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCustomOptionPane();
        }
    }

    private static class Options implements ActionListener {
        private JPanel panel;

        public Options(JPanel panel) {
            this.panel = panel;
        }

        // Option window to change the volume of the game
        // TODO: change options window from JFrame to JPanel
        // TODO: implement volume adjustment
        private void popup(){
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
            panel.setVisible(true);
        }
    }

    public static class SliderPanel extends JPanel {
        private JSlider slider;
        private double sliderValue = 0.5; // Initial value
        private JButton backButton;

        public SliderPanel() {
            slider = new JSlider(0, 100, 50); // Minimum, maximum, initial value
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(5);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setFont(new Font("Proxy 1", Font.PLAIN, 16));

            // Update sliderValue when slider changes
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    sliderValue = slider.getValue() / 100.0;
                }
            });

            backButton = createButton("Confirm", e -> {
                this.setVisible(false);
                musicVolumeMultiplier = (float) getSliderValue();
                adjustVolume(musicVolumeMultiplier);
            });

            add(slider, gbc);
            add(backButton, gbc);
        }

        public double getSliderValue() {
            return sliderValue;
        }
    }

    private static class SelectCar implements ActionListener {
        private SelectCar(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

//    |---------------------- GAMEPLAY ------------------------|
    private static class StartGame implements ActionListener {

        private final GamePanel gamePanel;
        public StartGame(GamePanel panel){
            this.gamePanel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // initialize variables for game state
            wPressed = false;
            aPressed = false;
            sPressed = false;
            dPressed = false;

            p1 = new ImageObject(p1originalX, p1originalY, carWidth, carHeight, -1.5708);
            p2 = new ImageObject(p2originalX, p2originalY, carWidth, carHeight, -1.5708);

            menuBar.setVisible(false);
            playing = true;
            gamePanel.startTimer();

            // define threads
            Thread t1 = new Thread(new Countdown());
            Thread t2 = new Thread(new PlayerMover(p1, true));
            Thread t3 = new Thread(new PlayerMover(p2, false));

            // start threads
            t1.start();
            t2.start();
            t3.start();
        }
    }

    private static class Countdown implements Runnable {
        @Override
        public void run() {
            // basic countdown
            try {
                while (countdownDuration > 0) {
                    Thread.sleep(1000);
                    countdownDuration -= 1000;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class GamePanel extends JPanel {
        private Timer timer;

        public GamePanel() {
            timer = new Timer(32, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (playing) {
                        repaint();
                    }
                }
            });
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            if (playing) {
                // draw background
                g2D.drawImage(background, XOFFSET, YOFFSET, null);
                g2D.drawImage(street, XOFFSET, YOFFSET, null);
                g2D.drawImage(border, XOFFSET, YOFFSET, null);

                // draw players
                g2D.drawImage(rotateImageObject(p1).filter(p1Image, null), (int) p1.getX(),
                        (int) p1.getY(), null);
                g2D.drawImage(rotateImageObject(p2).filter(p2Image, null), (int) p2.getX(),
                        (int) p2.getY(), null);

                // draw stoplights
                if (countdownDuration > 2000) {
                    g2D.drawImage(red_light, lightX, lightY, null);
                } else if (countdownDuration > 1000) {
                    g2D.drawImage(yellow_light, lightX, lightY, null);
                } else if (countdownDuration > 0) {  // Adjusted condition for green light
                    g2D.drawImage(green_light, lightX, lightY, null);
                    counted = true;
                }

                // draw text


                g2D.dispose();
            }
        }

        public void startTimer(){
            timer.start();
        }

    }

    private static class PlayerMover implements Runnable {
        private double accelStep, rotateStep, brakeStep, friction;
        private ImageObject player;
        private boolean useWASD;

        public PlayerMover(ImageObject in, boolean useWASD) {
            accelStep = 0.03;
            rotateStep = 0.03;
            brakeStep = 0.05;
            friction = 0.009;
            this.player = in;
            this.useWASD = useWASD;
        }

        public void run() {
            while (!endGame){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }

                // after the initial countdown completes
                if (counted) { // TODO: change back to counted
                    if (player.getVelocity() > 0) player.changeVelocity(-friction);
                    else player.changeVelocity(friction);
                    System.out.println(player.getVelocity());
                    if (useWASD) {

                        if (wPressed && checkMaxSpeed()) player.changeVelocity(accelStep);
                        if (sPressed) player.changeVelocity(-brakeStep);
                        if (aPressed && checkStillRotate()) {
                            if (player.getVelocity() >= 0) player.rotate(rotateStep);
                            else player.rotate(-rotateStep);
                        }
                        if (dPressed && checkStillRotate()){
                            if (player.getVelocity() < 0) player.rotate(rotateStep);
                            else player.rotate(-rotateStep);
                        }
                    }
                    else {
                        if (upPressed && checkMaxSpeed()) player.changeVelocity(accelStep);
                        if (downPressed) player.changeVelocity(-brakeStep);
                        if (leftPressed && checkStillRotate()) {
                            if (player.getVelocity() >= 0) player.rotate(rotateStep);
                            else player.rotate(-rotateStep);
                        }
                        if (rightPressed && checkStillRotate()){
                            if (player.getVelocity() < 0) player.rotate(rotateStep);
                            else player.rotate(-rotateStep);
                        }
                    }
                    player.move(-player.getVelocity() * Math.cos(player.getAngle() - pi / 2.0),
                            player.getVelocity() * Math.sin(player.getAngle() - pi / 2.0));
                }
            }
        }

        private boolean checkStillRotate(){
            return (player.getVelocity() > 0.05 || player.getVelocity() < -0.05);
        }

        private boolean checkMaxSpeed() {
            return player.getVelocity() < maxSpeed;
        }
    }

//      |---------------------- MISC CLASSES ----------------------|

    private static class ImageObject {
        private double x;
        private double y;
        private double xwidth;
        private double yheight;
        private double angle; // radians
        private double internalangle; // radians
        private Vector<Double> coords;
        private double velocity;

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
            velocity = 0.0;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
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

        public void changeVelocity(double in) { velocity = velocity + (in); }

        public void setVelocity(double in) { velocity = in; }

        public double getVelocity() { return velocity; }

        public void setInternalAngle(double internalangle) {
            this.internalangle = internalangle;
        }

        public void setCoords(Vector<Double> coords) {
            this.coords = coords;
        }

        public void move(double xinput, double yinput){
            x = x + xinput;
            y = y + yinput;
        }

        public void moveTo(double xinput, double yinput){
            x = xinput;
            y = yinput;
        }

        public void rotate(double angleinput){
            angle = angle + angleinput;
            while (angle > twoPi){
                angle = angle - twoPi;
            }
            while (angle < 0){
                angle = angle + twoPi;
            }
        }

        public void spin(double internalangleinput){
            internalangle = internalangle + internalangleinput;
            while (internalangle > twoPi){
                internalangle = internalangle - twoPi;
            }
            while (internalangle < 0){
                internalangle = internalangle + twoPi;
            }
        }
    }

    private static class Music implements Runnable {
        private String path = "audio/music/bgm.wav";

        public Music() {

        }

        public Music(String file) {
            this.path = file;
        }

        public void play() {
            Thread t = new Thread(this);
            t.start();
        }

        public void run() {
            File songFile = new File(path);
            AudioInputStream inputStream = null;

            // basic play music with looping
            try {
                Clip clip = AudioSystem.getClip();
                inputStream = AudioSystem.getAudioInputStream(songFile);
                clip.open(inputStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);

                adjustVolume(musicVolumeMultiplier , clip);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//     |---------------------- MISC FUNCTIONS -------------------|

    private static JButton createButton(String title, ActionListener actionListener) {
        JButton button = new JButton(title);
        button.setFont(new Font("Proxy 1", Font.PLAIN, 16));
        button.setBorder(null);
//        button.setBackground(new Color(235, 235, 235));
//        button.setContentAreaFilled(false); // transparent background
//        button.setBackground(null);
//        button.setMaximumSize(dim);
//        button.setPreferredSize(dim);
        button.setBackground(buttonColor);

        if (actionListener != null) {
            button.addActionListener(actionListener);
        }

        return button;
    }

    private static BufferedImage scaleImage(BufferedImage in, double scale){
        BufferedImage before = in;
        int w = in.getWidth();
        int h = in.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);
        return after;
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj){
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(),
                35 / 2.0, carHeight / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static void adjustVolume(float volumeMultiplier, Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float currentGain = gainControl.getValue();
        float targetGain = currentGain + (20.0f * (float) Math.log10(volumeMultiplier));
        gainControl.setValue(targetGain);
    }

//     |-------------- KEY BINDING AND DETECTION --------------|

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

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

//     |---------------------- VARIABLES -----------------------|
    private static boolean playing;

    private static int menuButtonHeight;
    private static Color buttonColor;
    private static GridBagConstraints gbc;

    private static boolean endGame, counted;

    private static int countdownDuration;

    private static BufferedImage background, street, border;
    private static BufferedImage green_light, yellow_light, red_light;
    private static BufferedImage carRed, carBlue, carGreen, carCyan, carOrange, carPink, carYellow;

    private static int lightX, lightY;

    private static double maxSpeed;

    private static double lightScale;

    private static boolean upPressed, downPressed, leftPressed, rightPressed;
    private static boolean wPressed, aPressed, sPressed, dPressed;

    private static ImageObject p1, p2;
    private static BufferedImage p1Image, p2Image;

    private static int p1Lap, p2Lap;

    private static double carWidth, carHeight;
    private static double p1originalX, p1originalY;
    private static double p2originalX, p2originalY;

    private static int XOFFSET, YOFFSET, WINWIDTH, WINHEIGHT;
    private static double pi, twoPi;


    private static JFrame appFrame;
    private static JPanel menuBar;

    //TODO: Implement speed display

    private static float musicVolumeMultiplier;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}
