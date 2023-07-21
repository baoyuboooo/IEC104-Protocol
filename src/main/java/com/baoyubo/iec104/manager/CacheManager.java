package com.baoyubo.iec104.manager;

import com.baoyubo.iec104.constant.Constants;
import com.baoyubo.iec104.model.Message;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息缓存管理
 * <p>
 * 此场景有如下特种：
 * 1. 有界队列
 * 2. 先进先出(丢弃)
 * 3. 进入队列值一定是有序递增的（如果超过最大值则自动重置）
 * 4. 最大值 远远大于 队列容量
 * 5. 收到的阈值序列号 范围一定是：大于等于队列头部值，小于等于对垒尾部值+1  (当前队列中所有元素值 和 队列尾部值+1)
 *
 * @author yubo.bao
 * @date 2023/7/11 18:31
 */
public class CacheManager {

    /**
     * 有序链表 (队列)
     */
    private final LinkedList<Message> cacheQueue;

    /**
     * 缓存大小
     */
    private final int cacheCapacity;

    /**
     * 最大值（如果超过最大值，则自动重新从最小值重新开始）
     */
    private final short maxValueNum;

    /**
     * 最消值
     */
    private final short minValueNum;


    /**
     * 默认构造函数（默认缓存大小为256）
     */
    public CacheManager() {
        this.cacheQueue = new LinkedList<>();
        this.cacheCapacity = Constants.CACHE_CAPACITY;
        this.maxValueNum = Constants.SEQUENCE_NUM_MAX;
        this.minValueNum = Constants.SEQUENCE_NUM_MIN;
    }

    /**
     * 构造函数
     *
     * @param cacheCapacity 缓存大小
     */
    public CacheManager(int cacheCapacity, short maxValueNum, short minValueNum) {
        this.cacheQueue = new LinkedList<>();
        this.cacheCapacity = cacheCapacity;
        this.maxValueNum = maxValueNum;
        this.minValueNum = minValueNum;
    }

    /**
     * 获取所有缓存消息发送序列号
     *
     * @return List
     */
    public List<Short> getAllSendSequenceNum() {
        return cacheQueue.stream().map(i -> i.getControl().getSendSequenceNum()).collect(Collectors.toList());
    }

    /**
     * 清空缓存
     */
    public void clearAll() {
        cacheQueue.clear();
    }


    /**
     * 添加缓存
     */
    public void add(Message message) {
        cacheQueue.addLast(message);
        if (cacheQueue.size() > this.cacheCapacity) {
            cacheQueue.removeFirst();
        }
    }

    /**
     * 清除缓存中 小于n 的消息
     */
    public void removeLess(short n) {
        if (CollectionUtils.isEmpty(cacheQueue)) {
            return;
        }

        // 大于尾部边界值特殊处理
        // [32764, 32765, 32766, 32767]   <-- 0
        short lastSendSequenceNum = cacheQueue.getLast().getControl().getSendSequenceNum();
        if (lastSendSequenceNum == this.maxValueNum && n == this.minValueNum) {
            cacheQueue.clear();
            return;
        }
        // [32765, 32766, 32767, 0, 1]   <-- 2
        if (lastSendSequenceNum < this.maxValueNum && n == lastSendSequenceNum + 1) {
            cacheQueue.clear();
            return;
        }

        // 通用处理: 从后往前遍历，找到对应的元素后，前面的元素一定小于它
        // [32765, 32766, 32767, 0, 1]   <-- 0
        boolean find = false;
        Iterator<Message> iterator = cacheQueue.descendingIterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (n == message.getControl().getSendSequenceNum()) {
                find = true;
                continue;
            }
            if (find) {
                iterator.remove();
            }
        }
    }


    /**
     * 获取除缓存中 大于等于n 的消息
     */
    public List<Message> getGreaterEqual(short n) {

        if (CollectionUtils.isEmpty(cacheQueue)) {
            return Collections.emptyList();
        }

        // 大于尾部边界值特殊处理
        // [32764, 32765, 32766, 32767]   <-- 0
        short lastSendSequenceNum = cacheQueue.getLast().getControl().getSendSequenceNum();
        if (lastSendSequenceNum == this.maxValueNum && n == this.minValueNum) {
            return Collections.emptyList();
        }
        // [32765, 32766, 32767, 0, 1]   <-- 2
        if (lastSendSequenceNum < this.maxValueNum && n == lastSendSequenceNum + 1) {
            return Collections.emptyList();
        }

        // 通用处理: 从前往后历，找到对应的元素后，后面的元素一定大于它
        // [32765, 32766, 32767, 0, 1]   <-- 0
        List<Message> res = new ArrayList<>();
        boolean find = false;
        Iterator<Message> iterator = cacheQueue.iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (n == message.getControl().getSendSequenceNum()) {
                find = true;
            }
            if (find) {
                res.add(message);
            }
        }
        return res;
    }
}
