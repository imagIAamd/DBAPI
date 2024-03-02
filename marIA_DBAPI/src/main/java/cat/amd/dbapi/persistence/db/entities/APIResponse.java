package cat.amd.dbapi.persistence.db.entities;

import cat.amd.dbapi.persistence.db.managers.RequestManager;
import jakarta.persistence.*;
import org.json.JSONObject;

@Entity(name = "Response")
public class APIResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "response_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Column(columnDefinition = "TEXT")
    private String text;

    public APIResponse() {

    }

    public APIResponse(JSONObject data) {
        Long requestId = data.getLong("request_id");
        this.text = data.getString("text");
        this.request = RequestManager.findRequest(requestId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", request=" + request +
                ", text='" + text + '\'' +
                '}';
    }
}
