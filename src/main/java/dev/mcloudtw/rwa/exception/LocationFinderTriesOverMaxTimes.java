package dev.mcloudtw.rwa.exception;

public class LocationFinderTriesOverMaxTimes extends RuntimeException {
    public LocationFinderTriesOverMaxTimes(String message) {
        super(message);
    }
}
