package com.enit.Erecruitement;
import com.enit.Erecruitement.Candidate;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "resumes")
@Data
public class Resume {



    @Id
    private ObjectId idResume;
    @DBRef
    private Candidate candidate;
    private String post;
    private List <String> skills;
    private String experience;
    //private Map<String,List<String>> experienceDescription;
    //private ArrayList<ArrayList<String>> experienceDescription;
    //private List<List<String>> experienceDescription;
    //private List <String > projects;

    private List<Experience> experiencesDescription;

    private String filePath;
    private String imgPath;

    //private Map<String, List<String>> education;
    //private HashMap<String,ArrayList<String>> education;
    //private ArrayList<ArrayList<String>> education;
    //private List<List<String>> education;
    //private List<String> education;

    private List<Education> educations;



    public Resume() {
        /*experienceDescription=new HashMap<>();
        education=new HashMap<>();
         */
    }

    public Resume(String post, List<String> skills, String experience, List<String> projects) {
        this.post = post;
        this.skills = skills;
        this.experience = experience;
    }

    public ObjectId getIdResume() {
        return idResume;
    }

    public void setIdResume(ObjectId idResume) {
        this.idResume = idResume;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public List<Experience> getExperiencesDescription() {
        return experiencesDescription;
    }

    public void setExperiencesDescription(List<Experience> experiencesDescription) {
        this.experiencesDescription = experiencesDescription;
    }

    public List<Education> getEducations() {
        return educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }
}