import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9001);//listen for incoming client connections.
        ArrayList<Socket> sockets = new ArrayList<>();
        System.out.println("server started");
        while (true) {
            Socket accept = server.accept();
            DataInputStream inFormClient = new DataInputStream(accept.getInputStream());//receive data from the client.

            Thread t1 = new Thread() {
                public void run() {
                    try {
                        String name = inFormClient.readUTF();
                        System.out.println(name + " : Joined to the Server");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (true) {
                        try {
                            String s = inFormClient.readUTF();
                            if (s.equals("img")) {
                                sendAll(sockets, accept, s);// calls the sendAll method with the list of connected sockets, the current client socket, and the string "img" as parameters.
                                String name = inFormClient.readUTF();//client who sent the image
                                System.out.println(name + ": trying to send image");
                                /*byte[] sizeAr = new byte[4];
                                inFormClient.read(sizeAr)*/;
                                int size = inFormClient.readInt();// size of the image data that will be received next.

                                byte[] imageAr = new byte[size];//size corresponding to the image data.
                                inFormClient.readFully(imageAr);
                                System.out.println(name + ": Got Image by server");
                                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));//The image data is then converted into a BufferedImage using ImageIO.read() and stored in the image variable.
                                sendAll(sockets, accept, name);//forward the message to all other clients.
                                sendAll(sockets, accept, imageAr);
                                ImageIO.write(image, "jpg", new File("test.jpg"));
                                //sendAll(sockets,accept,imageAr);
                                continue;
                            }
                            try {
                                sendAll(sockets, accept, s);
                            } catch (Exception e) {

                            }

                            System.out.println(s);

                        } catch (IOException e) {
                            e.printStackTrace();
                            sockets.remove(accept);
                            break;
                        }
                    }
                }
            };
            sockets.add(accept);
            t1.start();
        }
    }

    public static void sendAll(ArrayList<Socket> clients, Socket socket, String message) throws IOException {
        for (Socket client : clients) {
            try {
                DataOutputStream outFromClient = new DataOutputStream(client.getOutputStream());
                if (client.getPort() != socket.getPort()) {
                    outFromClient.writeUTF(message);
                    outFromClient.flush();
                }
            } catch (Exception e) {
                clients.remove(client);
            }


        }
    }

    public static void sendAll(ArrayList<Socket> clients, Socket socket, byte [] imagear) throws IOException {
        //takes a byte array representing the image data as a parameter.
        // It writes the size of the image data and the image data itself
        for (Socket client : clients) {
            DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
            if (client.getPort() != socket.getPort()) {
                /*outputStream.write(image);
                outputStream.flush();*/
                try {
                    //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    //ImageIO.write(, "jpg", byteArrayOutputStream);

                    int size = imagear.length;
                    outputStream.writeInt(size);
                    outputStream.write(imagear);
                    outputStream.flush();
                    System.out.println("Flushed: " + System.currentTimeMillis());
                } catch (IOException e) {
                    clients.remove(client);
                }

            }

        }
    }
}
