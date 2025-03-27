package dev.mcloudtw.rwa.exception;

public class AlreadyInTeamException extends RuntimeException {
    public AlreadyInTeamException(String message) {
        super(message);
    }
}
