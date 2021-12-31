package com.example.betaversion;

import java.util.ArrayList;

public class User {
    ArrayList<Section> sections;
    String nickname;

    public User(ArrayList<Section> sections,String nickname)
    {
        this.sections = sections;
        this.nickname = nickname;
    }
}
