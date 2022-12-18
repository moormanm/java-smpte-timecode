package org.moormanity.smpte.timecode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimecodeOperationsTest {

    @Test
    public void verifyFramesVsTimecode() {
        verifyElapsedFramesVsTimecodeString("00:10:00;00", FrameRate._29_97_drop, 17982);
        verifyElapsedFramesVsTimecodeString("00:10:00;00", FrameRate._59_94_drop, 17982 * 2);
        verifyElapsedFramesVsTimecodeString("10:00:00;00", FrameRate._29_97_drop, 1078920);
        verifyElapsedFramesVsTimecodeString("10:00:00;00", FrameRate._59_94_drop, 1078920 * 2);
        verifyElapsedFramesVsTimecodeString("00:01:59;29", FrameRate._29_97_drop, 3597);
        verifyElapsedFramesVsTimecodeString("00:01:59;59", FrameRate._59_94_drop, 3597 * 2 + 1);

        verifyElapsedFramesVsTimecodeString("00:10:00:00", FrameRate._25, 15000);
        verifyElapsedFramesVsTimecodeString("10:00:00:00", FrameRate._25, 900000);
        verifyElapsedFramesVsTimecodeString("00:02:00:00", FrameRate._25, 3000);
        verifyElapsedFramesVsTimecodeString("00:01:59:24", FrameRate._25, 2999);
        verifyElapsedFramesVsTimecodeString("00:01:59:24", FrameRate._25, 2999);
    }

    @Test
    public void testAdd() {
        TimecodeRecord a =  TimecodeOperations.fromTimecodeString("01:23:45;06", FrameRate._29_97_drop);
        TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:23:13;01", FrameRate._29_97_drop);
        TimecodeRecord expected = TimecodeOperations.fromTimecodeString("02:46:58;07", FrameRate._29_97_drop);
        assertEquals(
                expected,
                TimecodeOperations.add(a,b)
        );
    }

    @Test
    public void testSubtract() {
        TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
        TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:00:00;00", FrameRate._29_97_drop);
        TimecodeRecord expected = TimecodeOperations.fromTimecodeString("22:29:59;28", FrameRate._29_97_drop);
        assertEquals(
                expected,
                TimecodeOperations.subtract(a,b)
        );
    }

    @Test
    public void addOperandsMustUseSameFrameRate() {
        TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
        TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:00:00;00", FrameRate._59_94_drop);
        assertThrows(IllegalArgumentException.class, ()-> TimecodeOperations.add(a,b));
    }
    @Test
    public void subtractOperandsMustUseSameFrameRate() {
        TimecodeRecord a =  TimecodeOperations.fromTimecodeString("23:30:00;00", FrameRate._29_97_drop);
        TimecodeRecord b =  TimecodeOperations.fromTimecodeString("01:00:00;00", FrameRate._59_94_drop);
        assertThrows(IllegalArgumentException.class, ()-> TimecodeOperations.subtract(a,b));
    }
    @Test
    public void fromElapsedFramesMustHaveAPositiveFrameNumber() {
        assertThrows(IllegalArgumentException.class, ()-> TimecodeOperations.fromElapsedFrames(-1, FrameRate._29_97_drop));
    }

    @Test
    public void timecodeStringMustBeValid() {
        assertThrows(IllegalArgumentException.class, ()-> TimecodeOperations.fromTimecodeString("mike", FrameRate._29_97_drop));
    }

    @Test
    public void timecodeStringFrameMustBeValid() {
        assertThrows(IllegalArgumentException.class, ()-> TimecodeOperations.fromTimecodeString("01:00:00;99", FrameRate._29_97_drop));
    }

    private void verifyElapsedFramesVsTimecodeString(String timecodeString, FrameRate frameRate, int totalElapsedFrames) {
        assertEquals(
                totalElapsedFrames,
                TimecodeOperations.toElapsedFrameCount(
                        TimecodeOperations.fromTimecodeString(timecodeString, frameRate)
                )
        );

        assertEquals(
                timecodeString,
                TimecodeOperations.toTimecodeString(
                        TimecodeOperations.fromElapsedFrames(totalElapsedFrames, frameRate)
                ));
    }
}