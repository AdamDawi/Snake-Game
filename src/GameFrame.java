import javax.swing.*;

public class GameFrame extends JFrame
{
    GameFrame()
    {
        this.setTitle("Snake");
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
        this.add(new GamePanel());
        pack();
        this.setLocationRelativeTo(null); //to set screen in the middle
        this.setVisible(true);
    }
}
