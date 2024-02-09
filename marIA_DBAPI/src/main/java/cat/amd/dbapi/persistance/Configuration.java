package cat.amd.dbapi.persistance;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// TODO fix imports

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "configuration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Property> properties;

    /**
     * Empty constructor
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
        this.properties = new ArrayList<>();
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * Adds a 'Property' object to 'properties' list
     *
     * @param property object to add
     */
    public void setProperty(Property property) {
        this.properties.add(property);
    }

    /**
     * Custom toString() method
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration [id = ")
                .append(", name = ")
                .append(name).append(", properties = [ ");
        for (Property property : properties) {
            sb.append(property.toString()).append(", ");
        }

        if (!properties.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]]");

        return sb.toString();
    }


}
