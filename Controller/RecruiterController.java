package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.Application;
import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Service.ApplicationService;
import com.enit.Erecruitement.Service.OfferService;
import com.enit.Erecruitement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
//import ch.qos.logback.core.model.Model;
import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Service.RecruiterService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;


//@RestController
//@RequestMapping("/recruiters")
@Controller
public class RecruiterController {
    @Autowired private RecruiterService recruiterService;
    @Autowired private OfferService offerService;
    @Autowired private ApplicationService applicationService;


    @GetMapping("/mySpaceRecruiter")
    public String mySpace()
    {
        return "mySpaceRecruiter";
    }
    @GetMapping("/recruiters/")
    public String getAllRecruiters(Model model) {
        List<Recruiter> listRecruiters = recruiterService.getAllRecruiters();
        model.addAttribute("recruiters", listRecruiters);
        return "recruiters";
    }
    @GetMapping("/recruiters/{id}")
    public Recruiter getRecruiterById(@PathVariable("id") ObjectId id) {
        return recruiterService.getRecruiterById(id);
    }

    @GetMapping("/recruiters/login")
    public String showLoginPage(){
        return "login";
    }

    @PostMapping("/recruiters/login")
    public String login(@RequestParam(name ="email") String email,
                        @RequestParam("password") String password, Model model) {
            if(recruiterService.connect(email, password)) {
                // Le login et mot de passe sont corrects
                // si le type de retour : ResponseEntity<String>
                // return ResponseEntity.ok().build();
                model.addAttribute("message", "Signed in successfully !");
                return "mySpace";
            } else {
                // Le login et mot de passe sont incorrects
                //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                model.addAttribute("message", "Email or password is incorrect");
                return "login";
            }
    }

    @GetMapping("/recruiters/showRegisterForm")
    public String createRecruiterForm(Model model)
    {
        Recruiter recruiter =new Recruiter();
        model.addAttribute("recruiter",recruiter);
        return "createRecruiter";
    }
    @PostMapping("/recruiters/create")
    public String createRecruiter (@ModelAttribute("recruiter") Recruiter recruiter)
    {
        recruiterService.createRecruiter(recruiter);
        return "redirect:/login";
    }
    @GetMapping("/recruiters/showFormForUpdate/{id}")
    public String updateRecruiterForm (@PathVariable ( value = "id") ObjectId id, Model model) {
        Recruiter recruiter = recruiterService.getRecruiterById(id);
        model.addAttribute("recruiter", recruiter);
        return "editAccountRecruiter";
    }
    @GetMapping("/recruiters/delete/{id}")
    public String deleteRecruiter(@PathVariable (value = "id") ObjectId id) {
        recruiterService.deleteRecruiter(id);
        return "redirect:/recruiters/";
    }

    @GetMapping("/recruiters/checkOffers")
    public String checkOffers () {

        return "redirect:/offers";
    }

    @GetMapping("/myProfile")
    public String profile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("recruiter",(Recruiter)currentUser);

        List<Offer> offers = offerService.getOffersByRecruiter((Recruiter)currentUser);
        model.addAttribute("offers",offers.size());

        List<String> companies = new ArrayList<>();
        for(Offer offer: offers){
            if(!companies.contains(offer.getCompanyName()))
                companies.add(offer.getCompanyName());
        }
        model.addAttribute("companies", companies.size());

        List<Application> applications=applicationService.getAppByRecruiter((Recruiter)currentUser);
        model.addAttribute("applications", applications.size());

        Integer interviews = 0;
        for(Application application: applications){
            if(application.getConfirmation()!= null && application.getConfirmation()==true)
                interviews++;
        }
        model.addAttribute("interviews", interviews);

        return "recruiterProfile";
    }

    //    public ResponseEntity<Recruiter> createRecruiter(@RequestBody Recruiter recruiter) {
//        try {
//            //recruiterService.createRecruiter(recruiter);
//            return new ResponseEntity<>(recruiterService.createRecruiter(recruiter), HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


    //    @PutMapping("/update/{id}")
//    public ResponseEntity<Recruiter> updateRecruiter(@PathVariable("id") ObjectId id, @RequestBody Recruiter recruiter) {
//        if (recruiterService.updateRecruiter(id, recruiter) != null) {
//            //return new ResponseEntity<>( HttpStatus.OK);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }



//    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.PUT})
//    public Recruiter updateRecruiter1( @RequestBody Recruiter recruiter) {
//        return recruiterService.update(recruiter);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<HttpStatus> deleteRecruiter(@PathVariable("id") ObjectId id) {
//        try {
//            recruiterService.deleteRecruiter(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }




}
