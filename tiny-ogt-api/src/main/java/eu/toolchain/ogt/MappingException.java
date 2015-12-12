package eu.toolchain.ogt;

import lombok.Getter;

public class MappingException extends RuntimeException {
    private static final long serialVersionUID = 6991019600165017678L;

    @Getter
    private final Context path;
    @Getter
    private final String originalMessage;

    public MappingException(final Context path, final String message) {
        super(path.path() + ": " + message);
        this.path = path;
        this.originalMessage = message;
    }

    public MappingException(final Context path, final String message, final Throwable cause) {
        super(path.path() + ": " + message, cause);
        this.path = path;
        this.originalMessage = message;
    }
}
