package com.enit.Erecruitement.Controller;

import com.enit.Erecruitement.*;
import com.enit.Erecruitement.Service.ResumeService;
import com.itextpdf.html2pdf.HtmlConverter;
import com.lowagie.text.DocumentException;
import com.mongodb.client.FindIterable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.groupdocs.parser.Parser;
import com.groupdocs.parser.data.PageImageArea;
import com.groupdocs.parser.options.ImageFormat;
import com.groupdocs.parser.options.ImageOptions;


import com.lowagie.text.DocumentException;


import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;

import com.spire.pdf.texts.PdfTextExtractOptions;
import com.spire.pdf.texts.PdfTextExtractor;
import jakarta.servlet.http.HttpServletRequest;


import javax.annotation.processing.AbstractProcessor;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.logging.ErrorManager;

import static java.awt.SystemColor.text;
import static org.springframework.util.StringUtils.cleanPath;

@Controller
public class ResumeController {


    @Autowired
    private ResumeService resumeService;
    @Autowired
    private TemplateEngine templateEngine;

    /*** Recherche de Resume ***/
//    @PostMapping("/searchResume")   //Recherche par texte + sort descendant selon experience
//    @CrossOrigin
//    public String search(@RequestParam(name ="rechResume") String text, Model model){
//        List<Resume> resumes = resumeService.getResumeByText(text);
//        model.addAttribute("resumes", resumes);
//        return "resumeSearch";
//    }
    @PostMapping("/searchResume")   //Recherche par texte + sort descendant selon experience
    @CrossOrigin
    public String search(@RequestParam(name ="rechResume") String text,
                         @RequestParam(name ="experience")String experience ,
                         @RequestParam(name ="skills") String skills, Model model){


        List<Resume> resumes = resumeService.getResumeByText(text); /** text can't be null bcz it's required in html **/
        if(!experience.isEmpty())
        {
            List<Resume> resumesExp = resumeService.getResumeByText(experience);
            if(!resumesExp.isEmpty())
            {
                List<Resume> list = new ArrayList<>();
                for (Resume resume : resumesExp) {
                    if (resumes.contains(resume))
                        list.add(resume);
                }
                resumes=list;
            }
        }else {
            experience="+0";
        }

        if(!skills.isEmpty())
        {
            List<Resume> resumesSkills = resumeService.getResumeByText(skills);
            if(!resumesSkills.isEmpty())
            {
                List<Resume> list = new ArrayList<>();
                for (Resume resume : resumesSkills) {
                    if (resumes.contains(resume))
                        list.add(resume);
                }
                resumes=list;
            }
        }

        model.addAttribute("resumes", resumes);
        model.addAttribute("nbrAll",resumes.size());
        return "resumeSearch";
    }
    /****************************/



    @GetMapping("/resumeForm")
    public String createResumeForm(Model model)
    {

        Resume resume =new Resume();

        model.addAttribute("resume",resume);

        return "createResume";


    }
    @PostMapping("/createResumeBeforeEdu")
    public String createResumeBeforeEdu(@ModelAttribute("resume") Resume resume, @RequestParam(name="image") MultipartFile fileI, HttpServletRequest request, Model model) throws IOException {
        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Resume test=resumeService.getResumeByCandidate(candidate);
        Optional<Resume> optional = Optional.ofNullable(test);

        if(optional.isPresent())
        {
            resumeService.deleteResume(test.getIdResume());
        }
        String filename = cleanPath(fileI.getOriginalFilename());


        //if(filename!="")
        Path path1 = Paths.get("images/");
        Files.createDirectories(path1);
        Files.copy(fileI.getInputStream(), path1.resolve(candidate.getName() +
                candidate.getSurname() + ".png"), StandardCopyOption.REPLACE_EXISTING);

        resume.setExperiencesDescription(new ArrayList<Experience>());
        resume.setEducations(new ArrayList<Education>());

        resumeService.createResume(resume, candidate) ;

        Education education=new Education();
        model.addAttribute("education",education);

        return "create-resume-education";
    }

