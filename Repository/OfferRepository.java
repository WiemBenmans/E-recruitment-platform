package com.enit.Erecruitement.Repository;

import com.enit.Erecruitement.Offer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends MongoRepository<Offer, ObjectId> {

    List<Offer> findBySalary (Long salary);
    @Query("{ 'salary' : {$gte: ?0}}")
    List<Offer> findBySalaryGreaterThanEqual(long salary);

    @Query("{ 'salary' : { $gte: ?0, $lte: ?1 } }")
    List<Offer> findBySalaryRange(Long minSalary , Long maxSalary);

    List<Offer> findByExperience(String experience);
    List<Offer> findBySkills(List<String> skills);
    List<Offer> findByLocation (String location);



}
