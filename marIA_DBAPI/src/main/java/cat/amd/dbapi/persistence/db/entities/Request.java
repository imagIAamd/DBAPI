package cat.amd.dbapi.persistence.db.entities;

import cat.amd.dbapi.persistence.db.managers.ModelManager;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id")
    private long id;

    @Column(nullable = false)
    private String prompt;

    @Column(name = "image", length = 2083)
    private String imagePath;

    @Column(name = "request_date")
    private String requestDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    public Request() {

    }

    public Request(JSONObject data) {
        this.user = UserManager.findUser("admin");
        this.prompt = data.getString("prompt");
        this.model = ModelManager.findModelByName("llava");
        this.imagePath = "resources/test";

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.requestDate = currentDate.format(sqlDateFormat);
    }

    public Request(String prompt) {
        this.prompt = prompt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", prompt='" + prompt + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", user=" + user +
                ", model=" + model +
                '}';
    }
}
