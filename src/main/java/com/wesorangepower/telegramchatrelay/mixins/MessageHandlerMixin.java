package com.wesorangepower.telegramchatrelay.mixins;

import com.wesorangepower.telegramchatrelay.models.MessageQueue;
import com.wesorangepower.telegramchatrelay.client.GameMessageHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(ClientPlayNetworkHandler.class)
public class MessageHandlerMixin
{
    Queue<String> q = new MessageQueue<>(104);

    @Inject(method = "onGameMessage", at = @At(value = "HEAD"))
    private void addChatMessage(GameMessageS2CPacket packet, CallbackInfo ci)
    {
        var rawMessage = packet.content().getString();
        if (!q.contains(rawMessage))
        {
            q.add(rawMessage);
            GameMessageHandler.onMessage(rawMessage);
        }

    }
}
