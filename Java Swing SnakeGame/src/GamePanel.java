import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel
{
    private final int screenSize=600; //Screen Width = 600 and Screen Height = 600
    private final int unitSize=25; //Apple and snake segments
    private Font gameFont = new Font("Ink Free", Font.BOLD, 60);
    private int gameSpeed=70;
    private Random random = new Random();
    private int xSnakeHead=random.nextInt(screenSize/3); //screenSize/3 because of starting snake position difficulties
    private int ySnakeHead=random.nextInt(screenSize/3); //screenSize/3 because of starting snake position difficulties
    private int xApple=random.nextInt(screenSize);
    private int yApple=random.nextInt(screenSize);
    private int xVelocity=25;
    private int yVelocity=25;
    private int score=0;
    private int[] xSeg = new int[100];
    private int[] ySeg = new int[100];
    private FontMetrics metrics = getFontMetrics(gameFont); //to set the subtitles correctly 
    private Timer gameTimer;
    private boolean gameLost = false;
    Color randomColor;
    private JButton startButton = new JButton("Start game");
    private JButton leaderboardButton = new JButton("Leaderboard");
    private JSlider speedSlider = new JSlider(20, 100, 70);
    private JLabel speedLabel = new JLabel("Set your game speed:");
    /* 
        direction=0 --> right, left
        direction=1 --> up, down
    */
    private int direction=0;
    
    GamePanel()
    {
        this.setPreferredSize(new Dimension(screenSize, screenSize));
        this.setBackground(Color.BLACK);
        
        setButtons();

        this.addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) 
            {
                keyClick(e);
            }
        });

        xSnakeHead=xSnakeHead-xSnakeHead%unitSize;
        ySnakeHead=ySnakeHead-ySnakeHead%unitSize;

        gameTimer = new Timer(gameSpeed, new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                ongoingGame();
            }
        });
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2D = (Graphics2D)g;
        super.paintComponent(g); //to set background
        g2D.setFont(gameFont);

        if(gameTimer.isRunning())
        {
            drawApple(g2D);
            drawSnake(g2D, xSnakeHead, ySnakeHead);
            for(int i=0; i<score; i++)
            {
                drawSnake(g2D, xSeg[i], ySeg[i]);
            }
            
            drawScore(g2D);
        }
        else if(gameLost==true)
        {
            g2D.setColor(Color.RED);
            g2D.drawString("YOU LOSE NOOB", (screenSize-metrics.stringWidth("YOU LOSE NOOB"))/2, screenSize/2);
        }
    }

    public void drawApple(Graphics2D g2D)
    {
        g2D.setColor(Color.RED);
        g2D.fillOval(xApple-xApple%unitSize, yApple-yApple%unitSize, unitSize, unitSize);
    }

    public void drawSnake(Graphics2D g2D, int x, int y)
    {
        randomColor = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        g2D.setColor(randomColor);
        g2D.fillRect(x, y, unitSize, unitSize);
    }
    public void drawScore(Graphics2D g2D)
    {
        g2D.setColor(Color.BLUE);
        g2D.drawString("Score: " + score, (screenSize-metrics.stringWidth("Score: " + score))/2, g2D.getFont().getSize());  
    }

    public void nextApple()
    {
        xApple=random.nextInt(screenSize);
        yApple=random.nextInt(screenSize);
    }

    public void moveSnake()
    {
        for(int i=score; i>0; i--) //setting snake segments
        {
            xSeg[i]=xSeg[i-1];
            ySeg[i]=ySeg[i-1];
        }

        if(score>0) //setting first segment to last snake head position
        {
            xSeg[0]=xSnakeHead;
            ySeg[0]=ySnakeHead;
        }

        if(direction == 0) xSnakeHead+=xVelocity;
        else ySnakeHead+=yVelocity;
    }
   
    public void keyClick(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_UP)
        {
            if(score==0)
            {
                direction=1;
                yVelocity=-25;
            }
            else if(score>0 && direction==0) //if snake have segment or segments then cant move in opposit direction
            {
                direction=1;
                yVelocity=-25;
            }
            
        } 
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if(score==0)
            {
                direction=1;
                yVelocity=25;
            }
            else if(score>0 && direction==0)
            {
                direction=1;
                yVelocity=25;
            }
            
        }  
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) 
        {
            if(score==0)
            {
                direction=0;
                xVelocity=25;
            }
            else if(score>0 && direction==1)
            {
                direction=0;
                xVelocity=25;
            }
            
        }  
        else if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            if(score==0)
            {
                direction=0;
                xVelocity=-25;
            }
            else if(score>0 && direction==1)
            {
                direction=0;
                xVelocity=-25;
            }
        }  
        repaint();
    }

    public void setButtons()
    {
        GridLayout grid = new GridLayout(2, 1);
        this.add(startButton, grid);
        startButton.setPreferredSize(new Dimension(400, 100));
        startButton.setBackground(Color.BLUE);
        startButton.setFocusable(false);
        startButton.setFont(gameFont);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                startGame();
                gameTimer.start();
            }
        });

        this.add(leaderboardButton, grid);
        leaderboardButton.setPreferredSize(new Dimension(400, 100));
        leaderboardButton.setBackground(Color.BLUE);
        leaderboardButton.setFocusable(false);
        leaderboardButton.setFont(gameFont);

        this.add(speedLabel, grid);
        speedLabel.setPreferredSize(new Dimension(400, 100));
        speedLabel.setOpaque(true);
        speedLabel.setBackground(Color.BLUE);
        speedLabel.setFocusable(false);
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);//Centering label
        speedLabel.setFont(new Font("Ink Free", Font.BOLD, 40));

        this.add(speedSlider, grid);
        speedSlider.setPreferredSize(new Dimension(400, 100));
        speedSlider.setBackground(Color.BLUE);
        speedSlider.setFocusable(false);
        speedSlider.setPaintLabels(true);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setFont(new Font("Ink Free", Font.BOLD, 20));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gameSpeed=((JSlider)e.getSource()).getValue();
                gameTimer.setDelay(gameSpeed);
            }
            
        });
    }

    public void setButtonsOff()
    {
        startButton.setVisible(false);
        startButton.setEnabled(false);

        leaderboardButton.setVisible(false);
        leaderboardButton.setEnabled(false);

        speedSlider.setVisible(false);
        speedSlider.setEnabled(false);

        speedLabel.setVisible(false);
        speedLabel.setEnabled(false);
    }

    public void ongoingGame()
    {
        moveSnake();
        repaint();
        checkBorderCollisions();
        checkAppleCollision();
        checkSegmentCollisions();
    }

    public void startGame()
    {
        this.requestFocusInWindow(true);
        setButtonsOff();
        gameLost=false;
    }

    public void checkAppleCollision()
    {
        int colApple = xApple / unitSize; 
        int rowApple = yApple / unitSize; 
        int colSnake = xSnakeHead / unitSize; 
        int rowSnake = ySnakeHead / unitSize; 

        if(rowSnake==rowApple && colSnake==colApple) //checking individual cells 
        {
            score++;
            nextApple();
        } 
    }

    public void checkBorderCollisions()
    {
        if(xSnakeHead>=600 || xSnakeHead+unitSize<=0 || ySnakeHead>=600 || ySnakeHead+unitSize<=0) lose();
    }

    public void checkSegmentCollisions()
    {
        int colSnake = xSnakeHead / unitSize; 
        int rowSnake = ySnakeHead / unitSize; 

        for(int i=0; i<score; i++)
        {
            int colSeg = xSeg[i] /unitSize;
            int rowSeg = ySeg[i] /unitSize;
            if(rowSnake==rowSeg && colSnake==colSeg) lose(); //checking individual cells 
        }
    }

    public void lose()
    {
        gameLost=true;
        gameTimer.stop();
        repaint();
        int option = JOptionPane.showConfirmDialog(getComponentPopupMenu(), "Restart?","Tic-Tac-Toe", JOptionPane.YES_NO_OPTION); 
        if(option==0) restart();
    }

    public void restart()
    {
        gameLost=false;
        score=0;
        xSnakeHead=random.nextInt(screenSize/3);
        ySnakeHead=random.nextInt(screenSize/3);
        nextApple();
        gameTimer.start();
        direction=0;
        xVelocity=25;
        yVelocity=25;
        xSnakeHead=xSnakeHead-xSnakeHead%unitSize;
        ySnakeHead=ySnakeHead-ySnakeHead%unitSize;
    }
}
