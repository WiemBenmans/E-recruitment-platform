package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Application;
import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Recruiter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, ObjectId> {

    List<Application> findByRecruiter(Recruiter recruiter);
    List<Application> findByCandidate(Candidate candidate);
    List<Application> findByOffer(Offer offer);

    Application findByCandidateAndOffer(Candidate candidate, Offer offer);

}
