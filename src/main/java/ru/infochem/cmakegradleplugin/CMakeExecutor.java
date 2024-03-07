package ru.infochem.cmakegradleplugin;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;

public class CMakeExecutor {
    private final Logger logger;

    public CMakeExecutor(Logger logger) {
        this.logger = logger;
    }

    public void exec(final Iterable<String> cmdLine, final File workingDir) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : cmdLine) {
            stringBuilder.append(arg);
            if (cmdLine.iterator().hasNext())
                stringBuilder.append(" ");
        }
        exec(stringBuilder.toString(), workingDir);
    }

    public void exec(final String cmdLine, final File workingDir) {
        if (!workingDir.exists() || !workingDir.isDirectory())
            throw new IllegalArgumentException("The provided working directory is not a valid directory.");

        ProcessBuilder processBuilder = new ProcessBuilder(cmdLine);
        processBuilder.directory(workingDir);

        try {
            Process process = processBuilder.start();
//            InputStream inputStream = process.getInputStream();
//            InputStream errorStream = process.getErrorStream();
//            Stream<String> inputLines = new BufferedReader(new InputStreamReader(inputStream)).lines();
//            Stream<String> errorLines = new BufferedReader(new InputStreamReader(errorStream)).lines();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
