package service;

import dto.Message;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class CatchingMessageTask extends Task<Message> {
    private Socket socket;
    private DataInputStream inFromServer;

    public CatchingMessageTask(Socket socket) throws IOException {
        this.socket = socket;
        inFromServer = new DataInputStream(socket.getInputStream());
    }

    @Override
    protected Message call() throws Exception {
        while (true) {
            String s = inFromServer.readUTF();//reads data from the socket's input stream.
            if(s.equals("img")){
                //Receive name
                String name = inFromServer.readUTF();
                // Receive the image byte array from the server
                //InputStream inputStream = socket.getInputStream();
                System.out.println("Reading: " + System.currentTimeMillis());
                //byte[] sizeAr = new byte[4];

                System.out.println("Reading: 1 ok");
                int size = inFromServer.readInt();
                byte[] imageAr = new byte[size];
                inFromServer.readFully(imageAr);
                System.out.println("Reading: 2 ok");
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
                System.out.println("Converting ok");
                updateValue(new Message(name+" : ",image));//The received message and image are packaged into a Message object and passed to the updateValue() method
                System.out.println("Updated value");
                //System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                ImageIO.write(image, "jpg", new File("test2.jpg"));
                System.out.println("Over");
                continue;
            }
            updateValue(new Message(s,null));
        }
    }
}
