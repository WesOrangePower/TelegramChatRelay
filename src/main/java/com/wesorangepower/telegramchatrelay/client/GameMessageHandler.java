package com.wesorangepower.telegramchatrelay.client;

import com.wesorangepower.telegramchatrelay.ConfigurationManager;
import com.wesorangepower.telegramchatrelay.telegram.LocalTelegramBot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

import java.util.Locale;

public class GameMessageHandler
{
    public static final String MESSAGE_SEPARATOR = " ➠ Я ] ";

    public static ActionResult onMessage(String rawMessage)
    {
        var message = rawMessage.replaceAll("§\\S", "");

//        if (message.contains("[BCFind]")) return ActionResult.SUCCESS;

        var lowercaseUsername = MinecraftClient.getInstance()
                .getSession()
                .getUsername()
                .toLowerCase(Locale.ROOT);

        var bot = LocalTelegramBot.getInstance();
        if (message.contains(MESSAGE_SEPARATOR))
        {
            var splitMessage = message.split(MESSAGE_SEPARATOR);

            var messageSender = splitMessage[0].substring(1);
            var messageContent = splitMessage[1];
            var telegramMessage = messageSender + ": " + messageContent;
            var asyncSendMessage = new Thread(() -> bot.send(telegramMessage));
            asyncSendMessage.start();
            return ActionResult.SUCCESS;

        }

        if (ConfigurationManager.getInstance().getConfiguration().getAllChat())
        {
            var asyncSendQuietMessage = new Thread(() -> bot.sendQuiet(message));
            asyncSendQuietMessage.start();
            return ActionResult.SUCCESS;

        }

        if (message.toLowerCase().contains(lowercaseUsername))
        {
            var splitByArrow = message.split("➠");
            var username = splitByArrow[0].toLowerCase(Locale.ROOT);
            if (username.contains(lowercaseUsername))
            {
                return ActionResult.SUCCESS;
            }

            var asyncSendMessage = new Thread(() -> bot.send(message));
            asyncSendMessage.start();
        }
        return ActionResult.SUCCESS;
    }
}
