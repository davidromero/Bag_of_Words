package app;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;

class MongoDB {
    private MongoClient client;
    private MongoDatabase database;

    MongoDB(String databaseName) {
        this.client = new MongoClient();
        this.database = client.getDatabase(databaseName);
    }

    public void closeConnection(){
        client.close();
    }

}
