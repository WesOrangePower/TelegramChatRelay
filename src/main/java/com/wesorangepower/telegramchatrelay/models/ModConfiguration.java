package com.wesorangepower.telegramchatrelay.models;

public class ModConfiguration
{

    private String apiKey;
    private Long chatId;
    private boolean allChat;

    public ModConfiguration(String apiKey, Long chatId, boolean allChat)
    {
        this.apiKey = apiKey;
        this.chatId = chatId;
        this.allChat = allChat;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public Long getChatId()
    {
        return chatId;
    }

    public void setChatId(Long chatId)
    {
        this.chatId = chatId;
    }

    public boolean getAllChat()
    {
        return allChat;
    }

    public void setAllChat(boolean allChat)
    {
        this.allChat = allChat;
    }
}
