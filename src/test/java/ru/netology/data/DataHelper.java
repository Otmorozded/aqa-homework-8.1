package ru.netology.data;

import lombok.Value;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getAuthInfoWrongPassword() {
        return new AuthInfo("vasya", "12345");
    }

    public static AuthInfo getOtherAuthInfo() {
        return new AuthInfo("petya", "123qwerty");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCode(AuthInfo authInfo) {
        String id = "";
        String verificationCode = "";
        val idSQL = "SELECT id FROM users WHERE login='" + authInfo.login + "'";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            id = (String) runner.query(conn, idSQL, new ScalarHandler<>());
            val codeSQL = "SELECT code FROM auth_codes WHERE created = (SELECT max(created) FROM auth_codes WHERE user_id='" + id + "')";
            verificationCode = (String) runner.query(conn, codeSQL, new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new VerificationCode(verificationCode);
    }

    public static VerificationCode getWrongVerificationCode() {
        return new VerificationCode("12345");
    }


    public static void clearData() {
        val deleteUserSQL = "DELETE FROM users";
        val deleteCardSQL = "DELETE FROM cards";
        val deleteAuthCodesSQL = "DELETE FROM auth_codes";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass"
                );
        ) {
            runner.update(conn, deleteAuthCodesSQL);
            runner.update(conn, deleteCardSQL);
            runner.update(conn, deleteUserSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


