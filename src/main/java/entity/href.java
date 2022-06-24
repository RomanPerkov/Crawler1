package entity;

import jakarta.persistence.*;


@Entity
@Table(name = "href")
public class href {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;
    @Column(name = "href")
    private String href;

    public href() {
    }

    public href(String href) {

        this.href = href;
    }

    @Override
    public String toString() {
        return "href{" +
                "id='" + id + '\'' +
                ", href='" + href + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
