
package edu.vt.resumemanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import edu.vt.resumemanager.utils.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;

// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Account.
@Entity
@Data
// Name of the database table represented
@Table(name = "Account")

@NamedQueries({
        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
        , @NamedQuery(name = "Account.findById", query = "SELECT a FROM Account a WHERE a.id = :id")
        , @NamedQuery(name = "Account.findByUsername", query = "SELECT a FROM Account a WHERE a.username = :username")
        , @NamedQuery(name = "Account.findByPassword", query = "SELECT a FROM Account a WHERE a.password = :password")
        , @NamedQuery(name = "Account.findBySecurityQuestion", query = "SELECT a FROM Account a WHERE a.securityQuestion = :securityQuestion")
        , @NamedQuery(name = "Account.findBySecurityAnswer", query = "SELECT a FROM Account a WHERE a.securityAnswer = :securityAnswer")
        , @NamedQuery(name = "Account.findByEmail", query = "SELECT a FROM Account a WHERE a.email = :email")
        , @NamedQuery(name = "Account.findByTwoFaStatus", query = "SELECT a FROM Account a WHERE a.twoFactorAuthenticationStatus = :twoFaStatus")
        , @NamedQuery(name = "Account.findByCellPhoneNumber", query = "SELECT a FROM Account a WHERE a.cellPhoneNumber = :cellPhoneNumber")
        , @NamedQuery(name = "Account.findByCellPhoneCarrier", query = "SELECT a FROM Account a WHERE a.cellPhoneCarrier = :cellPhoneCarrier")})

public class Account implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the database table Account.

    CREATE TABLE Account
    (
        id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
        Accountname VARCHAR(32) NOT NULL,
        password VARCHAR(255) NOT NULL,
        security_question VARCHAR(255) NOT NULL,
        security_answer VARCHAR(128) NOT NULL,
        email VARCHAR(128) NOT NULL,
        two_fa_status INT NOT NULL,
        cell_phone_number VARCHAR(24),
        cell_phone_carrier VARCHAR(32),
        profile_picture VARCHAR(255),
        role VARCHAR(32)
    );
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    /*
    Primary Key id is auto generated by the system as an Integer value
    starting with 1 and incremented by 1, i.e., 1,2,3,...
    A deleted entity object's primary key number is not reused.
     */
    // id INT UNSIGNED NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // Accountname VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "username")
    private String username;

    // To store Salted and Hashed Password Parts
    // password VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password")
    private String password;

    // security_question VARCHAR(255) NOT NULL,
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "security_question")
    private String securityQuestion;

    // security_answer VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "security_answer")
    private String securityAnswer;

    // email VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "email")
    private String email;

    /*
     Two-Factor Authentication Status:
         = 0 Off
         = 1 Send random code via email
         = 2 Send random code via SMS
     */
    // two_fa_status INT NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "two_fa_status")
    private int twoFactorAuthenticationStatus;

    // cell_phone_number VARCHAR(24)
    @Size(max = 24)
    @Column(name = "cell_phone_number")
    private String cellPhoneNumber;

    // cell_phone_carrier VARCHAR(32)
    @Size(max = 32)
    @Column(name = "cell_phone_carrier")
    private String cellPhoneCarrier;

    // role VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "role",columnDefinition = "VARCHAR(32) NOT NULL DEFAULT 'employee'")
    private String role;




    @Getter(AccessLevel.NONE)
    // profile_picture VARCHAR(255) NOT NULL
    @Basic(optional = true)
    @Size( max = 255)
    @Column(name = "profile_picture",columnDefinition = "VARCHAR(255) DEFAULT 'defaultUserPhoto.png'")
    private String profilePicture;

    // suspended_until TIMESTAMP
    @Basic(optional = false)
    @Column(name = "suspended_until", columnDefinition = "TIMESPAMP DEFAULT NULL")
    private Timestamp suspended_until;

    /*
    ================================
    Instance Methods Used Internally
    ================================
     */

    // Generate and return a hash code value for the object with database primary key id
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*
     Checks if the User object identified by 'object' is the same as the User object identified by 'id'
     Parameter object = User object identified by 'object'
     Returns True if the User 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

    public String getProfilePicture(){
        if(profilePicture==null || profilePicture.isBlank()){
            profilePicture = Constants.FILES_URI+ "defaultUserPhoto.png";
        }else if(!profilePicture.startsWith(Constants.FILES_URI)){
            profilePicture = Constants.FILES_URI+profilePicture;
        }
        return profilePicture;
    }
}
