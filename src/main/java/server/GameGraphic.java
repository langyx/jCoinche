package server;

public class GameGraphic extends Thread
{

    GameGraphicFrame gameGraphicFrame = new GameGraphicFrame();
    boolean running = false;

    public GameGraphic()
    {

    }

    public void run()
    {
        this.running = true;
        while (running)
        {
            gameGraphicFrame.getContentPane().repaint();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
