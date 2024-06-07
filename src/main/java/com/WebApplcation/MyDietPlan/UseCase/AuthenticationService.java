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
    private final MyDietPlanRepository repository;

    /**
     * Constructs an AuthenticationService object.
     * @param session The HttpSession object used for session management.
     * @param repository the MyDiedPlanRepository object used fir accessing data from the repository.
     */
    @Autowired
    public AuthenticationService(HttpSession session, MyDietPlanRepository repository){
        this.session = session;
        this.repository = repository;
    }

    /**
     * Hashes the users password and sets it in the user object.
     * @param user The User object whose password needs to be hashed.
     */
    public void hashAndSetPassword(User user){
        String hashedPassword = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt());
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
     *Validates the login credentials of a user.
     * @param email The email of the user trying to log in.
     * @param password The password of the user attempting to log in.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */
    public void validateLogin(String email, String password) throws InputErrorException {
        try{
            String hashedPassword = repository.getPasswordByEmail(email);
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
    public User loginUserAndSetSession(String email, String password) throws InputErrorException {
        validateLogin(email, password);
        User user = repository.getUserByEmail(email);
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
            Subscription subscription = repository.getSubscriptionByUserID(getUser().getUserId());
            if(isSubExpired(subscription) && subscription.getActiveSubscription()){
                renewSub(subscription);
            }
            return !isSubExpired(subscription);
        } catch (EmptyResultDataAccessException e){
            System.out.println("Intet abonnement fundet");
            return false;
        }
    }

    /**
     * Renews the given subscription by extending its end date and updating its attributes.
     *
     * <p>If the subscription still has time remaining, one month is added to the current subscription end date.
     * Otherwise, one month is added to the current date.</p>
     *
     * <p>The method updates the subscription start date to the current date, sets the new end date,
     * updates the subscription price, and sets the subscription to active if it is not already active.</p>
     *
     * @param subscription the subscription to be renewed
     * @throws SystemErrorException if there is a data access error when updating the subscription
     * @throws EntityNotFoundException if the subscription cannot be found in the database
     */
    public void renewSub(Subscription subscription) throws SystemErrorException, EntityNotFoundException {

        java.sql.Date newEndDate;
        LocalDate subscriptionEndDate = subscription.getSubscriptionEndDate().toLocalDate();

        //If the user still has time left on their sub, a month gets added to their current sub end day.
        if (subscriptionEndDate.isAfter(LocalDate.now())){
            newEndDate = java.sql.Date.valueOf(subscriptionEndDate.plusMonths(1));
        } else {
            newEndDate = java.sql.Date.valueOf(LocalDate.now().plusMonths(1));
        }

        //Update sub attributes.
        subscription.setSubscriptionStartDate(Date.valueOf(LocalDate.now()));
        subscription.setSubscriptionEndDate(newEndDate);
        subscription.setSubscriptionPrice(196);

        //Set active status
        if(!subscription.getActiveSubscription()){
            subscription.setActiveSubscription(true);
        }

        try{
            repository.updateSubscription(subscription);
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde abonnement i databasen");
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl med databasen. Prøv igen senere");
        }
    }

    public boolean isSubExpired(Subscription subscription){
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(subscription.getSubscriptionEndDate().toLocalDate());
    }

    /**
     * Deletes a user by their user ID.
     * @param userID The userID of the user to be deleted.
     * @return True if the user was successfully deleted, false if not.
     */
    public boolean deleteUser(int userID) {
        return repository.deleteUser(userID);
    }


}




