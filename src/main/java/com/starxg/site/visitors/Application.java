package com.starxg.site.visitors;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

import org.apache.commons.cli.*;

import com.starxg.site.visitors.counter.Counter;
import com.starxg.site.visitors.counter.MemoryCounter;
import com.starxg.site.visitors.counter.MemoryWithSyncerCounter;
import com.starxg.site.visitors.handler.BadgeHandler;
import com.starxg.site.visitors.handler.JsonHandler;
import com.starxg.site.visitors.handler.RedirectHandler;
import com.starxg.site.visitors.handler.TextHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Application
 * 
 * @author huangxingguang
 */
public class Application {
    private static final Options OPTIONS;
    static {
        OPTIONS = new Options();
        OPTIONS.addOption("h", "usage help");
        OPTIONS.addOption(Option.builder("l").hasArg().type(String.class)
                .desc("Listen on address <addr> (default is 0.0.0.0)").build());
        OPTIONS.addOption(Option.builder("p").hasArg().type(Integer.TYPE)
                .desc("Listen on TCP port <port> (default is 80)").build());
        OPTIONS.addOption(Option.builder("b").hasArg().type(String.class).desc("Save binlog to a <file>").build());
    }

    public static void main(String[] args) throws Exception {
        CommandLine commandLine;

        try {
            commandLine = new DefaultParser().parse(OPTIONS, args);
            if (commandLine.hasOption("h")) {
                printHelpString(System.out);
                System.exit(0);
                return;
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelpString(System.err);
            System.exit(1);
            return;
        }

        final String host = commandLine.getOptionValue("l", "0.0.0.0");
        final int port = Integer.parseInt(commandLine.getOptionValue("p", "80"));
        final Counter counter;
        if (commandLine.hasOption("b")) {
            counter = new MemoryWithSyncerCounter(new File(commandLine.getOptionValue("b")));
        } else {
            counter = new MemoryCounter();
            System.out.println("Data will be lost at the end of the program.");
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext("/", new RedirectHandler());
        server.createContext("/json", new JsonHandler(counter));
        server.createContext("/text", new TextHandler(counter));
        server.createContext("/badge", new BadgeHandler(counter));

        server.start();
    }

    private static void printHelpString(OutputStream out) {
        try (PrintWriter pw = new PrintWriter(out);) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, "scp -h", null, OPTIONS,
                    HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            pw.flush();
        }
    }

}
