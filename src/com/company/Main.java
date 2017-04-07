package com.company;

import java.lang.reflect.InvocationTargetException;
import java.io.*;

public class Main {

    public static void main(String[] args) throws  IOException,
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException, NoSuchFieldException {
        
        TestClass testClass = new TestClass();
        byte[] ser = Serializator.serialaize(testClass);
        FileWorker.write(ser);
        byte[] get = FileWorker.getBytesFromFile();
        TestClass a = Serializator.deserialize(get);
        System.out.println(a.getDoubleField());
        System.out.println(a.strield1);
        System.out.println(a.a.a + " ce aa aa");
    }
}
