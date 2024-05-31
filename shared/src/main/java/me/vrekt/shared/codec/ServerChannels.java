package me.vrekt.shared.codec;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Holds data for server channels
 */
public enum ServerChannels {

    NIO(NioServerSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new NioEventLoopGroup(4);
        }
    },

    KQUEUE(KQueueServerSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new KQueueEventLoopGroup(4);
        }
    },

    EPOLL(EpollServerSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new EpollEventLoopGroup(4);
        }
    };

    private final Class<? extends ServerSocketChannel> channel;

    ServerChannels(Class<? extends ServerSocketChannel> channelClass) {
        this.channel = channelClass;
    }

    public abstract EventLoopGroup get();

    public Class<? extends ServerSocketChannel> channel() {
        return channel;
    }

    public EventLoopGroup newGroup() {
        return this.get();
    }


}
