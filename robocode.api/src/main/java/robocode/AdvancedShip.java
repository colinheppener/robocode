package robocode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public class AdvancedShip extends Ship{

    /**
     * Method to make saving data to a file easier
     * @param fileName The name of the file the data will be saved to
     * @param infoToWrite The data to be saved
     * @param showDate Whether or not the current date and time should be saved with the data
     * @param showPreviousRecord Whether or not you want to data that is currently present in the file to be logged in
     *                           ship's dialog.
     */
    public void writeFile(String fileName, Object infoToWrite, boolean showDate, boolean showPreviousRecord) {
        String info;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(getDataFile(fileName)));

                info = reader.readLine();

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            // Something went wrong reading the file, reset to 0.
            info = "unknown";
        } catch (NumberFormatException e) {
            // Something went wrong  reset to 0
            info = "unknown";
        }
        if(showPreviousRecord)
        out.println("Last saved data to: '"+fileName+".dat' was: "+info);

        info = String.valueOf(infoToWrite);
        //out.println("New Entry: "+info+" to: "+fileName);
        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile(fileName)));

            if(showDate)
                w.println(info+" on: "+dateFormat.format(date));
            if(!showDate)
                w.println(info);

            // PrintStreams don't throw IOExceptions during prints, they simply set a flag.... so check it here.
            if (w.checkError()) {
                out.println("I could not write the data!");
            }
        } catch (IOException e) {
            out.println("IOException trying to write: ");
            e.printStackTrace(out);
        } finally {
            if (w != null) {
                w.close();
            }
        }
    }


    /**
     * Method to make retrieving data from a file easier
     * @param fileName The file to retrieve the data from
    * @return The data present in the file
     */
    public String readFile(String fileName){
        try {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(getDataFile(fileName)));

                return reader.readLine();

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            // Something went wrong reading the file, reset to 0.
            return "IOException: Unknown";
        } catch (NumberFormatException e) {
            // Something went wrong  reset to 0
            return "NumberFormatException: Unknown";
        }
    }

    public String getName(){
        return peer.getName();
    }


}
