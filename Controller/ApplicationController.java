package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.*;
import com.enit.Erecruitement.Service.EmailSenderService;
import com.enit.Erecruitement.Service.OfferService;
import com.enit.Erecruitement.Service.ResumeService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.enit.Erecruitement.Service.ApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@Controller
public class ApplicationController {

    @Autowired ApplicationService applicationService;
    @Autowired EmailSenderService emailSenderService;
    @Autowired  OfferService offerService;
    @Autowired ResumeService resumeService;

    private List<Application> applicationsRecruiter;

    @GetMapping("/recruiters/checkApplications")
    public String checkApplications(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");

        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        /** Recuperer les données pour calculer la saimilarité **/
        for(Application application: applications){
            //System.out.println("Candidate :" + application.getCandidate());
            Resume resume = resumeService.getResumeByCandidate(application.getCandidate());
            //System.out.println("resume :" + resume);
            List<String> skills = resume.getSkills();  //recuperer skills du candidat
            List<String> skillsOffer = application.getOffer().getSkills();
            /** Calculer la similarité entre la requête et chaque document retourné**/

            Set<Object> similaritySet = offerService.similarity(skills,skillsOffer);
            /** recuperer key /value **/
            for (Object obj : similaritySet) {
                if (obj instanceof Integer) {
                    Integer similarity = (Integer) obj;
                    application.setSimilarity(similarity);
                    applicationService.saveApplication(application);
                } else if (obj instanceof List<?>) {
                    List<String> missingSkills = (List<String>) obj;
                    application.setMissingSkills(missingSkills);
                    applicationService.saveApplication(application);
                }
            }
        }

        model.addAttribute("applicationsRecruiter", applications);
        model.addAttribute("all", applications.size());
        Integer accepted =0;
        Integer notseen =0;
        Integer rejected =0;
        for (Application application: applications){
            if(application.getConfirmation() != null && application.getConfirmation()==true)
                accepted++;
            else if (application.getConfirmation() != null && application.getConfirmation()==false) {
                rejected++;
            }else {
                notseen++;
            }
        }
        model.addAttribute("accepted", accepted);
        model.addAttribute("notseen", notseen);
        model.addAttribute("rejected", rejected);

        return "offersApplications";
    }

    @GetMapping("/applications/all")
    public  String showAllApps ()
    {
        return  "redirect:/recruiters/checkApplications";
    }
    @GetMapping("/applications/accepted")
    public  String showAcceptedApps (Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");
        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        List<Application> apps=new ArrayList<>();
        for (Application app : applications){
            Boolean confirmation = app.getConfirmation();
            if(confirmation!=null && app.getConfirmation()== true)
                apps.add(app);
        }
        model.addAttribute("applicationsRecruiter", apps);
        return "offersApplications";
    }
    @GetMapping("/applications/rejected")
    public  String showRejectedApps (Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");
        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        List<Application> apps=new ArrayList<>();
        for (Application app : applications){
            if(app.getConfirmation()!= null && app.getConfirmation() == false)
                apps.add(app);
        }
        model.addAttribute("applicationsRecruiter", apps);
        return "offersApplications";
    }
    @GetMapping("/applications/notseen")
    public  String showNotSeenApps (Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");
        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        List<Application> apps=new ArrayList<>();
        for (Application app : applications){
            if(app.getConfirmation() == null)
                apps.add(app);
        }
        model.addAttribute("applicationsRecruiter", apps);
        return "offersApplications";
    }


//    @PostMapping("/apps/affichage")  /** Affichage des application : ALl/accepted/rejected/notProcessed **/
//    public String submitForm(@RequestParam("selectedTab") String selectedValue, Model model) {
//        if ("all-job".equals(selectedValue)) {
//            //return  "redirect:/recruiters/checkApplications";
//        } else if ("accepted-job".equals(selectedValue)) {
//            List<Application> apps=new ArrayList<>();
//            for (Application app : applicationsRecruiter){
//                if(app.getConfirmation())
//                    apps.add(app);
//            }
//            model.addAttribute("applicationsRecruiter", apps);
//            return "offersApplications";
//
//        } else if ("notProcessed-job".equals(selectedValue)) {
//            List<Application> apps=new ArrayList<>();
//            for (Application app : applicationsRecruiter){
//                if(app.getConfirmation() == null)
//                    apps.add(app);
//            }
//            model.addAttribute("applicationsRecruiter", apps);
//            return "offersApplications";
//        } else if ("rejected-job".equals(selectedValue)) {
//            List<Application> apps=new ArrayList<>();
//            for (Application app : applicationsRecruiter){
//                if(app.getConfirmation() == false)
//                    apps.add(app);
//            }
//            model.addAttribute("applicationsRecruiter", apps);
//            return "offersApplications";
//        }
//        return  "redirect:/recruiters/checkApplications";
//    }
    @GetMapping("/application/accept/{id}")
    public String acceptApplication(@PathVariable(value = "id") ObjectId id,Model model){
        Optional<Application> application = applicationService.getAppById(id);
        model.addAttribute("currentApplication", application.get());

        application.get().setConfirmation(true);
        applicationService.createApplicataion(application.get());

        emailSenderService.SendEmailAcceptance(application.get());
        //emailSenderService.SendEmailInterview(application.get(),date,time);

        /**Send Notification to the candidate **/
        NotificationSender sender = new NotificationSender();
        NotificationReceiver receiver = new NotificationReceiver();

        sender.addObserver(receiver);

        // Send a notification
        sender.setMessage("New message!");

        // Remove observer
        sender.deleteObserver(receiver);
        return "redirect:/recruiters/checkApplications";

    }
    @GetMapping("/application/accept/sendEmail/{id}")
    public String acceptInterview(@PathVariable(value = "id") ObjectId id,
                                    @RequestParam("date1") String date,
                                   @RequestParam("time1") String time
    ){
        Optional<Application> application = applicationService.getAppById(id);
        /** Enregistrer date / time de l'Interview **/
        application.get().setDateInterview(date);
        application.get().setTimeInterview(time);
        applicationService.createApplicataion(application.get());

        //emailSenderService.SendEmailInterview(application.get(),date,time);
        emailSenderService.SendEmailInterview(application.get());

        return "redirect:/recruiters/checkApplications";

    }

