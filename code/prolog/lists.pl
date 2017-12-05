member_(H, [H|T]).

member_(Elem, [H|T]) :-
  member_(Elem, T).

append_([], L, L).

append_([H|T], L1, [H|L2]) :- 
  append_(T, L1, L2).
