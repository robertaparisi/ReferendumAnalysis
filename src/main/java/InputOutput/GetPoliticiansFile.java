/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InputOutput;

import static InputOutput.TweetIndex.sourcenames_directory;
import java.io.BufferedReader;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Roberta
 */
public class GetPoliticiansFile {

    public static void decompress(String file_name, String dir) throws IOException {
        String politician;
        Set<String> politicians = new HashSet<>();
        
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(file_name))){
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.getSize()>1024){
                    Reader file_reader = new InputStreamReader(fin, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(file_reader);  
                    br.readLine();                  
                    while ((politician = br.readLine())!= null) {
                        String tweetter_screenname =politician.split(",")[4];
                        if (tweetter_screenname.length()>0){
                            politicians.add(tweetter_screenname);
                        }
                        

                    }
                }

            }
        }
        
        try (PrintWriter writer = new PrintWriter(dir + "politicians.txt", "UTF-8")) {
            writer.print(String.join(",", politicians));
        }
//        return (politicians);
    }
    
    public static void main (String[] args) throws IOException{
        
        decompress(sourcenames_directory + "sourcenames.tar", sourcenames_directory );
    }
}
