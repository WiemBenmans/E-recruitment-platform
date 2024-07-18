package com.enit.Erecruitement;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "applications")
public class Application {
    @Id
    private ObjectId id;
    @DBRef
    private Offer offer;
    @DBRef
    private  Recruiter recruiter;
    @DBRef
    private Candidate candidate;
    private Boolean confirmation;

    private String dateInterview;
    private String timeInterview;

    /** sert pour l'affichage dans la page du RECRUITER **/
    private int similarity;
    private List<String> missingSkills;
}
