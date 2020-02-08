
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DatabaseHandler {
    //Created all major io classes as syncronized to keep thread safety
    //creates static values for shared items
    private static Statement stmt;
    private static Connection con;

    void initialize() {

        try { //creates the database components and uses a hardcoded string as it's only used locally so far but will need to be changed to allow it to be edited
            String connectionUrl = "jdbc:sqlserver://127.0.0.1:1433;" +
                    "databaseName=Java4Project;user=ProjectUser;password=P@55word;";
            con = DriverManager.getConnection(connectionUrl);
            stmt = con.createStatement();

        }catch (SQLException e){
            //prints out a message to say what went wrong as well as typing a detailed report to a file
            JOptionPane.showMessageDialog(null, "Database connection could not be established, please check your connection and try again", "Info", JOptionPane.INFORMATION_MESSAGE);
            ErrorReporter.log(e);
        }
    }

    synchronized BoardGame[] getData(){
        try { //pulls the database table and stores it to a result set
            String query = "SELECT * FROM Games";
            ResultSet gamesRS = stmt.executeQuery(query);

            ArrayList resultList = new ArrayList<BoardGame>();

            //loops through the result set and adds all the values for each result to an array
            while(gamesRS.next()){
                BoardGame game = new BoardGame (
                        gamesRS.getString("Title"),
                            gamesRS.getString("PlayersMin") + "-" + gamesRS.getString("PlayersMax"),
                            gamesRS.getString("Themes"),
                            gamesRS.getString("Mechanics"),
                            (gamesRS.getString("SetupTime")),
                            (gamesRS.getString("PlayTime")),
                            gamesRS.getString("Comments"),
                            gamesRS.getString("GameID")
                    );
                //adds the array to the array list results
                resultList.add(game);
            }
            BoardGame[] returnResult = new BoardGame[resultList.size()];
            resultList.toArray(returnResult);
            return returnResult;

        }catch(SQLException e){
            //catches exception and gives an error pront and writes a detailed error to a file and returns null
            ErrorReporter.log(e);
            JOptionPane.showMessageDialog(null, "Error in retrieving results from sql query", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    synchronized ArrayList<Map<String, String>> getSearchData(){

        try {//pulls all the data from the table
            String query = "SELECT * FROM Games";
            ResultSet gamesRS = stmt.executeQuery(query);
            //creates an arraylist of map values for ease of sorting to store the results in
            ArrayList<Map<String, String>> resultList = new ArrayList<>();

            //loops through all the results and adds the values to a local map variable then adds it to the results list
            while(gamesRS.next()){

                Map<String, String> map = new HashMap<>();

                map.put("title", gamesRS.getString("Title"));
                map.put("playersMin", gamesRS.getString("PlayersMin"));
                map.put("playersMax",  gamesRS.getString("PlayersMax"));
                map.put("themes", gamesRS.getString("Themes"));
                map.put("mechanics", gamesRS.getString("Mechanics"));
                map.put("setupTime", gamesRS.getString("SetupTime"));
                map.put("playTime", gamesRS.getString("PlayTime"));
                map.put("comments", gamesRS.getString("Comments"));
                map.put("gameID", gamesRS.getString("GameID"));

                resultList.add(map);
            }
            //after the loop completes returns the arraylist of maps
            return resultList;


        }catch(SQLException e){
            //shows a dialog box and logs a detailed error report before returning null
            ErrorReporter.log(e);
            JOptionPane.showMessageDialog(null, "Error in retrieving results from sql query", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    synchronized void updateData(BoardGame game){

        //creates a string builder to build the query
        StringBuilder queryBuilder = new StringBuilder();

        try{
            //makes sure the array isn't null and adds updates for each component
            if (game.getGameID() != null){
                queryBuilder.append("UPDATE Games SET ");
                queryBuilder.append("PlayersMin = '").append(game.getPlayersMin());
                queryBuilder.append("', PlayersMax = '").append(game.getPlayersMax());
                queryBuilder.append("', Themes = '").append(game.getThemes());
                queryBuilder.append("', Mechanics = '").append(game.getMechanics());
                queryBuilder.append("', SetupTime = '").append(game.getSetupTime());
                queryBuilder.append("', PlayTime = '").append(game.getApproxPlayTime());
                queryBuilder.append("', Comments = '").append(game.getComments());
                queryBuilder.append("', Title = '").append(game.getTitle());
                queryBuilder.append("' WHERE GameID = '").append(game.getGameID()).append("';");

                //switches the string builder to a string and run the resulting query
                String query = queryBuilder.toString();
                System.out.println(stmt.executeUpdate(query));

            }else
            {
                queryBuilder.append("INSERT INTO Games (PlayersMin, PlayersMax, Themes, Mechanics, SetupTime, PlayTime, Comments, Title) VALUES (");
                queryBuilder.append("'").append(game.getPlayersMin());
                queryBuilder.append("', '").append(game.getPlayersMax());
                queryBuilder.append("', '").append(game.getThemes());
                queryBuilder.append("', '").append(game.getMechanics());
                queryBuilder.append("', '").append(game.getSetupTime());
                queryBuilder.append("', '").append(game.getApproxPlayTime());
                queryBuilder.append("', '").append(game.getComments());
                queryBuilder.append("', '").append(game.getTitle());
                queryBuilder.append("');");

                //switches the string builder to a string and run the resulting query
                String query = queryBuilder.toString();
                System.out.println(stmt.executeUpdate(query));
            }
        }
        catch (SQLException e) {
            //logs the error
            ErrorReporter.log(e);
            //sets a dialog box giving the user info, if the players are higher than the sql data type limits request they change it, otherwise give a general error
            if ((Integer.parseInt(game.getPlayersMin()) < 0) || (Integer.parseInt(game.getPlayersMin()) > 255)
                    || (Integer.parseInt(game.getPlayersMax()) < 0) || (Integer.parseInt(game.getPlayersMax()) > 255)) {
                JOptionPane.showMessageDialog(null, "Error in Changing game data, please make sure your player values are between 0-255 and separated by a -", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error in Changing game data please try again.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    synchronized void deleteData(String gameIDToDelete){

        //creates a query to delete the game based on fed id
        String query = "DELETE FROM Games WHERE GameID = " + gameIDToDelete + ";";

        try {//uses the results of a dialog box to make sure they want to delete the data and runs the query if so
            if (stmt.execute(query)){
                JOptionPane.showMessageDialog(null, "Game deleted successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch (SQLException e){
            //logs detailed report and shows general error to user
            ErrorReporter.log(e);
            JOptionPane.showMessageDialog(null, "Error deleting Game from sql query", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    String purgeNumberInput(String origData){
        //creats a string builder and removes anything thats not a number from the input and returns the result
        StringBuilder outputData = new StringBuilder();
        for (int i = 0; i < origData.length(); i++){
            if (!(origData.charAt(i)<'0' || origData.charAt(i)>'9')){
                outputData.append(origData.charAt(i));
            }
        }
        return outputData.toString();
    }
}
