package cat.amd.dbapi.persistence.db.entities;

import jakarta.persistence.*;

@Entity
public class Administrator {

    @Id
    @Column(name = "admin_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "password", nullable = false)
    private String password;

    public Administrator() {

    }

    public Administrator(User user) {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