    @PostMapping("/createEducationList")
    public String createResumeEducationList(@ModelAttribute("education") Education education, HttpServletRequest request, Model model)
    {
        if(education != null) {

            HttpSession session = request.getSession();
            Candidate candidate = (Candidate) session.getAttribute("currentUser");
            Resume test=resumeService.getResumeByCandidate(candidate);
            //Optional<Resume> optional = Optional.ofNullable(test);

            //resumeService.deleteResume(test.getIdResume());

            List<Education> edu = test.getEducations();
            edu.add(education);
            test.setEducations(edu);
            resumeService.createResume(test, candidate);
        }

        Experience experience=new Experience();
        model.addAttribute("experience",experience);

        return "create-resume-experience";
    }

    @PostMapping("/createEducation")
    public String createEducation(@ModelAttribute("education")Education education, HttpServletRequest request,Model model)
    {
        if(education != null) {
            HttpSession session = request.getSession();
            Candidate candidate = (Candidate) session.getAttribute("currentUser");
            Resume test = resumeService.getResumeByCandidate(candidate);
            //Optional<Resume> optional = Optional.ofNullable(test);

            //resumeService.deleteResume(test.getIdResume());

            List<Education> edu = test.getEducations();
            edu.add(education);
            test.setEducations(edu);
            resumeService.createResume(test, candidate);
        }
        model.addAttribute("education", new Education());
        return "create-resume-education";
    }
    @PostMapping("/createExperienceList")
    public String createResumeExperienceList(@ModelAttribute("experience") Experience experience, HttpServletRequest request, Model model) throws FileNotFoundException, DocumentException {


        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Resume test = resumeService.getResumeByCandidate(candidate);
        //Optional<Resume> optional = Optional.ofNullable(test);

        //resumeService.deleteResume(test.getIdResume());
        if(experience != null) {
            List<Experience> edu = test.getExperiencesDescription();
            edu.add(experience);
            test.setExperiencesDescription(edu);
            resumeService.createResume(test, candidate);

        }


        Context context = new Context();
        Map<String, Object> data= new HashMap<>();
        data.put("resume",test);
        context.setVariables(data);

        String htmlContent = templateEngine.process("resumePDF", context);

        FileOutputStream fileOutputStream = new FileOutputStream(test.getFilePath());
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(fileOutputStream, false);
        renderer.finishPDF();





        return "mySpaceCandidate";
        //model.addAttribute("experience",new Experience());


    }

    @PostMapping("/createExperience")
    public String createExperience(@ModelAttribute("experience")Experience experience, HttpServletRequest request,Model model)
    {
        if(experience != null) {
            HttpSession session = request.getSession();
            Candidate candidate = (Candidate) session.getAttribute("currentUser");
            Resume test = resumeService.getResumeByCandidate(candidate);
            //Optional<Resume> optional = Optional.ofNullable(test);

            //resumeService.deleteResume(test.getIdResume());

            List<Experience> edu = test.getExperiencesDescription();
            edu.add(experience);
            test.setExperiencesDescription(edu);
            resumeService.createResume(test, candidate);
        }

        model.addAttribute("experience", new Experience());
        return "create-resume-experience";
    }
    /*@PostMapping("/createResumeWithForm")
    public String createResume (@ModelAttribute("resume") Resume resume, @RequestParam(name="image") MultipartFile fileI, HttpServletRequest request, Model model) throws IOException, DocumentException {


        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        Resume test=resumeService.getResumeByCandidate(candidate);
        Optional<Resume> optional = Optional.ofNullable(test);

        if(optional.isPresent())
        {
            resumeService.deleteResume(test.getIdResume());
        }

        //BufferedImage image = (BufferedImage) fileI;
        String filename = cleanPath(fileI.getOriginalFilename());


        //if(filename!="")
        Path path1 = Paths.get("images/");
        Files.createDirectories(path1);
        Files.copy(fileI.getInputStream(), path1.resolve(candidate.getName() +
                candidate.getSurname() + ".png"), StandardCopyOption.REPLACE_EXISTING);



        resumeService.createResume(resume, candidate) ;

        model.addAttribute("resume",resume);
        //model.setAttribute("resume", resume);


        //File file=new File(candidate.getName()+candidate.getSurname()+".pdf");
        //HtmlConverter.convertToPdf(new File("C:\\Users\\wiemb\\Downloads\\Erecruitement-master\\src\\main\\resources\\templates\\resumePDF.html"),file);

        Context context = new Context();
        Map<String, Object> data= new HashMap<>();
        data.put("resume",resume);
        context.setVariables(data);

        String htmlContent = templateEngine.process("resumePDF", context);

        FileOutputStream fileOutputStream = new FileOutputStream(resume.getFilePath());
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(fileOutputStream, false);
        renderer.finishPDF();


        return "mySpaceCandidate";
    }*/


