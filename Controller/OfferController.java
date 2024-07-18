package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.*;
import com.enit.Erecruitement.Repository.RecruiterRepository;
import com.enit.Erecruitement.Service.ApplicationService;
import com.enit.Erecruitement.Service.OfferService;
import com.enit.Erecruitement.Service.ResumeService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.regexp.RE;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.*;

@Controller
//@CrossOrigin(origins = "http://localhost:3000")
public class OfferController {
    @Autowired  private OfferService offerService;
    @Autowired private RecruiterRepository recruiterRepository;

    @Autowired private ApplicationService applicationService;
    @Autowired private ResumeService resumeService;

    @GetMapping("/offers")  /*********** Afficher les offres de chaque recruteur *****/
    public String getOffersOfRecruiter(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");

        List<Offer> listOffers = offerService.getOffersByRecruiter(recruiter);

        int numberALlMyOffers = listOffers.size();  /** nbr tous les offres **/
//        System.out.println("size offers "+ numberALlMyOffers);

//        for(Offer offer: listOffers) /** nbr app par offre **/
//        {
//            List<Application> applications= applicationService.getAppByOffer(offer);
//            offer.setNbrApp(applications.size());
//            System.out.println("size apps/offer "+ applications.size());
//            offerService.saveOffer(offer);
//        }

        model.addAttribute("nbrAll",numberALlMyOffers);
        model.addAttribute("offers", listOffers);
        return  "mySpaceRecruiter";
    }
    @GetMapping("/offer/detail/{id}")
    public String showDetailsOffer(@PathVariable(value = "id") ObjectId id , Model model){
        Optional<Offer> offer = offerService.getOfferById(id);
        model.addAttribute("offer", offer.get());
        model.addAttribute("recruiter", offer.get().getRecruiter());

        return "offerDetail";
    }

    @GetMapping("/offer/detail/Candidate/{id}")
    public String showDetailsOfferForCandidate(@PathVariable(value = "id") ObjectId id , Model model){
        Optional<Offer> offer = offerService.getOfferById(id);
        model.addAttribute("offer", offer.get());
        model.addAttribute("recruiter", offer.get().getRecruiter());
        return "offerDetailCandidate";
    }

    @PostMapping("/offers/create")
    public String createOffer (@ModelAttribute("offers") Offer offer, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        Recruiter recruiter = (Recruiter) session.getAttribute("currentUser");
        offer.setRecruiter(recruiter);
        if(offer.getDateCreation() == null){
            offer.setDateCreation( LocalDate.now()); /** Date de creation de l'offre **/
        }
        offerService.saveOffer(offer);
        return "redirect:/offers";
    }

    @GetMapping("/offers/{id}")
    public Optional<Offer> getOfferById(@PathVariable("id") ObjectId id) {

        return offerService.getOfferById(id);
    }


    @GetMapping("/offers/showAddForm")
    public String createOfferForm(Model model)
    {
        Offer offer = new Offer();
        model.addAttribute("offers",offer);
        return "createOffer";
    }


    @GetMapping("/offers/showUpdateForm/{id}")
    public String updateOfferForm (@PathVariable (value = "id") ObjectId id, Model model) {
        Optional<Offer> existingOffer= offerService.getOfferById(id);
        model.addAttribute("offers", existingOffer.get());
        return "editOffer";
    }


    @PostMapping("/offers/update")
    public String updateOffer(@ModelAttribute("offers") Offer offer) {
        //offerService.updateOffer(offer.getIdOffer() ,offer);
        System.out.println("id offer update :"+ offer.getIdOffer());
        System.out.println("offer update :"+ offer.getIdOffer());

        offerService.saveOffer(offer);
        return "redirect:/offers";
    }
    @GetMapping("/offers/delete/{id}")
    public String deleteOffer(@PathVariable (value = "id") ObjectId id) {
        offerService.deleteOffer(id);
        return "redirect:/offers";
    }

