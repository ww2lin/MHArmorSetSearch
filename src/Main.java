import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import models.Equipment;
import utils.CsvToModel;

/**
 * Created by AlexLin on 3/28/17.
 */
public class Main {

    private static String FILE_PATH_HEAD_EQUIPMENT = "data/MHXX_EQUIP_HEAD.csv";
    private static String FILE_PATH_BODY_EQUIPMENT = "data/MHXX_EQUIP_BODY.csv";
    private static String FILE_PATH_ARM_EQUIPMENT = "data/MHXX_EQUIP_ARM.csv";
    private static String FILE_PATH_WST_EQUIPMENT = "data/MHXX_EQUIP_WST.csv";
    private static String FILE_PATH_LEG_EQUIPMENT = "data/MHXX_EQUIP_LEG.csv";

    public static void main(String args[]) throws IOException {
        List<Equipment> headEquipments = getEquipmentFromCsvFile(FILE_PATH_HEAD_EQUIPMENT);
        List<Equipment> bodyEquipments = getEquipmentFromCsvFile(FILE_PATH_BODY_EQUIPMENT);
        List<Equipment> armEquipments = getEquipmentFromCsvFile(FILE_PATH_ARM_EQUIPMENT);
        List<Equipment> wstEquipments = getEquipmentFromCsvFile(FILE_PATH_WST_EQUIPMENT);
        List<Equipment> legEquipments = getEquipmentFromCsvFile(FILE_PATH_LEG_EQUIPMENT);


        //for (int i = 0; i < armEquipments.size(); ++i){
        //    if (armEquipments.get(i).getName().equalsIgnoreCase("レザーグラブ")){
                System.out.println(armEquipments.get(0) + "  レザーグラブ" );
            //}
        //}
    }

    public static List<Equipment> getEquipmentFromCsvFile(String path) {
        CSVReader reader = null;
        try {
            List<Equipment> lst = new LinkedList<>();
            reader = new CSVReader(new FileReader(path));
            String[] nextLine;

            // skip over the header
            reader.readNext();

            // go over the CSV file line by line.
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                Equipment equipment = CsvToModel.csvEquipmentRowToModel(nextLine);
                lst.add(equipment);
            }
            return lst;
        } catch (IOException e) {
            return null;
        } finally {

            // Messy steps to clean up the file reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
