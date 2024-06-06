package com.example.iot_project.database;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBManager {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBManager() {
        mongoClient = MongoClients.create("mongodb+srv://iot123:123@cluster0.exklche.mongodb.net/");
        database = mongoClient.getDatabase("mydb"); // Thay đổi tên database tại đây
        collection = database.getCollection("names"); // Thay đổi tên collection tại đây
    }

    public void closeConnection() {
        mongoClient.close();
    }

    public void insertName(Name name) {
        Document document = new Document("name", name.getName());
        collection.insertOne(document);
    }

    // Các phương thức khác như findNames, updateName, deleteName cũng có thể được thêm vào
}
