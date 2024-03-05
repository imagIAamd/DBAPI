package cat.amd.dbapi.persistence.db.entities;


import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(name = "validation_code")
    private Integer validationCode;

    @ManyToMany
    @JoinTable(
            name = "User_Role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    /**
     * Empty constructor
     */
    public User() {

    }

    /**
     * Search by nickname constructor
     *
     * @param nickname nickname
     */
    public User(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Default constructor
     *
     * @param nickname  nickname
     * @param telephone telephone
     * @param email     email
     */
    public User(String nickname, String telephone, String email) {
        this.nickname = nickname;
        this.telephone = telephone;
        this.email = email;
    }

    /**
     * Constructor from JSON
     *
     * @param data JSON
     */
    public User(JSONObject data) {
        if (data.has("nickname")) {
            this.nickname = data.getString("nickname");
        }
        if (data.has("phone_number")) {
            this.telephone = data.getString("phone_number");
        }
        if (data.has("email")) {
            this.email = data.getString("email");
        }
        if (data.has("validation_code")) {
            this.validationCode = data.getInt("validation_code");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(Integer accessKey) {
        this.validationCode = accessKey;
    }

    public static boolean isValid(User user) {
        return user.nickname != null && user.validationCode != null && user.telephone != null && user.email != null;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", validationCode=" + validationCode +
                '}';
    }
}
