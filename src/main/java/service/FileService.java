package service;


import com.opencsv.CSVReader;
import io.undertow.server.handlers.form.FormData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;


public class FileService {
    public void writeToFile2(File formValue,
                             String folderPath) {
        try {
            File file = new File(String.valueOf(formValue));    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);      //appends line to string buffer
                sb.append("\n");     //line feed
            }
            fr.close();    //closes the stream and release the resources

            createFolderIfNotExists(folderPath);
            String filepath = createImageName(folderPath);
            File outputfile = new File(filepath);
            Scanner scanner = new Scanner(formValue);

            FileWriter fileWriter = new FileWriter(outputfile);

            fileWriter.write(String.valueOf(sb));

            fileWriter.close();
            outputfile.exists();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean createFolderIfNotExists(String dirName)
            throws SecurityException {
        File theDir = new File(dirName);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        return true;
    }

    public static String createImageName(String filepath) {
        Random rnd = new Random();
        int sayi = rnd.nextInt(999999999);
        File f = new File(filepath + "/" + String.valueOf(sayi) + ".xls");
        while (f.exists()) {
            sayi = rnd.nextInt(999999999);
            f = new File(filepath + "/" + String.valueOf(sayi) + ".xls");
        }
        return f.getPath();
    }



}

