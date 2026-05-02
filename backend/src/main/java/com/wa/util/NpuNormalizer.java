package com.wa.util;

/**
 * Normaliza número CNJ — espelha consultar_tjrj_datajud.py {@code normalize_npu}.
 */
public final class NpuNormalizer {

    private NpuNormalizer() {
    }

    public static String normalizeNpu(String raw) {
        if (raw == null) {
            return "";
        }
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() != 20) {
            return raw.trim();
        }
        return digits.substring(0, 7) + "-" + digits.substring(7, 9) + "." + digits.substring(9, 13) + "."
                + digits.charAt(13) + "." + digits.substring(14, 16) + "." + digits.substring(16, 20);
    }
}
