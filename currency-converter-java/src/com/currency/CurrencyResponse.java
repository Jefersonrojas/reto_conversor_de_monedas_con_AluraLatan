package com.currency;

import java.util.Map;

public class CurrencyResponse {
    public final String baseCode;
    public final String lastUpdateUtc;
    public final Map<String, Double> rates;

    public CurrencyResponse(String json) {
        this.baseCode = JsonUtils.extractString(json, "base_code");
        String t = JsonUtils.extractString(json, "time_last_update_utc");
        this.lastUpdateUtc = (t != null) ? t : "";
        this.rates = JsonUtils.extractRates(json);
    }
}
