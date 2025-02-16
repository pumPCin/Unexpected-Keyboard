package juloo.keyboard2;

import juloo.keyboard2.KeyValue;
import org.junit.Test;
import static org.junit.Assert.*;

public class KeyValueTest
{
  public KeyValueTest() {}

  @Test
  public void equals()
  {
    assertEquals(KeyValue.makeStringKeyWithSymbol("Foo", "Symbol", 0), KeyValue.makeStringKeyWithSymbol("Foo", "Symbol", 0));
  }

  @Test
  public void numpad_script() {}
  String apply_numpad_script(String script)
  {
    StringBuilder b = new StringBuilder();
    int map = KeyModifier.modify_numpad_script(script);
    for (char c : "0123456789".toCharArray())
      b.append(ComposeKey.apply(map, c).getChar());
    return b.toString();
  }
}
