package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Resume;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.List;

public interface SearchRepository {
    List<Offer> findByText(String text);
    List<Resume> findResumeByText(String text);

//    List<Resume> findResumeBySKills(List<String> skill);
//    FindIterable<Document> findResumeBySKills(String skill);

}
