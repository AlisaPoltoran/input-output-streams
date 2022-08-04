import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {

        //создание директорий и файлов
        createNewDirectory("Games/src");
        createNewDirectory("Games/res");
        createNewDirectory("Games/savegames");
        createNewDirectory("Games/temp");
        createNewDirectory("Games/src/main");
        createNewDirectory("Games/src/test");
        createNewFile("Games/src/main/Main.java");
        createNewFile("Games/src/main/Utils.java");
        createNewDirectory("Games/res/drawables");
        createNewDirectory("Games/res/vectors");
        createNewDirectory("Games/res/icons");

//         2.1. Создать три экземпляра класса GameProgress.
        GameProgress game1 = new GameProgress(14, 34, 53, 453.5);
        GameProgress game2 = new GameProgress(65, 23, 3, 900.56);
        GameProgress game3 = new GameProgress(2, 453, 34, 1223.34);

//        2.2. Сохранить сериализованные объекты GameProgress в папку savegames из предыдущей задачи.
        saveGame("Games/savegames/save1.dat", game1);
        saveGame("Games/savegames/save2.dat", game2);
        saveGame("Games/savegames/save3.dat", game3);

//        2.3. Созданные файлы сохранений из папки savegames запаковать в архив zip и 2.4. Удалить файлы сохранений,
//        лежащие вне архива
        zipFiles("Games/savegames/zip.zip", new File("Games/savegames"));

//        3.1. Произвести распаковку архива в папке savegames.
        openZip("Games/savegames/zip.zip", "Games/savegames");


        // запись информации об создании файлов и директорий
        String text = String.valueOf(sb);
        try (FileWriter fw = new FileWriter("Games/temp/temp.txt", true)) {
            fw.write(text);
            fw.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

//        3.2. Произвести считывание и десериализацию одного из разархивированных файлов save.dat b 3.3. Вывести в
//        консоль состояние сохранненой игры.
        System.out.println(openProgress("Games/savegames/save3.dat"));
    }

    public static StringBuilder sb = new StringBuilder();

    public static void createNewDirectory(String name) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        File dir = new File(name);
        LocalDateTime ldt = LocalDateTime.now();
        if (dir.mkdir()) {
            sb.append(ldt).append(" cоздан каталог ").append(name).append("\n");
        } else {
            sb.append(ldt).append(" не удалось создать каталог ").append(name).append("\n");
        }
    }

    public static void createNewFile(String name) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        File f = new File(name);
        try {
            LocalDateTime ldt = LocalDateTime.now();
            if (f.createNewFile()) {
                sb.append(ldt).append(" cоздан файл ").append(name).append("\n");
            } else {
                sb.append(ldt).append(" не удалось создать файл ").append(name).append("\n");
            }
        } catch (IOException ex) {
            LocalDateTime ldt = LocalDateTime.now();
            sb.append(ldt).append(" не удалось создать файл ").append(name).append(" из-за ").append(ex.getMessage())
                    .append("\n");
        }
    }

    public static void saveGame(String filePath, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String zipPath, File file) {
        List<String> list = new ArrayList<>();
        if (file.isDirectory()) {
            String[] s = file.list();
            for (String value : s) {
                list.add("Games/savegames/" + value);
            }

            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
                for (String filePath : list) {
                    File fileToZip = new File(filePath);
                    try (FileInputStream fis = new FileInputStream(fileToZip)) {
                        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                        zipOut.putNextEntry(zipEntry);
                        byte[] bytes = new byte[fis.available()];
                        fis.read(bytes);
                        zipOut.write(bytes);
                        zipOut.closeEntry();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        for (String s : list) {
            if (!s.endsWith("zip")) {
                File fileToDelete = new File(s);
                if (fileToDelete.delete()) {
                    LocalDateTime ldt = LocalDateTime.now();
                    sb.append(ldt).append(" удален ").append(fileToDelete.getName()).append("\n");
                }

            }
        }
    }

    public static void openZip(String zipPath, String destinationPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {

            File destinationDirectory = new File(destinationPath);
            LocalDateTime ldt = LocalDateTime.now();
            if (!destinationDirectory.exists()) {
                if (destinationDirectory.mkdir()) {
                    sb.append(ldt).append(" создан каталог ").append(destinationPath).append("\n");
                } else {
                    sb.append(ldt).append(" не удалось создать каталог ").append(destinationPath).append("\n");
                }
            } else {
                sb.append(ldt).append(" каталог ").append(destinationPath).append(" уже есть, создавать не нужно ").
                        append("\n");
            }

            ZipEntry zipEntry;
            String name;
            while ((zipEntry = zis.getNextEntry()) != null) {
                name = destinationPath + "/" + zipEntry.getName();
                FileOutputStream fous = new FileOutputStream(name);
                for (int c = zis.read(); c != -1; c = zis.read()) {
                    fous.write(c);
                }
                fous.flush();
                zis.closeEntry();
                fous.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress(String savedGamePath) {
        GameProgress gameProgress = null;

        try (FileInputStream fis = new FileInputStream(savedGamePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}



