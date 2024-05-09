package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputAlreadyExistsException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import java.sql.SQLException;


public class AuthenticationService {
    public static User user;
    public UserRepository ur;



    public User createUser(User user) throws InputAlreadyExistsException, SystemErrorException {
        try{
            return ur.createUser(user);
        } catch(DuplicateKeyException e){
            throw new InputAlreadyExistsException("Email already in use");
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
