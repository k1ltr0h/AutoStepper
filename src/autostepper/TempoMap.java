package autostepper;

import java.util.ArrayList;
import java.util.Locale;

public class TempoMap {
    public static class Segment {
        public final float startBeat;
        public final float startTime;
        public final float bpm;

        public Segment(float startBeat, float startTime, float bpm) {
            this.startBeat = startBeat;
            this.startTime = startTime;
            this.bpm = bpm;
        }
    }

    private final ArrayList<Segment> segments;

    public TempoMap(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    public static TempoMap constant(float bpm) {
        ArrayList<Segment> segs = new ArrayList<>();
        segs.add(new Segment(0f, 0f, bpm));
        return new TempoMap(segs);
    }

    public float getBpmAtBeat(float beat) {
        Segment current = segments.get(0);
        for (int i = 1; i < segments.size(); i++) {
            Segment next = segments.get(i);
            if (beat < next.startBeat) break;
            current = next;
        }
        return current.bpm;
    }

    public float timeAtBeat(float beat) {
        Segment current = segments.get(0);
        for (int i = 1; i < segments.size(); i++) {
            Segment next = segments.get(i);
            if (beat < next.startBeat) break;
            current = next;
        }
        float beatDelta = beat - current.startBeat;
        return current.startTime + (beatDelta * 60f / current.bpm);
    }

    public String toBpmsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            Segment seg = segments.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format(Locale.US, "%.6f=%.6f", seg.startBeat, seg.bpm));
        }
        return sb.toString();
    }
}
