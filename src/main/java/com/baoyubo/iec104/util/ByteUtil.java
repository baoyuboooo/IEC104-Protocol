package com.baoyubo.iec104.util;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * 字节工具类
 *
 * @author yubo.bao
 * @date 2023/7/19 17:42
 */
public final class ByteUtil {

    /**
     * 将 int 值转换为 字节数组（占4个字节，低位在前）
     *
     * @param value int 值
     * @return 字节数组
     */
    public static byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 24) & 0xFF)
        };
    }


    /**
     * 将 字节数组 转换为 int 值（占4个字节，低位在前）
     *
     * @param byteArray 字节数组
     * @return int 值
     */
    public static int byteArrayToInt(byte[] byteArray) {
        return (byteArray[3] << 24) |
            ((byteArray[2] & 0xFF) << 16) |
            ((byteArray[1] & 0xFF) << 8) |
            (byteArray[0] & 0xFF);
    }


    /**
     * 将 short 值转换为 字节数组（占2个字节，低位在前）
     *
     * @param value short 值
     * @return 字节数组
     */
    public static byte[] shortToByteArray(short value) {
        return new byte[]{
            (byte) (value & 0xFF),
            (byte) ((value >> 8) & 0xFF)
        };
    }


    /**
     * 将 字节数组 转换为 short 值（占2个字节，低位在前）
     *
     * @param byteArray 字节数组
     * @return short 值
     */
    public static short byteArrayToShort(byte[] byteArray) {
        return (short) ((byteArray[1] << 8) |
            (byteArray[0] & 0xFF));
    }


    /**
     * 将 float 值转换为 字节数组（占4个字节，低位在前）
     *
     * @param value float 值
     * @return 字节数组
     */
    public static byte[] floatToByteArray(float value) {
        int intValue = Float.floatToIntBits(value);
        return new byte[]{
            (byte) (intValue & 0xFF),
            (byte) ((intValue >> 8) & 0xFF),
            (byte) ((intValue >> 16) & 0xFF),
            (byte) ((intValue >> 24) & 0xFF)
        };
    }


    /**
     * 将 字节数组 转换为 float 值（占4个字节，低位在前）
     *
     * @param byteArray 字节数组
     * @return float 值
     */
    public static float byteArrayToFloat(byte[] byteArray) {
        int intValue = (byteArray[3] << 24) |
            ((byteArray[2] & 0xFF) << 16) |
            ((byteArray[1] & 0xFF) << 8) |
            (byteArray[0] & 0xFF);
        return Float.intBitsToFloat(intValue);
    }


    /**
     * 将 日期 转换为 CP56Time2a 格式字节数组 (占7个字节)
     *
     * @param date 日期
     * @return 字节数组
     */
    public static byte[] dateToCP56TimeByteArray(Date date) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 毫秒需要转换成两个字节其中 低位在前高位在后
        // 先转换成short
        int millisecond = calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);

        // 默认的高位在前
        byte[] millisecondByte = new byte[]{
            (byte) (0),
            (byte) (0),
            (byte) ((millisecond >> 8) & 0xFF),
            (byte) (millisecond & 0xFF)
        };

        bOutput.write(millisecondByte[3]);
        bOutput.write(millisecondByte[2]);

        // 分钟 只占6个比特位 需要把前两位置为零
        bOutput.write((byte) calendar.get(Calendar.MINUTE));

        // 小时需要把前三位置零
        bOutput.write((byte) calendar.get(Calendar.HOUR_OF_DAY));

        // 星期日的时候 week 是0
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY) {
            week = 7;
        } else {
            week--;
        }

        // 前三个字节是 星期 因此需要将星期向左移5位  后五个字节是日期  需要将两个数字相加 相加之前需要先将前三位置零
        bOutput.write((byte) (week << 5) + (calendar.get(Calendar.DAY_OF_MONTH)));

        // 前四字节置零
        bOutput.write((byte) ((byte) calendar.get(Calendar.MONTH) + 1)); // 注意 Calendar.MONTH 的范围是 0~11 来表示 1~12 月份
        bOutput.write((byte) (calendar.get(Calendar.YEAR) - 2000));

        return bOutput.toByteArray();
    }


    /**
     * 将 CP56Time2a 格式字节数组 转换为 日期 (占7个字节)
     *
     * @param byteArray 字节数组
     * @return 日期
     */
    public static Date cp56TimeByteArrayToDate(byte[] byteArray) {
        int year = (byteArray[6] & 0x7F) + 2000;
        int month = byteArray[5] & 0x0F;
        int day = byteArray[4] & 0x1F;
        int hour = byteArray[3] & 0x1F;
        int minute = byteArray[2] & 0x3F;
        int second = byteArray[1] > 0 ? byteArray[1] : (byteArray[1] & 0xff);
        int millisecond = byteArray[0] > 0 ? byteArray[0] : (byteArray[0] & 0xff);

        millisecond = (second << 8) + millisecond;
        second = millisecond / 1000;
        millisecond = millisecond % 1000;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // 注意 Calendar.MONTH 的范围是 0~11 来表示 1~12 月份
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }


    /**
     * 将字节转换成 十六进制
     */
    public static String toHexString(byte b) {
        return String.format("%2s", Integer.toHexString(b & 0xFF)).toUpperCase().replace(' ', '0');
    }


    /**
     * 将字节数组转换成 十六进制（空格分割）
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            String s = Integer.toHexString(aByte & 0xFF).toUpperCase();
            if (s.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(s);
            stringBuilder.append(" ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    /**
     * 将字节转换成 二进制
     */
    public static String toBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }


    /**
     * 将字节数组转换成 二进制（空格分割）
     */
    public static String toBinaryString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            stringBuilder.append(toBinaryString(aByte));
            stringBuilder.append(" ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }


    private ByteUtil() {
    }
}
