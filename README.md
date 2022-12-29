# java-smpte-timecode
A minimalist java library for dealing with SMPTE timecodes. Supports all major broadcast timecodes. 

Timecode to frames conversions are tested exhaustively against these other implementations:
- https://pypi.org/project/timecode/


![Coverage](.github/badges/jacoco.svg)

# Installation
Get from Maven Central:
```
<dependency>
    <groupId>org.moormanity</groupId>
    <artifactId>java-smpte-timecode</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Usage

Create an immutable [TimecodeRecord](https://github.com/moormanm/java-smpte-timecode/blob/master/src/main/java/org/moormanity/smpte/timecode/TimecodeRecord.java) from a string and framerate:

```
TimecodeRecord timecode = TimecodeOperations.fromTimecodeString("01:00:00;22", FrameRate._29_97_drop));
```

All supported framerates are in the [FrameRate.java](https://github.com/moormanm/java-smpte-timecode/blob/master/src/main/java/org/moormanity/smpte/timecode/FrameRate.java) enum.

Create an immutable TimecodeRecord from an elapsed frame count and framerate:
```
TimecodeOperations.fromElapsedFrames(10044,  FrameRate._25);
```

Subtract or add timecodes to create new ones:
```
TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:00:00;00", FrameRate._29_97_drop);
TimecodeRecord aMinusB = TimecodeOperations.subtract(a,b);
```

```        
TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:00:00;00", FrameRate._29_97_drop);
TimecodeRecord aPlusB = TimecodeOperations.add(a,b);
```

Get the total elapsed frames of a TimecodeRecord:
```
TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
int totalElapsedFrames = TimecodeOperations.toElapsedFrameCount(a);
```

Format a TimecodeRecord as a string:
```
TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
String timecodeString = TimecodeOperations.toTimecodeString(a);
```


## Supported Timecode Frame Rates

| Film / ATSC / HD | PAL / SECAM / DVB / ATSC | NTSC / ATSC / PAL-M | NTSC Non-Standard | ATSC |
| ---------------- | ------------------------ | ------------------- | ----------------- | ---- |
| 23.976           | 25                       | 29.97               | 30 DF             | 30   |
| 24               | 50                       | 29.97 DF            | 60 DF             | 60   |
| 24.98            | 100                      | 59.94               | 120 DF            | 120  |
| 47.952           |                          | 59.94 DF            |                   |      |
| 48               |                          | 119.88              |                   |      |
|                  |                          | 119.88 DF           |                   |      |



