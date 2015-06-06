package ru.mars.gameserver.netty;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

/**
 * Date: 01.11.14
 * Time: 14:18
 */
public class ChannelsHolder {
    private static ChannelsHolder instance = new ChannelsHolder();
    private ChannelGroup channels = new DefaultChannelGroup();

    public static ChannelsHolder getInstance() {
        return instance;
    }

    public ChannelGroup getChannels() {
        return channels;
    }

    public void setChannels(ChannelGroup channels) {
        this.channels = channels;
    }
}
