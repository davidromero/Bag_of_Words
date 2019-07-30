package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class GenModel {

    private String filePath;
    private Properties properties;

    void setFilePath(String filePath){
        this.filePath = filePath;
    }

    String getFilePath(){
        return filePath;
    }

    void loadProperties(){
        properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("src/main/resources/app.properties");
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

//    String getDBName(){
//        String dbName = properties.getProperty("DB_NAME");
//        if (dbName == null){
//            dbName = "ia_project";
//        }
//        return dbName;
//    }

}