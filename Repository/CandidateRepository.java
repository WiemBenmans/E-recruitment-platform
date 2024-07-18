package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Candidate;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends  MongoRepository<Candidate, ObjectId> {
    Candidate findByEmail(String email);
    Candidate findByEmailAndPassword(String email , String password);
}
