package dto;

import java.awt.image.BufferedImage;

public class Message {
    private String message;
    private BufferedImage image;

    public Message(String message, BufferedImage image) {
        this.setMessage(message);
        this.setImage(image);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
