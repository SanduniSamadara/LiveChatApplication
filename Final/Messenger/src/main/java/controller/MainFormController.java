package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import connection.SocketConnection;
import dto.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.CatchingMessageTask;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainFormController {
    public JFXTextField txtMessage;
    public VBox vBox;
    public JFXButton btnSendMessage;
    public JFXTextField txtName;
    public JFXButton btnChooseImage;
    public AnchorPane backgroundPane;
    public ScrollPane scrollPane;
    public ScrollPane imojiScroll;
    public ImageView happyMood;
    public VBox emojiVboxContainer;
    public ImageView imghappyMood;


    public void initialize() {
        txtMessage.setVisible(false);
        btnSendMessage.setDisable(true);
        imojiScroll.setVisible(false);
        loadImojiToUI();

        try {
            Socket socket = SocketConnection.getConnection().getSocket();//establishes a socket connection to the server
            CatchingMessageTask task = new CatchingMessageTask(socket);//listens for incoming messages from the server and updates the UI accordingly.
            task.valueProperty().addListener(new ChangeListener<Message>() {
                @Override
                public void changed(ObservableValue<? extends Message> observable, Message oldValue, Message newValue) {
                    //System.out.println(newValue);
                    Label l1 = new Label(newValue.getMessage());
                    l1.setWrapText(true);
                    l1.setMaxWidth(100);
                    l1.setMinHeight(20);
                    if (newValue.getImage() == null) {
                        HBox hb = new HBox();
                        hb.setAlignment(Pos.CENTER_LEFT);
                        hb.getChildren().add(l1);
                        vBox.getChildren().add(hb);
                    } else {
                        HBox hb = new HBox();
                        hb.setAlignment(Pos.CENTER_LEFT);
                        hb.getChildren().add(l1);
                        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(newValue.getImage(), null));
                        imageView.setFitHeight(100);
                        imageView.setFitWidth(100);
                        hb.getChildren().add(imageView);
                        vBox.getChildren().add(hb);
                    }

                }
            });

            Thread t = new Thread(task);
            t.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageOnKeyAction(ActionEvent actionEvent) throws IOException {
        Label l1 = new Label("ME : " + txtMessage.getText());//creates a label to display the sent message, and adds it to the UI.
        l1.setWrapText(true);
        l1.setMaxWidth(100);
        l1.setMinHeight(20);
        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER_RIGHT);
        hb.getChildren().add(l1);
        vBox.getChildren().add(hb);
        SocketConnection.getConnection().sendToServer(txtName.getText() + " : " + txtMessage.getText());
    }

    public void sendMessageOnAction(ActionEvent actionEvent) throws IOException {
        sendMessageOnKeyAction(actionEvent);
    }

    public void txtNameOnAction(ActionEvent actionEvent) {
        String text = txtName.getText();
        Pattern compile = Pattern.compile("^[A-z|\\s]{3,}$");
        Matcher matcher = compile.matcher(text);
        if (matcher.matches()) {
            txtMessage.setEditable(true);
            btnSendMessage.setDisable(false);
            txtName.setEditable(false);
            txtMessage.setVisible(true);
        }
        try {
            SocketConnection.getConnection().sendToServer(txtName.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnChoseImgOnAction(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        btnChooseImage.setOnAction((final ActionEvent e) -> {
            File file = fileChooser.showOpenDialog((Stage) (btnChooseImage.getScene().getWindow()));//If a file is selected, it creates an ImageView to display the selected image, adds it to the UI,
            if (file != null) {
                Image image1 = new Image(file.toURI().toString());
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                ImageView imageView = new ImageView(image1);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                hBox.getChildren().add(new Label("Me : "));
                hBox.getChildren().add(imageView);
                vBox.getChildren().add(hBox);
                try {
                    SocketConnection.getConnection().sendToServer(file, txtName.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


/*    private void loadEmojiToUI() {

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File("Imoji.txt")));
            StringBuilder stringBuilder = new StringBuilder();
            int ch;
            while ((ch = inputStreamReader.read()) != -1) {
                stringBuilder.append((char) ch);
            }
            String s = stringBuilder.toString();
            String[] split = s.split("[ ]");
            int index = 0;

            for (int i = 0; i < buttons.size() - 3; i = i + 8) {
                HBox hBox = new HBox();
                for (int j = 0; j < 8; j++) {
                    hBox.getChildren().add(buttons.get(index));
                    index++;
                }
                imojiVboxContainer.getChildren().add(hBox);
            }

            scrollPane.setContent(emojiVboxContainer);  // Set the content of the scroll pane to the emojiVboxContainer

        } catch (IOException e) {//reads the emojis from a text file, creates buttons for each emoji, sets their actions, and adds them to the UI.
            e.printStackTrace();
        }
    }*/
private void loadImojiToUI() {
    try {
        File file = new File("Imoji.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);


        StringBuffer stringBuffer = new StringBuffer();

        int ch;
        while ((ch = inputStreamReader.read()) != -1) {
            stringBuffer.append((char) ch);
        }

        String s = stringBuffer.toString();
        String[] split = s.split("[ ]");

        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            Button button = new Button(split[i]);
            button.setBackground(Background.EMPTY);
            button.setFont(new Font(button.getFont().getName(), 16));

            button.setOnAction(event -> {
                txtMessage.setText(txtMessage.getText().concat(button.getText()));
            });

            button.setPrefWidth(60);
            buttons.add(button);
        }

        int index = 0;

        for (int i = 0; i < buttons.size() - 3; i = i + 8) {
            HBox hBox = new HBox();
            for (int j = 0; j < 6; j++) {
                hBox.getChildren().add(buttons.get(index));
                index++;
            }
            emojiVboxContainer.getChildren().add(hBox);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void imghappyMoodOnMouseClick(MouseEvent mouseEvent) {// It toggles the visibility of the emoji scroll pane in the UI.
        if (imojiScroll.isVisible()) {
            imojiScroll.setVisible(false);
        } else {
            imojiScroll.setVisible(true);
        }
    }

}
