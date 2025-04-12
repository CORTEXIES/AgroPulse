package com.github.cortex.service.message.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandExecutor {

    private final Map<String, Command> commandMap = new ConcurrentHashMap<>();

    @Autowired
    public CommandExecutor(List<Command> commands) {
        commands.forEach(c -> commandMap.put(c.get(), c));
    }

    public Optional<BotApiMethod<?>> execute(String command) {
        return Optional.ofNullable(commandMap.get(command))
                .map(Command::execute);
    }
}