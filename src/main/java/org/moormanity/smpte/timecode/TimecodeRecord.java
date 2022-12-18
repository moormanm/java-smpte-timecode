package org.moormanity.smpte.timecode;


import lombok.Value;

@Value
public class TimecodeRecord {
    int hours;
    int minutes;
    int seconds;
    int frames;
    FrameRate frameRate;
}
