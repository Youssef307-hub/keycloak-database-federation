package org.opensingular.dbuserprovider.model;

public class DBQueries {

    public String getCount() {
        return "SELECT COUNT(*) FROM users";
    }

    public String getListAll() {
        return "SELECT \"id\", \"username\", \"email\", \"firstName\", \"lastName\", \"roles\" FROM users";
    }

    public String getFindById() {
        return "SELECT \"id\", \"username\", \"email\", \"firstName\", \"lastName\", \"roles\"" +
                "FROM users WHERE \"id\" = ?";
    }

    public String getFindByUsername() {
        return "SELECT \"id\", \"username\", \"email\", \"firstName\", \"lastName\", \"roles\"" +
                "FROM users WHERE \"username\" = ?";
    }

    public String getFindBySearchTerm() {
        return "SELECT \"id\", \"username\", \"email\", \"firstName\", \"lastName\", \"roles\"" +
                "FROM users WHERE upper(\"username\") like (?)  or upper(\"email\") like (?)";
    }

    public String getFindPasswordHash() {
        return "SELECT hash_pwd FROM users WHERE \"username\" = ? ";
    }

    public boolean getAllowKeycloakDelete() {
        return true;
    }
}
