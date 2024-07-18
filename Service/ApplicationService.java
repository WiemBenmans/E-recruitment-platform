package com.enit.Erecruitement.Service;

import com.enit.Erecruitement.Application;
import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.ApplicationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private OfferService offerService;

    public Optional<Application> getAppById(ObjectId id)
    {
        return applicationRepository.findById(id);
    }
    public List<Application> getAppByRecruiter(Recruiter recruiter){
        return applicationRepository.findByRecruiter(recruiter);
    }
    public List<Application> getAppByCandidate(Candidate candidate){
        return applicationRepository.findByCandidate(candidate);
    }
    public List<Application> getAppByOffer(Offer offer){

       /**  SET nbr app par offre **/
        List<Application> applications= applicationRepository.findByOffer(offer);
        offer.setNbrApp(applications.size());
        offerService.saveOffer(offer);

        return applicationRepository.findByOffer(offer);
    }
    public Application getAppByCandidateAndOffer(Candidate candidate, Offer offer){
        return applicationRepository.findByCandidateAndOffer(candidate,offer);
    }

    public List<Application> getAllApplications(){
        return  applicationRepository.findAll();
    }
    public Application createApplicataion(Application application)
    {
        return applicationRepository.save(application);
    }
    public void deleteApplication(ObjectId id)
    {
        Optional<Application> application = this.getAppById(id);
        applicationRepository.delete(application.get());
    }

    public void sortByDateInterview(List<Application> applications) {
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

        Comparator<Application> byDate = Comparator.comparing( application -> {
            String dateInterview = application.getDateInterview();
            if (dateInterview == null) {
                return LocalDate.MAX;
            }
            return LocalDate.parse(dateInterview, DATE_FORMATTER);
        });
        Comparator<Application> byTime = Comparator.comparing( application -> {
            String timeInterview = application.getTimeInterview();
            if (timeInterview == null) {
                return LocalTime.MAX;
            }
            return LocalTime.parse(timeInterview, TIME_FORMATTER);
        });
        Comparator<Application> byDateAndTime =byDate.thenComparing(byTime);
        applications.sort(byDateAndTime);
    }
    public Application saveApplication(Application application){
        return applicationRepository.save(application);
    }

}
