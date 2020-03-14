package com.example.mylistviewcrudsqlite;

public class Model {
    private int id;
    private String tarjeta;
    private String vencimiento;
    private String cupo;
    private String deuda;
    private String nombre;
    private String franquicia;
    private byte[] image;

    public Model(int id, String tarjeta, String vencimiento, String cupo, String deuda,String nombre, String franquicia, byte[] image) {
        this.id = id;
        this.tarjeta = tarjeta;
        this.vencimiento = vencimiento;
        this.cupo = cupo;
        this.deuda = deuda;
        this.nombre = nombre;
        this.franquicia = franquicia;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void settarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public String getCupo() {
        return cupo;
    }

    public void setCupo(String cupo) {
        this.cupo = cupo;
    }

    public String getDeuda() {
        return deuda;
    }

    public void setDeuda(String deuda) {
        this.deuda = deuda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFranquicia() {
        return franquicia;
    }

    public void setFranquicia(String franquicia) {
        this.franquicia = franquicia;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
