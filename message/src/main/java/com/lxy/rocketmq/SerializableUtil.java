package com.lxy.rocketmq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:27
 * java的序列化和反序列化
 **/
public class SerializableUtil {

    @SuppressWarnings("unchecked")
    public static <T> T parse(byte[] rec, Class<T> classType) {
        ByteArrayInputStream arrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            arrayInputStream = new ByteArrayInputStream(rec);
            objectInputStream = new ObjectInputStream(arrayInputStream);
            T t = (T) objectInputStream.readObject();
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(arrayInputStream);
            closeQuietly(objectInputStream);
        }
        return null;
    }

    public static byte[] toByte(Object obj) {
        ByteArrayOutputStream arrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            arrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            byte[] rtn = arrayOutputStream.toByteArray();
            return rtn;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(objectOutputStream);
            closeQuietly(arrayOutputStream);
        }
        return null;
    }

    public static void closeQuietly(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }
}

