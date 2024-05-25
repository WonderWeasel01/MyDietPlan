package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.Subscription;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;


@Service
public class AuthenticationService {
    private final HttpSession session;
    public MyDietPlanRepository repo;

    @Autowired
    public AuthenticationService(HttpSession session, MyDietPlanRepository repository){
        this.session = session;
        this.repo = repository;
    }


    /**
     * Sets up all the necessary information that a user needs and saves the user in the database.
     * @param user A user with information to put in the database
     * @return Returns the user with an auto generated id attached.
     * @throws SystemErrorException If the system cant connect to database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public User createUser(User user) throws SystemErrorException, InputErrorException {
        if(!isValidUser(user)){
            throw new InputErrorException("Venligst udfyld alle felterne korrekt");
        }
        hashAndSetPassword(user);
        user.setRole("User");
        user.setupDailyCalorieGoal();
        return saveUser(user);
    }

    private User saveUser(User user) throws SystemErrorException, InputErrorException, DuplicateKeyException {
        try {
            return repo.createUser(user);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            throw new InputErrorException("Email bruges allerede");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new InputErrorException("Venligst udfyld alle felterne korrekt");
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl under opretning af brugeren. Prøv igen senere");
        }
    }

    public void hashAndSetPassword(User user){
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
    }

    public User getUser(){
        return (User) session.getAttribute("user");
    }
    public void setSession(User user){
        session.setAttribute("user",user);
    }

    /**
     * Determines if a user object has the correct information attached to be put into the database.
     * @param user a user that has not been inserted into the database yet
     * @return True if user is valid, false if not.
     */
    public boolean isValidUser(User user){
        return user != null && StringUtils.hasText(user.getEmail()) && StringUtils.hasText(user.getPassword()) && StringUtils.hasText(user.getFirstName())
                && StringUtils.hasText(user.getLastName()) &&  StringUtils.hasText(user.getGoal()) && StringUtils.hasText(user.getActivityLevel()) && StringUtils.hasText(String.valueOf(user.getGender()))
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

    public void validateLogin(String email, String password) throws InputErrorException {
        try{
            String hashedPassword = repo.getPasswordByEmail(email);
            validatePassword(password, hashedPassword);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new InputErrorException("Email eller password forkert");
        }
    }

    public void validatePassword(String password, String hashedPassword) throws InputErrorException {
        if(!BCrypt.checkpw(password, hashedPassword)){
            throw new InputErrorException("Email eller password forkert");
        }
    }

    public User loginUser(String email, String password) throws InputErrorException {
        validateLogin(email, password);
        User user = repo.getUserByEmail(email);
        setSession(user);
        return getUser();
    }

    public void logout(){
        session.invalidate();
    }

    public boolean isAdminLoggedIn(){
        User user = getUser();
        return user != null && Objects.equals(user.getRole(), "Admin");
    }

    public boolean isUserLoggedIn(){
        User user = getUser();
        return user != null;
    }

    public boolean isPayingUser(){
        return repo.isActiveMember(getUser().getUserId());
    }
    

    public Subscription payingUser(Subscription subscription) {
        User user = getUser();
        int paidUserId = user.getUserId();


        // Set the subscription start date to the current date
        java.util.Date currentDate = new java.util.Date();
        java.sql.Date sqlStartDate = new java.sql.Date(currentDate.getTime());
        subscription.setSubscriptionStartDate(sqlStartDate);

        // Set the subscription end date one week later
        LocalDate localEndDate = sqlStartDate.toLocalDate().plusWeeks(4);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(localEndDate);
        subscription.setSubscriptionEndDate(sqlEndDate);

        //Set subscription to me active= True
        subscription.setSubscriptionStatus(true);

        //Set the Price of the membership
        subscription.setSubscriptionPrice(49.00);

        repo.insertSubscription(subscription,paidUserId);
        return subscription;
    }

    public boolean deleteUser(int userID) {
        return repo.deleteUser(userID);
    }


}




