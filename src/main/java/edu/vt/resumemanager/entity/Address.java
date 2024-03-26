
package edu.vt.resumemanager.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// The @Entity annotation designates this class as a JPA Entity POJO class representing the database table Address.
@Entity
@Data
// Name of the database table represented
@Table(name = "Address")

@NamedQueries({
        @NamedQuery(name = "Address.findAll", query = "SELECT a FROM Address a")
        , @NamedQuery(name = "Address.findById", query = "SELECT a FROM Address a WHERE a.id = :id")
        , @NamedQuery(name = "Address.findByEmail", query = "SELECT a FROM Address a WHERE a.email = :email")})

public class Address implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the database table Address.

    CREATE TABLE Address
    (
        id int unsigned NOT NULL,
        address1 varchar(128) NOT NULL,
        address2 varchar(128) DEFAULT NULL,
        city varchar(64) NOT NULL,
        state_name varchar(32) NOT NULL,
        zipcode varchar(10) NOT NULL,
        country varchar(255) NOT NULL,
        email varchar(128) NOT NULL,
        phone_number varchar(24) DEFAULT NULL,
    );
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    // id INT UNSIGNED NOT NULL
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // address1 VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "address1")
    private String address1;

    // address2 VARCHAR(128)
    @Size(max = 128)
    @Column(name = "address2")
    private String address2;

    // city VARCHAR(64) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "city")
    private String city;

    // state_name VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "state_name")
    private String stateName;

    // zipcode VARCHAR(10) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "zipcode")
    private String zipcode;

    // country VARCHAR(255) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "country")
    private String country;

    // email VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "email")
    private String email;

    // phone_number VARCHAR(24)
    @Size(max = 24)
    @Column(name = "phone_number")
    private String phoneNumber;

}
