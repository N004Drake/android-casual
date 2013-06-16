/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author adam
 */
class LinkedProperties extends Properties {

    private final LinkedHashSet<Object> keys = new LinkedHashSet<>();

    @Override
    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    @Override
    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
    @Override
    public Set<String> stringPropertyNames() {
    Set<String> set = new LinkedHashSet<>();

    for (Object key : this.keys) {
        set.add((String)key);
    }

    return set;
}
    
}
