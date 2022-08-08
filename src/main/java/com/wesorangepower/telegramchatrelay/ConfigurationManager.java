package com.wesorangepower.telegramchatrelay;

import com.google.gson.Gson;
import com.wesorangepower.telegramchatrelay.models.ModConfiguration;
import com.wesorangepower.telegramchatrelay.telegram.LocalTelegramBot;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.*;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class ConfigurationManager
{
    public static final String FILENAME = "config/telegram-chat-relay.json";

    private ModConfiguration configuration;

    private static ConfigurationManager INSTANCE;

    public static ConfigurationManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new ConfigurationManager();
        return INSTANCE;
    }

    private ConfigurationManager()
    {
        configuration = loadConfig();
    }

    public void saveConfig()
    {
        var file = new File(FILENAME);
        var gson = new Gson();
        var output = gson.toJson(configuration);
        try
        {
            var writer = new FileWriter(file);
            writer.write(output);
            writer.close();
        }
        catch (IOException e)
        {
            TelegramChatRelay.LOGGER.error("Failed to save config.");
        }
    }


    public ModConfiguration loadConfig()
    {
        var gson = new Gson();
        var file = new File(FILENAME);
        ModConfiguration config;
        try
        {
            if (file.createNewFile())
            {
                config = getDefaultConfig();
                var output = gson.toJson(config);
                var writer = new FileWriter(file);
                writer.write(output);
                writer.close();
            }

            var reader = new FileReader(file);
            config = gson.fromJson(reader, ModConfiguration.class);
            reader.close();
        }
        catch (IOException e)
        {
            TelegramChatRelay.LOGGER.error("Failed to load config.");
            config = getDefaultConfig();
        }
        return config;
    }

    private ModConfiguration getDefaultConfig()
    {
        return new ModConfiguration("XX:XXX", 1L, false);
    }


    public void openConfigScreen()
    {
        Runnable saving = this::saveConfig;

        var currentScreen = MinecraftClient.getInstance().currentScreen;
        var builder = ConfigBuilder.create()
                .setParentScreen(currentScreen)
                .setTitle(Text.of("Telegram chat relay config."))
                .setSavingRunnable(saving);

        var category = builder.getOrCreateCategory(Text.of("General config for Telegram Chat Relay"));

        List<TooltipListEntry> configEntries = getConfigEntries(builder.entryBuilder());
        for (TooltipListEntry configEntry : configEntries)
        {
            category.addEntry(configEntry);
        }

        MinecraftClient.getInstance().setScreen(builder.build());
    }


    private List<TooltipListEntry> getConfigEntries(ConfigEntryBuilder entryBuilder)
    {
        var key = entryBuilder.startStrField(Text.of("Bot API Key"), configuration.getApiKey())
                .setDefaultValue("")
                .setTooltip(Text.of("API Key for your Telegram bot.\nAsk @BotFather for one."))
                .setSaveConsumer(this::setApiKey)
                .build();

        var chatId = entryBuilder.startLongField(Text.of("Chat ID"), configuration.getChatId())
                .setDefaultValue(1L)
                .setTooltip(Text.of("Your primary Chat's ID.\nUse /chatid when messaging bot to get one"))
                .setSaveConsumer(this::setChatId)
                .build();

        var allChat = entryBuilder.startBooleanToggle(Text.of("Relay everything"), configuration.getAllChat())
                .setDefaultValue(false)
                .setTooltip(Text.of("Should all non-recurring messages\nbe sent to Telegram\nor just DMs and mentions?"))
                .setSaveConsumer(this::setAllChat)
                .build();

        return List.of(key, chatId, allChat);
    }

    public void setApiKey(String apiKey)
    {
        this.configuration.setApiKey(apiKey);
        saveConfig();
        LocalTelegramBot.reload();
    }

    public void setChatId(Long chatId)
    {
        this.configuration.setChatId(chatId);
        saveConfig();
        LocalTelegramBot.reload();
    }

    public void setAllChat(boolean allChat)
    {
        this.configuration.setAllChat(allChat);
        saveConfig();
        LocalTelegramBot.reloadConfig();
    }

    public ModConfiguration getConfiguration()
    {
        return configuration;
    }
}