    @GetMapping("/offers/suggestions/{id}")
    public String appsByOffer (@PathVariable (value = "id") ObjectId id, Model model) {
        Optional<Offer> offer= offerService.getOfferById(id);
        model.addAttribute("offer", offer.get());
        List<Application> applications= applicationService.getAppByOffer(offer.get());
        model.addAttribute("applications", applications);
        model.addAttribute("nbrAll", applications.size());

        List<Candidate> candidatesApps = new ArrayList<>(); /** les candidats qui ont appliqués **/
        for(Application application: applications){
            candidatesApps.add(application.getCandidate());
        }
        Map<Integer, List<Resume>> resumeMap = resumeService.getResumesBySkills(offer.get()); /** Tous les cvs selon l'offre **/
        Map<Integer, List<Resume>> resumeMap1 = new TreeMap<>();
        for (Map.Entry<Integer, List<Resume>> entry : resumeMap.entrySet()) {
            List<Resume> resumes = entry.getValue();
            List<Resume> resumes1 = new ArrayList<>();
            for(Resume resume:resumes){
                if(!candidatesApps.contains(resume.getCandidate())){
                    //entry.getValue().remove(resume);
                    resumes1.add(resume);
                    resumeMap1.put(entry.getKey(),resumes1);
                }
            }
//            if(entry.getValue().size()==0)
//                resumeMap.remove(entry.getKey());
        }
        resumeMap =resumeMap1;
        model.addAttribute("resumes",resumeMap);
        Integer size=0;
        for (Map.Entry<Integer, List<Resume>> entry : resumeMap.entrySet()) {
            size += entry.getValue().size();
        }
        model.addAttribute("nbrsugg",size);
        return "applicationsByOffer";
    }
    @PostMapping("/search")   //Recherche par texte + sort Ascendant selon experience
    @CrossOrigin
    public String search(@RequestParam(name ="rech") String text,
                         @RequestParam(name ="rechSalary") Long salary,
                         @RequestParam(name ="experience")String experience ,
                         @RequestParam(name ="skills") String skills,
                         @RequestParam(name ="location") String location,
                         Model model){
        //System.out.println(text +"// "+ experience +"// "+skills +"// "+location);

        List<Offer> offers = offerService.getOfferByText(text);
        if(salary != 0L)
        {
            List<Offer> salaries = offerService.getOfferBySalary(salary);
            if(!salaries.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : salaries) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }
        }
        if(!experience.isEmpty())
        {
            List<Offer> offersExp = offerService.getOfferByText(experience);
            if(!offersExp.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : offersExp) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }
        }
        if(!skills.isEmpty())
        {
            List<Offer> offersSkills = offerService.getOfferByText(skills);
            if(!offersSkills.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : offersSkills) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }
        }
        if(!location.isEmpty()){
            List<Offer> offersLocation = offerService.getOfferByText(location);
            if(!offersLocation.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : offersLocation) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }

        }
        //Trier les offres par salary décroissant
        Collections.sort(offers, new Comparator<Offer>() {
            public int compare(Offer e1, Offer e2) {
                return Double.compare(e2.getSalary(), e1.getSalary());
            }
        });
        model.addAttribute("textOffers", offers);
        return "offerSearch";
    }
    @PostMapping("/search/inMyOffers")   /** Rech des offres dans les applications(seulement les offres dans application) **/
    @CrossOrigin
    public String searchInMyOffers(@RequestParam(name ="keyword") String text,
                         @RequestParam(name ="salary") Long salary,
                         @RequestParam(name ="location") String location,
                         Model model, HttpServletRequest request){
        HttpSession session= request.getSession();
        Recruiter recruiter=(Recruiter) session.getAttribute("currentUser");

        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        List<Offer> myOffers = new ArrayList<>(); /** offres du recruteur ayant au moins une application **/
        for(Application application: applications){
            if(!myOffers.contains(application.getOffer()))
                myOffers.add(application.getOffer());
        }

        List<Offer> offers = offerService.getOfferByText(text); /** Tous les offres du rech par text **/
        List<Offer> listOfOffers =new ArrayList<>();
        for(Offer offer: offers){
            if(myOffers.contains(offer))
                listOfOffers.add(offer);
        }
        offers=listOfOffers;

        if(salary != 0L)
        {
            List<Offer> salaries = offerService.getOfferBySalary(salary);
            if(!salaries.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : salaries) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }
        }
        if(!location.isEmpty()){
            List<Offer> offersLocation = offerService.getOfferByText(location);
            if(!offersLocation.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : offersLocation) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }

        }

//        model.addAttribute("textOffers", offers);
//        return "offerSearch";
         applications=new ArrayList<>();
        for(Offer offer: offers){
            List<Application> applicationList= applicationService.getAppByOffer(offer);
            for (Application application: applicationList){
                if(!applications.contains(application))
                        applications.add(application);
            }
        }
        model.addAttribute("applicationsRecruiter", applications);
        return "offersApplications";

    }

    @GetMapping("/alljobs")
    public String getAllOffers(Model model){
        model.addAttribute("offers",offerService.getAllOffers());
        model.addAttribute("nbrAll",offerService.getAllOffers().size());
        return "alljobs";
    }
    @GetMapping("/alljobs/detail/{id}")
    public String showDetailsOffer_allJobs(@PathVariable(value = "id") ObjectId id , Model model){
        Optional<Offer> offer = offerService.getOfferById(id);
        model.addAttribute("offer", offer.get());
        model.addAttribute("recruiter", offer.get().getRecruiter());
        return "alljobs_details";
    }
    @PostMapping("/search/inAllOffers")   /** Rech dans tous offres  **/
    @CrossOrigin
    public String searchInAllOffers(@RequestParam(name ="keyword") String text,
                                   @RequestParam(name ="salary") Long salary,
                                   @RequestParam(name ="location") String location,
                                   Model model){


        List<Offer> offers = offerService.getOfferByText(text); /** Tous les offres du rech par text **/

        if(salary != 0L)
        {
            List<Offer> salaries = offerService.getOfferBySalary(salary);
            if(!salaries.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : salaries) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }
        }
        if(!location.isEmpty()){
            List<Offer> offersLocation = offerService.getOfferByText(location);
            if(!offersLocation.isEmpty())
            {
                List<Offer> list = new ArrayList<>();
                for (Offer offer : offersLocation) {
                    if (offers.contains(offer))
                        list.add(offer);
                }
                offers=list;
            }

        }

        model.addAttribute("offers",offers);
        model.addAttribute("nbrAll",offers.size());
        return "alljobs";

    }

