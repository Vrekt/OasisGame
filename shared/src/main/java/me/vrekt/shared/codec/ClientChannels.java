package me.vrekt.shared.codec;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Different type of socket channels
 * Adapted from LunarGdx
 */
public enum ClientChannels {

    NIO(NioSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new NioEventLoopGroup();
        }
    },

    KQUEUE(KQueueSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new KQueueEventLoopGroup();
        }
    },

    EPOLL(EpollSocketChannel.class) {
        @Override
        public EventLoopGroup get() {
            return new EpollEventLoopGroup();
        }
    };

    private final Class<? extends SocketChannel> channel;

    ClientChannels(Class<? extends SocketChannel> channelClass) {
        this.channel = channelClass;
    }

    public abstract EventLoopGroup get();

    public Class<? extends SocketChannel> channel() {
        return channel;
    }


}


