package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "car")
public class car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;
    @Column(name = "href")
    private String href;
    @Column(name = "productionEar")
    private String productionEar;
    @Column(name = "price")
    private String price;
    @Column(name = "brand")
    private String brand;
    @Column(name = "model")
    private String model;
    @Column(name = "engineCapacity")
    private String engineCapacity;
    @Column(name = "mileage")
    private String mileage;
    @Column(name = "tradeAccount")
    private String tradeAccount;



    public car() {
    }

    public car(String href, String productionEar, String price, String brand, String model, String engineCapacity, String mileage, String tradeAccount) {
        this.href = href;
        this.productionEar = productionEar;
        this.price = price;
        this.brand = brand;
        this.model = model;
        this.engineCapacity = engineCapacity;
        this.mileage = mileage;
        this.tradeAccount=tradeAccount;
    }

    public String getTradeAccount() {
        return tradeAccount;
    }

    public void setTradeAccount(String tradeAccount) {
        this.tradeAccount = tradeAccount;
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

    public String getProductionEar() {
        return productionEar;
    }

    public void setProductionEar(String productionEar) {
        this.productionEar = productionEar;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", href='" + href + '\'' +
                ", productionEar=" + productionEar +
                ", price=" + price +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", engineCapacity=" + engineCapacity +
                ", mileage=" + mileage +
                '}';
    }
}
