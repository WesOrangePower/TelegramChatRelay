package com.wesorangepower.telegramchatrelay;

import com.wesorangepower.telegramchatrelay.client.ChatMessageCallBack;
import com.wesorangepower.telegramchatrelay.client.GameMessageHandler;
import com.wesorangepower.telegramchatrelay.telegram.LocalTelegramBot;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class TelegramChatRelay implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getFormatterLogger("ChatRelay");

    @Override
    public void onInitialize()
    {
        try
        {
            LocalTelegramBot.load();
            ChatMessageCallBack.EVENT.register(GameMessageHandler::onMessage);
            LOGGER.log(Level.INFO, "Bot seems to have been loaded");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        var keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.telegramChatRelay.openConfig".toLowerCase(),
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_HOME,
                        "category.telegramChatRelay.binds".toLowerCase()
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                client ->
                {
                    if (keyBinding.wasPressed())
                    {
                        ConfigurationManager.getInstance().openConfigScreen();
                    }
                });

    }
}