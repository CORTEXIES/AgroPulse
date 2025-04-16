package com.github.cortex.agro.utils;

import com.github.cortex.agro.dto.AgroMessage;

import java.util.List;
import java.util.stream.Collectors;

public class AgroMessageUtils {

    public static String extractFieldData(List<AgroMessage> agroMessages) {
        return agroMessages.stream()
                .map( msg -> String.format(
                    	"Sender name: %s | Telegram id: %s \n -----> Text: %s\n",
                    	msg.agronomist().fullName(), msg.agronomist().telegramId(), msg.report()
                )).collect(Collectors.joining("\n\n"));
    }
}