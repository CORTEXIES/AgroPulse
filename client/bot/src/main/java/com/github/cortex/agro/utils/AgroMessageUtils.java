package com.github.cortex.agro.utils;

import com.github.cortex.agro.dto.AgroMessage;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class AgroMessageUtils {

    @Value("${excel.cell.fallback_value}")
    private static String FALLBACK_VALUE;

    public static String extractFieldData(List<AgroMessage> agroMessages) {
        return agroMessages.stream()
                .map( msg -> String.format(
                    	"Sender name: %s | Telegram id: %s | Text:\n%s\nPhoto url: %s\n",
                    	msg.agronomist().fullName(),
                        msg.agronomist().telegramId(),
                        msg.report().orElseGet(() -> FALLBACK_VALUE),
                        msg.photoUrl().orElseGet(() -> FALLBACK_VALUE)
                )).collect(Collectors.joining("\n\n"));
    }
}