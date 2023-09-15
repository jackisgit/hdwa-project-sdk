package com.hdwa.sdk.utils;

import com.hdwa.sdk.constant.BaseDecConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author abao
 * @since 2023/8/10
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 只保留3个版本文件目录数据，老版本删除掉
     *
     * @return
     */
    public static void clearHistoryDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] subdirectories = directory.listFiles(File::isDirectory);

            if (subdirectories != null && subdirectories.length > 3) {
                // 按文件名称降序排序
                Arrays.sort(subdirectories, Comparator.comparing(File::getName).reversed());
                // 保留前三个子文件夹，删除其他
                for (int i = 3; i < subdirectories.length; i++) {
                    log.warn("*****delete：" + subdirectories[i].getPath());
                    deleteDirectory(subdirectories[i]);
                }
            }
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param directory
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }


    /**
     * 修改temp目录到当前时间目录
     *
     * @param temp
     */
    public static void tempToNowDate(File temp, File physicalPath) {
        File nowFile = new File(physicalPath + File.separator + BaseDecConstant.DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        temp.renameTo(nowFile);
        log.warn("*****rename：" + temp.getPath() + "----->" + nowFile.getPath());
    }

    /**
     * 递归删除文件
     *
     * @param file
     */
    public static void deleteRecursive(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            for (File f : fs) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteRecursive(f);
                    f.delete();
                }
            }
        }
        file.delete();
    }

    /**
     * 下载文件
     *
     * @param filename
     * @param content
     * @throws Exception
     */
    public static void save(String filename, String content) throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(filename)), StandardCharsets.UTF_8));
        writer.write(content);
        writer.close();
    }


    /**
     * 获取最新的文件夹
     *
     * @param directory
     * @return
     */
    public static File getMaxDir(File directory) {
        File[] subdirectories = directory.listFiles(File::isDirectory);
        File resultFile;
        if (subdirectories != null && subdirectories.length > 0) {
            // 按文件名称降序排序
            Arrays.sort(subdirectories, Comparator.comparing(File::getName).reversed());
            resultFile = subdirectories[0];
            //临时目录排除
            if (resultFile.getName().equals(BaseDecConstant.TEMP)) {
                log.error("未找到文件夹：" + directory.getPath());
                return null;
            }else{
                return resultFile;
            }
        } else {
            log.error("未找到文件夹：" + directory.getPath());
        }
        return null;
    }
}
