package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputAlreadyExistsException;
import com.WebApplcation.MyDietPlan.Exception.MissingInputException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AuthenticationService {
    public static User user;
    @Autowired
    public UserRepository ur;

    public AuthenticationService(UserRepository userRepository){
        this.ur = userRepository;
    }



    public User createUser(User user) throws InputAlreadyExistsException, SystemErrorException, MissingInputException {
        try{
            user.setRole("User");
            return ur.createUser(user);
        } catch(DuplicateKeyException e){
            e.printStackTrace();
            throw new InputAlreadyExistsException("Email already in use");
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            throw new MissingInputException("Missing input");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("An error occured on our side");
        }
    }
/*
    public boolean validateUser(){

    }

    public boolean logout(){

    }

    public boolean deleteUser(int userID){

    }

    public int calculateAge(int dateOfBirth){

    }


*/
}
