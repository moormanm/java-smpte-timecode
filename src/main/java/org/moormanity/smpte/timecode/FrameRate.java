package org.moormanity.smpte.timecode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum FrameRate {

    /**
     * 23.976 fps (aka 23.98)
     * Also known as 24p for HD video, sometimes rounded up to 23.98 fps. started out as the format for dealing with 24fps film in a NTSC post environment.
     */
    _23_976("23.976 fps", 24000, 1001, 24, false, 0),

    /**
     * 24 fps
     * (film, ATSC, 2k, 4k, 6k)
     */
    _24("24 fps", 24, 1, 24, false, 0),


    /**
     * 24.98 fps
     * This frame rate is commonly used to facilitate transfers between PAL and NTSC video and film sources. It is mostly used to compensate for some error.
     */
    _24_98("24.98 fps", 25000, 1001, 25, false, 0),

    /**
     * 25 fps
     * (PAL, used in Europe, Uruguay, Argentina, Australia), SECAM, DVB, ATSC)
     */
    _25("25 fps", 25, 1, 25, false, 0),

    /**
     * 29.97 fps (30p)
     * (NTSC American System (US, Canada, Mexico, Colombia, etc.), ATSC, PAL-M (Brazil))
     * (30 / 1.001) frame/sec
     */
    _29_97("29.97 fps", 30000, 1001, 30, false, 0),
    /**
     * 29.97 drop fps
     */
    _29_97_drop("29.97 fps drop", 30000, 1001, 30, true, 2),

    /**
     * 30 fps
     * (ATSC) This is the frame count of NTSC broadcast video. However, the actual frame rate or speed of the video format runs at 29.97 fps.
     * This timecode clock does not run in realtime. It is slightly slower by 0.1%.
     * ie: 1:00:00:00:00 (1 day/24 hours) at 30 fps is approx 1:00:00:00;02 in 29.97dfA
     */
    _30("30", 30, 1, 30, false, 0),

    /**
     * 30 drop fps
     */
    _30_drop("30 drop", 3, 1, 30, true, 2),

    /**
     * 47.952 (48p?)
     * Double 23.976 fps
     */
    _47_952("47.952 fps", 48000, 1001, 48, false, 0),

    /**
     * 48 fps
     * Double 24 fps
     */
    _48("48 fps", 48, 1, 48, false, 0),

    /**
     * 50 fps
     * Double 25 fps\
     */
    _50("50 fps", 50, 1, 50, false, 0),

    /**
     * 59.94 fps
     * Double 29.97 fps
     * This video frame rate is supported by high definition cameras and is compatible with NTSC (29.97 fps).
     */
    _59_94("59.94 fps", 60000, 1001, 60, false, 0),

    /**
     * 59.94 fps drop
     */
    _59_94_drop("59.94 fps drop", 60000, 1001, 60, true, 4),

    /**
     * 60 fps
     * Double 30 fps
     * This video frame rate is supported by many high definition cameras. However, the NTSC compatible 59.94 fps frame rate is much more common.
     */
    _60("60 fps", 60, 1, 60, false, 0),


    /**
     * 60 drop fps
     * Double 30 fps
     * See the description for 30 drop for more info.
     * - Warning: This is not a video frame rate - it is a display rate only.
     */
    _60_drop("60 fps drop", 60, 1, 60, true, 4),

    /**
     * 100 fps
     * Double 50 fps / quadruple 25 fps
     */
    _100("100 fps", 100, 1, 100, false, 0),

    /**
     * 119.88 fps
     * Double 59.94 fps / quadruple 29.97 fps
     */
    _119_88("119.88 fps", 120_000, 1001, 120, false, 0),

    /**
     * 119.88 drop fps
     * Double 59.94 drop fps / quadruple 29.97 drop fps
     */
    _119_88_drop("119.88 fps", 120_000, 1001, 120, true, 4),

    /**
     * 120 fps
     * Double 60 fps / quadruple 30 fps
     */
    _120("120 fps", 120, 1, 120, false, 0),

    /**
     * 120 drop fps
     * <p>
     * Double 60 fps drop / quadruple 30 fps drop
     * See the description for 30 drop for more info.
     * - Warning: This is not a video frame rate - it is a display rate only.
     */
    _120_drop("120 fps", 120, 1, 120, true, 8);

    @NonNull
    private final String name;
    private final int numeratorForRealTimeFrameRate;
    private final int denominatorForRealTimeFrameRate;

    private final int numberOfElapsedFramesThatCompriseOneSecond;

    private final boolean isDropFrameMode;
    private final int numberOfFramesToDropInOneMinute;

    public double frameRateForElapsedFramesCalculation() {
        switch (this) {
            case _29_97_drop:
                return 29.97;
            case _59_94_drop:
                return 59.94;
            case _60_drop:
                return 59.94;
            case _119_88_drop:
                return 119.88;
            case  _120_drop:
                return  119.88;
            default:
                return getNumberOfElapsedFramesThatCompriseOneSecond();
        }
    }
}
