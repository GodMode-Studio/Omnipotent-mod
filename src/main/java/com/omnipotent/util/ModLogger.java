package com.omnipotent.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
@NonNull
public final class ModLogger {

    private Logger log;
    @Getter
    private final boolean enabled = Boolean.getBoolean("omniDebug");

    public void error(String message, Exception ex) {
        if (isEnabled())
            log.error(message, ex);
    }
}
