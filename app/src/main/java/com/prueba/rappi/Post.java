package com.prueba.rappi;

public class Post {

    private String titulo;
    private String descripcion;
    private String id;
    private String desc_corta;
    private String imagen;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc_corta() {
        return desc_corta;
    }

    public void setDesc_corta(String desc_corta) {
        this.desc_corta = desc_corta;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
