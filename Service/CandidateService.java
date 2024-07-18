package com.enit.Erecruitement.Service;

import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.CandidateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {

        return candidateRepository.findAll();
    }
    public Candidate getCandidateById(ObjectId id) {

        return candidateRepository.findById(id).orElse(null);
    }
    public Candidate createCandidate(Candidate candidate) {

        return candidateRepository.save(candidate);
    }
    public Candidate getRecruiterByEmailAndPassword(String email, String password)
    {
        return candidateRepository.findByEmailAndPassword(email, password);
    }
    public boolean connect(String email, String password){
        Candidate candidate = candidateRepository.findByEmail(email);
        if(candidate != null && candidate.getPassword().equals(password)) {
            return true;
        }
        return false;

    }

    public Candidate updateCandidate(ObjectId id, Candidate candidate) {
        Candidate existingCandidate = candidateRepository.findById(id).orElse(null);
        if (existingCandidate == null) {
            return null;
        }
        existingCandidate.setEmail(candidate.getEmail());
        existingCandidate.setPassword(candidate.getPassword());
        existingCandidate.setName(candidate.getName());
        existingCandidate.setSurname(candidate.getSurname());
        existingCandidate.setPhoneNumber(candidate.getPhoneNumber());
        return candidateRepository.save(existingCandidate);
    }
    public Candidate update(Candidate candidate)
    {
        return candidateRepository.save(candidate);
    }

    public boolean deleteCandidate(ObjectId id) {
        Candidate existingCandidate = candidateRepository.findById(id).orElse(null);
        if (existingCandidate == null) {
            return false;
        }
        candidateRepository.delete(existingCandidate);
        return true;
    }

}


