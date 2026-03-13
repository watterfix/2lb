package servisi;

import java.io.*;
import java.util.*;
import java.nio.file.*;

/**
 * Сервис для работы с конфигурацией в формате Properties
 * Простая альтернатива JSON без внешних зависимостей
 */
public class KonfigServis {

    /**
     * Сохранить конфигурацию в файл
     */
    public void sohranitKonfig(String putKFailu, Map<String, String> dannie)
            throws IOException {
        Properties properties = new Properties();

        // Добавляем все данные в Properties
        for (Map.Entry<String, String> entry : dannie.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        // Сохраняем в файл с комментарием
        try (FileOutputStream fos = new FileOutputStream(putKFailu)) {
            properties.store(fos, "Конфигурация AlbomBpechatleniu");
        }
    }

    /**
     * Загрузить конфигурацию из файла
     */
    public Map<String, String> zagruzitKonfig(String putKFailu)
            throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(putKFailu)) {
            properties.load(fis);
        }

        // Преобразуем Properties в Map
        Map<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }

        return map;
    }

    /**
     * Сохранить настройки проекта (более сложная структура)
     */
    public void sohranitNastroykiProekta(String putKFailu,
                                         List<String> poryadokSlaidi,
                                         Map<String, String> zametki,
                                         Map<String, String> animatsii)
            throws IOException {
        Properties properties = new Properties();

        // Сохраняем порядок слайдов
        properties.setProperty("kolichestvoSlaidi",
                String.valueOf(poryadokSlaidi.size()));

        for (int i = 0; i < poryadokSlaidi.size(); i++) {
            properties.setProperty("slaid." + i, poryadokSlaidi.get(i));
        }

        // Сохраняем заметки
        for (Map.Entry<String, String> entry : zametki.entrySet()) {
            properties.setProperty("zametka." + entry.getKey(), entry.getValue());
        }

        // Сохраняем анимации
        for (Map.Entry<String, String> entry : animatsii.entrySet()) {
            properties.setProperty("animatsiya." + entry.getKey(), entry.getValue());
        }

        // Сохраняем метаданные
        properties.setProperty("dataSozdaniya",
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new java.util.Date()));
        properties.setProperty("versiya", "1.0");

        try (FileOutputStream fos = new FileOutputStream(putKFailu)) {
            properties.store(fos, "Настройки проекта AlbomBpechatleniu");
        }
    }

    /**
     * Загрузить настройки проекта
     */
    public Map<String, Object> zagruzitNastroykiProekta(String putKFailu)
            throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(putKFailu)) {
            properties.load(fis);
        }

        Map<String, Object> result = new HashMap<>();
        List<String> poryadokSlaidi = new ArrayList<>();
        Map<String, String> zametki = new HashMap<>();
        Map<String, String> animatsii = new HashMap<>();

        // Читаем все свойства
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);

            if (key.startsWith("slaid.")) {
                // Извлекаем индекс из ключа "slaid.0", "slaid.1", etc.
                String indexStr = key.substring(6); // удаляем "slaid."
                try {
                    int index = Integer.parseInt(indexStr);
                    // Убедимся, что список достаточно большой
                    while (poryadokSlaidi.size() <= index) {
                        poryadokSlaidi.add("");
                    }
                    poryadokSlaidi.set(index, value);
                } catch (NumberFormatException e) {
                    // Пропускаем некорректные ключи
                }
            } else if (key.startsWith("zametka.")) {
                String slaidId = key.substring(8); // удаляем "zametka."
                zametki.put(slaidId, value);
            } else if (key.startsWith("animatsiya.")) {
                String slaidId = key.substring(11); // удаляем "animatsiya."
                animatsii.put(slaidId, value);
            } else {
                // Прочие свойства
                result.put(key, value);
            }
        }

        result.put("poryadokSlaidi", poryadokSlaidi);
        result.put("zametki", zametki);
        result.put("animatsii", animatsii);

        return result;
    }

    /**
     * Проверить существует ли файл конфигурации
     */
    public boolean sushestvuetKonfig(String putKFailu) {
        return Files.exists(Paths.get(putKFailu));
    }

    /**
     * Создать конфигурацию по умолчанию
     */
    public Map<String, String> sozdatKonfigPoUmolchaniyu() {
        Map<String, String> konfig = new HashMap<>();

        konfig.put("put.k.slaidam", "./slaidi");
        konfig.put("put.k.smailikam", "./resursi/smailiki");
        konfig.put("put.sohraneniya", "./proekti");
        konfig.put("shirina.slaida", "800");
        konfig.put("visota.slaida", "600");
        konfig.put("pokaz.zametok", "true");
        konfig.put("skorost.animatsii", "5");
        konfig.put("avtoperehod", "false");
        konfig.put("interval.avtoperehoda", "5");

        return konfig;
    }
}