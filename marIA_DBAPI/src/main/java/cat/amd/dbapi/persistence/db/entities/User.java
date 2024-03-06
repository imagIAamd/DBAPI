package cat.amd.dbapi.persistence.db.entities;


import jakarta.persistence.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cat.amd.dbapi.Constants.*;

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
            name = "User_x_Group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    @Column(name = "password")
    private String password;

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

    public User(String nickname, String telephone, String email, String password) {
        this.nickname = nickname;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructor from JSON
     *
     * @param data JSON
     */
    public User(JSONObject data) {
        if (data.has(NICKNAME)) {
            this.nickname = data.getString(NICKNAME);
        }
        if (data.has(PHONE_NUMBER)) {
            this.telephone = data.getString(PHONE_NUMBER);
        }
        if (data.has(EMAIL)) {
            this.email = data.getString(EMAIL);
        }
        if (data.has(VALIDATION_CODE)) {
            this.validationCode = data.getInt(VALIDATION_CODE);
        }
        if (data.has(PASSWORD)) {
            this.password = data.getString(PASSWORD);
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

    public Set<Group> getRoles() {
        return groups;
    }

    public void setRoles(Set<Group> groups) {
        this.groups = groups;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public JSONObject toJSON(List<Group> groups) {
        JSONObject json = new JSONObject();
        JSONArray groupArray = new JSONArray();

        json.put("nickname", Objects.requireNonNullElse(nickname, "null"));
        json.put("phone_number", Objects.requireNonNullElse(telephone, "null"));
        json.put("email", Objects.requireNonNullElse(email, "null"));

        if (groups == null || groups.isEmpty()) {
            return json;
        }

        for (Group group : groups) {
            if (Objects.equals(group.getName(), ROLE_PREMIUM_NAME) || Objects.equals(group.getName(), ROLE_ADMINISTRATOR_NAME)) {
                json.put("plan", "premium");
            }
            groupArray.put(group.getName());
        }

        if (!json.has("plan")) {
            json.put("plan", "free");
        }

        json.put("groups", groupArray);

        return json;
    }
}
