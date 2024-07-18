package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.UserRepository;
import com.enit.Erecruitement.Service.CandidateService;
import com.enit.Erecruitement.Service.OfferService;
import com.enit.Erecruitement.Service.RecruiterService;
import com.enit.Erecruitement.Service.UserService;
import com.enit.Erecruitement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class UserController {
    @Autowired  private UserService userService;
    @Autowired  private UserRepository userRepository;

    @Autowired  private CandidateService candidateService;
    @Autowired  private RecruiterService recruiterService;
    @Autowired private OfferService offerService;

    @GetMapping("/")
    public String getAllCandidates(Model model) {
        List<Candidate> listCandidates = candidateService.getAllCandidates();
        model.addAttribute("candidates", listCandidates);
        return "candidates";
    }
//    @GetMapping("/")
//    public String getAllUsers(Model model) {
//        List<User> list = userRepository.findAll();
//        model.addAttribute("candidates", list);
//        return "candidates";
//    }  Cannot map because there is no collection named user

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(name ="email") String email,
                        @RequestParam("password") String password, Model model,  HttpSession session) {

        if(recruiterService.connect(email,password)) {
            Recruiter currentUser =   recruiterService.getRecruiterByEmailAndPassword(email, password);
            session.setAttribute("currentUser", currentUser);
            List<Offer> listOffers = offerService.getOffersByRecruiter(currentUser);
            model.addAttribute("nbrAll", listOffers.size());
            model.addAttribute("offers", listOffers);

            return "mySpaceRecruiter";
        } else if (candidateService.connect(email,password)) {
            Candidate currentUser =  candidateService.getRecruiterByEmailAndPassword(email, password);
            session.setAttribute("currentUser", currentUser);
            //return "mySpaceCandidate";
            return "redirect:/offersForCandidates";
        } else {
            model.addAttribute("message", "Email or password is incorrect");
            return "login";
        }

    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Supprimer l'utilisateur de la session
        session.removeAttribute("currentUser");
        // Rediriger l'utilisateur vers la page de connexion
        return "redirect:/login";
    }

    @GetMapping("/editAccount")
    public String editAccount(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser instanceof Recruiter){
            model.addAttribute("currentUserR", (Recruiter)currentUser);
            return "editAccountRecruiter";
        }
        else if  (currentUser instanceof Candidate)
        {
            model.addAttribute("currentUserC", (Candidate)currentUser);
            return  "editAccountCandidate";
        }

        else
            return  "redirect:/login"; // A changer!

    }


    @GetMapping("/delete")
    public String deleteUser( HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser instanceof Recruiter){
            //recruiterService.deleteRecruiter(currentUser.getId());
            Recruiter r = (Recruiter) currentUser;
            recruiterService.deleteRecruiter(r.getId());
        }
        else if  (currentUser instanceof Candidate){
            //candidateService.deleteCandidate(currentUser.getId());
            Candidate c = (Candidate) currentUser;
            candidateService.deleteCandidate(c.getId());
        }

        return  "redirect:/login";

    }

    /***************************************
    @GetMapping("/offers")
    public String getOffersOfRecruiter(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("currentUser");
        Recruiter recruiter = (Recruiter) currentUser;
        List<Offer> listOffers = offerService.getOffersByRecruiter(recruiter);
        model.addAttribute("offers", listOffers);
        return  "offers";
    }
    @PostMapping("/offers/create")
    public String createOffer (@ModelAttribute("offer") Offer offer, HttpSession session)
    {
        Recruiter recruiter = (Recruiter) session.getAttribute("currentUser");
        offerService.createOffer(offer,recruiter) ;
        return "redirect:/offers";
    }

     *******/
}

