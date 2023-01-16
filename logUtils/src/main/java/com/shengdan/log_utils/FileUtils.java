package com.shengdan.log_utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

/**
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();


    private FileUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void writeString(Context context,String str) {
        if (context == null)return;
        String time = null;
        time = DateUtils.formatDate(DateUtils.Format.Y_M_d_H_m_s.getFormatStr(), new Date());
        //        time = String.valueOf(System.currentTimeMillis());
        StringBuilder builder = new StringBuilder(time);
        builder.append("  ");
        builder.append(str);

        String path =context.getExternalFilesDir(null).getAbsolutePath();

        FileChannel channel = null;

        File file = new File(path + "/crash_data.txt");
        if (file.exists()) {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            channel = os.getChannel();
        } else {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(
                        path + "/crash_data.txt", "rw");
                channel = randomAccessFile.getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                if (!makeFile(path)) {
                    return;
                }
            }
        }

        ByteBuffer buf = ByteBuffer.allocate(48);
        try {
            buf.clear();

            long pos = file.length();
            buf.put(builder.toString().getBytes());

            buf.flip();

            while (buf.hasRemaining()) {
                channel.write(buf, pos);
            }

            buf.put("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 将input流转为byte数组，自动关闭
     */
    public static byte[] toByteArray(InputStream input) throws Exception {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return result;
    }


    /**
     * 关闭InputStream
     */
    public static void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭InputStream
     */
    public static void closeQuietly(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getFromAssetsFile(Context context,String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String readFileAll(String fileName) {
        FileReader reader = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new FileReader(fileName);
            br = new BufferedReader(reader);

            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\n");
            }
        } catch (IOException e) {
            // 包括FileNotFoundException
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();//关闭缓冲流
                }
                if (reader != null) {
                    reader.close();//关闭缓冲流
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    /**
     * 获得文件存储路径
     * 注意输出的路径末尾没有'/'
     */
    public static String getFilePath(Context context) {
        if (Environment.isExternalStorageRemovable()) {//如果外部储存可用
            return context.getFilesDir().getPath();// 直接存在/data/data里，非root手机是看不到的
        } else {
            if (context.getExternalFilesDir(null) == null) {
                // 这种情况发生在1.无法使用SD卡的情况(没有SD卡，移除了SD卡)2.WRITE_EXTERNAL_STORAGE权限3.外存储设备被锁
                return context.getFilesDir().getPath();
            } else {
                return context.getExternalFilesDir(null)
                              .getPath();// 获得外部存储路径,默认路径为 /storage/emulated/0(sdcard1)/Android/data/"packageName"/files
            }
        }
    }


    /**
     * 从文件末尾开始读取文件，并逐行打印
     *
     * @param filename file path
     * @param charset character
     * @param charset endIndex 字符数(从文件末尾倒数endIndex字符开始读取)
     */
    public static String readFileFromEnd(String filename, int endIndex, String charset) {
        RandomAccessFile rf = null;

        StringBuilder sb = new StringBuilder();
        try {
            rf = new RandomAccessFile(filename, "r");
            long fileLength = rf.length();
            long start = rf.getFilePointer();// 返回此文件中的当前偏移量
            LogUtils.debug(TAG, start +
                    "当前偏移量----------------------------------------------------------------------------------");
            long readIndex;
            long filePointer = start + fileLength;
            if (endIndex < filePointer) {
                readIndex = filePointer - endIndex;
            } else {
                readIndex = start;
            }
            String line;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            int c;

            while (readIndex < fileLength) {
                c = rf.read();
                String readText = null;
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                        readText = new String(line.getBytes("ISO-8859-1"), charset);
                    } else {
                        LogUtils.debug(TAG, "readObj line : " + line);
                    }
                    readIndex++;
                }
                readIndex++;
                rf.seek(readIndex);
                if (readIndex == fileLength) {// 当文件指针退至文件开始处，输出第一行
                    readText = rf.readLine();
                }
                if (readText != null) {
                    sb.append(readText + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null) {
                    rf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static String readFileFromEnd(String filename, int startIndex, int endIndex, String charset) {
        RandomAccessFile rf = null;

        StringBuilder sb = new StringBuilder();
        try {
            rf = new RandomAccessFile(filename, "r");
            long fileLength = rf.length();
            long start = rf.getFilePointer();// 返回此文件中的当前偏移量

            long readIndex;
            // 总长度
            long filePointer = start + fileLength;
            if ((endIndex - startIndex) < filePointer) {
                readIndex = filePointer - startIndex;
            } else {
                readIndex = start;
            }
            String line;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            int c;

            while (readIndex < endIndex) {
                c = rf.read();
                String readText = null;
                if (c == '\n' || c == '\r') {
                    line = rf.readLine();
                    if (line != null) {
                        readText = new String(line.getBytes("ISO-8859-1"), charset);
                    } else {
                        LogUtils.debug(TAG, "readObj line : " + line);
                    }
                    readIndex++;
                }
                readIndex++;
                rf.seek(readIndex);
                if (readIndex == fileLength) {// 当文件指针退至文件开始处，输出第一行
                    readText = rf.readLine();
                }
                if (readText != null) {
                    sb.append(readText + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null) {
                    rf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static boolean makeFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * 可以传入一个多级不存在的路径，遍历依次创建
     *
     * @throws IOException
     */
    public static void makeDirs(String path) throws IOException {
        String sp = File.separator;
        String[] dirs = path.split(sp);
        String root = "";
        for (String p : dirs) {
            String dir;
            if (root.endsWith(sp)) {
                dir = root + p;
            } else {
                root += sp;
                dir = root + p;
            }
            root = dir;
            if (!makeDir(dir)) {
                throw new IOException(String.format("create %s failed", dir));
            }
        }
    }


    /**
     * 只能传入一级不存在的路径
     */
    public static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }


    /*
     * 对于needSync为true的情况，可以不传递copyFileCallback，因为是同步的
     * return boolean表示needSync为true的情况下是否拷贝成功，true->成功，false->失败
     */
    public static boolean copyFromAssets(boolean needSync, final AssetManager assets, final String source, final String dest, final boolean isCover, final CopyFileCallback copyFileCallback) {
        try {
            if (needSync) {
                copyAssetsFileOption(assets, source, dest, isCover, copyFileCallback);
            } else {
                Schedulers.io().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            copyAssetsFileOption(assets, source, dest, isCover, copyFileCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            copyFileFailed(source, dest, copyFileCallback);
            return false;
        }
    }


    public interface CopyFileCallback {
        void copyFinish(boolean copySucceed);
    }


    private static void copyAssetsFileOption(AssetManager assets, String source, String dest, boolean isCover, CopyFileCallback copyFileCallback) throws Exception {
        File file = new File(dest);
        if (isCover || !file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                fos = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }

                if (copyFileCallback != null) {
                    copyFileCallback.copyFinish(true);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                copyFileFailed(source, dest, copyFileCallback);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        } else {
            // file一定存在于sdcard上
            long fileSize = 0;
            try {
                fileSize = obtainFileSizeNio(file);
            } catch (IOException ex) {
                ex.printStackTrace();
                LogUtils.debug(TAG, "sdcard内获取" + source + "文件大小失败");
            }
            long assetFileSize;
            try {
                assetFileSize = assets.open(source).available();
            } catch (IOException ex) {
                ex.printStackTrace();
                LogUtils.debug(TAG, "apk内没有" + source + "文件");

                if (copyFileCallback != null) {
                    if (true) {
                        copyFileCallback.copyFinish(true);
                    } else {
                        copyFileCallback.copyFinish(false);
                    }
                }
                return;
            }
            if (fileSize == assetFileSize) {
                if (copyFileCallback != null) {
                    copyFileCallback.copyFinish(true);
                }
            } else {
                copyAssetsFileOption(assets, source, dest, true, copyFileCallback);
            }
        }
    }


    private static void copyFileFailed(String source, String dest, CopyFileCallback copyFileCallback) {
        if (copyFileCallback != null) {
            copyFileCallback.copyFinish(false);
        }

        if (deleteFile(new File(dest))) {
            LogUtils.debug(TAG, "删除sdcard内" + source + "文件成功");
        } else {
            LogUtils.debug(TAG, "删除sdcard内" + source + "文件失败");
        }
    }


    /*
     * 获取文件大小的方法(单位：字节)
     * 通过FileChannel类来获取文件大小，这个方法通常结合输入流相关，
     * 因此可以用于文件网络传输时实时计算文件大小；
     * */
    public static long obtainFileSizeNio(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        return fc.size();
    }


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean syncDelete(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.debug(TAG, fileName + "为空");
            return false;
        }

        return deleteFile(new File(fileName));
    }


    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    return file.delete();
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                return file.delete();
            } else {
                return false;
            }
        }
    }


    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     */
    public static void asyncDelete(final String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.debug(TAG, fileName + "为空");
            return;
        }

        Schedulers.io().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                deleteFile(new File(fileName));
            }
        });
    }


    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LogUtils.debug(TAG, "删除单个文件" + fileName + "成功！");
                return true;
            } else {
                LogUtils.debug(TAG, "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            LogUtils.debug(TAG, "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }


    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            LogUtils.debug(TAG, "删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteSingleFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            LogUtils.debug(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            LogUtils.debug(TAG, "删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 备注：写入对象流
     */
    public static void writeObj(Context context,Object obj, String fileName) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(
                            getFilePath(context) + "/" +
                                    fileName);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);

                    //FileOutputStream fileOutputStream = context
                    //        .openFileOutput(fileName, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                    oos.writeObject(obj);

                    oos.close();
                    fileOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    /**
     * 备注：  读取对象流
     */
    public static void readObj(Context context,String fileName, final ReadFileCallBack callBack) {
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
                    File parent = new File(
                            getFilePath(context) +
                                    "/" + fileName);
                    if (!parent.exists()) {
                        //此处不需要创建文件，如果文件不存在，则说明还没有写入过；直接返回null给上层
                        callBack.complete(null);
                        return;
                    }
                    // 使用以下方法会发生java.lang.RuntimeException: java.lang.IllegalArgumentException: File /data/data/com.alex.datasave/files/user.txt contains a path separator
                    //FileInputStream fileInputStream = context.openFileInput(fileName);

                    FileInputStream fileInputStream = new FileInputStream(parent);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Object obj = objectInputStream.readObject();

                    objectInputStream.close();
                    fileInputStream.close();
                    callBack.complete(obj);
                } catch (Exception ex) {
                    //返回空回调给上层
                    callBack.complete(null);
                    ex.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取缓存大小
     *
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) throws Exception {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return StringUtils.getFormatSize(cacheSize);
    }


    /**
     * 获取文件大小
     *
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 获取Asset文件夹下的文件内容
     */
    public static String getAssetFileContent(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager
                    .open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public interface ReadFileCallBack {
        void complete(Object o);
    }
}
