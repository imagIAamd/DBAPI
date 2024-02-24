package cat.amd.dbapi.persistence.db.entities;


import jakarta.persistence.*;
import org.json.JSONObject;

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

    @Column(name = "access_key")
    private String accessKey;

    /**
     * Empty constructor
     *
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
     * @param nickname nickname
     * @param telephone telephone
     * @param email email
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
        data = data.getJSONObject("data");
        this.nickname = data.getString("nickname");
        this.telephone = data.getString("phone_number");
        this.email = data.getString("email");
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

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", accessKey=" + accessKey +
                '}';
    }
}
