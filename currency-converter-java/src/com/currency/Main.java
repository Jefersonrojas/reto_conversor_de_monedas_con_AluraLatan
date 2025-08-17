package com.currency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static class Config {
        String API_KEY;
        String BASE;
        String API_URL_TEMPLATE;
        int TIMEOUT_SECONDS;
    }

    public static void main(String[] args) {
        ensureConfigExists();
        Config cfg = readConfig();
        ApiClient client = new ApiClient(cfg.API_KEY, cfg.BASE, cfg.API_URL_TEMPLATE, cfg.TIMEOUT_SECONDS);
        String json;
        try {
            json = client.fetchRates();
        } catch (Exception e) {
            System.out.println("No se pudo consultar la API: " + e.getMessage());
            System.out.println("Si tu firewall bloquea la salida, o no tienes internet, el programa terminará.");
            return;
        }
        CurrencyResponse data = new CurrencyResponse(json);
        if (data.rates == null || data.rates.isEmpty()) {
            System.out.println("No se obtuvieron tasas de cambio. Revisa tu API key y moneda base en config.json");
            return;
        }
        Scanner sc = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("#,##0.0000");
        while (true) {
            System.out.println("\n=== CONVERSOR DE MONEDAS ===");
            System.out.println("Base: " + (data.baseCode != null ? data.baseCode : cfg.BASE) + (data.lastUpdateUtc.isEmpty() ? "" : (" | Actualizado: " + data.lastUpdateUtc)));
            System.out.println("1) Ver total de monedas disponibles");
            System.out.println("2) Filtrar por prefijo (ej. 'US', 'PE')");
            System.out.println("3) Buscar por texto (contiene) ");
            System.out.println("4) Top N monedas más comunes (USD, EUR, GBP, JPY, PEN, BRL, MXN, ARS, CLP, COP)");
            System.out.println("5) Convertir monto entre dos monedas");
            System.out.println("6) Exportar resultados filtrados a salida.txt");
            System.out.println("7) Cambiar moneda base y actualizar");
            System.out.println("0) Salir");
            System.out.print("Elige una opción: ");
            String op = sc.nextLine().trim();
            if (op.equals("0")) break;
            switch (op) {
                case "1":
                    System.out.println("Monedas disponibles (" + data.rates.size() + "):");
                    printList(new ArrayList<>(data.rates.keySet()));
                    break;
                case "2":
                    System.out.print("Prefijo (p.ej. 'US' o 'PE'): ");
                    String pref = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    List<String> byPref = data.rates.keySet().stream()
                            .filter(k -> k.startsWith(pref))
                            .sorted()
                            .collect(Collectors.toList());
                    printWithValues(byPref, data.rates, df);
                    writeReport("salida.txt", byPref, data, df);
                    System.out.println("Guardado en salida.txt");
                    break;
                case "3":
                    System.out.print("Texto a buscar (contiene): ");
                    String txt = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    List<String> byContains = data.rates.keySet().stream()
                            .filter(k -> k.contains(txt))
                            .sorted()
                            .collect(Collectors.toList());
                    printWithValues(byContains, data.rates, df);
                    writeReport("salida.txt", byContains, data, df);
                    System.out.println("Guardado en salida.txt");
                    break;
                case "4":
                    List<String> commons = Arrays.asList("USD","EUR","GBP","JPY","PEN","BRL","MXN","ARS","CLP","COP");
                    List<String> top = commons.stream().filter(data.rates::containsKey).collect(Collectors.toList());
                    printWithValues(top, data.rates, df);
                    writeReport("salida.txt", top, data, df);
                    System.out.println("Guardado en salida.txt");
                    break;
                case "5":
                    System.out.print("Monto: ");
                    String sAmt = sc.nextLine().trim();
                    double amount;
                    try { amount = Double.parseDouble(sAmt); } catch (Exception ex) { System.out.println("Monto inválido."); break; }
                    System.out.print("Desde (código, p.ej. USD): ");
                    String from = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    System.out.print("Hacia (código, p.ej. PEN): ");
                    String to = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    if (!data.rates.containsKey(from) || !data.rates.containsKey(to)) {
                        System.out.println("Código(s) inválido(s).");
                        break;
                    }
                    // Convert via base
                    double rateFrom = data.rates.get(from);
                    double rateTo = data.rates.get(to);
                    double result = amount / rateFrom * rateTo;
                    System.out.println(df.format(amount) + " " + from + " = " + df.format(result) + " " + to);
                    break;
                case "6":
                    System.out.println("¿Qué quieres exportar?");
                    System.out.println(" a) Todo");
                    System.out.println(" b) Top comunes");
                    String opt = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                    List<String> list;
                    if (opt.equals("b")) {
                        list = Arrays.asList("USD","EUR","GBP","JPY","PEN","BRL","MXN","ARS","CLP","COP")
                                .stream().filter(data.rates::containsKey).collect(Collectors.toList());
                    } else {
                        list = new ArrayList<>(data.rates.keySet()).stream().sorted().collect(Collectors.toList());
                    }
                    writeReport("salida.txt", list, data, df);
                    System.out.println("Exportado a salida.txt");
                    break;
                case "7":
                    System.out.print("Nueva base (p.ej. USD, PEN, EUR): ");
                    String newBase = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                    updateBaseInConfig(newBase);
                    System.out.println("Actualizando con base=" + newBase + "...");
                    // Re-fetch
                    try {
                        String json2 = new ApiClient(readConfig().API_KEY, newBase, readConfig().API_URL_TEMPLATE, readConfig().TIMEOUT_SECONDS).fetchRates();
                        data = new CurrencyResponse(json2);
                    } catch (Exception e) {
                        System.out.println("Error al actualizar: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private static void printList(List<String> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%-6s", items.get(i));
            if ((i + 1) % 10 == 0) System.out.println();
        }
        System.out.println();
    }

    private static void printWithValues(List<String> codes, Map<String, Double> rates, java.text.DecimalFormat df) {
        if (codes.isEmpty()) {
            System.out.println("(Sin resultados)");
            return;
        }
        System.out.println(String.format("%-6s | %s", "CODE", "RATE"));
        System.out.println("--------------");
        for (String c : codes) {
            Double v = rates.get(c);
            if (v != null) {
                System.out.println(String.format("%-6s | %s", c, df.format(v)));
            }
        }
    }

    private static void writeReport(String fileName, List<String> codes, CurrencyResponse data, java.text.DecimalFormat df) {
        StringBuilder sb = new StringBuilder();
        sb.append("Base: ").append(data.baseCode).append("\n");
        if (!data.lastUpdateUtc.isEmpty()) sb.append("Actualizado: ").append(data.lastUpdateUtc).append("\n");
        sb.append(String.format("%-6s | %s\n", "CODE", "RATE"));
        sb.append("--------------\n");
        for (String c : codes) {
            Double v = data.rates.get(c);
            if (v != null) {
                sb.append(String.format("%-6s | %s\n", c, df.format(v)));
            }
        }
        try {
            Files.writeString(Path.of(fileName), sb.toString());
        } catch (IOException ignored) {}
    }

    private static void ensureConfigExists() {
        Path p = Path.of("config.json");
        if (Files.exists(p)) return;
        String def = "{\n" +
                "  \"API_KEY\": \"2dee0cca59f8675567c68625\",\n" +
                "  \"BASE\": \"USD\",\n" +
                "  \"API_URL_TEMPLATE\": \"https://v6.exchangerate-api.com/v6/{API_KEY}/latest/{BASE}\",\n" +
                "  \"TIMEOUT_SECONDS\": 20\n" +
                "}";
        try { Files.writeString(p, def); } catch (IOException ignored) {}
    }

    private static Config readConfig() {
        try {
            String s = Files.readString(Path.of("config.json"));
          
            Config cfg = new Config();
            cfg.API_KEY = extract(s, "API_KEY", "2dee0cca59f8675567c68625");
            cfg.BASE = extract(s, "BASE", "USD");
            cfg.API_URL_TEMPLATE = extract(s, "API_URL_TEMPLATE", "https://v6.exchangerate-api.com/v6/{API_KEY}/latest/{BASE}");
            cfg.TIMEOUT_SECONDS = Integer.parseInt(extract(s, "TIMEOUT_SECONDS", "20"));
            return cfg;
        } catch (Exception e) {
            System.out.println("Error leyendo config.json, usando valores por defecto.");
            Config cfg = new Config();
            cfg.API_KEY = "2dee0cca59f8675567c68625";
            cfg.BASE = "USD";
            cfg.API_URL_TEMPLATE = "https://v6.exchangerate-api.com/v6/{API_KEY}/latest/{BASE}";
            cfg.TIMEOUT_SECONDS = 20;
            return cfg;
        }
    }

    private static void updateBaseInConfig(String newBase) {
        try {
            String s = Files.readString(Path.of("config.json"));
           
            String updated = s.replaceAll("(\"BASE\"\\s*:\\s*\")[A-Z]{3}(\"\\s*)", "$1" + newBase + "$2");
            Files.writeString(Path.of("config.json"), updated);
        } catch (IOException ignored) {}
    }

    private static String extract(String json, String key, String def) {
        String pat = "\"" + key + "\"";
        int i = json.indexOf(pat);
        if (i < 0) return def;
        int colon = json.indexOf(':', i);
        if (colon < 0) return def;
        int j = colon + 1;
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) j++;
        if (j < json.length() && json.charAt(j) == '"') {
            int k = json.indexOf('"', j + 1);
            if (k > j) return json.substring(j + 1, k);
        } else {
           
            int k = j;
            while (k < json.length()) {
                char c = json.charAt(k);
                if (Character.isWhitespace(c) || c == ',' || c == '}') break;
                k++;
            }
            return json.substring(j, k).trim();
        }
        return def;
    }
}
