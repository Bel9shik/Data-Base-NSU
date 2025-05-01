package nsu.kardash.backendsportevents.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AttributesService {
    public static List<String> getAllAttributes(Class<?> clazz) {

        Field[] fields = clazz.getDeclaredFields();

        List<String> attributes = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            attributes.add(field.getName());
        }

        return attributes;
    }

}
