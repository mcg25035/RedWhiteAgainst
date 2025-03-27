package dev.mcloudtw.rwa.exception;

public class LocationFinderTriesOverMaxTimes extends Exception {
    public LocationFinderTriesOverMaxTimes(String message) {
        super(message);
    }
}
