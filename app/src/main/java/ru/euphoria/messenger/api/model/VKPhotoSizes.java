package ru.euphoria.messenger.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import ru.euphoria.messenger.json.JsonArray;
import ru.euphoria.messenger.json.JsonObject;

/**
 * Model to parse a list of photo with and heigh.
 *
 * See https://vk.com/dev/objects/photo_sizes
 */

public class VKPhotoSizes extends VKModel implements Serializable {
    private ArrayList<PhotoSize> sizes;

    /**
     * Creates a new photo sizes model with fields from json source.
     *
     * @param array the json array to parse
     */
    public VKPhotoSizes(JsonArray array) {
        sizes = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            sizes.add(new PhotoSize(array.optJsonObject(i)));
        }
    }

    public PhotoSize forType(char type) {
        for (PhotoSize size : sizes) {
            if (size.type == type) {
                return size;
            }
        }

        return null;
    }

    public static class PhotoSize extends VKModel implements Serializable{
        public String src;
        public int width;
        public int height;
        public char type;

        public PhotoSize(JsonObject source) {
            this.src = source.optString("src");
            this.width = source.optInt("width");
            this.height = source.optInt("height");
            this.type = source.optString("type").charAt(0);
        }
    }
}
