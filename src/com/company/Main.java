package com.company;

import java.awt.datatransfer.SystemFlavorMap;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;



import static com.company.Converter.byteToDouble;
import static com.company.Converter.doubleToByte;
import static com.company.Converter.toByte;


public class Main {



    public static void write (byte[] bytes) throws IOException{
        try {
            FileOutputStream fos = new FileOutputStream("myfile.bin");
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {}
    }


    public static byte[] getBytesFromFile () throws IOException{
        Path path = Paths.get("myfile.bin");
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public static void main(String[] args) throws IllegalAccessException, IOException {
	// write your code here
        Converter.makeCodes();
        TestClass testClass = new TestClass();
        byte[] ser = serialaize(testClass);
        byte[] bytes = doubleToByte(6.87);
        double d = byteToDouble(bytes);
        System.out.println(ser.length);
        write(ser);
        System.out.println(getBytesFromFile().length);

    }

    public static <T extends Object> T deserialize (byte[] bytes)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {

        if (bytes.length == 2) return null;

        byte[] classBytes = Arrays.copyOfRange(bytes, 1, bytes.length - 1);
        byte[] nameLengthBytes = Arrays.copyOfRange(classBytes, 0, 4);
        int nameLength = Converter.byteToInt(nameLengthBytes);

        byte[] nameBytes = Arrays.copyOfRange(classBytes, 4, 4 + nameLength);
        String className = Converter.byteToString(nameBytes);
        Object object = Class.forName(className).getConstructor().newInstance();
        

        return (T) object;
    }

    public static <T extends  Object> byte[] serialaize  (T   object ) throws IllegalAccessException, IOException
    {
        if (object == null) return new byte[]{(byte) '(', (byte) ')'};

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[] {(byte)'('});
        LinkedList<Byte> result = new LinkedList<>();
        Class c =  object.getClass();

        //записали длину имени сразу после скобки
        byte[] nameBytes = c.getName().getBytes();
        out.write(Converter.intToByte(nameBytes.length));
        //записали имя
        out.write(nameBytes);

        Field[] fields = c.getDeclaredFields();

        //идем по полям
        for (Field field : fields)
        {
            field.setAccessible(true);
            //пишем код типа поля
            //тк типов всего 9 можем просто переводить номер в байт и вписывать
            //1 int
            //2 double
            //3 float
            //4 char
            //5 long
            //6 short
            //7 byte
            //8 - bool
            //9 -string
            //10 = любой другой класс

            //вписали код поля
            Class fieldType = field.getType();
            String type = fieldType.getName();
            int typeNumber = (Converter.codes.containsKey(type)) ? Converter.codes.get(type) : 10;
            out.write(Converter.intToByte(typeNumber));

            //длина имени поля и его код
            byte[] nameBytesField = field.getName().toString().getBytes();
            out.write(Converter.intToByte(nameBytesField.length));
            out.write(nameBytesField);

            //если строка - пишем ее длину
            if (typeNumber == 9) {
                if (field.get(object) != null)
                    out.write(Converter.intToByte(toByte(9,field.get(object)).length));
                else out.write(Converter.intToByte(-1));
            }

            if (typeNumber != 10 && field.get(object) != null)
                out.write(toByte(typeNumber,field.get(object)));
            else if (typeNumber != 9) {
                //если класс - пишем какой
                out.write(type.getBytes());
                out.write(serialaize(field.get(object)));
            }

        }

        out.write(new byte[] {(byte)')'});
        return out.toByteArray();
    }
}
