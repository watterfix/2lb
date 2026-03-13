package persistence;

import kollektsii.SlaidKolleksiya;
import model.Slaid.IzobrazhenieSlaid;
import model.Slaid.Slaid;
import model.animatsiya.Animatsiya;
import model.kontent.Kontent;
import model.kontent.SmailikKontent;
import model.kontent.TekstKontent;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

public class ProektSaver {

    public static void sohranitProekt(SlaidKolleksiya kollektsiya,
                                      String papkaProekta,
                                      Map<String, Object> nastroyki) throws IOException {

        // Создаем структуру папок
        Files.createDirectories(Paths.get(papkaProekta));
        String papkaSlaidi = papkaProekta + "/slaidi";
        String papkaDannie = papkaProekta + "/dannie";
        Files.createDirectories(Paths.get(papkaSlaidi));
        Files.createDirectories(Paths.get(papkaDannie));

        // Сохраняем слайды
            JSONArray jsonSlaidi = new JSONArray();
        int index = 0;

        for (Slaid slaid : kollektsiya) {
            if (slaid instanceof IzobrazhenieSlaid) {
                IzobrazhenieSlaid izSlaid = (IzobrazhenieSlaid) slaid;

                // Сохраняем изображение
                String imyaFaila = "slaid_" + index + ".png";
                String putKFailu = papkaSlaidi + "/" + imyaFaila;

                izSlaid.otobrazhit();
                BufferedImage bufer = izSlaid.poluchitBuferIzobrazheniya();
                if (bufer != null) {
                    ImageIO.write(bufer, "PNG", new File(putKFailu));
                }

                // Создаем JSON для слайда
                JSONObject jsonSlaid = new JSONObject();
                jsonSlaid.put("id", izSlaid.poluchitId());
                jsonSlaid.put("imya_faila", imyaFaila);
                jsonSlaid.put("zametka", izSlaid.poluchitZametku());

                // Сохраняем анимацию
                JSONObject jsonAnimatsiya = new JSONObject();
                Animatsiya anim = izSlaid.poluchitAnimatsiyu();
                jsonAnimatsiya.put("tip", anim.poluchitTip().name());
                jsonAnimatsiya.put("prodolzhitelnost", anim.poluchitProdolzhitelnost());
                jsonSlaid.put("animatsiya", jsonAnimatsiya);

                // Сохраняем контент
                JSONArray jsonKontent = new JSONArray();
                for (Kontent kontent : izSlaid.poluchitSpisokKontenta()) {
                    JSONObject jsonKontentObj = new JSONObject();
                    jsonKontentObj.put("tip", kontent.poluchitTip().name());

                    if (kontent instanceof TekstKontent) {
                        TekstKontent tekst = (TekstKontent) kontent;
                        jsonKontentObj.put("tekst", tekst.poluchitTekst());
                        jsonKontentObj.put("ugol", tekst.poluchitUgol().name());
                        jsonKontentObj.put("masshtab", tekst.poluchitMasshtab());
                    } else if (kontent instanceof SmailikKontent) {
                        SmailikKontent smailik = (SmailikKontent) kontent;
                        jsonKontentObj.put("tip_smailika", smailik.poluchitTipSmailika().name());
                        jsonKontentObj.put("x", smailik.poluchitPozitsiyu().x);
                        jsonKontentObj.put("y", smailik.poluchitPozitsiyu().y);
                    }

                    jsonKontent.put(jsonKontentObj);
                }
                jsonSlaid.put("kontent", jsonKontent);

                jsonSlaidi.put(jsonSlaid);
                index++;
            }
        }

        // Сохраняем настройки
        JSONObject jsonProekt = new JSONObject();
        jsonProekt.put("nazvanie_proekta", nastroyki.getOrDefault("nazvanie", "Безымянный проект"));
        jsonProekt.put("data_sozdaniya", new Date().toString());
        jsonProekt.put("kolichestvo_slaidi", kollektsiya.razmer());
        jsonProekt.put("slaidi", jsonSlaidi);
        jsonProekt.put("nastroyki_pokaza", new JSONObject(nastroyki));

        // Сохраняем JSON
        String jsonPut = papkaDannie + "/proekt.json";
        try (FileWriter writer = new FileWriter(jsonPut)) {
            jsonProekt.write(writer, 2, 0);
        }

        // Сохраняем порядок слайдов
        String poryadokPut = papkaDannie + "/poryadok.txt";
        try (PrintWriter writer = new PrintWriter(poryadokPut)) {
            for (Slaid slaid : kollektsiya) {
                writer.println(slaid.poluchitId());
            }
        }
    }

    public static SlaidKolleksiya zagruzitProekt(String papkaProekta) throws IOException {
        String papkaDannie = papkaProekta + "/dannie";
        String jsonPut = papkaDannie + "/proekt.json";

        String jsonStr = new String(Files.readAllBytes(Paths.get(jsonPut)));
        JSONObject jsonProekt = new JSONObject(jsonStr);

        SlaidKolleksiya kollektsiya = new kollektsii.SlaidSpisok();
        JSONArray jsonSlaidi = jsonProekt.getJSONArray("slaidi");

        for (int i = 0; i < jsonSlaidi.length(); i++) {
            JSONObject jsonSlaid = jsonSlaidi.getJSONObject(i);

            // Загружаем изображение
            String imyaFaila = jsonSlaid.getString("imya_faila");
            String putKFailu = papkaProekta + "/slaidi/" + imyaFaila;

            BufferedImage image = ImageIO.read(new File(putKFailu));
            IzobrazhenieSlaid slaid = new IzobrazhenieSlaid();
            slaid.ustanovitId(jsonSlaid.getString("id"));
            slaid.ustanovitIzobrazhenie(image);
            slaid.ustanovitOriginalnoeIzobrazhenie(image);

            // Загружаем заметку
            slaid.ustanovitZametku(jsonSlaid.optString("zametka", ""));

            // Загружаем анимацию
            JSONObject jsonAnim = jsonSlaid.getJSONObject("animatsiya");
            Animatsiya anim = new Animatsiya();
            anim.ustanovitTip(model.animatsiya.TipAnimatsii.valueOf(
                    jsonAnim.getString("tip")));
            anim.ustanovitProdolzhitelnost(jsonAnim.getInt("prodolzhitelnost"));
            slaid.ustanovitAnimatsiyu(anim);

            // Загружаем контент
            JSONArray jsonKontent = jsonSlaid.getJSONArray("kontent");
            for (int j = 0; j < jsonKontent.length(); j++) {
                JSONObject jsonKontentObj = jsonKontent.getJSONObject(j);
                String tip = jsonKontentObj.getString("tip");

                if ("TEKST".equals(tip)) {
                    TekstKontent tekst = new TekstKontent(
                            jsonKontentObj.getString("tekst"),
                            model.kontent.UgolSlaida.valueOf(jsonKontentObj.getString("ugol")),
                            jsonKontentObj.getDouble("masshtab")
                    );
                    slaid.dobavitKontent(tekst);
                } else if ("SMAILIK".equals(tip)) {
                    SmailikKontent smailik = new SmailikKontent(
                            model.kontent.SmailikKontent.TipSmailika.valueOf(
                                    jsonKontentObj.getString("tip_smailika")),
                            new java.awt.Point(
                                    jsonKontentObj.getInt("x"),
                                    jsonKontentObj.getInt("y")
                            )
                    );
                    slaid.dobavitKontent(smailik);
                }
            }

            kollektsiya.dobavit(slaid);
        }

        return kollektsiya;
    }
}