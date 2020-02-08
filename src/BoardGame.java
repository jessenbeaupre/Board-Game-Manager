import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class BoardGame {

    private String title;
    private String players;
    private String themes;
    private String mechanics;
    private String imageRef;
    private String setupTime;
    private String approxPlayTime;
    private String comments;
    private String gameID;

    String getTitle()
    {
        return title;
    }

    String getPlayers()
    {
        return players;
    }

    String getPlayersMin(){String[] playersMinMax = players.split("-", 2);
        return playersMinMax[0];}

    String getPlayersMax(){String[] playersMinMax = players.split("-", 2);
        return playersMinMax[1];}

    String getThemes()
    {
        return themes;
    }

    String getMechanics()
    {
        return mechanics;
    }

    String getImageRef()
    {
        return imageRef;
    }

    String getSetupTime()
    {
        return setupTime;
    }

    String getApproxPlayTime()
    {
        return approxPlayTime;
    }

    String getComments()
    {
        return comments;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setPlayers(String players)
    {
        this.players = players;
    }

    public void setThemes(String themes)
    {
        this.themes = themes;
    }

    public void setMechanics(String mechanics)
    {
        this.mechanics = mechanics;
    }

    public void setImageRef(String imageRef)
    {
        this.imageRef = imageRef;
    }

    public void setSetupTime(String setupTime)
    {
        this.setupTime = setupTime;
    }

    public void setApproxPlayTime(String approxPlayTime)
    {
        this.approxPlayTime = approxPlayTime;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public void setGameID(String gameID)
    {
        this.gameID = gameID;
    }

    String getGameID()
    {
        return gameID;
    }

    BoardGame(String title, String players, String themes, String mechanics, String setupTime, String approxPlayTime, String comments, String gameID){
        this.title = title;
        this.players = players;
        this.themes = themes;
        this.mechanics = mechanics;
        this.setupTime = setupTime;
        this.approxPlayTime = approxPlayTime;
        this.comments = comments;
        this.gameID = gameID;
        this.imageRef = loadImage();
    }

    BoardGame(){
        comments = "";
        imageRef = "";
    }

    public String loadImage(){
        //creats a file path for the image file
        Path imageFilePath = Paths.get("images.txt");

        try{//tries to search through the file line by line and if it finds the title returns the image path otherwise returns an empty string
            Scanner imageScanner = new Scanner(imageFilePath.toFile());
            imageScanner.useDelimiter("\n");
            while (imageScanner.hasNext()){
                String[] keyValue = imageScanner.next().split("-", 2);
                if (keyValue[0].equals(title)){
                    return keyValue[1];
                }
            }
        }catch (FileNotFoundException e){
            ErrorReporter.log(e);
        }

        return "";
    }

}
