package com.example.moveair5.relatedmusic;

public class Sleep extends Music {
    String name;
    int genre;
    int route;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getGenre() {
        return genre;
    }

    @Override
    public void setGenre(int genre) {
        this.genre = genre;
    }

    @Override
    public int getRoute() {
        return route;
    }

    @Override
    public void setRoute(int route) {
        this.route = route;
    }

    public Sleep() {
    }
}
