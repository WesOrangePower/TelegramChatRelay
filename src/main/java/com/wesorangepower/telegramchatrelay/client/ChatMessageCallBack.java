package com.wesorangepower.telegramchatrelay.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface ChatMessageCallBack {

    /**
     * Callback for receiving a message.
     * Called before a text message is displayed.
     */
    Event<ChatMessageCallBack> EVENT = EventFactory.createArrayBacked(ChatMessageCallBack.class,
        (listeners) -> (message) -> {
            for (ChatMessageCallBack listener : listeners) {
                ActionResult result = listener.onChatMessage(message);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
    });
    ActionResult onChatMessage(String message);
}
