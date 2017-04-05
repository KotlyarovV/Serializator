package com.company;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by vitaly on 03.04.17.
 */
public class Converter {

    public static HashMap<String, Integer> codes = new HashMap<>();
    public static HashMap<Integer, Integer> lengths = new HashMap<>();

    public static byte[] toByte (int number, Object value) {
        switch (number) {
            case 1: return intToByte((int)value);
            case 2: return doubleToByte((double)value);
            case 3: return floatToByte((float)value);
            case 4: return new byte[] {(byte) ((char) value)};
            case 5: return longToByte((long) value);
            case 6: return shortToByte((short) value);
            case 7: return new byte[] {(byte) value};
            case 8: return boolToByte((boolean) value);
            case 9: return value.toString().getBytes();
        }
        return null;
    }



    public static void  makeCodes () {
        codes.put("int",1);//1 int
        codes.put("double",2);//2 double
        codes.put("float",3);//3 float
        codes.put("char",4);//4 char
        codes.put("long",5);//5 long
        codes.put("short",6);//6 short
        codes.put("byte",7);//7 byte
        codes.put("bool",8);//8 - bool
        codes.put("java.lang.String",9);//9 -string

        lengths.put(1, 4);
        lengths.put(2, 8);
        lengths.put(3, 4);
        lengths.put(4, 1);
        lengths.put(5, 8);
        lengths.put(6, 2);
        lengths.put(7, 1);
        lengths.put(8, 4);
    }

    public static byte[] doubleToByte(double number) {
        byte [] bytes = ByteBuffer.allocate(8).putDouble(number).array();
        return bytes;
    }

    public static byte[] longToByte(long number) {
        byte [] bytes = ByteBuffer.allocate(8).putLong(number).array();
        return bytes;
    }

    public static byte[] shortToByte(short number) {
        byte [] bytes = ByteBuffer.allocate(2).putShort(number).array();
        return bytes;
    }

    public static byte[] intToByte(int number) {
        byte [] bytes = ByteBuffer.allocate(4).putInt(number).array();
        System.out.println(bytes.length);
        return bytes;
    }

    public static byte[] boolToByte(boolean a) {
        int number = a? 1 : 0;
        byte [] bytes = ByteBuffer.allocate(4).putInt (number).array();
        return bytes;
    }

    public static byte[] floatToByte(float number) {
        byte [] bytes = ByteBuffer.allocate(4).putFloat(number).array();
        return bytes;
    }

    public static  int byteToInt (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static String byteToString (byte[] bytes)
            throws UnsupportedEncodingException
    { return new String(bytes, "utf-8");};

    public static  double byteToDouble (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static  float byteToFloat (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static  char byteToChar (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar();
    }

    public static  long byteToLong (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static  long byteToShort (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    public static  boolean byteToBoolean (byte[] bytes) {
        int a = byteToInt(bytes);
        return a == 1;
    }
}
