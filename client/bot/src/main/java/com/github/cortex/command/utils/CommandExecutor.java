package com.github.cortex.command.utils;

import com.github.cortex.command.Command;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandExecutor {

    private final Map<String, Command> targetToCommand = new ConcurrentHashMap<>();

    @Autowired
    public CommandExecutor(List<Command> commands) {
        commands.forEach(c -> targetToCommand.put(c.get(), c));
    }

    public Optional<PartialBotApiMethod<?>> execute(Message msg) {
        return Optional.ofNullable(targetToCommand.get(msg.getText()))
                .map(command -> command.execute(msg));
    }
}