    @GetMapping("/application/reject/{id}")
    public String rejectApplication(@PathVariable(value = "id") ObjectId id){
        Optional<Application> application = applicationService.getAppById(id);
        application.get().setConfirmation(false);
        application.get().setDateInterview(null);
        application.get().setTimeInterview(null);
        applicationService.createApplicataion(application.get());
        emailSenderService.SendEmailRejection(application.get());
        return "redirect:/recruiters/checkApplications";

    }
    @GetMapping("/application/detail/{id}")
    public String showDetailsOffer(@PathVariable(value = "id") ObjectId id , Model model){
        Optional<Application> application = applicationService.getAppById(id);
        System.out.println("application : "+ application);
        model.addAttribute("recruiter", application.get().getRecruiter());
        model.addAttribute("offer", application.get().getOffer());
        return "offerDetail";
    }

    @GetMapping("/application/resume/{id}") /** Recruiter can see the resume of a candidate who applied **/
    public ResponseEntity<InputStreamResource> showResumeById (@PathVariable(value = "id") ObjectId id) throws FileNotFoundException {
        Optional<Application> application = applicationService.getAppById(id);
        Candidate candidate = application.get().getCandidate();
        Resume resume = resumeService.getResumeByCandidate(candidate);
        File cv= ResourceUtils.getFile(resume.getFilePath());
        String fileName =cv.getName();

        String pdfPath = cv.getAbsolutePath();

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=" +fileName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(cv));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(cv.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    @GetMapping("/recruiters/checkInterviews")
    public String checkInterviews(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Recruiter recruiter =(Recruiter) session.getAttribute("currentUser");

        List<Application> applications = applicationService.getAppByRecruiter(recruiter);
        applicationService.sortByDateInterview(applications);
        model.addAttribute("applications", applications);
        return "interviews";
    }
    @GetMapping("/candidates/checkInterviews")
    public String checkInterviewsCandidate(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Candidate candidate =(Candidate) session.getAttribute("currentUser");

        List<Application> applications = applicationService.getAppByCandidate(candidate);
        applicationService.sortByDateInterview(applications);
        model.addAttribute("applications", applications);
        return "interviews";
    }


    @GetMapping("/application/check") /** Verifier mes applications (candidat) **/
    public String showMyApplications(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        List<Application> applications = applicationService.getAppByCandidate(candidate);
        model.addAttribute("applications", applications);
        return "myApplications";
    }
    @GetMapping("/application/delete/{id}") /** retirer une application (candidat) **/
    public String deleteApplication(@PathVariable (value = "id") ObjectId id)
    {
        applicationService.deleteApplication(id);
        return "redirect:/application/check";
    }

    @GetMapping("/application/waiting") /** (candidat) **/
    public String myApplicationWaiting(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        List<Application> applications=new ArrayList<>();
        for(Application app : applicationService.getAppByCandidate(candidate)){
            if(app.getConfirmation()==null) {
                applications.add(app);
            }}
        //List<Application> applications=applicationService.getAppByCandidate(candidate);
        model.addAttribute("applications", applications);
        return "myApplicationsWaiting";
    }

    @GetMapping("/application/success") /** (candidat) **/
    public String myApplicationSuccess(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        List<Application> applications=new ArrayList<>();
        for(Application app : applicationService.getAppByCandidate(candidate)){
            if(app.getConfirmation()==true) {
                applications.add(app);
            }}
        model.addAttribute("applications", applications);
        return "myApplicationsSuccess";
    }
    @GetMapping("/application/rejected") /** (candidat) **/
    public String myApplicationRejected(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        List<Application> applications=new ArrayList<>();
        for(Application app : applicationService.getAppByCandidate(candidate)){
            if(app.getConfirmation()==false) {
                applications.add(app);
            }}
        model.addAttribute("applications", applications);
        return "myApplicationsRejected";
    }

}
