package cn.edu.scu.jiangpeyton;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    public List<API> profiles = new ArrayList<>();

    class API {
        public String name;
        public String Provider;
        public String Class;
        public String method;
        public List<String> hash = new ArrayList<>();
        public Key key;

        class Key {
            public boolean diff;

            class KeyStruct{
                
            }
        }

    }
}
