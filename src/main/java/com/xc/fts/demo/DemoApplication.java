package com.xc.fts.demo;

import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Objects;

@RestController
@SpringBootApplication
public class DemoApplication {

    @Autowired
    private IdWorker idWorker;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * @param file        切片内容
     * @param chunkNumber 当前块编号
     * @param totalChunks 总分片数量
     * @param identifier  这个就是每个文件的唯一标示。
     * @return 返回值
     * @throws IOException 异常
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public Object post(MultipartFile file, Integer chunkNumber, Integer totalChunks, String identifier, String relativePath) throws IOException {

        System.out.println(chunkNumber + "/" + totalChunks);

        String tmp = System.getProperty("java.io.tmpdir");

        String name = identifier + "_" + chunkNumber;

        File tmp_file = new File(tmp, name);

        file.transferTo(tmp_file);

        return "";
    }

    @RequestMapping(value = "upload", method = RequestMethod.OPTIONS)
    public Object options(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        return "";
    }

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public Object get(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        return "";
    }

    @RequestMapping(value = "complete", method = RequestMethod.GET)
    public Object complete(String uniqueIdentifier, String name, Integer chunkSize) {

        //最后整合，做后续业务
        String tmp = System.getProperty("java.io.tmpdir");

        String finalFilePath = tmp + File.separator + idWorker.nextId() + "@" + name;

        String[] tmpFiles = new String[chunkSize];

        for (int i = 0; i < chunkSize; i++) {
            tmpFiles[i] = tmp + File.separator + uniqueIdentifier + "_" + (i + 1);
        }

        boolean isOk = mergeFiles(tmpFiles, finalFilePath);

        if (isOk) {
            //TODO:拿文件说点事吧
        }

        return "";
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public Object download(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    private static boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || Strings.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return new File(fpaths[0]).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i++) {
            files[i] = new File(fpaths[i]);
            if (Strings.isEmpty(fpaths[i]) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.length; i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        for (int i = 0; i < fpaths.length; i++) {
            files[i].delete();
        }
        return true;
    }
}
