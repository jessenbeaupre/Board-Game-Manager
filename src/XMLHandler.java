import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.*;
import javax.xml.stream.events.*;

public class XMLHandler
{
    private String gamesListFile = "GamesList.xml";
    private int maxGameID = 0;
    private ArrayList<BoardGame> boardGameList = new ArrayList<BoardGame>();


    public ArrayList<BoardGame> getBoardGameList()
    {
        return boardGameList;
    }

    @SuppressWarnings({ "unchecked", "null" })
    public ArrayList<BoardGame> loadGames()
    {
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(gamesListFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            BoardGame boardGame = null;
            while (eventReader.hasNext())
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement())
                {
                    StartElement startElement = event.asStartElement();
                    // If we have an item element, we create a new item
                    String elementName = startElement.getName().getLocalPart();
                    switch (elementName) {
                        case "Game":
                            boardGame = new BoardGame();
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            while (attributes.hasNext())
                            {
                                Attribute attribute = attributes.next();
                                if (attribute.getName().toString().equals("Title"))
                                {
                                    boardGame.setTitle(attribute.getValue());
                                }
                            }
                            break;
                        case "Players":
                            event = eventReader.nextEvent();
                            boardGame.setPlayers(event.asCharacters().getData());
                            break;
                        case "Themes":
                            event = eventReader.nextEvent();
                            boardGame.setThemes(event.asCharacters().getData());
                            break;
                        case "Mechanics":
                            event = eventReader.nextEvent();
                            boardGame.setMechanics(event.asCharacters().getData());
                            break;
                        case "SetupTime":
                            event = eventReader.nextEvent();
                            boardGame.setSetupTime(event.asCharacters().getData());
                            break;
                        case "ApproxPlayTime":
                            event = eventReader.nextEvent();
                            boardGame.setApproxPlayTime(event.asCharacters().getData());
                            break;
                        case "Comments":
                            event = eventReader.nextEvent();
                            boardGame.setComments(event.asCharacters().getData());
                            break;
                        case "GameID":
                            event = eventReader.nextEvent();
                            boardGame.setGameID(event.asCharacters().getData());
                            int gameID = Integer.parseInt(event.asCharacters().getData());
                            if ( gameID > maxGameID)
                                {
                                    maxGameID = gameID;
                                }
                            break;
                    }
                }
                if (event.isEndElement())
                {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals("Game"))
                    {
                        boardGameList.add(boardGame);
                    }
                }
            }

            in.close();

        }
        catch (FileNotFoundException | XMLStreamException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return boardGameList;


    }

    public void saveGames(BoardGame boardGameToChange)
    {
        try
        {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(gamesListFile));


            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent newLine = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(newLine);

            // create list open tag
            StartElement gamesListStartElement = eventFactory.createStartElement("",
                    "", "GameList");
            eventWriter.add(gamesListStartElement);
            eventWriter.add(newLine);

            boardGameList.forEach(boardGame ->
            {
                try
                {
                    if (boardGame.getGameID().equals(boardGameToChange.getGameID()))
                    {
                        createGameEntry(eventWriter, boardGameToChange);
                    }
                    else
                    {
                        createGameEntry(eventWriter, boardGame);
                    }

                } catch (XMLStreamException e)
                {
                    e.printStackTrace();
                }
            });

            if (boardGameToChange.getGameID() == null)
            {
                boardGameList.add(boardGameToChange);
                boardGameToChange.setGameID(Integer.toString(++maxGameID));
                createGameEntry(eventWriter, boardGameToChange);
            }

            //writes end for the list
            eventWriter.add(eventFactory.createEndElement("", "", "GamesList"));
            eventWriter.add(newLine);
            eventWriter.add(eventFactory.createEndDocument());

            eventWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deleteGame(BoardGame boardGameToDelete)
    {
        try
        {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(gamesListFile));


            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent newLine = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(newLine);

            // create list open tag
            StartElement gamesListStartElement = eventFactory.createStartElement("",
                    "", "GameList");
            eventWriter.add(gamesListStartElement);
            eventWriter.add(newLine);

            for(int i = 0; i < boardGameList.size(); i++)
            {
                try
                {
                        if (!boardGameList.get(i).getGameID().equals(boardGameToDelete.getGameID()))
                        {
                            createGameEntry(eventWriter, boardGameList.get(i));
                        } else
                        {
                            boardGameList.remove(boardGameToDelete);
                        }
                    } catch (XMLStreamException e)
                {
                    e.printStackTrace();
                }

            }

            //writes end for the list
            eventWriter.add(eventFactory.createEndElement("", "", "GamesList"));
            eventWriter.add(newLine);
            eventWriter.add(eventFactory.createEndDocument());

            eventWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void createGameEntry(XMLEventWriter eventWriter, BoardGame boardGame) throws XMLStreamException
    {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent tab = eventFactory.createDTD("\t");
        XMLEvent newLine = eventFactory.createDTD("\n");

        StartElement gameStartElement = eventFactory.createStartElement("","", "Game");
        eventWriter.add(tab);
        eventWriter.add(gameStartElement);
        eventWriter.add(eventFactory.createAttribute("Title", boardGame.getTitle()));
        eventWriter.add(newLine);

        // Write the different values
        //createGameField(eventWriter, "Title", boardGame.getTitle());
        createGameField(eventWriter, "Players", boardGame.getPlayers());
        createGameField(eventWriter, "Themes", boardGame.getThemes());
        createGameField(eventWriter, "Mechanics", boardGame.getMechanics());
        createGameField(eventWriter, "SetupTime", boardGame.getSetupTime());
        createGameField(eventWriter, "ApproxPlayTime", boardGame.getApproxPlayTime());
        createGameField(eventWriter, "Comments", boardGame.getComments());
        createGameField(eventWriter, "GameID", boardGame.getGameID());

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createEndElement("", "", "Game"));
        eventWriter.add(newLine);

    }

    private void createGameField(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent newLine = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(newLine);

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
