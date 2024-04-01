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
     * @param execClass {@link java.lang.Class} label for logger messages
     */
    public CMakeExecutor(Class<?> execClass) {
        PREFIX = "[%s EXECUTOR] ".formatted(execClass.getSimpleName());
    }

    /**
     * A method for executing a command in a specific build directory.
     *
     * @param cmdLine List containing command line arguments. Each argument must be defined in a separate list item. Spaces inside arguments are not allowed
     * @param workingDir Directory where the commands are executed
     */
    public void execute(List<String> cmdLine, File workingDir) {
        execute(cmdLine, workingDir, logger::info, logger::error);
    }

    /**
     * A method for executing a command in a specific build directory with custom print functions.
     *
     * @param cmdLine List containing command line arguments. Each argument must be defined in a separate list item. Spaces inside arguments are not allowed
     * @param workingDir Directory where the commands are executed
     * @param inputPrintAction Function for printing the output of the execution
     * @param errorPrintAction Function for printing output of execution errors
     */
    public void execute(final List<String> cmdLine, final File workingDir,
                        Consumer<? super String> inputPrintAction, Consumer<? super String> errorPrintAction) {
        if (!workingDir.exists() || !workingDir.isDirectory())
            throw new IllegalArgumentException(PREFIX + "The provided working directory is not a valid directory.");
        ProcessBuilder processBuilder = new ProcessBuilder(cmdLine);
        processBuilder.directory(workingDir);
        logger.debug(PREFIX + "Setup ProcessBuilder with \"{}\" command in {} directory", cmdLine, workingDir.getAbsolutePath());
        try {
            Process process = processBuilder.start();
            logger.info(PREFIX + "Starting Process - {}", process);
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            printStream(executorService, new StreamPrintService(process.getInputStream(), inputPrintAction));
            printStream(executorService, new StreamPrintService(process.getErrorStream(), errorPrintAction));
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new GradleException(PREFIX + "Command execution failed with an error(return code %d)".formatted(exitCode));
            }
        } catch (IOException | InterruptedException e) {
            throw new GradleException(PREFIX + "An exception occurred during the execution of the command", e);
        }
    }

    private void printStream(ExecutorService executorService, StreamPrintService printService) {
        Future<Void> streamFuture = executorService.submit(printService);
        throwIfTimeOut(streamFuture);
    }

    private void throwIfTimeOut(Future<?> future) {
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            logger.warn(PREFIX + "Timed out waiting for input stream to be closed.");
        }
    }

    /**
     * A callable class for use in {@link java.util.concurrent.ThreadPoolExecutor}
     * Used to print information from input streams (Basic input stream and error input stream)
     */
    public static class StreamPrintService implements Callable<Void> {
        private final InputStream stream;
        private final Consumer<? super String> executable;

        /**
         * Accepts an input stream and a function to print this stream
         * @param stream {@link java.io.InputStream} provides information from command execution
         * @param executable A function with {@link java.util.function.Consumer} type to print information from {@link java.io.InputStream}
         */
        public StreamPrintService(InputStream stream, Consumer<? super String> executable) {
            this.stream = stream;
            this.executable = executable;
        }

        /**
         * Method for using in {@link java.util.concurrent.ThreadPoolExecutor#submit(Callable)  }
         */
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
