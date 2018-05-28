package com.syhan.javatool.share.args;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OptionParser {
    //
    private static final Logger logger = LoggerFactory.getLogger(OptionParser.class);

    private List<String> optionKeys = new ArrayList<>();
    private List<String> optionDescriptions = new ArrayList<>();

    private HashMap<String, String> options = new HashMap<>();

    public OptionParser accepts(String optionKey, String optionDescription) {
        this.optionKeys.add(optionKey);
        this.optionDescriptions.add(optionDescription);
        return this;
    }

    public void parse(String[] args) {
        //
        // if invalid args then System.exit(0)
        for (String optionKey : optionKeys) {
            String value = findFromArguments(optionKey, args);
            if (value == null) {
                logger.info(optionKey + " is Required.");
                System.exit(0);
            }
            this.options.put(optionKey, value);
        }
    }

    private String findFromArguments(String optionKey, String[] args) {
        //
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-" + optionKey)
                    && i + 1 < args.length
                    && !args[i + 1].startsWith("-")) {
                return args[i + 1];
            }
        }
        return null;
    }

    public String get(String optionKey) {
        //
        return options.get(optionKey);
    }

    @Override
    public String toString() {
        //
        return options.toString();
    }

    public static void main(String[] args) {
        OptionParser optionParser = new OptionParser();
        optionParser.accepts("a", "aaa")
                .accepts("b", "bbb")
                .accepts("c", "ccc")
                .parse(new String[]{"-c", "chare", "-a", "alpha", "-b", "beta"});

        logger.info(optionParser.toString());
    }
}
