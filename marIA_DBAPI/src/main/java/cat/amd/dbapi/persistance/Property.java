package cat.amd.dbapi.persistance;

import jakarta.persistence.*;

@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private String value;

    @ManyToOne
    private Configuration configuration;

    /**
     * Empty constructor
     *
     */
    public Property() {
        super();
    }

    /**
     * Constructor with 'key' String parameter &
     * 'value' String parameter
     *
     * @param key property key
     * @param value property value
     */
    public Property(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        // Maybe can return a JSON formatted String?

        return "Property [key = " + key + ", value = " + value + " ]";
    }
}
