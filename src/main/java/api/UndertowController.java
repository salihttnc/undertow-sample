package api;

import io.undertow.Handlers;
import io.undertow.Undertow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import model.User;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.FileService;
import service.UserService;

import java.nio.file.Paths;
import java.util.Deque;
import java.util.List;
import java.util.Map;


public class UndertowController {
    UserService userService = new UserService();
    FileService fileService = new FileService();

    public void start() {
        Undertow.builder().addHttpListener(8080, "localhost")
                .setHandler(new BlockingHandler(Handlers.path()

                        // REST API path
                        .addPrefixPath("/api", Handlers.routing()
                                .get("/get", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                        List<User> list = userService.getUsers();
                                        httpServerExchange.getResponseSender().send(list.toString());
                                    }
                                })
                                .delete("/delete/{id}", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                        httpServerExchange.getResponseSender().send("customer delete");
                                        Map<String, Deque<String>> queryParams = httpServerExchange.getQueryParameters();

                                        Deque<String> parameter = queryParams.get("id");
                                        String apiKey = parameter.getFirst();
                                        userService.delete(apiKey);

                                    }
                                })
                                .post("/{id}/{name}", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                        httpServerExchange.getResponseSender().send("customer");
                                        Map<String, Deque<String>> queryParams = httpServerExchange.getQueryParameters();

                                        Deque<String> parameter = queryParams.get("name");
                                        String apiKey = parameter.getFirst();
                                        userService.addDataBase(apiKey);

                                    }

                                })
                                .post("/fileUpload", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "multipart/form-data");

                                        FormParserFactory.Builder builder = FormParserFactory.builder();

                                        final FormDataParser formDataParser = builder.build().createParser(httpServerExchange);
                                        if (formDataParser != null) {
                                            httpServerExchange.startBlocking();
                                            FormData formData = formDataParser.parseBlocking();

                                            for (String data : formData) {
                                                for (FormData.FormValue formValue : formData.get(data)) {
                                                    if (formValue.isFile()) {
                                                        // process file here: formValue.getFile();
                                                        String uploadedFileLocation = "/home/altosis/Documents/" ;
                                                        fileService.writeToFile2(formValue.getFile(), uploadedFileLocation);
                                                    }
                                                }
                                            }
                                        }

                                    }


                                })


                                .setFallbackHandler(new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {

                                    }
                                }))
                        .addPrefixPath("/apiV2", Handlers.routing()

                                .post("/json", (ex) -> {

                                    ex.getRequestReceiver().receiveFullString((e, m) -> {
                                        userService.addDataBaseWithJSON(m);
                                    });
                                    ex.getResponseSender().send("Hello World");

                                })
                                .put("/deneme/{pathVariable}", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                        httpServerExchange.getResponseSender().send("customer delete");
                                    }
                                }))

                        // Redirect root path to /static to serve the index.html by default
                        .addExactPath("/", Handlers.redirect("/static"))


                        // Serve all static files from a folder
                        .addPrefixPath("/static", new ResourceHandler(
                                new PathResourceManager(Paths.get("/home/altosis/Downloads/undertow-sample/src/main/resources/index.html"), 100))
                                .setWelcomeFiles("index.html"))

                )).build().start();

    }

}
/*
 .post("/fileUpload", new HttpHandler() {
                                    @Override
                                    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
                                        httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                                        httpServerExchange.getResponseSender().send("customer");
                                        FormParserFactory.Builder builder = FormParserFactory.builder();
                                        final FormDataParser formDataParser = builder.build().createParser(httpServerExchange);
                                        if (formDataParser != null) {
                                            httpServerExchange.startBlocking();
                                            FormData formData = formDataParser.parseBlocking();

                                            for (String data : formData) {
                                                for (FormData.FormValue formValue : formData.get(data)) {
                                                    if (formValue.isFile()) {

                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
 */
/*
 Builder builder = FormParserFactory.builder();

    final FormDataParser formDataParser = builder.build().createParser(exchange);
    if (formDataParser != null) {
        exchange.startBlocking();
        FormData formData = formDataParser.parseBlocking();

        for (String data : formData) {
            for (FormData.FormValue formValue : formData.get(data)) {
                if (formValue.isFile()) {
                    // process file here: formValue.getFile();
                }
            }
        }
    }
 */