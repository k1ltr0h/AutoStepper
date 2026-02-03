package autostepper;

import ddf.minim.AudioMetaData;
import java.io.File;

public class SongMeta {
    public final String title;
    public final String artist;
    public final String genre;
    public final String credit;
    
    public SongMeta(String title, String artist, String genre, String credit) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.credit = credit;
    }
    
    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot <= 0) return name;
        return name.substring(0, dot);
    }
    
    private static String[] parseArtistTitleFromName(String name) {
        String raw = stripExtension(name).replace("_", " ").trim();
        String[] result = new String[] { "", raw };
        String[] split = raw.split(" - ", 2);
        if (split.length == 2) {
            result[0] = split[0].trim();
            result[1] = split[1].trim();
        } else {
            String clean = raw.replace("-", " ").trim();
            result[1] = clean;
        }
        return result;
    }
    
    public static SongMeta from(File file, AudioMetaData meta) {
        String title = "";
        String artist = "";
        String genre = "";
        if (meta != null) {
            title = meta.title();
            artist = meta.author();
            genre = meta.genre();
        }
        if (title == null) title = "";
        if (artist == null) artist = "";
        if (genre == null) genre = "";
        if (title.trim().isEmpty() || artist.trim().isEmpty()) {
            String[] parsed = parseArtistTitleFromName(file.getName());
            if (title.trim().isEmpty()) title = parsed[1];
            if (artist.trim().isEmpty()) artist = parsed[0];
        }
        if (title.trim().isEmpty()) title = stripExtension(file.getName());
        String credit = "AutoStepper by phr00t.com";
        return new SongMeta(title.trim(), artist.trim(), genre.trim(), credit);
    }
}
