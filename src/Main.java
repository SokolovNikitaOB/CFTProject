import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Данная программа предназначена для сортировки данных методом слияния, полученных чтением из файлов.
 * Отсортированные данные записываются в новый файл.
 */
public class Main {

    /**
     * Работа функции main:
     * <ul>
     *     <li>Чтение из консоли параметров программы
     *     (эти параметры должны соответствовать шаблону, отображенному в консоли).</li>
     *     <li>В случае некорректного ввода, программа запрашивает повторный ввод
     *     (и будет запрашивать пока данные не будут введены корректно).</li>
     *     <li>В случае успешного ввода, программа записывает отсортированные данные в файл
     *     (если файл с указанным именем не существует, он создается).</li>
     * </ul>
     */
    public static void main(String[] args) {
        boolean reverseOrder = false;
        boolean isNumbers = false;
        String pathToNewFile;
        List<String> pathsToFiles;
        Scanner in = new Scanner(System.in);

        while (true){
            System.out.println("Введите данные согласно шаблону:");
            System.out.println("(ex: -a/-d -i/-s path/name_new_file.txt path/name.txt path/name.txt path/name.txt)");
            String input = in.nextLine();

            if(!input.matches("(-a\\s|-d\\s)?(\\-s|\\-i)(\\s.+\\.txt){2,}")){
                System.out.println("Incorrect input");
                continue;
            }

            List<String> inputList = new ArrayList<>(List.of(input.split("\\s")));

            if(inputList.get(0).equals("-d")){
                reverseOrder = true;
                inputList.remove("-d");
            }else if(inputList.get(0).equals("-a")){
                inputList.remove("-a");
            }
            if(inputList.get(0).equals("-i")) isNumbers = true;
            pathToNewFile = inputList.get(1);
            pathsToFiles = inputList.stream().skip(2).collect(Collectors.toList());

            break;
        }

        if(isNumbers){
            try {
                writeToFile(Main.mergeFilesData(readFilesWithNumber(pathsToFiles),reverseOrder), pathToNewFile);
            }catch (NumberFormatException e){
                System.out.println("Should have used -s instead of -i");
            }
        }else {
            writeToFile(Main.mergeFilesData(readFiles(pathsToFiles), reverseOrder), pathToNewFile);
        }

    }


    /**
     * Этот метод предназначен для записи в файл данных из коллекции.
     * @param fileData коллекция данных.
     * @param path путь до файла (если файл не существует, он создается).
     * @param <V> подразумевается объект классов String или Integer.
     */
    public static <V> void writeToFile(List<V> fileData, String path) {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))){
            for (V f : fileData){
                bufferedWriter.write(String.valueOf(f) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Этот метод предназначен для чтения одного и более файлов, содержащих строки.
     * Полученные данные записываются в лист, элементами которого являются листы данных из одного файла.
     * @param pathsFiles лист путей до файлов.
     * @return лист данных из файлов.
     */
    public static List<List<String>> readFiles(List<String> pathsFiles)  {
        BufferedReader bufferedReader;
        List<List<String>> listOfFilesData = new ArrayList<>();
        List<String> helper;

        for (int i = 0; i < pathsFiles.size(); i++) {
            try {
                bufferedReader = new BufferedReader(new FileReader(pathsFiles.get(i)));
                helper = new ArrayList<>();
                bufferedReader.lines().sorted().forEach(helper::add);
                if(helper.size() > 0) {
                    listOfFilesData.add(helper);
                }
            } catch (FileNotFoundException e) {
                System.out.println(pathsFiles.get(i) + " not found");
            }
        }

        return listOfFilesData;
    }

    /**
     * Этот метод предназначен для чтения одного и более файлов, содержащих целые числа.
     * По сути этот метод использует метод readFiles(List<String> pathsFiles) для чтения,
     * а считанные данные оборачивает в Integer.
     * @param pathsFiles лист путей до файлов.
     * @return лист данных из файлов.
     */
    public static List<List<Integer>> readFilesWithNumber(List<String> pathsFiles){
       return readFiles(pathsFiles)
               .stream()
               .map(a -> a
                       .stream()
                       .map(Integer::parseInt)
                       .sorted()
                       .collect(Collectors.toList()))
               .collect(Collectors.toList());
    }

    /**
     * Этот метод предназначен для слияния всех данных полученных из файлов один отсортированный лист.
     * @param listOfFilesData лист данных файлов.
     * @param reverseOrder порядок сортировки.
     * @param <V> любой объект имплементирующий интерфейс Comparable.
     * @return отсортированный лист, состоящий из данных всех файлов.
     */
    public static <V extends Comparable<V>> List<V> mergeFilesData(List<List<V>> listOfFilesData, boolean reverseOrder){
        List<V> sortedList = new ArrayList<>();
        if(listOfFilesData.size() > 1) {
            for (int i = 0; i < listOfFilesData.size() - 1; i++) {
                if (i == 0) {
                    sortedList = mergeSorting(listOfFilesData.get(i), listOfFilesData.get(i + 1));
                } else {
                    sortedList = mergeSorting(sortedList, listOfFilesData.get(i + 1));
                }
            }
            if(reverseOrder) Collections.reverse(sortedList);
            return sortedList;
        }else {
            return listOfFilesData.get(0);
        }
    }

    /**
     * Этот метод предназначен для сортировки двух листов методом слияния.
     * @param firstList первый лист.
     * @param secondList второй лист.
     * @param <V> любой объект имплементирующий интерфейс Comparable.
     * @return отсортированный состоящий из двух полученных на вход коллекций лист.
     */
    public static <V extends Comparable<V>> List<V> mergeSorting(List<V> firstList, List<V> secondList){
        List<V> sortedList = new ArrayList<>();
        int i = 0;
        int j = 0;
        while(true){
            if(firstList.get(i).compareTo(secondList.get(j)) < 0){
                sortedList.add(firstList.get(i));
                i++;
            }else {
                sortedList.add(secondList.get(j));
                j++;
            }
            if(j == secondList.size()){
                sortedList.addAll(firstList.subList(i, firstList.size()));
                break;
            }
            if(i == firstList.size()){
                sortedList.addAll(secondList.subList(j, secondList.size()));
                break;
            }
        }
        return sortedList;
    }

}
