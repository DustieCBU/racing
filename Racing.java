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
        YOFFSET = 0;
        WINWIDTH = 1280;
        WINHEIGHT = 720;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endGame = false;
        countdownDuration = 3000;
        p1Lap = 0;
        p2Lap = 0;
        lightScale = 0.1;
        musicVolumeMultiplier = 0.2f;

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
            e.printStackTrace();
        }

        // scale down countdown images
        green_light = scaleImage(green_light, lightScale);
        red_light = scaleImage(red_light, lightScale);
    }

    private static void displayMenu() {
        appFrame = new JFrame("Racing");
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH, WINHEIGHT);

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

        JPanel gamePanel = new GamePanel();
        gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.ipady = 15;
        gbc.ipadx = 50;

        // initialize menu bar JPanel
        System.out.println("main menu");
        menuBar = new JPanel();
        menuBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        // define buttons
        JButton startGameButton = createButton("Start Game", new StartGame((GamePanel) gamePanel), menuButtonSize);
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
        appFrame.setLocationRelativeTo(null);
        appFrame.getContentPane().add(gamePanel);
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
            JButton customButton = new JButton("OK");
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
            JOptionPane optionPane = new JOptionPane("Custom JOptionPane with Custom Font", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{customButton});
            optionPane.setFont(customFont);
            customButton.addActionListener(customActionListener);

            // Create a custom JDialog to contain the JOptionPane
            JDialog dialog = optionPane.createDialog("Custom Dialog");
            dialog.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCustomOptionPane();
            System.out.println("Prompt Quit");
        }
    }

    private static class Options implements ActionListener {
        // Option window to change the volume of the game
        // TODO: change options window from JFrame to JPanel
        // TODO: implement volume adjustment
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

    private static class SelectCar implements ActionListener{
        private SelectCar(){

        }

        public void openMenu(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }

//    |---------------------- GAMEPLAY ------------------------|
    private static class StartGame implements ActionListener{
        private final GamePanel gamePanel;
        public StartGame(GamePanel panel){
            this.gamePanel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Start Game");
            // initialize variables for game state
            endGame = true;

            wPressed = false;
            aPressed = false;
            sPressed = false;
            dPressed = false;

            endGame =  false;

            gamePanel.setPlaying(true);

            // define threads
            Thread t1 = new Thread(new Countdown());
            Thread t2 = new Thread(new PlayerMover());
            // start threads
            t1.start();
            t2.start();
        }
    }

    private static class Countdown implements Runnable {
        @Override
        public void run() {
            menuBar.setVisible(false);
            System.out.println("Hide menubar");
            // basic countdown
            try {
                System.out.println("Get Ready!");

                while (countdownDuration > 0) {
                    System.out.println(countdownDuration / 1000);
                    Thread.sleep(1000);
                    countdownDuration -= 1000;
                }
                System.out.println("Go!");


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class GamePanel extends JPanel {
        private Timer timer;
        private boolean playing;

        public GamePanel() {
            timer = new Timer(32, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (playing) {
                        repaint();
                    }
                }
            });

            playing = false;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (playing) {
                System.out.println("drawing");
                Graphics g2D = (Graphics2D) g;

                g2D.drawImage(background, XOFFSET, YOFFSET, null);

                g2D.dispose();
            }
        }

        public void setPlaying(boolean in) {
            playing = in;
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
            while (!endGame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
    }

//      |---------------------- MISC CLASSES ----------------------|

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

                // Get the volume control from the clip
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                // Adjust the volume by providing a multiplier
                float currentGain = gainControl.getValue();
                float targetGain = currentGain + (20.0f * (float) Math.log10(musicVolumeMultiplier));
                gainControl.setValue(targetGain);
                clip.start();
                System.out.println("Playing song");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//     |---------------------- MISC FUNCTIONS -------------------|

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

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

//     |---------------------- VARIABLES -----------------------|

    private static final int menuButtonHeight = 50;
    private static final Dimension menuButtonSize = new Dimension(200, menuButtonHeight);

    private static boolean endGame;

    private static long countdownDuration;

    private static BufferedImage background, green_light, red_light, player1, player2;

    private static double lightScale;

    private static boolean upPressed, downPressed, leftPressed, rightPressed;
    private static boolean wPressed, aPressed, sPressed, dPressed;

    private static ImageObject p1, p2;

    private static double p1width;
    private static double p1height;

    private static int p1Lap;
    private static int p2Lap;

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
    private static JPanel countdownPanel;

    private static float musicVolumeMultiplier;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}
