package com.company;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by vitaly on 03.04.17.
 */
public class Converter {

    public static HashMap<String, Integer> codes = new HashMap<>();


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

    public static  double byteToDouble (byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }


}
