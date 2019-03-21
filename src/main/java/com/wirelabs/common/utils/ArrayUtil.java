package com.wirelabs.common.utils;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jaison Pereira - 28 de dez de 2016 Classe com metodos uteis para
 *         tratamento de listas
 */
@Component
public class ArrayUtil {

    public <T> Object[] getArrayObject(List<T> list) {
        final Object[] array = new Object[list.size()];
        int i = 0;
        for (final T obj : list) {
            array[i] = obj;
            i++;
        }
        return array;
    }

}
