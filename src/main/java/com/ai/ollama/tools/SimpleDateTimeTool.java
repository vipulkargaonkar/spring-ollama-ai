package com.ai.ollama.tools;

import org.slf4j.Logger;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class SimpleDateTimeTool {

    private final Logger logger = getLogger(getClass());

    @Tool(description = "Get the current date and time in users zone.")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder
                        .getTimeZone()
                        .toZoneId())
                .toString();
    }

    @Tool(description = "Set the alarm for given time.")
    void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {
        var dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        this.logger.info("Set the alarm for given time. {}", dateTime);
    }

}
