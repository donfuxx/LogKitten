package com.appham.logkitten.notifications;

public enum LogKittenChannel {
    LOG_KITTEN_SERVICE("Log Kitten Service", "Log Kitten monitoring service status"),
    LOG_KITTEN_ENTRIES("Log Kitten Entries", "Log Kitten log entries show in this channel");

    private final CharSequence channelName;
    private final String channelDesc;

    LogKittenChannel(CharSequence channelName, String channelDesc) {
        this.channelName = channelName;
        this.channelDesc = channelDesc;
    }

    public CharSequence getChannelName() {
        return channelName;
    }

    public String getChannelDesc() {
        return channelDesc;
    }
}
