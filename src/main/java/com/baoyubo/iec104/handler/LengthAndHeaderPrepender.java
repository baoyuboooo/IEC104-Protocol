package com.baoyubo.iec104.handler;

import com.baoyubo.iec104.constant.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.List;

/**
 * IEC104协议自动填充: 启动字符固定头、APDU长度
 *
 * @author yubo.bao
 * @date 2023/7/3 13:34
 */
public class LengthAndHeaderPrepender extends LengthFieldPrepender {

    public LengthAndHeaderPrepender() {
        super(1, 0, false);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
        out.add(0, ctx.alloc().buffer(1).writeByte(Constants.HEADER));
    }
}
