package com.WebApplcation.MyDietPlan.Repository;

import com.WebApplcation.MyDietPlan.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.PreparedStatement;
import java.sql.Statement;

public class UserRepository {

    private JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts a new user into the database.
     * @param user a new user that doesn't exist in the database
     * @return The user that was insert into the database, with the auto-generated id attached.
     */
    public User createUser(User user){
        String sql = "INSERT INTO `User`(`first_name`, `email`, `goal`, `last_name`, `activity_level`, `gender`, `weight`, `age`, `role`) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getGoal());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getActivityLevel());
            ps.setString(6, String.valueOf(user.getGender()));
            ps.setDouble(7, user.getWeight());
            ps.setInt(8, user.getAge());
            ps.setString(9, user.getRole());
            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int userId = generatedKey.intValue();
            user.setUserId(userId);//
        }
        return user;
    }

    /**
     * Deletes a user from the database
     * @param userID The id of an existing user
     * @return True if deletion was successful. False if something went wrong
     */

    public boolean deleteUser(int userID){
        String sql = "DELETE FROM `User` WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql);

        //Returns true if deletion was successful
        return rowsAffected > 0;
    }

    /**
     * Finds a user by their userId.
     * @param id The userId you are searching for.
     * @return The user, which matches the userId you are searching for.
     * @throws EmptyResultDataAccessException Throws an exception whenever you are searching for a userId that doesn't exist.
     */

    public User getUserByID(int id) throws EmptyResultDataAccessException{
        String sql ="SELECT * FROM `User` WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,userRowMapper(),id);
    }



    /**
     * Updates settings for a user in the database.
     * @param userId The id of an existing user.
     * @param user An existing user in the database.
     * @return User that was just updated in the database.
     */

    public User updateUser(int userId, User user){
        String sql ="UPDATE `User` SET `user_id`=?, `first_name`=?, `email`=?, `goal`=?, `last_name`=?, `activity_level`=?, `gender`=?, `weight`=?, `height`=?,  `age`=?, `role`=? WHERE id = ?";
        jdbcTemplate.update(sql, user.getUserId(), user.getFirstName(), user.getEmail(), user.getGoal(), user.getLastName(), user.getActivityLevel(), user.getGender(), user.getWeight(), user.getHeight(), user.getAge(), user.getRole(), userId);
        return getUserByID(userId);

    }


    /**
     * @return A RowMapper for mapping ResultSet rows in DataBase
     */
    private RowMapper<User> userRowMapper(){
        return ((rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id "));
            user.setFirstName(rs.getString("first_name"));
            user.setEmail(rs.getString("email"));
            user.setGoals(rs.getString("goal"));
            user.setLastName(rs.getString("last_name"));
            user.setHeight(rs.getInt("activity_level"));
            user.setGender(rs.getString("gender").charAt(0));
            user.setWeight(rs.getInt("weight"));
            user.setHeight(rs.getInt("height"));
            user.setAge(rs.getInt("age"));
            user.setRole(rs.getString("role"));
            return user;
        });
    }


}
