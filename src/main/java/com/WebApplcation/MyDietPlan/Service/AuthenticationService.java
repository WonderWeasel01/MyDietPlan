package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.util.StringUtils;


@Service
public class AuthenticationService {
    public static User user;
    @Autowired
    public MyDietPlanRepository repo;

    public AuthenticationService(MyDietPlanRepository repository){
        this.repo = repository;
    }


    /**
     *
     * @param user A user with information to put in the database
     * @return Returns the user with an auto generated id attached.
     * @throws SystemErrorException If the system cant connect to database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public User createUser(User user) throws SystemErrorException, InputErrorException, DuplicateKeyException {
        if(user == null || !isValidUser(user)){
            throw new InputErrorException("Missing inputs");
        }

        try{
            String hashedPassword = hashPassword(user.getPassword());
            user.setPassword(hashedPassword);
            user.setRole("User");
            return repo.createUser(user);
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

    /**
     * Determines if a user object has the correct information attached to be put into the database.
     * @param user a user that has not been inserted into the database yet
     * @return True if user is valid, false if not.
     */
    public boolean isValidUser(User user){
        return StringUtils.hasText(user.getEmail()) && StringUtils.hasText(user.getPassword()) && StringUtils.hasText(user.getFirstName())
                && StringUtils.hasText(user.getLastName()) &&  StringUtils.hasText(user.getGoal()) && StringUtils.hasText(user.getActivityLevel())
                && user.getHeight() > 0 && user.getAge() > 0 && user.getWeight() > 0;
    }

    /**
     * Hashes a users password and adds salt
     * @param password Password that needs hashing
     * @return The hashed password
     */
    public String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public boolean validateLogin(String email, String password) throws EntityNotFoundException {
        try{
            String hashedPassword = repo.getPasswordByEmail(email);
            return BCrypt.checkpw(password, hashedPassword);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Failed to find user with given email");
        }
    }

    public User loginUser(String email){
        return user = repo.getUserByEmail(email);
    }

    public void logout(){
        AuthenticationService.user = null;
    }


/*


    public boolean deleteUser(int userID){

    }

    public int calculateAge(int dateOfBirth){

    }
 */


}
