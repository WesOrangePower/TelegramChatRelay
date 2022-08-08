package com.wesorangepower.telegramchatrelay.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.wesorangepower.telegramchatrelay.ConfigurationManager;
import com.wesorangepower.telegramchatrelay.TelegramChatRelay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Objects;

public class LocalTelegramBot
{

    public TelegramBot telegramBot;
    private ConfigurationManager configurationManager;

    private static LocalTelegramBot instance;

    public static LocalTelegramBot getInstance()
    {
        if (instance == null)
            load();
        return instance;
    }

    public static void reload()
    {
        if (instance != null)
        {
            instance.telegramBot.removeGetUpdatesListener();
            instance.telegramBot.shutdown();
            instance.telegramBot = null;
        }
        instance = new LocalTelegramBot();
    }

    public static void load()
    {
        reload();
    }

    public static void reloadConfig()
    {
        instance.configurationManager = ConfigurationManager.getInstance();
    }

    private LocalTelegramBot()
    {
        this.configurationManager = ConfigurationManager.getInstance();
        var token = configurationManager.getConfiguration().getApiKey();

        telegramBot = new TelegramBot(token);

        var response = telegramBot.execute(new GetMe());
        if (!response.isOk())
        {
            TelegramChatRelay.LOGGER.error("Failed to set up a the Telegram bot. Check your API Key: " + response);
            telegramBot.removeGetUpdatesListener();
            telegramBot.shutdown();
            return;
        }

        telegramBot.setUpdatesListener(new LocalUpdatesListener());
    }

    public void onUpdateReceived(Update update)
    {
        if (update == null || update.message() == null)
            return;
        var message = update.message();
        TelegramChatRelay.LOGGER.info(
                String.format("Update received: TEXT=%s, CHAT ID=%s, AUTHOR=%s: %s",
                        message.text(),
                        message.chat().id(),
                        message.authorSignature(),
                        message
                )
        );

        var text = update.message().text();

        if (text == null)
        {
            return;
        }

        if (text.equals("/start") || text.equals("/chatid"))
        {
            var response = String.format(
                    "This bot is currently running Telegram Chat Relay mod for Minecraft. " +
                            "This chat's id is: %s.", message.chat().id()
            );
            send(response, message.chat().id());
        }

        if (message.chat().id().equals(configurationManager.getConfiguration().getChatId()))
        {
            if (text.equals("/allchat"))
            {
                var allChat = configurationManager.getConfiguration().getAllChat();
                configurationManager.setAllChat(!allChat);
                send("Bridging of all messages is " + (allChat ? "off." : "on."));
                return;
            }

            if (text.equals("/help"))
            {
                send("/allchat - Toggles between bridging of all received messages and only DMs and mentions.\n" +
                        "/tgdisconnect - Disconnects from current session.");
                return;
            }

            if (text.startsWith("/tgdisconnect"))
            {
                Objects.requireNonNull(MinecraftClient
                                .getInstance()
                                .getNetworkHandler())
                        .getConnection()
                        .disconnect(Text.of(""));
                return;
            }

            if (MinecraftClient.getInstance().player != null)
            {
                MinecraftClient.getInstance().player.sendChatMessage(text);
            }
        }
    }

    public void send(String s)
    {
        send(s, configurationManager.getConfiguration().getChatId(), false);
    }

    public void send(String s, Long chatId)
    {
        send(s, chatId, false);
    }

    public void send(String s, Long chatId, boolean quiet)
    {
        var sendMessage = new SendMessage(chatId, s);
        sendMessage.disableNotification(quiet);
        try
        {
            telegramBot.execute(sendMessage);
        }
        catch (Exception e)
        {
            LogManager.getLogger().log(Level.WARN, e.toString());
        }
    }

    public void sendQuiet(String s)
    {
        send(s, configurationManager.getConfiguration().getChatId(), false);
    }

    private class LocalUpdatesListener implements UpdatesListener
    {
        @Override
        public int process(List<Update> updates)
        {
            for (var update : updates)
            {
                onUpdateReceived(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
    }
}
