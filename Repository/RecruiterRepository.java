package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Recruiter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepository extends MongoRepository<Recruiter, ObjectId> {
    Recruiter findByEmail (String email);
    Recruiter findByEmailAndPassword (String email, String password);
}
