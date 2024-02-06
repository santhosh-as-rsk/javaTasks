package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class replaceStringAllFiles {
    public static final String serverLocation = "/home/ubuntu/opa_mobile/";

    public static String getFilePath(String projectPath) throws IOException {
        String propertiesFile = projectPath+"src\\main\\resources\\application.properties";
        assert new File(propertiesFile).exists();
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile.replace('\\' , '/'));
        properties.load(fileInputStream);
        String loggingFile = properties.getProperty("logging.file").toString();
        return loggingFile.replace("OpaServer.log","");
    }
    static ArrayList<String> updateServerPath(File[] arr, int level, ArrayList<String> arrayList, String localPath) throws IOException {
        for (File f : arr) {
            if (f.isFile()) {
                Path path = Paths.get(f.getAbsolutePath());
                String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                if (content.contains(localPath) && !f.getPath().contains("target")) {
                    content = content.replace(localPath,serverLocation);
                    Files.write(path,content.getBytes(StandardCharsets.UTF_8));
                    System.out.println(f.getName()+" updated successfully");
                    arrayList.add(f.getName());
                }
            }
            else if (f.isDirectory()) {
                updateServerPath(f.listFiles(), level + 1, arrayList, localPath);
            }
        }
        return arrayList;
    }

    public static void updateAppVersion(String projectPath, String newVersion) throws IOException {
        String pomFilePath =  projectPath + "pom.xml";
        Path path = Paths.get(pomFilePath);
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        int versionStarts = content.indexOf("<version>") + 9;
        int versionEnds = content.indexOf("</version>");
        String version = content.substring(versionStarts,versionEnds);
        System.out.println("current version:- " + version);
        content = content.replace(version, newVersion);
        Files.write(path,content.getBytes(StandardCharsets.UTF_8));
        System.out.println(newVersion + " version updated successfully");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner objPath = new Scanner(System.in); // C:\Users\HP\Documents\OpaServer\ /mnt/c/Users/HP/Documents/OpaServer/
        System.out.println("Enter project path");
        String projectPath = objPath.nextLine();
        String localPath = getFilePath(projectPath);
        Scanner objVersion = new Scanner(System.in);
        System.out.println("Enter new version");
        String newVersion = objVersion.nextLine();
        File project = new File(projectPath);
        if (project.exists() && project.isDirectory()) {
            File [] projectArr = project.listFiles();
            assert projectArr != null;
            ArrayList<String> fileList = updateServerPath(projectArr,0, new ArrayList<>(), localPath );
            System.out.println(fileList);
        }
        else{
            System.out.println("verify the project path");
        }
        updateAppVersion(projectPath, newVersion);
    }
}
