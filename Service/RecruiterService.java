package com.enit.Erecruitement.Service;

import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.RecruiterRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class RecruiterService {
    @Autowired
    private RecruiterRepository recruiterRepository;

    public Recruiter getRecruiterByEmailAndPassword(String email, String password)
    {
        return recruiterRepository.findByEmailAndPassword(email, password);
    }
    public List<Recruiter> getAllRecruiters() {
        return recruiterRepository.findAll();
    }
    public Recruiter getRecruiterById(ObjectId id) {

        return recruiterRepository.findById(id).orElse(null);
    }
    public Recruiter createRecruiter(Recruiter recruiter) {return recruiterRepository.save(recruiter); }
    public boolean connect(String email, String password){
        Recruiter recruiter = recruiterRepository.findByEmail(email);
        if(recruiter != null && recruiter.getPassword().equals(password)) {
            return true;
        }
        return false;

    }
    public Recruiter updateRecruiter(ObjectId id, Recruiter recruiter) {
        Recruiter existingRecruiter = recruiterRepository.findById(id).orElse(null);
        if (existingRecruiter == null) {
            return null;
        }
        existingRecruiter.setEmail(recruiter.getEmail());
        existingRecruiter.setPassword(recruiter.getPassword());
        existingRecruiter.setName(recruiter.getName());
        existingRecruiter.setSurname(recruiter.getSurname());
        return recruiterRepository.save(existingRecruiter);
    }
    public Recruiter update(Recruiter recruiter)
    {
        return recruiterRepository.save(recruiter);
    }
    public boolean deleteRecruiter(ObjectId id) {
        Recruiter existingRecruiter = recruiterRepository.findById(id).orElse(null);
        if (existingRecruiter == null) {
            return false;
        }
        recruiterRepository.delete(existingRecruiter);
        return true;
    }



}