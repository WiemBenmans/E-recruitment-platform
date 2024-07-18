package com.enit.Erecruitement.Service;

import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.OfferRepository;
import com.enit.Erecruitement.Repository.SearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class OfferService {
    @Autowired  private OfferRepository offerRepository;
    @Autowired private SearchRepository searchRepository;
    public List<Offer> getAllOffers(){
        return offerRepository.findAll();
    }

    public List<Offer> getOffersByRecruiter(Recruiter recruiter){
        List<Offer> offers = this.getAllOffers();
        List<Offer> offersRecruiter = new ArrayList<>();
        for(Offer offer : offers)
        {
            ObjectId id = (ObjectId) offer.getRecruiter().getId();
            if (id.equals(recruiter.getId()))
                offersRecruiter.add(offer);
        }
        return offersRecruiter;

    }

    public Optional<Offer> getOfferById(ObjectId idOffer){
        return offerRepository.findById(idOffer);
    }

    public Offer createOffer(Offer offer,Recruiter recruiter)
    {
        offer.setRecruiter(recruiter);
        if(offer.getDateCreation() == null){
            offer.setDateCreation( LocalDate.now()); /** Date de creation de l'offre **/
        }
        return offerRepository.save(offer);
    }
    public Offer saveOffer(Offer offer)
    {
        return offerRepository.save(offer);
    }



//    public void updateOffer(ObjectId id, Offer offer) {
//        Optional<Offer> optionalOffer = offerRepository.findById(id);
//        if (optionalOffer.isPresent()) {
//            Offer existingOffer = optionalOffer.get();
//
//            existingOffer.setProfile(offer.getProfile());
//            existingOffer.setDateOffer(offer.getDateOffer());
//            existingOffer.setDescription(offer.getDescription());
//            existingOffer.setExperience(offer.getExperience());
//            existingOffer.setSalary(offer.getSalary());
//            existingOffer.setSkills(offer.getSkills());
//            existingOffer.setCompanyName(offer.getCompanyName());
//            existingOffer.setLocation(offer.getLocation());
//            offerRepository.save(existingOffer);
//        }else
//            offerRepository.save(offer);
//    }
    public  boolean deleteOffer(ObjectId idOffer)
    {
       Offer existingOffer = offerRepository.findById(idOffer).orElse(null);
        if (existingOffer == null) {
            return false;
        }
        offerRepository.delete(existingOffer);
        return true;
    }

    public List<Offer> getOfferByText(String text){

        return searchRepository.findByText(text);
    }
    public List<Offer> getOfferBySalary(Long salary)
    {

        List<Offer> listSalary = offerRepository.findBySalaryGreaterThanEqual(salary);
        return listSalary;
    }

    public List<Offer> getOfferBySalaryRange(Long minSalary, Long maxSalary)
    {
        return offerRepository.findBySalaryRange(minSalary, maxSalary);
    }
    public List<Offer> getOfferByExperience(String experience)
    {
        List<Offer> listExperience = offerRepository.findByExperience(experience);

        return listExperience;
    }
    public List<Offer> getOfferBySkills (List<String> skills )
    {
        List<Offer> listExperience = offerRepository.findBySkills(skills);
        return listExperience;
    }
    public List<Offer> getOfferByLocation(String location)
    {
        List<Offer> listLocation = offerRepository.findByLocation(location);
        return listLocation;
    }
    /** For CANDIDATE  **/
    public Set<Object> similarity (List<String> skills , List<String> offerSkills){
        Set<Object> similaritySet= new HashSet<>();

        /** rendre les listes en minuscule **/
        List<String> listeMinuscule1 = new ArrayList<>();
        for (String element : skills) {
            listeMinuscule1.add(element.toLowerCase());
        }
        skills= listeMinuscule1;
        List<String> listeMinuscule2 = new ArrayList<>();
        for (String element : offerSkills) {
            listeMinuscule2.add(element.toLowerCase());
        }
        offerSkills=listeMinuscule2;
        /** Calcul de similarité + Voir skills manquants **/
        List<String> missingSkills= new ArrayList<>();
        double similarity = 0;
        for(String skill : offerSkills){
            if(skills.contains(skill))
                similarity++;
            else
                missingSkills.add(skill);
        }
        similarity = (similarity/offerSkills.size())*100;
        similaritySet.add((int)Math.round(similarity));
        similaritySet.add(missingSkills);
        /*** tester affichage
        System.out.println("Service : " + similaritySet); ***/
        return similaritySet;
    }



//    public List<Offer> getOfferBySalary(Long salary, List<Offer> offers)
//    {
//        List<Offer> list = new ArrayList<>();
//        List<Offer> listSalary = offerRepository.findBySalaryGreaterThanEqual(salary);
//        for (Offer offer : offers) {
//            if (listSalary.contains(offer))
//                list.add(offer);
//        }
//        return list;
//    }
//
//    public List<Offer> getOfferBySalaryRange(Long minSalary, Long maxSalary)
//    {
//        return offerRepository.findBySalaryRange(minSalary, maxSalary);
//    }
//    public List<Offer> getOfferByExperience(String experience,List<Offer> offers)
//    {
//        List<Offer> list = new ArrayList<>();
//        List<Offer> listExperience = offerRepository.findByExperience(experience);
//        for (Offer offer : offers) {
//            if (listExperience.contains(offer))
//                list.add(offer);
//        }
//        return list;
//    }
//    public List<Offer> getOfferBySkills (List<String> skills ,List<Offer> offers)
//    {
//        List<Offer> list = new ArrayList<>();
//        List<Offer> listExperience = offerRepository.findBySkills(skills);
//        for (Offer offer : offers) {
//            if (listExperience.contains(offer))
//                list.add(offer);
//        }
//        return list;
//    }
//    public List<Offer> getOfferByLocation(String location, List<Offer> offers)
//    {
//        List<Offer> list = new ArrayList<>();
//        List<Offer> listExperience = offerRepository.findByLocation(location);
//        for (Offer offer : offers) {
//            if (listExperience.contains(offer))
//                list.add(offer);
//        }
//        return list;
//    }


}
