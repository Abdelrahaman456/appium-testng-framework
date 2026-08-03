package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 🌐 Enterprise Web Configuration Portal & Test Suite Runner.
 * Provides a modern Web UI (http://localhost:8080) for business & QA teams
 * to view/edit test data (UAT & PROD) and trigger test execution.
 */
public class ConfigServer {

    private static final int PORT = 8080;
    private static final String UAT_PATH  = "src/test/resources/config/uat.properties";
    private static final String PROD_PATH = "src/test/resources/config/prod.properties";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new DashboardHandler());
        server.createContext("/save", new SaveConfigHandler());
        server.createContext("/run", new RunTestHandler());
        server.setExecutor(null);

        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║  🌐 Tree Digital Insurance — Test Config Web Dashboard Active    ║");
        System.out.println("║  Access in browser: http://localhost:" + PORT + "                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");

        server.start();
    }

    static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Properties uatProps  = loadProps(UAT_PATH);
            Properties prodProps = loadProps(PROD_PATH);

            String html = generateDashboardHtml(uatProps, prodProps);
            byte[] response = html.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    static class SaveConfigHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder formData = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    formData.append(line);
                }

                Map<String, String> params = parseFormData(formData.toString());
                String env = params.getOrDefault("env", "uat");
                String targetPath = "prod".equals(env) ? PROD_PATH : UAT_PATH;

                saveProps(targetPath, params);

                String redirectHtml = "<html><head><meta http-equiv='refresh' content='2;url=/' /></head>"
                        + "<body style='font-family:sans-serif; background:#0f172a; color:#10b981; text-align:center; padding-top:100px;'>"
                        + "<h2>✅ Config Saved Successfully for " + env.toUpperCase() + "!</h2>"
                        + "<p style='color:#94a3b8;'>Redirecting back to dashboard...</p></body></html>";

                byte[] response = redirectHtml.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            }
        }
    }

    static class RunTestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String env = "uat";
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.contains("env=prod")) env = "prod";

            final String runEnv = env;
            new Thread(() -> {
                try {
                    System.out.println("[WEB UI] Triggering TestNG Run for env: " + runEnv);
                    ProcessBuilder pb = new ProcessBuilder("mvn.cmd", "test", "-Denv=" + runEnv);
                    pb.inheritIO();
                    pb.start();
                } catch (Exception e) {
                    System.out.println("[WEB UI] Error triggering test run: " + e.getMessage());
                }
            }).start();

            String html = "<html><head><meta http-equiv='refresh' content='3;url=/' /></head>"
                    + "<body style='font-family:sans-serif; background:#0f172a; color:#3b82f6; text-align:center; padding-top:100px;'>"
                    + "<h2>🚀 Test Suite Execution Started for " + env.toUpperCase() + "!</h2>"
                    + "<p style='color:#94a3b8;'>Check terminal or IntelliJ output. Redirecting...</p></body></html>";

            byte[] response = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private static Properties loadProps(String filePath) {
        Properties p = new Properties();
        File f = new File(filePath);
        if (f.exists()) {
            try (InputStream in = Files.newInputStream(f.toPath())) {
                p.load(in);
            } catch (Exception ignored) {}
        }
        return p;
    }

    private static void saveProps(String filePath, Map<String, String> params) {
        File f = new File(filePath);
        Properties p = loadProps(filePath);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"env".equals(entry.getKey())) {
                p.setProperty(entry.getKey(), entry.getValue());
            }
        }
        try (OutputStream out = Files.newOutputStream(f.toPath())) {
            p.store(out, "Updated via Web Config Dashboard");
            System.out.println("[WEB CONFIG] Updated " + filePath + " successfully.");
        } catch (Exception e) {
            System.out.println("[WEB CONFIG] Error saving " + filePath + ": " + e.getMessage());
        }
    }

    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                map.put(k, v);
            }
        }
        return map;
    }

    private static String generateFieldHtml(String label, String key, String value) {
        return "<div class=\"form-group\"><label>" + label + "</label><input type=\"text\" name=\"" + key + "\" value=\"" + value + "\"></div>\n";
    }

    private static String generateDashboardHtml(Properties uat, Properties prod) {
        StringBuilder uatFields = new StringBuilder();
        uatFields.append(generateFieldHtml("Customer Phone Number", "default.phone", uat.getProperty("default.phone", "")));
        uatFields.append(generateFieldHtml("Customer Email", "default.email", uat.getProperty("default.email", "")));
        uatFields.append(generateFieldHtml("IBAN Number", "default.iban", uat.getProperty("default.iban", "")));
        uatFields.append(generateFieldHtml("Payment Card Number", "default.card.number", uat.getProperty("default.card.number", "")));
        uatFields.append(generateFieldHtml("Card Expiry (MM/YYYY)", "default.card.expiry", uat.getProperty("default.card.expiry", "")));
        uatFields.append(generateFieldHtml("Card CVV", "default.card.cvv", uat.getProperty("default.card.cvv", "")));
        uatFields.append(generateFieldHtml("Card Holder Name", "default.card.holder", uat.getProperty("default.card.holder", "")));
        uatFields.append(generateFieldHtml("Seller National ID", "default.seller.id", uat.getProperty("default.seller.id", "")));
        uatFields.append(generateFieldHtml("Car Model Year", "default.car.year", uat.getProperty("default.car.year", "")));

        StringBuilder prodFields = new StringBuilder();
        prodFields.append(generateFieldHtml("Customer Phone Number", "default.phone", prod.getProperty("default.phone", "")));
        prodFields.append(generateFieldHtml("Customer Email", "default.email", prod.getProperty("default.email", "")));
        prodFields.append(generateFieldHtml("IBAN Number", "default.iban", prod.getProperty("default.iban", "")));
        prodFields.append(generateFieldHtml("Payment Card Number", "default.card.number", prod.getProperty("default.card.number", "")));
        prodFields.append(generateFieldHtml("Card Expiry (MM/YYYY)", "default.card.expiry", prod.getProperty("default.card.expiry", "")));
        prodFields.append(generateFieldHtml("Card CVV", "default.card.cvv", prod.getProperty("default.card.cvv", "")));
        prodFields.append(generateFieldHtml("Card Holder Name", "default.card.holder", prod.getProperty("default.card.holder", "")));
        prodFields.append(generateFieldHtml("Seller National ID", "default.seller.id", prod.getProperty("default.seller.id", "")));
        prodFields.append(generateFieldHtml("Car Model Year", "default.car.year", prod.getProperty("default.car.year", "")));

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Tree Digital Insurance — Test Config Dashboard</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #0f172a; color: #f8fafc; margin: 0; padding: 20px; }
                    .container { max-width: 900px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 30px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
                    h1 { color: #10b981; margin-top: 0; display: flex; align-items: center; gap: 10px; }
                    .tabs { display: flex; gap: 10px; margin-bottom: 25px; border-bottom: 2px solid #334155; padding-bottom: 10px; }
                    .tab-btn { background: #334155; color: #94a3b8; border: none; padding: 10px 20px; border-radius: 6px; font-weight: bold; cursor: pointer; }
                    .tab-btn.active { background: #3b82f6; color: white; }
                    .form-section { display: none; }
                    .form-section.active { display: block; }
                    .form-group { margin-bottom: 18px; }
                    label { display: block; font-size: 13px; color: #94a3b8; margin-bottom: 6px; font-weight: 600; }
                    input[type="text"] { width: 100%; padding: 10px; background: #0f172a; border: 1px solid #475569; border-radius: 6px; color: #f8fafc; font-size: 14px; box-sizing: border-box; }
                    input[type="text"]:focus { border-color: #3b82f6; outline: none; }
                    .btn-group { display: flex; gap: 15px; margin-top: 30px; }
                    .btn-save { background: #10b981; color: white; border: none; padding: 12px 25px; border-radius: 6px; font-weight: bold; cursor: pointer; font-size: 15px; }
                    .btn-run  { background: #8b5cf6; color: white; border: none; padding: 12px 25px; border-radius: 6px; font-weight: bold; cursor: pointer; font-size: 15px; text-decoration: none; display: inline-block; }
                    .btn-save:hover { background: #059669; }
                    .btn-run:hover  { background: #7c3aed; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🌳 Tree Digital Insurance — Test Config Manager</h1>
                    <p style="color: #94a3b8;">Manage environment test data and trigger automation runs directly from your browser.</p>

                    <div class="tabs">
                        <button class="tab-btn active" onclick="switchTab('uat')">🧪 UAT Config</button>
                        <button class="tab-btn" onclick="switchTab('prod')">⚠️ PROD Config</button>
                    </div>

                    <!-- UAT FORM -->
                    <div id="uat-section" class="form-section active">
                        <form action="/save" method="POST">
                            <input type="hidden" name="env" value="uat">
                            """ + uatFields.toString() + """
                            <div class="btn-group">
                                <button type="submit" class="btn-save">💾 Save UAT Config</button>
                                <a href="/run?env=uat" class="btn-run">🚀 Run UAT Test Suite</a>
                            </div>
                        </form>
                    </div>

                    <!-- PROD FORM -->
                    <div id="prod-section" class="form-section">
                        <form action="/save" method="POST">
                            <input type="hidden" name="env" value="prod">
                            """ + prodFields.toString() + """
                            <div class="btn-group">
                                <button type="submit" class="btn-save" style="background:#ef4444;">💾 Save PROD Config</button>
                                <a href="/run?env=prod" class="btn-run" style="background:#dc2626;">⚠️ Run PROD Test Suite</a>
                            </div>
                        </form>
                    </div>
                </div>

                <script>
                    function switchTab(env) {
                        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
                        document.querySelectorAll('.form-section').forEach(s => s.classList.remove('active'));
                        if (env === 'uat') {
                            document.querySelectorAll('.tab-btn')[0].classList.add('active');
                            document.getElementById('uat-section').classList.add('active');
                        } else {
                            document.querySelectorAll('.tab-btn')[1].classList.add('active');
                            document.getElementById('prod-section').classList.add('active');
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }
}
