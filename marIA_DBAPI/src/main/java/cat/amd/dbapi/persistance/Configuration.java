package cat.amd.dbapi.persistance;

import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;


    /* TODO uncomment when 'Property' class exists

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, fetch =  FetchType.EAGER)
    private List<Property> properties;

    */


    /**
     * Empty constructor
     *
     */
    public Configuration() {
        super();
    }

    /**
     * Constructor with 'name' String parameter
     *
     * @param name configuration name
     */
    public Configuration(String name) {
        this.name = name;


        // TODO uncomment when 'Property' class exists
        //this.properties = new ArrayList<>();
    }

    // TODO add getters and setters


    // TODO override toString method


}
