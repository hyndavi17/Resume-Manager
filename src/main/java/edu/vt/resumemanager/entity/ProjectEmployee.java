

package edu.vt.resumemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Project.
@Entity
@Data
// Name of the database table represented
@Table(name = "ProjectEmployee")

@NamedQueries({
        @NamedQuery(name = "ProjectEmployee.findAll", query = "SELECT e FROM ProjectEmployee e")
        , @NamedQuery(name = "ProjectEmployee.findById", query = "SELECT e FROM Project e WHERE e.id = :id")
        , @NamedQuery(name = "ProjectEmployee.findByEmployeeId", query = "SELECT e FROM ProjectEmployee e WHERE e.employee = :id")
        , @NamedQuery(name = "ProjectEmployee.findByProjectId", query = "SELECT e FROM ProjectEmployee e WHERE e.project = :id")})

public class ProjectEmployee implements Serializable {
    /*
   ========================================================
   Instance variables representing the attributes (columns)
   of the database table ProjectEmployee.

   CREATE TABLE ProjectEmployee
   (
        employee int unsigned NOT NULL,
        project int unsigned NOT NULL,
        position varchar(255) DEFAULT NULL,
        dateAdded timestamp,
   );
   ========================================================
    */
    private static final long serialVersionUID = 1L;
    // employee INT UNSIGNED NOT NULL
    @Id
    @Basic(optional = false)
    @Column(name = "employee")
    private Integer employee;

    // project INT UNSIGNED NOT NULL
    @Basic(optional = false)
    @Column(name = "project")
    private Integer project;

    // position VARCHAR(255) NOT NULL
    @Basic(optional = true)
    @Size( max = 255)
    @Column(name = "position")
    private String position;

    // dateAdded timestamp NOT NULL
    @Basic(optional = true)
    @Column(name = "dateAdded")
    private Timestamp dateAdded;

}
