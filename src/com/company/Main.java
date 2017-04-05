package com.company;

import java.awt.datatransfer.SystemFlavorMap;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

import static com.company.Converter.*;


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

    public static void main(String[] args) throws IllegalAccessException, IOException,
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchFieldException,
            UnsupportedEncodingException {
	// write your code here
        Converter.makeCodes();
        TestClass testClass = new TestClass();
        byte[] ser = serialaize(testClass);
        byte[] bytes = doubleToByte(6.87);
        double d = byteToDouble(bytes);
        System.out.println(ser.length);
        write(ser);
        System.out.println(getBytesFromFile().length);
        TestClass a = deserialize(ser);
    }

    public static <T extends Object> T deserialize (byte[] bytes)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchFieldException,
            UnsupportedEncodingException {

        if (bytes.length == 2) return null;

        byte[] classBytes = Arrays.copyOfRange(bytes, 1, bytes.length - 1);
        byte[] nameLengthBytes = Arrays.copyOfRange(classBytes, 0, 4);
        int nameLength = Converter.byteToInt(nameLengthBytes);

        byte[] nameBytes = Arrays.copyOfRange(classBytes, 4, 4 + nameLength);
        String className = Converter.byteToString(nameBytes);
        Class classObj = Class.forName(className);
        Object object = classObj.getConstructor().newInstance();

        int i = 4 + nameLength;

        while (i < classBytes.length) {

            byte[] codeFieldBytes = Arrays.copyOfRange(classBytes, i, i+ 4);
            int codeField = Converter.byteToInt(codeFieldBytes);
            i = i + 4;

            byte[] nameFieldLengthBytes = Arrays.copyOfRange(classBytes, i, i + 4);
            int nameFieldLength = Converter.byteToInt(nameFieldLengthBytes);
            i = i + 4 ;

            byte[] nameFieldBytes = Arrays.copyOfRange(classBytes, i , i + nameFieldLength);
            String nameField = Converter.byteToString(codeFieldBytes);

            if (codeField < 9) {
                //получаем размер поля в байтах из его кода
                int sizeField = Converter.lengths.get(codeField);
                byte[] fieldByte = Arrays.copyOfRange(classBytes, i, i + sizeField);
                i = i + sizeField;

                Field fieldAbstract = classObj.getField(nameField);

                switch (codeField){

                    case 1:
                        fieldAbstract.set(object, byteToInt(fieldByte));
                        break;
                    case 2:
                        fieldAbstract.set(object, byteToDouble(fieldByte));
                        break;
                    case 3:
                        fieldAbstract.set(object, Converter.byteToFloat(fieldByte));
                        break;
                    case 4:
                        fieldAbstract.set(object, Converter.byteToChar(fieldByte));
                        break;
                    case 5:
                        fieldAbstract.set(object, Converter.byteToLong(fieldByte));
                        break;
                    case 6:
                        fieldAbstract.set(object, Converter.byteToShort(fieldByte));
                        break;
                    case 7:
                        fieldAbstract.set(object, fieldByte[0]);
                        break;
                    case 8:
                        fieldAbstract.set(object, Converter.byteToBoolean(fieldByte));
                        break;
                }
            } else if (codeField == 9) {
                byte[] sizeStringBytes = Arrays.copyOfRange(classBytes, i , i + 4);
                i = i + 4;
                int sizeString = byteToInt(sizeStringBytes);

                byte[] stringBytes = Arrays.copyOfRange(classBytes, i , i + sizeString);
                i = i + sizeString;
                String string = byteToString(stringBytes);

                Field fieldAbstract = classObj.getField(nameField);
                fieldAbstract.set(object, string);
            } else {

                byte[] lengthNameOfClassBytes = Arrays.copyOfRange(classBytes, i , i + 4);
                i = i + 4;
                int lengthNameOfClass = byteToInt(lengthNameOfClassBytes);

                byte[] nameOfClassBytes = Arrays.copyOfRange(classBytes, i , i + lengthNameOfClass);
                i = i + lengthNameOfClass;

                int j = i;

                do { j ++; }
                while ((char) bytes[j] != ')');

                byte[] classBytesIn = Arrays.copyOfRange(classBytes, i , j + 1);

                Field fieldAbstract = classObj.getField(nameField);

                fieldAbstract.set(object, deserialize(classBytesIn));

                i = j + 1;
            }
        }




        return (T) object;
    }

    public static <T extends  Object> byte[] serialaize  (T   object ) throws IllegalAccessException, IOException
    {
        if (object == null) return new byte[]{(byte) '(', (byte) ')'};

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[] {(byte)'('});
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
                out.write(intToByte(type.length()));
                out.write(type.getBytes());
                out.write(serialaize(field.get(object)));
            }

        }

        out.write(new byte[] {(byte)')'});
        return out.toByteArray();
    }
}
