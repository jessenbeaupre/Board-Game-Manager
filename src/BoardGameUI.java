
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class BoardGameUI implements Runnable{

    BoardGame game;

    private ImageIcon imageIcon;

    private JLabel titleLabel;
    private JLabel playersLabel;
    private JLabel themesLabel;
    private JLabel mechanicsLabel;
    private JLabel imageRefLabel;
    private JLabel setupTimeLabel;
    private JLabel approxPlayTimeLabel;
    private JLabel commentsLabel;

    private JTextField titleField;
    private JTextField playersField;
    private JTextField themesField;
    private JTextField mechanicsField;
    private JTextField setupTimeField;
    private JTextField approxPlayTimeField;
    private JTextArea commentsArea;
    private JButton editButton;
    private JButton imageButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JScrollPane commentsPane;

    private Container contentPane;
    private SpringLayout spLayout;

    private static JFrame gameFrame;

    BoardGameUI(BoardGame bg){

        game = bg;
        imageIcon = new ImageIcon(game.loadImage());
    }

    BoardGameUI(){
        //sets some default empty values to avoid errors
        game = new BoardGame();
        imageIcon = new ImageIcon(game.loadImage());
        }

    public void run(){

        //creates the ui content
        createFrame();
        createContent();

        //if the game was created without data (called from add new) then also add editing components
        if (game.getGameID() == null) editPressed();

        //Display the window.
        gameFrame.pack();
        gameFrame.setVisible(true);
        gameFrame.setMinimumSize(new Dimension(550, 0));
    }

    private void createFrame(){
        gameFrame = new JFrame("Board Game Name"); //initializes and titles the window
        gameFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); //sets the x button to terminate the program
        gameFrame.setSize(600  ,800); //sets starting dimensions
        gameFrame.setVisible(true); //need here as well as later or the font area has a null pointer exception

        //Set up the content pane.
        contentPane = gameFrame.getContentPane();
        spLayout = new SpringLayout();
        contentPane.setLayout(spLayout);
    }

    private void createContent(){

        //creates buttons
        editButton = new JButton("Edit Page");
        imageButton = new JButton("Edit Image");

        //Create and add the components.
        imageRefLabel = new JLabel(imageIcon);
        titleLabel = new JLabel("Title: " + game.getTitle());
        playersLabel = new JLabel("Players: " + game.getPlayers());
        themesLabel = new JLabel("Themes: " + game.getThemes());
        mechanicsLabel = new JLabel("Mechanics: " + game.getMechanics());
        setupTimeLabel = new JLabel("Setup time: " + game.getSetupTime() + "m");
        approxPlayTimeLabel = new JLabel("Approximate play length: " + game.getApproxPlayTime() + "m");
        commentsLabel = new JLabel("<html>Comments:<br>" + game.getComments().replaceAll("\n", "<br>") + "</html>");

        //add the components to the content pane
        contentPane.add(imageRefLabel);
        contentPane.add(imageButton);
        contentPane.add(titleLabel);
        contentPane.add(playersLabel);
        contentPane.add(themesLabel);
        contentPane.add(mechanicsLabel);
        contentPane.add(setupTimeLabel);
        contentPane.add(approxPlayTimeLabel);
        contentPane.add(commentsLabel);
        contentPane.add(editButton);

        //Adjust constraints for the labels
        //image anchors
        spLayout.putConstraint(SpringLayout.WEST, imageRefLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, imageRefLabel, 5, SpringLayout.NORTH, contentPane);

        //image button anchors
        spLayout.putConstraint(SpringLayout.WEST, imageButton, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, imageButton, 2, SpringLayout.SOUTH, imageRefLabel);

        //Title label anchors
        spLayout.putConstraint(SpringLayout.WEST, titleLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, titleLabel, 10, SpringLayout.SOUTH, imageButton);

        //Players label anchors
        spLayout.putConstraint(SpringLayout.WEST, playersLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, playersLabel, 10, SpringLayout.SOUTH, titleLabel);

        //Themes label anchors
        spLayout.putConstraint(SpringLayout.WEST, themesLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, themesLabel, 10, SpringLayout.SOUTH, playersLabel);

        //Mechanics label anchors
        spLayout.putConstraint(SpringLayout.WEST, mechanicsLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, mechanicsLabel, 10, SpringLayout.SOUTH, themesLabel);

        //Setup time label anchors
        spLayout.putConstraint(SpringLayout.WEST, setupTimeLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, setupTimeLabel, 10, SpringLayout.SOUTH, mechanicsLabel);

        //Approx play time label anchors
        spLayout.putConstraint(SpringLayout.WEST, approxPlayTimeLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, approxPlayTimeLabel, 10, SpringLayout.SOUTH, setupTimeLabel);

        //Comments label anchors
        spLayout.putConstraint(SpringLayout.WEST, commentsLabel, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, commentsLabel, 10, SpringLayout.SOUTH, approxPlayTimeLabel);

        //Title button anchors
        spLayout.putConstraint(SpringLayout.WEST, editButton, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, editButton, 10, SpringLayout.SOUTH, commentsLabel);

        //creates container edges on the bottom
        spLayout.putConstraint(SpringLayout.SOUTH, contentPane, 20, SpringLayout.SOUTH, editButton);

        //setting a listener to check for edit pressed and calling the respective method
        ActionListener editPress = e -> editPressed();
        editButton.addActionListener(editPress);
        ActionListener editImagePress = e -> editImagePressed();
        imageButton.addActionListener(editImagePress);
    }

    private void editPressed() {
        //initializes the textfield components
        titleField = new JTextField(game.getTitle());
        playersField = new JTextField(game.getPlayers());
        themesField = new JTextField(game.getThemes());
        mechanicsField = new JTextField(game.getMechanics());
        setupTimeField = new JTextField(game.getSetupTime());
        approxPlayTimeField = new JTextField(game.getApproxPlayTime());
        commentsArea = new JTextArea(game.getComments());

        //creates buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        //makes a new text area and sets it in a scrollpane
        commentsPane = new JScrollPane(commentsArea);
        contentPane.add(commentsPane);

        //adding the components to the content pane
        contentPane.add(titleField);
        contentPane.add(playersField);
        contentPane.add(themesField);
        contentPane.add(mechanicsField);
        contentPane.add(setupTimeField);
        contentPane.add(approxPlayTimeField);
        contentPane.add(commentsPane);
        contentPane.add(saveButton);
        contentPane.add(cancelButton);

        //a padding value for easy adjustment
        int rightPadding = 50;
        //title constraints
        titleLabel.setText("Title: ");
        spLayout.putConstraint(SpringLayout.WEST, titleField, 2, SpringLayout.EAST, titleLabel);
        spLayout.putConstraint(SpringLayout.NORTH, titleField, 0, SpringLayout.NORTH, titleLabel);
        titleField.setPreferredSize(new Dimension((contentPane.getWidth() - titleLabel.getWidth() + titleField.getPreferredSize().width - rightPadding), titleField.getPreferredSize().height));
        //players constraints
        playersLabel.setText("Players: ");
        spLayout.putConstraint(SpringLayout.WEST, playersField, 2, SpringLayout.EAST, playersLabel);
        spLayout.putConstraint(SpringLayout.NORTH, playersField, 0, SpringLayout.NORTH, playersLabel);
        playersField.setPreferredSize(new Dimension((contentPane.getWidth() - playersLabel.getWidth() + playersField.getPreferredSize().width - rightPadding), playersField.getPreferredSize().height));
        //themes constraints
        themesLabel.setText("Themes: ");
        spLayout.putConstraint(SpringLayout.WEST, themesField, 2, SpringLayout.EAST, themesLabel);
        spLayout.putConstraint(SpringLayout.NORTH, themesField, 0, SpringLayout.NORTH, themesLabel);
        themesField.setPreferredSize(new Dimension((contentPane.getWidth() - themesLabel.getWidth() + themesField.getPreferredSize().width - rightPadding), themesField.getPreferredSize().height));
        //mechanics constraints
        mechanicsLabel.setText("Mechanics: ");
        mechanicsLabel.updateUI();
        spLayout.putConstraint(SpringLayout.WEST, mechanicsField, 2, SpringLayout.EAST, mechanicsLabel);
        spLayout.putConstraint(SpringLayout.NORTH, mechanicsField, 0, SpringLayout.NORTH, mechanicsLabel);
        mechanicsField.setPreferredSize(new Dimension((contentPane.getWidth() - mechanicsLabel.getWidth() + mechanicsField.getPreferredSize().width - rightPadding), mechanicsField.getPreferredSize().height));
        //setup constraints
        setupTimeLabel.setText("Setup time: ");
        spLayout.putConstraint(SpringLayout.WEST, setupTimeField, 2, SpringLayout.EAST, setupTimeLabel);
        spLayout.putConstraint(SpringLayout.NORTH, setupTimeField, 0, SpringLayout.NORTH, setupTimeLabel);
        setupTimeField.setPreferredSize(new Dimension((contentPane.getWidth() - setupTimeLabel.getWidth() + setupTimeField.getPreferredSize().width - rightPadding), setupTimeField.getPreferredSize().height));
        //approx play constraints
        approxPlayTimeLabel.setText("Approximate play length: ");
        spLayout.putConstraint(SpringLayout.WEST, approxPlayTimeField, 2, SpringLayout.EAST, approxPlayTimeLabel);
        spLayout.putConstraint(SpringLayout.NORTH, approxPlayTimeField, 0, SpringLayout.NORTH, approxPlayTimeLabel);
        approxPlayTimeField.setPreferredSize(new Dimension((contentPane.getWidth() - approxPlayTimeLabel.getWidth() + approxPlayTimeField.getPreferredSize().width - rightPadding), approxPlayTimeField.getPreferredSize().height));
        //comments constraints
        commentsLabel.setText("Comments:\n");
        spLayout.putConstraint(SpringLayout.WEST, commentsPane, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, commentsPane, 2, SpringLayout.SOUTH, commentsLabel);
        commentsPane.setPreferredSize(new Dimension((contentPane.getWidth() - rightPadding), (commentsArea.getPreferredSize().height) * 2));
        //save button constraints
        spLayout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, saveButton, 10, SpringLayout.SOUTH, commentsPane);
        //cancel button constraints
        spLayout.putConstraint(SpringLayout.WEST, cancelButton, 5, SpringLayout.EAST, saveButton);
        spLayout.putConstraint(SpringLayout.NORTH, cancelButton, 0, SpringLayout.NORTH, saveButton);

        //contentpane bottom constraint
        spLayout.putConstraint(SpringLayout.SOUTH, contentPane, 10, SpringLayout.SOUTH, cancelButton);

        editButton.setVisible(false);

        //creates and adds event listeners to the buttons
        ActionListener savePressed = e -> savePressed();
        ActionListener cancelPressed = e -> cancelPressed();
        saveButton.addActionListener(savePressed);
        cancelButton.addActionListener(cancelPressed);
    }

    private void editImagePressed(){

        //creates a dialog box to get the local image path and creates file and writer objects
        String imagePath = JOptionPane.showInputDialog("please copy file path to image");
        Path imageFilePath = Paths.get("images.txt");
        StringBuilder fileData = new StringBuilder();

        try{
            //creates objects to check if the text file already has a file path
            Scanner fileScanner = new Scanner(imageFilePath.toFile());
            fileScanner.useDelimiter("\n");
            String fileLine;
            boolean wrote = false;
            //loops through to see if any of the lines contain the file and edits that path if it does, and creates a new line if it doesn't
            while (fileScanner.hasNext()){
                fileLine = fileScanner.nextLine();
                System.out.println(fileLine);
                if (fileLine.contains(game.getTitle())){
                    //adds the new file path to the current file and changes that it wrote it to the bool
                    fileData.append(game.getTitle()).append("-").append(imagePath).append("\n");
                    wrote = true;
                }else{
                    //writes each other path again if it's not the one we're looking for
                    fileData.append(fileLine).append("\n");
                }
            }
            //writes a new line if it didn't already write one due to it not finding the line
            if (!wrote){
                fileData.append(game.getTitle()).append("-").append(imagePath).append("\n");
            }

            //writes the new file builder data to a file flushes so it finishes and then closes the writer
            FileWriter fileWriter = new FileWriter(imageFilePath.toFile());
            fileWriter.write(fileData.toString());
            fileWriter.flush();
            fileWriter.close();

        }catch(IOException e){
            //logs the error if it runs into an exception
            ErrorReporter.log(e);
        }
    }

    private void savePressed(){
        //creats a new local database handler
        DatabaseHandler dbh = new DatabaseHandler();

        //creates values from all the textfields to be sent to the save method on the handler
        String[] playersMinMax;
        playersMinMax = playersField.getText().split("-", 2);
        playersMinMax[0] = dbh.purgeNumberInput(playersMinMax[0]);
        playersMinMax[1] = dbh.purgeNumberInput(playersMinMax[1]);

        game.setTitle(titleField.getText());
        game.setPlayers(playersMinMax[0] + "-" + playersMinMax[1]);
        game.setThemes(themesField.getText());
        game.setMechanics(mechanicsField.getText());
        game.setSetupTime(dbh.purgeNumberInput(setupTimeField.getText()));
        game.setApproxPlayTime(dbh.purgeNumberInput(approxPlayTimeField.getText()));
        game.setComments(commentsArea.getText());

        //adds all those values to an array
        String[] data = new String[]{game.getTitle(), playersMinMax[0], playersMinMax[1], game.getThemes(),
                game.getMechanics(), game.getSetupTime(), game.getApproxPlayTime(), game.getComments(), game.getGameID()};

        //calls the update method with the data string
        dbh.updateData(game);

        //calls cancel to refresh the ui
        cancelPressed();

    }

    private void cancelPressed(){
        //removes all the edit elements
        contentPane.remove(titleField);
        contentPane.remove(playersField);
        contentPane.remove(themesField);
        contentPane.remove(mechanicsField);
        contentPane.remove(setupTimeField);
        contentPane.remove(approxPlayTimeField);
        contentPane.remove(commentsPane);
        contentPane.remove(saveButton);
        contentPane.remove(cancelButton);

        //removes all normal elemts
        contentPane.remove(imageRefLabel);
        contentPane.remove(imageButton);
        contentPane.remove(titleLabel);
        contentPane.remove(playersLabel);
        contentPane.remove(themesLabel);
        contentPane.remove(mechanicsLabel);
        contentPane.remove(setupTimeLabel);
        contentPane.remove(approxPlayTimeLabel);
        contentPane.remove(commentsLabel);
        contentPane.remove(editButton);

        //recreates all the elements and paint the ui again with the new data
        createContent();
        gameFrame.paintAll(gameFrame.getGraphics());
    }

}
