

package edu.vt.resumemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;


@Entity(name = "Skill")
// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Skill.
@Data
// Name of the database table represented
@Table(name = "Skill")
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Skill.findAll", query = "SELECT s FROM Skill s")
        , @NamedQuery(name = "Skill.findById", query = "SELECT s FROM Skill s WHERE s.user = :user")
        , @NamedQuery(name = "Skill.findBySkillName", query = "SELECT s FROM Skill s WHERE s.name = :name")})

public class Skill implements Serializable {
    /*
   ========================================================
   Instance variables representing the attributes (columns)
   of the database table Skill.

   CREATE TABLE Skill
   (
       user int unsigned NOT NULL,
	   name varchar(128) NOT NULL,
   );
   ========================================================
    */
    // 	user int unsigned NOT NULL
    @Id
    @Basic(optional = false)
    @Column(name = "user")
    private Integer user;

    // name VARCHAR(255) NOT NULL
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;


}
