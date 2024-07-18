package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Resume;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SearchRepositoryImpl implements  SearchRepository{
    @Autowired  MongoClient client;
    @Autowired   MongoConverter mongoConverter;
    @Autowired MongoTemplate mongoTemplate;
    @Override
    public List<Offer> findByText(String text) {
        final List<Offer> offers = new ArrayList<>();

        MongoDatabase database = client.getDatabase("E-recruitement");
        MongoCollection<Document> collection = database.getCollection("offers");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                        new Document("index", "searchOffers")
                                .append("text",
                                        new Document("query", text)
                                                .append("path",
                                                        new Document("wildcard", "*")))),
                new Document("$sort",
                        new Document("experience", 1L))));

        result.forEach(document -> offers.add(mongoConverter.read(Offer.class,document)));
        return offers;
    }

    @Override
    public List<Resume> findResumeByText (String text)
    {
        final List<Resume> resumes=new ArrayList<>();

        MongoDatabase database = client.getDatabase("E-recruitement");
        MongoCollection<Document> collection = database.getCollection("resumes");
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                        new Document("index", "searchResume")
                                .append("text",
                                        new Document("query", text)
                                                .append("path",
                                                        new Document("wildcard", "*")))),
                new Document("$sort",
                        new Document("experience", -1L))));

        result.forEach(document -> resumes.add(mongoConverter.read(Resume.class,document)));
        return resumes;

    }
//    @Override
//    public List<Resume> findResumeBySKills(List<String> skillsR){
//        try {
//            // Créer un index pour le champ "skills"
//            IndexDefinition indexDefinition = new Index().on("skills", Sort.Direction.ASC);
//            mongoTemplate.indexOps("resumes").ensureIndex(indexDefinition);
//            System.out.println( "Index created successfully!");
//        } catch (Exception e) {
//            System.out.println("Failed to create index: " + e.getMessage());
//        }
//        List<Resume> result = mongoTemplate.find(query(where("skills").in(skillsR)), Resume.class, "skills");
//
//        return result;
//    }
//    @Override
//    public FindIterable<Document> findResumeBySKills(String skillsR){
//        MongoDatabase database = client.getDatabase("E-recruitement");
//        MongoCollection<Document> collection = database.getCollection("resumes");
//        collection.createIndex(Indexes.text("skills"));
//        Bson textSearch = Filters.text(skillsR); // Create a text search filter
//        FindIterable<Document> results = collection.find(textSearch);
//        return  results;
//    }

}