import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoardGameList {

    //initialize all variables we want to be able to talk to each other nicely
    private JTable gameTable;
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu actionsMenu;
    private JMenuItem openGame;
    private JMenuItem addNewGame;
    private JMenuItem resetSearchGames;
    private JMenuItem refreshMenu;
    private JMenuItem deleteGame;
    private JScrollPane scrollPane;
    private SpringLayout spLayout;
    private Container contentPane;
    private DefaultTableModel tableModel;

    private JLabel playersCountSearchLabel;
    private JLabel keywordSearchLabel;
    private JLabel setupMaxSearchLabel;
    private JLabel playMaxSearchLabel;
    private JLabel chaosChoiceLabel;
    private JSpinner playersSearchSpinner;
    private JSpinner setupMaxSpinner;
    private JSpinner playMaxSpinner;
    private JTextField keywordSearchField;
    private JCheckBox chaosChoiceBox;
    private JButton searchButton;
    private ExecutorService threadPool;

    //private DatabaseHandler dbh;
    private XMLHandler xmlHandler;

    private int tableRowSelected;

    private ArrayList<BoardGame> boardGames;
    private String columnNames[]={"Title","Players","Theme", "Mechanics", "Setup Time", "Approx. play Time"};

    private BoardGameList(){

        /*//creates and initializes the database handler so the shared variables are accessable
        dbh = new DatabaseHandler();
        dbh.initialize();

        //fills the array of data with the results of a query
        boardGames = dbh.getData();

        //asserts that data retrieval was not problematic
        assert (boardGames != null);
        */



        //creates the xml handler for pulling from and saving to a file
        xmlHandler = new XMLHandler();

        boardGames = xmlHandler.loadGames();

        //creates all the components for the ui
        createFrame();
        createMenu();
        createTable();
        createSearchComponents();
        setLayout();

        //sets the sizes of the columns so they're more view friendly
        resizeTable();

        //creates listener so you an double click to open a game and select it for other methods
        addMouseListener();

        //creates a thread pool for the game objects to execute independently
        threadPool = Executors.newSingleThreadExecutor();

        //adds important components to the main frame and bring them to the front with the set visible command
        frame.setVisible(true);//need or the new added components don't show up
    }

    private void addMouseListener(){
        //creates a listener that performs the open action on double click
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            if (e.getClickCount() == 2){
                openActionPerformed();
            }
            }
        };
        //adds the new listener to the table
        gameTable.addMouseListener(ml);
    }

    private void createFrame(){
        //creates the main ui window and sets it's starting dimensions
        frame = new JFrame("Board Game Manager"); //initializes and titles the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //sets the x button to terminate the program
        frame.setSize(1280  ,720); //sets starting dimensions
        frame.setVisible(true); //need here as well as later or the font area has a null pointer exception
    }

    private void createTable(){
        //created it as an anonymous inner class that disables editing the rows directly through the board game list by
        //overwriting the isCellEditable method to always return false so the edit pages will have to be used
        gameTable = new JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;   //Disallow the editing of any cell directly through the list
            }
        };

        //creates a table model so the data is easier to edit later and sets that table model to the table then adds data to it
        tableModel = new DefaultTableModel();
        gameTable.setModel(tableModel);
        //adds the columnNames values so they exist on the first itteration of the program
        tableModel.setColumnCount(0);
        for (String c: columnNames) {
            tableModel.addColumn(c);
        }

        refreshGamesListData();

        //sets a row select listener so we know what game data to pass to the open command
        gameTable.getSelectionModel().addListSelectionListener(e -> {
            tableRowSelected = gameTable.getSelectedRow();
        });

        //sets the auto resize type to what i believe works best in this use case
        gameTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        //adds the JTable to a scroll pane so when full it's scrollable
        scrollPane = new JScrollPane(gameTable);

    }

    private void createMenu(){
        //initialises menu pieces
        menuBar = new JMenuBar();
        actionsMenu = new JMenu("Actions");
        openGame = new JMenuItem("Open Board Game");
        addNewGame = new JMenuItem("Add Game");
        deleteGame = new JMenuItem("delete game");
        resetSearchGames = new JMenuItem("reset search");
        refreshMenu = new JMenuItem("refresh list");
        //nests the components in each other
        actionsMenu.add(openGame);
        actionsMenu.add(addNewGame);
        actionsMenu.add(deleteGame);
        actionsMenu.add(resetSearchGames);
        menuBar.add(actionsMenu);
        menuBar.add(refreshMenu);
        //adds an event listener for when options are selected
        ActionListener openPress = e -> openActionPerformed();
        openGame.addActionListener( openPress);
        ActionListener addNewPress = e -> addNewActionPerformed();
        addNewGame.addActionListener(addNewPress);
        ActionListener refreshPressed = e -> refreshGamesListData();
        refreshMenu.addActionListener(refreshPressed);
        ActionListener deletePressed = e-> deleteSelectedGame();
        deleteGame.addActionListener(deletePressed);
    }

    private void createSearchComponents(){

        //initialize the components for the search bar
        playersCountSearchLabel = new JLabel("Players:");
        keywordSearchLabel = new JLabel("Keyword search:");
        setupMaxSearchLabel = new JLabel("Max setup time:");
        playMaxSearchLabel = new JLabel("Max play time:");
        chaosChoiceLabel = new JLabel("Randomly choose game:");
        playersSearchSpinner = new JSpinner();
        keywordSearchField = new JTextField(8);
        setupMaxSpinner = new JSpinner();
        playMaxSpinner = new JSpinner();
        chaosChoiceBox = new JCheckBox();
        searchButton = new JButton("Search");

        //make the spinner objects not editable through text
        ((JSpinner.DefaultEditor) playMaxSpinner.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) setupMaxSpinner.getEditor()).getTextField().setEditable(false);
        ((JSpinner.DefaultEditor) playersSearchSpinner.getEditor()).getTextField().setEditable(false);

        //sets a uniform size and sets the spinners to aim for that size
        Dimension spinnerSize = new Dimension(40, 30);
        playersSearchSpinner.setPreferredSize(spinnerSize);
        playMaxSpinner.setPreferredSize(spinnerSize);
        setupMaxSpinner.setPreferredSize(spinnerSize);

        //adds an event listener to eearch when the button is pressed
        ActionListener searchPress = e -> searchGamesList();
        searchButton.addActionListener(searchPress);

    }

    private void setLayout(){

        //creates a container for the content pane and adds all the components
        spLayout = new SpringLayout();
        contentPane = frame.getContentPane();
        contentPane.setLayout(spLayout);
        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
        contentPane.add(scrollPane);
        contentPane.add(playersCountSearchLabel);
        contentPane.add(keywordSearchLabel);
        contentPane.add(setupMaxSearchLabel);
        contentPane.add(playMaxSearchLabel);
        contentPane.add(chaosChoiceLabel);
        contentPane.add(playersSearchSpinner);
        contentPane.add(keywordSearchField);
        contentPane.add(setupMaxSpinner);
        contentPane.add(playMaxSpinner);
        contentPane.add(chaosChoiceBox);
        contentPane.add(searchButton);

        //sets layout contraints for all main page components
        //players count label constraints
        spLayout.putConstraint(SpringLayout.WEST, playersCountSearchLabel, 20, SpringLayout.WEST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, playersCountSearchLabel, 5, SpringLayout.NORTH, contentPane);

        //keyword search label constraints
        spLayout.putConstraint(SpringLayout.WEST, keywordSearchLabel, 20, SpringLayout.EAST, playersCountSearchLabel);
        spLayout.putConstraint(SpringLayout.NORTH, keywordSearchLabel, 0, SpringLayout.NORTH, playersCountSearchLabel);

        //setup time max label constraints
        spLayout.putConstraint(SpringLayout.WEST, setupMaxSearchLabel, 20, SpringLayout.EAST, keywordSearchLabel);
        spLayout.putConstraint(SpringLayout.NORTH, setupMaxSearchLabel, 0, SpringLayout.NORTH, keywordSearchLabel);

        //play time max label constraints
        spLayout.putConstraint(SpringLayout.WEST, playMaxSearchLabel, 20, SpringLayout.EAST, setupMaxSearchLabel);
        spLayout.putConstraint(SpringLayout.NORTH, playMaxSearchLabel, 0, SpringLayout.NORTH, setupMaxSearchLabel);

        //chaos choice label
        spLayout.putConstraint(SpringLayout.WEST, chaosChoiceLabel, 20, SpringLayout.EAST, playMaxSearchLabel);
        spLayout.putConstraint(SpringLayout.NORTH, chaosChoiceLabel, 0, SpringLayout.NORTH, playMaxSearchLabel);

        //players spinner constraints
        spLayout.putConstraint(SpringLayout.NORTH, playersSearchSpinner, 5, SpringLayout.SOUTH, playersCountSearchLabel);
        spLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, playersSearchSpinner, 5, SpringLayout.HORIZONTAL_CENTER, playersCountSearchLabel);

        //keyword search text field constraints
        spLayout.putConstraint(SpringLayout.NORTH, keywordSearchField, 2, SpringLayout.SOUTH, keywordSearchLabel);
        spLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, keywordSearchField, 0, SpringLayout.HORIZONTAL_CENTER, keywordSearchLabel);

        //setup max spinner constraints
        spLayout.putConstraint(SpringLayout.NORTH, setupMaxSpinner, 5, SpringLayout.SOUTH, setupMaxSearchLabel);
        spLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, setupMaxSpinner, 0, SpringLayout.HORIZONTAL_CENTER, setupMaxSearchLabel);

        //play max spinner constraints
        spLayout.putConstraint(SpringLayout.NORTH, playMaxSpinner, 5, SpringLayout.SOUTH, playMaxSearchLabel);
        spLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, playMaxSpinner, 0, SpringLayout.HORIZONTAL_CENTER, playMaxSearchLabel);

        //chaos choice box constraints
        spLayout.putConstraint(SpringLayout.NORTH, chaosChoiceBox, 10, SpringLayout.SOUTH, chaosChoiceLabel);
        spLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, chaosChoiceBox, 0, SpringLayout.HORIZONTAL_CENTER, chaosChoiceLabel);

        //search button constraints
        spLayout.putConstraint(SpringLayout.NORTH, searchButton, 15, SpringLayout.NORTH, chaosChoiceLabel);
        spLayout.putConstraint(SpringLayout.WEST, searchButton, 25, SpringLayout.EAST, chaosChoiceLabel);

        //scroll layout constraints
        spLayout.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, contentPane);
        spLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, chaosChoiceBox);
        spLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, contentPane);
        spLayout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, contentPane);

    }

    private void resizeTable(){
        //creates new fonts for the table data and menu/table headers
        Font menuFont = new FontUIResource(frame.getFont().getFontName(), frame.getFont().getStyle(), 18);
        Font tableFont = new FontUIResource(frame.getFont().getFontName(), frame.getFont().getStyle(), 24);
        Font buttonFont = new FontUIResource(frame.getFont().getFontName(), Font.BOLD, 14);
        Font labelFont = new FontUIResource(frame.getFont().getFontName(), frame.getFont().getStyle(), 24);

        //assigns the fonts to the respective desired areas
        UIManager.put("Menu.font", menuFont);
        UIManager.put("MenuItem.font", menuFont);
        UIManager.put("Table.font", tableFont);
        UIManager.put("TableHeader.font", menuFont);
        UIManager.put("Label.font", labelFont);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("TextField.font", tableFont);
        UIManager.put("TextArea.font", tableFont);
        UIManager.put("Spinner.font", labelFont);

        //sets the table row height in line with the new font size
        gameTable.setRowHeight(24);
        //refreshes the components so the font updates show up on launch
        SwingUtilities.updateComponentTreeUI(frame);
        SwingUtilities.updateComponentTreeUI(gameTable);
        SwingUtilities.updateComponentTreeUI(actionsMenu);
        SwingUtilities.updateComponentTreeUI(openGame);
        //sets the columns to format after the text changes so all the information is presented well
        resizeColumnWidth(gameTable);
    }

    private void openActionPerformed(){

        //sends a new instance of the boardgame class with data to the threadpool
        threadPool.execute(new BoardGameUI(boardGames.get(tableRowSelected), xmlHandler));
    }

    private void addNewActionPerformed(){
        //creates a board game instance with blank fields and sends it to the thread pool
        threadPool.execute(new BoardGameUI(xmlHandler));
    }

    private void refreshGamesListData(){

        boardGames = xmlHandler.getBoardGameList();
        //deletes the current rows by setting them to 0 and adds all the rows from the data pull
        tableModel.setRowCount(0);
        for (BoardGame game : boardGames) {
            String[] dataLine = {game.getTitle(), game.getPlayers(), game.getThemes(), game.getMechanics(), game.getSetupTime() + "m", game.getApproxPlayTime() + "m"};
            tableModel.addRow(dataLine);
        }

        //adjusts the table dimensions and repaints it to the ui
        resizeTable();
        gameTable.repaint();
    }

    private void searchGamesList(){

        ArrayList<Map<String, String>> searchData = new ArrayList<>();

        //loops through all the results and adds the values to a local map variable then adds it to the results list
        for (BoardGame game:boardGames
             )
        {
            Map<String, String> map = new HashMap<>();

            map.put("title", game.getTitle());
            map.put("playersMin", game.getPlayersMin());
            map.put("playersMax",  game.getPlayersMax());
            map.put("themes", game.getThemes());
            map.put("mechanics", game.getMechanics());
            map.put("setupTime", game.getSetupTime());
            map.put("playTime", game.getApproxPlayTime());
            map.put("comments", game.getComments());
            map.put("gameID", game.getGameID());

            searchData.add(map);
        }

        //makes an arraylist of strings for the data that gets past the filter
        ArrayList<String[]> filteredData = new ArrayList<>();

        //sets all the relevant check data from the search component items
        int playersCheck = (Integer) playersSearchSpinner.getValue();
        String keyword = keywordSearchField.getText();
        int maxSetupCheck = (Integer) setupMaxSpinner.getValue();
        int maxPlaytimeCheck = (Integer) playMaxSpinner.getValue();
        boolean randomChoice  = chaosChoiceBox.isSelected();

        //creates a loop to go through all the elements of the unsorted data
        for (Map<String, String> map : searchData) {
            //Checks search component variables relative to their data counterpart or ignores it if the search component
            // hasn't been changed or is at it's default value
            if (    //checks that the player count is between the min and max
                    (Integer.parseInt(map.get("playersMin")) <= playersCheck &&
                    Integer.parseInt(map.get("playersMax")) >= playersCheck || playersCheck == 0) &&
                    //checks that the keyword is in either the title, mechanics, or themes strings
                    (map.get("title").toLowerCase().contains(keyword.toLowerCase()) ||
                            map.get("mechanics").toLowerCase().contains(keyword.toLowerCase()) ||
                            map.get("themes").toLowerCase().contains(keyword.toLowerCase()) ||
                            keyword.equals("")) &&
                    //checks that the setup or play time of the game is less than the search requires
                    (Integer.parseInt(map.get("setupTime")) <= maxSetupCheck || maxSetupCheck == 0) &&
                    (Integer.parseInt(map.get("playTime")) <= maxPlaytimeCheck || maxPlaytimeCheck == 0)) {

                //adds what gets past the filters to the new data array
                filteredData.add(new String[]{map.get("title"),map.get("playersMin") + "-" + map.get("playersMax"),
                        map.get("themes"),map.get("mechanics"),map.get("setupTime") + "m",map.get("playTime") + "m",map.get("comments"),map.get("gameID"),});
            }
        }

        //sets the rows to empty
        tableModel.setRowCount(0);

        //if the random choice box is unchecked add all the filtered data to the table
        if(!randomChoice) {
            for (String[] dataLine : filteredData) {
                tableModel.addRow(dataLine);
            }
            //if the random choice box is filled then it chooses
        }else{
            //takes a random entry from the filtered data and adds only that value
            String[] randomGame = filteredData.get(((int) (Math.random() * filteredData.size())));
            tableModel.addRow(randomGame);
        }

        //changes the table size to display data best and repaints it
        resizeTable();
        gameTable.repaint();
    }

    private void deleteSelectedGame(){
        //checks that there is a row selected and it won't cause an array out of bounds exception
        if (gameTable.getSelectedRow() > 0 && gameTable.getSelectedRow() < boardGames.size()){
            //if it's a valid selection pop up a dialog box to be sure that they want to delete the data and calls to delete if yes is selected
            if (JOptionPane.showConfirmDialog(null, ("Are you sure you want to delete: " + boardGames.get(gameTable.getSelectedRow()).getTitle() + "?"), "WARNING",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                //calls to delete and refreshes the table after the delete
                xmlHandler.deleteGame(boardGames.get(gameTable.getSelectedRow()));
                refreshGamesListData();
            }

        } else {
            JOptionPane.showMessageDialog(null, "Please select a game", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resizeColumnWidth(JTable table) {
        //creates a columnNames model to allow the data to be formatted well
        final TableColumnModel columnModel = table.getColumnModel();
        //creates a loop that for each columnNames checks what the cell renderer
        //will show up as the max preferred size from each row and set that to the columnNames preferred max
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 20; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column); //gets the cell rendered for the current columnNames and row
                Component comp = table.prepareRenderer(renderer, row, column); //creates a component to test the renderer
                width = Math.max(comp.getPreferredSize().width +1 , width); //uses the rendered to set width to the new max preferred size of the cell if it's larger than the current
            }
            if(width > 300) {
                width = 300; //sets the width to 300 if it's too large
            }
            columnModel.getColumn(column).setPreferredWidth(width); //sets the preferred width for the columnNames from the result of the previous loops
        }
    }

    public static void main(String[] args) {
        //creates a new instance of itself when the program is run
        new BoardGameList();
    }

}
