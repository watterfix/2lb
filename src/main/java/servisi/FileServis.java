package servisi;

import model.Slaid.Slaid;
import fabriki.SlaidFabrika;
import fabriki.IzobrazhenieSlaidFabrika;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileServis {
    private SlaidFabrika slaidFabrika;

    public FileServis() {
        this.slaidFabrika = new IzobrazhenieSlaidFabrika();
    }

    public List<Slaid> zagruzitSlaidiIzPapki(String putKPapke) throws IOException {
        List<Slaid> slaidi = new ArrayList<>();
        File papka = new File(putKPapke);

        if (!papka.exists() || !papka.isDirectory()) {
            throw new IOException("Папка не существует: " + putKPapke);
        }

        // Фильтр для изображений
        String[] rasshireniya = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        File[] faili = papka.listFiles((dir, name) -> {
            for (String ext : rasshireniya) {
                if (name.toLowerCase().endsWith(ext)) {
                    return true;
                }
            }
            return false;
        });

        if (faili != null) {
            for (File fail : faili) {
                try {
                    Slaid slaid = slaidFabrika.sozdatSlaid(fail);
                    slaidi.add(slaid);
                } catch (Exception e) {
                    System.err.println("Ошибка загрузки файла: " + fail.getName());
                }
            }
        }

        return slaidi;
    }

    public void sohranitSlaidKakIzobrazhenie(Slaid slaid, String putKFailu) throws IOException {
        if (slaid instanceof model.Slaid.IzobrazhenieSlaid) {
            model.Slaid.IzobrazhenieSlaid izobrazhenieSlaid =
                    (model.Slaid.IzobrazhenieSlaid) slaid;

            izobrazhenieSlaid.otobrazhit();
            BufferedImage bufer = izobrazhenieSlaid.poluchitBuferIzobrazheniya();

            if (bufer != null) {
                String format = poluchitFormatIzImeni(putKFailu);
                ImageIO.write(bufer, format, new File(putKFailu));
            }
        }
    }

    private String poluchitFormatIzImeni(String imyaFaila) {
        if (imyaFaila.toLowerCase().endsWith(".png")) return "PNG";
        if (imyaFaila.toLowerCase().endsWith(".jpg") ||
                imyaFaila.toLowerCase().endsWith(".jpeg")) return "JPEG";
        if (imyaFaila.toLowerCase().endsWith(".gif")) return "GIF";
        if (imyaFaila.toLowerCase().endsWith(".bmp")) return "BMP";
        return "PNG"; // По умолчанию
    }
}