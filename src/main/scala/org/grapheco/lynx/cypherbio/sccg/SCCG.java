package org.grapheco.lynx.cypherbio.sccg;


import org.grapheco.lynx.cypherbio.sccg.methods.SCCGC;
import org.grapheco.lynx.cypherbio.sccg.methods.SCCGD;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SCCG {
    /***
     * return mid position sequence
     * @param targetFilePath tar
     * @param referenceFilePath ref
     * @return mid position sequence
     * @throws IOException _
     * @throws InterruptedException _
     */
    public String compress(String targetFilePath, String referenceFilePath) throws IOException, InterruptedException {

        File file = new File(targetFilePath);
        String targetParentDirectory = file.getParent();

        String tempPath = targetParentDirectory + "/temp";
        Path tempFolder = Paths.get(tempPath);

        if (!Files.exists(tempFolder)) {
            Files.createDirectory(tempFolder);
        }

        SCCGC.compress(referenceFilePath,targetFilePath,tempPath);

        String fileName = file.getName();
        String zipFileName = tempPath+"/"+fileName+".7z";

        SCCGD.use7zip(zipFileName,tempPath);

        try (BufferedReader reader = new BufferedReader(new FileReader(targetFilePath))) {
            reader.readLine();
            reader.readLine();

            StringBuilder mergedContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                mergedContent.append(line).append(System.lineSeparator());
            }

            // delete temp folder
            deleteFolder(tempFolder);

            return mergedContent.toString();
        }
    }

    /***
     * uncompress
     * notice:
     *      1、line space = 50
     * @param referencePath ref path
     * @param targetInformation tar information->first line
     * @param midPositionCode mid-position code
     * @param resultPath result path
     *
     */
    public void uncompress(String referencePath, String targetInformation,String midPositionCode, String resultPath) throws IOException, InterruptedException {
        String result = targetInformation + "\n50\n"+midPositionCode;

        File file = new File(resultPath);
        String targetParentDirectory = file.getParent();
        String fileName = file.getName();

        String tempPath = targetParentDirectory + "/temp";
        Path tempFolder = Paths.get(tempPath);

        if (!Files.exists(tempFolder)) {
            Files.createDirectory(tempFolder);
        }

        String tempFile = tempPath+"/"+fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SCCGD.uncompress(referencePath,tempFile,targetParentDirectory);


    }



    /***
     * The compressed result is stored in the temp file in the same directory as the target file, and the content of the compressed result is returned
     *
     * @param referencePath reference file path
     * @param targetPath target file path
     * @param tempPath temp file path -> target dict + /temp
     * @return list<String> -> postion encode file
     * @throws IOException -
     * @throws InterruptedException -
     */
    public List<String> compress(String referencePath, String targetPath,String tempPath)
            throws IOException, InterruptedException {

        SCCGC.compress(referencePath,targetPath,tempPath);

        File file = new File(targetPath);
        String fileName = file.getName();

        String zipPath = tempPath+"/"+fileName+".7z";
        SCCGD.use7zip(zipPath, tempPath);

        Stream<String> lines = Files.lines(Paths.get(tempPath+"/"+fileName));
        List<String> filteredLines = lines
                .skip(2)
                .collect(Collectors.toList());

        lines.close();

        return filteredLines;
    }



    /*** de
     *
     * @param referencePath reference path
     * @param compressedResult compressed result -> List<String>
     * @param resultPath result path
     * @return sequence
     * @throws IOException -
     * @throws InterruptedException -
     */
    public String uncompress(String referencePath, String resultPath, List<String> compressedResult,String information,String fileName)
            throws IOException, InterruptedException {

        //
        /***
         * todo
         * write compressed result to temp file(file path -> result path)
         * get file path
         * uncompress
         * get sequence file and file path
         */

        // write compressed result to a temp file

        String tempDict = resultPath+"/temp";
        String path = tempDict+"/"+fileName;
        try {
            if (!Files.exists(Paths.get(tempDict))) {
                Files.createDirectories(Paths.get(tempDict));
            }
            if (!Files.exists(Paths.get(path))) {
                Files.createFile(Paths.get(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        write(compressedResult,path,information);

        String targetSequence;


        SCCGD.uncompress(referencePath, path, resultPath);
        String result = resultPath+"/"+fileName;

        targetSequence = read(result);

        return targetSequence;
    }

    /** de
     * write sequence to temp file
     *
     * @param sequence sequence
     * @param flag     True -> write reference sequence, check whether the file exists, if exists break,the file is processed if it does not exist ; False -> write target sequence
     */
    public static void processSequence(String sequence, Boolean flag) {

        String filePath;

        if (flag) {
            filePath = "./reference.fa";
        } else {
            filePath = "./target.fa";
        }

        try {
            FileWriter fileWriter = new FileWriter(filePath);

            if (flag) {
                fileWriter.write(">reference\n");
            } else {
                fileWriter.write(">target\n");
            }

            int length = sequence.length();
            for (int i = 0; i < length; i += 50) {
                if (i + 50 < length) {
                    fileWriter.write(sequence.substring(i, i + 50) + "\n");
                } else {
                    fileWriter.write(sequence.substring(i) + "\n");
                }
            }

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param filteredLines _
     * @param filePath _
     *
     */
    public static void write(List<String> filteredLines, String filePath, String information) {
        filteredLines.add(0, information);
        filteredLines.add(1, "50");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            for (String line : filteredLines) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     *
     * @param filePath _
     * @return result
     */
    public static String read(String filePath) {
        String data = "";
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filePath));

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(">")) {
                    content.append(line);
                }
            }

            reader.close();

            data = content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /***
     * delete folder & all file in folder
     * @param folder  _
     */
    private static void deleteFolder(Path folder) {
        try {
            Files.walk(folder)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
