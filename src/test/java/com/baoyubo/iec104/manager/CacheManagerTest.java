package com.baoyubo.iec104.manager;

import com.baoyubo.iec104.enums.FrameTypeEnum;
import com.baoyubo.iec104.model.Message;
import com.baoyubo.iec104.model.MessageControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yubo.bao
 * @date 2023/7/21 17:32
 */
class CacheManagerTest {

    private Message mockMessage(int sendSequenceNum) {

        MessageControl control = new MessageControl();
        control.setSendSequenceNum((short) sendSequenceNum);

        Message message = new Message();
        message.setFrameType(FrameTypeEnum.I_FRAME);
        message.setControl(control);

        return message;
    }

    private String getQueueAllStr(CacheManager cacheMgr) {
        return cacheMgr.getAllSendSequenceNum().toString();
    }

    private String getStr(List<Message> list) {
        return list.stream()
                .map(i -> i.getControl().getSendSequenceNum())
                .collect(Collectors.toList()).toString();
    }

    @Test
    public void getGreaterEqual() {
        CacheManager cacheMgr = new CacheManager(3, (short) 8, (short) 0);

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(5));
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 5)));
        Assertions.assertEquals("[6, 7, 8]", getStr(cacheMgr.getGreaterEqual((short) 6)));
        Assertions.assertEquals("[7, 8]", getStr(cacheMgr.getGreaterEqual((short) 7)));
        Assertions.assertEquals("[8]", getStr(cacheMgr.getGreaterEqual((short) 8)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 0)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 1)));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 7)));
        Assertions.assertEquals("[8]", getStr(cacheMgr.getGreaterEqual((short) 8)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 0)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 1)));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 7)));
        Assertions.assertEquals("[8, 0, 1]", getStr(cacheMgr.getGreaterEqual((short) 8)));
        Assertions.assertEquals("[0, 1]", getStr(cacheMgr.getGreaterEqual((short) 0)));
        Assertions.assertEquals("[1]", getStr(cacheMgr.getGreaterEqual((short) 1)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 2)));
        Assertions.assertEquals("[]", getStr(cacheMgr.getGreaterEqual((short) 3)));
    }


    @Test
    public void removeLess() {

        CacheManager cacheMgr = new CacheManager(3, (short) 8, (short) 0);

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 5);
        Assertions.assertEquals("[6, 7, 8]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 6);
        Assertions.assertEquals("[6, 7, 8]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 7);
        Assertions.assertEquals("[7, 8]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 8);
        Assertions.assertEquals("[8]", getQueueAllStr(cacheMgr));


        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 0);
        Assertions.assertEquals("[]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(6));
        cacheMgr.add(mockMessage(7));
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 1);
        Assertions.assertEquals("[6, 7, 8]", getQueueAllStr(cacheMgr));

    }


    @Test
    public void removeLess2() {

        CacheManager cacheMgr = new CacheManager(3, (short) 8, (short) 0);

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 7);
        Assertions.assertEquals("[8]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 8);
        Assertions.assertEquals("[8]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 0);
        Assertions.assertEquals("[]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.removeLess((short) 1);
        Assertions.assertEquals("[8]", getQueueAllStr(cacheMgr));


        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        cacheMgr.removeLess((short) 7);
        Assertions.assertEquals("[8, 0, 1]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        cacheMgr.removeLess((short) 8);
        Assertions.assertEquals("[8, 0, 1]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        cacheMgr.removeLess((short) 0);
        Assertions.assertEquals("[0, 1]", getQueueAllStr(cacheMgr));

        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        cacheMgr.removeLess((short) 1);
        Assertions.assertEquals("[1]", getQueueAllStr(cacheMgr));


        cacheMgr.clearAll();
        cacheMgr.add(mockMessage(8));
        cacheMgr.add(mockMessage(0));
        cacheMgr.add(mockMessage(1));
        cacheMgr.removeLess((short) 2);
        Assertions.assertEquals("[]", getQueueAllStr(cacheMgr));

    }

}