package org.example;

import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageDownloaderThread {

    public static void insertUrls(String urls, String saveFolder) {
        // 정규 표현식 패턴
        String pattern = "//www[^'\"].+?\\.jpg";

        // 정규 표현식에 맞는 문자열 찾기
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(urls);

        ExecutorService executor = Executors.newFixedThreadPool(10); // 최대 10개의 스레드 사용

        File folder = new File(saveFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }

        int index = 0;
        while (m.find()) {
            String url = "https:" + m.group(0).replaceAll("[\"']", "");
            //System.out.println("Found URL: " + url);

            String destinationFile = saveFolder + "/" + (++index) + ".jpg";

            // 이미지 다운로드 작업을 스레드로 전달
            executor.execute(new ImageDownloadTask(url, destinationFile));
        }

        executor.shutdown(); // 작업이 완료되면 스레드 풀 종료
    }

    private static class ImageDownloadTask implements Runnable {
        private String imageUrl;
        private String destinationFile;

        public ImageDownloadTask(String imageUrl, String destinationFile) {
            this.imageUrl = imageUrl;
            this.destinationFile = destinationFile;
        }

        @Override
        public void run() {
            try {
                downloadImage(imageUrl, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void downloadImage(String imageUrl, String destinationFile) throws IOException {
            try (InputStream in = new URL(imageUrl).openStream();
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                //System.out.println("Image downloaded: " + destinationFile);
            } catch (IOException e) {
                System.out.println("Failed to download image: " + imageUrl);
                throw e; // 다운로드 실패시 예외 던지기
            }
        }
    }
}
