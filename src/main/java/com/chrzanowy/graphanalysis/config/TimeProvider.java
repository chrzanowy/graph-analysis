package com.chrzanowy.graphanalysis.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeProvider {

    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

}
