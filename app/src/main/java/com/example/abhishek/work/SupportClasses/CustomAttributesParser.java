package com.example.abhishek.work.SupportClasses;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomAttributesParser {

    public String getImageUrl(JSONArray customAttr) {
        String url = "";
        try {
            for (int i = 0; i < customAttr.length(); i++) {
                JSONObject tmpAttr = (JSONObject) customAttr.getJSONObject(i);

                String key = tmpAttr.getString("attribute_code");
                if (key.equals("image")){
                    url = tmpAttr.getString("value");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
