import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.ArrayList;

public class Board extends JPanel implements ActionListener {
    private final int WIDTH    = 300;
    private final int HEIGHT   = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;
    private final int RAND_POS = 29;
    private final int DELAY    = 100;

    private ArrayList<Integer> x = new ArrayList<Integer>();
    private ArrayList<Integer> y = new ArrayList<Integer>();

    private int apple_x;
    private int apple_y;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private boolean inGame;
    private boolean paused;
    private boolean goldenActivated;

    private Timer timer;

    private Image apple;
    private Image ball;
    private Image golden;
    private Image head;
    private Image pause;

    public Board() {
        addKeyListener(new TAdapter());
        setBackground(Color.black);

        ImageIcon iia = new ImageIcon(this.getClass().getResource("apple.png"));
        ImageIcon iid = new ImageIcon(this.getClass().getResource("dot.png"));
        ImageIcon iig = new ImageIcon(this.getClass().getResource("gapple.png"));
        ImageIcon iih = new ImageIcon(this.getClass().getResource("head.png"));
        ImageIcon iip = new ImageIcon(this.getClass().getResource("pause.png"));

        apple  = iia.getImage();
        ball   = iid.getImage();
        golden = iig.getImage();
        head   = iih.getImage();
        pause  = iip.getImage();

        setFocusable(true);
        initGame();
    }

    public void initGame() {
        x.clear();
        y.clear();

        up = false;
        down = false;
        left = false;
        right = false;

        inGame = true;
        paused = false;
        goldenActivated = false;

        for (int z = 0; z < 3; z++) {
            x.add(150);
            y.add(150);
        }

        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paint(Graphics g) {
        super.paint(g);

        // Draw Snake
        for (int z = 1; z < x.size(); z++) {
            g.drawImage(ball, x.get(z), y.get(z), this);
        }
        g.drawImage(head, x.get(0), y.get(0), this);

        // Draw Apple
        if(!goldenActivated) {
            g.drawImage(apple, apple_x, apple_y, this);
        }
        else {
            g.drawImage(golden, apple_x, apple_y, this);
        }

        // Draw Pause
        if(paused) {
            g.drawImage(pause, 0, 0, this);
        }

        // Misc
        if(inGame) {
            Toolkit.getDefaultToolkit().sync();
        }
        else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        String msg1 = "Game Over";
        String msg2 = "Your Score: " + x.size();
        String msg3 = "Press R to Retry";
        Font small = new Font("Helvetica", Font.BOLD, 16);
        FontMetrics metr = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg1, (WIDTH - metr.stringWidth(msg1)) / 2, (HEIGHT / 2) - 30);
        g.drawString(msg2, (WIDTH - metr.stringWidth(msg2)) / 2, (HEIGHT / 2));
        g.drawString(msg3, (WIDTH - metr.stringWidth(msg3)) / 2, (HEIGHT / 2) + 30);
    }

    public void checkApple() {
        if ((x.get(0) == apple_x) && (y.get(0) == apple_y)) {
            // Add dots
            if(goldenActivated) {
                goldenActivated = false;
                addDot();
                addDot();
            }
            addDot();

            // Set up new apple
            if (((int) (Math.random() * 10)) <= 1)
            {
                goldenActivated = true;
            }
            locateApple();
        }
    }

    public void move() {
        x.remove(x.size()-1);
        y.remove(y.size()-1);

        x.add(0, x.get(0));
        y.add(0, y.get(0));

        if(left) {
            x.set(0, x.get(0) - DOT_SIZE);
        }
        if(right) {
            x.set(0, x.get(0) + DOT_SIZE);
        }
        if(up) {
            y.set(0, y.get(0) - DOT_SIZE);
        }
        if(down) {
            y.set(0, y.get(0) + DOT_SIZE);
        }
    }

    public void checkCollision() {
        for (int z = 3; z < x.size(); z++) {
            int a = (int) x.get(0);
            int b = (int) x.get(z);
            int c = (int) y.get(0);
            int d = (int) y.get(z);
            if ((a == b) && (c == d)) {
                inGame = false;
            }
        }

        if (y.get(0) > HEIGHT || y.get(0) < 0) {
            inGame = false;
        }
        if (x.get(0) > WIDTH || x.get(0) < 0) {
            inGame = false;
        }
    }

    public void locateApple() {
        int r1 = (int) (Math.random() * RAND_POS);
        int r2 = (int) (Math.random() * RAND_POS);
        apple_x = ((r1 * DOT_SIZE));
        apple_y = ((r2 * DOT_SIZE));

        for(int i = 0; i < x.size(); i++) {
            if(apple_x == x.get(i) && apple_y == y.get(i)) {
                locateApple();
            }
        }
    }

    public void addDot() {
        int end = x.size() - 1;
        x.add(end, x.get(end));
        y.add(end, y.get(end));
    }

    public void actionPerformed(ActionEvent e) {
        if (inGame && !paused) {
            checkApple();
            move();
            checkCollision();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if ((key == KeyEvent.VK_LEFT) && (!right)) {
                left = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!left)) {
                right = true;
                up = false;
                down = false;
            }

            if ((key == KeyEvent.VK_UP) && (!down)) {
                up = true;
                right = false;
                left = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!up)) {
                down = true;
                right = false;
                left = false;
            }

            if (key == KeyEvent.VK_P) 
            {
                paused = !paused;
            }

            if ((key == KeyEvent.VK_R) && (!inGame))
            {
                timer.stop();
                initGame();
            }
        }
    }
}