//    @PostMapping("/search")   //Recherche par texte + sort Ascendant selon experience
//    @CrossOrigin
//    public String search(@RequestParam(name ="rech") String text, Model model){
//            List<Offer> offers = offerService.getOfferByText(text);
//            model.addAttribute("textOffers", offers);
//            return "offerSearch";
//    }


//    @PostMapping("/search/salary")   //Recherche par salaire
//    public String searchS(@RequestParam(name ="rechSalary") Long salary, Model model){
//        List<Offer> offers = offerService.getOfferBySalary(salary);
//        model.addAttribute("textOffers", offers);
//        return "offerSearch";
//    }
//
//    @PostMapping("/search/experience")   //Recherche par salaire
//    public String searchE(@RequestParam(name ="experience")String experience, Model model){
//        List<Offer> offers = offerService.getOfferByExperience(experience);
//        model.addAttribute("textOffers", offers);
//        return "offerSearch";
//    }
//    @PostMapping("/search/skills")   //Recherche par salaire
//    public String searchM(@RequestParam(name ="skills") List<String> skills, Model model){
//        List<Offer> offers = offerService.getOfferBySkills(skills);
//        model.addAttribute("textOffers", offers);
//        return "offerSearch";
//    }
//    @PostMapping("/search/location")   //Recherche par salaire
//    public String searchL(@RequestParam(name ="location") String location, Model model){
//        List<Offer> offers = offerService.getOfferByLocation(location);
//        model.addAttribute("textOffers", offers);
//        return "offerSearch";
//    }






    @GetMapping("/offersForCandidates")  /** Afficher tous les offres dans la page d'accueil du candidat **/
    @CrossOrigin
    public String getOffers(Model model,HttpServletRequest request) {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Resume resume = resumeService.getResumeByCandidate(candidate);
        /** recuperer skills du candidat dans la session **/
        List<String> skills = resume.getSkills();
        String skillsStr ="";
        for(String skill : skills)
        {
            skillsStr += ","+ skill;
        }
        List<Offer> offersSkills = offerService.getOfferByText(skillsStr);
        /** Calculer la similarité entre la requête et chaque document retourné**/
        for(Offer offer: offersSkills)
        {
            List<String> skillsOffer= offer.getSkills();
            Set<Object> similaritySet = offerService.similarity(skills,skillsOffer);
            //Application application = applicationService.getAppByCandidateAndOffer(candidate,offer);
            /** recuperer key /value **/
            for (Object obj : similaritySet) {
                if (obj instanceof Integer) {
                    Integer similarity = (Integer) obj;
                    //application.setSimilarity(similarity);
                    offer.setSimilarity(similarity);
                } else if (obj instanceof List<?>) {
                    List<String> missingSkills = (List<String>) obj;
                    //application.setMissingSkills(missingSkills);
                    offer.setMissingSkills(missingSkills);
                }
            }
        }
        /**Trier les offres par salary décroissant **/
        Collections.sort(offersSkills, new Comparator<Offer>() {
            public int compare(Offer e1, Offer e2) {
                return Double.compare(e2.getSimilarity(), e1.getSimilarity());
            }
        });

        model.addAttribute("offers",offersSkills);

        /** Other Offers **/
        List<Offer> listOffers = offerService.getAllOffers();
        List<Offer> list = new ArrayList<>();
        for (Offer offer : listOffers) {
            if (!offersSkills.contains(offer))
                list.add(offer);
        }
        model.addAttribute("OtherOffers", list);
        return  "mySpaceCandidate";
    }

    /*** Apply ***/
    @GetMapping("/offers/apply/{id}")
    public String apply ( @PathVariable (value = "id") ObjectId id ,HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Optional<Offer> offer = offerService.getOfferById(id);
        Recruiter recruiter = offer.get().getRecruiter();


//        List<Candidate> candidates = offer.get().getCandidates();
//        if( ! candidates.contains(candidate))
//            candidates.add(candidate);
//          model.addAttribute("offersApp", offer);

        Application application = new Application();
        application.setOffer(offer.get());
        application.setCandidate(candidate);
        application.setRecruiter(recruiter);
        applicationService.createApplicataion(application);

        return  "redirect:/offersForCandidates";
    }




}