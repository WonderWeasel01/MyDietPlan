package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;



@Service
public class AuthenticationService {
    public static User user;
    @Autowired
    public UserRepository ur;

    public AuthenticationService(UserRepository userRepository){
        this.ur = userRepository;
    }


    /**
     *
     * @param user A user with information to put in the database
     * @return Returns the user with an auto generated id attached.
     * @throws SystemErrorException If the system cant connect to database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public User createUser(User user) throws SystemErrorException, InputErrorException {
        if(user == null){
            throw new InputErrorException("Missing inputs");
        }

        try{
            String hashedPassword = hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            user.setRole("User");
            return ur.createUser(user);
        } catch(DuplicateKeyException e){
            e.printStackTrace();
            throw new DuplicateKeyException("Email already in use");
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            throw new InputErrorException("Failed to create user due to missing input");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Failed to create user due to a database issue");
        }
    }

    public String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public boolean validateUser(String email, String password){

        String hashedPassword = ur.getPasswordByEmail(email);
        return BCrypt.checkpw(password, hashedPassword);
    }

    public  void loginUser(String email){
        user = ur.getUserByEmail(email);
    }


/*
    public boolean logout(){

    }

    public boolean deleteUser(int userID){

    }

    public int calculateAge(int dateOfBirth){

    }
 */


}
