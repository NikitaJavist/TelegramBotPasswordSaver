package com.example.telegrambotpasswordsaver.Components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Buttons {



        private static final InlineKeyboardButton STORE_BUTTON = new InlineKeyboardButton("Store");
        private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Help");

        private static final  InlineKeyboardButton WIPE_BUTTON = new InlineKeyboardButton("Wipe");

        private static  final InlineKeyboardButton SHOWALL_BUTTON = new InlineKeyboardButton("Show all");

        public static InlineKeyboardMarkup inlineMarkup() {
            STORE_BUTTON.setCallbackData("/store");
            HELP_BUTTON.setCallbackData("/help");
            WIPE_BUTTON.setCallbackData("/wipeall");
            SHOWALL_BUTTON.setCallbackData("/showall");

            List <InlineKeyboardButton> rowInline = List.of(STORE_BUTTON, HELP_BUTTON);
            List<InlineKeyboardButton> rowInline2 = List.of(SHOWALL_BUTTON, WIPE_BUTTON);


            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> mainList = new ArrayList<>();
            mainList.add(rowInline);
            mainList.add(rowInline2);
            markupInline.setKeyboard(mainList);

            return markupInline;
        }

}
