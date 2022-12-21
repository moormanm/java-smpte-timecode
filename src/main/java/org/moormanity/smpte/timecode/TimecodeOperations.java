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

    public static TimecodeRecord fromElapsedFrames(int elapsedFrames, @NonNull FrameRate frameRate) {
        if (elapsedFrames < 0) {
            throw new IllegalArgumentException("frameNumber must be positive: " + elapsedFrames);
        }
        int adjustedElapsedFrames;
        // adjust for dropFrame

        // modify input elapsed frame count in the case of a drop rate
        double framesPer10Minutes = frameRate.frameRateForElapsedFramesCalculation() * 600;
        int d = (int) (elapsedFrames / framesPer10Minutes);
        int m = (int) (elapsedFrames % framesPer10Minutes);
        // don't allow negative numbers
        int f = Math.max(0, m - frameRate.getNumberOfFramesToDropInOneMinute());

        int part1 = 9 * frameRate.getNumberOfFramesToDropInOneMinute() * d;
        int part2 = (frameRate.getNumberOfFramesToDropInOneMinute() *
                (int)(f / ((framesPer10Minutes - frameRate.getNumberOfFramesToDropInOneMinute()) / 10)));
        adjustedElapsedFrames = elapsedFrames + part1 + part2;


        int logicalFps = frameRate.getNumberOfElapsedFramesThatCompriseOneSecond();
        int frames = adjustedElapsedFrames % logicalFps;
        int seconds = (adjustedElapsedFrames / logicalFps) % 60;
        int minutes = (adjustedElapsedFrames / (logicalFps * 60)) % 60;
        int hours = adjustedElapsedFrames / (logicalFps * 3600) % 24;
        return new TimecodeRecord(hours, minutes, seconds, frames, frameRate);
    }

    public static int toElapsedFrameCount(@NonNull TimecodeRecord a) {

        int totalMinutes = (60 * a.getHours()) + a.getMinutes();
        int frameRateForElapsedFramesCalculation = a.getFrameRate().getNumberOfElapsedFramesThatCompriseOneSecond();
        int base = (frameRateForElapsedFramesCalculation * 60 * 60 * a.getHours())
                        + (frameRateForElapsedFramesCalculation * 60 * a.getMinutes())
                        + (frameRateForElapsedFramesCalculation * a.getSeconds())
                        + a.getFrames();
        int dropOffset = a.getFrameRate().getNumberOfFramesToDropInOneMinute() * (totalMinutes - (totalMinutes / 10));
        return base - dropOffset;
    }

    public static TimecodeRecord add(@NonNull TimecodeRecord a, @NonNull TimecodeRecord b) {
        if (a.getFrameRate() != b.getFrameRate()) {
            throw new IllegalArgumentException("frame rates must match");
        }
        return fromElapsedFrames(toElapsedFrameCount(a) + toElapsedFrameCount(b), a.getFrameRate());
    }

    public static TimecodeRecord subtract(@NonNull TimecodeRecord a, @NonNull TimecodeRecord b) {
        if (a.getFrameRate() != b.getFrameRate()) {
            throw new IllegalArgumentException("frame rates must match");
        }
        return fromElapsedFrames(toElapsedFrameCount(a) - toElapsedFrameCount(b), a.getFrameRate());
    }


}
