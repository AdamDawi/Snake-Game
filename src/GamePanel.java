import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;

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
    //------------Buttons for menu------------
    private JButton startButton = new JButton("Start game");
    private JButton leaderboardButton = new JButton("Leaderboard");
    private JButton exitButton = new JButton("Exit");
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
        
        setMenuButtons();
        xSnakeHead=xSnakeHead-xSnakeHead%unitSize;
        ySnakeHead=ySnakeHead-ySnakeHead%unitSize;

        this.addKeyListener(new KeyAdapter() 
        {
            @Override
            public void keyPressed(KeyEvent e) 
            {
                keyClick(e);
            }
        });

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
            g2D.drawString("YOU LOSE", (screenSize-metrics.stringWidth("YOU LOSE"))/2, screenSize/2);
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
        boolean collisionWithSeg=true;
        while(collisionWithSeg==true) //while there is collision with snake segment
        {
            collisionWithSeg=false;
            xApple=random.nextInt(screenSize);
            yApple=random.nextInt(screenSize);
            for(int i=0; i<score; i++)
            {
                if(xApple/unitSize==xSeg[i]/unitSize && yApple/unitSize==ySeg[i]/unitSize) //checking apple and snake segment rows and columns
                {
                    collisionWithSeg=true;
                    break;
                }
            }
        }
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

    public void setMenuButtons()
    {
        GridLayout grid = new GridLayout(2, 1);
        this.add(startButton, grid);
        startButton.setPreferredSize(new Dimension(400, 100));
        startButton.setBackground(Color.BLUE);
        startButton.setFocusable(false);
        startButton.setFont(gameFont);
        startButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                startGame();
            }
        });

        this.add(leaderboardButton, grid);
        leaderboardButton.setPreferredSize(new Dimension(400, 100));
        leaderboardButton.setBackground(Color.BLUE);
        leaderboardButton.setFocusable(false);
        leaderboardButton.setFont(gameFont);
        leaderboardButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                setMenuVisible(false);
                createLeaderboard();
            }
            
        });

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

        this.add(exitButton, grid);
        exitButton.setPreferredSize(new Dimension(400, 100));
        exitButton.setBackground(Color.BLUE);
        exitButton.setFocusable(false);
        exitButton.setFont(gameFont);
        exitButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                System.exit(0);
            }
            
        });
    }

    public void setMenuVisible(boolean s)
    {
        startButton.setVisible(s);
        startButton.setEnabled(s);

        leaderboardButton.setVisible(s);
        leaderboardButton.setEnabled(s);

        speedSlider.setVisible(s);
        speedSlider.setEnabled(s);

        speedLabel.setVisible(s);
        speedLabel.setEnabled(s);

        exitButton.setVisible(s);
        exitButton.setEnabled(s);
    }

    public void createLeaderboard()
    {
        JLabel[] playerLabels = new JLabel[5];
        JLabel topLabel = new JLabel("Top players:");
        JButton backButton = new JButton("Back");
        JPanel panel = this;
        GridLayout grid = new GridLayout(5, 1);

        this.add(topLabel, grid);
        topLabel.setPreferredSize(new Dimension(600, 80));
        topLabel.setOpaque(true);
        topLabel.setBackground(Color.BLUE);
        topLabel.setFocusable(false);
        topLabel.setHorizontalAlignment(SwingConstants.CENTER);//Centering label
        topLabel.setFont(new Font("Ink Free", Font.BOLD, 40));
        
        for(int i=0; i<5; i++)
        {
            playerLabels[i] = new JLabel();
            this.add(playerLabels[i], grid);
            playerLabels[i].setPreferredSize(new Dimension(600, 80));
            playerLabels[i].setOpaque(true);
            playerLabels[i].setBackground(Color.BLUE);
            playerLabels[i].setFocusable(false);
            playerLabels[i].setHorizontalAlignment(SwingConstants.CENTER);//Centering label
            playerLabels[i].setFont(new Font("Ink Free", Font.BOLD, 30));
        }
        readPlayerScores(playerLabels);
        this.add(backButton, grid);
        backButton.setPreferredSize(new Dimension(400, 80));
        backButton.setBackground(Color.BLUE);
        backButton.setFocusable(false);
        backButton.setFont(gameFont);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel.remove(topLabel);
                for(int i=0; i<5; i++) panel.remove(playerLabels[i]);
                panel.remove(backButton);
                setMenuVisible(true);
                panel.revalidate();
                panel.repaint();
            }
        });
    }

    public void savePlayerScore(String nick) 
    {
        try 
        {
            BufferedReader in = new BufferedReader(new FileReader("Scores.txt"));
            in.mark(8192); //for .reset() to work
            StringTokenizer tokens;
            String line, tempNick="";
            int tempSpeed=0, tempScore=0;
            int numberOfLines=0;
            TreeMap<Integer, ArrayList<HashMap<String, Integer>>> treeScore = new TreeMap<>(Collections.reverseOrder()); //reversed treemap(from the largest to the smallest) for players score, nick and speed
            
            for(; numberOfLines<5; numberOfLines++) if((line = in.readLine())==null) break;//when there are no more players scores
            in.reset(); //restarting position of reading lines to the start
            for(int j=0; j<numberOfLines; j++)
            {
                line = in.readLine();
                tokens = new StringTokenizer(line, ";");
                tempNick = tokens.nextToken();
                tempScore = Integer.parseInt(tokens.nextToken());
                tempSpeed = Integer.parseInt(tokens.nextToken());

                if (!treeScore.containsKey(tempScore)) //if tree have this score we make list to store multiple duplicates
                {
                    treeScore.put(tempScore, new ArrayList<HashMap<String, Integer>>());
                }

                HashMap<String, Integer> tempHashMap = new HashMap<>();
                tempHashMap.put(tempNick, tempSpeed);

                treeScore.get(tempScore).add(tempHashMap);
            }
            HashMap<String, Integer> tempHashMap = new HashMap<>();
            tempHashMap.put(nick, gameSpeed);

            if (!treeScore.containsKey(score)) //if tree have this score we make list to store multiple duplicates
            {
                treeScore.put(score, new ArrayList<HashMap<String, Integer>>());
            }
            
            treeScore.get(score).add(tempHashMap);
            
            in.close();

            PrintWriter out = new PrintWriter(new FileWriter("Scores.txt"));
            
            if(treeScore.size()>=5) numberOfLines=5; //if there is more than 5 scores
            else numberOfLines+=1;
            int k=0;

            for(Entry<Integer, ArrayList<HashMap<String, Integer>>> el : treeScore.entrySet())// taking 5 best scores from TreeMap
            {
                if(k>numberOfLines) break;
                tempScore=el.getKey();

                for (HashMap<String, Integer> playerScores : el.getValue()) 
                {
                    for (Map.Entry<String, Integer> pair : playerScores.entrySet()) 
                    {
                        tempNick = pair.getKey();
                        tempSpeed = pair.getValue();
                    }
                    if(++k>numberOfLines) break;
                    out.println(tempNick+";"+tempScore+";"+tempSpeed);
                }
            }
            out.close();
        } 
        catch (IOException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    public void readPlayerScores(JLabel[] playerLabels)
    {
        try 
        {
            BufferedReader in = new BufferedReader(new FileReader("Scores.txt"));
            String line, nickFromFile, scoreFromFile, speedFromFile;
            StringTokenizer tokens;

            for(int j=0; j<5; j++)
            {
                if((line = in.readLine())==null) break;//when there are no more players scores

                tokens = new StringTokenizer(line, ";");
                nickFromFile = tokens.nextToken();
                scoreFromFile = tokens.nextToken();
                speedFromFile = tokens.nextToken();
                playerLabels[j].setText(j+1+". "+nickFromFile+" score: "+scoreFromFile+" with speed "+speedFromFile);
            } 
            in.close();
        } 
        catch (IOException e) 
        {
            System.out.println(e.getMessage());
        }
    }

    public void createSaveFrame()
    {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JButton saveButton = new JButton("Save your score");
        JButton noButton = new JButton("No thanks");
        JLabel saveLabel = new JLabel("Write your nick:");
        JTextArea textArea = new JTextArea("");

        saveLabel.setFont(new Font("Ink Free", Font.BOLD, 40));
        saveLabel.setBackground(Color.BLUE);
        saveLabel.setOpaque(true);
        saveLabel.setFocusable(false);
        saveLabel.setHorizontalAlignment(SwingConstants.CENTER);

        textArea.setFont(new Font("Ink Free", Font.BOLD, 50));
        textArea.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(textArea.getText().length()>13) e.consume();
            }
        });

        saveButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        saveButton.setBackground(Color.BLUE);
        saveButton.setFocusable(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textArea.getText().equals("")) savePlayerScore(textArea.getText());
                setMenuVisible(true);
                gameLost=false;
                frame.dispose();
            }
            
        });

        noButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        noButton.setBackground(Color.BLUE);
        noButton.setFocusable(false);
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMenuVisible(true);
                gameLost=false;
                frame.dispose();
            }
            
        });

        bottomPanel.setLayout(new GridLayout(1, 2));
        bottomPanel.add(saveButton);
        bottomPanel.add(noButton);

        panel.setLayout(new GridLayout(3, 1));
        panel.setPreferredSize(new Dimension(500, 300));
        panel.add(saveLabel);
        panel.add(textArea);
        panel.add(bottomPanel);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
        setMenuVisible(false);
        restart();
    }

    public void lose()
    {
        gameLost=true;
        gameTimer.stop();
        createSaveFrame();
        repaint();
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
