

package edu.vt.resumemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Education.
@Entity
@Data
// Name of the database table represented
@Table(name = "Education")

@NamedQueries({
        @NamedQuery(name = "Education.findAll", query = "SELECT e FROM Education e")
        , @NamedQuery(name = "Education.findById", query = "SELECT e FROM Education e WHERE e.id = :id")
        , @NamedQuery(name = "Education.findByUserId", query = "SELECT e FROM Education e WHERE e.user = :id")})

public class Education implements Serializable {
    /*
   ========================================================
   Instance variables representing the attributes (columns)
   of the database table Education.

   CREATE TABLE Education
   (
        id int unsigned NOT NULL AUTO_INCREMENT,
        user int unsigned NOT NULL,
        instituteName varchar(255) DEFAULT NULL,
        degree varchar(255) NOT NULL,
        startDate date NOT NULL,
        graduationDate date NOT NULL,
        currentlyAttending BOOLEAN NOT NULL DEFAULT 0,
        description TEXT DEFAULT NULL,
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

    @Basic(optional = false)
    @Column(name = "user")
    private Integer user;

    // instituteName VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "instituteName")
    private String instituteName;

    // degree VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "degree")
    private String degree;

    // startDate Date NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "startDate")
    private Date startDate;

    // graduationDate Date NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "graduationDate")
    private Date graduationDate;

    @Basic(optional = true)
    @Column(name = "description")
    private String description;


}
