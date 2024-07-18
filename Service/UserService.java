package com.enit.Erecruitement.Service;

import com.enit.Erecruitement.Recruiter;
import com.enit.Erecruitement.Repository.UserRepository;
import com.enit.Erecruitement.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public boolean connect(String email, String password){
        User user = userRepository.findByEmail(email);
        if(user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;

    }
//    public boolean deleteUser(ObjectId id) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user == null) {
//            return false;
//        }
//        userRepository.delete(user);
//        return true;
//    }
}