package com.github.cortex.agro;

import java.util.List;
import java.util.stream.Collectors;

public class AgroMessageUtils {

    public static String extractFieldData(List<AgroMessage> agroMessages) {
        return agroMessages.stream()
                .map( msg -> String.format(
                    	"[ Sender name: %s | Telegram id: %s | Text:\n\n%s ]",
                    	msg.getSenderName(), msg.getTelegramId(), msg.getText()
                )).collect(Collectors.joining("\n\n"));
    }
}