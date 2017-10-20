package client;

import client.Client;

import java.util.Scanner;

public class UserInput extends Thread
{
    boolean run = true;

    public UserInput()
    {

    }

    public void run()
    {
        while (run)
        {
            Scanner scan = new Scanner(System.in).useDelimiter("\n");
            String readedLine = scan.next();
            Client.writeMessage(Client.getServerFlux(), readedLine);
        }
    }
}
