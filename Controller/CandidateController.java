package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Resume;
import com.enit.Erecruitement.Service.CandidateService;
import com.enit.Erecruitement.Service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/candidates")
@Controller
public class CandidateController {
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private ResumeService resumeService;

    @GetMapping("/candidates/")
    public String getAllCandidates(Model model) {
        List<Candidate> listCandidates = candidateService.getAllCandidates();
        model.addAttribute("candidates", listCandidates);
        return "candidates";
    }
    @GetMapping("/candidates/showRegisterFormCandidate")
    public String createCandidateForm(Model model)
    {
        Candidate candidate = new Candidate();
        model.addAttribute("candidate",candidate);
        return "createCandidate";
    }
    @PostMapping("/candidates/create")
    public String createCandidate (@ModelAttribute("candidate") Candidate candidate)
    {
        candidateService.createCandidate(candidate);
        return "redirect:/login";
    }
    @GetMapping("/candidates/showFormForUpdate/{id}")
    public String updateCandidateForm (@PathVariable( value = "id") ObjectId id, Model model) {
        Candidate candidate = candidateService.getCandidateById(id);
        model.addAttribute("candidate", candidate);
        return "editAccountCandidate";
    }

    @GetMapping("/candidates/delete/{id}")
    public String deleteCandidate(@PathVariable (value = "id") ObjectId id) {
        candidateService.deleteCandidate(id);
        return "redirect:/candidates/";
    }
    @GetMapping("/candidates/profil")
    public String deleteCandidate(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Resume resume=resumeService.getResumeByCandidate(candidate);
        model.addAttribute("resume", resume);
        return "CandidateProfil";
    }

}
