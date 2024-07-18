package com.enit.Erecruitement.Service;


import com.enit.Erecruitement.Application;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jobmatchnow.platform@gmail.com"); //email du plateforme
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        this.mailSender.send(simpleMailMessage);
    }
    public void SendEmailAcceptance(Application application)
    {
        String to = application.getCandidate().getEmail();
        String subject= "Acceptance of your application";
        String message ="Dear " +application.getCandidate().getName() + " "+ application.getCandidate().getSurname()+
                ",\n\n" +
                "Your application on the offer : \n "
                + "Profile : " + application.getOffer().getProfile() + "\n"
                +"Description : " +application.getOffer().getDescription()+ "\n"
                +"Company Name : " +application.getOffer().getCompanyName() + "\n"
                + "Location : " +application.getOffer().getLocation() + "\n"
                +"is ACCEPTED. You will receive an Email as soon as possible from the recruiter to inform you about the date of the interview \n \nBest regards,\n"+

                application.getOffer().getRecruiter().getName() +" "+ application.getOffer().getRecruiter().getSurname()+"\n"+
                application.getOffer().getCompanyName();


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jobmatchnow.platform@gmail.com"); //email du plateforme
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        this.mailSender.send(simpleMailMessage);

    }
    public void SendEmailRejection(Application application)
    {
        String to = application.getCandidate().getEmail();
        String subject= "Rejection of your application";
        String message ="Dear " +application.getCandidate().getName() + " "+ application.getCandidate().getSurname()+
                ",\n\n" +
                "Your application on the offer : \n"
                + "Profile : " + application.getOffer().getProfile() + "\n"
                +"Description : " +application.getOffer().getDescription()+ "\n"
                +"Company Name : " +application.getOffer().getCompanyName() + "\n"
                + "Location : " +application.getOffer().getLocation() + "\n"
                +"is REJECTED because of some missing skills and experience.\n \nBest regards,\n"+

                application.getOffer().getRecruiter().getName() +" "+ application.getOffer().getRecruiter().getSurname()+"\n"+
                application.getOffer().getCompanyName();


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jobmatchnow.platform@gmail.com"); //email du plateforme
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        this.mailSender.send(simpleMailMessage);

    }
//    public void SendEmailInterview(Application application , String date, String time)
//    {
//        String to = application.getCandidate().getEmail();
//        String subject= "Acceptance of your application";
//        String message ="Dear "+ application.getCandidate().getName() + " "+ application.getCandidate().getSurname()+ ",\n \n"+
//                "Thank you for your interest in the "+ application.getOffer().getProfile() +" position at " + application.getOffer().getCompanyName()
//            +".\n After reviewing your application, we would like to invite you for an interview to further discuss your qualifications and experience. We would like to schedule the interview on "
//            + date +" at " + time + " at our " + application.getOffer().getLocation() +" office. \n For more details you can contact us via this email: "+ application.getOffer().getRecruiter().getEmail()+". \n \n  Best regards,\n"+
//
//            application.getOffer().getRecruiter().getName() +" "+ application.getOffer().getRecruiter().getSurname()+"\n"+
//            application.getOffer().getCompanyName();
//
//
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom("erij.sayaricpm@gmail.com"); //email du plateforme
//        simpleMailMessage.setTo(to);
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(message);
//
//        this.mailSender.send(simpleMailMessage);
//
//    }
    public void SendEmailInterview(Application application )
    {
        String to = application.getCandidate().getEmail();
        String subject= "Acceptance of your application";
        String message ="Dear "+ application.getCandidate().getName() + " "+ application.getCandidate().getSurname()+ ",\n \n"+
                "Thank you for your interest in the "+ application.getOffer().getProfile() +" position at " + application.getOffer().getCompanyName()
                +".\n After reviewing your application, we would like to invite you for an interview to further discuss your qualifications and experience. We would like to schedule the interview on "
                + application.getDateInterview() +" at " + application.getTimeInterview() + " at our " + application.getOffer().getLocation() +" office. \n For more details you can contact us via this email: "+ application.getOffer().getRecruiter().getEmail()+". \n \n  Best regards,\n"+

                application.getOffer().getRecruiter().getName() +" "+ application.getOffer().getRecruiter().getSurname()+"\n"+
                application.getOffer().getCompanyName();


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jobmatchnow.platform@gmail.com"); //email du plateforme
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        this.mailSender.send(simpleMailMessage);

    }

}
