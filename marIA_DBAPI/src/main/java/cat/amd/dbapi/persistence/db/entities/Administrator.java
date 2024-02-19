package cat.amd.dbapi.persistence.db.entities;

import jakarta.persistence.*;

/*
    TODO create role tables and permission tables and relation tables between them
 */

@Entity
public class Administrator {

    @Id
    @Column(name = "admin_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
