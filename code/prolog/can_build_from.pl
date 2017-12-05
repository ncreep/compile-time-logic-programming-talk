can_build_from(list(A), B, list(B)).

can_build_from(set(A), B, set(B)).

can_build_from(bit_set, int, bit_set).

can_build_from(bit_set, B, set(B)).

can_build_from(sorted_set(A), B, sorted_set(B)) :-
  ordering(B).
  
ordering(int).
ordering(string).
