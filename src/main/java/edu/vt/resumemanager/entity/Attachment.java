
package edu.vt.resumemanager.entity;

import edu.vt.resumemanager.utils.Constants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Attachment.
@Entity
@Data
// Name of the database table represented
@Table(name = "Attachment")

@NamedQueries({
        @NamedQuery(name = "Attachment.findAll", query = "SELECT e FROM Attachment e")
        , @NamedQuery(name = "Attachment.findById", query = "SELECT e FROM Attachment e WHERE e.id = :id")
        , @NamedQuery(name = "Attachment.findByUserId", query = "SELECT e FROM Attachment e WHERE e.user = :id")
        , @NamedQuery(name = "Attachment.findByFilename", query = "SELECT u FROM Attachment u WHERE u.name = :filename")})

public class Attachment implements Serializable {
    /*
   ========================================================
   Instance variables representing the attributes (columns)
   of the database table Attachment.

   CREATE TABLE Attachment
   (
        id int unsigned NOT NULL AUTO_INCREMENT,
        user int unsigned NOT NULL,
        name varchar(255) NOT NULL,
        size DECIMAL NOT NULL,
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

    // name VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;

    public Attachment(){

    }
    public Attachment(String filename, Account id) {
        this.name = filename;
        user = id.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
   ===============
   Instance Method
   ===============
    */
    public String getFilePath() {
        return Constants.FILES_ABSOLUTE_PATH + getName();
    }

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
     Checks if the UserFile object identified by 'object' is the same as the UserFile object identified by 'id'
     Parameter object = UserFile object identified by 'object'
     Returns True if the UserFile 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }
}
