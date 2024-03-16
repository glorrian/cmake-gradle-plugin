package dev.infochem.cmakegradleplugin;

import org.gradle.api.GradleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Executor implementation for running CMake commands.
 *
 * @version 1.0
 */
public class CMakeExecutor {
    private final Logger logger = LoggerFactory.getLogger(CMakeExecutor.class);
    private final String PREFIX;

    /**
     * Accepts an argument to define the task for which the command will be executed.
     * The class object is used only for logging
     *
     * @param _execClass {@link java.lang.Class} label for logger messages
     */
    public CMakeExecutor(Class<?> _execClass) {
        PREFIX = "[%s EXECUTOR] ".formatted(_execClass.getSimpleName());
    }

    /**
     * A method for executing a command in a specific build directory.
     *
     * @param cmdLine A {@link java.util.List} containing command line arguments. Each argument must be defined in a separate list item. Spaces inside arguments are not allowed
     * @param workingDir A {@link java.io.File} of the directory where the commands are executed
     */
    public void execute(final List<String> cmdLine, final File workingDir) {
        if (!workingDir.exists() || !workingDir.isDirectory())
            throw new IllegalArgumentException(PREFIX + "The provided working directory is not a valid directory.");

        ProcessBuilder processBuilder = new ProcessBuilder(cmdLine);
        logger.debug("{}Setup ProcessBuilder", PREFIX);
        logger.warn("{}Setup ProcessBuilder with \"{}\" command", PREFIX, cmdLine);
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


    /**
     * A callable class for use in {@link java.util.concurrent.ThreadPoolExecutor}
     * Used to print information from input streams (Basic input stream and error input stream)
     */
    private static class StreamPrintTask implements Callable<Void> {
        private final InputStream stream;
        private final Consumer<? super String> executable;

        /**
         * Accepts an input stream and a function to print this stream
         * @param _stream {@link java.io.InputStream} provides information from command execution
         * @param _executable A function with {@link java.util.function.Consumer} type to print information from {@link java.io.InputStream}
         */
        public StreamPrintTask(InputStream _stream, Consumer<? super String> _executable) {
            stream = _stream;
            executable = _executable;
        }

        /**
         * Method for using in {@link java.util.concurrent.ThreadPoolExecutor#submit(Callable)  }
         * @return {@link java.lang.Void}
         */
        @Override
        public Void call() {
            executeStream(stream, executable);
            return null;
        }

        /**
         * Print all information from {@link java.io.InputStream} using {@link java.io.BufferedReader}
         * @param inputStream {@link java.io.InputStream} provides information from command execution
         * @param exec A function with {@link java.util.function.Consumer} type to print information from {@link java.io.InputStream}
         */
        private void executeStream(InputStream inputStream, Consumer<? super String> exec) {
            Stream<String> inputLines = new BufferedReader(new InputStreamReader(inputStream)).lines();
            inputLines.forEach(exec);
        }
    }
}
