package com.enit.Erecruitement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "offers")
public class Offer {
    @Id
    private ObjectId idOffer;
    private String profile;
    private String companyName;

    private String location;
    private String description;
    private List<String> skills;
    private String experience;
    private String dateOffer ;

    private LocalDate dateCreation ;
    private Long salary;
    @DBRef
    private Recruiter recruiter;

    /** sert pour l'affichage dans la page du candidat **/
    private int similarity;
    private List<String> missingSkills;

    /** Compter les applications par offre **/
    private int nbrApp;




}