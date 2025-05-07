package Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Model implements Parcelable {

    private String id;
    private String by;
    private String name;
    private String price;
    private String img;
    private String type;

    public Model(String id, String by, String name, String price, String img, String type) {
        this.id = id;
        this.by = by;
        this.name = name;
        this.price = price;
        this.img = img;
        this.type = type;
    }

    public Model(String id, String name, String price, String type, String img) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.img = img;
    }

    public Model(String name, String price, String type, String img) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.img = img;
    }

    public Model(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public Model() {}

    protected Model(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
        img = in.readString();
        type = in.readString();
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(img);
        dest.writeString(type);
    }

    // Getters and setters for the fields


    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}