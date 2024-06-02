package com.WebApplcation.MyDietPlan.UseCase;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;


@Service
public class AuthenticationService {
    private final HttpSession session;
    public MyDietPlanRepository repo;

    /**
     * Constructs an AuthenticationService object.
     * @param session The HttpSession object used for session management.
     * @param repository the MyDiedPlanRepository object used fir accessing data from the repository.
     */
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

    /**
     * Saves a user in the database.
     * @param user Save a user object.
     * @return The saved user object
     * @throws SystemErrorException If the system can't connect to the database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     * @throws DuplicateKeyException If the user has the same id as an already existing user.
     */
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

    /**
     * Hashes the users password and sets it in the user object.
     * @param user The User object whose password needs to be hashed.
     */
    public void hashAndSetPassword(User user){
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
    }

    /**
     * Retrieves the User object stored in the session.
     * @return The user object stored in the session. If none is found returns null.
     */
    public User getUser(){
        return (User) session.getAttribute("user");
    }

    /**
     * Sets the User object in a session.
     * @param user The User object to be set in the session.
     */
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

    /**
     *Validates the login credentials of a user.
     * @param email The email of the user trying to log in.
     * @param password The password of the user attempting to log in.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public void validateLogin(String email, String password) throws InputErrorException {
        try{
            String hashedPassword = repo.getPasswordByEmail(email);
            validatePassword(password, hashedPassword);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new InputErrorException("Email eller password forkert");
        }
    }

    /**
     * Validates the provided password against the stored hashed password.
     * @param password The password provided by the user.
     * @param hashedPassword The hashed password stored in the database
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public void validatePassword(String password, String hashedPassword) throws InputErrorException {
        if(!BCrypt.checkpw(password, hashedPassword)){
            throw new InputErrorException("Email eller password forkert");
        }
    }

    /**
     * Logs in a user with the provided email and password.
     * @param email The email of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @return The User object of the logged-in user.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public User loginUser(String email, String password) throws InputErrorException {
        validateLogin(email, password);
        User user = repo.getUserByEmail(email);
        setSession(user);
        return getUser();
    }

    /**
     * Logs out the current user by invalidating the current session.
     */
    public void logout(){
        session.invalidate();
    }

    /**
     * Checks if the user that is logged in has the Admin role attached.
     * @return True if the user is an admin, false if not.
     */
    public boolean isAdminLoggedIn(){
        User user = getUser();
        return user != null && Objects.equals(user.getRole(), "Admin");
    }

    /**
     * Checks if the user is logged in.
     * @return True if the user is logged in, false if not.
     */
    public boolean isUserLoggedIn(){
        User user = getUser();
        return user != null;
    }

   /**
     * Checks if the user is a paying user.
     * @return True if the user is a paying user, false if not.
     */
    public boolean isPayingUser() throws SystemErrorException, EntityNotFoundException {
        try{

            Subscription subscription = repo.getSubscriptionByUserID(getUser().getUserId());

            System.out.println(subscription);
            //Renew if active subscription ran out
            if(isSubExpired(subscription) && subscription.isActiveSubscription()){
                return renewSub(subscription);
            }
            else return !isSubExpired(subscription);

        } catch (EmptyResultDataAccessException e){
            System.out.println("Intet abonnement fundet");
            return false;
        }
    }

    public boolean isSubExpired(Subscription subscription){
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(subscription.getSubscriptionEndDate().toLocalDate());
    }

    public boolean renewSub(Subscription subscription) throws SystemErrorException, EntityNotFoundException {
        Date date;

        //If the user still has time left on their sub, the month gets added to their current sub end day.
        if (subscription.getSubscriptionEndDate().toLocalDate().isAfter(LocalDate.now())){
            date = Date.valueOf(subscription.getSubscriptionEndDate().toLocalDate().plusMonths(1));
        } else {
            date = Date.valueOf(LocalDate.now().plusMonths(1));
        }

        //Update sub attributes.
        subscription.setSubscriptionStartDate(Date.valueOf(LocalDate.now()));
        subscription.setSubscriptionEndDate(date);
        subscription.setSubscriptionPrice(196);

        //Set active status
        if(!subscription.isActiveSubscription()){
            subscription.setActiveSubscription(true);
        }

        try{
            return repo.updateSubscription(subscription);
        }catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Kunne ikke finde abonnement i databasen");
        } catch (DataAccessException e) {
            throw new SystemErrorException("Der skete en fejl med databasen. Prøv igen senere");
        }
    }



    public void cancelUserSubscription(int userID) throws EntityNotFoundException, SystemErrorException {
        try{
            repo.cancelSubscription(userID);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde et aktivt abonnement");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Kunne ikke oprette forbindelse til databasen");
        }
    }



    /**
     * Sets up a subscription for the user.
     */
    public void paySub() throws SystemErrorException, EntityNotFoundException{
        User user = getUser();
        int userID = user.getUserId();
        Subscription subscription = getSubscriptionByUserID(userID);

        try{
            //If the user has a sub, renew it, else create a new one
            if(subscription != null){
            renewSub(subscription);
            } else{
                subscription = setupNewSubscription();
                repo.insertSubscription(subscription,userID);
            }
        }catch (DataAccessException e){
            throw new SystemErrorException("Der skete en database fejl. Prøv igen senere");
        }
    }

    public Subscription getSubscriptionByUserID(int userID){
        try{
            return repo.getSubscriptionByUserID(userID);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    /**
     * Sets up a new subscription.
     * @return The configured Subscription object.
     */
    public Subscription setupNewSubscription(){
        Subscription subscription = new Subscription();

        java.util.Date currentDate = new java.util.Date();
        java.sql.Date sqlStartDate = new java.sql.Date(currentDate.getTime());
        subscription.setSubscriptionStartDate(sqlStartDate);

        // Set the subscription end date one week later
        LocalDate localEndDate = sqlStartDate.toLocalDate().plusMonths(1);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(localEndDate);
        subscription.setSubscriptionEndDate(sqlEndDate);

        //Set subscription to me active= True
        subscription.setActiveSubscription(true);

        //Set the Price of the membership
        subscription.setSubscriptionPrice(0);
        return subscription;
    }



    /**
     * Deletes a user by their user ID.
     * @param userID The userID of the user to be deleted.
     * @return True if the user was successfully deleted, false if not.
     */
    public boolean deleteUser(int userID) {
        return repo.deleteUser(userID);
    }


}




