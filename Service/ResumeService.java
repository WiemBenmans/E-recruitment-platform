package com.enit.Erecruitement.Service;


import com.enit.Erecruitement.Candidate;
import com.enit.Erecruitement.Offer;
import com.enit.Erecruitement.Repository.ResumeRepository;
import com.enit.Erecruitement.Repository.SearchRepository;
import com.enit.Erecruitement.Resume;
import com.mongodb.client.FindIterable;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired private SearchRepository searchRepository;
    @Autowired private  OfferService offerService;
    public Optional<Resume> getResumeById(ObjectId id){
        return  resumeRepository.findById(id);
    }

    public Resume createResume(Resume resume, Candidate candidate) {
        resume.setCandidate(candidate);
        return resumeRepository.save(resume);
    }

    public Resume getResumeByCandidate(Candidate candidate) {

        return resumeRepository.findByCandidate(candidate);
    }

    public boolean deleteResume(ObjectId idR) {

        Resume resume = resumeRepository.findById(idR).orElse(null);
        if (resume == null) {
            return false;
        }
        resumeRepository.delete(resume);
        return true;
    }

    public String readResume(File file) throws FileNotFoundException, TesseractException {
        /*BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext pcontext = new ParseContext();

        //parsing the document using PDF parser
        PDFParser pdfparser;
        pdfparser = new PDFParser();
        pdfparser.parse(inputstream, handler, metadata,pcontext);

        //getting the content of the document
        System.out.println("Contents of the PDF :" + handler.toString());

        //getting metadata of the document
        System.out.println("Metadata of the PDF:");
        String[] metadataNames = metadata.names();

        for(String name : metadataNames) {
            System.out.println(name + " : " + metadata.get(name));
        }
    }*/
        // Extract text from PDF file using Tesseract


        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("/path/to/tessdata");
        //String text = tesseract.doOCR(new File("/path/to/resume.pdf"));
        String text = tesseract.doOCR(file);
        return text;

    }

    public String extractText(MultipartFile file) {
        Tika tika = new Tika();
        try {
            return tika.parseToString(file.getInputStream());
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Failed to extract text from file", e);
        }
    }

    public Resume parseResume(String text) {
        // Implement parsing logic here
        return new Resume();
    }

    /**** Pour la recherche ***/
    public List<Resume> getResumeByText(String text){
        return searchRepository.findResumeByText(text);
    }


    /** Suggestion de CVs  selon offre pour recruteur : Seulement si similarity >= 60% & Sorted  **/
    public Map<Integer, List<Resume>> getResumesBySkills(Offer offer)
    {
        Map<Integer, List<Resume>> resumesSuggestions = new TreeMap<>(Comparator.reverseOrder()); //Map à retourner
        List<String> skillsOffer = offer.getSkills();
        List<Resume> resumes = resumeRepository.findAll();
        for(Resume resume: resumes){
            List<String> skills = resume.getSkills();
            Set<Object> similaritySet= new HashSet<>();
            similaritySet = offerService.similarity(skills, skillsOffer);
            /** recuperer key /value **/
            for (Object obj : similaritySet) {
                if (obj instanceof Integer) {
                    Integer similarity = (Integer) obj;
                    if(similarity >= 30){
                        if(resumesSuggestions.containsKey(similarity)){
                            resumesSuggestions.get(similarity).add(resume);
                        }else{
                            List<Resume> resumeList=new ArrayList<>();
                            resumeList.add(resume);
                            resumesSuggestions.put(similarity,resumeList);
                        }

                    }
                }
            }
        }
        return resumesSuggestions;
    }

    public List<Resume> getAllResumes(){
        return resumeRepository.findAll();
    }


}