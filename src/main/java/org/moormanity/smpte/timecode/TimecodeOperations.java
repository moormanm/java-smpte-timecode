package org.moormanity.smpte.timecode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimecodeOperations {

    private static final Pattern TIMECODE_STRING_PATTERN = Pattern.compile("([0-9][0-9]):([0-5][0-9]):([0-5][0-9])[:;]([0-9][0-9])");

    public static TimecodeRecord fromTimecodeString(@NonNull String timecodeString, @NonNull FrameRate frameRate) {
        Matcher matcher = TIMECODE_STRING_PATTERN.matcher(timecodeString);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Could not parse timecode string: " + timecodeString);
        }

        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        int seconds = Integer.parseInt(matcher.group(3));
        int frames = Integer.parseInt(matcher.group(4));

        if (frames >= frameRate.getNumberOfElapsedFramesThatCompriseOneSecond()) {
            throw new IllegalArgumentException("Timecode string is not suitable for given framerate. frame " + frames + " is out of range of " + frameRate.getName());
        }
        return new TimecodeRecord(hours, minutes, seconds, frames, frameRate);

    }

    public static String toTimecodeString(@NonNull TimecodeRecord a) {
        String template = a.getFrameRate().getNumberOfElapsedFramesThatCompriseOneSecond() > 100
                ? "%02d:%02d:%02d%s%03d"
                : "%02d:%02d:%02d%s%02d";

        return String.format(template,
                a.getHours(),
                a.getMinutes(),
                a.getSeconds(),
                a.getFrameRate().isDropFrameMode() ? ";" : ":",
                a.getFrames());
    }

    public static TimecodeRecord fromElapsedFrames(int frameNumber, @NonNull FrameRate frameRate) {
        if(frameNumber < 0) {
            throw new IllegalArgumentException("frameNumber must be positive: " + frameNumber);
        }
        int inElapsedFrames;
        // adjust for dropFrame
        if (frameRate.isDropFrameMode()) {
            // modify input elapsed frame count in the case of a drop rate
            int framesPer10Minutes = frameRate.getNumberOfElapsedFramesThatCompriseOneSecond() * 600;
            int d = frameNumber / framesPer10Minutes;
            int m = frameNumber % framesPer10Minutes;
            // don't allow negative numbers
            int f = Math.max(0, m - frameRate.getNumberOfFramesToDropInOneMinute());

            int part1 = 9 * frameRate.getNumberOfFramesToDropInOneMinute() * d;
            int part2 = frameRate.getNumberOfFramesToDropInOneMinute() *
                    (f / ((framesPer10Minutes - frameRate.getNumberOfFramesToDropInOneMinute()) / 10));
            inElapsedFrames = frameNumber + part1 + part2;
        } else {
            inElapsedFrames = frameNumber;
        }

        int logicalFps = frameRate.getNumberOfElapsedFramesThatCompriseOneSecond();
        int frames = inElapsedFrames % logicalFps;
        int seconds = (inElapsedFrames / logicalFps) % 60;
        int minutes = (inElapsedFrames / (logicalFps * 60)) % 60;
        int hours = inElapsedFrames / (logicalFps * 3600) % 24;
        return new TimecodeRecord(hours, minutes, seconds, frames, frameRate);
    }

    public static int toElapsedFrameCount(@NonNull TimecodeRecord a) {
        int totalMinutes = (60 * a.getHours()) + a.getMinutes();
        int logicalFps = a.getFrameRate().getNumberOfElapsedFramesThatCompriseOneSecond();
        int base = (logicalFps * 60 * 60 * a.getHours())
                + (logicalFps * 60 * a.getMinutes())
                + (logicalFps * a.getSeconds())
                + a.getFrames();
        int dropOffset = a.getFrameRate().getNumberOfFramesToDropInOneMinute() * (totalMinutes - (totalMinutes / 10));
        return base - dropOffset;
    }

    public static TimecodeRecord add(@NonNull TimecodeRecord a, @NonNull TimecodeRecord b) {
       if(a.getFrameRate() != b.getFrameRate()) {
           throw new IllegalArgumentException("frame rates must match");
       }
       return fromElapsedFrames(toElapsedFrameCount(a) + toElapsedFrameCount(b), a.getFrameRate());
    }

    public static TimecodeRecord subtract(@NonNull TimecodeRecord a, @NonNull TimecodeRecord b) {
        if(a.getFrameRate() != b.getFrameRate()) {
            throw new IllegalArgumentException("frame rates must match");
        }
        return fromElapsedFrames(toElapsedFrameCount(a) - toElapsedFrameCount(b), a.getFrameRate());
    }


}
