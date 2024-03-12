package dev.infochem.cmakegradleplugin;

import org.gradle.api.GradleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CMakeExecutor {
    private final Logger logger = LoggerFactory.getLogger(CMakeExecutor.class);

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
        logger.debug("Setup ProcessBuilder with \"%s\" command".formatted(cmdLine));
        processBuilder.directory(workingDir);
        logger.debug("Setup directory(\"%s\") to ProcessBuilder".formatted(workingDir.getAbsolutePath()));

        try {
            Process process = processBuilder.start();
            logger.info("Starting Process - %s".formatted(process));

            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            if (errorStream.readAllBytes().length != 0) {
                execStream(errorStream, logger::error);
                throw new GradleException("Error of Cmake execution");
            }
            execStream(inputStream, logger::info);
        } catch (IOException e) {
            throw new GradleException(e.getMessage());
        }
    }

    private void execStream(final InputStream inputStream, Consumer<? super String> exec) {
        Stream<String> inputLines = new BufferedReader(new InputStreamReader(inputStream)).lines();
        inputLines.forEach(exec);
    }
}
