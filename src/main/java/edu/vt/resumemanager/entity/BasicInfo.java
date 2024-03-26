

package edu.vt.resumemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
// Name of the database table represented
@Table(name = "BasicInfo")
@NamedQueries({
        @NamedQuery(name = "BasicInfo.findAll", query = "SELECT u FROM BasicInfo u")
        , @NamedQuery(name = "BasicInfo.findById", query = "SELECT u FROM BasicInfo u WHERE u.id = :id")})

public class BasicInfo {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the database table BasicInfo.

    CREATE TABLE BasicInfo
    (
        id int unsigned NOT NULL,
        first_name varchar(32) NOT NULL,
        middle_name varchar(32) DEFAULT NULL,
        last_name varchar(32) NOT NULL,
        dob date NOT NULL,
        summary varchar(512) NULL
    );
    ========================================================
     */
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // first_name VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "first_name")
    private String firstName;

    // middle_name VARCHAR(32)
    @Size(max = 32)
    @Column(name = "middle_name")
    private String middleName;

    // last_name VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "last_name")
    private String lastName;

    // dob Date NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "dob")
    private Date dob;

    // summary VARCHAR(512) NOT NULL
    @Basic(optional = true)
    @Size(max = 1024)
    @Column(name = "summary")
    private String summary;


    public String getFullName(){
        return this.getFirstName() + " " + (getMiddleName()!=null?getMiddleName()+" ":"") + getLastName();
    }
}
