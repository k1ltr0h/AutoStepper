/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autostepper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Phr00t
 */
public class SMGenerator {
 
    private static String Header = 
            "#TITLE:$TITLE;\n" +
            "#SUBTITLE:;\n" +
            "#ARTIST:$ARTIST;\n" +
            "#TITLETRANSLIT:;\n" +
            "#SUBTITLETRANSLIT:;\n" +
            "#ARTISTTRANSLIT:;\n" +
            "#GENRE:$GENRE;\n" +
            "#CREDIT:$CREDIT;\n" +
            "#BANNER:$BGIMAGE;\n" +
            "#BACKGROUND:$BGIMAGE;\n" +
            "#LYRICSPATH:;\n" +
            "#CDTITLE:;\n" +
            "#MUSIC:$MUSICFILE;\n" +
            "#OFFSET:$STARTTIME;\n" +
            "#SAMPLESTART:30.0;\n" +
            "#SAMPLELENGTH:30.0;\n" +
            "#SELECTABLE:YES;\n" +
            "#BPMS:$BPMS;\n" +
            "#STOPS:;\n" +
            "#KEYSOUNDS:;\n" +
            "#ATTACKS:;";
    
    public static final String Challenge = "Challenge";
    public static final String Hard = "Hard";
    public static final String Medium = "Medium";
    public static final String Easy = "Easy";
    public static final String Beginner = "Beginner";
    
    private static String NoteFramework =
            "//---------------dance-single - ----------------\n" +
            "#NOTES:\n" +
            "     dance-single:\n" +
            "     :\n" +
            "     $DIFFICULTY\n" +
            "     $RADAR:\n" +
            "$NOTES\n" +
            ";\n\n";

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) { }
            }
            if (os != null) {
                try { os.close(); } catch (IOException e) { }
            }
        }
    }    
    
    public static void AddNotes(BufferedWriter smfile, String difficultyName, StepGenerator.NoteData noteData) {
        try {
            if (smfile == null) return;
            String difficulty = buildDifficulty(difficultyName, noteData.meter);
            smfile.write(NoteFramework.replace("$DIFFICULTY", difficulty)
                                      .replace("$RADAR", noteData.getRadarString())
                                      .replace("$NOTES", noteData.notes));
        } catch(Exception e) {
            System.err.println("Failed to write notes: " + e.getMessage());
        }
    }
    
    private static String buildDifficulty(String name, int meter) {
        return name + ":\n" +
               "     " + meter + ":";
    }
    
    public static void Complete(BufferedWriter smfile) {
        try {
            if (smfile == null) return;
            smfile.close();
        } catch(Exception e) {
            System.err.println("Failed to close SM file: " + e.getMessage());
        }
    }

    public static File getSMFile(File songFile, String outputdir) {
        String filename = songFile.getName();
        File dir = new File(outputdir, filename + "_dir/");
        return new File(dir, filename + ".sm");
    }
    
    private static String sanitizeTag(String value) {
        if (value == null) return "";
        return value.replace(";", " ").replace("\n", " ").replace("\r", " ").trim();
    }
    
    public static BufferedWriter GenerateSM(TempoMap tempoMap, float startTime, File songfile, String outputdir, SongMeta meta) {
        String filename = songfile.getName();
        String songname = filename.replace(".mp3", " ").replace(".wav", " ").replace(".com", " ").replace(".org", " ").replace(".info", " ");
        String title = sanitizeTag(meta != null ? meta.title : "");
        if (title.isEmpty()) title = songname;
        String shortName = title.length() > 60 ? title.substring(0, 60) : title;
        String artist = sanitizeTag(meta != null ? meta.artist : "");
        if (artist.isEmpty()) artist = "Unknown Artist";
        String genre = sanitizeTag(meta != null ? meta.genre : "");
        String credit = sanitizeTag(meta != null ? meta.credit : "");
        if (credit.isEmpty()) credit = "AutoStepper by phr00t.com";
        File dir = new File(outputdir, filename + "_dir/");
        dir.mkdirs();
        File smfile = new File(dir, filename + ".sm");
        // get image for sm
        File imgFile = new File(dir, filename + "_img.png");
        String imgFileName = "";
        if( imgFile.exists() == false ) {
            System.out.println("Attempting to get image for background & banner...");            
            GoogleImageSearch.FindAndSaveImage(songname.replace("(", " ").replace(")", " ").replace("www.", " ").replace("_", " ").replace("-", " ").replace("&", " ").replace("[", " ").replace("]", " "), imgFile.getAbsolutePath());
        }
        if( imgFile.exists() ) {
            System.out.println("Got an image file!");
            imgFileName = imgFile.getName();
        } else System.out.println("No image file to use :(");
        try {
            smfile.delete();
            copyFileUsingStream(songfile, new File(dir, filename));
            BufferedWriter writer = new BufferedWriter(new FileWriter(smfile));
            writer.write(Header.replace("$TITLE", shortName)
                         .replace("$ARTIST", artist)
                         .replace("$GENRE", genre)
                         .replace("$CREDIT", credit)
                         .replace("$BGIMAGE", imgFileName)
                         .replace("$MUSICFILE", filename)
                         .replace("$STARTTIME", Float.toString(startTime + AutoStepper.STARTSYNC))
                         .replace("$BPMS", tempoMap.toBpmsString()));
            return writer;
        } catch(Exception e) {
            System.err.println("Failed to generate SM file for: " + songfile.getAbsolutePath() + " (" + e.getMessage() + ")");
        }
        return null;
    }
}