    /*@PostMapping("/createResumeWithForm")
    public String createResume (@ModelAttribute("resume") Resume resume, HttpServletRequest request, Model model)
    {

        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        resumeService.createResume(resume, candidate) ;
        model.addAttribute("resume", resume);



        //resumeService.createResume(resume);
        return "resumePDF";
    }*/

    /*@PostMapping("/createResumeWithForm")
    public String createResume (@ModelAttribute("resume") Resume resume, HttpServletRequest request, Model model)
    {

        HttpSession session = request.getSession();
        Candidate candidate = (Candidate) session.getAttribute("currentUser");
        resumeService.createResume(resume, candidate) ;
        model.addAttribute("resume", resume);



        //resumeService.createResume(resume);
        return "resumePDF";
    }*/




    /*@GetMapping("/resumes")
    public String resume(HttpSession session, Model model) {
        Candidate currentUser = (Candidate) session.getAttribute("currentUser");
        Resume resume=resumeService.getResumeByCandidate(currentUser);
        Optional<Resume> optional = Optional.ofNullable(resume);
        if (optional.isPresent())
        {
            return  "resumeEx";
        }
        else {

            return  "resumeNEx";}

    }*/






    @GetMapping("/editToResumeForm")
    public String editResumeForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        Resume resume = resumeService.getResumeByCandidate((Candidate) currentUser);
        model.addAttribute("resumeF", resume);

