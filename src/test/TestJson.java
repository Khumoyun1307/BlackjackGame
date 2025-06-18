package test;

import com.google.gson.Gson;

public class TestJson {
    public static void main(String[] args) {
        Gson gson = new Gson();
        String json = gson.toJson(new int[]{1, 2, 3});
        System.out.println(json); // should print: [1,2,3]
    }
}