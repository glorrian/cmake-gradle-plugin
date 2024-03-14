package dev.infochem.cmakegradleplugin;

import org.gradle.api.GradleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CMakeExecutor {
    private final Logger logger = LoggerFactory.getLogger(CMakeExecutor.class);
    private final String PREFIX;

    public CMakeExecutor(Class<?> _execClass) {
        PREFIX = "[%s EXECUTOR] ".formatted(_execClass);
    }

    public void execute(final List<String> cmdLine, final File workingDir) {
        if (!workingDir.exists() || !workingDir.isDirectory())
            throw new IllegalArgumentException(PREFIX + "The provided working directory is not a valid directory.");

        ProcessBuilder processBuilder = new ProcessBuilder(cmdLine);
        logger.debug("{}Setup ProcessBuilder", PREFIX);
        logger.debug("{}Setup ProcessBuilder with \"{}\" command", PREFIX, cmdLine);
        processBuilder.directory(workingDir);
        logger.debug("{}Setup directory(\"{}\") to ProcessBuilder", PREFIX, workingDir.getAbsolutePath());

        try {
            Process process = processBuilder.start();
            logger.info("{}Starting Process - {}", PREFIX, process);
            ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
            Future<Void> inputStreamFuture = executorService.submit(new StreamPrintTask(process.getInputStream(), logger::info));
            Future<Void> errorStreamFuture = executorService.submit(new StreamPrintTask(process.getErrorStream(), logger::error));
            int exitCode = process.waitFor();
            try {
                inputStreamFuture.get(3, TimeUnit.SECONDS);
                errorStreamFuture.get(3, TimeUnit.SECONDS);
            } catch (ExecutionException | TimeoutException e) {
                logger.error(PREFIX + "Timed out waiting for InputStream to be closed.");
            }
            if (exitCode != 0) {
                throw new GradleException(PREFIX + "Command execution failed with an error(return code %d)".formatted(exitCode));
            }
        } catch (IOException | InterruptedException e) {
            throw new GradleException(PREFIX + "An exception occurred during the execution of the command", e);
        }


    }

    private static class StreamPrintTask implements Callable<Void> {
        private final InputStream stream;
        private final Consumer<? super String> executable;
        public StreamPrintTask(InputStream _stream, Consumer<? super String> _executable) {
            stream = _stream;
            executable = _executable;
        }
        @Override
        public Void call() {
            executeStream(stream, executable);
            return null;
        }
        private void executeStream(InputStream inputStream, Consumer<? super String> exec) {
            Stream<String> inputLines = new BufferedReader(new InputStreamReader(inputStream)).lines();
            inputLines.forEach(exec);
        }


    }
}
