/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.saar.basic



import org.junit.*;
import com.google.common.collect.SetMultimap


/**
 *
 * @author koller
 */
class AkSetMultimapTest {
    @Test
    public void testEmpty() {
        SetMultimap<String,String> map = new AkSetMultimap<String,String>();
        assert map.isEmpty();
        assert 0 == map.size();
    }
    
    @Test
    public void testPut() {
        SetMultimap<String,String> map = new AkSetMultimap<String,String>();
        map.put("a", "b");
        map.put("a", "c");
        map.put("d", "e");
        
        assert 3 ==  map.size();
        assert new HashSet(["a", "d"]).equals(map.keySet());
        assert new HashSet(["b", "c"]).equals(map.get("a"));
        assert new HashSet(["e"]).equals(map.get("d"));
    }
}