        return  "editResume";
    }

   /* @PostMapping("/resumes/create")
    public String createResume (@ModelAttribute("resume") Resume resume)
    {
        resumeService.createResume(resume,resume.getCandidate());
        return "mySpaceCandidate";
    }*/

    @GetMapping("/deleteResume")
    public String deleteResume( HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");


        Resume resume= resumeService.getResumeByCandidate((Candidate) currentUser);
        resumeService.deleteResume(resume.getIdResume());



        return  "mySpaceCandidate";

    }

    @GetMapping("upResume")
    public String toUploadResume()
    {
        return "uploadResume";
    }




    @PostMapping("/resumes/upload")
    public String uploadFile(@RequestParam(name="file") MultipartFile file,HttpSession session, Model model) throws IOException{


        Candidate currentUser = (Candidate) session.getAttribute("currentUser");
        Resume test=resumeService.getResumeByCandidate(currentUser);
        Optional<Resume> optional = Optional.ofNullable(test);

        if(optional.isPresent())
        {
            resumeService.deleteResume(test.getIdResume());
        }

        // Get the filename and extension of the uploaded file
        String filename = cleanPath(file.getOriginalFilename());
        //String extension = FilenameUtils.getExtension(filename);

        // Check if the uploaded file is a pdf
       /* if (!extension.equalsIgnoreCase("pdf")) {
            return "redirect:/error";
        }*/



        // Save the uploaded file to disk
        String filePath=".\\uploads\\"+currentUser.getName()+
                currentUser.getSurname()+".pdf";
        Path path = Paths.get("uploads/");
        Files.createDirectories(path);
        Files.copy(file.getInputStream(), path.resolve(currentUser.getName()+
                currentUser.getSurname()+".pdf"), StandardCopyOption.REPLACE_EXISTING);

        Resume cv=new Resume();
        //cv.setFilePath(".\\uploads\\"+filename);
        //this code to get the file
        // File resume = ResourceUtils.getFile(".\\uploads\\"+filename);
        //String pdfPath = resume.getAbsolutePath();



        cv.setFilePath(filePath);




        // This code example demonstrates how to extract and images in directory.
        // Create an instance of Parser class
        Parser parser = new Parser(cv.getFilePath());

        // Extract images from document
        Iterable<PageImageArea> images = parser.getImages();

        // Check if images extraction is supported
        if (images!= null) {


            // Create the options to save images in PNG format
            ImageOptions options = new ImageOptions(ImageFormat.Png);

            int imageNumber = 0;

            // Iterate over images
            for (PageImageArea image : images) {
                // Save the image to the PNG file
                image.save(String.format(".\\images\\"+currentUser.getName()+
                        currentUser.getSurname()+".png", imageNumber), options);
                // imageNumber++;
                break;
            }
        }

        cv.setImgPath(".\\images\\"+currentUser.getName()+currentUser.getSurname()+".png");

        //Step 1 Load the image file using Java ImageIO (which is built into Java)
        BufferedImage image = ImageIO.read(new File(cv.getImgPath()));

        //Step 2 Get a cropped version (x, y, width, height) (0,0 is top left corner)
        BufferedImage crop = image.getSubimage(0,50, image.getWidth(), image.getHeight()-50);

        //Step 3 Save the image back to a File
        ImageIO.write(crop, "PNG", new File(cv.getImgPath()));



        //model.addAttribute("resumeS",resume.getName());


        resumeService.createResume(cv,currentUser);


        //Create a PdfDocument object
        //PdfDocument doc = new PdfDocument();
        PdfDocument doc= new PdfDocument();

        //Load a PDF file
        //doc.loadFromFile("C:\\Users\\Administrator\\Desktop\\Invoice.pdf");
        doc.loadFromFile(cv.getFilePath());

        //Get the first page
        //PdfPageBase page = doc.getPages().get(0);
        PdfPageBase page = doc.getPages().get(0);

        //Create a PdfTextExtractor object
        PdfTextExtractor textExtractor = new PdfTextExtractor(page);

        //Create a PdfTextExtractOptions object
        PdfTextExtractOptions extractOptions = new PdfTextExtractOptions();

        //Set the option to extract text using SimpleExtraction strategy
        extractOptions.setSimpleExtraction(true);

        //Extract text from the specified area
        String text = textExtractor.extract(extractOptions);



        //Write to a txt file
        //Files.write(Paths.get("C:\\Users\\wiemb\\Downloads\\Erecruitement-master\\uploads\\text.txt"), text.getBytes());

        List<String> mots= new ArrayList<String>();
        //mots= Arrays.asList(text.split(" "));
        mots= Arrays.asList(text.split(System.getProperty("line.separator")));

        String champ=new String();

        champ="";

        /*Resume cv=new Resume();
        Candidate currentUser = (Candidate) session.getAttribute("currentUser");*/
        //cv.setCandidate(currentUser);


        String ts=new String();



        int pos=0;
        List<String> time=new ArrayList<>();
        String motl=new String();
        String motl1=new String();

        List<Education> educations=new ArrayList<>();
        List<Experience> experiences=new ArrayList<>();
        List<String> skills=new ArrayList<>();

        Education education=new Education();
        Experience exp=new Experience();

        cv.setPost(mots.get(4));
        for( String mot : mots)
        {
            motl=mot.toLowerCase();
            if(motl.contains("education"))
            {
                //champ="";
                pos= mots.indexOf(mot)+1;
                int nb=0;
                //ts="";
                //List<String> sp=Arrays.asList(mots.get(pos+1).split(" "));
                for(int i=pos; i<mots.size();i++){
                    if(nb==0){
                        education=new Education();
                        education.setCollegeName(mots.get(i));}

                    if(nb==1){education.setDegree(mots.get(i));}
                    if(nb==2){
                        List<String> sp=Arrays.asList(mots.get(i).split("-"));
                        education.setDateStart(sp.get(0));
                        //education.setDateEnd(sp.get(1));
                        educations.add(education);
                    }

                    nb=(nb+1)%3;


                    if((motl1.contains("skill"))||(motl1.contains("project"))||(motl1.contains("experience"))||(motl1.contains("achievement"))||(motl1.contains("publication"))||(motl1.contains("interest"))||(motl1.contains("language")))
                    {
                        cv.setEducations(educations);
                        break;
                    }

                }


            }
            if(motl.contains("experience"))
            {
                //champ="";
                pos= mots.indexOf(mot)+1;
                int nb=0;
                //ts="";
                //List<String> sp=Arrays.asList(mots.get(pos+1).split(" "));
                for(int i=pos; i<mots.size();i++){
                    if(nb==0){
                        exp=new Experience();
                        List<String> sp=Arrays.asList(mots.get(i).split("|"));
                        List<String> sp1=Arrays.asList(sp.get(1).split("-"));

                        exp.setJobPost(sp.get(0));
                        exp.setDateStart(sp1.get(0));
                        //exp.setDateEnd(sp1.get(1));
                    }

                    if(nb==1){education.setDegree(mots.get(i));}
                    if((nb==1)||(nb==2)||(nb==3)){
                        champ=champ+" "+mots.get(i);
                    }
                    if(nb==4)
                    {
                        champ=champ+" "+mots.get(i);
                        exp.setJobDetails(champ);
                        experiences.add(exp);
                    }

                    nb=(nb+1)%5;


                    if((motl1.contains("skill"))||(motl1.contains("project"))||(motl1.contains("education"))||(motl1.contains("achievement"))||(motl1.contains("publication"))||(motl1.contains("interest"))||(motl1.contains("language")))
                    {
                        cv.setExperiencesDescription(experiences);
                        break;
                    }

                }


            }

            if(motl.contains("competence"))
            {
                //champ="";
                pos= mots.indexOf(mot)+1;
                int nb=0;
                //ts="";
                //List<String> sp=Arrays.asList(mots.get(pos+1).split(" "));
                for(int i=pos; i<mots.size();i++){
                    skills.add(mots.get(i));

                    nb=(nb+1)%5;


                    if((motl1.contains("experience"))||(motl1.contains("project"))||(motl1.contains("education"))||(motl1.contains("achievement"))||(motl1.contains("publication"))||(motl1.contains("interest"))||(motl1.contains("language")))
                    {
                        cv.setSkills(skills);
                        break;
                    }

                }


            }

        }


        model.addAttribute("test",mots.get(0));


        resumeService.createResume(cv,currentUser);


        return "test";
        //return "mySpaceCandidate";
    }
        @GetMapping("/showResume")
    public ResponseEntity<InputStreamResource> showResume(HttpSession session) throws FileNotFoundException {
        Candidate currentUser = (Candidate) session.getAttribute("currentUser");

        Resume resume=resumeService.getResumeByCandidate(currentUser);
        // File cv=ResourceUtils.getFile(resume.getFilePath().substring(1));
        File cv=ResourceUtils.getFile(resume.getFilePath());
        String fileName =cv.getName();


        String pdfPath = cv.getAbsolutePath();


        //File file = new File(pdfPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=" +fileName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(cv));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(cv.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

        //model.addAttribute("resumeF",pdfPath);


        //return pdfPath;
    }

    @GetMapping("/showResume/{id}")  /** Recruiter can see resume of candidate after search **/
    public ResponseEntity<InputStreamResource> showResumeById(@PathVariable(value = "id") ObjectId id) throws FileNotFoundException {
        Optional<Resume> resume = resumeService.getResumeById(id);
        File cv=ResourceUtils.getFile(resume.get().getFilePath());
        String fileName =cv.getName();

        String pdfPath = cv.getAbsolutePath();


        //File file = new File(pdfPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=" +fileName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(cv));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(cv.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);

    }

    @GetMapping("/suggestion/resume/{id}") /** Recruiter can see the resume of a Suggestion **/
    public ResponseEntity<InputStreamResource> showResumeByIdForSuggestion (@PathVariable(value = "id") ObjectId id) throws FileNotFoundException {
        Optional<Resume> resume = resumeService.getResumeById(id);
        File cv= ResourceUtils.getFile(resume.get().getFilePath());
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

    @GetMapping("/allresumes")
    public String getAllResumes (Model model)
    {
        model.addAttribute("resumes", resumeService.getAllResumes());
        model.addAttribute("nbrAll", resumeService.getAllResumes().size());
        return "resumeSearch";
    }


}

