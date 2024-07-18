package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Resume;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<Resume, ObjectId> {
    Resume findByCandidate(Candidate candidate);

}
