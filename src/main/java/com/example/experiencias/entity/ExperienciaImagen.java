package com.example.experiencias.entity;

public class ExperienciaImagen {

    private Integer id;
    private Integer experienciaId;
    private String url;

    public ExperienciaImagen(Integer id, Integer experienciaId, String url) {
        this.id = id;
        this.experienciaId = experienciaId;
        this.url = url;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getExperienciaId() { return experienciaId; }
    public void setExperienciaId(Integer experienciaId) { this.experienciaId = experienciaId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return "ExperienciaImagen [id=" + id + ", experienciaId=" + experienciaId + ", url=" + url + "]";
    }
}
