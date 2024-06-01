package juloo.keyboard2;

import java.util.Arrays;

public final class ComposeKey
{
  /** Apply the pending compose sequence to [kv]. */
  public static KeyValue apply(int state, KeyValue kv)
  {
    switch (kv.getKind())
    {
      case Char:
        KeyValue res = apply(state, kv.getChar());
        // Grey-out characters not part of any sequence.
        if (res == null)
          return kv.withFlags(kv.getFlags() | KeyValue.FLAG_GREYED);
        return res;
      /* These keys are not greyed. */
      case Event:
      case Modifier:
      case Compose_pending:
        return kv;
      /* Other keys cannot be part of sequences. */
      default:
        return kv.withFlags(kv.getFlags() | KeyValue.FLAG_GREYED);
    }
  }

  /** Apply the pending compose sequence to char [c]. */
  static KeyValue apply(int prev, char c)
  {
    char[] states = ComposeKeyData.states;
    char[] edges = ComposeKeyData.edges;
    int prev_length = edges[prev];
    int next = Arrays.binarySearch(states, prev + 1, prev + prev_length, c);
    if (next < 0)
      return null;
    next = edges[next];
    char next_header = states[next];
    if (next_header == 0) // Enter a new intermediate state.
      return KeyValue.makeComposePending(String.valueOf(c), next, 0);
    else if (next_header > 0) // Character final state.
      return KeyValue.makeCharKey(next_header);
    else // next_header is < 0, string final state.
    {
      int next_length = edges[next];
      return KeyValue.makeStringKey(
          new String(states, next + 1, next + next_length));
    }
  }

  /** The state machine is comprised of two arrays.

      The [states] array represents the different states and the associated
      transitions:
      - The first cell is the header cell, [states[s]].
      - If the header is equal to [0],
        The remaining cells are the transitions characters, sorted
        alphabetically.
      - If the header is positive,
        This is a final state, [states[s]] is the result of the sequence.
        In this case, [edges[s]] must be equal to [1].
      - If the header is equal to [-1],
        This is a final state, the remaining cells represent the result string
        which starts at index [s + 1] and has a length of [edges[s] - 1].

      The [edges] array represents the transition state corresponding to each
      accepted inputs.
      - If [states[s]] is a header cell, [edges[s]] is the number of cells
        occupied by the state [s], including the header cell.
      - If [states[s]] is a transition, [edges[s]] is the index of the state to
        jump into.
      - If [states[s]] is a part of a final state, [edges[s]] is not used. */
}
