package com.wirelabs.common.utils;

public final class LengthControl {

    private LengthControl() {
        throw new IllegalStateException("Utility class LengthControl");
    }

    public static final int DESC_MIN_LENGTH = 3;
    public static final int DESC_MAX_LENGTH = 80;
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 55;
    public static final String DEFAULT_MESSAGE = " lenght must between " + DESC_MIN_LENGTH + " until ";

    public static final String NAME_DEFAULT_MESSAGE = " name " + DEFAULT_MESSAGE + NAME_MAX_LENGTH;
    public static final String DESC_DEFAULT_MESSAGE = " description " + DEFAULT_MESSAGE + DESC_MAX_LENGTH;
    public static final int FULL_DESC_MAX_LENGTH = 255;

}
