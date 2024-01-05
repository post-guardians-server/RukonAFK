package me.rukon0621.afk.util;

import me.rukon0621.afk.RukonAFK;

import javax.annotation.Nullable;
import java.io.*;

public class Serializer {
    //객체 직렬화
    public static byte[] serialize(Object obj) {
        try {
            if(obj==null) {
                RukonAFK.inst().getLogger().severe("SERIALIZE EXCEPTION!");
            }

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            oo.close();
            return bo.toByteArray();
        } catch (IOException e) {
            return new byte[]{};
        }
    }

    @Nullable
    public static Object deserialize(byte[] bytes) {
        if(bytes==null) return null;
        try {
            ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return oi.readObject();
        } catch (Exception e) {
            RukonAFK.inst().getLogger().severe("DESERIALIZE EXCEPTION!");
            e.printStackTrace();
            return null;
        }
    }
}
