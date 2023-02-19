package com.example.myplugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class MyService {
    private static final int HTTP_PORT = 8080;

    public MyService(Project project) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
            server.createContext("/", new MyHttpHandler(project));
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class MyHttpHandler implements HttpHandler {
        private final Project project;

        public MyHttpHandler(Project project) {
            this.project = project;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestURI = exchange.getRequestURI().toString();
            String filePath = parseFilePath(requestURI);
            int lineNumber = parseLineNumber(requestURI);

            if (filePath != null && lineNumber >= 0) {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(filePath));
                if (virtualFile != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (editor != null) {
                        editor.getCaretModel().moveToLogicalPosition(editor.offsetToLogicalPosition(lineNumber));
                    }
                }
                String response = "File opened: " + filePath + ", line: " + lineNumber;
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
            exchange.close();
        }

        private String parseFilePath(String requestURI) {
            // Parse the file path from the request URI
            // ...

            return filePath;
        }

        private int parseLineNumber(String requestURI) {
            // Parse the line number from the request URI
            // ...

            return lineNumber;
        }
    }
}
