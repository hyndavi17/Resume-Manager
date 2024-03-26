package edu.vt.resumemanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
@Entity
@Data
// Name of the database table represented
@Table(name = "Configuration")

public class Configuration implements Serializable {
    /*
   ========================================================
   Instance variables representing the attributes (columns)
   of the database table Certificate.

   CREATE TABLE Configuration
   (
        id int unsigned NOT NULL AUTO_INCREMENT,
        config_key varchar(255) NOT NULL,
        name varchar(255) NOT NULL,
        value varchar(255) NOT NULL,
   );
   */
    private static final long serialVersionUID = 1L;

    // id INT UNSIGNED NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "config_key")
    private String configKey;

    @Basic(optional = false)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @Column(name = "value")
    private String value;


}
