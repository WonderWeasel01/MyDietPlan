package com.WebApplcation.MyDietPlan.UseCase;

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
    public boolean isPayingUser(){
        return repo.isActiveMember(getUser().getUserId());
    }


    /**
     * Sets up a subscription for the user.
     * @return The created Subscription object.
     */
    public Subscription payingUser() {
        User user = getUser();
        int paidUserId = user.getUserId();
        Subscription subscription = setupSubscription();

        // Set the subscription start date to the current date
        repo.insertSubscription(subscription,paidUserId);
        return subscription;
    }

    /**
     * Sets up a new subscription.
     * @return The configured Subscription object.
     */
    public Subscription setupSubscription(){
        Subscription subscription = new Subscription();

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




