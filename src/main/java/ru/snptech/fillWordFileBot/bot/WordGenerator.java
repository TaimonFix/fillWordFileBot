package ru.snptech.fillWordFileBot.bot;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import ru.snptech.fillWordFileBot.dto.User;

import java.io.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;

/**
 * Класс для создания .docx файла
 */
public class WordGenerator {

    /**
     * Создание документа
     * @param user ранее сформированные данные пользователя
     * @return сформированный документ в формате .docx
     */
    public File createDocument(User user) {
        File file = new File("review.docx");
        byte[] imageBytes = downloadImage(user.getPhotoPath());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(file);
             InputStream imageStream = new ByteArrayInputStream(imageBytes)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Фамилия: " + user.getSurname());
            run.addBreak();
            run.setText("Имя: " + user.getName());
            run.addBreak();
            run.setText("Отчество: " + user.getPatronymic());
            run.addBreak();
            run.setText("Дата рождения: " + user.getBirthDate().format(dateFormat));
            run.addBreak();
            run.setText("Пол: " + user.getGender());
            run.addBreak();
            run.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_JPEG, "image.jpg",
                    Units.toEMU(300),
                    Units.toEMU(300));

            document.write(out);

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Загрузка изображения
     * @param imagePath путь к изображению
     * @return массив байтов, содержащий изображение
     */
    private byte[] downloadImage(String imagePath)  {
        try (InputStream in = new URL(imagePath).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке изображения: " + e.getMessage());
        }
        return null;
    }
}
