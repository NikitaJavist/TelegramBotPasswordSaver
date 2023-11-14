package com.example.telegrambotpasswordsaver.Components;

import lombok.Getter;

@Getter
public enum LIST_OF_COMMANDS {


    START("/start","START BOT"),
    STORE ("/store","STORE LOGIN AND PASSWORD"),
    HELP("/help","HOW DOES BOT WORK?"),
    SHOW_ALL("/showall","SHOW ALL DATA"),
    WIPE_ALL("/wipeall", "DELETE ALL DATA");


    final private String command, description;


    LIST_OF_COMMANDS(final String command, final String description) {
        this.command = command;
        this.description = description;
    }


}



