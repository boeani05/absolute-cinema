package com.egger.cinema;

public interface IAdminSupport {
    /**
     * Authenticates an admin user based on provided credentials.
     *
     * @param username The admin's username.
     * @param password The admin's password.
     * @return true if authentication is successful, false otherwise.
     */
    boolean authenticateAdmin(String username, String password);

    /**
     * Prints the admin view, displaying all movies and their details.
     */
    void printAdminView();
}
